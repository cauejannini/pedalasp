package jannini.android.ciclosp.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

import jannini.android.ciclosp.CustomItemClasses.CyclingPath;
import jannini.android.ciclosp.MainActivity;
import jannini.android.ciclosp.R;

/**
 * Created by cauejannini on 04/08/16.
 */
public class SwipeFragment extends Fragment {

    private SwipeFragmentInteractionListener mListener;

    private static final String ARG_POSITION = "position";
    private int position;

    static Context context;

    public SwipeFragment () {
        Log.e("EMPTY CONSTRUCTOR", "YES");
    }

    public static SwipeFragment newInstance(Context c, int position) {
        SwipeFragment swipeFragment = new SwipeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        swipeFragment.setArguments(args);

        Log.e("EMPTY CONSTRUCTOR", "NO");
        context = c;

        return swipeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        } else {
            Log.e("GETARGUMENTS", "NULL");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get views
        View swipeView = inflater.inflate(R.layout.route_detail_fragment, null);
        TextView timeTV = (TextView) swipeView.findViewById(R.id.route_line_time);
        TextView distanceTV = (TextView) swipeView.findViewById(R.id.route_line_distance);
        TextView inclinationTV = (TextView) swipeView.findViewById(R.id.route_line_inclination);

        LinearLayout aChartParentView = (LinearLayout) swipeView.findViewById(R.id.aChart);

        //aChartEngine
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();renderer.setLineWidth(30);
        renderer.setColor(Color.BLUE);
        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(22);
        XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BELOW);
        fill.setColor(Color.BLUE);
        renderer.addFillOutsideLine(fill);

        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        mRenderer.setMargins(new int[]{15, 0, 0, 0});
        mRenderer.setBackgroundColor(Color.YELLOW);
        mRenderer.setGridColor(Color.GREEN);
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setShowGrid(false);
        mRenderer.setShowAxes(false);
        mRenderer.setShowLabels(false);
        mRenderer.setShowLegend(false);
        mRenderer.setDisplayValues(false);
        mRenderer.setClickEnabled(true);
        mRenderer.setXLabelsPadding(100);
        mRenderer.setYLabelsPadding(100);

        ArrayList<GraphicalView> graphViewArray = new ArrayList<>();
        // For each other cycling path stored on cyclingPathList
        for (int i = 0; i < MainActivity.cyclingPathList.size(); i++) {

            CyclingPath cp = MainActivity.cyclingPathList.get(i);

            /** Create line graph series to the current cyclingPath, if there's. Add to list. */

            if (!cp.referenceDistances.isEmpty() && !cp.pathElevation.isEmpty()) {

                // aChartEngine
                XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
                XYSeries xySeries = new XYSeries("Minha série");

                // Get cyclingPath's elevations list
                ArrayList<Double> elevations = cp.pathElevation;

                // Get cyclingPath's reference distances list
                ArrayList<Double> referenceDistances = cp.referenceDistances;
                // Add each elevation point to the elevationSeries1 variable
                for (int y = 0; y < elevations.size(); y++) {

                    //aChartEngine
                    xySeries.add(referenceDistances.get(y), elevations.get(y));

                }

                //aChartEngine
                mDataset.addSeries(xySeries);

                final GraphicalView gView = ChartFactory.getLineChartView(context, mDataset, mRenderer);
                gView.setClickable(true);
                gView.setPadding(0, 0, 0, 0);



                gView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // handle the click event on the chart

                        SeriesSelection seriesSelection = gView.getCurrentSeriesAndPoint();

                        if (seriesSelection != null) {

                            Double clickedRefDistance = seriesSelection.getXValue();

                            mListener.onSwipeFragmentInteraction(clickedRefDistance);

                        }
                    }
                });

                graphViewArray.add(gView);
            }
        }

        if (!graphViewArray.isEmpty()) {
            aChartParentView.addView(graphViewArray.get(position), 0);
        } else {
            TextView textView = (TextView) swipeView.findViewById(R.id.textViewChartWarn);
            textView.setVisibility(View.VISIBLE);
        }

        timeTV.setText(MainActivity.cyclingPathList.get(position).getEstimatedTime() + " min");
        distanceTV.setText(MainActivity.cyclingPathList.get(position).totalDistance + " km");
        inclinationTV.setText(MainActivity.cyclingPathList.get(position).maxInclination+ " °");

        return swipeView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (SwipeFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SwipeFragmentInteractionListener");
        }
    }

    public interface SwipeFragmentInteractionListener {

        void onSwipeFragmentInteraction(Double clickedRefDistance);
    }

}
