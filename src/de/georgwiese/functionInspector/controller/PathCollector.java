package de.georgwiese.functionInspector.controller;

import java.util.ArrayList;

import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.Helper;
import de.georgwiese.functionInspector.uiClasses.Point2D;

import android.graphics.Path;
import android.util.Log;

public class PathCollector {
	
	// Number of pixels plus width that are drawn in best quality
	static final int TOLERANCE_SIDE = 50;
	// Number of pixels plus height that are drawn
	static final int TOLERANCE_UPB  = 800;
	
	StateHolder sh;
	FktCanvas canvas;
	ArrayList<Path> paths;
	double[] originalPos, currentPos;
	
	public PathCollector(StateHolder sh, FktCanvas canvas) {
		this.sh = sh;
		this.canvas = canvas;
		paths = new ArrayList<Path>();
		originalPos = new double[2];
		currentPos  = new double[2];
		originalPos[0] = 0;
		originalPos[1] = 0;
		currentPos[0] = 0;
		currentPos[1] = 0;
	}
	
	public void setPaths(ArrayList<Path> paths, double[] pos) {
		this.paths = paths;
		originalPos = pos.clone();
		currentPos  = pos.clone();
	}
	
	public ArrayList<Path> getPaths() {
		return paths;
	}
	
	public void clearPaths(){
		paths.clear();
	}
	
	public void updateCurrentPos(){
		// get Old and new positions in Units as Point2D
		Point2D newCurrent = new Point2D(sh.getMiddle(0), sh.getMiddle(1));
		Point2D oldCurrent = new Point2D(currentPos[0], currentPos[1]);
		
		for(Path p:paths){
			p.offset(Helper.getDeltaPx(oldCurrent.x - newCurrent.x, sh.getZoom(0)),
					 Helper.getDeltaPx(newCurrent.y - oldCurrent.y, sh.getZoom(1)));
		}
		
		currentPos = sh.getMiddle().clone();
				
		if(Helper.getDeltaPx(Math.abs(currentPos[0] - originalPos[0]), sh.getZoom(0)) > TOLERANCE_SIDE ||
				Helper.getDeltaPx(Math.abs(currentPos[1] - originalPos[1]), sh.getZoom(1)) > TOLERANCE_UPB)
			sh.redraw = true;
	}
}
