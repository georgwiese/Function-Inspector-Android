package de.georgwiese.functionInspector;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FunctionScrollView extends ScrollView {

	Context mContext;
	int maxHeight;
	int textSize;
	LinearLayout ll;
	FunctionTextView heading;
	FunctionTextView[] entries = new FunctionTextView[100];
	ArrayList<FunctionTextView> entriesList;
	
	public FunctionScrollView(Context context, int maxHeight) {
		super(context);
		mContext=context;
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, (int)mContext.getResources().getDisplayMetrics().density*100));
		
		setBackgroundColor(Color.BLACK);
		this.maxHeight=maxHeight;
		
		ll = new LinearLayout(context);
		//ll.setPadding(0, 0, 0, 0);
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		addView(ll);
		
		heading = new FunctionTextView("", Color.WHITE);
		heading.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		
		textSize=15;
		for (int i=0; i<100; i++){
			entries[i] = new FunctionTextView("", Color.BLUE);
		}
		entriesList = new ArrayList<FunctionTextView>();
	}
	
	public void setHeadingColor(int c){
		heading.setTextColor(c);
	}
	
	private void updateBody(){
		ll.removeAllViews();
		ll.addView(heading);
		//ll.addView(new FunctionTextView("asjha", Color.RED));
		//entries.add(new FunctionTextView("Blabla", Color.GREEN));
		for (FunctionTextView tv : entriesList)
			//if (!tv.getText().toString().equals(""))
				ll.addView(tv);
		invalidate();
		//setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 200));
	}
	
	public void clear(){
		//heading.setText("");
		//for (int i=0; i<100; i++)
			//entries[i].setText("");
		entriesList.clear();
		updateBody();
	}
	
	public void setHeading(String heading){
		this.heading.setText(heading);
		updateBody();
	}
	
	public void setEntryTextSize(int size){
		textSize = size;
	}
	
	public void addEntry(String text, int color){
		/*for (int i=0; i<100; i++){
			if (entries[i].getText().toString().equals("")){
				entries[i].setText(text);
				break;
			}
		}*/
		entries[entriesList.size()].setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		entries[entriesList.size()].setText(text);
		entries[entriesList.size()].setTextColor(color);
		entriesList.add(entries[entriesList.size()]);
		//entries.add(new FunctionTextView(text, color));
		updateBody();
	}
	
	public int getEntrySize(){
		return entriesList.size();
	}
	
	private class FunctionTextView extends TextView{
		public FunctionTextView(String text, int color){
			super(mContext);
			setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			setTextColor(color);
			//setPadding(0, 0, 0, 0);
			setText(text);
			setGravity(Gravity.CENTER_HORIZONTAL);
		}
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		super.setOnClickListener(l);
		ll.setOnClickListener(l);
	}
	/*
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, Math.min(100, heightMeasureSpec));
	}*/
}
