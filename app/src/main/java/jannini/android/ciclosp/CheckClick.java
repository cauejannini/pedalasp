package jannini.android.ciclosp;

import java.util.List;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class CheckClick {
	
	LatLng result;
	
	public LatLng checkClick(LatLng point, List<LatLng> list, double maxDistance) {
		
		for (int i=0 ; i < list.size() - 1 ; i++ ){

			LatLng coord0 = list.get(i);
			LatLng coord1 = list.get(i+1);
			
			double lat0 = coord0.latitude;
			double lng0 = coord0.longitude; 
			double lat1 = coord1.latitude; 
			double lng1 = coord1.longitude; 
			
			double deltaX = lat1 - lat0;
			double deltaY = lng1 - lng0;
			
			if ((deltaX == 0) && (deltaY == 0)) {
				continue;
			}
			
			double a = point.latitude - lat0;
			double b = point.longitude - lng0;
			
			double u = (a * deltaX + b * deltaY) / (deltaX * deltaX + deltaY * deltaY);
			
			LatLng closestPoint;
			if (u < 0) {
				closestPoint = coord0;
			} else if (u > 1) {
				closestPoint = coord1;
			} else {
				closestPoint = new LatLng((lat0 + u * deltaX), (lng0 + u * deltaY));
			}
			
			Location closestPointLocation = new Location("closestPointLocation");
			closestPointLocation.setLatitude(closestPoint.latitude);
			closestPointLocation.setLongitude(closestPoint.longitude);
			
			Location pointLocation = new Location("pointLocation");
			pointLocation.setLatitude(point.latitude);
			pointLocation.setLongitude(point.longitude);
			
			double distance = closestPointLocation.distanceTo(pointLocation);			
			
			if (distance < maxDistance) {
				result = closestPoint;
				i = list.size();
			} else {
				result = null;
			}
		}
		
		return result;
		
	}
}
 