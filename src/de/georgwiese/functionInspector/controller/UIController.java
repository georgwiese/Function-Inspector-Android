package de.georgwiese.functionInspector.controller;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.MenuView;
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
	public static final int MENU_MODE=3;

	public static final int NORMAL_COLOR = Color.parseColor("#000000");
	public static final int ACTIVE_COLOR = Color.parseColor("#002233");

	MenuView menus[];
	ImageButton menuButtons[];
	Context c;
	boolean isTablet, isLandscape;
	FktCanvas fktCanvas;
	
	
	/**
	 * UIController changes and animates all UI Elements
	 * @param c: Current Context
	 * @param stateHolder: StateHolder object
	 * @param isTablet: whether or not we have a tablet
	 * @param isLandscape: whether or not the device is in landscape orientation
	 */
	public UIController(Context c, StateHolder stateHolder, boolean isTablet, boolean isLandscape){
		this.c = c;
		this.isTablet = isTablet;
		menus = new MenuView[4];	// Assuming that no ID is higher than 3
		menuButtons = new ImageButton[4];
		menus[MENU_FKT] = (MenuView) ((MainScreen) c).findViewById(R.id.menuFunction);
		menus[MENU_PARAM] = (MenuView) ((MainScreen) c).findViewById(R.id.menuParam);
		menus[MENU_POINTS] = (MenuView) ((MainScreen) c).findViewById(R.id.menuPoints);
		menus[MENU_MODE] = (MenuView) ((MainScreen) c).findViewById(R.id.menuMode);
		menuButtons[MENU_FKT] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonFkt);
		menuButtons[MENU_PARAM] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonParam);
		menuButtons[MENU_POINTS] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonPoints);
		menuButtons[MENU_MODE] = (ImageButton) ((MainScreen) c).findViewById(R.id.menuButtonMode);
		fktCanvas = (FktCanvas) ((MainScreen) c).findViewById(R.id.fktCanvas);
		fktCanvas.setOnTouchListener(new FktCanvasTouchListener(stateHolder, fktCanvas));
		for (MenuView menu:menus)
			menu.setVisibility(View.GONE);
		onConfigChange();
		setLandscape(isLandscape);
	}
	
	/**
	 * This method will show or hide a menu and - if necessary - hide others
	 * if there is't enough room on the screen
	 * @param id: ID of the menu. Use static Integers of this class starting with MENU_
	 */
	public void toggleMenu(int id){
		fktCanvas.invalidate();
		if (menus[id].getVisibility()==View.GONE || menus[id].getVisibility()==View.INVISIBLE){
			menus[id].setVisibility(View.VISIBLE);
			menuButtons[id].setBackgroundColor(ACTIVE_COLOR);
		}
		else{
			menus[id].setVisibility((isTablet && isLandscape)?View.INVISIBLE:View.GONE);
			menuButtons[id].setBackgroundColor(NORMAL_COLOR);
		}
		if (!(isTablet && isLandscape)){
			for (int i = 0; i < menus.length; i++){
				if (i != id){
					menus[i].setVisibility(View.GONE);
					menuButtons[i].setBackgroundColor(NORMAL_COLOR);
				}}}
	}
	
	public void setLandscape(boolean value){
		isLandscape = value;
		onConfigChange();
	}
	
	public void onConfigChange(){
		boolean first = true;
		for(int i=0; i<menus.length; i++){
			if (first && menus[i].getVisibility()==View.VISIBLE)
				first = false;
			else{
				menus[i].setVisibility((isTablet && isLandscape)?View.INVISIBLE:View.GONE);
				menuButtons[i].setBackgroundColor(NORMAL_COLOR);
			}
		}
		
		// TODO: When called at initialization, this is still 0 
		/*
		int menuModeWidth = Math.max(menus[MENU_MODE].getWidth(), menuButtons[MENU_MODE].getWidth());
		Log.d("Developer", "onConfigChange() --> " + menus[MENU_MODE].getWidth() + ", " + menuButtons[MENU_MODE].getWidth() + ", " + menuModeWidth);
		menus[MENU_MODE].setMinimumWidth(menuModeWidth);
		menuButtons[MENU_MODE].setMinimumWidth(menuModeWidth);
		menus[MENU_MODE].invalidate();
		*/
	}
	
	
}
