package de.georgwiese.functionInspector.controller;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import de.georgwiese.functionInspector.uiClasses.MenuView;
import de.georgwiese.functionInspectorLite.MainScreen;
import de.georgwiese.functionInspectorLite.R;

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
	boolean tabletLandscape;
	
	public UIController(Context c, boolean tabletLandscape){
		this.c = c;
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
		for (MenuView menu:menus)
			menu.setVisibility(View.GONE);
		onConfigChange();
		setTabletLandscape(tabletLandscape);
	}
	
	public void toggleMenu(int id){
		if (menus[id].getVisibility()==View.GONE || menus[id].getVisibility()==View.INVISIBLE){
			menus[id].setVisibility(View.VISIBLE);
			menuButtons[id].setBackgroundColor(ACTIVE_COLOR);
		}
		else{
			menus[id].setVisibility(tabletLandscape?View.INVISIBLE:View.GONE);
			menuButtons[id].setBackgroundColor(NORMAL_COLOR);
		}
		if (!tabletLandscape){
			for (int i = 0; i < menus.length; i++){
				if (i != id){
					menus[i].setVisibility(View.GONE);
					menuButtons[i].setBackgroundColor(NORMAL_COLOR);
				}}}
	}
	
	public void setTabletLandscape(boolean value){
		tabletLandscape = value;
		onConfigChange();
	}
	
	public void onConfigChange(){
		boolean first = true;
		for(int i=0; i<menus.length; i++){
			if (first && menus[i].getVisibility()==View.VISIBLE)
				first = false;
			else{
				menus[i].setVisibility(tabletLandscape?View.INVISIBLE:View.GONE);
				menuButtons[i].setBackgroundColor(NORMAL_COLOR);
			}
		}
		
		// TODO: When called at initialization, this is still 0 
		int menuModeWidth = Math.max(menus[MENU_MODE].getWidth(), menuButtons[MENU_MODE].getWidth());
		menus[MENU_MODE].setMinimumWidth(menuModeWidth);
		menuButtons[MENU_MODE].setMinimumWidth(menuModeWidth);
	}
}
