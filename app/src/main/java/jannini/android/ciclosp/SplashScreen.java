package jannini.android.ciclosp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import jannini.android.ciclosp.NetworkRequests.JSONParser;

public class SplashScreen extends Activity {

	// Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // url to get all items lists    
    String url_obter_dados = "http://pedalasp.org/dbaccess/obter_dados.php";
    String url_obter_bikesampa = "http://pedalasp.org/dbaccess/obter_bikesampa.php";
    String url_obter_ciclosampa = "http://pedalasp.org/dbaccess/obter_ciclosampa.php";

    // String to store jSON Object on device storage
    String jsonObjString;
    String jsonObjBSString;
    String jsonObjCSString;

    ProgressBar progressBar;

    static boolean[] result_code = {false, false, false};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_drawable));

		new CarregarDB().execute();
	}

	 // Background Async Task to Load all estacoes by making HTTP Request
	class CarregarDB extends AsyncTask<String, String, String> {

        // Checa conexão com a internet antes de começar as tarefas em background
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!isNetworkAvailable()) {
            	cancel(true);
            	AlertDialog.Builder network_alert = new AlertDialog.Builder(SplashScreen.this);
    	        network_alert.setTitle(R.string.network_alert_title)
    	        		.setMessage(R.string.network_alert_dialog_splash)
    	        		.setPositiveButton(R.string.fechar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
    	               	.setNegativeButton(R.string.network_settings, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                finish();
                            }
                        });
    	        network_alert.show();
            }
        }

        //Retorna todos os estacionamentos da URL e gaurda na ListEstacionamentos (Lista de objetos de classe "Estacionamento")
        protected String doInBackground(String... args) {

            if (!isCancelled()) {

                // Building Parameters

                JSONObject jObjGeral = jParser.makeHttpRequest(url_obter_dados);
                JSONObject jObjBikeSampa = jParser.makeHttpRequest(url_obter_bikesampa);
                JSONObject jObjCicloSampa = jParser.makeHttpRequest(url_obter_ciclosampa);

                MainActivity.pref = getApplicationContext().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
                MainActivity.editor = MainActivity.pref.edit();

                try {

                    if (jObjGeral != null) {

                        int successGeral = jObjGeral.getInt("success");

                        if (successGeral == 1) {

                            jsonObjString = jObjGeral.toString();

                            MainActivity.editor.putString("jsonObjString", jsonObjString);

                            result_code[0] = true;

                        }
                    }

                    if (jObjBikeSampa != null) {

                        int successBikeSampa = jObjBikeSampa.getInt("success");

                        if (successBikeSampa == 1) {

                            jsonObjBSString = jObjBikeSampa.toString();

                            MainActivity.editor.putString("jsonObjBSString", jsonObjBSString);

                            Calendar now = Calendar.getInstance();
                            String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                            String minutes = String.valueOf(now.get(Calendar.MINUTE));
                            if (minutes.length() == 1) {
                                minutes = "0" + minutes;
                            }
                            String updateTimeBS = hours + ":" + minutes;

                            MainActivity.editor.putString("updateTimeBS", updateTimeBS);

                            result_code[1] = true;

                        }
                    }

                    if (jObjCicloSampa != null) {

                        int successCicloSampa = jObjCicloSampa.getInt("success");

                        if (successCicloSampa == 1) {

                            jsonObjCSString = jObjCicloSampa.toString();

                            MainActivity.editor.putString("jsonObjCSString", jsonObjCSString);

                            Calendar now = Calendar.getInstance();
                            String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                            String minutes = String.valueOf(now.get(Calendar.MINUTE));
                            if (minutes.length() == 1) {
                                minutes = "0" + minutes;
                            }
                            String updateTimeCS = hours + ":" + minutes;

                            MainActivity.editor.putString("updateTimeCS", updateTimeCS);

                            result_code[2] = true;

                        }
                    }

                    MainActivity.editor.commit();

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

            return null;
        }

        // Adiciona marcadores no mapa para todos os objetos encontrados.
		protected void onPostExecute(String file_url) {

			Intent i = new Intent(SplashScreen.this, DrawerExpActivity.class);
            startActivity(i);

			finish();
		}

		protected void onCancelled(String file_url) {

		}
	}

    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
	}
}