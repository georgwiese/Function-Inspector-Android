package de.georgwiese.calculationFunktions;

public class Point {
	private double x,y;
	private int type;
	public final static int TYPE_NONE = 0;
	public final static int TYPE_EXTREMA = 1;
	public final static int TYPE_ROOT = 2;
	public final static int TYPE_FUNCTION_INTERSECTION = 3;
	public final static int TYPE_INFLECTION = 4;
	public final static int TYPE_DISCONTINUITY = 5;
	
	public Point (double x, double y, int type){
		this.x=x;
		this.y=y;
		this.type=type;
	}

	public int getType(){
		return type;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
}
