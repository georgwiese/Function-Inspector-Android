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
import android.widget.TextView;
import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.functionInspector.uiClasses.EnterFunctionView;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.MenuView;
import de.georgwiese.functionInspector.uiClasses.MyKeyboardView;
import de.georgwiese.functionInspectorLite.MainScreen;
import de.georgwiese.functionInspectorLite.R;

/**
 * Class to control verious UI Elements, for example
 * hiding and showing menus.
 * @author Georg Wiese
 *
 */
public class UIController {
	
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
	boolean isTablet, isLandscape;
	FktCanvas fktCanvas;
	LinearLayout llButtons, llTrace, menuContainer;
	TextView traceTv;
	MyKeyboardView kv;
	AdView ad;
	ArrayList<EnterFunctionView> efv;
	
	DecimalFormat df1, df2;
	Animation menuIn, menuOut, efvIn, efvOut;
	
	/**
	 * UIController changes and animates all UI Elements
	 * @param c: Current Context
	 * @param stateHolder: StateHolder object
	 * @param isTablet: whether or not we have a tablet
	 * @param isLandscape: whether or not the device is in landscape orientation
	 */
	public UIController(Context c, StateHolder stateHolder, PathCollector pathCollector, boolean isTablet, boolean isLandscape){
		this.c = c;
		sh = stateHolder;
		this.isTablet = isTablet;
		menus = new MenuView[3];	// Assuming that no ID is higher than 2
		menuButtons = new ImageButton[3];
		dividers = new View[3];
		menus[MENU_FKT] = (MenuView) ((MainScreen) c).findViewById(R.id.menuFunction);
		menus[MENU_PARAM] = (MenuView) ((MainScreen) c).findViewById(R.id.menuParam);
		menus[MENU_POINTS] = (MenuView) ((MainScreen) c).findViewById(R.id.menuPoints);
		//menus[MENU_MODE] = (MenuView) ((MainScreen) c).findViewById(R.id.menuMode);
		menuButtons[MENU_FKT] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonFkt);
		menuButtons[MENU_PARAM] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonParam);
		menuButtons[MENU_POINTS] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonPoints);
		//menuButtons[MENU_MODE] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonMode);
		dividers[MENU_FKT] = (View) ((MainScreen) c).findViewById(R.id.divider_fkt);
		dividers[MENU_PARAM] = (View) ((MainScreen) c).findViewById(R.id.divider_param);
		dividers[MENU_POINTS] = (View) ((MainScreen) c).findViewById(R.id.divider_points);
		llButtons = (LinearLayout) ((MainScreen) c).findViewById(R.id.ll_menuButtons);
		llTrace   = (LinearLayout) ((MainScreen) c).findViewById(R.id.ll_traceBar);
		menuContainer = (LinearLayout) ((MainScreen) c).findViewById(R.id.ll_menus);
		traceTv   = (TextView) ((MainScreen) c).findViewById(R.id.mode_trace_tv);
		kv   = (MyKeyboardView) ((MainScreen) c).findViewById(R.id.keyboardView);
		ad   = (AdView) ((MainScreen) c).findViewById(R.id.adView);
		efv  = new ArrayList<EnterFunctionView>();
		fktCanvas = (FktCanvas) ((MainScreen) c).findViewById(R.id.fktCanvas);
		fktCanvas.setOnTouchListener(new FktCanvasTouchListener(this, stateHolder, pathCollector, fktCanvas));

		df1 = new DecimalFormat("0.0##");
		df2 = new DecimalFormat("0.00");

		menuIn  = AnimationUtils.loadAnimation(c, R.anim.menu_in);
		menuOut = AnimationUtils.loadAnimation(c, R.anim.menu_out);
		efvIn  = AnimationUtils.loadAnimation(c, R.anim.efv_in);
		efvOut = AnimationUtils.loadAnimation(c, R.anim.efv_out);
		
		for (MenuView menu:menus)
			menu.setVisibility(View.GONE);
		onConfigChange();
		setLandscape(isLandscape);
		updateEfvs();
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
			ad.setVisibility(View.GONE);
			kv.setVisibility(View.VISIBLE);
		}
		else{
			ad.setVisibility(View.VISIBLE);
			kv.setVisibility(View.GONE);
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
	
	
}
