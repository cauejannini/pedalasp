package jannini.android.ciclosp.Models;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.Utils;
import jannini.android.ciclosp.R;

public class BikeLane extends MapElement{
	public String name;
	public String info;
	public Double distanceKm;
	public ArrayList<ArrayList<LatLng>> paths = new ArrayList<>();
	public int type, id;
    public ArrayList<LatLngBounds> boundsList = new ArrayList<>();
	private ArrayList<MarkerOptions> accessList = new ArrayList<>();
	private ArrayList<Marker> accessMarkerList = new ArrayList<>();
	private ArrayList<Polyline> polylineList = new ArrayList<>();
	public int color;

	public BikeLane(Context context, int id, String nome, String info, Double distanceKm, ArrayList<ArrayList<LatLng>> paths, Integer type){
		super(context);
		this.id = id;
		this.name = nome;
		this.info = info;
		this.distanceKm = distanceKm;
		this.paths = paths;
		this.type = type;
        getBounds();
	}

	@Override
	String getTitle() {
		return name;
	}

	@Override
	String getDescription() {
		return info;
	}

	public void addAccess (String title, String info, double lat, double lng) {
		MarkerOptions access = new MarkerOptions()
				.position(new LatLng(lat, lng))
				.title(title)
				.snippet(info)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_access))
				.anchor(0.5f, 0.5f);

		accessList.add(access);
	}

	@Override
	public void drawOnMap (GoogleMap googleMap) {

		int zIndex = 1;

		switch (type) {
			case 0:
				this.color = context.getResources().getColor(R.color.bikelane_permanent_0);
				zIndex = 3;
				break;
			case 1:
				this.color = context.getResources().getColor(R.color.bikelane_permanent_1);
				zIndex = 3;
				break;
			case 2:
				this.color = context.getResources().getColor(R.color.bikelane_recreational);
				zIndex = 1;
				break;
			case 3:
				this.color = context.getResources().getColor(R.color.bikelane_preferential);
				zIndex = 2;
				break;
		}

		for (ArrayList<LatLng> path : this.paths) {
			polylineList.add(googleMap.addPolyline(new PolylineOptions()
					.addAll(path)
					.color(this.color)
					.width(Utils.getPixelValue(context, Constant.bikeLaneWidth))
					.zIndex(zIndex)));
		}

		for (MarkerOptions access : accessList) {
			accessMarkerList.add(googleMap.addMarker(access));
		}

		isDrawn = true;
	}

	@Override
	public void removeFromMap () {

		for (Polyline polyline : polylineList) {polyline.remove();}

		for (Marker accessMarker : accessMarkerList) {accessMarker.remove();}

		isDrawn = false;
	}

	@Override
	public void setVisible (boolean visibility) {
		for (Polyline polyline : polylineList) {
			polyline.setVisible(visibility);
		}
		for (Marker accessMarker : accessMarkerList) {
			accessMarker.setVisible(visibility);
		}
	}

	private void getBounds() {

		boundsList.clear();
		boundsList = new ArrayList<>();

		for (int i = 0; i<paths.size(); i++){
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			for (LatLng latLng : paths.get(i)) {
				builder.include(latLng);
			}
			boundsList.add(builder.build());
		}
	}

}
