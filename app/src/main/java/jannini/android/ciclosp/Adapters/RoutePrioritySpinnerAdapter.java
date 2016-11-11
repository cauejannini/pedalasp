package jannini.android.ciclosp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import jannini.android.ciclosp.R;

/**
 * Created by cauejannini on 28/10/16.
 */

public class RoutePrioritySpinnerAdapter extends BaseAdapter {

    Context context;
    String[] items;
    LayoutInflater inflater;

    public RoutePrioritySpinnerAdapter (Context context) {
        this.context = context;
        this.items = context.getResources().getStringArray(R.array.route_priorities);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        holder = new ViewHolder();
        view = inflater.inflate(R.layout.spinner_route_priority, viewGroup, false);
        holder.tvPriority = (TextView)view.findViewById(R.id.tv_priority);
        holder.ivPriority = (ImageView) view.findViewById(R.id.iv_priority);
        holder.tvPriority.setText(items[position]);

        switch (position) {
            case 0:
                holder.ivPriority.setBackgroundResource(R.drawable.ic_prioritize_bikelane);
                break;
            case 1:
                holder.ivPriority.setBackgroundResource(R.drawable.ic_prioritize_fastest);
                break;
            case 2:
                holder.ivPriority.setBackgroundResource(R.drawable.ic_prioritize_flattest);
                break;
        }

        return view;
    }
    private static class ViewHolder{
        TextView tvPriority;
        ImageView ivPriority;
    }
}