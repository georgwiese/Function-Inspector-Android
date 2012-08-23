package de.georgwiese.functionInspector.controller;

import de.georgwiese.functionInspectorLite.R;
import android.content.Context;
import android.util.Log;

public class InputController {
	StateHolder sh;
	Context      c;
	UIController uic;
	
	public InputController(Context c, StateHolder sh, UIController uic){
		this.sh = sh;
		this.c  =  c;
		this.uic = uic;
	}
	
	public void onButtonClick(int id){
		switch(id){
		case R.id.menuButtonFkt:
			uic.toggleMenu(UIController.MENU_FKT);
			break;
		case R.id.menuButtonParam:
			uic.toggleMenu(UIController.MENU_PARAM);
			break;
		case R.id.menuButtonPoints:
			uic.toggleMenu(UIController.MENU_POINTS);
			break;
		case R.id.menuButtonMode:
			uic.toggleMenu(UIController.MENU_MODE);
			break;
		}
	}
}
