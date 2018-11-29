package jannini.android.ciclosp.Activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jannini.android.ciclosp.Adapters.CustomInfoWindowAdapter;
import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.R;
import jannini.android.ciclosp.Utils;

public class ReportActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback {

	private GoogleMap googleMap;

	ProgressBar pBar;
	Button btClearAddress, btSearch;
	EditText etReportAddress, etReportDetails;
	ImageView locationIndicator;

	List<Address> addressList = new ArrayList<>();
	List<Address> addressListBase = new ArrayList<>();
	//Marker marker_address = null;

	LatLng user_latlng;

	LatLng reportLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);

		pBar = (ProgressBar) findViewById(R.id.pb_search);
		btClearAddress = (Button) findViewById(R.id.bt_clear_address);
		btSearch = (Button) findViewById(R.id.bt_lupa);
		etReportAddress = (EditText) findViewById(R.id.et_report_address);
        etReportDetails = (EditText) findViewById(R.id.et_report_details);
		locationIndicator = (ImageView) findViewById(R.id.iv_location_indicator);

		btClearAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				etReportAddress.setText("");
				btClearAddress.setVisibility(View.GONE);
			}
		});

		Intent i = getIntent();
		Double lat = i.getDoubleExtra("latitude", 0);
		Double lng = i.getDoubleExtra("longitude", 0);

		user_latlng = new LatLng(lat, lng);

		((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.report_map)).getMapAsync(this);

		etReportAddress.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    if (etReportAddress.getText().toString().trim().length() == 0) {
                        etReportAddress.setError(getString(R.string.mandatory_field));
                    } else {
                        findAddress(etReportAddress);
                    }
                }
                return false;
            }
		});

	}

	public void geocodeLatLng(final LatLng latlng) {

		// Create a geocoder object
		final Geocoder geoCoder = new Geocoder(this);

		new AsyncTask<String, String, String>() {

			@Override
			protected void onPreExecute() {

				btClearAddress.setVisibility(View.GONE);
				pBar.setVisibility(View.VISIBLE);
			}

			@Override
			protected String doInBackground(String... params) {

				if (latlng != null) {
					try {
						addressListBase = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					cancel(true);
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				pBar.setVisibility(View.GONE);
				btClearAddress.setVisibility(View.VISIBLE);

				if (!addressListBase.isEmpty()) {

					String address;
					// Check number of AddressLine before using the second
					if (addressListBase.get(0).getMaxAddressLineIndex() > 0) {
						address = addressListBase.get(0).getAddressLine(0) + ", " + addressListBase.get(0).getAddressLine(1);
					} else {
						address = addressListBase.get(0).getAddressLine(0);
					}

					etReportAddress.setText(address);
				}
			}
		}.execute();
	}

	@Override
	public void onMapReady(GoogleMap gMap) {

		googleMap = gMap;
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
		} else {
			googleMap.setMyLocationEnabled(true);
		}
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater()));
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(getApplicationContext(),
					R.string.null_map, Toast.LENGTH_SHORT)
					.show();
		}

		// Bloco abaixo foi adicionado pra evitar o erro de null em CameraUpdateFactory
		try {
			MapsInitializer.initialize(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
			@Override
			public void onCameraMoveStarted(int i) {
				ObjectAnimator liftLocationIndicator = ObjectAnimator.ofFloat(locationIndicator,"translationY", Utils.getPixelValue(ReportActivity.this, -10));
				liftLocationIndicator.setDuration(150);
				liftLocationIndicator.start();
			}
		});

		googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
			@Override
			public void onCameraIdle() {

				ObjectAnimator lowerLocationIndicator = ObjectAnimator.ofFloat(locationIndicator,"translationY", 0);
				lowerLocationIndicator.setDuration(150);
				lowerLocationIndicator.start();

				geocodeLatLng(googleMap.getCameraPosition().target);
			}
		});

		setUserLocation();
	}

	public void findAddress(View view) {

		addressList.clear();
		addressListBase.clear();

		// Create a geocoder object
		final Geocoder geoCoder = new Geocoder(this);

		// Get the string from the EditText
		final String s_address = etReportAddress.getText().toString();

		new AsyncTask<String, String, String>() {

			@Override
			protected void onPreExecute() {
				btClearAddress.setVisibility(View.GONE);
				pBar.setVisibility(View.VISIBLE);
			}

			@Override
			protected String doInBackground(String... params) {

				//Checar primeiro se algo foi digitado.
				if (!s_address.trim().equals("")) {
					try {
						addressListBase = geoCoder.getFromLocationName(s_address, 5);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					cancel(true);
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				pBar.setVisibility(View.GONE);
				btClearAddress.setVisibility(View.VISIBLE);

				final ArrayList<Address> addressList = new ArrayList<>();

				// Manual bounding box for addresses
				for (int i = 0; i < addressListBase.size(); i++) {
					addressList.add(addressListBase.get(i));
				}
				String[] s_addressList = null;
				ArrayList<String> array_address = new ArrayList<>();

				// Checar se o endere�o n�o por acaso foi encontrado. Caso negativo, ent�o lan�ar o AlertDialog no "else".
				if (!addressList.isEmpty()) {

					// Create String[] with addresses
					for (int i = 0; i < addressList.size(); i++) {

						// Check number of AddressLine before using the second
						if (addressList.get(i).getMaxAddressLineIndex() > 0) {
							array_address.add(addressList.get(i).getAddressLine(0) + ", "
									+ addressList.get(i).getAddressLine(1));
						} else {
							array_address.add(addressList.get(i).getAddressLine(0));
						}

						s_addressList = new String[array_address.size()];
						s_addressList = array_address.toArray(s_addressList);
					}

					if (addressList.size() == 1) {
						Address address = addressList.get(0);
						if (address.hasLatitude() && address.hasLongitude()) {
							double lat = address.getLatitude();
							double lng = address.getLongitude();
							LatLng coordinate = new LatLng(lat, lng);

							CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
							CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.6f);
							googleMap.animateCamera(center);
							googleMap.animateCamera(zoom);
                            geocodeLatLng(coordinate);
						}
					} else {
						AlertDialog.Builder alert_enderecos = new AlertDialog.Builder(ReportActivity.this);
						alert_enderecos.setTitle(R.string.which_address)
								.setItems(s_addressList, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Address address = addressList.get(which);
										if (address.hasLatitude() && address.hasLongitude()) {
											double lat = address.getLatitude();
											double lng = address.getLongitude();
											LatLng coordinate = new LatLng(lat, lng);

											CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
											CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.6f);
											googleMap.moveCamera(center);
											googleMap.animateCamera(zoom);
                                            geocodeLatLng(coordinate);
										}
									}
								});
						alert_enderecos.show();
					}
				} else {
					AlertDialog.Builder alert = new AlertDialog.Builder(ReportActivity.this);
					alert.setTitle(R.string.end_nao_encontrado_titulo)
							.setMessage(R.string.end_nao_encontrado_mensagem)
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});

					// Create the AlertDialog object and return it
					alert.show();
				}
			}

			protected void onCancelled() {

				// resetSearchingButton();
			}
		}.execute();
	}

	public void Report(View view) {

		String address = etReportAddress.getText().toString();

		if (isNetworkAvailable()) {

			reportLatLng = googleMap.getCameraPosition().target;

			String type = "";
			RadioButton rb_bur = (RadioButton) findViewById(R.id.report_buraco);
			RadioButton rb_sin = (RadioButton) findViewById(R.id.report_sinalização);
			RadioButton rb_out = (RadioButton) findViewById(R.id.report_outro);

			if (!rb_bur.isChecked() && !rb_sin.isChecked() && !rb_out.isChecked()) {
				Toast.makeText(this, R.string.rbReportError, Toast.LENGTH_SHORT).show();
			} else if (rb_out.isChecked() && etReportDetails.getText().toString().trim().equals("")){
				etReportDetails.setError(getResources().getString(R.string.etReportDetailsError));
			} else {

				if (rb_bur.isChecked()) {
					type = "bu";
				} else if (rb_sin.isChecked()) {
					type = "si";
				} else if (rb_out.isChecked()) {
					type = "ou";
				}

				String messageString = etReportDetails.getText().toString();

				Calls.sendAlert(Constant.TOKEN, address, String.valueOf(reportLatLng.latitude), String.valueOf(reportLatLng.longitude), type, messageString, new CallHandler() {
					@Override
					public void onSuccess(int responseCode, String response) {
						super.onSuccess(responseCode, response);
						Utils.showThanksToast(ReportActivity.this);
						finish();
					}

					@Override
					public void onFailure(int responseCode, String response) {
						super.onFailure(responseCode, response);

						Utils.showErrorToast(ReportActivity.this);

					}
				});
			}
		} else {

			AlertDialog.Builder network_alert = new AlertDialog.Builder(ReportActivity.this);
			network_alert.setTitle(R.string.network_alert_title)
					.setMessage(R.string.network_alert_dialog)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					})
					.setNegativeButton(R.string.network_settings, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
					});
			network_alert.show();
		}
	}

	public void setUserLocation() {

		CameraUpdate cameraUpdate;

		//Store last known location in "location" variable
		LatLng latLng_sp = new LatLng(-23.550765, -46.630437);
		if (user_latlng != null && user_latlng.latitude != 0 && user_latlng.longitude != 0) {
			double lat = user_latlng.latitude;
			double lng = user_latlng.longitude;

			if (lat > -23.778678 && lat < -23.400375 && lng > -46.773075 && lng < -46.355934) {
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 17);
			} else {
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng_sp, 12);
			}

            geocodeLatLng(user_latlng);
			googleMap.moveCamera(cameraUpdate);

		} else {

			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
			} else {
				Criteria criteria = new Criteria();
				criteria.setSpeedRequired(false);
				criteria.setAltitudeRequired(false);
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				LocationManager locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
				locationManager.requestSingleUpdate(criteria, this, null);
			}

		}

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
	}

	@Override
	public void onLocationChanged(Location location) {
		user_latlng = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 17);
        geocodeLatLng(user_latlng);
		googleMap.moveCamera(cameraUpdate);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Criteria criteria = new Criteria();
					criteria.setSpeedRequired(false);
					criteria.setAltitudeRequired(false);
					criteria.setAccuracy(Criteria.ACCURACY_FINE);
					LocationManager locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
					if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
						locationManager.requestSingleUpdate(criteria, this, null);
					}

				}
		}
	}

					@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
}
