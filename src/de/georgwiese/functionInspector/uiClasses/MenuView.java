package de.georgwiese.functionInspector.uiClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;
import de.georgwiese.functionInspectorSpecial.*;

/**
 * Class that encapsulates an empty menu with title
 * and content box.
 * @author Georg Wiese
 *
 */
public class MenuView extends LinearLayout {
	
	public static final int BODY_BACKGROUND_COLOR = Color.argb(220, 217, 249, 255); 
	
	LinearLayout body;

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Inflate the menu Layout
		inflate(context, R.layout.menu, (ViewGroup)getRootView());
		// Find body view and store it
		body = (LinearLayout) getRootView().findViewById(R.id.menu_body);
		// Set title and gravity
		String title = context.getResources().getString(attrs.getAttributeResourceValue("http://schemas.android.com/apk/lib/de.georgwiese.functionInspector", "menuTitle", 0));
		TextView tvTitle = ((TextView) getRootView().findViewById(R.id.menu_heading));
		if (title != null)
			tvTitle.setText(title);
		body.setGravity(attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "gravity", Gravity.LEFT));
		body.setBackgroundColor(BODY_BACKGROUND_COLOR);
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
	
	@Override
	public void removeAllViews() {
		if (body != null)
			body.removeAllViews();
		else
			super.removeAllViews();
	}
}
