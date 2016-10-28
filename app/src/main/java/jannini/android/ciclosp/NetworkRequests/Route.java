package jannini.android.ciclosp.NetworkRequests;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import jannini.android.ciclosp.Constant;

public class Route {

    public Route() {}

    public static void getRoute (final LatLng start, final LatLng end, final GetRouteInterface handlerInterface) {

        final ArrayList<ArrayList<LatLng>> pathRoutes = new ArrayList<>();
        final ArrayList<Double> distances = new ArrayList<>();
        final ArrayList<Integer> durations = new ArrayList<>();
        final ArrayList<ArrayList<Double>> elevationLists = new ArrayList<>();
        final ArrayList<LatLngBounds> boundsList = new ArrayList<>();

        final ArrayList<String> encodedPaths = new ArrayList<>();

        String s_url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&avoid=highways&mode=bicycling&alternatives=true";

        Calls.jsonRequest(s_url, new CallHandler() {
            @Override
            public void onSuccess(int responseCode, String response) {

                try {
                    JSONObject job = new JSONObject(response);

                    JSONArray arrayOfRoutes = job.getJSONArray("routes");

                    Log.e("Number of routes found", String.valueOf(arrayOfRoutes.length()));

                    for (int i = 0; i < arrayOfRoutes.length(); i++) {

                        // Get encoded path and transform into ArrayList<LatLng>
                        JSONObject routeObject = arrayOfRoutes.getJSONObject(i);
                        JSONObject overviewPolyline = routeObject.getJSONObject("overview_polyline");
                        String encodedPath = overviewPolyline.getString("points");
                        encodedPaths.add(encodedPath);

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

                    ArrayList<Integer> numbersOfSamplesList = new ArrayList<>();

                    for (int i = 0; i < encodedPaths.size(); i++) {
                        int numberOfSamples = distances.get(i).intValue() / Constant.distanceBetweenElevationSamples;
                        if (numberOfSamples < 2) {
                            numbersOfSamplesList.add(2);
                        } else {
                            numbersOfSamplesList.add(numberOfSamples);
                        }
                    }

                    Calls.getElevationLists(encodedPaths, numbersOfSamplesList, new CallHandler() {
                        @Override
                        public void onSuccess(int responseCode, String response) {
                            try {
                                JSONArray jarray = new JSONArray(response);

                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject elevationJob = jarray.getJSONObject(i);
                                    String status = elevationJob.getString("status");
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

                                handlerInterface.onFinished(pathRoutes, elevationLists, distances, durations, boundsList);

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

    public static ArrayList<ArrayList<LatLng>> getDirectionsFromDoc(Document doc) {

        ArrayList<ArrayList<LatLng>> routesPolylines = new ArrayList<>();
        NodeList routesNodeList = doc.getElementsByTagName("route");

        for (int i=0 ; i<routesNodeList.getLength() ; i++){
            Node routeNode = routesNodeList.item(i);
            NodeList routeNodeChilds = routeNode.getChildNodes();

            ArrayList<LatLng> listGeopoints = new ArrayList<>();

            NodeList nlLegs = routeNodeChilds.item(getNodeIndex(routeNodeChilds, "leg")).getChildNodes();

            if (nlLegs.getLength() > 0) {
                for (int y = 0; y < nlLegs.getLength(); y++) {
                    if (nlLegs.item(y).getNodeName().equals("step")){
                        Node node1 = nlLegs.item(y);
                        NodeList nl2 = node1.getChildNodes();

                        Node nodePolyline = nl2.item(getNodeIndex(nl2, "polyline"));
                        NodeList nlPoints = nodePolyline.getChildNodes();
                        Node nodePoints= nlPoints.item(getNodeIndex(nlPoints, "points"));
                        ArrayList<LatLng> arr = decodePoly(nodePoints.getTextContent());
                        for (int j = 0; j < arr.size(); j++) {
                            listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                                    .get(j).longitude));
                        }
                    }
                }
            }
            routesPolylines.add(listGeopoints);
        }

        return routesPolylines;
    }

    public ArrayList<Integer> getDistanceValues(Document doc) {

        ArrayList<Integer> distanceList = new ArrayList<>();

        NodeList routeList = doc.getElementsByTagName("route");

        for (int i =0; i<routeList.getLength(); i++) {
            NodeList routeChildNodeList = routeList.item(i).getChildNodes();
            Node legNode = routeChildNodeList.item(getNodeIndex(routeChildNodeList, "leg"));
            NodeList legChildNodeList = legNode.getChildNodes();
            Node distanceNode = legChildNodeList.item(getNodeIndex(legChildNodeList, "distance"));
            NodeList distanceChildNodeList = distanceNode.getChildNodes();
            Node valueNode = distanceChildNodeList.item(getNodeIndex(distanceChildNodeList, "value"));
            Log.i("DistanceValue", valueNode.getTextContent());
            distanceList.add(Integer.parseInt(valueNode.getTextContent()));
        }

        return distanceList;
    }

    private static int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
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

    static class RouteResponseWrapper {

        ArrayList<ArrayList<LatLng>> routePaths;
        ArrayList<ArrayList<Double>> pathsElevations;
        ArrayList<Double> distances;
        ArrayList<String> durations;

        public RouteResponseWrapper(ArrayList<ArrayList<LatLng>> routePaths, ArrayList<ArrayList<Double>> pathsElevations, ArrayList<Double> distances, ArrayList<String> durations) {
            this.routePaths = routePaths;
            this.pathsElevations = pathsElevations;
            this.distances = distances;
            this.durations = durations;
        }
    }

    public String getDurationText(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            Log.i("DurationText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "0";
        }
    }

    public int getDurationValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DurationValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getDistanceText(Document doc) {
        /*
         * while (en.hasMoreElements()) { type type = (type) en.nextElement();
         *
         * }
         */

        try {
            NodeList nl1;
            nl1 = doc.getElementsByTagName("distance");
            Node node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = null;
            nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            return node2.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

        /*
         * NodeList nl1; if(doc.getElementsByTagName("distance")!=null){ nl1=
         * doc.getElementsByTagName("distance");
         *
         * Node node1 = nl1.item(nl1.getLength() - 1); NodeList nl2 = null; if
         * (node1.getChildNodes() != null) { nl2 = node1.getChildNodes(); Node
         * node2 = nl2.item(getNodeIndex(nl2, "value")); Log.d("DistanceText",
         * node2.getTextContent()); return node2.getTextContent(); } else return
         * "-1";} else return "-1";
         */
    }

    public String getStartAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("start_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public String getEndAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("end_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
    }
    }

    public String getCopyRights(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("copyrights");
            Node node1 = nl1.item(0);
            Log.i("CopyRights", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
        return "-1";
        }

    }
}