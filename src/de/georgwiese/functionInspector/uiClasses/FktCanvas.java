package de.georgwiese.functionInspector.uiClasses;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;

import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.functionInspector.controller.PathCollector;
import de.georgwiese.functionInspector.controller.StateHolder;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Build;
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
	public static final int COLOR_BACKGROUND   = Color.BLACK;
	public static final int COLOR_AXES         = Color.WHITE;
	public static final int COLOR_LINES        = Color.parseColor("#222222");
	public static final int[] COLORS_GRAPHS    = {Color.RED, Color.GREEN, Color.CYAN};
	public static final int COLOR_ACTIVE_POINT = Color.YELLOW;
	public static final int COLOR_INTERSECTION = Color.GRAY;
	public static final int COLOR_TRACELINE    = Color.argb(100, 255, 255, 255);
	public static final int COLOR_BOX          = Color.argb(200, 0, 0, 0);
	
	static final float BOX_PADDING = 5;
	
	Context c;
	Paint paint;
	StateHolder sh;
	PathCollector pathCollector;
	double[] steps;
	protected DecimalFormat df1, df2;
	OnSizeChangedListener oscl;
	float borderBottom;

	@TargetApi(11)
	public FktCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		invalidate();
		c = context;
		paint = new Paint();
		steps = new double[2];
		steps[0] = 1;
		steps[1] = 1;
		df1 = new DecimalFormat("0.0##");
		df2 = new DecimalFormat("0.00");
		
		// Anything 40 dips from the bottom edge is obscured by the bar
		borderBottom = 40 * context.getResources().getDisplayMetrics().density;
		
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
			setLayerType(LAYER_TYPE_SOFTWARE, null);
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
		//if(sh.redraw){
			steps[0] = Helper.getSteps(sh.getZoom(0), sh.getFactor(0));
			steps[1] = Helper.getSteps(sh.getZoom(1), sh.getFactor(1));
		//}
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
				double factoredOut = i * steps[0] / sh.getFactor(0);
				String text = df1.format(factoredOut) + Helper.getFactorString(sh.getFactor(0));
				/*if (sh.getFactor(0) == Math.PI)
					text=df1.format(i*steps[0]/Math.PI)+"\u03C0";
				else if (sh.getFactor(0) == Math.PI/180)
					text=df1.format(i*steps[0]/Math.PI*180)+"\u00B0";
				else if (sh.getFactor(0) == Math.E)
					text=df1.format(i*steps[0]/Math.E)+"e";
				else
					text=df1.format(i*steps[0]);
				*/
				if (zero.y <= getHeight()-borderBottom-30 && zero.y>=0)
					canvas.drawText(text, x, (float)zero.y+20, paint);
				else if (zero.y > getHeight()-borderBottom-30)
					canvas.drawText(text, x, getHeight()-borderBottom-10, paint);
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
				double factoredOut = i * steps[1] / sh.getFactor(1);
				String text = df1.format(factoredOut) + Helper.getFactorString(sh.getFactor(1));
				/*if (sh.getFactor(1) == Math.PI)
					text=df1.format(i*steps[1]/Math.PI)+"\u03C0";
				else if (sh.getFactor(1) == Math.PI/180)
					text=df1.format(i*steps[1]/Math.PI*180)+"\u00B0";
				else if (sh.getFactor(1) == Math.E)
					text=df1.format(i*steps[1]/Math.E)+"e";
				else
					text=df1.format(i*steps[1]);*/
				
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
		
		synchronized(pathCollector){
			pathCollector.update();
			
			ArrayList<Path> paths = pathCollector.getPaths();
			ArrayList<ArrayList<Point>> roots = pathCollector.getRoots();
			ArrayList<ArrayList<Point>> extrema = pathCollector.getExtrema();
			ArrayList<ArrayList<Point>> inflections = pathCollector.getInflections();
			ArrayList<ArrayList<Double>> discontinuities = pathCollector.getDiscontinuities();
			ArrayList<Point> intersections = pathCollector.getIntersections();
			
			for (int i=0; i<paths.size();i++){
				paint.setColor(COLORS_GRAPHS[i%COLORS_GRAPHS.length]);
				
				paint.setStyle(Style.FILL_AND_STROKE);
				if (sh.disRoots)
					if (i<roots.size())
						for (Point p:roots.get(i))
							canvas.drawCircle((float)Helper.unitToPx(p.getX(), 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x,
									(float)Helper.unitToPx(0, p.getY(), sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y, 5, paint);
				if (sh.disExtrema)
					if (i<extrema.size())
						for (Point p:extrema.get(i))
							canvas.drawCircle((float)Helper.unitToPx(p.getX(), 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x,
									(float)Helper.unitToPx(0, p.getY(), sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y, 5, paint);
				if (sh.disInflections)
					if (i<inflections.size())
						for (Point p:inflections.get(i))
							canvas.drawCircle((float)Helper.unitToPx(p.getX(), 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x,
									(float)Helper.unitToPx(0, p.getY(), sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y, 5, paint);
				paint.setStyle(Style.STROKE);
				if (sh.disDiscon){
					if (i<discontinuities.size()){
						for (Double d:discontinuities.get(i)){
							float x = (float)Helper.unitToPx(d, 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x;
							float y = (float)Helper.unitToPx(0, 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y;
							Path p = new Path();
							p.moveTo(x, (y % 30) - 30);
							p.lineTo(x, getHeight());
							float[] intervals = {20,10};
							paint.setStrokeWidth(4);
							paint.setPathEffect(new DashPathEffect(intervals, 0));
							canvas.drawPath(p, paint);
							paint.setStrokeWidth(2);
							paint.setPathEffect(null);	
						}
					}
					paint.setStyle(Style.STROKE);
				}
				if (paths.get(i) != null)
					canvas.drawPath(paths.get(i), paint);
			}
			if (sh.disIntersections){
				paint.setColor(COLOR_INTERSECTION);
				paint.setStyle(Style.FILL_AND_STROKE);
				for (Point p:intersections)
					canvas.drawCircle((float)Helper.unitToPx(p.getX(), 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x,
							(float)Helper.unitToPx(0, p.getY(), sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y, 5, paint);
				paint.setStyle(Style.STROKE);
			}
			
			// Active Point
			Point activePoint = sh.getActivePoint();
			if (activePoint != null){
				paint.setColor(Color.YELLOW);
				paint.setStyle(Style.FILL_AND_STROKE);
				if(activePoint.getType() == Point.TYPE_DISCONTINUITY){
					float x = (float)Helper.unitToPx(activePoint.getX(), 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x;
					paint.setStrokeWidth(6);
					canvas.drawLine(x, 0, x, getHeight(), paint);
					
					// Draw value box
					String text = "x = " + df2.format(activePoint.getX() / sh.getFactor(0)) +
							Helper.getFactorString(sh.getFactor(0));
					paint.setColor(COLOR_BOX);
					paint.setTextSize(20);
					float width = paint.measureText(text);
					canvas.drawRect(x - width/2 - BOX_PADDING, getHeight() / 2 - 20, x + width/2 + BOX_PADDING, getHeight() / 2 + 20, paint);
					paint.setColor(Color.YELLOW);
					paint.setTextAlign(Align.LEFT);
					paint.setStrokeWidth(1);
					canvas.drawText(text, x - width/2, getHeight() / 2 + 10, paint);
				}
				else{
					float x = (float)Helper.unitToPx(activePoint.getX(), 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x;
					float y = (float)Helper.unitToPx(0, activePoint.getY(), sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y;
					canvas.drawCircle(x, y, 7, paint);
					
					// Draw value box
					String text = "( " + df2.format(activePoint.getX() / sh.getFactor(0)) +
							Helper.getFactorString(sh.getFactor(0)) + " | " +
							df2.format(activePoint.getY() / sh.getFactor(1)) +
							Helper.getFactorString(sh.getFactor(1)) + " )";
					paint.setColor(COLOR_BOX);
					paint.setTextSize(20);
					float width = paint.measureText(text);
					canvas.drawRect(x - width/2 - BOX_PADDING, y - 10, x + width/2 + BOX_PADDING, y - 50, paint);
					paint.setColor(Color.YELLOW);
					paint.setTextAlign(Align.LEFT);
					paint.setStrokeWidth(1);
					canvas.drawText(text, x - width/2, y - 20, paint);
				}
			}
			
			// Trace
			if (sh.getMode() == StateHolder.MODE_TRACE){
				float x = (float)Helper.unitToPx(sh.currentX, 0, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).x;
				paint.setStrokeWidth(2);
				paint.setColor(COLOR_TRACELINE);
				canvas.drawLine(x, 0, x, getHeight(), paint);

				ArrayList<String> texts   = new ArrayList<String>();
				ArrayList<String> textsS  = new ArrayList<String>();
				ArrayList<Integer> colors = new ArrayList<Integer>();
				
				for(int i = 0; i < sh.getFkts().size(); i++){
					Function f = sh.getFkts().get(i);
					if (f != null){
						f.setParams(sh.getParams());
						double yU = f.calculate(sh.currentX);
						float slope	= (float) f.slope(sh.currentX);
						float y = (float)Helper.unitToPx(0, yU, sh.getZoom(), sh.getMiddle(), getWidth(), getHeight()).y;
						
						texts.add("f" + (i + 1) + "(x) = " + df2.format(yU / sh.getFactor(1)) +
								Helper.getFactorString(sh.getFactor(1)));
						textsS.add("f'" + (i + 1) + "(x) = " + df2.format(slope));
						colors.add(COLORS_GRAPHS[i % COLORS_GRAPHS.length]);
						
						paint.setColor(COLORS_GRAPHS[i % COLORS_GRAPHS.length]);
						paint.setStyle(Style.FILL_AND_STROKE);
						if (!Double.isNaN(yU)){
							canvas.drawCircle(x, y, 5, paint);
							if (sh.disSlope){
								// Draw Tangents
								float zoomFactor = (float)(sh.getZoom(1) / sh.getZoom(0));
								paint.setStrokeWidth(1);
								if (!Float.isNaN(slope))
									canvas.drawLine(0, y + slope * x * zoomFactor,
											getWidth(), y - slope * (getWidth() - x) * zoomFactor, paint);
							}
						}
					}
				}

				// Draw value box
				paint.setColor(COLOR_BOX);
				paint.setTextSize(25);
				float width = 0;
				// calculate maximum width
				for (String text:texts)
					width = Math.max(width, paint.measureText(text));
				for (String text:textsS)
					width = Math.max(width, paint.measureText(text));
				x = Math.min(x, getWidth() - width - 3*BOX_PADDING);
				float height = BOX_PADDING + texts.size()*40;
				if (sh.disSlope)
					height += textsS.size() * 40 + 30;
				canvas.drawRect(x, BOX_PADDING, x + width + 2*BOX_PADDING, height, paint);
				paint.setTextAlign(Align.LEFT);
				paint.setStrokeWidth(0);
				for (int i = 0; i < texts.size(); i++){
					paint.setColor(colors.get(i));
					canvas.drawText(texts.get(i), x + BOX_PADDING, BOX_PADDING + 30 + i*40, paint);
					if(sh.disSlope)
						canvas.drawText(textsS.get(i), x + BOX_PADDING, BOX_PADDING + texts.size() * 40 + 30 + 20 + i*40, paint);
				}
			}
		}
		//paint.setColor(COLOR_AXES);
		//paint.setStrokeWidth(2);
	}
	
	public File getFile(String path, String name){
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
		Canvas bitmapCanvas = new Canvas();
		bitmapCanvas.setBitmap(bitmap);
		bitmapCanvas.drawColor(Color.BLACK);
		onDraw(bitmapCanvas);
		if (!sh.isPro){
			Bitmap watermark= BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
			bitmapCanvas.drawBitmap(watermark, (float)getWidth()/2-watermark.getWidth()/2, getHeight()-watermark.getHeight()-20, new Paint());
		}
		AlertDialog.Builder b = new AlertDialog.Builder(c);
		b.setPositiveButton(c.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();}});
		File fPath = new File(path);
		fPath.mkdir();
		File f = new File(path, name);
		try{
			if (f.exists()){
				b.setTitle(R.string.file_error_title);
				b.setMessage(R.string.file_error_message_exists);
				b.create().show();
				return null;
			}
			else{
				FileOutputStream fos = new FileOutputStream(new File(path, name));
				bitmap.compress(CompressFormat.JPEG,100,fos);
				fos.flush();
				fos.close();
				b.setTitle(R.string.file_saved_title);
				b.setMessage(R.string.file_saved_message);
				b.create().show();
				return f;
			}
			}catch(Exception e){
				Log.e("Developer", e.toString());
				b.setTitle(R.string.file_error_title);
				b.setMessage(R.string.file_error_message_unknown);
				b.create().show();
				return null;
			}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (oscl != null)
			oscl.onSizeChanged(w, h, oldw, oldh);
		resetPaths();
	}
	
	public void resetPaths(){
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
