package de.georgwiese.functionInspector.uiClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import com.actionbarsherlock.internal.view.View_HasStateListenerSupport;
import com.actionbarsherlock.internal.view.View_OnAttachStateChangeListener;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import de.georgwiese.functionInspector.controller.UIController;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;

public class OverflowButton extends ImageButton implements View_HasStateListenerSupport, OnClickListener {

	public static final int THEME_DARK  = 0;
	public static final int THEME_LIGHT = 1;

	public static final int ACTIVE_COLOR = UIController.ACTIVE_COLOR;
	public static final int ACTIVE_COLOR_LIGHT = Color.parseColor("#aaaaaa");
	
	Context c;
	MenuPopup menu;
	int color, theme;

	public OverflowButton(Context context, int theme){
		this(context);
		this.theme = theme;
		if (theme == THEME_LIGHT){
			color = Color.WHITE;
			setBackgroundColor(color);
			setImageResource(R.drawable.ic_menu_moreoverflow_normal_holo_light);
		}
	}
	
	public void setMenuID(int id){
		if (menu != null)
			menu.setMenuID(id);
	}
	
	public OverflowButton(Context context){
		this(context, null);
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	
	public OverflowButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		theme = THEME_DARK;
		c = context;
		setOnClickListener(this);
		color = Color.argb(0, 0, 0, 0);
		setBackgroundColor(color);
		setImageResource(R.drawable.ic_menu_moreoverflow_normal_holo_dark);
	}
	
	public void buildMenu(String[] options,
			de.georgwiese.functionInspector.uiClasses.MenuPopup.OnMenuItemClickListener listener){
		MenuBuilder mb = new MenuBuilder(c);
		for (String option:options)
			mb.add(option);
		menu = new MenuPopup(c, mb, this, listener);
	}

	@Override
	public void addOnAttachStateChangeListener(
			View_OnAttachStateChangeListener listener) {
		if (theme == THEME_LIGHT)
			setBackgroundColor(ACTIVE_COLOR_LIGHT);
		else
			setBackgroundColor(ACTIVE_COLOR);
	}

	@Override
	public void removeOnAttachStateChangeListener(
			View_OnAttachStateChangeListener listener) {
		setBackgroundColor(color);
	}

	@Override
	public void onClick(View v) {
		if (menu != null){
			if (menu.isShowing())
				menu.dismiss();
			else
				menu.show();
		}
	}
}
