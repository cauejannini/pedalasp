package jannini.android.ciclosp.Models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import jannini.android.ciclosp.Adapters.CustomInfoWindowAdapter;
import jannini.android.ciclosp.Utils;
import jannini.android.ciclosp.R;

/**
 * Created by cauejannini on 12/04/17.
 */

public class MapController implements LocationListener {

    private int LOC_UPDATE_MIN_TIME = 0;
    private int LOC_UPDATE_MIN_DISTANCE = 0;

    private final int RC_GET_USER_LAST_LOCATION = 1;
    private final int RC_START_LOCATION_UPDATES = 2;

    private Activity activity;
    private MapInteractionListener mapListener;
    private GoogleMap map;

    private boolean isNavigationOn = false;
    private ToggleButton tbSwitchNavigation;

    private LocationManager locationManager;
    private Marker userMarker, markerSearch;
    List<Marker> listMarker = new ArrayList<>();

    public MapController (Activity activity, GoogleMap map, ToggleButton tbSwitchNavigation) throws Exception {
        if (activity != null && map != null) {
            if (activity instanceof MapInteractionListener) {
                this.activity = activity;
                this.mapListener = (MapInteractionListener) activity;
                this.map = map;
                this.map.setBuildingsEnabled(false);
                this.map.getUiSettings().setMyLocationButtonEnabled(false);
                this.map.getUiSettings().setZoomControlsEnabled(false);
                this.map.setInfoWindowAdapter(new CustomInfoWindowAdapter((LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)));
                this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                this.map.getUiSettings().setCompassEnabled(true);

                getUserLastLocation();
                startLocationUpdates();
                //setMapEvents();
            } else {
                throw new Exception("Activity "+activity.toString()+" deve implementar MapInteractionInterface");
            }
        } else {
            throw new Exception("Null parameter");
        }
    }

    private void getUserLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_GET_USER_LAST_LOCATION);
        } else {
            this.map.setMyLocationEnabled(false);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            updateUserPosition(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
        }
    }

    public void startLocationUpdates() {

        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // GET BEST AVAILABLE PROVIDER
        String bestAvailableProvider = locationManager.getBestProvider(criteria, false);
        if (bestAvailableProvider != null) {
            if (locationManager.isProviderEnabled(bestAvailableProvider)) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_START_LOCATION_UPDATES);
                } else {
                    locationManager.requestLocationUpdates(bestAvailableProvider, LOC_UPDATE_MIN_TIME, LOC_UPDATE_MIN_DISTANCE, this);
                }
            } else {
                Utils.showToastWithMessage(activity, activity.getString(R.string.loc_verifique_gps));
            }
        } else {
            Utils.showToastWithMessage(activity, activity.getString(R.string.loc_verifique_gps));
        }
    }

    private void switchNavigation(boolean shouldTurnNavigationOn) {
        isNavigationOn = shouldTurnNavigationOn;
        tbSwitchNavigation.setChecked(false);
        if (mapListener != null) {
            mapListener.onNavigationSwitched(shouldTurnNavigationOn);
        }
    }

    private void updateUserPosition(LatLng latLng) throws IllegalArgumentException {
        if (latLng != null) {
            if (userMarker != null) {
                userMarker.setPosition(latLng);
            } else {
                userMarker = this.map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_off))
                        .anchor(0.5f, 0.5f)
                );
                userMarker.setTag(new String[]{"userMarker"});
            }
        } else {
            throw new IllegalArgumentException("MapController.updateUserPosition: LatLng null");
        }
    }
