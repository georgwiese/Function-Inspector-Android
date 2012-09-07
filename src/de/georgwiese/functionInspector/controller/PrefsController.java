package de.georgwiese.functionInspector.controller;

import android.content.Context;

public class PrefsController {

	public static String KEY_PREFS = "prefs";
	public static String KEY_DATA  = "data";
	
	Context c;

	public PrefsController(Context context) {
		c = context;
	}
	
	// Get Pref
	public String getPrefStr(String key, String defaultV){
		return c.getSharedPreferences(KEY_PREFS, 0).getString(key, defaultV);
	}
	
	// Put Pref
	public void putPrefStr(String key, String value){
		c.getSharedPreferences(KEY_PREFS, 0).edit().putString(key, value).commit();
	}
	
	// Get Data
	public String getDataStr(String key, String defaultV){
		return c.getSharedPreferences(KEY_DATA, 0).getString(key, defaultV);
	}
	
	public float getDataFloat(String key, float defaultV){
		return c.getSharedPreferences(KEY_DATA, 0).getFloat(key, defaultV);
	}
	
	// Put Data
	public void putDataStr(String key, String value){
		c.getSharedPreferences(KEY_DATA, 0).edit().putString(key, value).commit();
	}
	
	public void putDataFloat(String key, float value){
		c.getSharedPreferences(KEY_DATA, 0).edit().putFloat(key, value).commit();
	}
}
