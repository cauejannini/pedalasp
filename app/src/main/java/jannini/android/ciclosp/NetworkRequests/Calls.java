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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Calls {

	public static void sendReport (final String address, final String lat, final String lng, final String type, final String message){
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date currentLocalTime = cal.getTime();
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		final String timestamp = date.format(currentLocalTime);
		
		new AsyncTask<String, String, String>() {
			
			protected String doInBackground(String... args) {

				try {

					URL url = new URL("http://pedalasp.org/dbaccess/report_bikelane_problem.php");

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

}