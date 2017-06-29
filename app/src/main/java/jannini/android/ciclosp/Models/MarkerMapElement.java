package jannini.android.ciclosp.Models;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by cauejannini on 12/04/17.
 */

abstract class MarkerMapElement extends MapElement {
    private Marker marker;
    private LatLng latLng;
    private String[] markerTagArray;
    private Integer minimumZoom;

    MarkerMapElement(Context context, LatLng latLng, String[] markerTagArray) {
        super(context);
        if (latLng != null) {
            this.latLng = latLng;
            this.markerTagArray = markerTagArray;
        } else {
            throw new IllegalArgumentException("MarkerMapElement: latLng or icon null");
        }
    }

    protected void setMinimumZoom (int zoom) {
        minimumZoom = zoom;
    }

    abstract BitmapDescriptor getIcon();

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public void drawOnMap(GoogleMap map) {

        String title = getTitle();
        String description = getDescription();
        BitmapDescriptor icon = getIcon();

        if (icon != null && title != null && description != null) {
            marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(icon)
                    .anchor(0.5f, 0.5f)
                    .snippet(description));

            isDrawn = true;

            if (markerTagArray != null) {
                marker.setTag(markerTagArray);
            }

            if (minimumZoom != null && map.getCameraPosition().zoom < minimumZoom) {
                setVisible(false);
            }
        }
    }

    @Override
    public void removeFromMap() {
        if (marker != null) {
            marker.remove();
            isDrawn = false;
        }
    }

    @Override
    public void setVisible(boolean visibility) {
        if (marker != null) {
            marker.setVisible(visibility);
        }
    }

    public boolean isSelected() {
        return marker.isInfoWindowShown();
    }

    public void deselectMarker() {
        marker.hideInfoWindow();
    }
}
