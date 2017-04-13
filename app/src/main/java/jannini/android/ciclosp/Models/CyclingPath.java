package jannini.android.ciclosp.Models;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;

import jannini.android.ciclosp.CheckClick;
import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.NetworkRequests.Utils;

public class CyclingPath implements Parcelable {
    Context context;
    public boolean isDrawn = false;
    public GoogleMap map;
    public Polyline routePolyline;
    public PolylineOptions routePolylineOptions;
	public ArrayList<LatLng> pathLatLng = new ArrayList<>();
	public ArrayList<Double> pathElevation = new ArrayList<>();
	public double totalDistanceInKm, totalDistanceInMeters;
	public int totalDurationSecs;
	public double maxInclination;
	public LatLngBounds bounds;
	public int percentageOnBikeLanes;
    public ArrayList<Polyline> intersectionPolylines = new ArrayList<>();
    public ArrayList<PolylineOptions> intersectionPolylineOptions = new ArrayList<>();
    public boolean isSelected = false;
    public boolean mostBikeLanes, fastest, flattest;

	public CyclingPath(Context context, ArrayList<LatLng> pathLatLng, double totalDistance, int totalDurationSecs, ArrayList<Double> pathElevation, LatLngBounds bounds, ArrayList<BikeLane> bikeLaneList, GoogleMap map){

        this.context = context;
        this.routePolylineOptions = new PolylineOptions().addAll(pathLatLng);
        this.map = map;

        this.pathLatLng = pathLatLng;
		this.pathElevation = pathElevation;
        this.totalDistanceInMeters = totalDistance;
		String roundedDouble = String.format(Locale.US, "%.2f", totalDistanceInMeters/1000);
		this.totalDistanceInKm = Double.valueOf(roundedDouble);
		this.totalDurationSecs = totalDurationSecs;
		this.bounds = bounds;

		this.maxInclination = calculateMaxInclination();
		this.percentageOnBikeLanes = calculatePercentageOnBikeLanes(bikeLaneList);

        this.mostBikeLanes = this.fastest = this.flattest = false;

        setSelected(false);
	}

    public void drawOnMap() {
        isDrawn = true;
        this.routePolyline = map.addPolyline(this.routePolylineOptions);
        for (PolylineOptions intersectionPOpt : this.intersectionPolylineOptions) {
            this.intersectionPolylines.add(map.addPolyline(intersectionPOpt));
        }
    }

    public String getReadableDuration() {
        String readableDuration = "";

        if (this.totalDurationSecs < 60) {
            readableDuration = this.totalDurationSecs + " segs";
        } else if (this.totalDurationSecs < 3600) {
            int minutes = this.totalDurationSecs / 60;
            readableDuration = minutes + " min";
        } else {
            int hours = this.totalDurationSecs / 3600;
            int minutes = ((this.totalDurationSecs / 3600) - hours) * 60;
            readableDuration = hours + " h " + minutes + " min";
        }

        return readableDuration;
    }

	private double calculateMaxInclination() {
		double maxInclinationDegrees = 0;

		for (int i=0; i<pathElevation.size()-2; i++) {
			double h0 = pathElevation.get(i);
			double h1 = pathElevation.get(i+2);

			// Calculate angle of this step in degrees
            int distanceBetweenElevationSamples = Constant.distanceBetweenElevationSamplesLower;
            if (totalDistanceInMeters > Constant.totalMaxDistanceForLower) {
                distanceBetweenElevationSamples = Constant.distanceBetweenElevationSamplesHigher;
            }
			double seno = (h1 - h0) / distanceBetweenElevationSamples*2;
			double radians = Math.asin(seno);
			double degrees = Math.toDegrees(radians);
			// Add the degrees for the inclination of this step to list of all inclinations
			String roundedStringDouble = String.format(Locale.US, "%.1f", degrees);
			Double degreesTwoDecimals = Double.valueOf(roundedStringDouble);
			// If this is the biggest inclination so fat, cast it to "maxInclinationRadians" variable.
			if (i == 0 || degreesTwoDecimals > maxInclinationDegrees) {
				maxInclinationDegrees = degreesTwoDecimals;
			}
		}
		return maxInclinationDegrees;
	}

