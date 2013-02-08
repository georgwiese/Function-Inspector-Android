package de.georgwiese.functionInspector;

import java.util.Random;
import com.appbarbecue.AppBarbecueClient;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import de.georgwiese.functionInspector.controller.*;
import de.georgwiese.functionInspector.uiClasses.*;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;
import de.georgwiese.functionInspectorSpecial.*;

/*
 * To do when switching versions
 * - package
 * - title, icon, permission
 */

public class MainScreen extends FragmentActivity {
	
	Context mContext;

	public static final String KEY_UNLOCK_FEATURE = "proUnlock";
	
	public static final String PACKAGE_PRO = "de.georgwiese.functionInspectorPro";
	public static final String PACKAGE_LITE = "de.georgwiese.functionInspectorLite";
	public static final String PACKAGE_UNLOCK = "de.georgwiese.functionInspectorUnlock";
	public static final String PACKAGE_SPECIAL = "de.georgwiese.functionInspectorSpecial";
	
	// Controllers
	StateHolder stateHolder;
	InputController inputController;
	UIController uiController;
	PathCollector pathCollector;
	RedrawThread redrawThread;
	UpdateThread updateThread;
	DialogController dialogController;
	
	// UI Elements
	FktCanvas canvas;
	Dialog mSplashDialog;
    
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        showSplashScreen();
        
    	mContext=this;
    	
    	Handler handler = new Handler();
    	
    	handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
		    	setContentView(R.layout.main);
		    	
		    	String packageName = getApplicationContext().getPackageName();
		    	boolean isPro = packageName.equals(PACKAGE_PRO) ||
		    						packageName.equals(PACKAGE_SPECIAL);
		    	Log.d("Dev", "isPro: "+isPro);
		    	Log.d("Dev", "packageName: "+packageName);
		    	Log.d("Dev", "should: "+PACKAGE_SPECIAL);
		    	if(packageName.equals(PACKAGE_UNLOCK))
		    		isPro |= AppBarbecueClient.getInstance().isFeatureUnlocked(KEY_UNLOCK_FEATURE);
		    	stateHolder = new StateHolder(mContext, isPro);
		    	DisplayMetrics dm = getResources().getDisplayMetrics();
		    	// Determine the approximate width in inch.
		    	// China Tablet: 5, Nexus 7 6, LGOS: 3.4
		    	boolean isTablet = Math.max(dm.widthPixels / dm.xdpi, dm.heightPixels / dm.ydpi) > 4.5;
		    	canvas = (FktCanvas) findViewById(R.id.fktCanvas);
		    	dialogController = new DialogController(mContext, getSupportFragmentManager(), stateHolder, MainScreen.this);
		    	pathCollector = new PathCollector(stateHolder, canvas);
		    	uiController = new UIController(mContext, stateHolder, pathCollector, dialogController, isTablet, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
		    	dialogController.setUIContoller(uiController);
		    	inputController = new InputController(mContext, stateHolder, uiController, dialogController, canvas);
		    	((MyKeyboardView) findViewById(R.id.keyboardView)).setUIController(uiController);
		    	
		    	canvas.setOnSizeChangedListener(new FktCanvas.OnSizeChangedListener() {
					@Override
					public void onSizeChanged(int w, int h, int oldw, int oldh) {
						// Update uiController because at initialization time, size of canvas is still zero
						uiController.onConfigChange();
					}
				});
		    	redrawThread = new RedrawThread(null, stateHolder, canvas, pathCollector);
		    	canvas.setProps(stateHolder, pathCollector);
		    	updateThread = new UpdateThread(canvas, stateHolder);
		    	redrawThread.start();
		    	updateThread.start();
		    	
		    	removeSplashScreen();
			}
		}, 1000);
    }
    
    
    
   /**
    * Shows the splash screen over the full Activity
    */
   protected void showSplashScreen() {
       mSplashDialog = new Dialog(this, R.style.SplashScreen);
       mSplashDialog.setContentView(R.layout.splashscreen);
       mSplashDialog.setCancelable(false);
       mSplashDialog.show();
    
       // Set Runnable to remove splash screen just in case
       final Handler handler = new Handler();
       handler.postDelayed(new Runnable() {
         @Override
         public void run() {
           removeSplashScreen();
         }
       }, 30000);
   }
    
    /**
     * Removes the Dialog that displays the splash screen
     */
    protected void removeSplashScreen() {
        if (mSplashDialog != null) {
            mSplashDialog.dismiss();
            mSplashDialog = null;

    		// If First startup, show welcome dialog
    		if (dialogController != null && stateHolder != null){
    	        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
    	    	Random r = new Random();
    	    	//((AdView) findViewById(R.id.adView)).setVisibility(View.VISIBLE);
                if (getApplicationContext().getPackageName().equals(PACKAGE_PRO) &&
                		!AppBarbecueClient.getInstance().isFeatureUnlocked(KEY_UNLOCK_FEATURE))
    	    		dialogController.showDialog(DialogController.UNLOCK_DIALOG);
    	    	else if (sp.getBoolean(StateHolder.KEY_FIRSTSTART, true)){
    	        	SharedPreferences.Editor e = sp.edit();
    	        	e.putBoolean(StateHolder.KEY_FIRSTSTART, false);
    	        	e.commit();
    	        	dialogController.showDialog(DialogController.WELCOME_DIALOG);
    	        }
				// If LITE Version, show FB dialog (1/12), PRO dialog (1/4) or nothing (2/3) after 1.5 seconds
		    	else if (r.nextInt(3)==1 && !stateHolder.isPro){
		    		if (r.nextInt(4)==1)
		    			dialogController.showDialog(DialogController.FACEBOOK_DIALOG);
		    		else
		    			dialogController.showDialog(DialogController.PRO_DIALOG);
		    	}
    		}
        }

    }
    
    /**
     * Called not only when Activity is created, but also when it is resumed from
     * the Activity stack, e.g. after showing the Preferences Screen. 
     */
    @Override
    protected void onStart() {
    	
    	super.onStart();
    	if (stateHolder != null){
    		stateHolder.initialize(mContext);
    		
    		//Handle Fullscreen
        	if (stateHolder.fullscreen){
            	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    		}	
    		else{
    			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    		}
    	}
    	if (uiController != null)
    		uiController.updateMode();
    }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("Developer", "Button Clicked!");
    	if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
    		if (uiController.isKBvisible())
    			uiController.setKBVisible(false);
    		else if (uiController.isMenuVisible())
    			uiController.hideAllMenus();
    		else
    			finish();
    	}
    	else if (keyCode == KeyEvent.KEYCODE_MENU){
    		((OverflowButton)findViewById(R.id.menuButton)).onClick(null);
    	}
    	return false;
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if (stateHolder != null)
    		stateHolder.saveCurrentState();
    	else
    		Log.e("Developer", "stateHolder null");
    	// So it won't bother to restore them (causes errors)
    	if (dialogController != null)
    		dialogController.closeAllDialogs();
    	else
    		Log.e("Developer", "dialogController null");
    }
    
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
    	// TODO: Implement Trackball Event
    	return super.onTrackballEvent(event);
    	//if (!graph.onTrackballEvent(event))
    	//	return super.onTrackballEvent(event);
    	//else
    	//	return true;
    }
    
    public void onButtonClick(View v){
    	inputController.onButtonClick(v);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	Log.d("Developer", "ConfigChange" + (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE));
    	if(uiController != null)
    		uiController.setLandscape(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE);
    }
    
    public void restart(){
    	stateHolder.saveCurrentState();
    	Intent intent = getIntent();
    	finish();
    	startActivity(intent);
    }
}
