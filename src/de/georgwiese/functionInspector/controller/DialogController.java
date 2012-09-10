package de.georgwiese.functionInspector.controller;

import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.functionInspectorLite.MainScreen;
import de.georgwiese.functionInspectorLite.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogController {

	public static final int ABOUT_DIALOG     = 0;
	public static final int PRO_DIALOG       = 1;
	public static final int WELCOME_DIALOG   = 2;
	public static final int TRY_DIALOG       = 3;
	public static final int BUY_DIALOG       = 4; // TODO: Implement Buy Dialog
	public static final int PIC_DIALOG       = 5;
	public static final int FACEBOOK_DIALOG  = 6;
	public static final int SET_PARAM_DIALOG = 7;
	public static final int SET_MIN_DIALOG   = 8;
	public static final int SET_MAX_DIALOG   = 9;
	public static final int SET_X_DIALOG     = 10;
	
	
	Context c;
	FragmentManager fm;
	UIController uic;
	StateHolder sh;
	
	public DialogController(Context context, FragmentManager fragmentManager, StateHolder stateHolder) {
		c = context;
		fm = fragmentManager;
		sh = stateHolder;
	}
	
	/**
	 * Need to be called as soon as uiController is initialized
	 * @param uiController
	 */
	public void setUIContoller ( UIController uiController){
		uic = uiController;
	}
	
	public void showDialog(int id){
		switch (id) {
		case ABOUT_DIALOG:
	        new DialogFragment(){
	        	@Override
	        	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        		return new AlertDialog.Builder(new ContextThemeWrapper(c, R.style.Theme_Sherlock_Dialog_FunctionInspector))
	        				.setTitle(R.string.menu_about_str)
	        				.setMessage(R.string.about)
	        				.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
	        				.create();
	        	}
	        }.show(fm, "about");
			break;
			
		case WELCOME_DIALOG:
	        new DialogFragment(){
	        	@Override
	        	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        		return new AlertDialog.Builder(new ContextThemeWrapper(c, R.style.Theme_Sherlock_Dialog_FunctionInspector))
	        				.setTitle(R.string.welcome_dialog_title)
	        				.setMessage(R.string.welcome_dialog_message)
	        				.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
	        				.create();
	        	}
	        }.show(fm, "welcome");
			break;
			
		case PRO_DIALOG:
	        new DialogFragment(){
	        	@Override
	        	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        		return new AlertDialog.Builder(new ContextThemeWrapper(c, R.style.Theme_Sherlock_Dialog_FunctionInspector))
	        				.setTitle(R.string.pro_dialog_title)
	        				.setMessage(R.string.pro_dialog_message)
	        				.setIcon(R.drawable.icon_pro)
	        				.setPositiveButton(R.string.pro_dialog_buy, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=de.georgwiese.functionInspectorPro"));
									startActivity(intent);
								}
							})
	        				.setNeutralButton(R.string.pro_dialog_try, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									showDialog(TRY_DIALOG);
								}
							})
	        				.setNegativeButton(R.string.pro_dialog_notnow, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
	        				.create();
	        	}
	        }.show(fm, "welcome");
			break;
			
		case TRY_DIALOG:
	        new DialogFragment(){
	        	@Override
	        	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        		return new AlertDialog.Builder(new ContextThemeWrapper(c, R.style.Theme_Sherlock_Dialog_FunctionInspector))
    						.setTitle(R.string.pro_dialog_title)
    						.setMessage(R.string.try_dialog_message)
    						.setIcon(R.drawable.icon_pro)
    						// TODO: Implement Try Dialog Functionality
	        				.setNegativeButton(sh.tryUsed?R.string.try_dialog_button_inactive:R.string.try_dialog_button,
	        						new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (!sh.tryUsed){
										sh.tryUsed = true;
										sh.setIsPro(true);
										((MainScreen) c).restart();
									}
									dialog.dismiss();
								}
							})
	        				.create();
	        	}
	        }.show(fm, "welcome");
			break;
			
		case PIC_DIALOG:
			new DialogFragment(){
				@Override
				public void onCreate(Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
					setStyle(STYLE_NORMAL, R.style.Theme_Sherlock_Dialog_FunctionInspector);
				};
				
				public View onCreateView(LayoutInflater inflater,
						android.view.ViewGroup container, Bundle savedInstanceState) {
					
					getDialog().setTitle(R.string.pic_takeSc);
					View v = inflater.inflate(R.layout.mv_screenshot, container);

		    		EditText et_pic = (EditText)v.findViewById(R.id.mv_pic_et);
		    		Button bt_pic = (Button)v.findViewById(R.id.mv_pic_save);
		    		final Button bt_open = (Button)v.findViewById(R.id.mv_pic_open);
		    		final Button bt_share = (Button)v.findViewById(R.id.mv_pic_share);
		    		TextView path = (TextView)v.findViewById(R.id.mv_pic_path);
		    		
		    		// TODO: adjust text color according to background color
		    		if (getTheme() == android.R.style.Theme_Holo_Light ||
		    				getTheme() == android.R.style.Theme_Holo_Light_Dialog ||
		    				getTheme() == android.R.style.Theme_Holo_Light_Panel){
		    			path.setTextColor(Color.BLACK);
		    		}
		    		// TODO: implement Buttons behavior (screenshot)
		    		bt_pic.setOnClickListener(new OnClickListener() {
		    			@Override
		    			public void onClick(View v) {
		    				//ss=graph.saveFile(Environment.getExternalStorageDirectory().toString()+"/"+getSharedPreferences("prefs", 0).getString("prefs_folder", "Function Inspector")+"/", et_pic.getText().toString()+".jpg");
		    				//bt_share.setEnabled(ss!=null);
		    				//bt_open.setEnabled(ss!=null);
		    			}
		    		});
		    		bt_open.setOnClickListener(new OnClickListener() {
		    			@Override
		    			public void onClick(View v) {
		    				//Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		    				//sendIntent.setDataAndType(Uri.fromFile(ss), "image/jpeg");
		    				//startActivity(sendIntent);
		    			}
		    		});
		    		bt_share.setOnClickListener(new OnClickListener() {
		    			@Override
		    			public void onClick(View v) {
		    				//Intent sendIntent = new Intent(Intent.ACTION_SEND);
		    				//sendIntent.setType("image/jpg");
		    				//sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ss));
		    				//startActivity(Intent.createChooser(sendIntent, getString(R.string.pic_share)));
		    			}
		    		});
		    		path.setText(Environment.getExternalStorageDirectory() + "/" +
		    					sh.getScreenshotFolder() + "/");
					return v;
				};
			}.show(fm, "screenshot");
			break;
			
		case FACEBOOK_DIALOG:
	        new DialogFragment(){
	        	@Override
	        	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        		return new AlertDialog.Builder(new ContextThemeWrapper(c, R.style.Theme_Sherlock_Dialog_FunctionInspector))
    						.setTitle(R.string.facebook_dialog_title)
    						.setMessage(R.string.facebook_dialog_message)
	        				.setNegativeButton(R.string.facebook_dialog_visit, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse("https://www.facebook.com/function.inspector"));
									dialog.dismiss();
									startActivity(i);
								}
							})
	        				.setNeutralButton("Google +", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse("https://plus.google.com/101546893405114417708"));
									dialog.dismiss();
									startActivity(i);
								}
							})
	        				.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
	        				.create();
	        	}
	        }.show(fm, "welcome");
			break;
			
		case SET_PARAM_DIALOG:
		case SET_MIN_DIALOG:
		case SET_MAX_DIALOG:
		case SET_X_DIALOG:
			final int dialogID = id;
			new DialogFragment(){
				@Override
				public void onCreate(Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
					setStyle(STYLE_NORMAL, R.style.Theme_Sherlock_Dialog_FunctionInspector);
				};
				
				public View onCreateView(LayoutInflater inflater,
						android.view.ViewGroup container, Bundle savedInstanceState) {
					
					int titleId = 0;
					if (dialogID == SET_PARAM_DIALOG) titleId = R.string.param_setParam;
					if (dialogID == SET_MIN_DIALOG) titleId = R.string.param_setMinParam;
					if (dialogID == SET_MAX_DIALOG) titleId = R.string.param_setMaxParam;
					if (dialogID == SET_X_DIALOG) titleId = R.string.mode_setx_title;
					getDialog().setTitle(titleId);
					View v = inflater.inflate(R.layout.enter_number_dialog, container);

		    		final EditText et = (EditText)v.findViewById(R.id.param_et);
		    		Button bt = (Button)v.findViewById(R.id.param_bt);
		    		
		    		bt.setOnClickListener(new OnClickListener() {
		    			@Override
		    			public void onClick(View v) {
		    				if (!et.getText().toString().equals("")){
			    				double value = CalcFkts.calculate(et.getText().toString());
								if (dialogID == SET_PARAM_DIALOG) uic.setParam(value);
								if (dialogID == SET_MIN_DIALOG) uic.setMinParam(value);
								if (dialogID == SET_MAX_DIALOG) uic.setMaxParam(value);
								if (dialogID == SET_X_DIALOG) uic.setCurrentX(value);
		    				}
		    				getDialog().dismiss();
		    			}
		    		});
					return v;
				};
			}.show(fm, "param");
			break;
			
		default:
			break;
		}
	}
}
