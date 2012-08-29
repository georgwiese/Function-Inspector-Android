package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import android.util.Log;
import android.view.animation.AnimationUtils;

import de.georgwiese.calculationFunktions.*;
import de.georgwiese.functionInspector.uiClasses.Helper;

/**
 * This class is used to save all necessary information that discribe the
 * current state of Function Inspector, like the functions, zoom factors,
 * current position and much more.
 * @author Georg Wiese
 *
 */
public class StateHolder {
	// Constants describing the current mode
	public static final int MODE_PAN   = 0;
	public static final int MODE_TRACE = 1;
	public static final int MODE_SLOPE = 2;
	
	// Constants dealing with speed
	static final double FRICTION_FACTOR = 0.9;
	static final double MAX_SPEED       = 2.0;	// in px / ms
	static final double MIN_SPEED		= 0.03;
	
	// Constants dealing with zoom
	static final double ZOOM_IN_FACTOR  = 2.0;
	static final double ZOOM_IN_UPDATE  = 1.05;
	
	public boolean redraw;			// whether or not FktCanvas needs to redraw the functions
	public boolean doDyn;			// whether or not the UpdateThread should move according to current speed
	public boolean doZoom;			// whether or not the UpdateThread should zoom according to desiredZoom
	ArrayList<Function> fkts;		// All the functions
	double[] params;				// Parameters (a, b and c)
	double[] zoom;					// current zoom factor, zoom[0] on x, zoom [1] in y axis
	double[] desiredZoom;			// To what Zoom level it should be animated
	double[] factor;				// what number should be factored out when drawing the coordinate system
	double[] middle;				// coordinate that is at the middle of the screen (current position)
	double[] speed;					// current Speed in units / ms
	long prevTimeSpeed;				// Time used to measure speed
	long prevTimeDynamics;			// Time used to calculate the offset
	int mode;						// current moving mode (see constants)
	
	public StateHolder(){
		redraw = true;
		doDyn  = false;
		doZoom = false;
		fkts = new ArrayList<Function>();
		params = new double[3];
		params[0] = 1.0;
		params[1] = 1.0;
		params[2] = 1.0;
		zoom = new double[2];
		zoom[0] = 1.0;
		zoom[1] = 1.0;
		desiredZoom = new double[2];
		desiredZoom[0] = 1.0;
		desiredZoom[1] = 1.0;
		factor = new double[2];
		factor[0] = 1.0;
		factor[1] = 1.0;
		middle = new double[2];
		middle[0] = 0.0;
		middle[1] = 0.0;
		speed = new double[2];
		speed[0] = 0.0;
		speed[1] = 0.0;
		prevTimeSpeed = 0;
		prevTimeDynamics = 0;
		mode = MODE_PAN;
	}

	public void addFkt(String f){
		if (CalcFkts.check(f)){
			fkts.add(new Function(CalcFkts.formatFktString(f)));
			redraw=true;
		}
	}
	
	public double getZoom(int dimension){
		return zoom[dimension];
	}
	
	public double[] getZoom(){
		return zoom;
	}
	
	public void zoomIn(){
		desiredZoom[0] = ZOOM_IN_FACTOR * zoom[0];
		desiredZoom[1] = ZOOM_IN_FACTOR * zoom[1];
		doZoom = true;
	}
	
	public void zoom(double factor){
		zoom(factor, factor);
	}
	
	public void zoom(double factorX, double factorY){
		zoom[0] = zoom[0] * factorX;
		zoom[1] = zoom[1] * factorY;
	}
	
	public double getFactor(int dimension){
		return factor[dimension];
	}
	
	public double getMiddle(int dimension){
		return middle[dimension];
	}
	
	public double[] getMiddle(){
		return middle;
	}
	
	public int getMode(){
		return mode;
	}
	
	public ArrayList<Function> getFkts() {
		return fkts;
	}
	
	public double[] getParams() {
		return params;
	}
	
	public void move(double dx, double dy){
		middle[0] += dx;
		middle[1] += dy;
		long currentTime = AnimationUtils.currentAnimationTimeMillis();
		if (prevTimeSpeed != 0 && prevTimeSpeed != currentTime){
			speed[0] = 0.5 * speed[0] + 0.5 * dx / (currentTime - prevTimeSpeed);
			speed[1] = 0.5 * speed[1] + 0.5 * dy / (currentTime - prevTimeSpeed);
		}
		prevTimeSpeed = currentTime;
	}
	
	public double getSpeed(int dimension) {
		return speed[dimension];
	}
	
	/**
	 * Called from UpdateThread every 50 ms to slower the speed
	 */
	public void updateSpeed(){
		for (int i = 0; i <  speed.length; i++){
			speed[i] *= FRICTION_FACTOR;
			speed[i]  = Math.signum(speed[i]) * Math.min(Math.abs(speed[i]), Helper.getDeltaUnit((float)MAX_SPEED, zoom[i]));
			if (Math.abs(speed[i]) < Helper.getDeltaUnit((float) MIN_SPEED, zoom[i]))
				speed[i] = 0;
		}
	}
	
	public void updateZoom(){
		for (int i = 0; i < zoom.length; i++){
			if (zoom[i] < desiredZoom[i])
				zoom[i] *= ZOOM_IN_UPDATE;
			else
				zoom[i] /= ZOOM_IN_UPDATE;
		}
		// Check if done
		if (Math.abs(zoom[0]/desiredZoom[0]-1) < (ZOOM_IN_UPDATE - 1) * 2 &&
				Math.abs(zoom[1]/desiredZoom[1]-1) < (ZOOM_IN_UPDATE - 1) * 2)
			doZoom = false;
	}
}
