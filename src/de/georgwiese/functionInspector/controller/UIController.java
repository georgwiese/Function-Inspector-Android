package de.georgwiese.functionInspector.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.google.ads.AdView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.functionInspector.uiClasses.EnterFunctionView;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.MenuPopup.OnMenuItemClickListener;
import de.georgwiese.functionInspector.uiClasses.MenuView;
import de.georgwiese.functionInspector.uiClasses.MyKeyboardView;
import de.georgwiese.functionInspector.uiClasses.OverflowButton;
import de.georgwiese.functionInspector.uiClasses.SwitchButtonSet;
import de.georgwiese.functionInspector.uiClasses.SwitchButtonSet.OnStateChangedListener;
import de.georgwiese.functionInspectorLite.MainScreen;
import de.georgwiese.functionInspectorLite.R;

/**
 * Class to control verious UI Elements, for example
 * hiding and showing menus.
 * @author Georg Wiese
 *
 */
public class UIController implements OnSeekBarChangeListener, OnStateChangedListener, OnMenuItemClickListener {
	
	public static final int MENU_FKT=0;
	public static final int MENU_PARAM=1;
	public static final int MENU_POINTS=2;
	//public static final int MENU_MODE=3;

	public static final int NORMAL_COLOR = Color.parseColor("#000000");
	public static final int ACTIVE_COLOR = Color.parseColor("#1c3640");
	public static final int HIGHLIGHT_COLOR = Color.parseColor("#3691b3");

	MenuView menus[];
	ImageButton menuButtons[];
	View dividers[];
	Context c;
	StateHolder sh;
	DialogController dc;
	boolean isTablet, isLandscape;
	FktCanvas fktCanvas;
	LinearLayout llButtons, llTrace, menuContainer, optionsBar;
	TextView traceTv;
	MyKeyboardView kv;
	AdView ad;
	ArrayList<EnterFunctionView> efv;
	SwitchButtonSet menuParamSbs;
	Button menuParamMin, menuParamValue, menuParamMax;
	SeekBar menuParamSb;
	OverflowButton menuButton;
	
	DecimalFormat df1, df2;
	Animation menuIn, menuOut, efvIn, efvOut, optionsIn, optionsOut;
	String[] paramNames = {"a", "b", "c"};
	boolean sbChangedByClick;
	
