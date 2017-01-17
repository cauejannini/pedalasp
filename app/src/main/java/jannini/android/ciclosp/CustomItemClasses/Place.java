package jannini.android.ciclosp.CustomItemClasses;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.NetworkRequests.Utils;
import jannini.android.ciclosp.R;

public class Place {
	Context context;
	public String name, address, phone, site, publicEmail, currentOpenStatus, short_desc, displayServices = "";
	public LatLng latLng;
	public boolean isVerified, isFeatured, hasDeals = false;
	public ArrayList<Integer> categoryIdList;
	public int id, iconId, logoId;
	public Marker mapMarker;
	public boolean isDrawn = false;

	public Place(Context context, int id, String name, LatLng latLng, String address, String phone, String site, String publicEmail, String currentOpenStatus, String short_desc, String displayServices, ArrayList<Integer> categoryIdList, boolean isVerified, boolean isFeatured, boolean hasDeals, int iconId, int logoId){
		this.context = context;
		this.id = id;
		this.name = name;
		this.latLng = latLng;
		this.address = address;
		this.phone = phone;
		this.site = site;
		this.publicEmail = publicEmail;
		this.currentOpenStatus = currentOpenStatus;
		this.short_desc = short_desc;
		this.isVerified = isVerified;
		this.isFeatured = isFeatured;
		this.hasDeals = hasDeals;
		this.displayServices = displayServices;
		this.categoryIdList = categoryIdList;
		this.iconId = iconId;
		this.logoId = logoId;

	}

	public void drawOnMap (GoogleMap googleMap) {
		if (name != null && latLng != null && Constant.mapPlacesImages.get(this.iconId) != null) {
			mapMarker = googleMap.addMarker(new MarkerOptions()
					.title(name)
					.icon(BitmapDescriptorFactory.fromBitmap(Constant.mapPlacesImages.get(this.iconId)))
					.position(latLng)
					.anchor(0.5f, 1.0f)
					.snippet(displayServices + Utils.newline
							+ context.getString(R.string.address) + ": " + address + Utils.newline
							+ context.getString(R.string.phone) +": " + phone + Utils.newline
							+ Utils.newline
							+ short_desc
							+ Utils.newline));
			mapMarker.setTag(new String[]{"place", String.valueOf(id)});
			isDrawn = true;

			if (googleMap.getCameraPosition().zoom < Constant.ZOOM_FOR_NOT_FEATURED_PLACES && !isVerified) {
				setVisible(false);
			}
		}
	}

	public void removeFromMap () {
		if (mapMarker != null) {
			mapMarker.remove();
			isDrawn = false;
		}
	}

	public void setVisible (boolean bool) {
		if (mapMarker != null) {
			mapMarker.setVisible(bool);
		}
	}
}
