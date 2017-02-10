package jannini.android.ciclosp.Adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.MainActivity;
import jannini.android.ciclosp.R;

public class MyListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values, values2;
    private final String userGreeting;
    private final ViewHolder holder = new ViewHolder();

    public MyListAdapter(Context context, String[] values, String[] values2, String userGreeting) {
        super(context, R.layout.drawer_list_item, values);
        this.context = context;
        this.values = values;
        this.values2 = values2;
        this.userGreeting = userGreeting;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View drawListView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        drawListView.setPadding(18, 16, 18, 16);

        holder.image = (ImageView) drawListView.findViewById(R.id.image);
        holder.text = (TextView) drawListView.findViewById(R.id.item);
        holder.description = (TextView) drawListView.findViewById(R.id.descricao);
        holder.background = drawListView;
        holder.btSettings = (Button) drawListView.findViewById(R.id.bt_settings);
        holder.progressBar = (ProgressBar) drawListView.findViewById(R.id.pb_loading_listitem);
        drawListView.setTag(holder);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        holder.text.setText(values[position]);
        holder.description.setText(values2[position]);
        holder.btSettings.setVisibility(View.GONE);

        switch (position) {
            case Constant.LISTPOS_BIKE_LANE:
                if (Constant.States[0]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
                holder.image.setBackgroundResource(R.drawable.ic_lanes);
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
            case Constant.LISTPOS_PLACES:
                if (Constant.States[1]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
                holder.image.setBackgroundResource(R.drawable.btic_add_estabelecimento);
                holder.btSettings.setVisibility(View.VISIBLE);
                if (MainActivity.placesIsLoading) { holder.progressBar.setVisibility(View.VISIBLE);} else {holder.progressBar.setVisibility(View.GONE);}
                holder.btSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).selectPlacesCategories();
                        }
                    }
                });
                break;
            case Constant.LISTPOS_SHARING_STATIONS:
                if (Constant.States[2]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
                holder.image.setBackgroundResource(R.drawable.ic_bikesampa);
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
            case Constant.LISTPOS_PARKING:
                if (Constant.States[3]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
                holder.image.setBackgroundResource(R.drawable.ic_parking);
                break;
            case Constant.LISTPOS_PARKS:
                if (Constant.States[4]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
                holder.image.setBackgroundResource(R.drawable.ic_park);
                break;
            case Constant.LISTPOS_WIFI:
                if (Constant.States[5]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
                holder.image.setBackgroundResource(R.drawable.ic_wifi);
                break;
            case Constant.LISTPOS_ALERTS:
                if (Constant.States[6]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
                holder.image.setBackgroundResource(R.drawable.ic_alert);
                break;
            case Constant.LISTPOS_MY_ACCOUNT:
                drawListView.setPadding(18, 25, 18, 25);
                holder.text.setText(userGreeting);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.description.setTextColor(context.getColor(R.color.water_blue));
                }
                holder.image.setVisibility(View.GONE);
                break;
            case Constant.LISTPOS_WRITE_FOR_US:
                drawListView.setPadding(18, 25, 18, 25);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.text.setTextColor(context.getColor(R.color.water_blue));
                }
                holder.image.setVisibility(View.GONE);
                holder.description.setVisibility(View.GONE);
                break;
        }

        return drawListView;
    }

    private class ViewHolder {
        public TextView text;
        TextView description;
        Button btSettings;
        ImageView image;
        ProgressBar progressBar;
        public View background;
    }
  
} 