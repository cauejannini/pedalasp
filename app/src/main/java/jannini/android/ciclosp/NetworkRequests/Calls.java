package jannini.android.ciclosp.NetworkRequests;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;

public class Calls {

	public static void jsonRequest (final String s_url, final CallHandler handler) {

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				URL url;
				HttpURLConnection connection = null;
				try {
					url = new URL(s_url);
					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();

					assert connection != null;
					InputStream is = new BufferedInputStream(connection.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();
					int code = connection.getResponseCode();
					return new ResponseWrapper(code, response);

				} catch (Exception e) {
					Log.e("Buffer Error", "Error converting result " + e.toString());

				} finally {
					assert connection != null;
					connection.disconnect();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				handler.onResponse(wrapper.responseCode, wrapper.response);
			}

		}.execute();
	}

	public static void sendReport (
			final String address,
			final String lat,
			final String lng,
			final String type,
			final String message) {
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date currentLocalTime = cal.getTime();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		final String timestamp = date.format(currentLocalTime);
		
		new AsyncTask<String, String, String>() {
			
			protected String doInBackground(String... args) {

				try {

					URL url = new URL(Constant.url_report);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write("name="+""+"&"+
									"email="+""+"&"+
									"address="+address+"&"+
									"lat="+lat+"&"+
									"lng="+lng+"&"+
									"type="+type+"&"+
									"message="+message+"&"+
									"timestamp="+timestamp
					);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					InputStream is = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();

					Log.i("RR.sendReport response", response);

				} catch (IOException e) {
					e.printStackTrace();
				}

		        return null;
			}
		
		}.execute();
		
		
	}

	public static void sendParkedHere (
			final String deviceID,
			final String lat,
			final String lng,
			final CallHandler handler) {

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date currentLocalTime = cal.getTime();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String timestamp = date.format(currentLocalTime);

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {

				try {

					URL url = new URL(Constant.url_send_parkedHere);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write(
							"device_id="+deviceID+"&"+
							"lat="+lat+"&"+
							"lng="+lng+"&"+
							"timestamp="+timestamp
					);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					InputStream is = connection.getInputStream();
					int responseCode = connection.getResponseCode();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();

					Log.i("sendParkedHere response", response);

					return new ResponseWrapper(responseCode, response);

				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				if (handler != null) {
					handler.onResponse(wrapper.responseCode, wrapper.response);
				}
			}
		}.execute();
	}

	public static void sendOriginDestination (
			final String deviceID,
			LatLng originLatLng,
			LatLng destinationLatLng,
			Double distance,
			Double maxInclination,
			final CallHandler handler) {

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date currentLocalTime = cal.getTime();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String timestamp = date.format(currentLocalTime);

		final String originLat = String.valueOf(originLatLng.latitude);
		final String originLng = String.valueOf(originLatLng.longitude);
		final String destinationLat = String.valueOf(destinationLatLng.latitude);
		final String destinationLng = String.valueOf(destinationLatLng.longitude);

		final String strDistance = String.valueOf(distance);
		final String strMaxInclination = String.valueOf(maxInclination);

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {

				try {

					URL url = new URL(Constant.url_send_originDestination);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write(
							"device_id="+deviceID+"&"+
									"origin_lat="+originLat+"&"+
									"origin_lng="+originLng+"&"+
									"destination_lat="+destinationLat+"&"+
									"destination_lng="+destinationLng+"&"+
									"distance="+strDistance+"&"+
									"max_inclination="+strMaxInclination+"&"+
									"timestamp="+timestamp
					);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					InputStream is = connection.getInputStream();
					int responseCode = connection.getResponseCode();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();

					Log.e("sendOriginDestination", response);

					return new ResponseWrapper(responseCode, response);

				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				if (handler != null) {
					handler.onResponse(wrapper.responseCode, wrapper.response);
				}
			}
		}.execute();


	}

	public static void createDevice (
			final CallHandler handler) {

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date currentLocalTime = cal.getTime();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String timestamp = date.format(currentLocalTime);

		final String model = Build.MODEL;

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {

				try {

					URL url = new URL(Constant.url_create_device);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write(
							"model="+model+"&"+
									"timestamp="+timestamp
					);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					InputStream is = connection.getInputStream();
					int responseCode = connection.getResponseCode();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();

					Log.e("createDeviceID response", response);

					return new ResponseWrapper(responseCode, response);

				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				if (handler != null) {
					handler.onResponse(wrapper.responseCode, wrapper.response);
				}
			}
		}.execute();


	}

