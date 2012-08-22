package de.georgwiese.functionInspector;

import java.util.ArrayList;
import java.util.List;
import de.georgwiese.*;
import de.georgwiese.functionInspectorPro.*;
import de.georgwiese.functionInspectorLite.*;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MarketArrayAdapter extends ArrayAdapter {
	ArrayList<MarketInfo> markets;
	Context mContext;
	
	public MarketArrayAdapter(Context context, int textViewResourceId,
			ArrayList markets) {
		super(context, textViewResourceId, markets);
		this.markets=markets;
		mContext=context;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.buy_list_item, null);
            }
            if (markets.size()>position) {
                    TextView market = (TextView) v.findViewById(R.id.buy_tv_market);
                    TextView priceu = (TextView) v.findViewById(R.id.buy_tv_price_u);
                    TextView pricee = (TextView) v.findViewById(R.id.buy_tv_price_e);
                    market.setText(markets.get(position).name);
                    priceu.setText("    "+markets.get(position).priceU);
                    pricee.setText("    "+markets.get(position).priceE);
            }
            return v;
    }
}
