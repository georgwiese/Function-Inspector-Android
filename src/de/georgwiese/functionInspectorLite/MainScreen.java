package de.georgwiese.functionInspectorLite;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract.RawContacts.Data;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.Obfuscator;
import com.android.vending.licensing.ServerManagedPolicy;

import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.functionInspector.FrameView;
import de.georgwiese.functionInspector.MarketArrayAdapter;
import de.georgwiese.functionInspector.MarketInfo;
import de.georgwiese.functionInspector.controller.InputController;
import de.georgwiese.functionInspector.controller.PathCollector;
import de.georgwiese.functionInspector.controller.RedrawThread;
import de.georgwiese.functionInspector.controller.StateHolder;
import de.georgwiese.functionInspector.controller.UIController;
import de.georgwiese.functionInspector.controller.UpdateThread;
import de.georgwiese.functionInspector.uiClasses.FktCanvas;
import de.georgwiese.functionInspector.uiClasses.FktCanvas.OnSizeChangedListener;
import de.georgwiese.functionInspector.uiClasses.MenuView;

/*
 * - package
 * - VERSION_
 * - title, icon, permission
 * - string
 * - Menu
 */
public class MainScreen extends Activity {
	// Version IDs
	private static final int VERSION_LITE=FrameView.VERSION_LITE;
	private static final int VERSION_PRO=FrameView.VERSION_PRO;
	private int version=VERSION_LITE;
	
	// For licensing (not done currently)
	/*
	private static final int ANDROID_MARKET=1;
	private int market=0;
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8+tolw+nDBbEy8+va0aixI4X4yEAg0VtwrwQbwE6aRYslO+ET9Qy+w0Wpu6WPQt4P/ude1HOXA/2vUtoGu41dToSZX9mUMO2GnYBrEZALzELkaT21yP3r7mfE9RNqWT/LLeihvzo/ThBLPTq4z1qTSKP8279+L1TfWTkZJTPAwS17K6JreP76AAWYIreXRfXhGqj1zs1gUj0rp/f1/XirR1M+VWUYOwQ8ys0mqaa1intM2XsHTdmoDBGoKqZdbqRJaEXvL2YoPCVqvfOJD+6+iGVcFhGnAw6esUyaCczXZdpA1VZqqNQNS6RB9gCgqdmJl6oTUa/p1YMp27xuNuulQIDAQAB";
	private static final byte[] SALT = new byte[] {
	    -42, 60, 39, -111, -112, -7, 73, -69, 55, 87, -99, -41, 76, -117, -34, -117, -17, 37, -64, 89
	};
	MyLicenseCheckerCallback mLicenseCheckerCallback;
	LicenseChecker mChecker;
	*/
	
	Context mContext;
	Activity mActivity;
	
	FrameView graph;
	public ImageButton bt_graph, bt_param,bt_points,bt_photo;
	
	public static final int ABOUT_DIALOG=0;
	public static final int PRO_DIALOG=1;
	public static final int WELCOME_DIALOG=2;
	public static final int TRY_DIALOG=3;
	public static final int BUY_DIALOG=4;
	public static final int PIC_DIALOG=5;
	public static final int FACEBOOK_DIALOG=6;
	private Dialog aboutDialog, buyDialog, picDialog;
	private AlertDialog proDialog;
	private AlertDialog welcomeDialog;
	private AlertDialog tryDialog;
	private AlertDialog facebookDialog;
	Boolean isFullscreen, isLight;
	
	File ss;
	LinearLayout graphView;
    //ImageView splashView;
    
	int bgDrawable, bgSpecialDrawable, focussedDrawable;
	//SplashThread mSplashThread;
	Dialog mSplashDialog;
	double[] sMinParams;
	double[] sMaxParams;
	double[] sParams;
	boolean[] choices;
	ArrayList<String> savedFkts;
	ArrayList<Function> fkts;
	
	
	// Controllers
	StateHolder stateHolder;
	InputController inputController;
	UIController uiController;
	PathCollector pathCollector;
	RedrawThread redrawThread;
	UpdateThread updateThread;
	