	/**
	 * UIController changes and animates all UI Elements
	 * @param c: Current Context
	 * @param stateHolder: StateHolder object
	 * @param isTablet: whether or not we have a tablet
	 * @param isLandscape: whether or not the device is in landscape orientation
	 */
	public UIController(Context c, StateHolder stateHolder, PathCollector pathCollector, DialogController dialogController, boolean isTablet, boolean isLandscape){
		this.c = c;
		sh = stateHolder;
		dc = dialogController;
		this.isTablet = isTablet;
		menus = new MenuView[3];	// Assuming that no ID is higher than 2
		menuButtons = new ImageButton[3];
		dividers = new View[3];
		MainScreen ms = (MainScreen) c;
		menus[MENU_FKT] = (MenuView) ms.findViewById(R.id.menuFunction);
		menus[MENU_PARAM] = (MenuView) ms.findViewById(R.id.menuParam);
		menus[MENU_POINTS] = (MenuView) ms.findViewById(R.id.menuPoints);
		menuButtons[MENU_FKT] = (ImageButton) ms.findViewById(R.id.menuButtonFkt);
		menuButtons[MENU_PARAM] = (ImageButton) ms.findViewById(R.id.menuButtonParam);
		menuButtons[MENU_POINTS] = (ImageButton) ms.findViewById(R.id.menuButtonPoints);
		dividers[MENU_FKT] = (View) ms.findViewById(R.id.divider_fkt);
		dividers[MENU_PARAM] = (View) ms.findViewById(R.id.divider_param);
		dividers[MENU_POINTS] = (View) ms.findViewById(R.id.divider_points);
		llButtons = (LinearLayout) ms.findViewById(R.id.ll_menuButtons);
		llTrace   = (LinearLayout) ms.findViewById(R.id.ll_traceBar);
		menuContainer = (LinearLayout) ms.findViewById(R.id.ll_menus);
		optionsBar = (LinearLayout) ms.findViewById(R.id.optionsBar);
		traceTv   = (TextView) ms.findViewById(R.id.mode_trace_tv);
		kv   = (MyKeyboardView) ms.findViewById(R.id.keyboardView);
		ad   = (AdView) ms.findViewById(R.id.adView);
		
		//Initialize evfs
		efv  = new ArrayList<EnterFunctionView>();
		for (Function f: sh.getFkts())
			efv.add(new EnterFunctionView(c, kv, this, f.getString()));
		
		menuButton = (OverflowButton) ms.findViewById(R.id.menuButton);
		String[] options = {"About", "Pro", "Welcome", "Try", "Facebook", "Pic", "Buy", "set Param.", "set min Param.", "set max Param."};
		menuButton.buildMenu(options, this);
		
		df1 = new DecimalFormat("0.0##");
		df2 = new DecimalFormat("0.00");

		menuParamSbs    = (SwitchButtonSet) ms.findViewById(R.id.mv_param_sbs);
		menuParamSb     = (SeekBar) ms.findViewById(R.id.mv_param_sb);
		menuParamMin    = (Button) ms.findViewById(R.id.mv_param_btMin);
		menuParamValue  = (Button) ms.findViewById(R.id.mv_param_btParam);
		menuParamMax    = (Button) ms.findViewById(R.id.mv_param_btMax);
		updateMenuParam(true);
		menuParamSb.setOnSeekBarChangeListener(this);
		menuParamSbs.setOnStateChangedListener(this);
		sbChangedByClick = false;
		
		fktCanvas = (FktCanvas) ms.findViewById(R.id.fktCanvas);
		fktCanvas.setOnTouchListener(new FktCanvasTouchListener(this, stateHolder, pathCollector, fktCanvas));


		menuIn  = AnimationUtils.loadAnimation(c, R.anim.menu_in);
		menuOut = AnimationUtils.loadAnimation(c, R.anim.menu_out);
		efvIn  = AnimationUtils.loadAnimation(c, R.anim.efv_in);
		efvOut = AnimationUtils.loadAnimation(c, R.anim.efv_out);
		optionsIn  = AnimationUtils.loadAnimation(c, R.anim.optionsbar_in);
		optionsOut = AnimationUtils.loadAnimation(c, R.anim.optionsbar_out);
		
		for (MenuView menu:menus)
			menu.setVisibility(View.GONE);
		onConfigChange();
		setLandscape(isLandscape);
		updateEfvs();
	}
	
	public void setMinParam(double value){
		sh.setMinParam(menuParamSbs.getState(), value);
		updateMenuParam(true);
	}
	
	public void setParam(double value){
		sh.setParam(menuParamSbs.getState(), value);
		if (value < sh.getMinParams()[menuParamSbs.getState()])
			sh.setMinParam(menuParamSbs.getState(), value);
		if (value > sh.getMaxParams()[menuParamSbs.getState()])
			sh.setMaxParam(menuParamSbs.getState(), value);
		updateMenuParam(true);
	}
	
	public void setMaxParam(double value){
		sh.setMaxParam(menuParamSbs.getState(), value);
		updateMenuParam(true);
	}
	
	/**
	 * This method should be called to Update the Parameter Menu
	 * @param complete: whether or not Seekbar and min/max Buttons should also be updated
	 * @param newState: New State of SwitchButtonSet
	 */
	public void updateMenuParam(boolean complete, int newState){
		int id = newState;
		menuParamValue.setText(paramNames[id] + ":\n" + df2.format(sh.getParams()[id]));
		if(complete){
			menuParamMin.setText("min. " + paramNames[id] + ":\n" + df2.format(sh.getMinParams()[id]));
			menuParamMax.setText("max. " + paramNames[id] + ":\n" + df2.format(sh.getMaxParams()[id]));
			sbChangedByClick = true;
			menuParamSb.setProgress((int)Math.round(((sh.getParams()[id] - sh.getMinParams()[id]) /
					(sh.getMaxParams()[id] - sh.getMinParams()[id])) * menuParamSb.getMax()));
		}
	}
	
	/**
	 * This method should be called to Update the Parameter Menu
	 * @param complete: whether or not Seekbar and min/max Buttons should also be updated
	 */
	public void updateMenuParam(boolean complete){
		updateMenuParam(complete, menuParamSbs.getState());
	}
	
