package jannini.android.ciclosp.NetworkRequests;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

/**
 * Created by cauejannini on 04/07/16.
 */
public interface GetRouteInterface {

    void onFinished(ArrayList<ArrayList<LatLng>> routesPaths, ArrayList<ArrayList<Double>> pathsElevations, ArrayList<Double> distances, ArrayList<Integer> durationsInSecs, ArrayList<LatLngBounds> boundsList);
}


