package jannini.android.ciclosp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class CheckClick {
	
	private LatLng result;
	
	LatLng checkClick(LatLng point, List<LatLng> list, double maxDistance) {

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
	public GetIntersectionResult getIntersectionBool (ArrayList<LatLng> routePath, ArrayList<LatLng> lanePath, LatLngBounds laneBounds) {
		boolean hasIntersection = false;

		int countSubsequentTrues = 0;

		ArrayList<PolylineOptions> pOptList = new ArrayList<>();
		ArrayList<LatLng> intersectionPath = new ArrayList<>();

		// Locations to check whether point is close to start or end of lane. Because if it is, it coulb be outside bounds and still be valid.
		Location startPointLocation = new Location("startPointLocation");
		startPointLocation.setLatitude(lanePath.get(0).latitude);
		startPointLocation.setLongitude(lanePath.get(0).longitude);

		Location endPointLocation = new Location("startPointLocation");
		endPointLocation.setLatitude(lanePath.get(lanePath.size()-1).latitude);
		endPointLocation.setLongitude(lanePath.get(lanePath.size()-1).longitude);

		for (int i = 0; i <routePath.size(); i++) {

			LatLng latLngPoint = routePath.get(i);

			Location pointLocation = new Location("pointLocation");
			pointLocation.setLatitude(latLngPoint.latitude);
			pointLocation.setLongitude(latLngPoint.longitude);

			boolean checkThisPoint = false;

			if (laneBounds.contains(routePath.get(i))
					|| pointLocation.distanceTo(startPointLocation) <= Constant.routeIntersectionTolerance
					|| pointLocation.distanceTo(endPointLocation) <= Constant.routeIntersectionTolerance) {
				// Se o atual ponto da rota estiver fora das bounds da ciclovia, já seta checkThisPoint como false.
				checkThisPoint = checkPoint(routePath.get(i), lanePath);
			}

			if (checkThisPoint) {
				countSubsequentTrues++;
				if (countSubsequentTrues > 2) {
					hasIntersection = true;
					if (countSubsequentTrues == 3) {
						intersectionPath.add(routePath.get(i - 2));
						intersectionPath.add(routePath.get(i - 1));
					}
					intersectionPath.add(routePath.get(i));
				}
			} else {
				if (countSubsequentTrues > 2) {
					PolylineOptions pOpt = new PolylineOptions().addAll(intersectionPath);
					pOptList.add(pOpt);
				}
				countSubsequentTrues = 0;
				intersectionPath.clear();
			}
		}

		return new GetIntersectionResult(hasIntersection, pOptList);

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
		public ArrayList<PolylineOptions> pOptList;

		GetIntersectionResult(boolean hasIntersection, ArrayList<PolylineOptions> pOptList) {
			this.hasIntersection = hasIntersection;
			this.pOptList = pOptList;
		}
	}

	public void isPointInsideBounds(LatLng point, ArrayList<LatLng> latLngPath) {

	}
}
 