	/**
	 * This method will show or hide a menu and - if necessary - hide others
	 * if there is't enough room on the screen
	 * @param id: ID of the menu. Use static Integers of this class starting with MENU_
	 */
	public void toggleMenu(int id){
		fktCanvas.invalidate();
		if (menus[id].getVisibility()==View.GONE || menus[id].getVisibility()==View.INVISIBLE){
			// Show Menu
			menus[id].setVisibility(View.VISIBLE);
			menuButtons[id].setBackgroundColor(ACTIVE_COLOR);
			dividers[id].setBackgroundColor(ACTIVE_COLOR);
			menus[id].startAnimation(menuIn);
			if (id == MENU_FKT){
				setKBVisible(true);
				efv.get(0).requestFocus();
			}
		}
		else{
			// Hide Menu
			menus[id].setVisibility((isTablet && isLandscape)?View.INVISIBLE:View.GONE);
			menuButtons[id].setBackgroundColor(NORMAL_COLOR);
			dividers[id].setBackgroundColor(HIGHLIGHT_COLOR);
			menus[id].startAnimation(menuOut);
			if (id == MENU_FKT)
				setKBVisible(false);
		}
		if (!(isTablet && isLandscape)){
			for (int i = 0; i < menus.length; i++){
				if (i != id){
					// Hide Menu
					if (menus[i].getVisibility() == View.VISIBLE)
						menus[i].startAnimation(menuOut);
					menus[i].setVisibility(View.GONE);
					menuButtons[i].setBackgroundColor(NORMAL_COLOR);
					dividers[i].setBackgroundColor(HIGHLIGHT_COLOR);

					if (i == MENU_FKT)
						setKBVisible(false);
				}}}
		
		if (menus[id].getVisibility() == View.VISIBLE)
			updateMenuWidth(id);
	}
	
	public void updateMenuWidth(int id){
		updateMenuWidth(id, fktCanvas.getWidth());
	}
	
	public void updateMenuWidth(int id, int newWidthPx){
		int maxWidth = (int)(350 * c.getResources().getDisplayMetrics().density);
		if (!(isTablet && isLandscape) && id >= 0 && id < menus.length &&
				newWidthPx >= maxWidth){
			menus[id].setLayoutParams(new LinearLayout.LayoutParams(maxWidth, LayoutParams.WRAP_CONTENT));
			switch(id){
			case MENU_FKT:
				menuContainer.setGravity(Gravity.LEFT); break;
			case MENU_PARAM:
				menuContainer.setGravity(Gravity.CENTER_HORIZONTAL); break;
			case MENU_POINTS:
				menuContainer.setGravity(Gravity.RIGHT); break;
			}
		}
		else
			for(MenuView menu:menus)
				menu.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
	}
	
	
	public void hideAllMenus(){
		for (int i = 0; i < menus.length; i++)
			if (menus[i].getVisibility() == View.VISIBLE)
				toggleMenu(i);
	}
	
	public void focusNextEfv(){
		for(int i = 0; i < efv.size(); i++){
			if (efv.get(i).hasFocus() && i < efv.size() - 1){
				efv.get(i + 1).requestFocus();
				break;
			}
		}
	}
	
	public void setKBVisible(boolean visible){
		if(visible){
			kv.setVisibility(View.VISIBLE);
			if (optionsBar.getVisibility() == View.VISIBLE){
				optionsBar.setVisibility(View.GONE);
				optionsBar.startAnimation(optionsOut);
			}
		}
		else{
			kv.setVisibility(View.GONE);
			if (optionsBar.getVisibility() != View.VISIBLE){
				optionsBar.setVisibility(View.VISIBLE);
				optionsBar.startAnimation(optionsIn);
			}
		}
	}
	
