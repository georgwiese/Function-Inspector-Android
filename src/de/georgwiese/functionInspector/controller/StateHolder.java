package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import de.georgwiese.calculationFunktions.*;

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
	
	public boolean redraw;			// whether or not FktCanvas needs to redraw the functions
	ArrayList<Function> fkts;		// All the functions
	double[] zoom;					// current zoom factor, zoom[0] on x, zoom [1] in y axis
	double[] factor;				// what number should be factored out when drawing the coordinate system
	double[] middle;				// coordinate that is at the middle of the screen
	int mode;
	
	public StateHolder(){
		redraw = true;
		fkts = new ArrayList<Function>();
		zoom = new double[2];
		zoom[0] = 1.0;
		zoom[1] = 1.0;
		factor = new double[2];
		factor[0] = 1.0;
		factor[1] = 1.0;
		middle = new double[2];
		middle[0] = 0.0;
		middle[1] = 0.0;
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
	
	public double getFactor(int dimension){
		return factor[dimension];
	}
	
	public double getMiddle(int dimension){
		return middle[dimension];
	}
	
	public int getMode(){
		return mode;
	}
	
	public void move(double dx, double dy){
		middle[0] += dx;
		middle[1] += dy;
	}
}
