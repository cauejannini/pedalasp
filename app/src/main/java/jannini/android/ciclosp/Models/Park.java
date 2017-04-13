package jannini.android.ciclosp.Models;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import jannini.android.ciclosp.R;

import static jannini.android.ciclosp.Constant.MARKER_TAG_PARK;
import static jannini.android.ciclosp.NetworkRequests.Utils.newline;

public class Park extends MarkerMapElement {
	private String name;
	private String address;
	private String info;
	private String opHours;
	private String bikeLaneInfo;
        
	public Park(Context context, String name, String address, Double lat, Double lng, String info, String opHours, String bikeLaneInfo){
		super(context, new LatLng(lat, lng), new String[]{MARKER_TAG_PARK});
		if (name != null && address != null) {
			this.name = name;
			this.address = address;
			this.info = info;
			this.opHours = opHours;
			this.bikeLaneInfo = bikeLaneInfo;
		} else {
			throw new IllegalArgumentException("Park constructor: name or address null");
		}
	}

	@Override
	BitmapDescriptor getIcon() {
		return BitmapDescriptorFactory.fromResource(R.drawable.mapic_park);
	}

	@Override
	String getTitle() {
		return name;
	}

	@Override
	String getDescription() {
		return address
				+ newline + opHours
				+ newline
				+ newline + info
				+ newline + bikeLaneInfo;
	}
}
