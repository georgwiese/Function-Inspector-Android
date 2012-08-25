package de.georgwiese.functionInspector.uiClasses;

/**
 * Collection of helper Methods useful for translating coordinate systems.
 * @author Georg Wiese
 *
 */
public class Helper {
	
	public static double pxToUnit(float px, double zoom, double middle, float widthPx){
		return (px-widthPx/2)/30/zoom+middle;
	}
	
	public static float unitToPx(double unit, double zoom, double middle, float widthPx){
		return Math.round((unit-middle)*30*zoom+widthPx/2);
	}
	
	public static double getDeltaUnit(float deltaPixel, double z){
		return deltaPixel/30/z;
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

}
