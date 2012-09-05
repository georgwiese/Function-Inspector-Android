package de.georgwiese.functionInspector.uiClasses;

import java.util.ArrayList;
import java.util.Collections;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.functionInspector.controller.UIController;
import de.georgwiese.functionInspectorLite.*;
import de.georgwiese.functionInspectorPro.*;

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
	ImageButton more;
	boolean refresh;
	QuickAction qa;
	
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
				//et.requestFocus();
				et.setText("");
			}
		});
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
		
		more=new ImageButton(mContext);
		more.setImageResource(R.drawable.more);
		addView(more);
		
		//MORE
		final boolean isPro=true;
		final ActionItem dIntegral = new ActionItem();
		dIntegral.setTitle(mContext.getResources().getString(R.string.fkt_menu_integral));
		dIntegral.setIcon(mContext.getResources().getDrawable(R.drawable.integral_icon));
		dIntegral.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {/*
				Intent i = new Intent();
				i.setClassName("de.georgwiese.integralcalculator", "de.georgwiese.integralcalculator.StartPage");
				i.putExtra("fkt", et.getText().toString());
				if (fv.isCallable(i))
					mContext.startActivity(i);
				else{
		    		Dialog ic = new Dialog (mContext);
		    		ic.setContentView(R.layout.ic_layout);
		    		ic.setTitle(R.string.ic_title);
		    		ImageButton bIc = (ImageButton) ic.findViewById(R.id.bt_ic_market);
		    		bIc.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=de.georgwiese.integralcalculator"));
							mContext.startActivity(intent);
						}				
					});
		    		ic.show();}
		    		
		    		*/
				// TODO: Implement Integral
				//uic.getParams();
				//(new IntegralCalcDialog(mContext, et.getText().toString(), fv.getParams())).show();
				//if (qa!=null)
				//	qa.dismiss();
			}});
		//final ActionItem iIntegral = new ActionItem();
		//iIntegral.setTitle("Indefinite integral");
		//iIntegral.setIcon(mContext.getResources().getDrawable(R.drawable.wa_icon));
		final ActionItem save = new ActionItem();
		save.setTitle(mContext.getResources().getString(R.string.fkt_menu_save));
		save.setIcon(mContext.getResources().getDrawable(R.drawable.save_icon));
		// TODO: Implement save Functions
		/*save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isPro)
					Toast.makeText(mContext, R.string.fkt_menu_lite, Toast.LENGTH_LONG).show();
				else if (!et.getText().toString().equals("")){
					fv.addSavedFkt(et.getText().toString());
					Toast.makeText(mContext, R.string.saved_saved, Toast.LENGTH_LONG).show();}}
		});*/
		final ActionItem open = new ActionItem();
		open.setTitle(mContext.getResources().getString(R.string.fkt_menu_open));
		open.setIcon(mContext.getResources().getDrawable(R.drawable.open_icon));
		/*open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isPro)
					Toast.makeText(mContext, R.string.fkt_menu_lite, Toast.LENGTH_LONG).show();
				else{
					final String[] empty = {mContext.getResources().getString(R.string.saved_empty)};
					final String[] items = fv.getSavedFktsArray().length>0?fv.getSavedFktsArray():empty;
					AlertDialog.Builder b = new AlertDialog.Builder(mContext);
					b.setTitle(mContext.getResources().getString(R.string.saved_choose));
					b.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!items[which].equals(empty[0])){
								et.setText("");
								et.setText(items[which]);
							}
							dialog.cancel();
						}
					});
					b.create().show();}}});*/
		final ActionItem manage = new ActionItem();
		manage.setTitle(mContext.getResources().getString(R.string.fkt_menu_manage));
		manage.setIcon(mContext.getResources().getDrawable(R.drawable.manage_icon));
		/*manage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isPro)
					Toast.makeText(mContext, R.string.fkt_menu_lite, Toast.LENGTH_LONG).show();
				else{
					AlertDialog.Builder b = new AlertDialog.Builder(mContext);
					final ArrayList<Integer> del = new ArrayList<Integer>();
					final String[] empty = {mContext.getResources().getString(R.string.saved_empty)};
					final String[] items = fv.getSavedFktsArray().length>0?fv.getSavedFktsArray():empty;
					b.setTitle(R.string.saved_delete);
					b.setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							if (isChecked)
								del.add(which);
						}
					});
					b.setPositiveButton(R.string.saved_delete_bt, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Collections.sort(del);
							for (int i=del.size()-1;i>=0;i--)
								fv.deleteSavedFkt(del.get(i));
							dialog.cancel();
						}
					});
					b.create().show();}}});*/
		more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				qa=new QuickAction(v);
				qa.addActionItem(dIntegral);
				qa.addActionItem(save);
				qa.addActionItem(open);
				qa.addActionItem(manage);
				qa.show();}});
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
}
