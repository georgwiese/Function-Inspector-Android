package de.georgwiese.calculationFunktions;

import java.util.ArrayList;

import android.util.Log;

public class PointMaker {
	public static ArrayList<Point> getExtrema(Function f, ArrayList<Double> discontinuities, double startX, double endX, double precision){
		double x=startX;
		double lastY = f.calculate(x-precision);
		double y = f.calculate(x);
		boolean higher = y>lastY;
		ArrayList<Point> result = new ArrayList<Point>();
		while (x<=endX){
			for (double d:discontinuities){
				if ((x-precision)<d && (x)>d){
					x+=precision;
					lastY=y;
					y= f.calculate(x);
					higher=y>lastY;
					break;
				}}
			if (y>lastY!=higher){
				double x1=x-2*precision;
				double x2=x;
				double m = (x1+x2)/2;
				for (int i=0; i<16; i++){
					if (f.calculate((m+x1)/2)>f.calculate((x2+m)/2)!=higher)
						x1=m;
					else
						x2=m;
					m = (x1+x2)/2;
				}
				if (!Double.isNaN(f.calculate(m)))
					result.add(new Point(m, f.calculate(m), Point.TYPE_EXTREMA));
			}
			higher=y>lastY;
			x+=precision;
			lastY=y;
			y= f.calculate(x);
		}
		return result;
	}

	public static ArrayList<Point> getInflectionPoints(Function f, ArrayList<Double> discontinuities, double startX, double endX, double precision){
		double x=startX;
		double lastY = f.slope(x-precision);
		double y = f.slope(x);
		boolean higher = y>lastY;
		ArrayList<Point> result = new ArrayList<Point>();
		while (x<=endX){
			for (double d:discontinuities){
				if ((x-precision)<d && (x)>d){
					x+=precision;
					lastY=y;
					y= f.slope(x);
					higher=y>lastY;
					break;
				}}
			if (y>lastY!=higher & Math.abs(y-lastY)>0.0001){
				double x1=x-2*precision;
				double x2=x;
				double m = (x1+x2)/2;
				for (int i=0; i<16; i++){
					if (f.slope((m+x1)/2)>f.slope((x2+m)/2)!=higher)
						x1=m;
					else
						x2=m;
					m = (x1+x2)/2;
				}
				if (!Double.isNaN(f.calculate(m)))
					result.add(new Point(m, f.calculate(m), Point.TYPE_INFLECTION));
			}
			higher=y>lastY;
			x+=precision;
			lastY=y;
			y= f.slope(x);
		}
		return result;
	}

	public static ArrayList<Point> getRoots(Function f, ArrayList<Point> extrema, ArrayList<Double> discontinuities, double startX, double endX, double precision){
		double x=startX;
		double y = f.calculate(x);
		boolean positive=y>0;
		ArrayList<Point> result = new ArrayList<Point>();
		if (extrema!=null)
			for (Point p:extrema)
				if (Math.abs(p.getY())<0.0002)
					result.add(new Point(p.getX(), 0, Point.TYPE_ROOT));
		while (x<=endX){
			for (double d:discontinuities){
				if (x<d && (x+precision)>d){
					x+=precision;
					y= f.calculate(x);
					positive=y>0;
					break;
				}}
			if (y==0)
				result.add(new Point(x, f.calculate(x), Point.TYPE_ROOT));
			else if (y>0!=positive){
				double x1=x-precision;
				double x2=x;
				double m = (x1+x2)/2;
				double ym = f.calculate(m);
				boolean done=false;
				for (int i=0; i<15; i++){
					if (ym==0){
						result.add(new Point(m, ym, Point.TYPE_ROOT));
						done=true;
						i=15;
					}
					else if (ym>0==positive)
						x1=m;
					else
						x2=m;
					m = (x1+x2)/2;
					ym = f.calculate(m);
				}
				if (!done && !Double.isNaN(ym))
					result.add(new Point(m, ym, Point.TYPE_ROOT));
			}
			positive=y>0;
			x+=precision;
			y= f.calculate(x);
		}
		//if (f.getString().equals("sin(x)-(tan(x))"))
			//for (Point p:result)
				//Log.d("Developer", "("+Double.toString(p.getX())+"|"+Double.toString(p.getY())+")");
		return result;
	}
	public static ArrayList<Point> getRoots (Function f, double startX, double endX, double precision){
		ArrayList<Double> discontinuities = f.getDiscontinuities(startX, endX, precision);
		return getRoots(f, getExtrema(f,discontinuities, startX, endX, precision), discontinuities, startX, endX, precision);
	}

	public static ArrayList<Point> getIntersections(Function f1, Function f2, double startX, double endX, double precision){
		Function f = new Function(f1.getString()+"-("+f2.getString()+")");
		f.setA(f1.getA());
		f.setB(f1.getB());
		f.setC(f1.getC());
		//Log.d("Developer", "Intersections: "+f.getString());
		ArrayList<Point> ps = getRoots(f, startX, endX, precision);
		ArrayList<Point> result = new ArrayList<Point>();
		for (Point p:ps)
			result.add(new Point(p.getX(), f1.calculate(p.getX()), Point.TYPE_FUNCTION_INTERSECTION));
		return result;
	}
	
	public static ArrayList<Double> getDiscontinuities(Function f, double startX, double endX, double precision){
		return f.getDiscontinuities(startX, endX, precision);
	}
}
