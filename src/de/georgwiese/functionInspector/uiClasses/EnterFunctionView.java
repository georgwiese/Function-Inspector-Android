package de.georgwiese.functionInspector.uiClasses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.functionInspector.controller.UIController;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;

/**
 * THIS CLASS STILL NEEDS REVIEW
 * @author Georg Wiese
 *
 */
public class EnterFunctionView extends LinearLayout {

	int nr;
	Context mContext;
	MyKeyboardView kv;
	final UIController uic;
	int color;
	Paint p;
	float scale;
	Button close;
	TextView tv;
	EditText et;
	OverflowButton overflow;
	boolean refresh;
	
	public EnterFunctionView(Context context, MyKeyboardView keyboardView, UIController uic, String f) {
		this(context, keyboardView, uic);
		refresh=false;
		et.setText(f);
		refresh=true;
	}
	
	public EnterFunctionView(Context context, MyKeyboardView keyboardView, UIController uic) {
		super(context, null);
		kv=keyboardView;
		mContext=context;
		this.uic = uic;
		scale=context.getResources().getDisplayMetrics().density;
		p = new Paint();
		p.setAntiAlias(true);
		p.setTextSize(15*scale);
		p.setTextAlign(Align.CENTER);
		nr=1;
		refresh=true;
		
		setOrientation(HORIZONTAL);
		
		close= new Button(mContext);
		close.setText("X");
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et.requestFocus();
				et.setText("");
			}
		});
		close.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		close.setPadding(10, close.getPaddingTop(), 10, close.getPaddingBottom());
		addView(close);
		
		tv = new TextView(mContext){
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				canvas.drawText(Integer.toString(nr), scale*10, scale*30, p);
			};
		};
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
		tv.setText("f (x)=");
		addView(tv);
		
		et = new EditText(mContext);
		et.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
		et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		et.setFocusableInTouchMode(true);
		et.setCursorVisible(true);
		et.invalidate();
		et.setClickable(true);

	    //disable keypad
		// TODO: Keyboard still pops up when App is brought back to foreground and EditText is focussed.
	    et.setOnTouchListener(new OnTouchListener(){
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {

	            int inType = et.getInputType(); // backup the input type
	            et.setInputType(InputType.TYPE_NULL); // disable soft input
	            et.onTouchEvent(event); // call native handler
	            et.setInputType(inType); // restore input type
	            
	            /**
	             * ATTENTION: The following code will set the cursor position
	             * and request focus. This is actually supposed to be done in
	             * the onTouch() method, but not if input type is TYPE_NULL.
	             * The formula for calculating the cursor position is assuming
	             * that every character has the same width which makes it kind
	             * of inaccurate.
	             */
	            Paint p = new Paint();
	            p.setTextSize(et.getTextSize());
	            et.setSelection(Math.max(0, Math.min(et.getEditableText().length(),
	            		Math.round(et.getEditableText().length() *
	            		(event.getX() - et.getPaddingLeft()) / p.measureText(et.getEditableText().toString())))));
	            et.requestFocus();
	            return  true; // consume touch even
	        }
	        });
            
		final UIController uicF = uic;
		et.addTextChangedListener(new TextWatcher() {
			int lengthBefore;;
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				lengthBefore=count;
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (refresh){
					boolean correct = CalcFkts.check(CalcFkts.formatFktString(s.toString()));
					if (lengthBefore==0 | s.toString().equals(""))
						uicF.updateEfvs();
					if (s.toString().equals("") | correct)
						uicF.updateFkts();
					
					
					if (correct)
						et.setTextColor(Color.BLACK);
					else
						et.setTextColor(Color.RED);
				}
			}
		});
		et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					kv.setEditText((EditText)v);
					uicF.setKBVisible(true);
				}
			}
		});
		addView(et);
		
		overflow = new OverflowButton(mContext, OverflowButton.THEME_LIGHT);
		String[] options = new String[4];
		options[0] =  mContext.getResources().getString(R.string.fkt_menu_integral);
		options[1] =  mContext.getResources().getString(R.string.fkt_menu_save);
		options[2] =  mContext.getResources().getString(R.string.fkt_menu_open);
		options[3] =  mContext.getResources().getString(R.string.fkt_menu_manage);
		overflow.buildMenu(options, uic);
		addView(overflow);
	}
	
	public void setId(int id){
		overflow.setMenuID(id);
	}
	
	public void setColor(int c){
		color=c;
		tv.setTextColor(c);
		p.setColor(c);
	}

	public void setNr(int nr){
		this.nr=nr;
	}
	
	public EditText getEt(){
		return et;
	}
	
	public String getText(){
		return et.getEditableText().toString();
	}
	
	public void setText(String text){
		et.setText(text);
	}
}
