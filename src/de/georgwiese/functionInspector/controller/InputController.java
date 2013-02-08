package de.georgwiese.functionInspector.controller;

import com.actionbarsherlock.internal.view.View_HasStateListenerSupport;
import com.actionbarsherlock.internal.view.View_OnAttachStateChangeListener;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuPopupHelper;

import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;
import de.georgwiese.functionInspectorSpecial.*;

public class InputController {
	StateHolder sh;
	Context      c;
	UIController uic;
	DialogController dc;
	FktCanvas canvas;
	
	public InputController(Context c, StateHolder sh, UIController uic, DialogController dialogController, FktCanvas canvas){
		this.sh     = sh;
		this.c      =  c;
		this.uic    = uic;
		dc          = dialogController;
		this.canvas = canvas;
	}
	
	/**
	 * This function handles all click events from the UI
	 * @param v: View the user clicked on.
	 */
	public void onButtonClick(View v){
		switch(v.getId()){
		case R.id.menuButtonFkt:
			uic.toggleMenu(UIController.MENU_FKT);
			break;
		case R.id.menuButtonParam:
			uic.toggleMenu(UIController.MENU_PARAM);
			break;
		case R.id.menuButtonPoints:
			uic.toggleMenu(UIController.MENU_POINTS);
			break;
		case R.id.mv_points_roots:
			Log.d("Developer", "DisRoots clicked");
			sh.disRoots = ((CheckBox) v).isChecked();
			break;
		case R.id.mv_points_extrema:
			sh.disExtrema = ((CheckBox) v).isChecked();
			break;
		case R.id.mv_points_inflections:
			sh.disInflections = ((CheckBox) v).isChecked();
			break;
		case R.id.mv_points_discontinuities:
			sh.disDiscon = ((CheckBox) v).isChecked();
			break;
		case R.id.mv_points_intersections:
			sh.disIntersections = ((CheckBox) v).isChecked();
			break;
		case R.id.cbDisSlope:
			sh.disSlope = ((CheckBox) v).isChecked();
			canvas.invalidate();
			break;
		case R.id.mv_param_btParam:
			dc.showDialog(DialogController.SET_PARAM_DIALOG);
			break;
		case R.id.mv_param_btMin:
			dc.showDialog(DialogController.SET_MIN_DIALOG);
			break;
		case R.id.mv_param_btMax:
			dc.showDialog(DialogController.SET_MAX_DIALOG);
			break;
		case R.id.mode_trace_tv:
			dc.showDialog(DialogController.SET_X_DIALOG);
			break;
		case R.id.menuFktBtPro:
		case R.id.mv_points_btPro:
			dc.showDialog(DialogController.PRO_DIALOG);
			break;
		case R.id.zoom_minus:
			sh.zoomOut();
			break;
		case R.id.zoom_plus:
			sh.zoomIn();
			break;
		case R.id.center_button:
			sh.reset();
			canvas.resetPaths();
			break;
		case R.id.btTangentEq:
			dc.showDialog(DialogController.TANGENT_EQ_DIALOG);
			break;
		case R.id.beta_feedback:
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("http://georgwiese.blogspot.com/2012/09/fi-4-0-beta.html?m=1"));
			c.startActivity(i);
			break;
		}
		canvas.invalidate();
	}
}
