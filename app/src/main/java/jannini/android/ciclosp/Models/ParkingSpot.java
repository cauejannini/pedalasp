package jannini.android.ciclosp.Models;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;

import static jannini.android.ciclosp.Utils.newline;

public class ParkingSpot extends MarkerMapElement{

	private String name, address, type, info, opHours;
	private int parkingSpaces, id;
        
	public ParkingSpot(Context context, int id, String name, Double lat, Double lng, String address, String type,
					   int parkingSpaces, String opHours, String info){
		super(context, new LatLng(lat, lng), new String[]{Constant.MARKER_TAG_PARKING_SPOT});

		if (name != null && type != null) {
			this.id = id;
			this.name = name;
			this.address = address;
			this.type = type;
			this.parkingSpaces = parkingSpaces;
			this.opHours = opHours;
			this.info = info;
		} else {
			throw new IllegalArgumentException("ParkingSpot constructor: name of type null");
		}
	}

	@Override
	BitmapDescriptor getIcon() {
		if (type.equals("b")) {
			return BitmapDescriptorFactory.fromResource(R.drawable.mapic_parking_b);
		} else {
			return BitmapDescriptorFactory.fromResource(R.drawable.mapic_parking_p);
		}
	}

	@Override
	String getTitle() {
		return name;
	}

	@Override
	String getDescription() {
		if (type.equals("b")) {
			return context.getString(R.string.parking_spaces) + " " + parkingSpaces
					+ newline + newline + context.getString(R.string.openingHours) + " " + opHours;
		} else {
			return context.getString(R.string.numero_de_paraciclos) + " " + (parkingSpaces / 2)
					+ newline + context.getString(R.string.endereco_aproximado) + ": " + address;
		}
	}
}
