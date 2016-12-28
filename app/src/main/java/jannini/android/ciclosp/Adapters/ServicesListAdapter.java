package jannini.android.ciclosp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import jannini.android.ciclosp.R;

public class ServicesListAdapter extends ArrayAdapter<String> {
    private final Context context;
    String[] servicesStrings;
    private final ViewHolder holder = new ViewHolder();

    public ServicesListAdapter(Context context, String[] stringArray) {
        super(context, R.layout.list_item_services, stringArray);
        this.context = context;
        this.servicesStrings = stringArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View serviceListItem = inflater.inflate(R.layout.list_item_services, parent, false);

        //holder.ivService = (ImageView) serviceListItem.findViewById(R.id.iv_service);
        //holder.ivService.setBackgroundResource(Constant.PlaceServicesImages[position]);
        holder.tvService = (TextView) serviceListItem.findViewById(R.id.tv_service);
        holder.tvService.setText(servicesStrings[position]);
        serviceListItem.setTag(holder);

        if(((ListView)parent).isItemChecked(position)) {
            serviceListItem.setBackgroundResource(R.drawable.tb_waterblue_c);
        }

        switch (position) {
            case 8:
                //holder.ivService.setVisibility(View.GONE);
                //holder.tvService.setVisibility(View.GONE);
                holder.etOtherService = (EditText) serviceListItem.findViewById(R.id.et_other_service);
                holder.tvOtherService = (TextView) serviceListItem.findViewById(R.id.tv_other_service);
                holder.etOtherService.setVisibility(View.VISIBLE);
                holder.tvOtherService.setVisibility(View.VISIBLE);
                break;
        }

        return serviceListItem;
    }

    private class ViewHolder {
        TextView tvService, tvOtherService;
        EditText etOtherService;
        ImageView ivService;
    }
  
} 