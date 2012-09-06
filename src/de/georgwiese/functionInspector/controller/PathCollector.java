package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.Helper;
import de.georgwiese.functionInspector.uiClasses.Point2D;

import android.graphics.Matrix;
import android.graphics.Path;
import android.util.Log;

public class PathCollector {
	
	// Number of pixels plus width that are drawn in best quality
	static final int TOLERANCE_SIDE = 50;
	
	StateHolder sh;
	FktCanvas canvas;
	ArrayList<Path> paths;
	ArrayList<ArrayList<Point>> roots, extrema, inflections;
	ArrayList<Point> intersections;
	ArrayList<ArrayList<Double>> discontinuities;
	Matrix transMatrix;
	double[] originalPos, currentPos;
	double[] originalZoom, currentZoom;
	
	public PathCollector(StateHolder sh, FktCanvas canvas) {
		this.sh = sh;
		this.canvas = canvas;
		
		paths = new ArrayList<Path>();
		roots = new ArrayList<ArrayList<Point>>();
		extrema = new ArrayList<ArrayList<Point>>();
		inflections = new ArrayList<ArrayList<Point>>();
		intersections = new ArrayList<Point>();
		discontinuities = new ArrayList<ArrayList<Double>>();
		
		transMatrix = new Matrix();
		originalPos  = new double[2];
		currentPos   = new double[2];
		originalZoom = new double[2];
		currentZoom  = new double[2];
		originalPos[0] = 0;
		originalPos[1] = 0;
		currentPos[0] = 0;
		currentPos[1] = 0;
		originalZoom[0] = 1;
		originalZoom[1] = 1;
		currentZoom[0] = 1;
		currentZoom[1] = 1;
	}
	
	public void setPathsAndPoints(ArrayList<Path> paths, ArrayList<ArrayList<Point>> roots,
			ArrayList<ArrayList<Point>> extrema, ArrayList<ArrayList<Point>> inflections,
			ArrayList<Point> intersections, ArrayList<ArrayList<Double>> discontinuities,
			double[] pos, double[] zoom) {
		this.paths   = paths;
		originalPos  = pos.clone();
		currentPos   = pos.clone();
		originalZoom = zoom.clone();
		currentZoom  = zoom.clone();
		this.roots = roots;
		this.extrema = extrema;
		this.inflections = inflections;
		this.intersections = intersections;
		this.discontinuities = discontinuities;
	}
	
	public ArrayList<Path> getPaths() {
		return paths;
	}
	
	public ArrayList<ArrayList<Point>> getRoots() {
		return roots;
	}
	
	public ArrayList<ArrayList<Point>> getExtrema() {
		return extrema;
	}
	
	public ArrayList<ArrayList<Point>> getInflections() {
		return inflections;
	}
	
	public ArrayList<ArrayList<Double>> getDiscontinuities() {
		return discontinuities;
	}
	
	public ArrayList<Point> getIntersections() {
		return intersections;
	}
	
	public void clearPaths(){
		paths.clear();
	}
	
	public void update(){
		
		// get Old and new positions in Units as Point2D
		Point2D newCurrentPos = new Point2D(sh.getMiddle(0), sh.getMiddle(1));
		Point2D oldCurrentPos = new Point2D(currentPos[0], currentPos[1]);
		
		double[] newCurrentZoom = sh.getZoom();
		double[] oldCurrentZoom = currentZoom.clone();
		
		transMatrix.reset();
		transMatrix.setScale((float)(newCurrentZoom[0] / oldCurrentZoom[0]),
				(float)(newCurrentZoom[1] / oldCurrentZoom[1]),
				canvas.getWidth()/2, canvas.getHeight()/2);
		//transMatrix.setTranslate(Helper.getDeltaPx(oldCurrentPos.x - newCurrentPos.x, sh.getZoom(0)),
		//		Helper.getDeltaPx(newCurrentPos.y - oldCurrentPos.y, sh.getZoom(1)));

		for(Path p:paths){
			if (p != null){
				p.offset(Helper.getDeltaPx(oldCurrentPos.x - newCurrentPos.x, sh.getZoom(0)),
						Helper.getDeltaPx(newCurrentPos.y - oldCurrentPos.y, sh.getZoom(1)));
				p.transform(transMatrix);
			}
		}
		
		currentZoom = sh.getZoom().clone();
		currentPos  = sh.getMiddle().clone();
				
		if(Helper.getDeltaPx(Math.abs(currentPos[0] - originalPos[0]), sh.getZoom(0)) > TOLERANCE_SIDE ||
				oldCurrentZoom[0] != newCurrentZoom[0] || oldCurrentZoom[1] != newCurrentZoom[1])
			sh.redraw = true;
	}
}
