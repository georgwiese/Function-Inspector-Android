package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.Helper;
import de.georgwiese.functionInspector.uiClasses.Point2D;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FktCanvasGestureListener extends SimpleOnGestureListener implements OnScaleGestureListener {

	FktCanvas canvas;
	StateHolder sh;
	long timeLastZoomStop;
	PathCollector pathCollector;
	UIController uic;
	SpanStorage ss;
	
	public FktCanvasGestureListener(FktCanvas canvas, StateHolder sh, PathCollector pathCollector, UIController uic, SpanStorage ss) {
		this.canvas = canvas;
		this.sh     = sh;
		timeLastZoomStop = 0;
		this.pathCollector = pathCollector;
		this.uic = uic;
		this.ss = ss;
	}
	
	public long getTimeLastZoomStop() {
		return timeLastZoomStop;
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		sh.redraw=true;
		timeLastZoomStop=AnimationUtils.currentAnimationTimeMillis();
		ss.currentSpanX = 0;
		ss.currentSpanY = 0;
		ss.prevSpanX = 0;
		ss.prevSpanY = 0;
	}
	
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		ss.prevSpanX = ss.currentSpanX;
		ss.prevSpanY = ss.currentSpanY;
		return true;
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		/*if (bZoomXY){
			if (xPrevSpan!=0 & xSpan!=0)
				zoomFactorX=oldZoomX*xSpan/xPrevSpan*detector.getScaleFactor();
			if (yPrevSpan!=0 & ySpan!=0)
				zoomFactorY=oldZoomY*ySpan/yPrevSpan*detector.getScaleFactor();
		}
		else{
			zoomFactorX*=detector.getScaleFactor();
			zoomFactorY*=detector.getScaleFactor();
		}*/
		if (sh.zoomXY){
		if (ss.currentSpanX != 0 && ss.currentSpanY != 0 &&
			ss.prevSpanX != 0 && ss.prevSpanY != 0)
				sh.zoom(ss.currentSpanX/ss.prevSpanX, ss.currentSpanY/ss.prevSpanY);
			ss.prevSpanX = ss.currentSpanX;
			ss.prevSpanY = ss.currentSpanY;
		}
		else
			sh.zoom(detector.getScaleFactor());
		//sh.zoom(detector.getCurrentSpanX()/detector.getPreviousSpanX(),
		//		detector.getCurrentSpanY()/detector.getPreviousSpanY());
		canvas.invalidate();
		return true;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		super.onLongPress(e);
		
		sh.toggleMode();
		sh.currentX = Helper.pxToUnit(e.getX(), 0, sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight()).x;
		uic.updateMode();
		canvas.invalidate();
	}
	
	/*
	@Override
	public void onLongPress(MotionEvent e) {
		if (mode==MODE_PAN){
			final SharedPreferences prefs = mContext.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
			final Dialog d = new Dialog(mContext);
			d.setTitle(R.string.prefs_factor_title);
			LinearLayout ll = new LinearLayout(mContext);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.setPadding(20, 0, 20, 20);
			LinearLayout ll2 = new LinearLayout(mContext);
			ll2.setOrientation(LinearLayout.VERTICAL);
			TextView x = new TextView(mContext);
			x.setText(R.string.prefs_factor_x);
			x.setGravity(Gravity.CENTER);
			TextView y = new TextView(mContext);
			y.setText(R.string.prefs_factor_y);
			y.setGravity(Gravity.CENTER);
			SwitchButtonSet sbx = new SwitchButtonSet(mContext, null, 4);
			sbx.setCaptions(new String[]{"1","PI","e", "DEG"});
			sbx.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			sbx.setState(prefs.getInt("prefs_factor_x", 0));
			sbx.setOnStateChangedListener(new SwitchButtonSet.OnStateChangedListener() {
				@Override
				public void onStateChanged(int newState) {
					SharedPreferences.Editor editor = prefs.edit();
					editor.putInt("prefs_factor_x", newState);
					factorX=newState;
					editor.commit();
				}
			});
			SwitchButtonSet sby = new SwitchButtonSet(mContext, null, 4);
			sby.setState(prefs.getInt("prefs_factor_y", 0));
			sby.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			sby.setOnStateChangedListener(new SwitchButtonSet.OnStateChangedListener() {
				@Override
				public void onStateChanged(int newState) {
					SharedPreferences.Editor editor = prefs.edit();
					editor.putInt("prefs_factor_y", newState);
					factorY=newState;
					editor.commit();
				}
			});
			sby.setCaptions(new String[]{"1","PI", "e", "DEG"});
			Button ok = new Button(mContext);
			ok.setText(R.string.ok);
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					d.cancel();
				}
			});
			//ll.addView(description);
			ll.addView(x);
			ll.addView(sbx);
			ll.addView(y);
			ll.addView(sby);
			ll2.addView(ll);
			ll2.addView(ok);
			View v = new View(mContext);
			v.setLayoutParams(new LayoutParams(1, 1));
			//ll2.addView(v); //doesn't work otherwise for some reason...
			d.setContentView(ll2);
			d.show();
		}
		super.onLongPress(e);
	}
	*/
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		sh.zoomIn();
		Point2D pos = Helper.pxToUnit(e.getX(), e.getY(), sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight());
		sh.moveDyn(pos.x, pos.y);
		return true;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		
		if (sh.getMode() == StateHolder.MODE_TRACE){
			sh.toggleMode();
			uic.updateMode();
			canvas.invalidate();
			return true;
		}
		
		synchronized (pathCollector) {
			ArrayList<ArrayList<Point>> roots = pathCollector.getRoots();
			ArrayList<ArrayList<Point>> extrema = pathCollector.getExtrema();
			ArrayList<ArrayList<Point>> inflections = pathCollector.getInflections();
			ArrayList<ArrayList<Double>> discontinuities = pathCollector.getDiscontinuities();
			ArrayList<Point> intersections = pathCollector.getIntersections();
			
			boolean hasNoActive = sh.getActivePoint() == null;
			sh.setActivePoint(null);
			
			if (sh.disExtrema){
				for (int i=0; i<extrema.size(); i++)
					for (Point p:extrema.get(i))
						if (distance(p,e.getX(),e.getY())<25)
							sh.setActivePoint(p);}
			if (sh.disInflections){
				for (int i=0; i<inflections.size(); i++)
					for (Point p:inflections.get(i))
						if (distance(p,e.getX(),e.getY())<25)
							sh.setActivePoint(p);}
			if (sh.disIntersections){
				for (Point p:intersections)
					if (distance(p,e.getX(),e.getY())<25)
						sh.setActivePoint(p);}
			if (sh.disDiscon){
				for (int i=0; i<discontinuities.size(); i++)
					for (Double d:discontinuities.get(i))
						if (Math.abs(e.getX() - (float)Helper.unitToPx(d.doubleValue(), 0, sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight()).x)<25)
							sh.setActivePoint(new Point(d, 0, Point.TYPE_DISCONTINUITY));}
			if (sh.disRoots){
				for (int i=0; i<roots.size(); i++)
					for (Point p:roots.get(i))
						if (distance(p,e.getX(),e.getY())<25)
							sh.setActivePoint(p);}
			
			if (hasNoActive && sh.getActivePoint() == null)
				uic.hideAllMenus();
			
			//Point2D pos = Helper.pxToUnit(e.getX(), e.getY(), sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight());
			//sh.moveDyn(pos.x, pos.y);
			
		}
		
		/*if (activePoint != null)
			pointDisplay.setVisibility(VISIBLE);
		else if (mode==MODE_PAN)
			pointDisplay.setVisibility(GONE);*/
		canvas.invalidate();
		return super.onSingleTapConfirmed(e);
	}
	
	private double distance(Point p, float x, float y){
		float dx = (float)Helper.unitToPx(p.getX(), 0, sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight()).x - x;
		float dy = (float)Helper.unitToPx(0, p.getY(), sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight()).y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Needed in order to implement independant Zooming (each axis on its own).
	 * Android does offer this functionality, but only for API level 11+.
	 * @author Georg Wiese
	 *
	 */
	public static class SpanStorage{
		public double currentSpanX, currentSpanY;
		public double prevSpanX, prevSpanY;
		
		public SpanStorage() {
			currentSpanX = 0;
			currentSpanY = 0;
			prevSpanX    = 0;
			prevSpanY    = 0;
		}
	}
}
