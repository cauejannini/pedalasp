package jannini.android.ciclosp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.Calendar;

import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.JSONParser;
import jannini.android.ciclosp.NetworkRequests.Utils;

public class SplashScreen extends Activity {

    SharedPreferences sharedPreferences;

	// Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // String to store jSON Object on device storage
    String jsonObjString, jsonObjBSString, jsonObjCSString, jsonObjPlaces;

    ProgressBar progressBar;

    boolean isThisFirstTime = false;

    static boolean[] splashResultCodes = {false, false, false, false, false};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String deviceID = sharedPreferences.getString(Constant.SPKEY_DEVICE_ID, "");
        if (deviceID.equals("")) {
            Calls.createDevice(new CallHandler() {
                @Override
                public void onSuccess(int code, String response) {
                    sharedPreferences.edit().putString(Constant.SPKEY_DEVICE_ID, response).apply();
                }
            });
        }

        jsonObjString = sharedPreferences.getString(Constant.spJobGeral, null);
        jsonObjBSString = sharedPreferences.getString(Constant.spJobBS, null);
        jsonObjCSString = sharedPreferences.getString(Constant.spJobCS, null);
        jsonObjPlaces = sharedPreferences.getString(Constant.spJobPlaces, null);

        if (jsonObjString != null && jsonObjBSString != null && jsonObjCSString != null && jsonObjPlaces != null) {

            Calls.getPlacesIconsAndCategories(this, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    Log.e("getIconsAndCategories", "CODE: " + responseCode + " | RESPONSE: " + response);
                    splashResultCodes[4] = true;

                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onFailure(int responseCode, String response) {
                    Log.e("getIconsAndCategories", "CODE: " + responseCode + " | RESPONSE: " + response);
                    Utils.showServerErrorToast(SplashScreen.this, responseCode+ " : "+response);

                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        } else {

            if (jsonObjString == null && jsonObjBSString == null && jsonObjCSString == null && jsonObjPlaces == null) {
                isThisFirstTime = true;
            }

            // If first time and no network, block access to the app.
            if (isThisFirstTime && !isNetworkAvailable()) {
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
            } else {

                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_drawable));

                Calls.getPlacesIconsAndCategories(this, new CallHandler() {
                    @Override
                    public void onSuccess(int responseCode, String response) {
                        Log.e("getIconsAndCategories", "CODE: " + responseCode + " | RESPONSE: " + response);
                        splashResultCodes[4] = true;
                    }
                    @Override
                    public void onFailure(int responseCode, String response) {
                        Log.e("getIconsAndCategories", "CODE: " + responseCode + " | RESPONSE: " + response);
                        Utils.showServerErrorToast(SplashScreen.this, responseCode+ " : "+response);
                    }
                });

                final SharedPreferences.Editor editor = sharedPreferences.edit();

                if (jsonObjString == null) {
                    Calls.jsonRequest(Constant.url_obter_dados, new CallHandler() {
                        @Override
                        public void onSuccess(int responseCode, String response) {

                            editor.putString(Constant.spJobGeral, response);
                            editor.apply();

                            splashResultCodes[0] = true;

                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            super.onFailure(responseCode, response);
                            Log.e("splash getDataHandler", "FAIL: " + responseCode + ": " + response);
                        }
                    });
                }

                if (jsonObjPlaces == null) {
                    Calls.jsonRequest(Constant.url_get_places, new CallHandler() {
                        @Override
                        public void onSuccess(int responseCode, String response) {

                            editor.putString(Constant.spJobPlaces, response);
                            editor.apply();

                            splashResultCodes[1] = true;

                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            Log.e("splash getPlaces fail", response);
                        }
                    });
                }

                if (jsonObjBSString == null) {
                    Calls.jsonRequest(Constant.url_obter_bikesampa, new CallHandler() {
                        @Override
                        public void onSuccess(int code, String response) {
                            Log.e("splash getBSHandler", code + ": " + response);

                            Calendar now = Calendar.getInstance();
                            String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                            String minutes = String.valueOf(now.get(Calendar.MINUTE));
                            if (minutes.length() == 1) {
                                minutes = "0" + minutes;
                            }
                            String updateTimeBS = hours + ":" + minutes;

                            editor.putString(Constant.spJobBS, response);
                            editor.putString(Constant.spUpdateTimeBS, updateTimeBS);
                            editor.apply();

                            splashResultCodes[2] = true;
                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            super.onFailure(responseCode, response);
                            Log.e("splash getBSHandler", "Fail: " + responseCode + ": " + response);
                        }
                    });
                }

                if (jsonObjCSString == null) {
                    Calls.jsonRequest(Constant.url_obter_ciclosampa, new CallHandler() {
                        @Override
                        public void onSuccess(int code, String response) {
                            Log.e("splash getCSHandler", code + ": " + response);

                            Calendar now = Calendar.getInstance();
                            String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                            String minutes = String.valueOf(now.get(Calendar.MINUTE));
                            if (minutes.length() == 1) {
                                minutes = "0" + minutes;
                            }
                            String updateTimeCS = hours + ":" + minutes;

                            editor.putString(Constant.spJobCS, response);
                            editor.putString(Constant.spUpdateTimeCS, updateTimeCS);
                            editor.apply();

                            splashResultCodes[3] = true;

                            if (isThisFirstTime) {
                                Intent i = new Intent(SplashScreen.this, DrawerExpActivity.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                            }
                            finish();
                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            super.onFailure(responseCode, response);
                            Log.e("splash getCSHandler", "Fail: " + responseCode + ": " + response);

                            if (isThisFirstTime) {
                                Intent i = new Intent(SplashScreen.this, DrawerExpActivity.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                            }
                            finish();
                        }
                    });
                }
            }
        }
	}

    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
	}
}
