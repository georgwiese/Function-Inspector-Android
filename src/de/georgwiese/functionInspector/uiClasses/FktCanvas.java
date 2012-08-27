package de.georgwiese.functionInspector.uiClasses;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.functionInspector.controller.PathCollector;
import de.georgwiese.functionInspector.controller.StateHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * This View will be used to display the coordinate System width the graphs.
 * @author Georg Wiese
 *
 */
public class FktCanvas extends LinearLayout {
	// Color constants
	static final int COLOR_BACKGROUND   = Color.BLACK;
	static final int COLOR_AXES         = Color.WHITE;
	static final int COLOR_LINES        = Color.parseColor("#222222");
	static final int[] COLORS_GRAPHS    = {Color.RED, Color.GREEN, Color.CYAN};
	static final int COLOR_ACTIVE_POINT = Color.YELLOW;
	static final int COLOR_INTERSECTION = Color.GRAY;
	
	Paint paint;
	StateHolder sh;
	PathCollector pathCollector;
	double[] steps;
	protected DecimalFormat df1,df2;
	OnSizeChangedListener oscl;
	
	public FktCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		invalidate();
		paint = new Paint();
		steps = new double[2];
		steps[0] = 1;
		steps[1] = 1;
		df1 = new DecimalFormat("0.0##");
		df2 = new DecimalFormat("0.00");
	}
	
	public void setOnSizeChangedListener(OnSizeChangedListener oscl){
		this.oscl = oscl;
	}
	
	/**
	 * Needs to be called before first drawing
	 * @param sh: StateHolder object
	 */
	public void setProps(StateHolder sh, PathCollector pathCollector){
		this.sh = sh;
		this.pathCollector = pathCollector;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// For testing:
		//sh.redraw = true;
		
		//if (!redrawThreadStarted){
		//	redrawThreadStarted=true;
		//	redrawThread.start();
		//}
		if(sh.redraw){
			steps[0] = Helper.getSteps(sh.getZoom(0), sh.getFactor(0));
			steps[0] = Helper.getSteps(sh.getZoom(0), sh.getFactor(0));
		}
		//if(bZoom | bZoomDyn){
		//	middleX=zoomToX-((zoomToX-lastMiddleX)*oldZoomX/totalZoomX);
		//	middleY=zoomToY-((zoomToY-lastMiddleY)*oldZoomY/totalZoomY);
		//}
		
		// Fill background and set paint properties
		canvas.drawColor(COLOR_BACKGROUND);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(15);
		paint.setStyle(Style.FILL_AND_STROKE);
		
		// Calculate area to draw in coordinate system units
		Point2D topLeft = Helper.pxToUnit(0, 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight());
		Point2D bottomRight = Helper.pxToUnit(getWidth(), getHeight(), sh.getZoom(), sh.getMiddle(), getWidth(), getHeight());
		double leftBorder = Double.valueOf(topLeft.x/steps[0]).intValue()-1;
		double rightBorder = Double.valueOf(bottomRight.x/steps[0]).intValue()+1;
		double bottomBorder = Double.valueOf(bottomRight.y/steps[1]).intValue()-1;
		double topBorder = Double.valueOf(topLeft.y/steps[1]).intValue()+1;
		Point2D zero = Helper.unitToPx(0, 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight());
		
		// Draw vertical lines of coordinate system
		for (double i = leftBorder; i <= rightBorder; i++){
			paint.setColor(COLOR_LINES);
			float x = (float) Helper.unitToPx(i*steps[0], 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x;
			canvas.drawLine(x, 0, x, getHeight(), paint);
			paint.setColor(COLOR_AXES);
			canvas.drawLine(x, (float)zero.y, x, (float)zero.y+5, paint);
			paint.setStrokeWidth(1);
			if (i!=0 & i%2==0){
				String text;
				if (sh.getFactor(0) == Math.PI)
					text=df1.format(i*steps[0]/Math.PI)+"\u03C0";
				else if (sh.getFactor(0) == Math.PI/180)
					text=df1.format(i*steps[0]/Math.PI*180)+"\u00B0";
				else if (sh.getFactor(0) == Math.E)
					text=df1.format(i*steps[0]/Math.E)+"e";
				else
					text=df1.format(i*steps[0]);
				//TODO: Implement borderTop and borderBottom
				if (zero.y <= getHeight()-30 && zero.y>=0)
					canvas.drawText(text, x, (float)zero.y+20, paint);
				else if (zero.y > getHeight()-30)
					canvas.drawText(text, x, getHeight()-10, paint);
				else if (zero.y < 0)
					canvas.drawText(text, x, 20, paint);
			}
			paint.setStrokeWidth(2);
		}
		
		// Draw horizontal lines of coordinate system
		paint.setTextAlign(Align.RIGHT);
		for (double i = topBorder; i>=bottomBorder; i--){
			paint.setColor(COLOR_LINES);
			float y = (float)Helper.unitToPx(0, i*steps[1], sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y;
			canvas.drawLine(0, y, getWidth(), y, paint);
			paint.setColor(COLOR_AXES);
			canvas.drawLine((float) zero.x, y, (float) zero.x-3, y, paint);
			paint.setStrokeWidth(1);
			if (i!=0 & i%2==0){
				String text;
				if (sh.getFactor(1) == Math.PI)
					text=df1.format(i*steps[1]/Math.PI)+"\u03C0";
				else if (sh.getFactor(1) == Math.PI/180)
					text=df1.format(i*steps[1]/Math.PI*180)+"\u00B0";
				else if (sh.getFactor(1) == Math.E)
					text=df1.format(i*steps[1]/Math.E)+"e";
				else
					text=df1.format(i*steps[1]);
				
				if (zero.x <= getWidth() && zero.x>=45)
					canvas.drawText(text, (float) (zero.x-10), y+5, paint);
				else if (zero.x > getWidth())
					canvas.drawText(text, getWidth()-10, y-10, paint);
				else if (zero.x < 45){
					paint.setTextAlign(Align.LEFT);
					canvas.drawText(text, 5, y+5, paint);
				}
			}
			paint.setStrokeWidth(2);
		}
		
		// Draw axes
		paint.setColor(COLOR_AXES);
		canvas.drawLine((float)zero.x, 0, (float)zero.x, getHeight(), paint);
		canvas.drawLine(0, (float)zero.y, getWidth(), (float) zero.y, paint);
		paint.setStyle(Style.STROKE);
		
		//synchronized (lockDrawing){
			/*
			offsetX=unitToPxX(0)-lastOriginX-totalOffsetX;
			offsetY=unitToPxY(0)-lastOriginY-totalOffsetY;
			totalOffsetX+=offsetX;
			totalOffsetY+=offsetY;
			for (Path p:paths)
				p.offset(offsetX, offsetY);
			
			Matrix matrix = new Matrix();
			lastZoomX=zoomFactorX/totalZoomX;
			totalZoomX*=lastZoomX;
			lastZoomY=zoomFactorY/totalZoomY;
			totalZoomY*=lastZoomY;
			matrix.setScale((float)(lastZoomX),(float)(lastZoomY),unitToPxX(0),unitToPxY(0));
			for (Path p:paths)
				p.transform(matrix);
			stepsX=getSteps(zoomFactorX, factorX);
			stepsY=getSteps(zoomFactorY, factorY);
			*/

		synchronized(pathCollector){
			pathCollector.updateCurrentPos();
			
			ArrayList<Path> paths = pathCollector.getPaths();
			
			for (int i=0; i<paths.size();i++){
				paint.setColor(COLORS_GRAPHS[i%COLORS_GRAPHS.length]);
				
				/*
				if (disRoots | disExtrema | disInflections |disDiscon){
					paint.setStyle(Style.FILL_AND_STROKE);
					if (disRoots)
						if (i<roots.size())
							for (Point p:roots.get(i))
								canvas.drawCircle(unitToPxX(p.getX()), unitToPxY(p.getY()), 5, paint);
					if (disExtrema)
						if (i<extrema.size())
							for (Point p:extrema.get(i))
								canvas.drawCircle(unitToPxX(p.getX()), unitToPxY(p.getY()), 5, paint);
					if (disInflections)
						if (i<inflections.size())
							for (Point p:inflections.get(i))
								canvas.drawCircle(unitToPxX(p.getX()), unitToPxY(p.getY()), 5, paint);
					paint.setStyle(Style.STROKE);
					if (disDiscon){
						if (i<discontinuities.size()){
							for (Double d:discontinuities.get(i)){
								if (unitToPxX(d)>-5 && unitToPxX(d)<getWidth()+5){
									Path p = new Path();
									p.moveTo(unitToPxX(d), -30 + unitToPxY(0)%30);
									p.lineTo(unitToPxX(d), getHeight());
									float[] intervals = {20,10};
									paint.setStrokeWidth(4);
									paint.setPathEffect(new DashPathEffect(intervals, 0));
									canvas.drawPath(p, paint);
									paint.setStrokeWidth(2);
									paint.setPathEffect(null);	
								}
							}}}
					paint.setStyle(Style.STROKE);
				}
				*/
				canvas.drawPath(paths.get(i), paint);
			}
		}
		/*
		if (disIntersections){
			paint.setColor(COLOR_INTERSECTION);
			paint.setStyle(Style.FILL_AND_STROKE);
			for (Point p:intersections)
				canvas.drawCircle(unitToPxX(p.getX()), unitToPxY(p.getY()), 5, paint);
			paint.setStyle(Style.STROKE);
		}
		}*/
		//paint.setColor(COLOR_AXES);
		//paint.setStrokeWidth(2);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (oscl != null)
			oscl.onSizeChanged(w, h, oldw, oldh);
		// Clear paths and tell it to redraw
		synchronized (pathCollector) {
			pathCollector.clearPaths();
		}
		sh.redraw = true;
	}

	public interface OnSizeChangedListener{
		public void onSizeChanged(int w, int h, int oldw, int oldh);
	}
}