	public void updateEfvs(){
		// TODO: Implement features that are commented out
		if (efv.size()==0)
			efv.add(new EnterFunctionView(c, kv, this));
		for (int i=0; i<efv.size(); i++){
			if (efv.get(i).getEt().getText().toString().equals("")
					&& !efv.get(i).getEt().hasFocus()
					&& i!=efv.size()-1){
				// Remove Efv
				//efv.get(i).startAnimation(efvOut);
				efv.remove(i);
				i--;
			}
		}
		int count=0;
		for (EnterFunctionView e:efv)
			if (e.getEt().getText().toString().equals(""))
				count++;
		if (count==0){//) && (efv.size()<3)){ version==VERSION_PRO | 
			// Insert Efv
			EnterFunctionView v = new EnterFunctionView(c, kv, this);
			efv.add(v);
			v.startAnimation(efvIn);
		}
		for (int i=0; i<efv.size(); i++){
			efv.get(i).setNr(i+1);
			efv.get(i).setColor(FktCanvas.COLORS_GRAPHS[i % FktCanvas.COLORS_GRAPHS.length]);
		}
		menus[MENU_FKT].removeAllViews();
		for (EnterFunctionView e:efv)
			menus[MENU_FKT].addView(e);
		/*
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
		}*/
	}
	
	public void updateFkts(){
		sh.clearFkts();
		for (EnterFunctionView e:efv)
			sh.addFkt(CalcFkts.formatFktString(e.getEt().getText().toString()));
		sh.storeFkts();

		fktCanvas.invalidate();
	}
	
	public void toggleMode(){
		if (llButtons.getVisibility() == View.VISIBLE){
			llTrace.setVisibility(View.VISIBLE);
			llButtons.setVisibility(View.GONE);
			llTrace.startAnimation(menuIn);
			llButtons.startAnimation(menuOut);
		}
		else{
			llTrace.setVisibility(View.GONE);
			llButtons.setVisibility(View.VISIBLE);
			llTrace.startAnimation(menuOut);
			llButtons.startAnimation(menuIn);
		}
		hideAllMenus();
		updateTraceTv();
	}
	
	public void updateTraceTv(){
		traceTv.setText("x = " + df2.format(sh.currentX));
	}
	
	public void setLandscape(boolean value){
		isLandscape = value;
		onConfigChange();
	}
	
	public void onConfigChange(){
		boolean first = true;
		int id = -1;
		for(int i=0; i<menus.length; i++){
			if (first && menus[i].getVisibility()==View.VISIBLE){
				first = false;
				id = i;
			}else{
				menus[i].setVisibility((isTablet && isLandscape)?View.INVISIBLE:View.GONE);
				menuButtons[i].setBackgroundColor(NORMAL_COLOR);
			}
		}
		
		// TODO: This is dirty! Find a better way to get new width.
		updateMenuWidth(id, fktCanvas.getHeight());
	}

	// Interface for Seekbar in Parameter Menu
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (!sbChangedByClick){
			int id = menuParamSbs.getState();
			double value = (double)progress / (double)seekBar.getMax() *
					(sh.getMaxParams()[id] - sh.getMinParams()[id]) +
					sh.getMinParams()[id];
			sh.setParam(menuParamSbs.getState(), value);
			updateMenuParam(false);
			sh.redraw = true;
			fktCanvas.invalidate();
		}
		sbChangedByClick = false;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		sh.preview = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		sh.preview = false;
		sh.redraw  = true;
	}

	@Override
	public void onStateChanged(int newState) {
		updateMenuParam(true, newState);
	}

	@Override
	public void onMenuItemClick(int menuID, int itemID) {
		String[] options = {"About", "Pro", "Welcome", "Try", "Facebook", "Pic", "Buy", "set Param.", "set min Param.", "set max Param."};
		switch(itemID){
		case 0:
			dc.showDialog(DialogController.ABOUT_DIALOG); break;
		case 1:
			dc.showDialog(DialogController.PRO_DIALOG); break;
		case 2:
			dc.showDialog(DialogController.WELCOME_DIALOG); break;
		case 3:
			dc.showDialog(DialogController.TRY_DIALOG); break;
		case 4:
			dc.showDialog(DialogController.FACEBOOK_DIALOG); break;
		case 5:
			dc.showDialog(DialogController.PIC_DIALOG); break;
		case 6:
			dc.showDialog(DialogController.BUY_DIALOG); break;
		case 7:
			dc.showDialog(DialogController.SET_PARAM_DIALOG); break;
		case 8:
			dc.showDialog(DialogController.SET_MIN_DIALOG); break;
		case 9:
			dc.showDialog(DialogController.SET_MAX_DIALOG); break;
		}
	}
	
	
}
