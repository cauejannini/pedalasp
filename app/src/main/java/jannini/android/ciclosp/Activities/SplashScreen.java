package jannini.android.ciclosp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.R;

public class SplashScreen extends Activity {

    SharedPreferences sharedPreferences;

    ProgressBar progressBar;

    int startedCalls = 0;
    int finishedCalls = 0;

    static boolean placesImagesAndCategoriesAreLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String token = sharedPreferences.getString(Constant.SPKEY_TOKEN, "");
        if (token.trim().equals("")) {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        } else {

            Constant.TOKEN = token;

            if (isNetworkAvailable()) {

                Calls.getPlacesIconsAndCategories(this, new CallHandler() {
                    @Override
                    public void onSuccess(int responseCode, String response) {
                        Log.e("getIconsAndCategories", "CODE: " + responseCode + " | RESPONSE: " + response);
                        placesImagesAndCategoriesAreLoaded = true;

                        checkOtherMissingData();
                    }

                    @Override
                    public void onFailure(int responseCode, String response) {
                        Log.e("getIconsAndCategories", "CODE: " + responseCode + " | RESPONSE: " + response);

                        checkOtherMissingData();
                    }
                });
            } else {
                checkOtherMissingData();
            }
        }
	}

    void checkOtherMissingData() {

        if (sharedPreferences.getString(Constant.SPKEY_JARRAY_BIKE_LANES, null) == null) {
            startedCalls++;
            Log.e("SPLASH GET", "BIKE LANES");

            Calls.getBikeLanes(Constant.TOKEN, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SPKEY_JARRAY_BIKE_LANES, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {
                        proceedFromSplash();
                    }
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {
                        proceedFromSplash();
                    }
                }
            });
        }

        if (sharedPreferences.getString(Constant.SPKEY_JARRAY_SHARING_STATIONS, null) == null) {
            startedCalls++;
            Log.e("SPLASH GET", "SHARING STATIONS");

            Calls.getSharingStations(Constant.TOKEN, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    Calendar now = Calendar.getInstance();
                    String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                    String minutes = String.valueOf(now.get(Calendar.MINUTE));
                    if (minutes.length() == 1) {
                        minutes = "0" + minutes;
                    }
                    String updateTime = hours + ":" + minutes;

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SPKEY_JARRAY_SHARING_STATIONS, response);
                    editor.putString(Constant.SPKEY_SHARING_STATIONS_UPDATE_TIME, updateTime);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (sharedPreferences.getString(Constant.SPKEY_JARRAY_PARKING_SPOTS, null) == null) {
            startedCalls++;
            Log.e("SPLASH GET", "PARKING SPOTS");

            Calls.getParkingSpots(Constant.TOKEN, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SPKEY_JARRAY_PARKING_SPOTS, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (sharedPreferences.getString(Constant.SPKEY_JARRAY_WIFI_SPOTS, null) == null) {
            startedCalls++;
            Log.e("SPLASH GET", "WIFI SPOTS");

            Calls.getWifiSpots(Constant.TOKEN, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SPKEY_JARRAY_WIFI_SPOTS, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (sharedPreferences.getString(Constant.SPKEY_JARRAY_PARKS, null) == null) {
            startedCalls++;
            Log.e("SPLASH GET", "PARKS");

            Calls.getParks(Constant.TOKEN, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SPKEY_JARRAY_PARKS, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (sharedPreferences.getString(Constant.SPKEY_JARRAY_ALERTS, null) == null) {
            startedCalls++;
            Log.e("SPLASH GET", "ALERTS");

            Calls.getAlerts(Constant.TOKEN, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SPKEY_JARRAY_ALERTS, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (sharedPreferences.getString(Constant.SPKEY_JARRAY_PLACES, null) == null) {
            startedCalls++;
            Log.e("SPLASH GET", "PLACES");

            Calls.getPlaces(Constant.TOKEN, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SPKEY_JARRAY_PLACES, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (finishedCalls == startedCalls) {proceedFromSplash();}

    }

    void proceedFromSplash() {

        Calls.getUser(Constant.TOKEN, new CallHandler() {
            @Override
            public void onSuccess(int responseCode, String response) {
                super.onSuccess(responseCode, response);

                try {
                    JSONObject job = new JSONObject(response);
                    Constant.USER_NAME = job.getString("name");
                    Constant.USER_LAST_NAME = job.getString("last_name");
                    Constant.USER_EMAIL = job.getString("email");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(int responseCode, String response) {
                super.onFailure(responseCode, response);
                Log.e("getUser", "FAIL: " + response);

                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        });
    }

    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
	}
}
