package de.georgwiese.functionInspector;

import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorLite.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MenuView extends LinearLayout {

	private LinearLayout container,empty,arrow,heading,body,bottom;
	private ScrollView sv;
	private TextView tvHeading;
	private View placeholder;
	private double arrowRatio;
	private Context mContext;
	private boolean small;
	
	public MenuView(Context context, double arrow_ratio, boolean small) {
		super(context);
		mContext=context;
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		//setBackgroundColor(Color.WHITE);
		this.small=small;
		if (small){
			setOrientation(HORIZONTAL);
			container = new LinearLayout(context);
			container.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
			container.setOrientation(VERTICAL);
			empty = new LinearLayout(context);
			empty.setLayoutParams(new LayoutParams(0, 100,3));
			addView(empty);
			addView(container);
		} else
			container=this;
		
		arrowRatio=arrow_ratio;
		
		sv=new ScrollView(context);
		sv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		sv.setBackgroundResource(R.drawable.dialog_bg);
		
		arrow= new LinearLayout(context);
		arrow.setOrientation(HORIZONTAL);
		placeholder = new View(context);
		placeholder.setBackgroundColor(Color.WHITE);
		//placeholder.setLayoutParams(new LayoutParams(25,0));
		arrow.addView(placeholder);
		ImageView iv = new ImageButton(context);
		iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_arrow));
		arrow.addView(iv);
		arrow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 6));
		container.addView(arrow);
		heading= new LinearLayout(context);
		heading.setGravity(Gravity.CENTER);
		heading.setBackgroundResource(R.drawable.dialog_heading);
		heading.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		tvHeading= new TextView(context);
		tvHeading.setTextSize(15);
		tvHeading.setGravity(Gravity.CENTER);
		tvHeading.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1));
		ImageButton btClose = new ImageButton(context);
		btClose.setBackgroundResource(R.drawable.close);
		btClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainScreen)mContext).resetButtons();
			}
		});
		heading.addView(tvHeading);
		//heading.addView(btClose);
		container.addView(heading);
		body= new LinearLayout(context);
		body.setOrientation(VERTICAL);
		body.setGravity(Gravity.CENTER);
		if (small)
			body.setPadding(2,5,2,0);
		else
			body.setPadding(10, 5, 10, 0);
		body.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		sv.addView(body);
		container.addView(sv);
		bottom= new LinearLayout(context);
		bottom.setBackgroundResource(R.drawable.dialog_bottom);
		bottom.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		container.addView(bottom);
	}

	public void setHeading(String h){
		tvHeading.setText(h);
	}
	
	public void addToBody(View v){
		body.addView(v);
	}
	public void updateArrow(float width){
		//invalidate();
		if (small)
			placeholder.setLayoutParams(new LayoutParams((int)Math.round(arrowRatio*width/4)-12,0));
		else
			placeholder.setLayoutParams(new LayoutParams((int)Math.round(arrowRatio*width)-12,0));
		//Log.d("Developer", "width: " + Float.toString(width));
		//setVisibility(VISIBLE);
	}
	public void show(){
		updateArrow(getWidth());
		//splaceholder.setLayoutParams(new LayoutParams((int)Math.round(arrowRatio*getWidth())-12,0));
		setVisibility(VISIBLE);
	}
	public void hide(){
		setVisibility(INVISIBLE);
	}
	
	@Override
	public void removeAllViews() {
		// TODO Auto-generated method stub
		body.removeAllViews();
	}
}
