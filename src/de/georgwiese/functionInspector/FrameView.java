package de.georgwiese.functionInspector;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ZoomControls;
import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.calculationFunktions.Point;
import de.georgwiese.functionInspector.SwitchButtonSet.OnStateChangedListener;
import de.georgwiese.functionInspectorLite.MainScreen;
import de.georgwiese.functionInspectorLite.R;
import de.georgwiese.functionInspectorLite.TableActivity;
import de.georgwiese.*;

public class FrameView extends FunctionView {
	public static final int VERSION_LITE=0;
	public static final int VERSION_PRO=1;
	private final boolean SHOW_TOUCH_POSITION=false;
	private ArrayList<Float> xs,ys;
	private int version;
	Context mContext;
	
	private ScaleGestureDetector mScaleDetector;
	private GestureDetector mGestDetector;
	private ZoomControls zc;
	private float xPrevSpan,yPrevSpan,xSpan,ySpan;
	private MyKeyboardView kv;
	LinearLayout ll_zoom;
	LinearLayout ll_Mbuttons;
	private double[] mMinParam= new double[3];;
	private double[] mMaxParam= new double[3];;
	
	//Menu
	private LinearLayout ll_menu, ll_bar;
	public static final int MENU_GRAPH=0;
	public static final int MENU_PARAM=1;
	public static final int MENU_POINTS=3;
	public static final int MENU_MODE=4;
	private MenuView menuGraph;
	private MenuView menuParam;
	private MenuView menuPoints;
	private MenuView menuMode;
	private ArrayList<EnterFunctionView> efv;
	public int menu;
	private ArrayList<String> savedFkts;
	ImageButton ratio;
	Button bt_g;
	
	
	
	//File
	File ss;
	
	//Points
	//Point  activePoint;
	//FunctionScrollView pointDisplay;
	
