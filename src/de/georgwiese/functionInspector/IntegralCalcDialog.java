package de.georgwiese.functionInspector;

import java.text.DecimalFormat;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import de.georgwiese.calculationFunktions.CalcFkts;
import de.georgwiese.calculationFunktions.Function;
import de.georgwiese.functionInspectorLite.R;

public class IntegralCalcDialog extends Dialog {
	final Context mContext;
    static final int PROGRESS_DIALOG=0;
    static final int INVALID_FUNCTION=1;
    static final int HELP_DIALOG=2;
    static final int ABOUT_DIALOG=3;
    static final int FI_DIALOG=4;
    ProgressThread progressThread;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    Dialog helpDialog;
    Dialog aboutDialog;
    Dialog fiDialog;

	static final int INTEGRAL=0;
	static final int AREA=1;
	public int bt_id;
    public String function;
	public double borderLeft;
	public double borderRight;
	public int steps;
    

    EditText edFkt;
    EditText edLeft;
    EditText edRight;
    SeekBar sbAcuracy;

    Button calc_int;
    Button calc_area;
    
    double a,b,c;

	public IntegralCalcDialog(Context context, String fkt, double[] params) {
		super(context);
		mContext=context;

		a=params[0];
		b=params[1];
		c=params[2];
		
		setContentView(R.layout.ic);
		setTitle(R.string.fkt_menu_integral);
		setCanceledOnTouchOutside(true);
		
		edFkt = (EditText)findViewById(R.id.EdFkt);
        edLeft = (EditText)findViewById(R.id.EdLeft);
        edRight = (EditText)findViewById(R.id.EdRight);
        sbAcuracy = (SeekBar)findViewById(R.id.SbAcuracy);
        calc_int = (Button)findViewById(R.id.BtCalc_int);
        calc_area = (Button)findViewById(R.id.BtCalc_area);
        
        edFkt.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable arg0) {}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String fkt = CalcFkts.formatFktString(edFkt.getText().toString());
				if(CalcFkts.check(fkt))
					edFkt.setTextColor(Color.BLACK);
				else
					edFkt.setTextColor(Color.RED);
			}
        });
        edFkt.setText(fkt);
        edLeft.requestFocus();
        
        
        calc_int.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
		    	bt_id=INTEGRAL;

				if(edFkt.getText().toString().equals("")|
		    			edLeft.getText().toString().equals("")|
		    			edRight.getText().toString().equals(""))
		    		Toast.makeText(mContext, mContext.getString(R.string.ic_error_fieldsEmpty), Toast.LENGTH_LONG).show();
		    		
		    	else if (!CalcFkts.check(edLeft.getText().toString()) | !CalcFkts.check(edRight.getText().toString()) | !CalcFkts.check(CalcFkts.formatFktString(edFkt.getText().toString())))
		    		Toast.makeText(mContext, mContext.getString(R.string.ic_error_inputInvalid), Toast.LENGTH_LONG).show();
		    	else{
		    		//get input
		    		function=CalcFkts.formatFktString(edFkt.getText().toString());
		    		borderLeft=CalcFkts.calculate(CalcFkts.formatFktString(edLeft.getText().toString()));
		    		borderRight=CalcFkts.calculate(CalcFkts.formatFktString(edRight.getText().toString()));
		    		steps=(int)Math.round(Math.pow(10, (double)sbAcuracy.getProgress()/20.0+1));
		    		
		    		showDialog(PROGRESS_DIALOG);
		    	}
			}});
        
        calc_area.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
		    	bt_id=AREA;

				if(edFkt.getText().toString().equals("")|
		    			edLeft.getText().toString().equals("")|
		    			edRight.getText().toString().equals(""))
		    		Toast.makeText(mContext, mContext.getString(R.string.ic_error_fieldsEmpty), Toast.LENGTH_LONG).show();
		    		
		    	else if (!CalcFkts.check(edLeft.getText().toString()) | !CalcFkts.check(edRight.getText().toString()) | !CalcFkts.check(CalcFkts.formatFktString(edFkt.getText().toString())))
	    			Toast.makeText(mContext, mContext.getString(R.string.ic_error_inputInvalid), Toast.LENGTH_LONG).show();
		    	else{
		    		//get input
		    		function=CalcFkts.formatFktString(edFkt.getText().toString());
		    		borderLeft=CalcFkts.calculate(CalcFkts.formatFktString(edLeft.getText().toString()),1.0,1.0);
		    		borderRight=CalcFkts.calculate(CalcFkts.formatFktString(edRight.getText().toString()),1.0,1.0);
		    		steps=(int)Math.round(Math.pow(10, (double)sbAcuracy.getProgress()/20.0+1));
		    		
		    		showDialog(PROGRESS_DIALOG);
		    	}
			}});

	}
        
    private void showDialog(int id){
    	switch(id){
    	case PROGRESS_DIALOG:
    		progressDialog=new ProgressDialog(mContext);
    		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		progressDialog.setMessage(mContext.getString(R.string.ic_calculating));
    		progressDialog.setCancelable(false);
    		progressThread=new ProgressThread(handler);
    		progressThread.start();
    		progressDialog.show();
    		break;
    	}
    }
    
    final Handler handler=new Handler(){
    	public void handleMessage(Message msg){
    		int i=msg.getData().getInt("i");
    		progressDialog.setProgress(Math.round((i+1)*100/steps));
    		if (i>=steps-1){
    			progressDialog.cancel();
    			DecimalFormat df = new DecimalFormat("#.###"); 
    			
    			AlertDialog.Builder b = new AlertDialog.Builder(mContext);
    			b.setTitle(R.string.ic_result);
    			if (bt_id==INTEGRAL)
					b.setMessage(mContext.getString(R.string.ic_integral) + " " + df.format(msg.getData().getDouble("result")));
				else if (bt_id==AREA)
					b.setMessage(mContext.getString(R.string.ic_area) + " " + df.format(msg.getData().getDouble("result")));
    			b.setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
    			b.create().show();
    			
    		}
    	}
    };


		    
		    private class ProgressThread extends Thread{
		    	Handler mHandler;
		    	
		    	ProgressThread(Handler h){
		    		mHandler=h;
		    	}
		    	
		    	public void run(){
		    		Function function2 = new Function(function,a,b,c);
		    		double temp;
		    		double result= 0.0;
		    			for (int i=0;i<steps;i++){
		    				temp=(borderRight-borderLeft)/steps*function2.calculate(borderLeft+(borderRight-borderLeft)/(2*steps)+i*(borderRight-borderLeft)/steps);
		    				if (bt_id==INTEGRAL)
		    					result+=temp;
		        			else if (bt_id==AREA)
		        				result+=Math.abs(temp);
							if(i%Math.round(steps*0.05)==0|i==(steps-1)){
		    					Message msg=mHandler.obtainMessage();
		    					Bundle b = new Bundle();
		    					b.putInt("i", i);
		    					b.putDouble("result", result);
		    					msg.setData(b);
		    					mHandler.sendMessage(msg);
		    				}
		    			}
		    	}
		    }
}
