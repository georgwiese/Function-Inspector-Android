package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
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
	
	// Constants dealing with speed
	static final double FRICTION_FACTOR = 0.97;
	static final double MAX_SPEED       = 1;	// in dp / ms
	static final double MIN_SPEED		= 0.001;
	
	// Constants dealing with zoom and dynamic moving
	static final int FRAMES = 30;
	static final double ZOOM_IN_FACTOR  = 2.0;
	static final double ZOOM_IN_UPDATE  = Math.pow(ZOOM_IN_FACTOR, 1 / (double)FRAMES);
	
	public boolean isPro;
	public boolean redraw;			// whether or not FktCanvas needs to redraw the functions
	public boolean doDyn;			// whether or not the UpdateThread should move according to current speed
	public boolean doZoom;			// whether or not the UpdateThread should zoom according to desiredZoom
	public boolean doMove;
	public boolean preview;
	public boolean tryUsed;
	public boolean disSlope;
	public boolean zoomXY;			// whether or not X and Y axes should be zoomed independently
	ArrayList<Function> fkts;		// All the functions
	Point activePoint;
	double[] params;				// Parameters (a, b and c)
	double[] minParams;
	double[] maxParams;
	double[] zoom;					// current zoom factor, zoom[0] on x, zoom [1] in y axis
	double[] desiredZoom;			// To what Zoom level it should be animated
	double[] desiredMiddle;
	double[] moveUpdate;			// How much should be zoomed each frame
	double[] factor;				// what number should be factored out when drawing the coordinate system
	double[] middle;				// coordinate that is at the middle of the screen (current position)
	double[] speed;					// current Speed in units / ms
	long prevTimeSpeed;				// Time used to measure speed
	long prevTimeDynamics;			// Time used to calculate the offset
	int mode;						// current moving mode (see constants)
	float maxSpeedPx;				// MAX_SPEED converted in px / ms (done in constructor)
	public boolean disRoots,
	disExtrema, disInflections,		// public booleans for whether or not those points should be displayed
	disIntersections, disDiscon;
	public double currentX;
	
	// Prefs
	public static String KEY_ISPRO	 	= "isPro";
	public static String KEY_FOLDER 	= "prefs_folder";
	public static String KEY_FKTS   	= "fkt";
	public static String VAL_FKTS_END	= "fkt_end";
	public static String KEY_PARAMS 	= "param";
	public static String KEY_MINP   	= "minParam";
	public static String KEY_MAXP   	= "maxParam";
	public static String KEY_ZOOM   	= "zoom";
	public static String KEY_MIDDLE   	= "middle";
	public static String KEY_POINTS   	= "points";
	public static String KEY_TRY_USED  	= "tryUsed";
	public static String KEY_ZOOMXY  	= "zoomXY";
	public static String KEY_DISSLOPE  	= "disSlope";
	PrefsController pc;
	String screenshotFolder;
	
	public StateHolder(Context c, boolean isPro){
		pc = new PrefsController(c);
		this.isPro = isPro || pc.getDataBoolean(KEY_ISPRO, false);
		pc.putDataBoolean(KEY_ISPRO, isPro);
		
		initialize(c);
	}
	
	public void initialize(Context c){
		redraw   = true;
		doDyn    = false;
		doMove   = false;
		doZoom   = false;
		preview  = false;
		zoomXY   = pc.getPrefsBoolean(KEY_ZOOMXY, false) && isPro;
		tryUsed  = pc.getDataBoolean(KEY_TRY_USED, false);
		disSlope = pc.getDataBoolean(KEY_DISSLOPE, false) && isPro;
		fkts = new ArrayList<Function>();
		for (int i = 0; true; i++ ){
			if ((i >= 3) && !isPro)
				break;
			String f = pc.getDataStr(KEY_FKTS + i, VAL_FKTS_END);
			if (f.equals(VAL_FKTS_END))
				break;
			else
				fkts.add(new Function(f));
		}
		params = new double[3];
		for (int i = 0; i < params.length; i++)
			params[i] = pc.getDataFloat(KEY_PARAMS + i, 1);
		minParams = new double[3];
		for (int i = 0; i < params.length; i++)
			minParams[i] = pc.getDataFloat(KEY_MINP + i, -5);
		maxParams = new double[3];
		for (int i = 0; i < params.length; i++)
			maxParams[i] = pc.getDataFloat(KEY_MAXP + i, 5);
		zoom = new double[2];
		zoom[0] = pc.getDataFloat(KEY_ZOOM + 0, 1);
		zoom[1] = (isPro && zoomXY)?pc.getDataFloat(KEY_ZOOM + 1, 1):zoom[0];
		desiredZoom = zoom.clone();
		factor = new double[2];
		factor[0] = 1.0;
		factor[1] = 1.0;
		middle = new double[2];
		middle[0] = pc.getDataFloat(KEY_MIDDLE + 0, 0);
		middle[1] = pc.getDataFloat(KEY_MIDDLE + 1, 0);
		desiredMiddle = middle.clone();
		moveUpdate = new double[]{0, 0};
		speed = new double[]{0.0, 0.0};
		prevTimeSpeed = 0;
		prevTimeDynamics = 0;
		mode = MODE_PAN;
		maxSpeedPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)MAX_SPEED, c.getResources().getDisplayMetrics());
		
		disRoots 			= pc.getDataBoolean(KEY_POINTS + "roots", false);
		disExtrema 			= pc.getDataBoolean(KEY_POINTS + "extrema", false) && isPro;
		disInflections 		= pc.getDataBoolean(KEY_POINTS + "inflections", false) && isPro;
		disDiscon 			= pc.getDataBoolean(KEY_POINTS + "discontinuities", false) && isPro;
		disIntersections 	= pc.getDataBoolean(KEY_POINTS + "intersections", false) && isPro;
		
		// Prefs
		screenshotFolder = pc.getPrefStr(KEY_FOLDER, "Function Inspector");
	}
	
	public void reset(){
		redraw 	= true;
		zoom 	= new double[]{1.0, 1.0};
		middle 	= new double[]{0.0, 0.0};
		speed	= new double[]{0.0, 0.0};
	}
	
	public void setIsPro(boolean value){
		isPro = value;
		pc.putDataBoolean(KEY_ISPRO, isPro);
	}

	public void addFkt(String f){
		if (CalcFkts.check(f)){
			fkts.add(new Function(CalcFkts.formatFktString(f)));
			redraw=true;
		}
		else
			fkts.add(null);
		redraw = true;
	}
	
	public void clearFkts(){
		fkts.clear();
	}
	
	/**
	 * Will store all functions in SharedPreference
	 */
	public void storeFkts(){
		int i = 0;
		for (Function f: fkts){
			if (f != null){
				pc.putDataStr(KEY_FKTS + i, f.getString());
				i++;
			}
		}
		pc.putDataStr(KEY_FKTS + i, VAL_FKTS_END);
	}
	
	public Point getActivePoint() {
		return activePoint;
	}
	
	public void setActivePoint(Point activePoint) {
		this.activePoint = activePoint;
	}
	
	public double getZoom(int dimension){
		return zoom[dimension];
	}
	
	public double[] getZoom(){
		return zoom;
	}
	
	public void zoomIn(){
		desiredZoom[0] = zoom[0] * ZOOM_IN_FACTOR;
		desiredZoom[1] = zoom[1] * ZOOM_IN_FACTOR;
		speed = new double[]{0, 0};
		doZoom = true;
	}
	
	public void zoomOut(){
		desiredZoom[0] = zoom[0] / ZOOM_IN_FACTOR;
		desiredZoom[1] = zoom[1] / ZOOM_IN_FACTOR;
		speed = new double[]{0, 0};
		doZoom = true;
	}
	
	public void moveDyn(double toX, double toY){
		desiredMiddle = new double[]{toX, toY};
		speed = new double[]{0, 0};
		double[] moveBy = new double[]{toX - middle[0], toY - middle[1]};
		//double value = Math.sqrt(toX * toX + toY * toY);
		moveUpdate = new double[]{ moveBy[0] / FRAMES, moveBy[1] / FRAMES};
		doMove = true;
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
	
	public void toggleMode(){
		mode = mode==MODE_PAN?MODE_TRACE:MODE_PAN;
	}
	
	public ArrayList<Function> getFkts() {
		return fkts;
	}
	
	public double[] getParams() {
		return params;
	}
	
	public double[] getMinParams() {
		return minParams;
	}
	
	public double[] getMaxParams() {
		return maxParams;
	}
	
	public void setParam(int id, double value){
		redraw = true;
		activePoint = null;
		params[id] = value;
	}
	
	public void setMinParam(int id, double value){
		minParams[id] = value;
	}
	
	public void setMaxParam(int id, double value){
		maxParams[id] = value;
	}
	
	public void move(double dx, double dy){
		middle[0] += dx;
		middle[1] += dy;
		long currentTime = AnimationUtils.currentAnimationTimeMillis();
		if (prevTimeSpeed != 0 && prevTimeSpeed != currentTime){
			speed[0] = 0.5 * speed[0] + 0.5 * dx / (currentTime - prevTimeSpeed);
			speed[1] = 0.5 * speed[1] + 0.5 * dy / (currentTime - prevTimeSpeed);
		}
		//Log.d("Developer", "Speed: " + Math.sqrt(speed[0] * speed[0] + speed[1] * speed[1]));

		prevTimeSpeed = currentTime;
	}
	
	public double getSpeed(int dimension) {
		return speed[dimension];
	}
	
	/**
	 * Called from UpdateThread every frame to slower the speed.
	 * 
	 * @param exponent:
	 * 		Number of frames that have been skipped, assuming
	 * 		60 frames / s
	 */
	public void updatePos(long timeElapsed){
		//Log.d("Developer", "Exponent: " + (double)timeElapsed / (1000 / 60));
		if (doMove){
			middle[0] += moveUpdate[0] * (double) timeElapsed / (1000 / 60);
			middle[1] += moveUpdate[1] * (double) timeElapsed / (1000 / 60);
			// Check if done
			if (Math.abs(middle[0] - desiredMiddle[0]) < Math.abs(moveUpdate[0]) * 2 &&
					Math.abs(middle[1] - desiredMiddle[1]) < Math.abs(moveUpdate[1]) * 2)
				doMove = false;
		}
		else{
			double factor = Math.pow(FRICTION_FACTOR, (double)timeElapsed / (1000 / 60));
			for (int i = 0; i <  speed.length; i++){
				speed[i] *= factor;
				speed[i]  = Math.signum(speed[i]) * Math.min(Math.abs(speed[i]), Helper.getDeltaUnit(maxSpeedPx, zoom[i]));
				if (Math.abs(speed[i]) < Helper.getDeltaUnit((float) MIN_SPEED, zoom[i]))
					speed[i] = 0;
			}
			move(speed[0] * timeElapsed, speed[1] * timeElapsed);
		}
	}
	
	public void updateZoom(long timeElapsed){
		double factor = Math.pow(ZOOM_IN_UPDATE, (double)timeElapsed / (1000 / 60));
		if (zoom[0] < desiredZoom[0])
			zoom(factor);
		else
			zoom(1 / factor);
		// Check if done
		if (Math.abs(zoom[0]/desiredZoom[0]-1) < (ZOOM_IN_UPDATE - 1) * 2 &&
				Math.abs(zoom[1]/desiredZoom[1]-1) < (ZOOM_IN_UPDATE - 1) * 2)
			doZoom = false;
	}
	
	// Prefs
	public String getScreenshotFolder() {
		return screenshotFolder;
	}
	
	public void setScreenshotFolder(String screenshotFolder) {
		this.screenshotFolder = screenshotFolder;
	}
	
	public void saveCurrentState(){
		pc.putDataFloat(KEY_MIDDLE + 0, (float)middle[0]);
		pc.putDataFloat(KEY_MIDDLE + 1, (float)middle[1]);
		pc.putDataFloat(KEY_ZOOM + 0, (float)zoom[0]);
		pc.putDataFloat(KEY_ZOOM + 1, (float)zoom[1]);

		for (int i = 0; i < params.length; i++){
			pc.putDataFloat(KEY_PARAMS + i, (float)params[i]);
			pc.putDataFloat(KEY_MINP + i, (float)minParams[i]);
			pc.putDataFloat(KEY_MAXP + i, (float)maxParams[i]);
		}
		pc.putDataBoolean(KEY_POINTS + "roots", disRoots);
		pc.putDataBoolean(KEY_POINTS + "extrema", disExtrema);
		pc.putDataBoolean(KEY_POINTS + "inflections", disInflections);
		pc.putDataBoolean(KEY_POINTS + "intersections", disIntersections);
		pc.putDataBoolean(KEY_POINTS + "discontinuities", disDiscon);

		pc.putDataBoolean(KEY_TRY_USED, tryUsed);
		pc.putDataBoolean(KEY_DISSLOPE, disSlope);
		pc.putPrefStr(KEY_FOLDER, screenshotFolder);
	}
}
