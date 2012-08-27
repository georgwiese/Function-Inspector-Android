package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;
import android.graphics.Path;
import android.os.Handler;
import android.util.Log;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.calculationFunktions.PointMaker;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.Helper;
import de.georgwiese.functionInspector.uiClasses.Point2D;


public class RedrawThread extends Thread{
	
	Handler handler;
	StateHolder sh;
	FktCanvas canvas;
	PathCollector pathCollector;
	
	public RedrawThread(Handler handler, StateHolder stateHolder, FktCanvas canvas, PathCollector pathCollector){
		super();
		this.handler = handler;
		this.sh = stateHolder;
		this.canvas = canvas;
		this.pathCollector = pathCollector;
	}
	/*
	public void setHandler(Handler h){
		handler=h;
	}
	*/
	@Override
	public void run() {
		while (true){
			if(sh.redraw){ // && !(bZoom | bZoomDyn)){//|bZoom|bZoomDyn){
				
				//synchronized (lockDrawing) {
				sh.redraw = false;
				Point2D _lastOrigin = Helper.unitToPx(0, 0, sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight());
				double _totalZoomX = sh.getZoom(0);
				double _totalZoomY = sh.getZoom(0);
				
				double[] _zoomFactor = sh.getZoom().clone();
				double[] _middle = sh.getMiddle().clone();
				int _width = canvas.getWidth();
				int _height = canvas.getHeight();
				ArrayList<Function> _fkts = new ArrayList<Function>(sh.getFkts());
				//minX = pxToUnitX(-50,_totalZoomX,_middleX);
				//maxX = pxToUnitX(getWidth()+50,_zoomFactorX,_middleX);
				
				ArrayList<Path> helperPaths = new ArrayList<Path>();
				
				ArrayList<ArrayList<Double>> hDiscon = new ArrayList<ArrayList<Double>>();
				/*
				ArrayList<ArrayList<Point>> hRoots=new ArrayList<ArrayList<Point>>();
				ArrayList<ArrayList<Point>> hExtrema=new ArrayList<ArrayList<Point>>();
				ArrayList<ArrayList<Point>> hInflections=new ArrayList<ArrayList<Point>>();
				boolean disRoots2=disRoots;
				boolean disInflections2=disInflections;
				boolean disExtrema2=disExtrema;
				*/
				
				for (int j=0; j<_fkts.size(); j++){
					Function f = _fkts.get(j);
					Path p = new Path();
					boolean first = true;
					/*
					if (f==null){
						hRoots.add(new ArrayList<Point>());
						hExtrema.add(new ArrayList<Point>());
						hInflections.add(new ArrayList<Point>());
						hDiscon.add(new ArrayList<Double>());
					}
					else{*/
					double [] params = sh.getParams();
					//TODO: implement Function.setParams(double[] params)
					f.setA(params[0]);
					f.setB(params[1]);
					f.setC(params[2]);
					
					hDiscon.add(PointMaker.getDiscontinuities(f, Helper.pxToUnit(-_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.pxToUnit(_width, 0, _zoomFactor, _middle, _width, _height).x, Helper.getDeltaUnit(15,_zoomFactor[0])));
					/*
					if (disExtrema2 | disRoots2)
						hExtrema.add(PointMaker.getExtrema(f, hDiscon.get(j), quality==QUALITY_PREVIEW?pxToUnitX(0,_zoomFactorX,_middleX):pxToUnitX(-getWidth(),_zoomFactorX,_middleX), quality==QUALITY_PREVIEW?pxToUnitX(getWidth(),_zoomFactorX,_middleX):pxToUnitX(2*getWidth(),_zoomFactorX,_middleX), getDeltaUnit(15,_zoomFactorX)));
					if (disRoots2)
						hRoots.add(PointMaker.getRoots(f, hExtrema.get(j), hDiscon.get(j), quality==QUALITY_PREVIEW?pxToUnitX(0,_zoomFactorX,_middleX):pxToUnitX(-getWidth(),_zoomFactorX,_middleX), quality==QUALITY_PREVIEW?pxToUnitX(getWidth(),_zoomFactorX,_middleX):pxToUnitX(2*getWidth(),_zoomFactorX,_middleX), getDeltaUnit(15,_zoomFactorX)));
					if (disInflections2)
						hInflections.add(PointMaker.getInflectionPoints(f, hDiscon.get(j), quality==QUALITY_PREVIEW?pxToUnitX(0,_zoomFactorX,_middleX):pxToUnitX(-getWidth(),_zoomFactorX,_middleX), quality==QUALITY_PREVIEW?pxToUnitX(getWidth(),_zoomFactorX,_middleX):pxToUnitX(2*getWidth(),_zoomFactorX,_middleX), getDeltaUnit(15,_zoomFactorX)));
					*/
					
					int inIndex=0;
					ArrayList<Double> discons = hDiscon.get(_fkts.indexOf(f));
					//TODO: decide whether or not to use different qualities
					for (float x=-_width; x<2*_width; x++){ //x+=quality){
						//if (quality==QUALITY_PREVIEW && x<-2*QUALITY_PREVIEW)
						//	x=-2*QUALITY_PREVIEW;
						double xU = Helper.pxToUnit(x, 0, _zoomFactor, _middle, _width, _height).x;
						double y = f.calculate(xU);
						if (inIndex<discons.size() && xU >= discons.get(inIndex)){
							first=true;
							inIndex++;
						}
						if (!Double.isNaN(y)){
							//if (!(quality==QUALITY_PREVIEW & x>getWidth())){
							if ((x>=-50 & x <= _width+50) | x % 5==0){
								float yPx = (float) Helper.unitToPx(0, y, _zoomFactor, _middle, _width, _height).y;
								// TODO: Find out why there is need to cut of y-values which have a high
								// absolute value.
								yPx = Math.max(Math.min(yPx, 2*_height), -_height);
								
								if (first)
									p.moveTo(x, yPx);
								first=false;
								p.lineTo(x, yPx);
							}
							//}
						}
						else
							first=true;
						//}
					}
					helperPaths.add(p);
				}
				/*
				ArrayList<Point> hIntersections = new ArrayList<Point>();
				if (disIntersections)
					for (int i = 0; i<_fkts.size()-1; i++)
						for (int j=i+1; j<_fkts.size(); j++)
							if (_fkts.get(i)!=null && _fkts.get(j)!=null && !_fkts.get(i).equals(_fkts.get(j)))
								hIntersections.addAll(PointMaker.getIntersections(_fkts.get(i), _fkts.get(j), quality==QUALITY_PREVIEW?pxToUnitX(0,_zoomFactorX,_middleX):pxToUnitX(-getWidth(),_zoomFactorX,_middleX), quality==QUALITY_PREVIEW?pxToUnitX(getWidth(),_zoomFactorX,_middleX):pxToUnitX(2*getWidth(),_zoomFactorX,_middleX), getDeltaUnit(15,_zoomFactorX)));
				*/
				synchronized (pathCollector){
					/*offsetX=0;
					offsetY=0;
					totalOffsetX=0;
					totalOffsetY=0;
					lastOriginX=_lastOriginX;
					lastOriginY=_lastOriginY;
					lastZoomX=1.0;
					lastZoomY=1.0;
					totalZoomX=_totalZoomX;
					totalZoomY=_totalZoomY;
					*/
					pathCollector.paths = helperPaths;
					//roots = new ArrayList<ArrayList<Point>>(hRoots);
					//extrema = new ArrayList<ArrayList<Point>>(hExtrema);
					//inflections = new ArrayList<ArrayList<Point>>(hInflections);
					//intersections = new ArrayList<Point>(hIntersections);
					//discontinuities = new ArrayList<ArrayList<Double>>(hDiscon);
				}
				//Looper.prepare();
				if (handler!=null)
					handler.sendEmptyMessage(0);
				//Looper.loop();
			}
			try{sleep(10);}catch(Exception e){}
		}
	}
}
