package jannini.android.ciclosp.NetworkRequests;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import jannini.android.ciclosp.Constant;

public class NotifySolvedReport {

	public void sendReport (final String timestamp){
		
		new AsyncTask<String, String, String>() {
			
			protected String doInBackground(String... args) {

				try {

					URL url = new URL(Constant.url_notify_solved);

					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setFixedLengthStreamingMode(29);
					connection.setRequestMethod("POST");

					// Criar o OutputStream para carregar a mensagem
					OutputStream os = connection.getOutputStream();
					BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
					buffWriter.write("timestamp="+timestamp);
					buffWriter.flush();
					buffWriter.close();
					os.close();

					connection.connect();

					InputStream is = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					is.close();
					String response = sb.toString();

					Log.i("NS Response", response);

				} catch (IOException e) {
					e.printStackTrace();
				}

			    return null;
			}
			
		}.execute();
	}

}
