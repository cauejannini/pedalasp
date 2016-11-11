package jannini.android.ciclosp.NetworkRequests;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    // function get json from url
    // by making HTTP POST or GET mehtod

    public JSONObject makeHttpRequest (String s_url) {

        // Set up the URL
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(s_url);
            // Obtain connection object
            connection = (HttpURLConnection) url.openConnection();

            assert connection != null;
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            json = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        } finally {
            assert connection != null;
            connection.disconnect();
        }


        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

}