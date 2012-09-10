package de.georgwiese.functionInspector.uiClasses;

import java.text.DecimalFormat;

/**
 * Collection of helper Methods useful for translating coordinate systems.
 * @author Georg Wiese
 *
 */
public class Helper {
	/*
	public static double pxToUnit(float px, double zoom, double middle, float widthPx){
		return (px-widthPx/2)/30/zoom+middle;
	}
	
	public static float unitToPx(double unit, double zoom, double middle, float widthPx){
		return Math.round((unit-middle)*30*zoom+widthPx/2);
	}
	*/
	public static Point2D pxToUnit(double px, double py, double[] zoom, double[] middle, int width, int height){
		return new Point2D((px-width/2)/30/zoom[0]+middle[0], (height/2-py)/30/zoom[1]+middle[1]);
	}
	
	public static Point2D unitToPx(double ux, double uy, double[] zoom, double[] middle, int width, int height){
		return new Point2D(Math.round((ux-middle[0])*30*zoom[0]+width/2), Math.round((middle[1] - uy)*30*zoom[1] + height/2));
	}
	
	public static double getDeltaUnit(float deltaPixel, double z){
		return deltaPixel/30/z;
	}
	
	public static float getDeltaPx(double deltaUnit, double z){
		return (float)(deltaUnit*30*z);
	}
	
	public static double getSteps(double z, double factor){
		int exponent = 0;
		double steps = getDeltaUnit(25,z);
		steps /= factor;
		while (steps<1 | steps>=10){
			if (steps<1){
				steps *= 10;
				--exponent;
			}
			if (steps >=10){
				steps /= 10;
				++exponent;
			}
		}
		if (steps>5)
			steps=10;
		else if (steps>2)
			steps=5;
		else if (steps>1)
			steps=2;
		else
			steps=1;
		return steps*Math.pow(10, exponent)*factor;
	}
	
	public static String getFactorString(double factor){

		if (factor == Math.PI)
			return "\u03C0";
		if (factor == Math.PI/180)
			return "\u00B0";
		if (factor == Math.E)
			return "e";
		if (factor == 1.0)
			return "";
		DecimalFormat df = new DecimalFormat("0.0##");
		return "*" + df.format(factor);
	}

}
