package jannini.android.ciclosp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

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

	// Returns booleanList of intersection points between route and one Bike Lane
	public GetIntersectionResult getIntersectionBool (ArrayList<LatLng> routePath, ArrayList<LatLng> lanePath) {
		boolean hasIntersection = false;
		ArrayList<Boolean> booleanList = new ArrayList<>();

		int countSubsequentTrues = 0;

		for (int i = 0; i <routePath.size(); i++) {
			boolean checkThisPoint = checkPoint(routePath.get(i), lanePath);
			if (checkThisPoint) {
				countSubsequentTrues++;
				booleanList.add(true);
				if (countSubsequentTrues == 4) {
					hasIntersection = true;
				}
			} else {
				countSubsequentTrues = 0;
				booleanList.add(false);
			}
		}

		return new GetIntersectionResult(hasIntersection, booleanList);

	}

	// Function that checks if point in route is on top of bike lane. Used by above function "checkIntersection"
	private boolean checkPoint(LatLng pointToCheck, ArrayList<LatLng> path) {

		boolean returnBool = false;

		for (int i=0 ; i < path.size() - 1 ; i++ ){

			LatLng coord0 = path.get(i);
			LatLng coord1 = path.get(i+1);

			double lat0 = coord0.latitude;
			double lng0 = coord0.longitude;
			double lat1 = coord1.latitude;
			double lng1 = coord1.longitude;

			double deltaX = lat1 - lat0;
			double deltaY = lng1 - lng0;

			if ((deltaX == 0) && (deltaY == 0)) {
				continue;
			}

			double a = pointToCheck.latitude - lat0;
			double b = pointToCheck.longitude - lng0;

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

			Location pointToCheckLocation = new Location("pointLocation");
			pointToCheckLocation .setLatitude(pointToCheck.latitude);
			pointToCheckLocation .setLongitude(pointToCheck.longitude);

			double distance = closestPointLocation.distanceTo(pointToCheckLocation);

			if (distance <= Constant.routeIntersectionTolerance) {
				i = path.size();
				returnBool = true;
				break;
			}
		}
		return returnBool;
	}

	public class GetIntersectionResult {
		public boolean hasIntersection;
		public ArrayList<Boolean> booleanList;

		GetIntersectionResult(boolean hasIntersection, ArrayList<Boolean> booleanList) {
			this.hasIntersection = hasIntersection;
			this.booleanList = booleanList;
		}
	}
}
 