package de.georgwiese.functionInspectorLite;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.georgwiese.functionInspector.uiClasses.SwitchButtonSet;
import de.georgwiese.functionInspector.controller.PrefsController;
import de.georgwiese.functionInspector.controller.StateHolder;

public class Prefs extends PreferenceActivity {
	boolean isPro;
	Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=this;
		isPro = getIntent().getExtras().getBoolean(StateHolder.KEY_ISPRO);
		addPreferencesFromResource(R.xml.prefs);
		final SharedPreferences prefs = getSharedPreferences(PrefsController.KEY_PREFS, MODE_PRIVATE);
		Preference startFullscreen = findPreference("prefs_startFullscreen");
		startFullscreen.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("prefs_startFullscreen", (Boolean) newValue);
				editor.commit();
				return true;
			}
		});
		/*
		Preference color = findPreference("prefs_color");
		color.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("prefs_color", (String) newValue);
				editor.commit();
				return true;
			}
		});
		*/
		Preference zoomXY = findPreference("prefs_zoomXY");
		zoomXY.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(StateHolder.KEY_ZOOMXY, !((Boolean) newValue));
				editor.commit();
				return true;
			}
		});
		Preference folder = findPreference("prefs_folder");
		folder.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(StateHolder.KEY_FOLDER, (String) newValue);
				editor.commit();
				return true;
			}
		});
		Preference factor = findPreference("prefs_factor");
		factor.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				final Dialog d = new Dialog(mContext);
				d.setTitle(R.string.prefs_factor_title);
				LinearLayout ll = new LinearLayout(mContext);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setPadding(20, 0, 20, 20);
				LinearLayout ll2 = new LinearLayout(mContext);
				ll2.setOrientation(LinearLayout.VERTICAL);
				TextView x = new TextView(mContext);
				x.setText(R.string.prefs_factor_x);
				x.setGravity(Gravity.CENTER);
				TextView y = new TextView(mContext);
				y.setText(R.string.prefs_factor_y);
				y.setGravity(Gravity.CENTER);
				SwitchButtonSet sbx = new SwitchButtonSet(mContext, null, 4);
				sbx.setCaptions(new String[]{"1","PI","e", "DEG"});
				sbx.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				sbx.setState(prefs.getInt("prefs_factor_x", 0));
				sbx.setOnStateChangedListener(new SwitchButtonSet.OnStateChangedListener() {
					@Override
					public void onStateChanged(int newState) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt("prefs_factor_x", newState);
						editor.commit();
					}
				});
				SwitchButtonSet sby = new SwitchButtonSet(mContext, null, 4);
				sby.setState(prefs.getInt("prefs_factor_y", 0));
				sby.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				sby.setOnStateChangedListener(new SwitchButtonSet.OnStateChangedListener() {
					@Override
					public void onStateChanged(int newState) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.putInt("prefs_factor_y", newState);
						editor.commit();
					}
				});
				sby.setCaptions(new String[]{"1","PI", "e", "DEG"});
				Button ok = new Button(mContext);
				ok.setText(R.string.ok);
				ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						d.cancel();
					}
				});
				//ll.addView(description);
				ll.addView(x);
				ll.addView(sbx);
				ll.addView(y);
				ll.addView(sby);
				ll2.addView(ll);
				ll2.addView(ok);
				View v = new View(mContext);
				v.setLayoutParams(new LayoutParams(1, 1));
				//ll2.addView(v); //doesn't work otherwise for some reason...
				d.setContentView(ll2);
				d.show();
				return false;
			}
		});
		Preference survey = findPreference("prefs_survey");
		survey.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://georgwiese.blogspot.com/p/survey.html?m=1"));
				startActivity(i);
				return false;
			}
		});
		Preference email = findPreference("prefs_email");
		email.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("plain/text");
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"georgwiese@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, getString(isPro?R.string.prefs_email_subjectPro:R.string.prefs_email_subjectLite));
				startActivity(Intent.createChooser(i, getString(R.string.prefs_email_send)));
				return false;
			}
		});
		Preference blog = findPreference("prefs_blog");
		blog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://georgwiese.blogspot.com?m=1"));
				startActivity(i);
				return false;
			}
		});
		Preference facebook = findPreference("prefs_facebook");
		facebook.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://www.facebook.com/pages/Function-Inspector/207887339248278"));
				startActivity(i);
				return false;
			}
		});
		Preference pro = findPreference("prefs_pro");
		if (isPro){
			pro.setTitle(getString(R.string.prefs_proPro_title));
			pro.setSummary(getString(R.string.prefs_proPro_summary));
		}
		else{
			pro.setTitle(getString(R.string.prefs_proLite_title));
			pro.setSummary(getString(R.string.prefs_proLite_summary));
			pro.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=de.georgwiese.functionInspectorPro"));
					startActivity(intent);
					return false;
				}
			});
		}
		if (!isPro){
			startFullscreen.setDefaultValue(false);
			startFullscreen.setEnabled(false);
			zoomXY.setDefaultValue(false);
			zoomXY.setEnabled(false);
			//color.setEnabled(false);
		}
		
	}
}
