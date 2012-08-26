package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.Helper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;

/**
 * Class to Handle every sort of touch input at FktCanvas
 * @author Georg Wiese
 *
 */
public class FktCanvasTouchListener implements OnTouchListener {
	
	StateHolder sh;
	FktCanvas canvas;
	float firstTouchX, firstTouchY, lastTouchX, lastTouchY;		// Store first and last touch position
	
	public FktCanvasTouchListener(StateHolder sh, FktCanvas canvas){
		this.sh = sh;
		this.canvas = canvas;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
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
			}
			else if (event.getPointerCount()==2){
				if (xPrevSpan==0){
					xPrevSpan=Math.abs(event.getX(1)-event.getX(0));
					if (xPrevSpan<20) xPrevSpan=0;
				}
				if (yPrevSpan==0){
					yPrevSpan=Math.abs(event.getY(1)-event.getY(0));
					if (yPrevSpan<20) yPrevSpan=0;
				}
				xSpan=Math.abs(event.getX(1)-event.getX(0));
				if (xSpan<20) xSpan=0;
				ySpan=Math.abs(event.getY(1)-event.getY(0));
				if (ySpan<20) ySpan=0;
				mScaleDetector.onTouchEvent(event);
			}
		}
		*/
		/*
		mGestDetector.onTouchEvent(event);
		if (!init){
			init=true;
			dynamics.run();
		}
		if(mScaleDetector.isInProgress())
			bZoom=true;
		*/
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			lastTouchX = event.getX();
			lastTouchY = event.getY();
			firstTouchX = lastTouchX;
			firstTouchY = lastTouchY;
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
			if ((sh.getMode()==StateHolder.MODE_PAN) != (event.getPointerCount()==2)){
				//if(AnimationUtils.currentAnimationTimeMillis()-timeLastZoomStop>500){
					sh.move(Helper.getDeltaUnit(lastTouchX-event.getX(0), sh.getZoom(0)),
							Helper.getDeltaUnit(event.getY(0)-lastTouchY, sh.getZoom(1)));
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
			lastTouchX=event.getX();
			lastTouchY=event.getY();
			//mLastTime=AnimationUtils.currentAnimationTimeMillis();
			//if (pxToUnitX(0)<minX | pxToUnitX(getWidth())>maxX)
			//	redraw=true;
			break;
		case MotionEvent.ACTION_UP:
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
