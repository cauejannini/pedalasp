package jannini.android.ciclosp.NetworkRequests;

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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jannini.android.ciclosp.Constant;

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
}
