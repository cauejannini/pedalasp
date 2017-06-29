package jannini.android.ciclosp.Models;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.GetRouteInterface;

public class Route {

    public Route() {}

    public static void getRoute (final Context context, final int requestId, final LatLng start, final LatLng end, final ArrayList<BikeLane> listBikeLanes, final GoogleMap gMap, final GetRouteInterface handlerInterface) {

        final ArrayList<CyclingPath> cyclingPathReturnList = new ArrayList<>();

        final ArrayList<ArrayList<LatLng>> pathRoutes = new ArrayList<>();
        final ArrayList<Double> distances = new ArrayList<>();
        final ArrayList<Integer> durations = new ArrayList<>();
        final ArrayList<ArrayList<Double>> elevationLists = new ArrayList<>();
        final ArrayList<LatLngBounds> boundsList = new ArrayList<>();

        final ArrayList<String> encodedPaths = new ArrayList<>();

        Calls.getDirections(start, end, new CallHandler() {
            @Override
            public void onSuccess(int responseCode, String response) {

                try {
                    JSONArray jarray = new JSONArray(response);

                    for (int x = 0 ; x< jarray.length(); x++) {

                        JSONObject job = jarray.getJSONObject(x);
                        JSONArray arrayOfRoutes = job.getJSONArray("routes");

                        Log.e("Number of routes found", String.valueOf(arrayOfRoutes.length()));

                        for (int i = 0; i < arrayOfRoutes.length(); i++) {

                            // Get encoded path and transform into ArrayList<LatLng>
                            JSONObject routeObject = arrayOfRoutes.getJSONObject(i);
                            JSONObject overviewPolyline = routeObject.getJSONObject("overview_polyline");
                            String encodedPath = overviewPolyline.getString("points");
                            encodedPaths.add(encodedPath);

                            Log.e("ENCODED" , encodedPath);

                            ArrayList<LatLng> latLngPath = decodePoly(encodedPath);
                            pathRoutes.add(latLngPath);

                            // Get bounds for this route
                            JSONObject boundsObj = routeObject.getJSONObject("bounds");

                            JSONObject neBoundsObj = boundsObj.getJSONObject("northeast");
                            Double neLat = neBoundsObj.getDouble("lat");
                            Double neLng = neBoundsObj.getDouble("lng");

                            JSONObject swBoundsObj = boundsObj.getJSONObject("southwest");
                            Double swLat = swBoundsObj.getDouble("lat");
                            Double swLng = swBoundsObj.getDouble("lng");

                            LatLng neBounds = new LatLng(neLat, neLng);
                            LatLng swBounds = new LatLng(swLat, swLng);
                            LatLngBounds bounds = new LatLngBounds(swBounds, neBounds);
                            boundsList.add(bounds);

                            // Get distance and duration of leg.
                            JSONArray legsArray = routeObject.getJSONArray("legs");

                            //The route should only have one leg because just one destination was asked.
                            JSONObject leg = legsArray.getJSONObject(0);
                            JSONObject distanceObj = leg.getJSONObject("distance");
                            String distanceInMetersString = distanceObj.getString("value");
                            Double distanceInMeters = Double.valueOf(distanceInMetersString);
                            distances.add(distanceInMeters);

                            JSONObject durationObj = leg.getJSONObject("duration");
                            Integer durationString = durationObj.getInt("value");
                            durations.add(durationString);

                        }
                    }

                    ArrayList<Integer> numbersOfSamplesList = new ArrayList<>();

                    for (int i = 0; i < encodedPaths.size(); i++) {
                        int numberOfSamples = 0;
                        if (distances.get(i) < Constant.totalMaxDistanceForLower) {
                            numberOfSamples = distances.get(i).intValue() / Constant.distanceBetweenElevationSamplesLower;
                        } else {
                            numberOfSamples = distances.get(i).intValue() / Constant.distanceBetweenElevationSamplesHigher;
                        }
                        if (numberOfSamples < 2) {
                            numbersOfSamplesList.add(2);
                        } else {
                            numbersOfSamplesList.add(numberOfSamples);
                        }
                        Log.e("SAMPLES", "- "+numberOfSamples);
                    }

                    Calls.getElevationLists(encodedPaths, numbersOfSamplesList, new CallHandler() {
                        @Override
                        public void onSuccess(int responseCode, String response) {
                            try {
                                JSONArray jarray = new JSONArray(response);

                                Log.e("Number of elevation", String.valueOf(jarray.length()));

                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject elevationJob = jarray.getJSONObject(i);
                                    String status = elevationJob.getString("status");
                                    Log.e("Status "+i, "igual: " + status);
                                    if (status.equals("OK")) {
                                        JSONArray elevationPointsArray = elevationJob.getJSONArray("results");

                                        // Elevation list to be populated and then added to list of elevation lists
                                        ArrayList<Double> elevationList = new ArrayList<>();

                                        for (int y = 0; y < elevationPointsArray.length(); y++) {
                                            JSONObject elevationPointJob = elevationPointsArray.getJSONObject(y);
                                            double elevationValue = elevationPointJob.getDouble("elevation");
                                            elevationList.add(elevationValue);
                                        }
                                        elevationLists.add(elevationList);
                                    }
                                }

                                // Create cyclingPathReturnList, select better routes for each priority and discard the rest.


                                if (!pathRoutes.isEmpty() && elevationLists.size() == pathRoutes.size()) {

                                    for (int i = 0; i < pathRoutes.size(); i++) {
                                        CyclingPath cp = new CyclingPath(context, pathRoutes.get(i),
                                                distances.get(i),
                                                durations.get(i),
                                                elevationLists.get(i),
                                                boundsList.get(i),
                                                listBikeLanes, gMap);
                                        cyclingPathReturnList.add(cp);
                                    }

                                    // Reorder cyclingPathReturnList so the max percentage of bike lanes is the last object

                                    Collections.sort(cyclingPathReturnList, new InclinationComparator());
                                    cyclingPathReturnList.get(0).flattest = true;
                                    double minInclination = cyclingPathReturnList.get(0).maxInclination;

                                    Collections.sort(cyclingPathReturnList, new MinDurationComparator());
                                    cyclingPathReturnList.get(0).fastest = true;
                                    double minDuration = cyclingPathReturnList.get(0).totalDurationSecs;

                                    Collections.sort(cyclingPathReturnList, new PercentageOnBikeLanesComparator());
                                    cyclingPathReturnList.get(cyclingPathReturnList.size() - 1).mostBikeLanes = true;

                                    // If mostBikeLanes cp matches any other best value, remove the other cycling path.
                                    if (cyclingPathReturnList.get(cyclingPathReturnList.size() - 1).maxInclination == minInclination) {
                                        for (CyclingPath cp : cyclingPathReturnList) {
                                            cp.flattest = false;
                                        }
                                        cyclingPathReturnList.get(cyclingPathReturnList.size() - 1).flattest = true;
                                    }
                                    if (cyclingPathReturnList.get(cyclingPathReturnList.size() - 1).totalDurationSecs == minDuration) {
                                        for (CyclingPath cp : cyclingPathReturnList) {
                                            cp.fastest = false;
                                        }
                                        cyclingPathReturnList.get(cyclingPathReturnList.size() - 1).fastest = true;
                                    }

                                    // Discard CyclingPaths that are not better in anything). Loop is done backwards to avoid skipping items after one is removed.
                                    for (int i = cyclingPathReturnList.size() - 1; i >= 0; i--) {
                                        if (!cyclingPathReturnList.get(i).mostBikeLanes && !cyclingPathReturnList.get(i).fastest && !cyclingPathReturnList.get(i).flattest) {
                                            cyclingPathReturnList.remove(i);
                                        }
                                    }

                                    handlerInterface.onFinished(1, requestId, cyclingPathReturnList);
                                } else {
                                    handlerInterface.onFinished(0, requestId, null);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    // Reorders cyclingPathReturnList by maxInclination
    public static class InclinationComparator implements Comparator<CyclingPath> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(CyclingPath cp1, CyclingPath cp2) {
            return Double.compare(cp1.maxInclination, cp2.maxInclination);
        }
    }
    // Reorders cyclingPathReturnList by PercentageOnBikeLanes
    public static class PercentageOnBikeLanesComparator implements Comparator<CyclingPath> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(CyclingPath cp1, CyclingPath cp2) {
            return Integer.compare(cp1.percentageOnBikeLanes, cp2.percentageOnBikeLanes);
        }
    }
    // Reorders cyclingPathReturnList by MinDuration
    public static class MinDurationComparator implements Comparator<CyclingPath> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(CyclingPath cp1, CyclingPath cp2) {
            return Double.compare(cp1.totalDurationSecs, cp2.totalDurationSecs);
        }
    }
}