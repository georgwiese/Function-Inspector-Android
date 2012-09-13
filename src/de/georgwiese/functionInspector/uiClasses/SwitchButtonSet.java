package de.georgwiese.functionInspector.uiClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * This class still needs review.
 * @author Georg Wiese
 *
 */
public class SwitchButtonSet extends LinearLayout {

	public static final int COLOR_NORMAL = Color.parseColor("#1c3640");
	public static final int COLOR_ACTIVE = Color.parseColor("#3691b3");

	int buttonCount = 3;
	int state;
	Button[] bts;
	OnStateChangedListener listener;
	
	public SwitchButtonSet(Context context, AttributeSet attrs){
		this(context, attrs, 3);
	}
	
	public SwitchButtonSet(Context context, AttributeSet attrs, int count) {
		super(context, attrs);
		buttonCount=count;
		state = 0;
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		setPadding(0, 0, 0, 0);
		bts = new Button[buttonCount];
		View[] ph = new View[buttonCount-1];
		for (int i=0; i<buttonCount; i++){
			final int i2=i;
			bts[i] = new Button(context);
			bts[i].setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			bts[i].setMinWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					80, context.getResources().getDisplayMetrics()));
			bts[i].setMinHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					40, context.getResources().getDisplayMetrics()));
			bts[i].setGravity(Gravity.CENTER);
			bts[i].setPadding(0, 0, 0, 0);
			bts[i].setTextColor(Color.BLACK);
			bts[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setState(i2);}});
			addView(bts[i]);
			if (i<buttonCount-1){
				ph[i] = new View(context);
				ph[i].setLayoutParams(new LayoutParams(2,2));
				addView(ph[i]);
			}
		}
		
		setState(0);
	}
	
	public SwitchButtonSet(Context context){
		this(context, null);
	}
	public void setState(int newState){
		resetButtons();
		if (newState<buttonCount){
			if (newState == 0)
				bts[0].setBackgroundColor(COLOR_ACTIVE);
			else if (newState == buttonCount-1)
				bts[newState].setBackgroundColor(COLOR_ACTIVE);
			else
				bts[newState].setBackgroundColor(COLOR_ACTIVE);
			bts[newState].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
			bts[newState].setTextColor(Color.BLACK);
			if (state!=newState & listener!=null)
				listener.onStateChanged(newState);
				
			state = newState;
		}
	}
	
	private void resetButtons(){
		bts[0].setBackgroundColor(COLOR_NORMAL);
		bts[buttonCount-1].setBackgroundColor(COLOR_NORMAL);
		for (int i=1; i<buttonCount-1; i++){
			bts[i].setBackgroundColor(COLOR_NORMAL);
		}
		for (int i = 0; i<buttonCount; i++){
			bts[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			bts[i].setTextColor(Color.WHITE);
		}
	}
	
	public int getState(){
		return state;
	}
	
	public void setCaptions(String[] c){
		if (c.length==buttonCount){
			for (int i=0; i<buttonCount; i++)
				bts[i].setText(c[i]);
		}
	}

	public interface OnStateChangedListener {
		public void onStateChanged (int newState);
	}
	
	public void setOnStateChangedListener(OnStateChangedListener l){
		listener = l;
	}
	
	public Button getButton(int which){
		return bts[which];
	}
}