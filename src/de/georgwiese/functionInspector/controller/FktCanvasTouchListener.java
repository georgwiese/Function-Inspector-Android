package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.functionInspector.controller.FktCanvasGestureListener.SpanStorage;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.Helper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;

/**
 * Class to Handle every sort of touch input at FktCanvas
 * @author Georg Wiese
 *
 */
public class FktCanvasTouchListener implements OnTouchListener {
	
	UIController uic;
	StateHolder sh;
	FktCanvas canvas;
	float firstTouchX, firstTouchY, lastTouchX, lastTouchY;		// Store first and last touch position
	
	FktCanvasGestureListener gListener;
	ScaleGestureDetector scaleGestDet;
	GestureDetector gestureDetector;
	SpanStorage ss;
	
	public FktCanvasTouchListener(UIController uic, StateHolder sh, PathCollector pathCollector, FktCanvas canvas){
		this.uic = uic;
		this.sh = sh;
		this.canvas = canvas;
		
		ss = new SpanStorage();
		gListener = new FktCanvasGestureListener(canvas, sh, pathCollector, uic, ss);
		scaleGestDet = new ScaleGestureDetector(canvas.getContext(), gListener);
		gestureDetector = new GestureDetector(gListener);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		
		sh.currentX = Helper.pxToUnit(event.getX(), 0, sh.getZoom(), sh.getMiddle(), canvas.getWidth(), canvas.getHeight()).x;
		
		if(sh.getMode() == StateHolder.MODE_TRACE)
			uic.updateTraceTv();

		// TODO: Use code that is commented out and fix jumping issue
		float xPos = // event.getPointerCount() == 2 ? (event.getX(0) + event.getX(1)) / 2 :
													event.getX(0);
		float yPos = // event.getPointerCount() == 2 ? (event.getY(0) + event.getY(1)) / 2 :
													event.getY(0);
		
		/*
		if (SHOW_TOUCH_POSITION){
			xs = new ArrayList<Float>();
			ys = new ArrayList<Float>();
			for (int i=0; i<event.getPointerCount();i++){
				xs.add(event.getX(i));
				ys.add(event.getY(i));
			}
		}
		*/
		/*
		if (sh.getMode()==StateHolder.MODE_PAN){
			if (event.getPointerCount()<2){
				xPrevSpan=0;
				yPrevSpan=0;
				xSpan=0;
				xSpan=0;
			}*/
		if (event.getPointerCount()==2){
			// assign span values, minimum of 20
			ss.currentSpanX = Math.max(Math.abs(event.getX(1)-event.getX(0)), 20);
			ss.currentSpanY = Math.max(Math.abs(event.getY(1)-event.getY(0)), 20);
		}
		//}
		
		scaleGestDet.onTouchEvent(event);
		
		// If user just finished zooming don't move (otherwise it may
		// move pretty quickly when the user lifts both fingers
		if (gListener.getTimeLastZoomStop() > AnimationUtils.currentAnimationTimeMillis() - 300)
			return true;
		/*
		if (!init){
			init=true;
			dynamics.run();
		}
		if(mScaleDetector.isInProgress())
			bZoom=true;
		*/
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			lastTouchX = xPos;
			lastTouchY = yPos;
			firstTouchX = lastTouchX;
			firstTouchY = lastTouchY;
			sh.doDyn = false;
			
			//Log.d("Developer", "ACTION_DOWN");
			/*
			velStoreX=0.0;
			velStoreY=0.0;
			velocityX=0.0;
			velocityY=0.0;
			mLastTime=AnimationUtils.currentAnimationTimeMillis();
			if (mode==MODE_PAN){
				mLastTime=AnimationUtils.currentAnimationTimeMillis();
				if (disDiscon)
					for (int i=0; i<discontinuities.size(); i++)
						for (Double d:discontinuities.get(i))
							if (Math.abs(event.getX()-unitToPxX(d))<25)
								activePoint=new Point(d, 0, Point.TYPE_DISCONTINUITY);
				if (disRoots)
					for (int i=0; i<roots.size(); i++)
						for (Point p:roots.get(i))
							if (distance(p,event.getX(),event.getY())<25)
								activePoint=p;
				if (disExtrema)
					for (int i=0; i<extrema.size(); i++)
						for (Point p:extrema.get(i))
							if (distance(p,event.getX(),event.getY())<25)
								activePoint=p;
				if (disInflections)
					for (int i=0; i<inflections.size(); i++)
						for (Point p:inflections.get(i))
							if (distance(p,event.getX(),event.getY())<25)
								activePoint=p;
				if (disIntersections)
					for (Point p:intersections)
						if (distance(p,event.getX(),event.getY())<25)
							activePoint=p;
				if (activePoint!=null)
					pointDisplay.setVisibility(VISIBLE);
				//else
					//pointDisplay.setVisibility(GONE);
			}
			*/
			break;
		case MotionEvent.ACTION_MOVE:
			if ((sh.getMode()==StateHolder.MODE_PAN) || (event.getPointerCount()==2)){
				//if(AnimationUtils.currentAnimationTimeMillis()-timeLastZoomStop>500){
					sh.move(Helper.getDeltaUnit(lastTouchX-xPos, sh.getZoom(0)),
							Helper.getDeltaUnit(yPos-lastTouchY, sh.getZoom(1)));
					/*
					if (distance(mFirstTouchX, mFirstTouchY, event.getX(0), event.getY(0))>30 && AnimationUtils.currentAnimationTimeMillis()-mLastTime>0){
						velStoreX=(event.getX(0)-mLastTouchX)/(AnimationUtils.currentAnimationTimeMillis()-mLastTime)*50;
						velStoreY=(-event.getY(0)+mLastTouchY)/(AnimationUtils.currentAnimationTimeMillis()-mLastTime)*50;
					}
					*/
				//}
			}
			/*
			else if (mode!=MODE_PAN){
				currentX=pxToUnitX(event.getX());
				pointDisplay.setVisibility(VISIBLE);
			}
			*/
			lastTouchX=xPos;
			lastTouchY=yPos;
			//mLastTime=AnimationUtils.currentAnimationTimeMillis();
			//if (pxToUnitX(0)<minX | pxToUnitX(getWidth())>maxX)
			//	redraw=true;
			break;
		case MotionEvent.ACTION_UP:
			sh.doDyn = true;
			/*
			xs=null;
			velocityX=velStoreX;
			velocityY=velStoreY;
			doDyn=true;
			if (pxToUnitX(0)<minX | pxToUnitX(getWidth())>maxX)
				redraw=true;
				*/
		}
		// update canvas View
		canvas.invalidate();
		return true;
	}

}
