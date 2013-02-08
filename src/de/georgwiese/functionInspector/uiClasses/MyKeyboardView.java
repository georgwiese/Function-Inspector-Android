package de.georgwiese.functionInspector.uiClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.georgwiese.functionInspector.controller.UIController;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorUnlock.*;
import de.georgwiese.functionInspectorSpecial.*;

/**
 * THIS CLASS STILL NEEDS REVIEW
 * @author Georg Wiese
 *
 */
public class MyKeyboardView extends LinearLayout {
	
	UIController uic;

	final int KEYBOARD_LEFT=-1;
	final int KEYBOARD_RIGHT=-2;
	TextView advice;
	IndicatorView iv;
	LinearLayout[] pages = new LinearLayout[2];
	LinearLayout[][] ll_hor= new LinearLayout[2][4];
	Button[][] buttons1 = new Button[7][4];
	View[][] ph1 = new View[6][4];
	Button[][] buttons2 = new Button[4][4];
	ImageButton del, down, info;
	EditText et;
	private Context mContext;
	private int currentLayout;
	private final int LAYOUT_COUNT=2;
	private SimpleOnGestureListener swipeListener;
	private GestureDetector gestureDetector;
	final String[][] labels1 = {{"7", "4", "1", "."},
			 {"8", "5", "2", "0"},
			 {"9", "6", "3", "x"},
			 {"+", "-", "*", "/"},
			 {"(", ")", "sqrt", "^"},
			 {"", "a", "b", "c"},
			 {"-","\u03C0","e","-"}};
	final String[][] labels2 = {{"abs", "sin", "cos", "tan"},
			 {"ln", "asin", "acos", "atan"},
			 {"log", "sinh", "cosh", "tanh"},
			 {"", "asinh", "acosh", "atanh"}};
	
