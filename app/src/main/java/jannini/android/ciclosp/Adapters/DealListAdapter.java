package jannini.android.ciclosp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import jannini.android.ciclosp.R;

public class DealListAdapter extends ArrayAdapter<String> {
    private final Context context;
    String[] featuredTitles, secondLines, thirdLines, blueLines;
    private final ViewHolder holder = new ViewHolder();

    public DealListAdapter(Context context, String[] featuredTitles, String[] secondLines, String[] thirdLines, String[] blueLines) {
        super(context, R.layout.list_item_services, featuredTitles);
        this.context = context;
        this.featuredTitles = featuredTitles;
        this.secondLines = secondLines;
        this.thirdLines = thirdLines;
        this.blueLines = blueLines;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View featuredListItem = inflater.inflate(R.layout.list_item_featured, parent, false);

        holder.tvTitle = (TextView) featuredListItem.findViewById(R.id.tv_featured_list_title);
        holder.tvSecondLine = (TextView) featuredListItem.findViewById(R.id.tv_featured_list_second_line);
        holder.tvThirdLine = (TextView) featuredListItem.findViewById(R.id.tv_featured_list_third_line);
        holder.tvBlueLine = (TextView) featuredListItem.findViewById(R.id.tv_featured_list_blue_line);

        holder.tvTitle.setText(featuredTitles[position]);
        holder.tvSecondLine.setText(secondLines[position]);
        holder.tvThirdLine.setText(thirdLines[position]);
        holder.tvBlueLine.setText(blueLines[position]);

        if (thirdLines[position].equals("")) {
            holder.tvThirdLine.setVisibility(View.INVISIBLE);
        } else {
            holder.tvThirdLine.setVisibility(View.VISIBLE);
        }

        featuredListItem.setTag(holder);

        return featuredListItem;
    }

    private class ViewHolder {
        TextView tvTitle, tvSecondLine, tvThirdLine, tvBlueLine;
    }
  
} 