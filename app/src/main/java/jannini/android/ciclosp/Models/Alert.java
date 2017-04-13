package jannini.android.ciclosp.Models;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import jannini.android.ciclosp.R;

import static jannini.android.ciclosp.Constant.MARKER_TAG_ALERT;
import static jannini.android.ciclosp.NetworkRequests.Utils.newline;

public class Alert extends MarkerMapElement{

	public int id;
	public Double lat;
	public Double lng;
	public String type, details, timestamp, address, userResponsible;

	public Alert(Context context, int id, String type, String address, Double lat, Double lng, String details,
				 String timestamp, String userResponsible){
		super(context, new LatLng(lat, lng), new String[]{MARKER_TAG_ALERT});
		if (context != null && type != null && address != null && details != null && timestamp != null) {
			this.id = id;
			this.type = type;
			this.address = address;
			this.lat = lat;
			this.lng = lng;
			this.details = details;
			this.timestamp = timestamp;
			this.userResponsible = userResponsible;
		} else {
			throw new IllegalArgumentException("Alert constructor: required parameter is null");
		}
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	@Override
	String getTitle() {
		switch (type) {
			case "bu":
				return context.getString(R.string.via_esburacada);
			case "si":
				return context.getString(R.string.problema_sinalizacao);
			default:
				return context.getString(R.string.alerta);
		}
	}

	@Override
	String getDescription() {
		return address + newline + context.getString(R.string.details) + " " + details + newline + context.getString(R.string.alerta_em) + " " + timestamp;
	}

	@Override
	BitmapDescriptor getIcon() {
		switch (type) {
			case "bu":
				return BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert_pothole);
			case "si":
				return BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert_signalling);
			default:
				return BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert);
		}
	}
}



