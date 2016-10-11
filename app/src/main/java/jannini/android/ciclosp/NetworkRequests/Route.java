package jannini.android.ciclosp.NetworkRequests;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Route {
public final static String MODE_DRIVING = "driving";
public final static String MODE_WALKING = "walking";

public Route() {
}

public Document getDocument(LatLng start, LatLng end, String mode) {
    String s_url = "http://maps.googleapis.com/maps/api/directions/xml?"
            + "origin=" + start.latitude + "," + start.longitude
            + "&destination=" + end.latitude + "," + end.longitude
            + "&sensor=false&units=metric&avoid=highways&mode=bicycling&alternatives=true";

    HttpURLConnection connection;

    try {

        URL url = new URL(s_url);
        connection = (HttpURLConnection) url.openConnection();
        InputStream is = new BufferedInputStream(connection.getInputStream());

        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = builder.parse(is);
        return doc;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
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

    /*
     * NodeList nl1 = doc.getElementsByTagName("distance"); Node node1 =
     * null; if (nl1.getLength() > 0) node1 = nl1.item(nl1.getLength() - 1);
     * if (node1 != null) { NodeList nl2 = node1.getChildNodes(); Node node2
     * = nl2.item(getNodeIndex(nl2, "value")); Log.i("DistanceValue",
     * node2.getTextContent()); return
     * Integer.parseInt(node2.getTextContent()); } else return 0;
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

public ArrayList<PolylineOptions> getDirection(Document doc) {

	ArrayList<PolylineOptions> routesPolylines = new ArrayList<>();
	NodeList routesNodeList = doc.getElementsByTagName("route");
	
	for (int i=0 ; i<routesNodeList.getLength() ; i++){
		Node routeNode = routesNodeList.item(i);
		NodeList routeNodeChilds = routeNode.getChildNodes();

	    ArrayList<LatLng> listGeopoints = new ArrayList<>();
	    PolylineOptions polyline = new PolylineOptions();

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
	    polyline.addAll(listGeopoints);
	    routesPolylines.add(polyline);
	}
    
    return routesPolylines;
}

private int getNodeIndex(NodeList nl, String nodename) {
    for (int i = 0; i < nl.getLength(); i++) {
        if (nl.item(i).getNodeName().equals(nodename))
            return i;
    }
    return -1;
}

private ArrayList<LatLng> decodePoly(String encoded) {
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
}