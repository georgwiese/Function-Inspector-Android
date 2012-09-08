package de.georgwiese.functionInspector.uiClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;

/**
 * This class still needs review.
 * @author Georg Wiese
 *
 */
public class SwitchButtonSet extends LinearLayout {

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
		bts = new Button[buttonCount];
		View[] ph = new View[buttonCount-1];
		for (int i=0; i<buttonCount; i++){
			final int i2=i;
			bts[i] = new Button(context);
			bts[i].setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			bts[i].setMinWidth(50);
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
				bts[0].setBackgroundResource(R.drawable.sb_left_pressed);
			else if (newState == buttonCount-1)
				bts[newState].setBackgroundResource(R.drawable.sb_right_pressed);
			else
				bts[newState].setBackgroundResource(R.drawable.sb_middle_pressed);
			bts[newState].setTextSize(22);
			if (state!=newState & listener!=null)
				listener.onStateChanged(newState);
				
			state = newState;
		}
	}
	
	private void resetButtons(){
		bts[0].setBackgroundResource(R.drawable.sb_left);
		bts[buttonCount-1].setBackgroundResource(R.drawable.sb_right);
		for (int i=1; i<buttonCount-1; i++){
			bts[i].setBackgroundResource(R.drawable.sb_middle);
		}
		for (int i = 0; i<buttonCount; i++)
			bts[i].setTextSize(16);
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