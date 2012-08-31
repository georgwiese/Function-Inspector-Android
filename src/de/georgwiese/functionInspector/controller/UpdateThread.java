package de.georgwiese.functionInspector.controller;

import android.view.animation.AnimationUtils;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;

/**
 * This thread updates position and zoom whenever it is changed
 * for any reason but user input
 * @author Georg Wiese
 *
 */
public class UpdateThread extends Thread {

	FktCanvas canvas;
	StateHolder sh;
	long currentTime, prevTime;
	
	public UpdateThread(FktCanvas canvas, StateHolder sh) {
		this.canvas = canvas;
		this.sh     = sh;
		currentTime = 0;
		prevTime    = 0;
	}
	
	@Override
	public void run() {
		//if(doDyn){
		//velocityX*=FRICTION_FACTOR;
		//velocityY*=FRICTION_FACTOR;
		//set Max velocity
		//if (Math.abs(velocityX)>MAX_VELOCITY)
		//	velocityX=velocityX>0?MAX_VELOCITY:-MAX_VELOCITY;
		//if (Math.abs(velocityY)>MAX_VELOCITY)
		//	velocityY=velocityY>0?MAX_VELOCITY:-MAX_VELOCITY;
		//set Min velocity
		//if (Math.abs(velocityX)<1.0)
		//	velocityX=0;
		//if (Math.abs(velocityY)<1.0)
		//	velocityY=0;
		//if (velocityX==0&velocityY==0&(pxToUnitX(0)<minX | pxToUnitX(getWidth())>maxX))
		//	redraw=true;
		
		//middleX-=getDeltaUnit((float)velocityX,zoomFactorX);
		//middleY-=getDeltaUnit((float)velocityY,zoomFactorY);
		
		
		/*
		if(bZoomDyn){
			if(zoomIn){
				if (zoomFactorX<oldZoomX*2){
					zoomFactorX*=factor;
					zoomFactorY*=factor;
				}
				else{
					redraw=true;
					bZoomDyn=false;
				}
			}
			else{
				if (zoomFactorX>oldZoomX/2){
					zoomFactorX/=factor;
					zoomFactorY/=factor;
				}
				else{
					redraw=true;
					bZoomDyn=false;
				}	
			}
		}
		*/
		while(true){
			
			currentTime = AnimationUtils.currentAnimationTimeMillis();
			
			if ((prevTime != 0 && currentTime != prevTime &&
					sh.doDyn && sh.getSpeed(0) != 0 && sh.getSpeed(1) != 0) ||
					sh.doZoom){
				
				if (sh.doZoom)
					sh.updateZoom(currentTime - prevTime);
				else{
					sh.updatePos(currentTime - prevTime);
				}

				canvas.postInvalidate();
				prevTime = currentTime;
				
				try{sleep(15);}catch(Exception e){}
			}
			else{
				prevTime = currentTime;
				try{sleep(100);}catch(Exception e){}
			}
			
		}
		//}
	}
}
