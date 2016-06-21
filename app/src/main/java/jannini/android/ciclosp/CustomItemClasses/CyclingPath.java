package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CyclingPath implements Parcelable {
	public ArrayList<LatLng> pathLatLng = new ArrayList<>();
	public ArrayList<Double> pathElevation = new ArrayList<>();
	public ArrayList<Double> referenceDistances = new ArrayList<>();
	public double maxInclination;
	public double referenceDistanceForMaxInclination;
	public ArrayList<Double> inclinationList = new ArrayList<>();
	public double totalDistance;
	public HashMap<Double, Double> hashRefDistInclination = new HashMap<>();
	public HashMap<Double, LatLng> hashRefDistLatLng = new HashMap<>();
	public int percentageOnBikeLanes;

	public CyclingPath(ArrayList<LatLng> pathLatLng, ArrayList<Double> pathElevation, ArrayList<Double> referenceDistances){
		this.pathLatLng = pathLatLng;
		this.pathElevation = pathElevation;
		this.referenceDistances = referenceDistances;

		HashMap<Double, LatLng> refDistLatLngHMAssist = new HashMap<>();
		for (int y = 0; y<referenceDistances.size(); y++) {
			refDistLatLngHMAssist.put(referenceDistances.get(y),
					pathLatLng.get(y));
		}
		this.hashRefDistLatLng = refDistLatLngHMAssist;

		ArrayList<Double> inclinationListAssistArray = new ArrayList<>();
		HashMap<Double, Double> refDistInclinationHMAssist = new HashMap<>();

		if (pathElevation.size() == referenceDistances.size()){
			double maxInclinationDegrees = 0;
			double referenceDistanceForMaxInclinationX = 0;

			for (int i=0; i<pathElevation.size()-1; i++) {
				double h0 = pathElevation.get(i);
				double h1 = pathElevation.get(i+1);

				double d0 = referenceDistances.get(i)*1000; // it's necessary to multiply by 1000 cause mapquest api gives height in meters and distance in kilometers
				double d1 = referenceDistances.get(i+1)*1000;

				// Se os steps são iguais ou muito próximos, ignorar o cálculo de elevação e considerar que ela é zero.
				if (d1-d0 < 3 && d1-d0 > -3){

					// Add the 0 degrees for the inclination of this step
					inclinationListAssistArray.add(0.0);


				} else {
					// Calculate angle of this step in degrees
					double seno = (h1 - h0) / (d1 - d0);
					double radians = Math.asin(seno);
					double degrees = Math.toDegrees(radians);
					// Add the degrees for the inclination of this step to list of all inclinations
					String roundedStringDouble = String.format(Locale.US, "%.1f", degrees);
					Double degreesTwoDecimals = Double.valueOf(roundedStringDouble);
					inclinationListAssistArray.add(degreesTwoDecimals);
					// Populate hashMap that will become variable hashM
					refDistInclinationHMAssist.put(referenceDistances.get(i), degreesTwoDecimals);
					// If this is the biggest inclination so fat, cast it to "maxInclinationRadians" variable.
					if (i == 0 || degreesTwoDecimals > maxInclinationDegrees) {
						maxInclinationDegrees = degreesTwoDecimals;
						referenceDistanceForMaxInclinationX = referenceDistances.get(i);
					}
				}
			}

			this.maxInclination = maxInclinationDegrees;
			this.referenceDistanceForMaxInclination = Double.valueOf(String.format(Locale.US, "%.2f", referenceDistanceForMaxInclinationX));
			this.inclinationList = inclinationListAssistArray;
			this.hashRefDistInclination = refDistInclinationHMAssist;
		} else {
			this.maxInclination = 999;
			this.referenceDistanceForMaxInclination = 999.9;
			this.inclinationList = null;
		}
		String roundedDouble = String.format(Locale.US, "%.2f", referenceDistances.get(referenceDistances.size()-1));
		this.totalDistance = Double.valueOf(roundedDouble);

		Log.d("REFERENCEDISTANCES:", referenceDistances.toString());

	}

	public CyclingPath (ArrayList<LatLng> pathLatLng) {
		this.pathLatLng = pathLatLng;
		this.pathElevation = new ArrayList<>();
		this.referenceDistances = new ArrayList<>();
		this.maxInclination = 999;
		this.referenceDistanceForMaxInclination = 999.9;
		this.inclinationList = new ArrayList<>();
		this.totalDistance = 999;
	}

	public CyclingPath (ArrayList<LatLng> pathLatLng, Double totalDistance) {
		this.pathLatLng = pathLatLng;
		this.pathElevation = new ArrayList<>();
		this.referenceDistances = new ArrayList<>();
		this.maxInclination = 999;
		this.referenceDistanceForMaxInclination = 999.0;
		this.inclinationList = new ArrayList<>();
		this.totalDistance = totalDistance;
	}

	public int getEstimatedTime(){
		Double estimatedTime = this.totalDistance / 10 * 60; // Considering 10 km/h. Result in minutes, that's why the *60
		return estimatedTime.intValue();
	}

	public Double getElevationFromRefDistance(Double refDistance){
		return this.hashRefDistInclination.get(refDistance);
	}

	public LatLng getLatLngFromRefDistance(Double refDistance){
		return this.hashRefDistLatLng.get(refDistance);
	}

	public void addPercentage(int percentage){
		this.percentageOnBikeLanes = percentage;
	}

	public boolean hasElevation() {
		return !this.pathElevation.isEmpty();
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeList(pathLatLng);
		out.writeList(pathElevation);
		out.writeList(referenceDistances);
		out.writeDouble(maxInclination);
		out.writeDouble(referenceDistanceForMaxInclination);
		out.writeList(inclinationList);
		out.writeDouble(totalDistance);
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
	public CyclingPath(Parcel in) {
		pathLatLng = in.readArrayList(LatLng.class.getClassLoader());
		pathElevation = in.readArrayList(Double.class.getClassLoader());
		referenceDistances = in.readArrayList(Double.class.getClassLoader());
		maxInclination = in.readDouble();
		referenceDistanceForMaxInclination = in.readDouble();
		inclinationList = in.readArrayList(Integer.class.getClassLoader());
		totalDistance = in.readDouble();
		percentageOnBikeLanes = in.readInt();
	}

}