/*
    private void setMapEvents() {

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (mapListener != null) {
                    mapListener.onCameraIdle();
                }
                displayPlaces();
            }
        });

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (i == REASON_GESTURE) {
                    switchNavigation(false);
                }
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            public void onMapClick(LatLng point) {

                hideAllBottomButtons();

                for (Marker marker : listMarker) {
                    marker.remove();
                }
                listMarker.clear();

                // Remove markerSearch from old search
                if (markerSearch != null) {
                    markerSearch.remove();
                    markerSearch = null;
                }

                Projection projection = map.getProjection();
                Point pointScreen = projection.toScreenLocation(point);
                Point testPointScreen = new Point(pointScreen.x + 30, pointScreen.y);
                LatLng testPoint = projection.fromScreenLocation(testPointScreen);

                Location pointLocation = new Location("pointLocation");
                pointLocation.setLatitude(point.latitude);
                pointLocation.setLongitude(point.longitude);

                Location testPointLocation = new Location("testPointLocation");
                testPointLocation.setLatitude(testPoint.latitude);
                testPointLocation.setLongitude(testPoint.longitude);

                double maxDistance = pointLocation.distanceTo(testPointLocation);

                CheckClick checking = new CheckClick();

                if (!cyclingPathList.isEmpty()) {
                    for (int i = 0; i < cyclingPathList.size(); i++) {
                        ArrayList<LatLng> list = cyclingPathList.get(i).pathLatLng;
                        LatLng closestPoint = checking.checkClick(point, list, maxDistance);
                        if (closestPoint != null) {
                            selectCyclingPath(cyclingPathList.get(i), true);
                            return;
                        }
                    }
                }

                hideBottomPanel();

                if (!ListBikeLanesPermanent.isEmpty()) {
                    if (States[0] && BikeLanesStates[0]) {

                        for (BikeLane bl: ListBikeLanesPermanent) {
                            for (ArrayList<LatLng> path: bl.paths) {

                                LatLng closestPoint = checking.checkClick(point, path, maxDistance);
                                if (closestPoint != null) {
                                    listMarker.add(googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(closestPoint.latitude, closestPoint.longitude))
                                            .title(bl.name)
                                            .snippet(bl.info + newline + newline
                                                    + getString(R.string.distancia_total) + " " + bl.distanceKm + " km")
                                            .anchor(0.5f, 0.0f)
                                            .alpha(0)));
                                    listMarker.get(0).showInfoWindow();

                                    // Center map in clicked point
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(closestPoint);
                                    googleMap.animateCamera(cameraUpdate);
                                    return;
                                }
                            }
                        }
                    }
                }

                if (!ListBikeLanesPreferential.isEmpty()) {
                    if (States[0] && BikeLanesStates[2]) {

                        for (BikeLane bl: ListBikeLanesPreferential) {
                            for (ArrayList<LatLng> path: bl.paths) {
                                LatLng closestPoint = checking.checkClick(point, path, maxDistance);
                                if (closestPoint != null) {
                                    listMarker.add(googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(closestPoint.latitude, closestPoint.longitude))
                                            .title(bl.name)
                                            .snippet(bl.info + newline + newline
                                                    + getString(R.string.distancia_total) + " " + bl.distanceKm + " km")
                                            .anchor(0.5f, 0.0f)
                                            .alpha(0)));
                                    listMarker.get(0).showInfoWindow();

                                    // Center map in clicked point
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(closestPoint);
                                    googleMap.animateCamera(cameraUpdate);
                                    return;
                                }
                            }
                        }
                    }
                }

                if (!ListBikeLanesRecreational.isEmpty()) {
                    if (States[0] && BikeLanesStates[1]) {

                        for (BikeLane bl: ListBikeLanesRecreational) {
                            for (ArrayList<LatLng> path: bl.paths) {
                                LatLng closestPoint = checking.checkClick(point, path, maxDistance);
                                if (closestPoint != null) {
                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(closestPoint.latitude, closestPoint.longitude))
                                            .title(bl.name)
                                            .anchor(0.5f, 0.0f)
                                            .alpha(0));
                                    if (rightNow.after(sundaySeven) && rightNow.before(sundaySixteen)) {
                                        marker.setSnippet(getString(R.string.open_now) + newline
                                                + bl.info + newline + newline
                                                + getString(R.string.distancia_total) + " " + bl.distanceKm + " km");
                                    } else {
                                        marker.setSnippet(getString(R.string.closed_now) + newline
                                                + bl.info + newline + newline
                                                + getString(R.string.distancia_total) + " " + bl.distanceKm + " km");
                                    }
                                    listMarker.add(marker);
                                    listMarker.get(0).showInfoWindow();

                                    // Center map in clicked point
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(closestPoint);
                                    googleMap.animateCamera(cameraUpdate);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        });

        googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                activeMarker = marker;

                String markerTitle = marker.getTitle();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("markerClick")
                        .setAction(markerTitle)
                        .build());

                // Remove markerSearch from old search
                if (markerSearch != null) {
                    markerSearch.remove();
                    markerSearch = null;
                }

                if (ListMarkersAlerts.contains(marker)) {
                    showBottomButton(notifyButton);
                } else {
                    hideBottomButton(notifyButton);
                }

                if (ListMarkersParkingSpots.contains(marker)) {
                    showBottomButton(btParkedHere);
                } else {
                    hideBottomButton(btParkedHere);
                    //hideBottomButton(llPlaceOptions);
                }

                if (marker.getTitle() != null) {
                    if (marker.getTitle().equals(getString(R.string.your_bike_is_here))
                        //|| marker.getTitle().equals(getString(R.string.saved_place))
                            ) {
                        showBottomButton(btRemovePlace);
                    } else {
                        hideBottomButton(btRemovePlace);
                        //hideBottomButton(llPlaceOptions);
                    }
                }

                if (marker.getTag() != null) {
                    String[] tag = (String[]) marker.getTag();
                    if (tag[0].equals("place")) {

                        markerSearch = googleMap.addMarker(new MarkerOptions()
                                .position(marker.getPosition()));

                        handlePlaceClick(Integer.valueOf(tag[1]));
                    } else if (tag[0].equals("markerNavigation")) {
                        hideBottomPanel();
                    }
                } else {
                    marker.showInfoWindow();
                    hideBottomPanel();
                }

                // Funcionalidades padrões para quando se clica em qualquer marcador
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 200, null);
                return true;
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                hideBottomPanel();

                if (activeMarker != null) activeMarker.hideInfoWindow();

                // Remove markerSearch, if there's one, and add again.
                if (markerSearch != null) {
                    markerSearch.remove();
                    markerSearch = null;
                }

                activeMarker = markerSearch = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.marcador_inserido)));

                final ArrayList<ObjectAnimator> hideAnimationsList = hideAllBottomButtons();

                // Geocode LatLng to Address
                final LatLng ll = latLng;

                new AsyncTask<String, Void, String>() {
                    protected void onPreExecute() {
                        btClearSearch.setVisibility(View.GONE);
                        pBarSearch.setVisibility(View.VISIBLE);
                        if (isRouteModeOn) {
                            setUpdating();
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String sAddress = "";
                        List<Address> adList = new ArrayList<>();
                        try {
                            adList = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!adList.isEmpty()) {
                            Address address = adList.get(0);
                            sAddress = address.getAddressLine(0);
                            for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
                                sAddress = sAddress + ", " + address.getAddressLine(i);
                            }
                        }
                        return sAddress;
                    }

                    protected void onPostExecute(String sAddress) {

                        pBarSearch.setVisibility(View.GONE);
                        resetUpdating();

                        btClearSearch.setVisibility(View.VISIBLE);

                        if (sAddress.equals("")) {
                            etSearch.setText(getString(R.string.marcador_inserido));

                        } else {
                            etSearch.setText(sAddress);
                            if (markerSearch != null) {
                                markerSearch.setTitle(sAddress);
                            }
                            activeMarker = markerSearch;
                        }

                        hideAnimationsList.get(1).cancel();
                        showBottomButton(btParkedHere);
                    }
                }.execute();
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
                hideAllBottomButtons();
            }
        });

    }*/

    public void onPermissionReturn(int requestCode) {
        switch (requestCode) {
            case RC_GET_USER_LAST_LOCATION:
                getUserLastLocation();
                break;
            case RC_START_LOCATION_UPDATES:
                startLocationUpdates();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public interface MapInteractionListener {
        public void onMapClick(LatLng point);
        public void onMapLongClick(LatLng point);
        public void onMarkerClick(Marker marker);
        public void onCameraMoveStarted(int reasonId);
        public void onCameraIdle();
        public void onNavigationSwitched(boolean isNavigationOn);
        public void onElementClickedListener(MapElement elemento, LatLng clickedLatLng);
    }
}