	public static void getDirections (final LatLng latLngOrigin, final LatLng latLngDestination, final CallHandler handler) {

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				URL url;
				HttpURLConnection connection = null;
				try {
					url = new URL(Constant.urlGetDirections);
					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					String oLat = String.valueOf(latLngOrigin.latitude);
					String oLng = String.valueOf(latLngOrigin.longitude);
					String dLat = String.valueOf(latLngDestination.latitude);
					String dLng = String.valueOf(latLngDestination.longitude);

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write(
							"oLat=" + oLat + "&" +
							"oLng=" + oLng + "&" +
							"dLat=" + dLat + "&" +
							"dLng=" + dLng
					);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					InputStream is = new BufferedInputStream(connection.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();
					int code = connection.getResponseCode();
					return new ResponseWrapper(code, response);

				} catch (Exception e) {
					Log.e("Buffer Error", "Error converting result " + e.toString());

				} finally {
					assert connection != null;
					connection.disconnect();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				handler.onResponse(wrapper.responseCode, wrapper.response);
			}

		}.execute();
	}

	static void getElevationLists (
			final ArrayList<String> encodedPathList,
			final ArrayList<Integer> samplesNumbers,
			final CallHandler handler) {

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				HttpURLConnection connection = null;
				try {
					URL url = new URL(Constant.urlGetElevationForUrls);
					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar variáveis strings para enviar no OutputStream
					String encodedPathListString = "";
					for (String encodedPath : encodedPathList) {
						encodedPathListString = encodedPathListString + "," + encodedPath;
					}

					String sampleNumbersString = "";
					for (int sampleNumber : samplesNumbers) {
						sampleNumbersString = sampleNumbersString + "," + String.valueOf(sampleNumber);
					}

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write(
							"encodedPathList=" + encodedPathListString + "&" +
									"sampleNumbers=" + sampleNumbersString + "&" +
									"key=" + Constant.elevationAuthKey
					);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					InputStream is = connection.getInputStream();
					int responseCode = connection.getResponseCode();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();

					return new ResponseWrapper(responseCode, response);

				} catch (Exception e) {
					Log.e("Buffer Error", "Error converting result " + e.toString());

				} finally {
					assert connection != null;
					connection.disconnect();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				handler.onResponse(wrapper.responseCode, wrapper.response);
			}
		}.execute();
	}

	public static void getAddressFromString (final Context context, final String address, final GeocoderCallHandler handler) {

		Log.e("getAddressFromString", "here");
		final Geocoder geocoder = new Geocoder(context);

		new AsyncTask<String, String, List<Address>>() {

			protected List<Address> doInBackground(String... args) {

				List<Address> addressListFromGeocoder = new ArrayList<>();
				try {
					addressListFromGeocoder = geocoder.getFromLocationName(address, 20, Constant.llLat, Constant.llLng, Constant.urLat, Constant.urLng);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return addressListFromGeocoder;
			}

			@Override
			protected void onPostExecute (List<Address> addressListFromGeocoder){

				if (addressListFromGeocoder.isEmpty()) {
					handler.onResponse(2, null);
				} else if (addressListFromGeocoder.size() == 1) {
					handler.onResponse(1, addressListFromGeocoder.get(0));
				} else {

					final ArrayList<Address> reorganizedAddressList = new ArrayList<>();
					int u = 0;

					for (int i = 0; i < addressListFromGeocoder.size(); i++) {

						Address ad = addressListFromGeocoder.get(i);

						// Place locations in São Paulo on top of list
						if (i != 0 && ad.getLocality() != null) {
							if (ad.getLocality().equalsIgnoreCase("São Paulo")) {
								reorganizedAddressList.add(reorganizedAddressList.get(u));
								reorganizedAddressList.set(u, ad);
								u++;
							}
						} else if (i != 0 && ad.getSubAdminArea() != null) {
							if (ad.getSubAdminArea().equalsIgnoreCase("São Paulo")) {
								reorganizedAddressList.add(reorganizedAddressList.get(u));
								reorganizedAddressList.set(u, ad);
								u++;
							}
						} else {
							reorganizedAddressList.add(ad);
						}
					}

					String[] s_addressList = null;
					ArrayList<String> array_address = new ArrayList<>();

					// Create String[] with addresses, limiting to 5 addresses.
					for (int i = 0; i < reorganizedAddressList.size() && i < 5; i++) {

						// Check number of AddressLine before using the second
						if (reorganizedAddressList.get(i).getMaxAddressLineIndex() > 0) {
							array_address.add(reorganizedAddressList.get(i).getAddressLine(0) + ", "
									+ reorganizedAddressList.get(i).getAddressLine(1));
						} else {
							array_address.add(reorganizedAddressList.get(i).getAddressLine(0));
						}

						s_addressList = new String[array_address.size()];
						s_addressList = array_address.toArray(s_addressList);
					}

					AlertDialog.Builder alert_enderecos = new AlertDialog.Builder(context);
					alert_enderecos.setTitle(context.getString(R.string.which_address))
							.setItems(s_addressList, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

									Address address = reorganizedAddressList.get(which);
									if (address.hasLatitude() && address.hasLongitude()) {
										handler.onResponse(1, address);
									}

								}
							});
					alert_enderecos.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialogInterface) {
							handler.onResponse(3, null);
						}
					});
					alert_enderecos.show();
				}
			}
		}.execute();

	}
}
