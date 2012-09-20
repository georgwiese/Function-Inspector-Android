package de.georgwiese.functionInspector.uiClasses;

public class Point2D {
	
	public double x, y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "( " + x + " | " + y + " )";
	}
}
