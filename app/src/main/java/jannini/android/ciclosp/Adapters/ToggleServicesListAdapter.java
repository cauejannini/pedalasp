package jannini.android.ciclosp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;

public class ToggleServicesListAdapter extends ArrayAdapter<String> {
    private final Context context;
    String[] servicesStrings;
    ArrayList<Integer> idsArray;
    private final ToggleServicesViewHolder holder = new ToggleServicesViewHolder();

    public ToggleServicesListAdapter(Context context, String[] stringArray, ArrayList<Integer> idsArray) {
        super(context, R.layout.list_item_services, stringArray);
        this.context = context;
        this.servicesStrings = stringArray;
        this.idsArray = idsArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View serviceListItem = inflater.inflate(R.layout.list_toggle_item_services, parent, false);

        holder.ivService = (ImageView) serviceListItem.findViewById(R.id.iv_toggle_service);
        holder.ivService.setImageBitmap(Constant.mapCategoriesIcons.get(idsArray.get(position)));
        holder.tvService = (TextView) serviceListItem.findViewById(R.id.tv_toggle_service);
        holder.tvService.setText(servicesStrings[position]);
        holder.tbService = (ToggleButton) serviceListItem.findViewById(R.id.tb_toggle_service);
        serviceListItem.setTag(holder);

        holder.tbService.setChecked(Constant.PlaceCategoriesStates.get(idsArray.get(position)));

        return serviceListItem;
    }

    public static class ToggleServicesViewHolder {
        int categoryId;
        ImageView ivService;
        TextView tvService;
        ToggleButton tbService;
    }
  
} 