	public FrameView(Context context, int version, double[] sMinParam, double[] sMaxParam, double[] sParam, boolean[] choices, ArrayList<String> savedFkts){
		super(context);
		this.version=version;
		final boolean isPro=version==VERSION_PRO;
		performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
		mContext=context;
		kv=new MyKeyboardView(context, this);
		kv.setVisibility(GONE);
		
		for (int i=0; i<3; i++){
			mMinParam[i] = sMinParam[i];
			mMaxParam[i] = sMaxParam[i];
			mParameter[i] = sParam[i];
		}
		this.savedFkts=savedFkts;
		
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setBackgroundColor(Color.RED);
		setOrientation(VERTICAL);
		setGravity(Gravity.RIGHT);
		menu=-1;
		ll_bar = new LinearLayout(context){
			@Override
			protected void onSizeChanged(int w, int h, int oldw, int oldh) {
				super.onSizeChanged(w, h, oldw, oldh);
				setBorderTop(ll_bar.getHeight());
			}
		};
		ll_bar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		addView(ll_bar);
		
		
		//Menu
		ll_menu = new LinearLayout(context);
		ll_menu.setGravity(Gravity.RIGHT);
		//ll_menu.setGravity(Gravity.TOP);
		ll_menu.setOrientation(VERTICAL);
		
		menuGraph = new MenuView(context,(double)1/8, false);
		efv = new ArrayList<EnterFunctionView>();
		updateEfvs();
		menuGraph.setHeading(context.getString(R.string.fkt_enterFkt));
		
		menuParam = new MenuView(context,(double)3/8, false);
		menuParam.setHeading(context.getString(R.string.param_setParam));
		menuParam.addToBody((LinearLayout)((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mv_param, null));
		final SeekBar sb= (SeekBar)menuParam.findViewById(R.id.mv_param_sb);
		final SwitchButtonSet sbs = (SwitchButtonSet)menuParam.findViewById(R.id.mv_param_sbs);
		sbs.setCaptions(new String[]{"a","b","c"});
		final Button minParam = (Button) menuParam.findViewById(R.id.mv_param_btMin);
		final Button parameter= (Button) menuParam.findViewById(R.id.mv_param_btParam);
		final Button maxParam = (Button) menuParam.findViewById(R.id.mv_param_btMax);
		final Dialog setMinParam = new Dialog(context);
		setMinParam.setContentView(R.layout.enter_number_dialog);
		final EditText setMinParam_et= (EditText)setMinParam.findViewById(R.id.param_et);
		Button setMinParam_bt= (Button)setMinParam.findViewById(R.id.param_bt);
		setMinParam.setTitle(R.string.param_setMinParam);
		setMinParam_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String s= setMinParam_et.getText().toString();
				double value;
				if (CalcFkts.check(s)){
					value=CalcFkts.calculate(s);
					int p = sbs.getState();
					if (value<=mMaxParam[p]){
						String p_str = (sbs.getState()==0?" a":(sbs.getState()==1?" b":" c"))+": \n";
						mMinParam[p]=CalcFkts.calculate(s);
						minParam.setText(mContext.getResources().getString(R.string.param_min)+p_str+df2.format(mMinParam[p]));
						sb.setProgress((int)Math.round((mParameter[p]-mMinParam[p])/(mMaxParam[p]-mMinParam[p])*sb.getMax()));
						setMinParam.cancel();
					}
				}}});
		final Dialog setParam = new Dialog(context);
		setParam.setContentView(R.layout.enter_number_dialog);
		final EditText setParam_et= (EditText)setParam.findViewById(R.id.param_et);
		Button setParam_bt= (Button)setParam.findViewById(R.id.param_bt);
		setParam.setTitle(R.string.param_setParam);
		setParam_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String s= setParam_et.getText().toString();
				double value;
				if (CalcFkts.check(s)){
					value=CalcFkts.calculate(s);
					int p = sbs.getState();
					String p_str = (sbs.getState()==0?" a":(sbs.getState()==1?" b":" c"))+": \n";
					sb.setProgress((int)Math.round((value-mMinParam[p])/(mMaxParam[p]-mMinParam[p])*sb.getMax()));
					mParameter[p]=value;
					parameter.setText(p_str+df2.format(mParameter[p]));
					setParam.cancel();
					redraw=true;
					invalidate();
				}}});
		final Dialog setMaxParam = new Dialog(context);
		setMaxParam.setContentView(R.layout.enter_number_dialog);
		final EditText setMaxParam_et= (EditText)setMaxParam.findViewById(R.id.param_et);
		Button setMaxParam_bt= (Button)setMaxParam.findViewById(R.id.param_bt);
		setMaxParam.setTitle(R.string.param_setMaxParam);
		setMaxParam_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String s= setMaxParam_et.getText().toString();
				double value;
				if (CalcFkts.check(s)){
					value=CalcFkts.calculate(s);
					int p = sbs.getState();
					if (value >=mMinParam[p]){
						mMaxParam[p]=CalcFkts.calculate(s);
						String p_str = (sbs.getState()==0?" a":(sbs.getState()==1?" b":" c"))+": \n";
						maxParam.setText(mContext.getResources().getString(R.string.param_max)+p_str+df2.format(mMaxParam[p]));
						sb.setProgress((int)Math.round((mParameter[p]-mMinParam[p])/(mMaxParam[p]-mMinParam[p])*sb.getMax()));
						setMaxParam.cancel();
					}
				}}});
		minParam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMinParam.show();}});
		parameter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setParam.show();}});
		maxParam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMaxParam.show();}});
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int qualityBefore;
			boolean touched=false;
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int p = sbs.getState();
				if (touched)
					mParameter[p]=progress/100.0*(mMaxParam[p]-mMinParam[p])+mMinParam[p];
				String p_str = (sbs.getState()==0?" a":(sbs.getState()==1?" b":" c"))+": \n";
				parameter.setText(p_str+df2.format(mParameter[p]));
				redraw=true;
				invalidate();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				touched=true;
				qualityBefore=quality;
				quality=QUALITY_PREVIEW;
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				touched=false;
				quality=qualityBefore;
				redraw=true;
				invalidate();
			}
		});
		sbs.setOnStateChangedListener(new OnStateChangedListener() {
			@Override
			public void onStateChanged(int newState) {
				sb.setProgress((int)Math.round((mParameter[newState]-mMinParam[newState])/(mMaxParam[newState]-mMinParam[newState])*sb.getMax()));
				String p = (newState==0?" a":(newState==1?" b":" c"))+": \n";
				minParam.setText(mContext.getResources().getString(R.string.param_min)+p+df2.format((double)mMinParam[newState]));
				maxParam.setText(mContext.getResources().getString(R.string.param_max)+p+df2.format((double)mMaxParam[newState]));
				parameter.setText(p+df2.format(mParameter[newState]));
				boolean enabled = isPro | newState==0;
				sb.setEnabled(enabled);
				minParam.setEnabled(enabled);
				maxParam.setEnabled(enabled);
				parameter.setEnabled(enabled);
			}
		});
		minParam.setText(mContext.getResources().getString(R.string.param_min)+" a:\n"+df2.format((double)mMinParam[0]));
		maxParam.setText(mContext.getResources().getString(R.string.param_max)+" a:\n"+df2.format((double)mMaxParam[0]));
		parameter.setText(" a:\n"+df2.format((double)mParameter[0]));
		sb.setProgress((int)Math.round((mParameter[0]-mMinParam[0])/(mMaxParam[0]-mMinParam[0])*sb.getMax()));
		
		menuPoints = new MenuView(context, (double)5/8, false);
		menuPoints.setHeading(mContext.getString(R.string.points_specialPoints));
		menuPoints.addToBody((LinearLayout)((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mv_points, null));
		final CheckBox[] cb = new CheckBox[5];
		cb[0]=(CheckBox)menuPoints.findViewById(R.id.mv_points_roots);
		cb[1]=(CheckBox)menuPoints.findViewById(R.id.mv_points_extrema);
		cb[2]=(CheckBox)menuPoints.findViewById(R.id.mv_points_inflections);
		cb[3]=(CheckBox)menuPoints.findViewById(R.id.mv_points_intersections);
		cb[4]=(CheckBox)menuPoints.findViewById(R.id.mv_points_discontinuities);
		Button pro_button = (Button)menuPoints.findViewById(R.id.mv_points_btPro);
		for (int i=0;i<5;i++){
			final int i2=i;
			cb[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					activePoint=null;
					if (pointDisplay!=null)
						pointDisplay.setVisibility(GONE);
					if (i2==0) disRoots=isChecked;
					if (i2==1) disExtrema=isChecked;
					if (i2==2) disInflections=isChecked;
					if (i2==3) disIntersections=isChecked;
					if (i2==4) disDiscon=isChecked;
					redraw=true;
					invalidate();
				}
			});
		}
		pro_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainScreen) mContext).showDialog(MainScreen.PRO_DIALOG);}});
		if (!isPro){
			cb[1].setEnabled(false);
			cb[2].setEnabled(false);
			cb[3].setEnabled(false);
			cb[4].setEnabled(false);
		}
		else
			pro_button.setVisibility(GONE);
		for (int i=0; i<5; i++)
			if (choices[i])
				cb[i].performClick();

		
		menuMode = new MenuView(context, (double)1/2, true);
		menuMode.setHeading(getResources().getString(R.string.mode_title));
		menuMode.addToBody((LinearLayout)((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mv_mode, null));
		final Dialog info = new Dialog(mContext);
		info.setContentView(R.layout.mode_info_layout);
		info.setTitle(R.string.mode_info_title);
		Button ok = (Button) info.findViewById(R.id.bt_mode_info_ok);
		final CheckBox checkbox = (CheckBox) info.findViewById(R.id.mode_info_cb);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Developer", "onClick");
				SharedPreferences.Editor e = mContext.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
				e.putBoolean("showModeInfo", !checkbox.isChecked());
				e.commit();
				info.cancel();
			}
		});
		Button[] bts = new Button[3];
		for (int i=0; i<3; i++){
			switch (i){
			case 0:
				bts[i] = (Button) menuMode.findViewById(R.id.mv_mode_pan);
				bts[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mode=MODE_PAN;
						pointDisplay.setVisibility(GONE);
						currentX=null;
						activePoint=null;
						ll_Mbuttons.setVisibility(GONE);
						((MainScreen)mContext).resetButtons();
						((MainScreen)mContext).setMode(MODE_PAN);
					}
				});
				break;
			case 1:
				bts[i] = (Button) menuMode.findViewById(R.id.mv_mode_trace);
				bts[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mode=MODE_TRACE;
						bt_g.setVisibility(GONE);
						pointDisplay.setVisibility(GONE);
						currentX=null;
						activePoint=null;
						ll_Mbuttons.setVisibility(VISIBLE);
						if (mContext.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("showModeInfo", true))
							info.show();
						//info.cancel();
						((MainScreen)mContext).resetButtons();
						((MainScreen)mContext).setMode(MODE_TRACE);
					}
				});
				break;
			case 2:
				bts[i] = (Button) menuMode.findViewById(R.id.mv_mode_slope);
				if (!isPro)
					bts[i].setEnabled(false);
				bts[i].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mode=MODE_SLOPE;
						bt_g.setVisibility(VISIBLE);
						pointDisplay.setVisibility(GONE);
						currentX=null;
						activePoint=null;
						ll_Mbuttons.setVisibility(VISIBLE);
						if (mContext.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("showModeInfo", true))
							info.show();
						((MainScreen)mContext).resetButtons();
						((MainScreen)mContext).setMode(MODE_SLOPE);
					}
				});
				break;
			}
		}
		
		
		//Zoom
		ll_Mbuttons = new LinearLayout(mContext);
		ll_Mbuttons.setOrientation(LinearLayout.HORIZONTAL);
		ll_Mbuttons.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		final Button bt_setX = new Button(mContext);
		bt_setX.setText(R.string.mode_setx);
		bt_setX.setLayoutParams(lp);
		final Button bt_getTable = new Button(mContext);
		bt_getTable.setText(R.string.mode_gettable);
		bt_getTable.setLayoutParams(lp);
		bt_g = new Button(mContext);
		bt_g.setText("g(x)");
		bt_g.setLayoutParams(lp);
		bt_g.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentX!=null){
					String result = new String();
					for (Function f:fkts){
						if(f!=null){
							double n = f.calculate(currentX)-f.slope(currentX)*currentX;
							String between = n>=0?" + ":" - ";
							if (Double.isNaN(f.slope(currentX)) || Double.isNaN(n))
								result+="g"+Integer.toString(fkts.indexOf(f)+1)+"(x) = "+
										"/"+"\n";					
							else
								result+="g"+Integer.toString(fkts.indexOf(f)+1)+"(x) = "+
										df1.format(f.slope(currentX))+"x"+
										between+df1.format(Math.abs(n))+"\n";
						}
					}
					AlertDialog.Builder b = new AlertDialog.Builder(mContext);
					b.setTitle(R.string.mode_slope_eq_title);
					b.setMessage(mContext.getString(R.string.mode_slope_eq_message) + 
							df1.format(currentX)+":\n\n"+result);
					b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();}
					});
					b.create().show();
				}
			}
		});
		ll_Mbuttons.addView(bt_setX);
		ll_Mbuttons.addView(bt_getTable);
		ll_Mbuttons.addView(bt_g);
		ll_Mbuttons.setVisibility(GONE);
		
		final Dialog setX = new Dialog(context);
		setX.setContentView(R.layout.enter_number_dialog);
		final EditText setX_et= (EditText)setX.findViewById(R.id.param_et);
		Button setX_bt= (Button)setX.findViewById(R.id.param_bt);
		setX.setTitle(R.string.mode_setx_title);
		setX_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String s= setX_et.getText().toString();
				if (CalcFkts.check(s)){
					currentX=CalcFkts.calculate(s);
					pointDisplay.setVisibility(VISIBLE);
					middleX=currentX;
					redraw=true;
					invalidate();
					setX.cancel();
				}}});
		bt_setX.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setX.show();}});
		bt_getTable.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, TableActivity.class);
				i.putExtra("bool_slope", mode==MODE_SLOPE);
				for (int j = 0 ; j<=fkts.size(); j++){
					if (j==fkts.size())
						i.putExtra("fkt"+Integer.toString(j), "end");
					else
						i.putExtra("fkt"+Integer.toString(j), fkts.size()>0 && fkts.get(j)!=null?fkts.get(j).getString():"empty");
				}
				i.putExtra("paramA", mParameter[0]);
				i.putExtra("paramB", mParameter[1]);
				i.putExtra("paramC", mParameter[2]);
				mContext.startActivity(i);
			}});
		
		zc=new ZoomControls(context);
		zc.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoomToX=middleX;
				zoomToY=middleY;
				zoomIn();
			}});
		zc.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zoomToX=middleX;
				zoomToY=middleY;
				zoomOut();
			}});
		ImageButton center = new ImageButton(context);
		center.setImageResource(R.drawable.center);
		center.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				middleX=0.0;
				middleY=0.0;
				velocityX=0.0;
				velocityY=0.0;
				velStoreX=0.0;
				velStoreX=0.0;
				redraw=true;
				invalidate();
			}
		});
		ratio = new ImageButton(context);
		ratio.setImageResource(R.drawable.ratio);
		ratio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setOneToOneZoomRatio();
			}
		});
		
		ll_menu.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 0, 1));
		addView(ll_menu);
		
		pointDisplay= new FunctionScrollView(mContext, 100);
		pointDisplay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mode==MODE_SLOPE | mode==MODE_TRACE){
					currentX=null;
					pointDisplay.setVisibility(GONE);
					invalidate();}
			}});
		pointDisplay.setVisibility(GONE);
		
		View v = new View(context);
		v.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
		ll_zoom = new LinearLayout(context);
		ll_zoom.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		ll_zoom.addView(center);
		ll_zoom.addView(ratio);
		ll_zoom.addView(v);
		ll_zoom.addView(zc);
		
		addView(ll_Mbuttons);
		addView(ll_zoom);
		addView(kv);
		ll_menu.addView(menuGraph);
		ll_menu.addView(menuParam);
		ll_menu.addView(menuPoints);
		ll_menu.addView(menuMode);
		ll_menu.addView(pointDisplay);
		menuGraph.setVisibility(GONE);
		menuParam.setVisibility(GONE);
		menuPoints.setVisibility(GONE);
		menuMode.setVisibility(GONE);
		
		mGestDetector= new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public void onLongPress(MotionEvent e) {
				if (mode==MODE_PAN){
					final SharedPreferences prefs = mContext.getSharedPreferences("prefs", Activity.MODE_PRIVATE);
					final Dialog d = new Dialog(mContext);
					d.setTitle(R.string.prefs_factor_title);
					LinearLayout ll = new LinearLayout(mContext);
					ll.setOrientation(LinearLayout.VERTICAL);
					ll.setPadding(20, 0, 20, 20);
					LinearLayout ll2 = new LinearLayout(mContext);
					ll2.setOrientation(LinearLayout.VERTICAL);
					TextView x = new TextView(mContext);
					x.setText(R.string.prefs_factor_x);
					x.setGravity(Gravity.CENTER);
					TextView y = new TextView(mContext);
					y.setText(R.string.prefs_factor_y);
					y.setGravity(Gravity.CENTER);
					SwitchButtonSet sbx = new SwitchButtonSet(mContext, null, 4);
					sbx.setCaptions(new String[]{"1","PI","e", "DEG"});
					sbx.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					sbx.setState(prefs.getInt("prefs_factor_x", 0));
					sbx.setOnStateChangedListener(new SwitchButtonSet.OnStateChangedListener() {
						@Override
						public void onStateChanged(int newState) {
							SharedPreferences.Editor editor = prefs.edit();
							editor.putInt("prefs_factor_x", newState);
							factorX=newState;
							editor.commit();
						}
					});
					SwitchButtonSet sby = new SwitchButtonSet(mContext, null, 4);
					sby.setState(prefs.getInt("prefs_factor_y", 0));
					sby.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					sby.setOnStateChangedListener(new SwitchButtonSet.OnStateChangedListener() {
						@Override
						public void onStateChanged(int newState) {
							SharedPreferences.Editor editor = prefs.edit();
							editor.putInt("prefs_factor_y", newState);
							factorY=newState;
							editor.commit();
						}
					});
					sby.setCaptions(new String[]{"1","PI", "e", "DEG"});
					Button ok = new Button(mContext);
					ok.setText(R.string.ok);
					ok.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							d.cancel();
						}
					});
					//ll.addView(description);
					ll.addView(x);
					ll.addView(sbx);
					ll.addView(y);
					ll.addView(sby);
					ll2.addView(ll);
					ll2.addView(ok);
					View v = new View(mContext);
					v.setLayoutParams(new LayoutParams(1, 1));
					//ll2.addView(v); //doesn't work otherwise for some reason...
					d.setContentView(ll2);
					d.show();
				}
				super.onLongPress(e);
			}
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				zoomToX=pxToUnitX(e.getX());
				zoomToY=pxToUnitY(e.getY());
				zoomIn();
				return true;
			}
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				activePoint=null;
				if (disDiscon)
					for (int i=0; i<discontinuities.size(); i++)
						for (Double d:discontinuities.get(i))
							if (Math.abs(e.getX()-unitToPxX(d))<25)
								activePoint=new Point(d, 0, Point.TYPE_DISCONTINUITY);
				if (disRoots)
					for (int i=0; i<roots.size(); i++)
						for (Point p:roots.get(i))
							if (distance(p,e.getX(),e.getY())<25)
								activePoint=p;
				if (disExtrema)
					for (int i=0; i<extrema.size(); i++)
						for (Point p:extrema.get(i))
							if (distance(p,e.getX(),e.getY())<25)
								activePoint=p;
				if (disInflections)
					for (int i=0; i<inflections.size(); i++)
						for (Point p:inflections.get(i))
							if (distance(p,e.getX(),e.getY())<25)
								activePoint=p;
				if (disIntersections)
					for (Point p:intersections)
						if (distance(p,e.getX(),e.getY())<25)
							activePoint=p;
				if (activePoint!=null)
					pointDisplay.setVisibility(VISIBLE);
				else if (mode==MODE_PAN)
					pointDisplay.setVisibility(GONE);
				invalidate();
				return super.onSingleTapConfirmed(e);
			}
		});
		
		mScaleDetector=new ScaleGestureDetector(context, new OnScaleGestureListener() {
			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				redraw=true;
				timeLastZoomStop=AnimationUtils.currentAnimationTimeMillis();
				bZoom=false;
				xPrevSpan=0;
				yPrevSpan=0;
			}
			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				lastZoomX=zoomFactorX;
				totalZoomX=zoomFactorX;
				lastZoomY=zoomFactorY;
				totalZoomY=zoomFactorY;
				oldZoomX=zoomFactorX;
				oldZoomY=zoomFactorY;
				lastMiddleX=middleX;
				lastMiddleY=middleY;
				//zoomToX=pxToUnitX(detector.getFocusX());
				//zoomToY=pxToUnitY(detector.getFocusY());
				zoomToX=middleX;
				zoomToY=middleY;
				bZoom=true;
				return true;
			}
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				if (bZoomXY){
					if (xPrevSpan!=0 & xSpan!=0)
						zoomFactorX=oldZoomX*xSpan/xPrevSpan*detector.getScaleFactor();
					if (yPrevSpan!=0 & ySpan!=0)
						zoomFactorY=oldZoomY*ySpan/yPrevSpan*detector.getScaleFactor();
				}
				else{
					zoomFactorX*=detector.getScaleFactor();
					zoomFactorY*=detector.getScaleFactor();
				}
				invalidate();
				return true;
			}
		});
	}
	public void updateEfvs(){
		if (efv.size()==0)
			efv.add(new EnterFunctionView(mContext, this,kv));
		for (int i=0; i<efv.size(); i++){
			if (efv.get(i).getEt().getText().toString().equals("")
					&& !efv.get(i).getEt().hasFocus()
					&& i!=efv.size()-1){
				efv.remove(i);
				i--;
			}
		}
		int count=0;
		for (EnterFunctionView e:efv)
			if (e.getEt().getText().toString().equals(""))
				count++;
		if (count==0 && (version==VERSION_PRO | efv.size()<3))
			efv.add(new EnterFunctionView(mContext, this, kv));
		for (int i=0; i<efv.size(); i++){
			efv.get(i).setNr(i+1);
			efv.get(i).setColor(COLORS_GRAPHS[i%COLORS_GRAPHS.length]);
		}
		menuGraph.removeAllViews();
		for (EnterFunctionView e:efv)
			menuGraph.addToBody(e);
		if (efv.size()==3 && version==VERSION_LITE){
			Button b = new Button(mContext);
			b.setText(R.string.fkt_buyPro);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((MainScreen)mContext).showDialog(MainScreen.PRO_DIALOG);
				}
			});
			menuGraph.addToBody(b);
		}
	}
	public void focusNextEfv(){
		for (int i=0; i<efv.size(); i++){
			if (efv.get(i).getEt().hasFocus() && i<efv.size()-1){
				efv.get(i+1).getEt().requestFocus();
				break;}}
	}
	public void updateFkts(){
		fkts.clear();
		for (EnterFunctionView e:efv){
			String s = CalcFkts.formatFktString(e.getEt().getText().toString());
			if (CalcFkts.check(s))
				fkts.add(new Function(s));
			else
				fkts.add(null);
		}

		redraw=true;
		invalidate();
	}
	@Override
	public void setColorSchema(int schema) {
		super.setColorSchema(schema);
		pointDisplay.setHeadingColor(schema==COLORSCHEMA_DARK?Color.WHITE:Color.BLACK);
		updateEfvs();
	}
	public void setZoomXY(boolean b){
		bZoomXY=b;
		if (b)
			ratio.setVisibility(VISIBLE);
		else
			ratio.setVisibility(GONE);
	}
	public void setOneToOneZoomRatio(){
		double zoomT=(zoomFactorX+zoomFactorY)/2;
		zoomFactorX=zoomT;
		zoomFactorY=zoomT;
		redraw=true;
		invalidate();
	}
	private double distance(Point p, float x, float y){
		return Math.sqrt(Math.pow(x-unitToPxX(p.getX()), 2)+Math.pow(y-unitToPxY(p.getY()), 2));
	}
	private double distance(float x1, float y1, float x2, float y2){
		return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
	}
	public void addMenuButton(View b){
		b.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
		ll_bar.addView(b);
	}
	public void addMenuButton(View b, boolean special){
		if (special)
			b.setLayoutParams(new LayoutParams(60, LayoutParams.WRAP_CONTENT));
		else
			b.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
		ll_bar.addView(b);
	}
	public int getCurrentMenu(){
		return menu;
	}
	public void showMenu (int menu_id){
		hideAllMenus();
		menu=menu_id;
		Animation in = AnimationUtils.loadAnimation(mContext, R.anim.menu_in);
		switch (menu_id){
		case MENU_GRAPH:
			pointDisplay.setVisibility(GONE);
			menuGraph.updateArrow(getWidth());
			kv.setVisibility(VISIBLE);
			ll_zoom.setVisibility(GONE);
			if (mode!=MODE_PAN)
				ll_Mbuttons.setVisibility(GONE);
			menuGraph.setVisibility(VISIBLE);
			menuGraph.startAnimation(in);
			boolean focus=false;
			for (int i=0; i<efv.size(); i++){
				if (!focus & efv.get(i).getEt().getText().toString().equals("")){
					efv.get(i).getEt().requestFocus();
					focus=true;}}
			if (!focus) efv.get(0).getEt().requestFocus();
			break;
		case MENU_PARAM:
			if (mode==MODE_PAN)
				pointDisplay.setVisibility(GONE);
			menuParam.updateArrow(getWidth());
			menuParam.setVisibility(VISIBLE);
			menuParam.startAnimation(in);
			break;
		case MENU_POINTS:
			pointDisplay.setVisibility(GONE);
			menuPoints.updateArrow(getWidth());
			menuPoints.setVisibility(VISIBLE);
			menuPoints.startAnimation(in);
			break;
		case MENU_MODE:
			pointDisplay.setVisibility(GONE);
			menuMode.updateArrow(getWidth());
			menuMode.setVisibility(VISIBLE);
			menuMode.startAnimation(in);
			break;
		}
	}
	public void hideAllMenus (){
		if (menu!=-1)
			hideMenu(menu);
		menu=-1;
	}
	public void hideMenu (int menu){
		Animation out = AnimationUtils.loadAnimation(mContext, R.anim.menu_out);
		switch (menu){
		case (MENU_GRAPH):
			if (activePoint!=null | currentX!=null)
				pointDisplay.setVisibility(VISIBLE);
			kv.setVisibility(GONE);
			ll_zoom.setVisibility(VISIBLE);
			if (mode!=MODE_PAN)
				ll_Mbuttons.setVisibility(VISIBLE);
			menuGraph.startAnimation(out);
			menuGraph.setVisibility(GONE);
			break;
		case (MENU_PARAM):
			if (activePoint!=null | currentX!=null)
				pointDisplay.setVisibility(VISIBLE);
			menuParam.startAnimation(out);
			menuParam.setVisibility(GONE);
			break;
		case (MENU_POINTS):
			if (activePoint!=null | currentX!=null)
				pointDisplay.setVisibility(VISIBLE);
			menuPoints.startAnimation(out);
			menuPoints.setVisibility(GONE);
			break;
		case (MENU_MODE):
			if (activePoint!=null | currentX!=null)
				pointDisplay.setVisibility(VISIBLE);
			menuMode.startAnimation(out);
			menuMode.setVisibility(GONE);
			break;
		}
	}
	public void setKBVisibe(boolean v){
		kv.setVisibility(v?VISIBLE:GONE);
	}
	public boolean isKBVisible(){
		return kv.getVisibility()==VISIBLE;
	}
	public double[] getParams(){
		return mParameter;
	}
	public void setParams(double[] param){
		mParameter=param;
	}
	public double[] getMinParams(){
		return mMinParam;
	}
	public void setMinParams(double[] minparam){
		mMinParam=minparam;
	}
	public double[] getMaxParams(){
		return mMaxParam;
	}
	public void setMaxParams(double[] maxparam){
		mMaxParam=maxparam;
	}
	public boolean[] getPointsChoices(){
		boolean[] result = {disRoots, disExtrema, disInflections, disIntersections, disDiscon};
		return result;
	}
	public ArrayList<Function> getFkts(){
		return fkts;
	}
	public String[] getSavedFktsArray(){
		String[] result = new String[savedFkts.size()];
		for (int i=0;i<savedFkts.size();i++)
			result[i]=savedFkts.get(i);
		return result;
	}
	public void addSavedFkt(String fkt){
		if (savedFkts.size()>=100)
			savedFkts.remove(0);
		savedFkts.add(fkt);
	}
	public void deleteSavedFkt(int index){
		if (index<savedFkts.size())
			savedFkts.remove(index);
	}
	public void setFkts(ArrayList<Function> fkts){
		this.fkts=fkts;
		efv.clear();
		for (int i=0; i<fkts.size(); i++)
			if (fkts.get(i)!=null)
				efv.add(new EnterFunctionView(mContext, this, kv, fkts.get(i).getString()));
		updateEfvs();
	}
	@Override
	public void addFkt(String f) {
		super.addFkt(f);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (SHOW_TOUCH_POSITION){
			xs = new ArrayList<Float>();
			ys = new ArrayList<Float>();
			for (int i=0; i<event.getPointerCount();i++){
				xs.add(event.getX(i));
				ys.add(event.getY(i));
			}
		}
		if (mode==MODE_PAN){
			if (event.getPointerCount()<2){
				xPrevSpan=0;
				yPrevSpan=0;
				xSpan=0;
				xSpan=0;
			}
			else if (event.getPointerCount()==2){
				if (xPrevSpan==0){
					xPrevSpan=Math.abs(event.getX(1)-event.getX(0));
					if (xPrevSpan<20) xPrevSpan=0;
				}
				if (yPrevSpan==0){
					yPrevSpan=Math.abs(event.getY(1)-event.getY(0));
					if (yPrevSpan<20) yPrevSpan=0;
				}
				xSpan=Math.abs(event.getX(1)-event.getX(0));
				if (xSpan<20) xSpan=0;
				ySpan=Math.abs(event.getY(1)-event.getY(0));
				if (ySpan<20) ySpan=0;
				mScaleDetector.onTouchEvent(event);
			}
		}
		mGestDetector.onTouchEvent(event);
		if (!init){
			init=true;
			dynamics.run();
		}
		if(mScaleDetector.isInProgress())
			bZoom=true;
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			mLastTouchX=event.getX();
			mLastTouchY=event.getY();
			mFirstTouchX=mLastTouchX;
			mFirstTouchY=mLastTouchY;
			velStoreX=0.0;
			velStoreY=0.0;
			velocityX=0.0;
			velocityY=0.0;
			mLastTime=AnimationUtils.currentAnimationTimeMillis();
			if (mode==MODE_PAN){
				mLastTime=AnimationUtils.currentAnimationTimeMillis();
				if (disDiscon)
					for (int i=0; i<discontinuities.size(); i++)
						for (Double d:discontinuities.get(i))
							if (Math.abs(event.getX()-unitToPxX(d))<25)
								activePoint=new Point(d, 0, Point.TYPE_DISCONTINUITY);
				if (disRoots)
					for (int i=0; i<roots.size(); i++)
						for (Point p:roots.get(i))
							if (distance(p,event.getX(),event.getY())<25)
								activePoint=p;
				if (disExtrema)
					for (int i=0; i<extrema.size(); i++)
						for (Point p:extrema.get(i))
							if (distance(p,event.getX(),event.getY())<25)
								activePoint=p;
				if (disInflections)
					for (int i=0; i<inflections.size(); i++)
						for (Point p:inflections.get(i))
							if (distance(p,event.getX(),event.getY())<25)
								activePoint=p;
				if (disIntersections)
					for (Point p:intersections)
						if (distance(p,event.getX(),event.getY())<25)
							activePoint=p;
				if (activePoint!=null)
					pointDisplay.setVisibility(VISIBLE);
				//else
					//pointDisplay.setVisibility(GONE);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if ((mode==MODE_PAN) != (event.getPointerCount()==2)){
				if(AnimationUtils.currentAnimationTimeMillis()-timeLastZoomStop>500){
					middleX-=getDeltaUnit(event.getX(0)-mLastTouchX,zoomFactorX);
					middleY+=getDeltaUnit(event.getY(0)-mLastTouchY,zoomFactorY);
					if (distance(mFirstTouchX, mFirstTouchY, event.getX(0), event.getY(0))>30 && AnimationUtils.currentAnimationTimeMillis()-mLastTime>0){
						velStoreX=(event.getX(0)-mLastTouchX)/(AnimationUtils.currentAnimationTimeMillis()-mLastTime)*50;
						velStoreY=(-event.getY(0)+mLastTouchY)/(AnimationUtils.currentAnimationTimeMillis()-mLastTime)*50;
					}
				}
			}
			else if (mode!=MODE_PAN){
				currentX=pxToUnitX(event.getX());
				pointDisplay.setVisibility(VISIBLE);
			}
			mLastTouchX=event.getX();
			mLastTouchY=event.getY();
			mLastTime=AnimationUtils.currentAnimationTimeMillis();
			if (pxToUnitX(0)<minX | pxToUnitX(getWidth())>maxX)
				redraw=true;
			break;
		case MotionEvent.ACTION_UP:
			xs=null;
			velocityX=velStoreX;
			velocityY=velStoreY;
			doDyn=true;
			if (pxToUnitX(0)<minX | pxToUnitX(getWidth())>maxX)
				redraw=true;
		}
		invalidate();
		return true;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if (redraw){
			activePoint=null;
			if (currentX==null)
				pointDisplay.setVisibility(GONE);
		}
		super.onDraw(canvas);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		DecimalFormat df = new DecimalFormat("#.####");
		if (activePoint!=null && mode==MODE_PAN){
			paint.setColor(COLOR_ACTIVE_POINT);
			if (activePoint.getType()==Point.TYPE_DISCONTINUITY){
				paint.setStrokeWidth(4);
				canvas.drawLine(unitToPxX(activePoint.getX()), 0, unitToPxX(activePoint.getX()), getHeight(), paint);
				paint.setStrokeWidth(2);
				paint.setPathEffect(null);
			}
			else{
				paint.setStrokeWidth(1);
				canvas.drawCircle(unitToPxX(activePoint.getX()), unitToPxY(activePoint.getY()), 7, paint);
			}
			if (colorSchema==COLORSCHEMA_DARK)
				pointDisplay.setBackgroundColor(Color.argb(200, 0, 0, 0));
			else if (colorSchema==COLORSCHEMA_LIGHT)
				pointDisplay.setBackgroundColor(Color.argb(200, 255, 255, 255));
			String sX;
			String sY;

			if (factorX==FACTOR_ONE | df.format(Math.abs(activePoint.getX())).equals("0"))
				sX = df.format(activePoint.getX()).equals("-0")?"0":df.format(activePoint.getX());
			else if (factorX==FACTOR_PI)
				sX = df.format(activePoint.getX()).equals("-0")?"0":df.format(activePoint.getX()/Math.PI)+"\u03C0";
			else if (factorX==FACTOR_E)
				sX = df.format(activePoint.getX()).equals("-0")?"0":df.format(activePoint.getX()/Math.E)+"e";
			else// if (factorX==FACTOR_DEG)
				sX = df.format(activePoint.getX()).equals("-0")?"0":df.format(activePoint.getX()/Math.PI*180)+"\u00B0";
			if (factorY==FACTOR_ONE | df.format(Math.abs(activePoint.getY())).equals("0"))
				sY = df.format(activePoint.getY()).equals("-0")?"0":df.format(activePoint.getY());
			else if (factorY==FACTOR_PI)
				sY = df.format(activePoint.getY()).equals("-0")?"0":df.format(activePoint.getY()/Math.PI)+"\u03C0";
			else if (factorY==FACTOR_E)
				sY = df.format(activePoint.getY()).equals("-0")?"0":df.format(activePoint.getY()/Math.E)+"e";
			else// if (factorY==FACTOR_DEG)
				sY = df.format(activePoint.getY()).equals("-0")?"0":df.format(activePoint.getY()/Math.PI*180)+"\u00B0";
			pointDisplay.clear();
			pointDisplay.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			pointDisplay.setEntryTextSize(25);
			switch (activePoint.getType()){
			case Point.TYPE_ROOT:
				pointDisplay.setHeading(getResources().getString(R.string.points_roots_label));break;
			case Point.TYPE_EXTREMA:
				pointDisplay.setHeading(getResources().getString(R.string.points_extrema_label));break;
			case Point.TYPE_INFLECTION:
				pointDisplay.setHeading(getResources().getString(R.string.points_inflection_label));break;
			case Point.TYPE_FUNCTION_INTERSECTION:
				pointDisplay.setHeading(getResources().getString(R.string.points_intersections_label));break;
			case Point.TYPE_DISCONTINUITY:
				pointDisplay.setHeading(getResources().getString(R.string.points_discontinuities_label));break;
			case Point.TYPE_NONE:
				pointDisplay.setHeading("<Error 1>");break;
			default:
				pointDisplay.setHeading("<Error 1>");break;
			}
			if (activePoint.getType()==Point.TYPE_DISCONTINUITY)
				pointDisplay.addEntry("x = "+sX,COLOR_ACTIVE_POINT);
			else
				pointDisplay.addEntry("("+sX+"|"+sY+")",COLOR_ACTIVE_POINT);
			//canvas.drawText("("+sX+"|"+sY+")", getWidth()/2, borderTop+40, paint);
			
		}
		else if (mode==MODE_TRACE | mode==MODE_SLOPE){
			if (currentX!=null){
				for (int i=0; i<fkts.size(); i++){
					paint.setStrokeWidth(1);
					paint.setColor(COLORS_GRAPHS[i%COLORS_GRAPHS.length]);
					if (fkts.get(i)!=null){
						canvas.drawCircle(unitToPxX(currentX), unitToPxY(fkts.get(i).calculate(currentX)), 7, paint);
						if (mode==MODE_SLOPE & fkts.get(i).slope(currentX)!=Double.NaN){
							canvas.drawLine(0, unitToPxY(fkts.get(i).calculate(currentX)-(currentX-pxToUnitX(0))*fkts.get(i).slope(currentX)), getWidth(),  unitToPxY(fkts.get(i).calculate(currentX)-(currentX-pxToUnitX(getWidth()))*fkts.get(i).slope(currentX)), paint);
						}
					}
				}
				pointDisplay.clear();
				pointDisplay.setEntryTextSize(15);
				if (colorSchema==COLORSCHEMA_DARK)
					pointDisplay.setBackgroundColor(Color.argb(200, 0, 0, 0));
				else if (colorSchema==COLORSCHEMA_LIGHT)
					pointDisplay.setBackgroundColor(Color.argb(200, 255, 255, 255));
				pointDisplay.setHeading(mContext.getResources().getString(mode==MODE_TRACE?R.string.mode_trace_pairs:R.string.mode_slope_pairs));

				for (int i=0; i<fkts.size(); i++){
					if (fkts.get(i)!=null){
						String sX;
						String sY;

						if (factorX==FACTOR_ONE | df.format(Math.abs(currentX)).equals("0"))
							sX = df.format(Math.abs(currentX)).equals("-0")?"0":df.format(currentX);
						else if (factorX==FACTOR_PI)
							sX = df.format(currentX/Math.PI)+"\u03C0";
						else if (factorX==FACTOR_E)
							sX = df.format(currentX/Math.E)+"e";
						else// if (factorX==FACTOR_DEG)
							sX = df.format(currentX/Math.PI*180)+"\u00B0";
						if (mode==MODE_TRACE){
							if (factorY==FACTOR_ONE | df.format(Math.abs(fkts.get(i).calculate(currentX))).equals("0"))
								sY = df.format(fkts.get(i).calculate(currentX)).equals("0")?"0":df.format(fkts.get(i).calculate(currentX));
							else if (factorY==FACTOR_PI)
								sY = df.format(fkts.get(i).calculate(currentX)/Math.PI)+"\u03C0";
							else if (factorY==FACTOR_E)
								sY = df.format(fkts.get(i).calculate(currentX)/Math.E)+"e";
							else// if (factorY==FACTOR_DEG)
								sY = df.format(fkts.get(i).calculate(currentX)/Math.PI*180)+"\u00B0";
						}
						else
							sY = df.format(fkts.get(i).slope(currentX));
						pointDisplay.addEntry("("+sX+"|"+sY+")", COLORS_GRAPHS[i%COLORS_GRAPHS.length]);
					}
				}
				if (pointDisplay.getEntrySize()<4)
					pointDisplay.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				else
					pointDisplay.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, (int) (100*mContext.getResources().getDisplayMetrics().density)));
			}
		}
		if (SHOW_TOUCH_POSITION && xs!=null){
			paint.setColor(Color.argb(100, 255, 255, 255));
			for (int i=0; i<xs.size();i++){
				canvas.drawCircle(xs.get(i), ys.get(i), 20, paint);
			}
		}
	}
	public File saveFile(String path, String name){
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
		Canvas bitmapCanvas = new Canvas();
		bitmapCanvas.setBitmap(bitmap);
		bitmapCanvas.drawColor(Color.BLACK);
		onDraw(bitmapCanvas);
		if (version==VERSION_LITE){
			Bitmap watermark= BitmapFactory.decodeResource(getResources(), R.drawable.watermark);
			bitmapCanvas.drawBitmap(watermark, (float)getWidth()/2-watermark.getWidth()/2, getHeight()-watermark.getHeight()-20, new Paint());
		}
		AlertDialog.Builder b = new AlertDialog.Builder(mContext);
		b.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
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
	
	public boolean isCallable(Intent intent) {  
        List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(intent,0);  
        return list.size() > 0;  
	}
	
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (mode==MODE_SLOPE | mode==MODE_TRACE){
			if (currentX==null)
				currentX=middleX;
			currentX=currentX+(double)event.getX()/zoomFactorX/5;
			pointDisplay.setVisibility(VISIBLE);
			return true;
		}
		return super.onTrackballEvent(event);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		menuParam.updateArrow((float)oldw);
	}
	
	public int getMode(){
		return mode;
	}
	
}