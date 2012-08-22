package de.georgwiese.functionInspector.uiClasses;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.georgwiese.functionInspectorLite.R;

public class MenuView extends LinearLayout {
	
	LinearLayout body;

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		inflate(context, R.layout.menu, (ViewGroup)getRootView());
		body = (LinearLayout) getRootView().findViewById(R.id.menu_body);
		String title = context.getResources().getString(attrs.getAttributeResourceValue("http://schemas.android.com/apk/lib/de.georgwiese.functionInspector", "menuTitle", 0));
		if (title != null)
			((TextView) getRootView().findViewById(R.id.menu_heading)).setText(title);
	}

	@Override
	public void addView(View child) {
		// Because inflate() calls this method, it will just call super.addView() at this point.
		// If this Menu has already been initialized (body != null), addView() means adding
		// views to the menu's body.
		if (body != null)
			body.addView(child);
		else
			super.addView(child);
	}

	@Override
	public void addView(View child, int index) {
		if (body != null)
			body.addView(child, index);
		else
			super.addView(child, index);
	}
	
	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		if (body != null)
			body.addView(child, index, params);
		else
			super.addView(child, index, params);
	}
	
	@Override
	public void addView(View child, int width, int height) {
		if (body != null)
			body.addView(child, width, height);
		else
			super.addView(child, width, height);
	}
	
	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		if (body != null)
			body.addView(child, params);
		else
			super.addView(child, params);
	}
}
