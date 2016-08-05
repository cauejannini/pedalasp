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

import jannini.android.ciclosp.DrawerExpActivity;
import jannini.android.ciclosp.R;

public class ListAdapterDrawerExp extends ArrayAdapter<String> {
  private final Context context;
  private final String[] values, values2;
  View splashListView;
  final ViewHolder holder = new ViewHolder();

  public ListAdapterDrawerExp(Context context, String[] values, String[] values2) {
    super(context, R.layout.splash_list_item, values);
    this.context = context;
    this.values = values;
    this.values2 = values2;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      splashListView = inflater.inflate(R.layout.splash_list_item, parent, false);

      splashListView.setPadding(16, 11, 16, 11);

      holder.image = (ImageView) splashListView.findViewById(R.id.image);
      holder.text = (TextView) splashListView.findViewById(R.id.item);
      holder.description = (TextView) splashListView.findViewById(R.id.descricao);
      holder.background = splashListView;
      splashListView.setTag(holder);

      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

      holder.text.setText(values[position]);
      holder.description.setText(values2[position]);

      switch (position) {
          case 0:
              if (DrawerExpActivity.states[0]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
              } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
              holder.image.setBackgroundResource(R.drawable.ic_lanes_permanent);
              break;
          case 1:
              if (DrawerExpActivity.states[1]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
              } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
              holder.image.setBackgroundResource(R.drawable.ic_bikesampa);
              break;
          case 2:
              if (DrawerExpActivity.states[2]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
              } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
              holder.image.setBackgroundResource(R.drawable.ic_parking);
              break;
          case 3:
              if (DrawerExpActivity.states[3]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
              } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
              holder.image.setBackgroundResource(R.drawable.ic_park);
              break;
          case 4:
              if (DrawerExpActivity.states[4]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
              } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
              holder.image.setBackgroundResource(R.drawable.ic_wifi);
              break;
          case 5:
              if (DrawerExpActivity.states[5]){holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_on);
              } else {holder.background.setBackgroundResource(R.drawable.drawer_list_item_bg_off);}
              holder.image.setBackgroundResource(R.drawable.ic_alert);
              break;
      }

      return splashListView;
  }

    public class ViewHolder {
        public TextView text;
        public TextView description;
        public ImageView image;
        public View background;
    }


} 