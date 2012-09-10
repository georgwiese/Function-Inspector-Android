package de.georgwiese.functionInspectorLite;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.functionInspector.controller.StateHolder;

public class TableActivity extends Activity {
	Function function1, function2, function3;
	Context mContext;
	//String[] functions;
	ArrayList<String> fktStrs;
	Double a,b,c;
	CheckBox[] cbs;
	//CheckBox cbShowPoints;
	int checkCount;
	RadioButton rbFunctionValue, rbSlope;
	boolean isPro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_activity);
		mContext=this;
		Bundle extras = getIntent().getExtras();
		
		
		fktStrs = new ArrayList<String>();
		for (int i=0; i>-1; i++){
			if (extras.getString("fkt"+Integer.toString(i)).equals("end"))
				break;
			else if (!extras.getString("fkt"+Integer.toString(i)).equals("empty"))
				fktStrs.add(extras.getString("fkt"+Integer.toString(i)));
		}
		cbs = new CheckBox[fktStrs.size()];
		checkCount=0;
		for (int i=0; i<cbs.length; i++){
			cbs[i]= new CheckBox(mContext);
			cbs[i].setText("  f"+Integer.toString(i+1)+"(x) = "+fktStrs.get(i));
			if (fktStrs.size()<4){
				cbs[i].setChecked(true);
				checkCount=fktStrs.size();
			}
			cbs[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked)
						checkCount++;
					else
						checkCount--;
					if (checkCount>2){
						for (CheckBox cb:cbs)
							if (!cb.isChecked())
								cb.setEnabled(false);}
					else
						for (CheckBox cb:cbs)
							cb.setEnabled(true);
				}
			});
		}
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.table_llCb);
		for (CheckBox cb:cbs)
			ll.addView(cb);
		
		a = extras.getDouble("paramA");
		b = extras.getDouble("paramB");
		c = extras.getDouble("paramC");
		
		final Button button = (Button) findViewById(R.id.table_activity_button);
		final EditText etStart = (EditText) findViewById(R.id.table_activity_edStart);
		final EditText etEnd = (EditText) findViewById(R.id.table_activity_edEnd);
		final EditText etInterval = (EditText) findViewById(R.id.table_activity_edInterval);
		final TableLayout table = (TableLayout) findViewById(R.id.table_activity_table);
		//cbShowPoints = (CheckBox)findViewById(R.id.cbShowPoints);

		isPro = extras.getBoolean(StateHolder.KEY_ISPRO);
		rbFunctionValue = (RadioButton) findViewById(R.id.table_activity_rbFunctionValue);
		rbSlope 		= (RadioButton) findViewById(R.id.table_activity_rbSlope);
		rbSlope.setEnabled(isPro);
		
		//if (bSlope)
			//cbShowPoints.setVisibility(View.GONE);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (CalcFkts.check(CalcFkts.formatFktString(etStart.getText().toString())) & CalcFkts.check(CalcFkts.formatFktString(etEnd.getText().toString())) & CalcFkts.check(CalcFkts.formatFktString(etInterval.getText().toString()))){
					if (CalcFkts.calculate(CalcFkts.formatFktString(etStart.getText().toString()))<CalcFkts.calculate(CalcFkts.formatFktString(etEnd.getText().toString()))){
						table.removeAllViews();
						function1=null;
						function2=null;
						function3=null;
						
						boolean bSlope = rbSlope.isChecked();
						
						for (int i = 0; i<cbs.length; i++){
							Function f;
							
							if (cbs[i].isChecked()){
								f = new Function(fktStrs.get(i));
								f.setA(a);
								f.setB(b);
								f.setC(c);
								if (function1==null) function1=f;
								else if (function2==null) function2=f;
								else if (function3==null) function3=f;
								
							}
						}
						Log.d("Developer", Boolean.toString(function1==null)+Boolean.toString(function2==null)+Boolean.toString(function3==null));
						
						ArrayList<Double> x = getXarray(CalcFkts.calculate(CalcFkts.formatFktString(etStart.getText().toString())), CalcFkts.calculate(CalcFkts.formatFktString(etEnd.getText().toString())), CalcFkts.calculate(CalcFkts.formatFktString(etInterval.getText().toString())));
						ArrayList<Double> y1 = bSlope?getSlopesArray(x, function1):getYarray(x, function1);
						ArrayList<Double> y2 = bSlope?getSlopesArray(x, function2):getYarray(x, function2);
						ArrayList<Double> y3 = bSlope?getSlopesArray(x, function3):getYarray(x, function3);

						TableRow[] rows = new TableRow[x.size()+1];
						TextView[] xs = new TextView[x.size()+1];
						TextView[] ys1 = new TextView[x.size()+1];
						TextView[] ys2 = new TextView[x.size()+1];
						TextView[] ys3 = new TextView[x.size()+1];
						DecimalFormat df = new DecimalFormat("0.00");
					
						rows[0] = new TableRow(mContext);
						rows[0].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
						rows[0].setBackgroundColor(Color.parseColor("#aaaaaa"));
						xs[0] = new TextView(mContext);
						ys1[0] = new TextView(mContext);
						ys2[0] = new TextView(mContext);
						ys3[0] = new TextView(mContext);
						xs[0].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
						ys1[0].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
						ys2[0].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
						ys3[0].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
						xs[0].setTextColor(Color.BLACK);
						ys1[0].setTextColor(Color.BLACK);
						ys2[0].setTextColor(Color.BLACK);
						ys3[0].setTextColor(Color.BLACK);
						xs[0].setPadding(5, 0, 0, 0);
						ys1[0].setPadding(5, 0, 0, 0);
						ys2[0].setPadding(5, 0, 0, 0);
						ys3[0].setPadding(5, 0, 0, 0);
						xs[0].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
						ys1[0].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
						ys2[0].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
						ys3[0].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
						xs[0].setText("x");
						int count=0;
						for (int i=0; i<cbs.length; i++){
							TextView tv;
							if (count==0) tv=ys1[0];
							else if (count==1) tv=ys2[0];
							else if (count==2) tv=ys3[0];
							else tv=null;
							
							if (tv!=null){
								if (cbs[i].isChecked()){
									if (bSlope)
										tv.setText("f'"+Integer.toString(i+1)+"(x)");
									else
										tv.setText("f"+Integer.toString(i+1)+"(x)");
									count++;
								}
							}
						}
						rows[0].addView(xs[0]);
						if (!y1.isEmpty())
							rows[0].addView(ys1[0]);
						if (!y2.isEmpty())
							rows[0].addView(ys2[0]);
						if (!y3.isEmpty())
							rows[0].addView(ys3[0]);
						table.addView(rows[0]);
						for (int i=1; i<x.size()+1; i++){
							rows[i] = new TableRow(mContext);
							rows[i].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
							xs[i] = new TextView(mContext);
							ys1[i] = new TextView(mContext);
							ys2[i] = new TextView(mContext);
							ys3[i] = new TextView(mContext);
							xs[i].setPadding(5, 0, 0, 0);
							ys1[i].setPadding(5, 0, 0, 0);
							ys2[i].setPadding(5, 0, 0, 0);
							ys3[i].setPadding(5, 0, 0, 0);
							xs[i].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
							ys1[i].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
							ys2[i].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
							ys3[i].setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT,1));
							xs[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
							ys1[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
							ys2[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
							ys3[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
							
							xs[i].setText(df.format(x.get(i-1)));
							ys1[i].setText(y1.isEmpty()?"":df.format(y1.get(i-1)));
							ys2[i].setText(y2.isEmpty()?"":df.format(y2.get(i-1)));
							ys3[i].setText(y3.isEmpty()?"":df.format(y3.get(i-1)));

							rows[i].addView(xs[i]);
							if (!y1.isEmpty())
								rows[i].addView(ys1[i]);
							if (!y2.isEmpty())
								rows[i].addView(ys2[i]);
							if (!y3.isEmpty())
								rows[i].addView(ys3[i]);
							table.addView(rows[i]);
						}
						final ScrollView sv = (ScrollView)findViewById(R.id.table_scrollView);
						sv.post(new Runnable() {
							@Override
							public void run() {
								sv.smoothScrollTo(0,table.getTop()-10);
							}
						});
						
						
					}
					else
						Toast.makeText(mContext, R.string.table_error_startEnd, Toast.LENGTH_LONG).show();
				}
				else
					Toast.makeText(mContext, R.string.table_error_invalid, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private ArrayList<Double> getXarray(double start, double end, double interval){
		ArrayList<Double> result = new ArrayList<Double>();
		for (double i=start;i<end;i+=interval){
			result.add(i);
		}
		if (result.get(result.size()-1)!=end)
			result.add(end);
		return result;
	}
	
	private ArrayList<Double> getYarray(ArrayList<Double> x, Function f){
		ArrayList<Double> result = new ArrayList<Double>();
		if (f!=null)
			for (double i:x)
				result.add(f.calculate(i));
		return result;
	}
	
	private ArrayList<Double> getSlopesArray(ArrayList<Double> x, Function f){
		ArrayList<Double> result = new ArrayList<Double>();
		if (f!=null)
			for (double i:x)
				result.add(f.slope(i));
		return result;
	}
}
