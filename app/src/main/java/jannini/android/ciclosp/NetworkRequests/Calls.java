package jannini.android.ciclosp.NetworkRequests;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;

import static jannini.android.ciclosp.Constant.mapPlaceCategories;

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
					return new ResponseWrapper(400, "Exception: " + e.toString());

				} finally {
					assert connection != null;
					connection.disconnect();
				}
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				handler.onResponse(wrapper.responseCode, wrapper.response);
			}

		}.execute();
	}

	public static void postRequest (final String stringUrl, final HashMap<String, String> mappedParams, final CallHandler handler) {

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				URL url;
				HttpURLConnection connection = null;

				String params = "";

				for (HashMap.Entry<String, String> entry : mappedParams.entrySet()) {

					if (params.length()>0) { params += "&"; }

					params += entry.getKey() + "=" + entry.getValue();
				}

				try {
					url = new URL(stringUrl);

					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write(params);
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
					return new ResponseWrapper(400, "Exception: " + e.toString());

				} finally {
					assert connection != null;
					connection.disconnect();
				}
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

				} catch (Exception e) {
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

				} catch (Exception e) {
					e.printStackTrace();
					return new ResponseWrapper(400, "sendParkedHere Exception: " + e.toString());
				}
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

				} catch (Exception e) {
					e.printStackTrace();
					return new ResponseWrapper(400, "sendOriginDestination Exception: " + e.toString());
				}
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				if (handler != null) {
					handler.onResponse(wrapper.responseCode, wrapper.response);
				}
			}
		}.execute();


	}

	public static void createDevice (final CallHandler handler) {

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date currentLocalTime = cal.getTime();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String timestamp = date.format(currentLocalTime);

		final String model = Build.MODEL;

		HashMap<String, String> map = new HashMap<>();
		map.put("model", model);
		map.put("timestamp", timestamp);

		Calls.postRequest(Constant.url_create_device, map, handler);

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
					return new ResponseWrapper(400, "getDirections Exception: " + e.toString());

				} finally {
					assert connection != null;
					connection.disconnect();
				}
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
					return new ResponseWrapper(400, "getElevationLists Exception: " + e.toString());

				} finally {
					assert connection != null;
					connection.disconnect();
				}
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

	public static void addParaciclo (
			final String address,
			final String lat,
			final String lng,
			final int vagas,
			final CallHandler handler) {

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {

				String response = "";

				try {

					URL url = new URL(Constant.url_add_paraciclo);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write(
									"lat="+lat+"&"+
									"lng="+lng+"&"+
									"address="+address+"&"+
									"vagas="+String.valueOf(vagas)+"&"+
									"added_by=user"

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
					response = sb.toString();

					Log.e("addPlace rCode:", "" + responseCode);

					return new ResponseWrapper(responseCode, response);

				} catch (Exception e) {
					e.printStackTrace();
					return new ResponseWrapper(400, "addParaciclo Exception: " + e.toString() + "| Response: " + response);
				}
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				handler.onResponse(wrapper.responseCode, wrapper.response);
			}
		}.execute();
	}

	public static void addEstabelecimento (
			final String name,
			final String address,
			final String locality,
			final String lat,
			final String lng,
			final String phone,
			final String email,
			final String categories,
			final String otherServices,
			final CallHandler handler) {

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date currentLocalTime = cal.getTime();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String timestamp = date.format(currentLocalTime);

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {

				String response = "";

				try {

					URL url = new URL(Constant.url_add_place);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write("name="+name+"&"+
							"address="+address+"&"+
							"locality="+locality+"&"+
							"lat="+lat+"&"+
							"lng="+lng+"&"+
							"phone="+phone+"&"+
							"email="+email+"&"+
							"categories="+categories+"&"+
							"other_services="+otherServices+"&"+
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
					response = sb.toString();

					return new ResponseWrapper(responseCode, response);

				} catch (Exception e) {
					e.printStackTrace();
					return new ResponseWrapper(400, "addPlace Exception: " + e.toString() + "| Response: " + response);
				}
			}

			@Override
			protected void onPostExecute(ResponseWrapper wrapper) {
				handler.onResponse(wrapper.responseCode, wrapper.response);
			}
		}.execute();
	}

	public static void getPlacesIconsAndCategories(final Context context, final CallHandler handler) {

		jsonRequest(Constant.url_get_places_icons_paths, new CallHandler() {

			@Override
			public void onSuccess(int responseCode, String response) {

				try {
					JSONArray jarray = new JSONArray(response);
					String densityString = Utils.getDeviceDensityString(context);
					for (int i = 0; i < jarray.length(); i++) {
						JSONObject job = jarray.getJSONObject(i);
						int id = job.getInt("id");
						String imagePath = job.getString("imagePath");

						// Create variable finalId to check when final image is put in HashMap
						int finalId = 0;
						if (i == jarray.length()-1) {
							finalId = id;
						}
						final int finalId1 = finalId;

						String url = Constant.baseurl_images + densityString + "/" + imagePath;

						getImageFromUrl(url, id, new BitmapCallHandler() {
							@Override
							public void onSuccess (Bitmap bitmap, int imageId) {

								Constant.mapPlaceIcon.put(imageId, bitmap);

								// Call handler if this was the last image
								if (finalId1 != 0 && finalId1 == imageId) {

									getPlacesCategories(context, new CallHandler() {
										@Override
										public void onSuccess(int responseCode, String response) {
											super.onSuccess(responseCode, response);
											handler.onResponse(200, "CATEGORIES OK, ICONS OK");
										}

										@Override
										public void onFailure(int responseCode, String response) {
											super.onFailure(responseCode, response);
											handler.onResponse(205, "CATEGORIES FAILED, ICONS OK");
										}
									});
								}
							}

							@Override
							public void onFailure (int imageId) {
								Log.e("getImageFromUrl", "Fail for imageId:" + String.valueOf(imageId));

								// Call handler if this was the last image
								if (finalId1 != 0 && finalId1 == imageId) {
									getPlacesCategories(context, new CallHandler() {
										@Override
										public void onSuccess(int responseCode, String response) {
											super.onSuccess(responseCode, response);
											handler.onResponse(205, "CATEGORIES OK, ICONS FAILED");
										}

										@Override
										public void onFailure(int responseCode, String response) {
											super.onFailure(responseCode, response);
											handler.onResponse(400, "Category response:" + response);
										}
									});
								}
							}
						});
					}

				} catch (final Exception e) {
					e.printStackTrace();
					getPlacesCategories(context, new CallHandler() {
						@Override
						public void onSuccess(int responseCode, String response) {
							super.onSuccess(responseCode, response);
							handler.onResponse(205, "CATEGORIES OK, ICONS EXCEPTION: "+e.toString());
						}

						@Override
						public void onFailure(int responseCode, String response) {
							super.onFailure(responseCode, response);
							handler.onResponse(400, "CATEGORIES FAILED: "+ response +" | ICONS EXCEPTION: " + e.toString());
						}
					});
				}

			}

			@Override
			public void onFailure(int responseCode, final String iconPathsResponse) {
				getPlacesCategories(context, new CallHandler() {
					@Override
					public void onSuccess(int responseCode, String response) {
						super.onSuccess(responseCode, response);
						handler.onResponse(205, "CATEGORIES OK, ICONS PATHS FAILED: "+ iconPathsResponse);
					}

					@Override
					public void onFailure(int responseCode, String response) {
						super.onFailure(responseCode, response);
						handler.onResponse(400, "CATEGORIES FAILED: "+ response +" | ICONS EXCEPTION: " + iconPathsResponse);
					}
				});
			}
		});



	}

	public static void getPlacesCategories (final Context context, final CallHandler handler) {

		jsonRequest(Constant.url_get_categories, new CallHandler() {
			@Override
			public void onSuccess(int responseCode, String response) {
				super.onSuccess(responseCode, response);

				try {
					JSONArray jarrayCategories = new JSONArray(response);
					String densityString = Utils.getDeviceDensityString(context);

					for (int i = 0; i < jarrayCategories.length(); i++) {
						JSONObject jobCategory = jarrayCategories.getJSONObject(i);

						int categoryId = jobCategory.getInt("id");
						String categoryDisplayName = jobCategory.getString("name_pt_br");
						if (!categoryDisplayName.trim().equals("")) {
							mapPlaceCategories.put(categoryId, categoryDisplayName);
							if (!Constant.PlaceCategoriesStates.containsKey(categoryId)) {
								Constant.PlaceCategoriesStates.put(categoryId, true);
							}
						}

						String imagePath = jobCategory.getString("path");

						// Create variable finalId to check when final image is put in HashMap
						int finalId = 0;
						if (i == jarrayCategories.length()-1) {
							finalId = categoryId;
						}
						final int finalId1 = finalId;

						String url = Constant.baseurl_images + densityString + "/" + imagePath;

						getImageFromUrl(url, categoryId, new BitmapCallHandler() {
							@Override
							public void onSuccess (Bitmap bitmap, int imageId) {

								Constant.mapCategoriesIcons.put(imageId, bitmap);

								// Call handler if this was the last image
								if (finalId1 != 0 && finalId1 == imageId) {
									handler.onResponse(200, "Category images HashMap OK");
								}
							}

							@Override
							public void onFailure (int imageId) {
								Log.e("getImageFromUrl", "Fail for categoryId:" + String.valueOf(imageId));
								if (finalId1 != 0 && finalId1 == imageId) {
									handler.onResponse(400, "getImageFromUrl Failed: " + "For category id: " + imageId);
								}
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.onResponse(400, "getCategoriesIconsImages Exception: " + e.toString());
				}
			}

			@Override
			public void onFailure(int responseCode, String response) {
				super.onFailure(responseCode, response);
				Log.e("getCategoriesIconsPaths", response);
				handler.onResponse(400, "getCategoriesIconsImages onFailure: " + response);
			}
		});
	}

	public static void getImageFromUrl (final String stringUrl, final int imageId, final BitmapCallHandler bitmapHandler) {
		new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap image = null;
				try {
					URL url = new URL(stringUrl);

					image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return image;
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				super.onPostExecute(bitmap);
				if (bitmap != null) {
					bitmapHandler.onResponse(200, bitmap, imageId);
				} else {
					bitmapHandler.onResponse(400, null, imageId);
				}
			}
		}.execute();
	}

	public static void getImageForPlaceId (final Context context, final int placeId, final BitmapCallHandler bitmapHandler) {
		new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				Bitmap image = null;
				try {
					URL url = new URL(Constant.urlGetImageForPlaceId);

					String densityString = Utils.getDeviceDensityString(context);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write("place_id="+placeId+"&"
									+"device_density="+densityString);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					image = BitmapFactory.decodeStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return image;
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				super.onPostExecute(bitmap);
				if (bitmap != null) {
					bitmapHandler.onResponse(200, bitmap, 0);
				} else {
					bitmapHandler.onResponse(400, null, 0);
				}
			}
		}.execute();
	}

	public static void getFeaturedForLocation (LatLng userLatLng, CallHandler handler) {

		String userLat = String.valueOf(userLatLng.latitude);
		String userLng = String.valueOf(userLatLng.longitude);

		HashMap<String, String> map = new HashMap<>();
		map.put("user_lat", userLat);
		map.put("user_lng", userLng);

		postRequest(Constant.url_get_featured_list_for_location, map, handler);
	}

	public static void getFeaturedForId (String featuredId, CallHandler handler) {

		HashMap<String, String> map = new HashMap<>();
		map.put("featured_id", featuredId);

		postRequest(Constant.url_get_featured_for_id, map, handler);

	}

	public static void getFeaturedListForPlaceId (String placeId, CallHandler handler) {

		HashMap<String, String> map = new HashMap<>();
		map.put("place_id", placeId);

		postRequest(Constant.url_get_featured_list_for_place_id, map, handler);

	}

	public static void getAllFeatured(CallHandler handler) {

		jsonRequest(Constant.url_get_all_featured, handler);
	}

	public static void getPlaceOpeningHours (String placeId, CallHandler handler) {

		HashMap<String, String> map = new HashMap<>();
		map.put("place_id", placeId);

		postRequest(Constant.url_get_op_hours_for_place_id, map, handler);
	}

	public static void updatePlaceInformation (String placeId,
									String name,
									String address,
									String locality,
									String lat,
									String lng,
									String phone,
									String email,
									String categories, String otherServices,
									final CallHandler handler) {

		HashMap<String, String> map = new HashMap<>();
		map.put("place_id", placeId);
		map.put("name", name);
		map.put("address", address);
		map.put("locality", locality);
		map.put("lat", lat);
		map.put("lng", lng);
		map.put("email", email);
		map.put("phone", phone);
		map.put("categories", categories);
		map.put("otherServices", otherServices);

		postRequest(Constant.url_update_place_information, map, handler);

	}

	public static void flagInexistentPlace (String placeId, CallHandler handler) {

		HashMap<String, String> map = new HashMap<>();
		map.put("place_id", placeId);

		postRequest(Constant.url_flag_place_inexistent, map, handler);

	}

	public static void getVoucherForDeviceId(String deviceId, String featuredId, CallHandler handler) {

		HashMap<String, String> map = new HashMap<>();
		map.put("device_id", deviceId);
		map.put("featured_id", featuredId);

		Calls.postRequest(Constant.url_get_voucher_for_user_id, map, handler);
	}

}