	public MyKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		
		swipeListener = new SimpleOnGestureListener(){
			private static final int SWIPE_MIN_DISTANCE = 120;
			private static final int SWIPE_THRESHOLD_VELOCITY = 200;
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
					setNextLayout(e1.getX()<e2.getX()?KEYBOARD_RIGHT:KEYBOARD_LEFT);
					return true;
				}
				else
					return false;	
			}
		};
		gestureDetector = new GestureDetector(swipeListener);
		
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		setOrientation(VERTICAL);
		setBackgroundColor(Color.BLACK);
		// Do nothing on click, not even trigger the onClick() method
		// of the underlying view...
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {}
		});
		
		if (mContext.getSharedPreferences("data", Activity.MODE_PRIVATE).getBoolean("showKeyboardAdvice", true)){
			advice = new TextView(mContext);
			advice.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			advice.setText(R.string.keyboard_advice);
			advice.setBackgroundColor(Color.argb(200, 0, 0, 0));
			advice.setGravity(Gravity.CENTER);
			advice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			advice.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					hideAdvice();
				}
			});
			addView(advice);
		}
		
		iv = new IndicatorView(mContext, 2);
		addView(iv);
		
		for (int i=0; i<pages.length; i++){
			pages[i] = new LinearLayout(mContext);
			pages[i].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			pages[i].setOrientation(VERTICAL);
		}
		
		for (int j = 0; j<pages.length; j++){
			for (int i=0;i<4;i++){
				ll_hor[j][i]=new LinearLayout(context);
				ll_hor[j][i].setOrientation(HORIZONTAL);
				//ll_hor[j][i].setPadding(5, 0, 5, 5);
				ll_hor[j][i].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				pages[j].addView(ll_hor[j][i]);
			}
		}
		del = new ImageButton(mContext);
		del.setImageResource(R.drawable.key_icon_del);
		del.setBackgroundResource(R.drawable.key_light);
		del.setLayoutParams(new LayoutParams(0, LayoutParams.FILL_PARENT,2));
		del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (et.getSelectionStart()>0)
				et.getText().delete(et.getSelectionStart()-1, et.getSelectionStart());
		}});
		down = new ImageButton(mContext);
		down.setImageResource(R.drawable.key_icon_down);
		down.setBackgroundResource(R.drawable.key_light);
		down.setLayoutParams(new LayoutParams(0, LayoutParams.FILL_PARENT,1));
		
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (uic != null)
					uic.focusNextEfv();
		}});
		info = new ImageButton(mContext);
		info.setImageResource(R.drawable.key_icon_info);
		info.setBackgroundResource(R.drawable.key_light);
		info.setLayoutParams(new LayoutParams(0, LayoutParams.FILL_PARENT,1));
		info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mContext)
				.setTitle(R.string.keyboard_info_title)
				.setMessage(R.string.keyboard_info_message)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).create().show();
		}});

		for (int x=0; x<7;x++){
			for (int y=0; y<4; y++){
				if (x != 0){
					ph1[x-1][y] = new View(mContext);
					ph1[x-1][y].setLayoutParams(new LayoutParams(5, 0));
				}
				
				final int x2=x;
				final int y2=y;
				buttons1[x][y]= new Button(context){
					@Override
					public boolean onTouchEvent(MotionEvent event) {
						gestureDetector.onTouchEvent(event);
						return super.onTouchEvent(event);
					}
				};
				buttons1[x][y].setText(labels1[x][y]);
				buttons1[x][y].setPadding(0, 0, 0, 0);
				buttons1[x][y].setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
				if ((x==2 && y==3))
					buttons1[x][y].setBackgroundResource(R.drawable.key_light);
				else
					buttons1[x][y].setBackgroundResource(R.drawable.key);
				buttons1[x][y].setTextColor(Color.WHITE);
				buttons1[x][y].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				buttons1[x][y].setHapticFeedbackEnabled(true);
				buttons1[x][y].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (et!=null){
							et.getEditableText().insert(et.getSelectionStart(), labels1[x2][y2]);
							
							if ((x2==4 & y2==2))
								et.getEditableText().insert(et.getSelectionStart(), "(");
							
							// Stay in Focus
							et.requestFocus();
						}
					}
				});
			}
		}
		for (int x=0; x<4;x++){
			for (int y=0; y<4; y++){
				final int x2=x;
				final int y2=y;
				buttons2[x][y]= new Button(context){
					@Override
					public boolean onTouchEvent(MotionEvent event) {
						gestureDetector.onTouchEvent(event);
						return super.onTouchEvent(event);
					}
				};
				buttons2[x][y].setPadding(0, 0, 0, 0);
				buttons2[x][y].setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
				buttons2[x][y].setBackgroundResource(R.drawable.key);
				buttons2[x][y].setTextColor(Color.WHITE);
				buttons2[x][y].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				buttons2[x][y].setHapticFeedbackEnabled(true);
				buttons2[x][y].setText(labels2[x][y]);
				buttons2[x][y].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (et!=null){
							et.getEditableText().insert(et.getSelectionStart(), labels2[x2][y2]);
							
							et.getEditableText().insert(et.getSelectionStart(), "(");
							
							setNextLayout(0);
						}
					}
				});
			}
		}
		for (int y=0; y<4; y++){
			for (int x=0; x<7;x++){
				if (!((x==5) && (y==0))){
					//if (x != 0)
					//	ll_hor[0][y].addView(ph1[x-1][y]);
					if (x==6 && y==0)
						ll_hor[0][y].addView(del);
					else if (x==6 && y==3)
						ll_hor[0][y].addView(down);
					else
						ll_hor[0][y].addView(buttons1[x][y]);
				}
			}
		}
		for (int y=0; y<4; y++){
			for (int x=0; x<4;x++){
				if (x==3 && y==0)
					ll_hor[1][y].addView(info);
				else
					ll_hor[1][y].addView(buttons2[x][y]);
			}
		}

		currentLayout=0;
		addView(pages[currentLayout]);
		addView(pages[1]);
		pages[0].setVisibility(VISIBLE);
		pages[1].setVisibility(GONE);
	}
	
	/**
	 * Needs to be called right after initialization
	 * @param uic
	 */
	public void setUIController(UIController uic) {
		this.uic = uic;
	}
	
	private void setNextLayout(int id){
		Animation out = AnimationUtils.loadAnimation(mContext, id==KEYBOARD_RIGHT? R.anim.out_right : R.anim.out_left);
		Animation in = AnimationUtils.loadAnimation(mContext, id==KEYBOARD_RIGHT? R.anim.in_right :  R.anim.in_left);
		in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				hideAdvice();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
		});
		
		int nextLayout=currentLayout;
		if (id==KEYBOARD_RIGHT) nextLayout--;
		else if (id==KEYBOARD_LEFT) nextLayout++;
		else nextLayout=id;
		if (nextLayout<0)
			nextLayout=LAYOUT_COUNT-1;
		else if (nextLayout==LAYOUT_COUNT)
			nextLayout=0;
		
		if (id==KEYBOARD_LEFT || id==KEYBOARD_RIGHT)
			pages[currentLayout].startAnimation(out);
		pages[currentLayout].setVisibility(GONE);
		pages[nextLayout].setVisibility(VISIBLE);
		if (id==KEYBOARD_LEFT || id==KEYBOARD_RIGHT)
			pages[nextLayout].startAnimation(in);
		currentLayout=nextLayout;
		iv.setActive(currentLayout);
	}
	public void setEditText(EditText edittext){
		et=edittext;
	}

	@Override
	public void setVisibility(int visibility) {
		boolean change=getVisibility()!=visibility;
		super.setVisibility(visibility);
		Animation in = AnimationUtils.loadAnimation(mContext, R.anim.keyboard_in);
		Animation out = AnimationUtils.loadAnimation(mContext, R.anim.keyboard_out);
		if (visibility==VISIBLE&&change)
			startAnimation(in);
		if (visibility==GONE&&change)
			startAnimation(out);
	}
	
	private void hideAdvice(){
		removeView(advice);
		Editor editor = mContext.getSharedPreferences("data", Activity.MODE_PRIVATE).edit();
		editor.putBoolean("showKeyboardAdvice", false);
		editor.commit();
	}
}
