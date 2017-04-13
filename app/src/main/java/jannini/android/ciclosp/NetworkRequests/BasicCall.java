package jannini.android.ciclosp.NetworkRequests;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

class BasicCall {

	private HashMap<String, String> headers = new HashMap<>();

	BasicCall() {}

	void addHeader(String key, String value) {
		headers.put(key, value);
	}

	void postRequest(final String stringUrl, final HashMap<String, String> mappedParams, final CallHandler handler) {

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				URL url;
				HttpURLConnection connection = null;

				String params = "";

				for (HashMap.Entry<String, String> entry : mappedParams.entrySet()) {
					if (params.length() > 0) {
						params += "&";
					}
					params += entry.getKey() + "=" + entry.getValue();
				}

				try {
					url = new URL(stringUrl);

					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();

					connection.setDoInput(true);
					connection.setRequestMethod("POST");
					for (HashMap.Entry<String, String> entry : headers.entrySet()) {
						connection.addRequestProperty(entry.getKey(), entry.getValue());
					}

					connection.setDoOutput(true);
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
					return new ResponseWrapper(500, "Exception: " + e.toString());

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

	void putRequest(final String stringUrl, final HashMap<String, String> mappedParams, final CallHandler handler) {

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				URL url;
				HttpURLConnection connection = null;

				String params = "";

				for (HashMap.Entry<String, String> entry : mappedParams.entrySet()) {
					if (params.length() > 0) {
						params += "&";
					}
					params += entry.getKey() + "=" + entry.getValue();
				}

				try {
					url = new URL(stringUrl);

					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();

					connection.setDoInput(true);
					connection.setRequestMethod("PUT");
					for (HashMap.Entry<String, String> entry : headers.entrySet()) {
						connection.addRequestProperty(entry.getKey(), entry.getValue());
					}

					connection.setDoOutput(true);
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
					return new ResponseWrapper(500, "Exception: " + e.toString());

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

	void getRequest(String stringUrl, final HashMap<String, String> mappedParams, final CallHandler handler) {

		String params = "";

		for (HashMap.Entry<String, String> entry : mappedParams.entrySet()) {
			if (params.length() > 0) {params += "&";}
			params += entry.getKey() + "=" + entry.getValue();
		}
		if (!params.equals("")) {stringUrl += "?" + params;}

		final String finalUrl = stringUrl;

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				URL url;
				HttpURLConnection connection = null;

				try {
					url = new URL(finalUrl);

					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();

					connection.setDoInput(true);
					connection.setRequestMethod("GET");
					for (HashMap.Entry<String, String> entry : headers.entrySet()) {
						connection.addRequestProperty(entry.getKey(), entry.getValue());
					}

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
					return new ResponseWrapper(500, "Exception: " + e.toString());

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

	void deleteRequest(String stringUrl, final HashMap<String, String> mappedParams, final CallHandler handler) {

		String params = "";

		for (HashMap.Entry<String, String> entry : mappedParams.entrySet()) {
			if (params.length() > 0) {params += "&";}
			params += entry.getKey() + "=" + entry.getValue();
		}
		if (!params.equals("")) {stringUrl += "?" + params;}

		final String finalUrl = stringUrl;

		new AsyncTask<String, String, ResponseWrapper>() {

			protected ResponseWrapper doInBackground(String... args) {
				// Set up the URL
				URL url;
				HttpURLConnection connection = null;

				try {
					url = new URL(finalUrl);

					// Obtain connection object
					connection = (HttpURLConnection) url.openConnection();

					connection.setDoInput(true);
					connection.setRequestMethod("DELETE");
					for (HashMap.Entry<String, String> entry : headers.entrySet()) {
						connection.addRequestProperty(entry.getKey(), entry.getValue());
					}

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
					return new ResponseWrapper(500, "Exception: " + e.toString());

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
}