	private int calculatePercentageOnBikeLanes(ArrayList<BikeLane> bikeLaneList) {

		for (BikeLane bl : bikeLaneList) {

            // Antes de fazer o resultFrom check da ciclovia, checar se os quadrantes se interseccionam. Caso negativo, pular pra próxima.
			for (int i = 0; i<bl.boundsList.size(); i++) {
                if (!doesBoundsIntersect(this.bounds, bl.boundsList.get(i))) {
                    continue;
                }

                CheckClick cc = new CheckClick();
                CheckClick.GetIntersectionResult resultFromCheck = cc.getIntersectionBool(this.pathLatLng, bl.paths.get(i), bl.boundsList.get(i));

                // Check if route has intersection
                if (resultFromCheck.hasIntersection) {
                    intersectionPolylineOptions.addAll(resultFromCheck.pOptList);
                }
            }
		}

        // Calculate meters of intersection and percentage of total
        double totalDistanceOfIntersection = 0.0;

        if (intersectionPolylineOptions.size() > 0) {
            for (PolylineOptions polyOptions : intersectionPolylineOptions) {
                double distanceOfPolyline = 0.0;
                for (int i = 1; i < polyOptions .getPoints().size(); i++) {
                    Location location0 = new Location("loc0");
                    location0.setLatitude(polyOptions .getPoints().get(i-1).latitude);
                    location0.setLongitude(polyOptions .getPoints().get(i-1).longitude);

                    Location location1 = new Location("loc0");
                    location1.setLatitude(polyOptions .getPoints().get(i).latitude);
                    location1.setLongitude(polyOptions .getPoints().get(i).longitude);

                    distanceOfPolyline += location0.distanceTo(location1);
                }
                totalDistanceOfIntersection += distanceOfPolyline;
            }
        }

        return (int) (totalDistanceOfIntersection / this.totalDistanceInKm)/10;

	}

    public void setSelected (boolean shouldSelect) {
        this.isSelected = shouldSelect;

        if (routePolyline != null) {

            if (shouldSelect) {
                routePolyline.setWidth(Utils.getPixelValue(this.context, Constant.selectedPolylineWidth));
                routePolyline.setColor(Color.BLUE);
                routePolyline.setZIndex(10);
                for (Polyline poly : intersectionPolylines) {
                    poly.setVisible(true);
                    poly.setWidth(Utils.getPixelValue(this.context, Constant.selectedPolylineWidth));
                    poly.setColor(Color.RED);
                    poly.setZIndex(10);
                }
            } else {
                routePolyline.setWidth(Utils.getPixelValue(this.context, Constant.unSelectedPolylineWidth));
                routePolyline.setColor(Color.LTGRAY);
                routePolyline.setZIndex(9);
                for (Polyline poly : intersectionPolylines) {
                    poly.setVisible(false);
                    poly.setWidth(Utils.getPixelValue(this.context, Constant.unSelectedPolylineWidth));
                }
            }
        }
    }

	private boolean doesBoundsIntersect(LatLngBounds b1, LatLngBounds b2) {

		return !(b1.southwest.latitude > b2.northeast.latitude || b2.southwest.latitude > b1.northeast.latitude)
				&& !(b1.southwest.longitude > b2.northeast.longitude || b2.southwest.longitude > b1.northeast.longitude);

	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeList(pathLatLng);
		out.writeList(pathElevation);
		out.writeDouble(maxInclination);
		out.writeDouble(totalDistanceInKm);
		out.writeInt(percentageOnBikeLanes);
	}

	public static final Creator<CyclingPath> CREATOR = new Creator<CyclingPath>() {
		public CyclingPath createFromParcel(Parcel in) {
			return new CyclingPath(in);
		}

		public CyclingPath[] newArray(int size) {
			return new CyclingPath[size];
		}
	};

	@SuppressWarnings("unchecked")
	private CyclingPath(Parcel in) {
		pathLatLng = in.readArrayList(LatLng.class.getClassLoader());
		pathElevation = in.readArrayList(Double.class.getClassLoader());
		maxInclination = in.readDouble();
		totalDistanceInKm = in.readDouble();
		percentageOnBikeLanes = in.readInt();
	}

}
