package de.georgwiese.functionInspector.controller;

import de.georgwiese.functionInspectorLite.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

public class DialogController {

	public static final int ABOUT_DIALOG=0;
	public static final int PRO_DIALOG=1;
	public static final int WELCOME_DIALOG=2;
	public static final int TRY_DIALOG=3;
	public static final int BUY_DIALOG=4; // TODO: Implement Buy Dialog
	public static final int PIC_DIALOG=5; // TODO: Implement pic dialog
	public static final int FACEBOOK_DIALOG=6;
	
	Context c;
	FragmentManager fm;
	
	public DialogController(Context context, FragmentManager fragmentManager) {
		c = context;
		fm = fragmentManager;
	}
	
	public void showDialog(int id){
		switch (id) {
		case ABOUT_DIALOG:
	        new DialogFragment(){
	        	@Override
	        	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        		return new AlertDialog.Builder(c)
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
	        		return new AlertDialog.Builder(c)
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
	        		return new AlertDialog.Builder(c)
	        				.setTitle(R.string.pro_dialog_title)
	        				.setMessage(R.string.pro_dialog_message)
	        				.setIcon(R.drawable.icon_pro)
	        				.setPositiveButton(R.string.pro_dialog_buy, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
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
	        		return new AlertDialog.Builder(c)
    						.setTitle(R.string.pro_dialog_title)
    						.setMessage(R.string.try_dialog_message)
    						.setIcon(R.drawable.icon_pro)
    						// TODO: Implement Try Dialog Functionality
	        				.setNegativeButton(R.string.try_dialog_button, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
	        				.create();
	        	}
	        }.show(fm, "welcome");
			break;
			
		case FACEBOOK_DIALOG:
	        new DialogFragment(){
	        	@Override
	        	public Dialog onCreateDialog(Bundle savedInstanceState) {
	        		return new AlertDialog.Builder(c)
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
			
		default:
			break;
		}
	}
}
