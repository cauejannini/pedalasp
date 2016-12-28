package jannini.android.ciclosp.CustomItemClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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
	public boolean isVerified, hasFeatured = false;
	public ArrayList<Integer> categoryIdList;
	public Bitmap icon;
	public int id, iconId;
	public Marker mapMarker;
	public boolean isDrawn = false;

	public Place(Context context, int id, String name, LatLng latLng, String address, String phone, String site, String publicEmail, String currentOpenStatus, String short_desc, String displayServices, ArrayList<Integer> categoryIdList, boolean isVerified, boolean hasFeatured, int iconId){
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
		this.hasFeatured = hasFeatured;
		this.displayServices = displayServices;
		this.categoryIdList = categoryIdList;
		this.iconId = iconId;
		this.icon = Constant.mapPlaceIcon.get(this.iconId);

		if (isVerified) Log.e("icon", String.valueOf(icon));
	}

	public void drawOnMap (GoogleMap googleMap) {
		if (name != null && latLng != null && this.icon != null) {
			mapMarker = googleMap.addMarker(new MarkerOptions()
					.title(name)
					.icon(BitmapDescriptorFactory.fromBitmap(this.icon))
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

			if (googleMap.getCameraPosition().zoom < Constant.ZOOM_FOR_UNVERIFIED_PLACES && !isVerified) {
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
