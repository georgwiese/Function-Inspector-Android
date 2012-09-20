package de.georgwiese.functionInspector.uiClasses;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;

import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuPopupHelper;

public class MenuPopup extends MenuPopupHelper {

	int menuID;
	OnMenuItemClickListener onMenuItemClickListener;
	
	public MenuPopup(Context context, MenuBuilder menu, View anchorView) {
		super(context, menu, anchorView);
		menuID = 0;
	}
	
	public MenuPopup(Context context, MenuBuilder menu, View anchorView,
			OnMenuItemClickListener listener) {
		this(context, menu, anchorView);
		setOnMenuItemClickListener(listener);
	}
	
	public void setMenuID(int menuID) {
		this.menuID = menuID;
	}
	
	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		super.onItemClick(parent, view, position, id);
		if (onMenuItemClickListener != null)
			onMenuItemClickListener.onMenuItemClick(menuID, position);
	}
	
	public interface OnMenuItemClickListener{
		public void onMenuItemClick(int menuID, int itemID);
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// Undo what they have done in the original class,
		// so the menu is not dismissed if menu button is
		// up.
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU) {
            dismiss();
            return true;
        }
		return false;
	}
}
