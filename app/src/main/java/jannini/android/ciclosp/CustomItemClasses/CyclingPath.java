package jannini.android.ciclosp.CustomItemClasses;

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

public class CyclingPath implements Parcelable {
    public Polyline routePolyline;
	public ArrayList<LatLng> pathLatLng = new ArrayList<>();
	public ArrayList<Double> pathElevation = new ArrayList<>();
	public double totalDistanceInKm;
	public int totalDurationSecs;
	public double maxInclination;
	public LatLngBounds bounds;
	public int percentageOnBikeLanes;
    public ArrayList<Polyline> intersectionPolylines = new ArrayList<>();
    public boolean isSelected = false;
    public boolean mostBikeLanes, fastest, flattest;

	public CyclingPath(ArrayList<LatLng> pathLatLng, double totalDistance, int totalDurationSecs, ArrayList<Double> pathElevation, LatLngBounds bounds, ArrayList<Ciclovia> cicloviaList, GoogleMap map){

        this.routePolyline = map.addPolyline(new PolylineOptions()
        .addAll(pathLatLng));

        this.pathLatLng = pathLatLng;
		this.pathElevation = pathElevation;
		String roundedDouble = String.format(Locale.US, "%.2f", totalDistance/1000);
		this.totalDistanceInKm = Double.valueOf(roundedDouble);
		this.totalDurationSecs = totalDurationSecs;
		this.bounds = bounds;

		this.maxInclination = calculateMaxInclination();
		this.percentageOnBikeLanes = calculatePercentageOnBikeLanes(cicloviaList, map);

        this.mostBikeLanes = this.fastest = this.flattest = false;

        setSelected(false);
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

		for (int i=0; i<pathElevation.size()-1; i++) {
			double h0 = pathElevation.get(i);
			double h1 = pathElevation.get(i+1);

			// Calculate angle of this step in degrees
			double seno = (h1 - h0) / Constant.distanceBetweenElevationSamples;
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

	private int calculatePercentageOnBikeLanes(ArrayList<Ciclovia> cicloviaList, GoogleMap map) {

		for (int i = 0; i<cicloviaList.size(); i++) {

            // Antes de fazer o resultFrom check da ciclovia, checar se os quadrantes se interseccionam. Caso negativo, pular pra próxima.
			if (!doesBoundsIntersect(this.bounds, cicloviaList.get(i).bounds)) {
				continue;
			}

			ArrayList<LatLng> bikeLanePath = cicloviaList.get(i).latLngList;

            CheckClick cc = new CheckClick();
            CheckClick.GetIntersectionResult resultFromCheck = cc.getIntersectionBool(this.pathLatLng, bikeLanePath);

            // Check if route has intersection
            if (resultFromCheck.hasIntersection) {
                int countTrues = 0;
                ArrayList<LatLng> intersectionPath = new ArrayList<>();

                for (int y = 0; y < resultFromCheck.booleanList.size(); y++) {
                    if (resultFromCheck.booleanList.get(y)) {
                        countTrues++;
                        if (countTrues > 3) {
                            // Se for a primeira vez, adiciona o index passado como primeiro
                            if (countTrues == 4) {
                                intersectionPath.add(this.pathLatLng.get(y-1));
                            }
                            intersectionPath.add(this.pathLatLng.get(y));
                        }
                    } else {
                        // Se estiver saindo de uma seção de "trues" adicionar a linha que foi desenhada antes de zerar
                        if (countTrues > 3) {
                            Polyline polyline = map.addPolyline(new PolylineOptions().addAll(intersectionPath));
                            polyline.setColor(Color.RED);
                            polyline.setWidth(3f);
                            intersectionPolylines.add(polyline);
                        }
                        countTrues = 0;
                        intersectionPath.clear();
                    }
                }
            }
		}

        // Calculate meters of intersection and percentage of total
        double totalDistanceOfIntersection = 0.0;

        if (intersectionPolylines.size() > 0) {
            for (Polyline polyline : intersectionPolylines) {
                double distanceOfPolyline = 0.0;
                for (int i = 1; i < polyline.getPoints().size(); i++) {
                    Location location0 = new Location("loc0");
                    location0.setLatitude(polyline.getPoints().get(i-1).latitude);
                    location0.setLongitude(polyline.getPoints().get(i-1).longitude);

                    Location location1 = new Location("loc0");
                    location1.setLatitude(polyline.getPoints().get(i).latitude);
                    location1.setLongitude(polyline.getPoints().get(i).longitude);

                    distanceOfPolyline += location0.distanceTo(location1);
                }
                totalDistanceOfIntersection += distanceOfPolyline;
            }
        }

        return (int) (totalDistanceOfIntersection / this.totalDistanceInKm)/10;

	}

    public void setSelected (boolean shouldSelect) {
        this.isSelected = shouldSelect;

        if (shouldSelect){
            routePolyline.setWidth(Constant.selectedPolylineWidth);
            routePolyline.setColor(Color.BLUE);
            routePolyline.setZIndex(10);
            for (Polyline poly: intersectionPolylines) {
                poly.setVisible(true);
                poly.setWidth(Constant.selectedPolylineWidth);
                poly.setColor(Color.RED);
                poly.setZIndex(10);
            }
        } else {
            routePolyline.setWidth(Constant.unSelectedPolylineWidth);
            routePolyline.setColor(Color.LTGRAY);
            routePolyline.setZIndex(9);
            for (Polyline poly: intersectionPolylines) {
                poly.setVisible(false);
                poly.setWidth(Constant.unSelectedPolylineWidth);
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
