package jannini.android.ciclosp.Models;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.NetworkRequests.Utils;
import jannini.android.ciclosp.R;

import static jannini.android.ciclosp.Constant.MARKER_TAG_PLACE;

public class Place extends MarkerMapElement {
	public String name, address, phone, site, publicEmail, currentOpenStatus, short_desc, displayServices = "";
	public LatLng latLng;
	public boolean isVerified, isFeatured, hasDeals = false;
	public ArrayList<Integer> categoryIdList;
	public int id, iconId, logoId;

	public Place(Context context, int id, String name, LatLng latLng, String address, String phone, String site, String publicEmail,
				 String currentOpenStatus, String short_desc, String displayServices, ArrayList<Integer> categoryIdList,
				 boolean isVerified, boolean isFeatured, boolean hasDeals, int iconId, int logoId){

		super(context, latLng, new String[]{MARKER_TAG_PLACE, String.valueOf(id)});
		if (context != null && name != null && latLng != null && address != null && phone != null && site != null && publicEmail != null
				&& currentOpenStatus != null && short_desc != null && displayServices != null && categoryIdList != null) {
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

			if (!isFeatured) {
				setMinimumZoom(Constant.ZOOM_FOR_NOT_FEATURED_PLACES);
			}
		} else {
			throw new IllegalArgumentException("Place constructor: Some required parameter is null");
		}
	}

	@Override
	String getTitle() {
		return name;
	}

	@Override
	String getDescription() {
		return displayServices + Utils.newline
				+ context.getString(R.string.address) + ": " + address + Utils.newline
				+ context.getString(R.string.phone) +": " + phone + Utils.newline
				+ Utils.newline
				+ short_desc
				+ Utils.newline;
	}

	@Override
	BitmapDescriptor getIcon() {

		Bitmap bitmapIcon = Constant.mapPlacesImages.get(this.iconId);

		if (bitmapIcon != null) {
			return BitmapDescriptorFactory.fromBitmap(Constant.mapPlacesImages.get(this.iconId));
		} else {
			return null;
		}
	}
}