	// UI Elements
	FktCanvas canvas;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showSplashScreen();
        
        // Licensing (not done corrently)
        /*
    	if (version == VERSION_PRO && market==ANDROID_MARKET){
        	// Try to use more data here. ANDROID_ID is a single point of attack.
        	String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        	// Library calls this when it's done.
        	mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        	// Construct the LicenseChecker with a policy.
        	mChecker = new LicenseChecker(
        	    this, new MyServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)),
        	    BASE64_PUBLIC_KEY);
        	//doCheck();
        	mChecker.checkAccess(mLicenseCheckerCallback);
        	
        }
        */
        
    	setContentView(R.layout.main);
    	mContext=this;
    	mActivity=this;

    	stateHolder = new StateHolder(this);
    	// For testing:
    	//stateHolder.addFkt("x^2");
    	//stateHolder.addFkt("-x^2");
    	stateHolder.addFkt("sin(x)");
    	stateHolder.addFkt("tan(x)+1");
    	//stateHolder.addFkt("e^x");
    	//stateHolder.addFkt("-e^x");
    	//TODO: Find a whether or not it is a tablet
    	canvas = (FktCanvas) findViewById(R.id.fktCanvas);
    	pathCollector = new PathCollector(stateHolder, canvas);
    	uiController = new UIController(mContext, stateHolder, pathCollector, true, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    	inputController = new InputController(mContext, stateHolder, uiController, canvas);
    	
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
    	
    	
    	/*
    	graphView=(LinearLayout)findViewById(R.id.ll_graphView);
        
    	graphView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// Loading preferences
				SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
		        sMinParams = new double[3];
		        sMaxParams = new double[3];
		        sParams = new double[3];
		        for (int i = 0; i<3; i++){
		        	sMinParams[i] = (double)sp.getFloat("minParam_"+Integer.toString(i), -5);
		            sMaxParams[i] = (double)sp.getFloat("maxParam_"+Integer.toString(i), 5);
		            sParams[i] = (double)sp.getFloat("param_"+Integer.toString(i), 1);
		        }
		        if (version==VERSION_LITE){
		        	sMinParams[2]=-5;
		        	sMaxParams[2]=5;
		        	sParams[2]=1;
		        	sMinParams[1]=-5;
		        	sMaxParams[1]=5;
		        	sParams[1]=1;
		        }
		        choices = new boolean[5];
		        for (int i=0; i<5; i++)
		        	choices[i] = sp.getBoolean("c_" + Integer.toString(i), false);
		        if (version==VERSION_LITE)
		        	for (int i=1; i<5; i++)
		        		choices[i]=false;
		        savedFkts = new ArrayList<String>();
		        for (int i=0;i<100;i++){
		        	String value = sp.getString("f_"+Integer.toString(i), "null");
		        	if (!value.equals("null") && (version==VERSION_PRO | savedFkts.size()<3))
		        		savedFkts.add(value);
		        	else break;
		        }
		         
		        fkts = new ArrayList<Function>();
		        try{
		        	String fkt = CalcFkts.formatFktString(getIntent().getExtras().getString("fkt"));
		        	if (CalcFkts.check(fkt))
		        		fkts.add(new Function(fkt));
		        }catch(Exception e){
			        //restore
			        try{
			        	boolean cont = true;
			        	int i=0;
				        while(cont){
				        		String s = sp.getString("fkt_"+Integer.toString(i),"end");
				        		if (s.equals("end"))
				        			cont=false;
				        		else if (!s.equals("null"))
				        			fkts.add(new Function(s));
				        		i++;
				        }
			        }catch(NullPointerException e2){}
			    }
		        if (fkts.size()>3 && version==VERSION_LITE)
		        	while (fkts.size()>3)
		        		fkts.remove(fkts.size()-1);
		        
		        // Initialize Graph View
		        graph = new FrameView(mContext, version, sMinParams, sMaxParams, sParams, choices, savedFkts);
				graph.setFkts(fkts);
		        bt_graph = new ImageButton(mContext);
		        graph.addMenuButton(bt_graph);
		        bt_param = new ImageButton(mContext);;
		        graph.addMenuButton(bt_param);
		        bt_points = new ImageButton(mContext);
		        graph.addMenuButton(bt_points);
		        bt_photo = new ImageButton(mContext);
		        graph.addMenuButton(bt_photo, false);
		        graphView.addView(graph);
		        
		        // Set Ad Visibility
				if (version==VERSION_LITE)
					findViewById(R.id.adView).setVisibility(View.VISIBLE);
				
		        onStart();
				removeSplashScreen();
				
				// If First startup, show welcome dialog
				if (sp.getBoolean("firstStart3", true)){
		        	SharedPreferences.Editor e = sp.edit();
		        	e.putBoolean("firstStart3", false);
		        	e.commit();
		        	showDialog(WELCOME_DIALOG);
		        }
				// If LITE Version, show FB dialog (1/15), PRO dialog (4/15) or nothing (2/3) after 1.5 seconds
	        	Random r = new Random();
	        	if (r.nextInt(3)==1){
	        		if (r.nextInt(5)==1)
	        			showDialog(FACEBOOK_DIALOG);
	        		else
	        			showDialog(PRO_DIALOG);
	        	}
			}
		}, 1000);
		*/
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
        }
    }
    
    /**
     * Called not only when Activity is created, but also when it is resumed from
     * the Activity stack, e.g. after showing the Preferences Screen. 
     */
    @Override
    protected void onStart() {
    	
    	super.onStart();
    	
    	// Check, if graph is null, because onStart() is called twice, first by Activity life cycle, than manually.
    	if (graph!=null){
    		// Set Preferences
    		graph.updateFactors();
	    	boolean isPro = version==VERSION_PRO;
	    	boolean zoomXY = !getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("prefs_zoomXY", true);
	    	graph.setZoomXY(zoomXY);
	    	if(!zoomXY)
	    		graph.setOneToOneZoomRatio();
			if (getSharedPreferences("prefs", MODE_PRIVATE).getString("prefs_color", "1").equals("2")&isPro){
	    		isLight=true;
				graph.setColorSchema(FrameView.COLORSCHEMA_LIGHT);
				bgDrawable=R.drawable.bt_bar_light;
	    		bgSpecialDrawable=R.drawable.bt_bar_light;
	    		focussedDrawable = R.drawable.bt_bar_focussed_light;
	    		graph.setColorSchema(FrameView.COLORSCHEMA_LIGHT);
	    		bt_graph.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_fkt_dark));
	    		bt_param.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_param_dark));
	    		bt_points.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_points_dark));
	    		if (graph.getMode()==FrameView.MODE_TRACE)
	    			bt_photo.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_mode_trace_dark));
	    		else if (graph.getMode()==FrameView.MODE_SLOPE)
	    			bt_photo.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_mode_slope_dark));
	    		else
	    			bt_photo.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_mode_pan_dark));
	    	}
			else{
				isLight=false;
				graph.setColorSchema(FrameView.COLORSCHEMA_DARK);
				bgDrawable=R.drawable.bt_bar;
	    		bgSpecialDrawable=R.drawable.bt_bar;
	    		focussedDrawable = R.drawable.bt_bar_focussed;
	    		graph.setColorSchema(FrameView.COLORSCHEMA_DARK);
	    		bt_graph.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_fkt));
	    		bt_param.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_param));
	    		bt_points.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_points));
	    		if (graph.getMode()==FrameView.MODE_TRACE)
	    			bt_photo.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_mode_trace));
	    		else if (graph.getMode()==FrameView.MODE_SLOPE)
	    			bt_photo.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_mode_slope));
	    		else
	    			bt_photo.setImageDrawable(getResources().getDrawable(R.drawable.bt_bar_mode_pan));
	       }
	    	resetButtons();
	    	if (isPro & getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("prefs_startFullscreen", false)){
	        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				isFullscreen=true;
			}	
			else{
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				isFullscreen=false;
			}
	    	bt_graph.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(graph.menu==-1 | graph.menu!=FrameView.MENU_GRAPH){
						resetButtons();
						graph.showMenu(FrameView.MENU_GRAPH);
						v.setBackgroundResource(focussedDrawable);
					}
					else{
						resetButtons();
					}
				}
			});
	        bt_param.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(graph.menu==-1 | graph.menu!=FrameView.MENU_PARAM){
						resetButtons();
						graph.showMenu(FrameView.MENU_PARAM);
						v.setBackgroundResource(focussedDrawable);
					}
					else{
						resetButtons();
					}
				}
			});
	        bt_points.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(graph.menu==-1 | graph.menu!=FrameView.MENU_POINTS){
						resetButtons();
						graph.showMenu(FrameView.MENU_POINTS);
						v.setBackgroundResource(focussedDrawable);
					}
					else{
						resetButtons();
					}
				}
			});
	        bt_photo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(graph.menu==-1 | graph.menu!=FrameView.MENU_MODE){
						resetButtons();
						graph.showMenu(FrameView.MENU_MODE);
						v.setBackgroundResource(focussedDrawable);
					}
					else{
						resetButtons();
					}
				}
			});
    	}
    }
    
    public boolean onCreateOptionsMenu(Menu m){
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.options_menu, m);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case R.id.menu_about:
    		showDialog(ABOUT_DIALOG);
    		return true;
    	case R.id.menu_prefs:
    		Intent i = new Intent(getApplicationContext(),Prefs.class);
    		i.putExtra("version", version);
    		startActivity(i);
    		return true;
    	case R.id.menu_upgrade:
    		showDialog(PRO_DIALOG);
    		return true;
    	/*case R.id.menu_version:
    		version=version==VERSION_LITE?VERSION_PRO:VERSION_LITE;
    		onCreate(null);
    		onStart();
    		return true;*/
    	case R.id.menu_screenshot:
    		showDialog(PIC_DIALOG);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    protected Dialog onCreateDialog(int id) {
    	switch (id){
    	case ABOUT_DIALOG:
    		aboutDialog = new Dialog (this);
    		aboutDialog.setContentView(R.layout.about_layout);
    		aboutDialog.setTitle(R.string.menu_about_str);
            Button ok2 = (Button) aboutDialog.findViewById(R.id.bt_about_ok);
            ok2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					aboutDialog.cancel();}});
            return aboutDialog;
    	case WELCOME_DIALOG:
    		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
    		builder2.setTitle(R.string.welcome_dialog_title);
    		builder2.setMessage(R.string.welcome_dialog_message);
    		builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();}});
    		welcomeDialog = builder2.create();
    		return welcomeDialog;
    	case PRO_DIALOG:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.pro_dialog_title);
    		builder.setIcon(R.drawable.icon_pro);
    		builder.setMessage(R.string.pro_dialog_message);
    		builder.setPositiveButton(R.string.pro_dialog_buy, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					showDialog(BUY_DIALOG);
				}
			});
    		builder.setNeutralButton(R.string.pro_dialog_try, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
			        dialog.cancel();
					showDialog(TRY_DIALOG);
				}
			});
    		builder.setNegativeButton(R.string.pro_dialog_notnow, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.cancel();
				}
			});
    		proDialog=builder.create();
    		return proDialog;
    	case TRY_DIALOG:
    		AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
    		builder3.setTitle(R.string.pro_dialog_title);
    		builder3.setIcon(R.drawable.icon_pro);
    		builder3.setMessage(R.string.try_dialog_message);
    		final boolean used = getSharedPreferences("data", MODE_PRIVATE).getBoolean("used3", false);
    		builder3.setPositiveButton(used==false?R.string.try_dialog_button:R.string.try_dialog_button_inactive, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (!used){
						SharedPreferences.Editor e= getSharedPreferences("data", MODE_PRIVATE).edit();
						e.putBoolean("used3", true);
						e.commit();
						version=VERSION_PRO;
						onPause();
						onCreate(null);
			    		onStart();
			    	}
				}
			});
    		tryDialog=builder3.create();
    		return tryDialog;
    	case BUY_DIALOG:
    		buyDialog = new Dialog (this);
    		buyDialog.setContentView(R.layout.buy_layout);
    		buyDialog.setTitle(R.string.pro_dialog_title);
            ListView lv = (ListView)buyDialog.findViewById(R.id.buy_lv);
            final ArrayList<MarketInfo> markets = new ArrayList<MarketInfo>();
            markets.add(new MarketInfo("Android Market", "2.49", "1.49", "market://details?id=de.georgwiese.functionInspectorPro", "https://market.android.com/details?id=de.georgwiese.functionInspectorPro"));
            markets.add(new MarketInfo("AndroidPIT App Center", "~2.00", "1.49", "appcenter://package/de.georgwiese.functionInspectorPro", "http://www.androidpit.com/en/android/doorway-to-app"));
            //markets.add(new MarketInfo("AndSpot Market", "1.25", "~0.89", "appcenter://package/de.georgwiese.functionInspectorPro", "http://andspot.com"));
            lv.setAdapter(new MarketArrayAdapter(this, R.id.bt_ic_market, markets));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            	@Override
            	public void onItemClick(AdapterView<?> arg0, View arg1,
            			int arg2, long arg3) {
            		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((MarketInfo)markets.get(arg2)).uri));
            		if (!graph.isCallable(intent))
            			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((MarketInfo)markets.get(arg2)).url));
            		startActivity(intent);
            	}
			});
            return buyDialog;
    	case PIC_DIALOG:
    		picDialog = new Dialog(this);
    		picDialog.setContentView(R.layout.mv_screenshot);
    		picDialog.setTitle(R.string.pic_takeSc);
    		LayoutParams params = picDialog.getWindow().getAttributes();
    		params.width=LayoutParams.FILL_PARENT;
    		picDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    		
    		final EditText et_pic = (EditText)picDialog.findViewById(R.id.mv_pic_et);
    		final Button bt_pic = (Button)picDialog.findViewById(R.id.mv_pic_save);
    		final Button bt_open = (Button)picDialog.findViewById(R.id.mv_pic_open);
    		final Button bt_share = (Button)picDialog.findViewById(R.id.mv_pic_share);
    		TextView path = (TextView)picDialog.findViewById(R.id.mv_pic_path);
    		bt_pic.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				ss=graph.saveFile(Environment.getExternalStorageDirectory().toString()+"/"+getSharedPreferences("prefs", 0).getString("prefs_folder", "Function Inspector")+"/", et_pic.getText().toString()+".jpg");
    				bt_share.setEnabled(ss!=null);
    				bt_open.setEnabled(ss!=null);
    			}
    		});
    		bt_open.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
    				sendIntent.setDataAndType(Uri.fromFile(ss), "image/jpeg");
    				startActivity(sendIntent);
    			}
    		});
    		bt_share.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Intent sendIntent = new Intent(Intent.ACTION_SEND);
    				sendIntent.setType("image/jpg");
    				sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ss));
    				startActivity(Intent.createChooser(sendIntent, getString(R.string.pic_share)));
    			}
    		});
    		path.setText("/sdcard/"+getSharedPreferences("prefs", 0).getString("prefs_folder", "Function Inspector")+"/");
    		return picDialog;
    	case FACEBOOK_DIALOG:
    		AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
    		builder4.setTitle(R.string.facebook_dialog_title);
    		builder4.setMessage(R.string.facebook_dialog_message);
    		builder4.setNegativeButton(R.string.facebook_dialog_visit, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse("http://www.facebook.com/pages/Function-Inspector/207887339248278"));
					startActivity(i);
				}
			});
    		builder4.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
    		facebookDialog=builder4.create();
    		return facebookDialog;
    		
    	default:
    		return null;
    	}
    }
    
    public void resetButtons(){
    	graph.hideAllMenus();
    	bt_graph.setBackgroundResource(bgDrawable);
    	bt_param.setBackgroundResource(bgDrawable);
    	bt_photo.setBackgroundResource(bgSpecialDrawable);
    	bt_points.setBackgroundResource(bgDrawable);
    }
    
    public void setMode(int mode){
    	switch(mode){
    	case FrameView.MODE_PAN:
    		if (isLight)
    			bt_photo.setImageResource(R.drawable.bt_bar_mode_pan_dark);
    		else
    			bt_photo.setImageResource(R.drawable.bt_bar_mode_pan);
    		break;
    	case FrameView.MODE_TRACE:
    		if (isLight)
    			bt_photo.setImageResource(R.drawable.bt_bar_mode_trace_dark);
    		else
    			bt_photo.setImageResource(R.drawable.bt_bar_mode_trace);
    		break;
    	case FrameView.MODE_SLOPE:
    		if (isLight)
    			bt_photo.setImageResource(R.drawable.bt_bar_mode_slope_dark);
    		else
    			bt_photo.setImageResource(R.drawable.bt_bar_mode_slope);
    		break;
    	}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode==KeyEvent.KEYCODE_BACK){
    		if (graph.getCurrentMenu()==FrameView.MENU_GRAPH && graph.isKBVisible())
    			graph.setKBVisibe(false);
    		else if (graph.getCurrentMenu()!=-1)
    			resetButtons();
    		else
    			finish();
    		return true;
    	}
    	return false;
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	//save current state
    	SharedPreferences.Editor e = getSharedPreferences("data", MODE_PRIVATE).edit();
    	if (graph!=null){
	    	ArrayList<Function> fkts = graph.getFkts();
	    	for (int i=0; i<fkts.size(); i++){
	    		Function f = fkts.get(i);
	    		if (f!=null)
	    			e.putString("fkt_"+Integer.toString(i), f.getString());
	    		else
	    			e.putString("fkt_"+Integer.toString(i), "null");		
	    	}
			e.putString("fkt_"+Integer.toString(fkts.size()), "end");		
	    	
	    	double[] minParams=graph.getMinParams();
	    	double[] maxParams=graph.getMaxParams();
	    	double[] params=graph.getParams();
	    	for (int i =0; i<3; i++){
	        	e.putFloat("minParam_"+Integer.toString(i), (float)minParams[i]);
	        	e.putFloat("maxParam_"+Integer.toString(i), (float)maxParams[i]);
	        	e.putFloat("param_"+Integer.toString(i), (float)params[i]);
	    	}
	    	boolean[] choices = graph.getPointsChoices();
	    	for (int i=0;i<5;i++)
	    		e.putBoolean("c_"+Integer.toString(i), choices[i]);
	    	String[] savedFkts = graph.getSavedFktsArray();
	    	for (int i=0; i<100; i++){
	    		if (i<savedFkts.length)
	    			e.putString("f_"+Integer.toString(i), savedFkts[i]);
	    		else
	    			e.putString("f_"+Integer.toString(i), "null");
	    	}
	    	e.commit();
    	}
    }
    
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
    	if (!graph.onTrackballEvent(event))
    		return super.onTrackballEvent(event);
    	else
    		return true;
    }
    
    public void onButtonClick(View v){
    	inputController.onButtonClick(v);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	Log.d("Developer", "ConfigChange" + (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE));
    	uiController.setLandscape(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE);
    }
/*
    private class MyLicenseCheckerCallback implements LicenseCheckerCallback{

        public void allow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
            // so do nothing
            Toast.makeText(mContext, "ALLOW!", Toast.LENGTH_LONG).show();
        }

        public void dontAllow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            //displayResult("Don't allow1");
            // Should not allow access. An app can handle as needed,
            // typically by informing the user that the app is not licensed
            // and then shutting down the app or limiting the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            //showDialog(0);
            Toast.makeText(mContext, "DONT ALLOW!", Toast.LENGTH_LONG).show();
        }

		@Override
		public void applicationError(ApplicationErrorCode errorCode) {
			
		}
    	
    }
    public class MyServerManagedPolicy extends ServerManagedPolicy{

		public MyServerManagedPolicy(Context context, Obfuscator obfuscator) {
			super(context, obfuscator);
		}
		
		@Override
		public boolean allowAccess() {
			return super.allowAccess();
		}
    	
    }
    */
}
