package jannini.android.ciclosp.Adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import jannini.android.ciclosp.MainActivity;
import jannini.android.ciclosp.R;

public class MyListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values, values2;
    View drawListView;
    final ViewHolder holder = new ViewHolder();

    public MyListAdapter(Context context, String[] values, String[] values2) {
        super(context, R.layout.drawer_list_item, values);
        this.context = context;
        this.values = values;
        this.values2 = values2;
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
        holder.btSettings = (Button) drawListView.findViewById(R.id.bt_settings);
        drawListView.setTag(holder);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        holder.text.setText(values[position]);
        holder.description.setText
        (values2[position]);
        holder.btSettings.setVisibility(View.GONE);

        switch (position) {
        case 0:
            if (MainActivity.states[0]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
            holder.image.setBackgroundResource(R.drawable.cor_ciclovia);
            holder.btSettings.setVisibility(View.VISIBLE);
            holder.btSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).selectBikeLaneTypes();
                    }
                }
            });
            break;
        case 1:
            if (MainActivity.states[1]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
            holder.image.setBackgroundResource(R.drawable.icon_bikesampa);
            holder.btSettings.setVisibility(View.VISIBLE);
            holder.btSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).selectSharingSystems();
                    }
                }
            });
            break;
        case 2:
            if (MainActivity.states[2]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
            holder.image.setBackgroundResource(R.drawable.ic_estabelecimento);
            holder.btSettings.setVisibility(View.VISIBLE);
            holder.btSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                    }
                }
            });
            break;
        case 3:
            if (MainActivity.states[3]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
            holder.image.setBackgroundResource(R.drawable.ic_parking);
            break;
        case 4:
            if (MainActivity.states[4]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
            holder.image.setBackgroundResource(R.drawable.ic_park);
            break;
        case 5:
            if (MainActivity.states[5]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
            holder.image.setBackgroundResource(R.drawable.ic_wifi);
            break;
        case 6:
            if (MainActivity.states[6]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
            holder.image.setBackgroundResource(R.drawable.ic_alert);
            break;
        }

        return drawListView;
    }

    public class ViewHolder {
        public TextView text;
        public TextView description;
        public Button btSettings;
        public ImageView image;
        public View background;
        public int position;
    }
  
} 