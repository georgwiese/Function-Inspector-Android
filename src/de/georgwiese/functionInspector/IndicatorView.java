package de.georgwiese.functionInspector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class IndicatorView extends View {
	int number, active;
	
	public IndicatorView(Context c, int number){
		super(c);
		this.number=number;
		active=0;
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 20));
		setBackgroundColor(Color.argb(200, 0, 0, 0));
	}
	
	public void setActive(int a){
		if (a>=0 && a<number)
			active = a;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		paint.setStrokeWidth(1);
		
		for (int i=0; i<number; i++){
			if (i==active){
				paint.setColor(Color.WHITE);
				paint.setStyle(Style.FILL_AND_STROKE);
			}
			else{
				paint.setColor(Color.GRAY);
				paint.setStyle(Style.STROKE);
			}//-(number-1)/2*20+i*20
			canvas.drawCircle(getWidth()/2-20*(number-1)/2+i*20, getHeight()/2, 6, paint);
		}
	}
}
