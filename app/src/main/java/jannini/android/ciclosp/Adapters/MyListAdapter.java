package jannini.android.ciclosp.Adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import jannini.android.ciclosp.MainActivity;
import jannini.android.ciclosp.MainActivity.ViewHolder;
import jannini.android.ciclosp.R;

public class MyListAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final String[] values, values2, quantidades;
  View drawListView;
  final static ViewHolder holder = new ViewHolder();

  public MyListAdapter(Context context, String[] values, String[] values2, String[] quantidades) {
    super(context, R.layout.drawer_list_item, values);
    this.context = context;
    this.values = values;
    this.values2 = values2;
    this.quantidades = quantidades;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    drawListView = inflater.inflate(R.layout.drawer_list_item, parent, false);
    
    drawListView.setPadding(18, 16, 18, 16);
    
    holder.image = (ImageView) drawListView.findViewById(R.id.image);
    holder.text = (TextView) drawListView.findViewById(R.id.item);
    holder.description = (TextView) drawListView.findViewById(R.id.descricao);
    holder.background = drawListView;
    holder.quantidade = (TextView) drawListView.findViewById(R.id.quantidade);
    drawListView.setTag(holder);
    
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    
    holder.text.setText(values[position]);
    holder.description.setText
    (values2[position]);
    holder.quantidade.setText(quantidades[position]);

    switch (position) {
    case 0:    	
    	if (MainActivity.states[0]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.cor_ciclovia);
    	break;
    case 1:
    	if (MainActivity.states[1]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.cor_ciclofaixa);
    	break;
    case 2:
    	if (MainActivity.states[2]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.cor_ciclorrota);
    	break;
    case 3:
    	if (MainActivity.states[3]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.icon_bikesampa);

    	break;
    case 4:
    	if (MainActivity.states[4]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.icon_ciclosampa);

    	break;
    case 5:
    	if (MainActivity.states[5]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.tree_150);

    	break;
    	
    case 6:
    	if (MainActivity.states[6]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.icon_bicicletarios);

    	break;
    	
    case 7:
    	if (MainActivity.states[7]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.icon_wifi);

    	break;
    	
    case 8:
    	if (MainActivity.states[8]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
    	} else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
    	holder.image.setBackgroundResource(R.drawable.icon_alertas);
    	/*if (activeNetworkInfo != null){
        	if (!activeNetworkInfo.isConnected()){
        		holder.text.setTextColor(Color.parseColor("#888888"));
        		holder.description.setTextColor(Color.parseColor("#888888"));
        	} else {}
        } else {
        	holder.text.setTextColor(Color.parseColor("#888888"));
        	holder.description.setTextColor(Color.parseColor("#888888"));
        }*/
    	break;
    }

    return drawListView;
  }
  
} 