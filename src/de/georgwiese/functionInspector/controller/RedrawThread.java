package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;
import android.graphics.Path;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.calculationFunktions.PointMaker;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.Helper;
import de.georgwiese.functionInspector.uiClasses.Point2D;


public class RedrawThread extends Thread{
	
	Handler handler;
	StateHolder sh;
	FktCanvas canvas;
	PathCollector pathCollector;
	long startTime;
	
	public RedrawThread(Handler handler, StateHolder stateHolder, FktCanvas canvas, PathCollector pathCollector){
		super();
		this.handler = handler;
		this.sh = stateHolder;
		this.canvas = canvas;
		this.pathCollector = pathCollector;
		
		//set this priority to be minimal
		setPriority(MIN_PRIORITY);
	}
	/*
	public void setHandler(Handler h){
		handler=h;
	}
	*/
	@Override
	public void run() {
		while (true){
			startTime = AnimationUtils.currentAnimationTimeMillis();
			if(sh.redraw){ // && !(bZoom | bZoomDyn)){//|bZoom|bZoomDyn){
				sh.redraw = false;
				Point2D _lastOrigin = Helper.unitToPx(0, 0, sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight());
				double _totalZoomX = sh.getZoom(0);
				double _totalZoomY = sh.getZoom(0);
				
				double[] _zoomFactor = sh.getZoom().clone();
				double[] _middle = sh.getMiddle().clone();
				int _width = canvas.getWidth();
				int _height = canvas.getHeight();
				// Clone fkts
				ArrayList<Function> _fkts = new ArrayList<Function>();
				for (Function f:sh.getFkts()){
					if (f == null)
						_fkts.add(null);
					else
						_fkts.add(f.clone());
				}
				//minX = pxToUnitX(-50,_totalZoomX,_middleX);
				//maxX = pxToUnitX(getWidth()+50,_zoomFactorX,_middleX);
				
				ArrayList<Path> helperPaths = new ArrayList<Path>();
				
				ArrayList<ArrayList<Double>> hDiscon     = new ArrayList<ArrayList<Double>>();
				ArrayList<ArrayList<Point>> hRoots       = new ArrayList<ArrayList<Point>>();
				ArrayList<ArrayList<Point>> hExtrema     = new ArrayList<ArrayList<Point>>();
				ArrayList<ArrayList<Point>> hInflections = new ArrayList<ArrayList<Point>>();
				
				for (int j=0; j<_fkts.size(); j++){
					Function f = _fkts.get(j);
					Path p = new Path();
					boolean first = true;
					
					if (f==null){
						hRoots.add(new ArrayList<Point>());
						hExtrema.add(new ArrayList<Point>());
						hInflections.add(new ArrayList<Point>());
						hDiscon.add(new ArrayList<Double>());
						helperPaths.add(null);
					}
					else{
						f.setParams(sh.getParams());
						
						hDiscon.add(PointMaker.getDiscontinuities(f, Helper.pxToUnit(-_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.pxToUnit(2*_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.getDeltaUnit(15,_zoomFactor[0])));
						hExtrema.add(PointMaker.getExtrema(f, hDiscon.get(j), Helper.pxToUnit(-_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.pxToUnit(2*_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.getDeltaUnit(15,_zoomFactor[0])));
						hRoots.add(PointMaker.getRoots(f, hExtrema.get(j), hDiscon.get(j), Helper.pxToUnit(-_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.pxToUnit(2*_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.getDeltaUnit(15,_zoomFactor[0])));
						hInflections.add(PointMaker.getInflectionPoints(f, hDiscon.get(j), Helper.pxToUnit(-_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.pxToUnit(2*_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.getDeltaUnit(15,_zoomFactor[0])));
						
						int inIndex=0;
						ArrayList<Double> discons = hDiscon.get(_fkts.indexOf(f));
						float quality = sh.preview? 5 : 1;
						float extra   = sh.preview? 10 : _width;
						for (float x=-extra; x<_width + extra; x+=quality){
							double xU = Helper.pxToUnit(x, 0, _zoomFactor, _middle, _width, _height).x;
							double y = f.calculate(xU);
							if (inIndex<discons.size() && xU >= discons.get(inIndex)){
								first=true;
								inIndex++;
							}
							if (!Double.isNaN(y)){
								if ((x>=-PathCollector.TOLERANCE_SIDE && x <= _width+PathCollector.TOLERANCE_SIDE) | x % 5==0){
									float yPx = (float) Helper.unitToPx(0, y, _zoomFactor, _middle, _width, _height).y;
									
									if (first)
										p.moveTo(x, yPx);
									first=false;
									p.lineTo(x, yPx);
								}
							}
							else
								first=true;
							//}
						}
						helperPaths.add(p);
					}
				}
				
				ArrayList<Point> hIntersections = new ArrayList<Point>();
				for (int i = 0; i<_fkts.size()-1; i++)
					for (int j=i+1; j<_fkts.size(); j++)
						if (_fkts.get(i)!=null && _fkts.get(j)!=null && !_fkts.get(i).equals(_fkts.get(j)))
							hIntersections.addAll(PointMaker.getIntersections(_fkts.get(i), _fkts.get(j),  Helper.pxToUnit(-_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.pxToUnit(2*_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.getDeltaUnit(15,_zoomFactor[0])));

				synchronized (pathCollector){
					pathCollector.setPathsAndPoints(helperPaths, hRoots, hExtrema, hInflections,
							hIntersections, hDiscon, _middle, _zoomFactor);
				}
				canvas.postInvalidate();
				//Looper.prepare();
				if (handler!=null)
					handler.sendEmptyMessage(0);
				//Looper.loop();
			}
			//Log.d("Developer", "Redraw time: " + (AnimationUtils.currentAnimationTimeMillis() - startTime));
			if (!sh.preview)
				try{sleep(200);}catch(Exception e){}
		}
	}
}
