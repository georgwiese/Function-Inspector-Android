package de.georgwiese.functionInspector;
import com.appbarbecue.AppBarbecueClient;

import android.app.Application;
import android.util.Log;


public class FunctionInspectorApplication extends Application {
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Developer", getApplicationContext().getPackageName() + "; " + 
        		getApplicationContext().getPackageName().equals("de.georgwiese.functionInspectorUnlock"));
        // The following has to be called in the onCreate() method of your Application
        // If you don't have and Application class in your app, create one like this and set it in the Manifest class
        // You get your api_key and api_secret after registering your app at http://boomcodes.com/developers
        if (getApplicationContext().getPackageName().equals("de.georgwiese.functionInspectorUnlock"))
        	AppBarbecueClient.initialize(this.getApplicationContext(),"678381d27c6e19ae9d69","9704247d38fb42e6794e91109a94a7643f403e29");
    }
}
