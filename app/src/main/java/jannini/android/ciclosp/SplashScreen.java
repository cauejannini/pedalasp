package jannini.android.ciclosp;

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

import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.JSONParser;

public class SplashScreen extends Activity {

    SharedPreferences sharedPreferences;

	// Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // String to store jSON Object on device storage
    String jsonObjString, jsonObjBSString, jsonObjCSString, jsonObjPlaces;

    ProgressBar progressBar;

    boolean isThisFirstTime = false;

    int startedCalls = 0;
    int finishedCalls = 0;

    static boolean placesImagesAndCategoriesAreLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        /*String deviceID = sharedPreferences.getString(Constant.SPKEY_DEVICE_ID, "");
        if (deviceID.equals("")) {
            Calls.createDevice(new CallHandler() {
                @Override
                public void onSuccess(int code, String response) {
                    sharedPreferences.edit().putString(Constant.SPKEY_DEVICE_ID, response).apply();
                }
            });
        }*/

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

    void checkOtherMissingData() {
        String jsonObjString = sharedPreferences.getString(Constant.spJobGeral, null);
        String jsonObjBSString = sharedPreferences.getString(Constant.spJobBS, null);
        String jsonObjCSString = sharedPreferences.getString(Constant.spJobCS, null);
        String jsonObjPlaces = sharedPreferences.getString(Constant.spJobPlaces, null);

        if (jsonObjString == null) {

            startedCalls++;

            Calls.jsonRequest(Constant.url_obter_dados, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    Log.e("Splash AllData Success", responseCode + ": " + response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.spJobGeral, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {
                        proceedFromSplash();
                    }
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);
                    Log.e("Splash AllData Fail", responseCode + ": " + response);
                    finishedCalls++;
                    if (finishedCalls == startedCalls) {
                        proceedFromSplash();
                    }
                }
            });
        }

        if (jsonObjBSString == null) {

            startedCalls++;
            Calls.jsonRequest(Constant.url_obter_bikesampa, new CallHandler() {
                @Override
                public void onSuccess(int code, String response) {
                    Log.e("Splash BS Success", code + ": " + response);

                    Calendar now = Calendar.getInstance();
                    String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                    String minutes = String.valueOf(now.get(Calendar.MINUTE));
                    if (minutes.length() == 1) {
                        minutes = "0" + minutes;
                    }
                    String updateTimeBS = hours + ":" + minutes;

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.spJobBS, response);
                    editor.putString(Constant.spUpdateTimeBS, updateTimeBS);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);
                    Log.e("Splash BS Failure", responseCode + ": " + response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (jsonObjCSString == null) {

            startedCalls++;
            Calls.jsonRequest(Constant.url_obter_ciclosampa, new CallHandler() {
                @Override
                public void onSuccess(int code, String response) {
                    Log.e("Splash BS Success", code + ": " + response);

                    Calendar now = Calendar.getInstance();
                    String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
                    String minutes = String.valueOf(now.get(Calendar.MINUTE));
                    if (minutes.length() == 1) {
                        minutes = "0" + minutes;
                    }
                    String updateTimeCS = hours + ":" + minutes;

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.spJobCS, response);
                    editor.putString(Constant.spUpdateTimeCS, updateTimeCS);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);
                    Log.e("Splash CS Failure", responseCode + ": " + response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (jsonObjPlaces == null) {

            startedCalls++;
            Calls.jsonRequest(Constant.url_get_places, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    Log.e("Splash Places Success", response);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.spJobPlaces, response);
                    editor.apply();

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    Log.e("Splash Places Failure", response);

                    finishedCalls++;
                    if (finishedCalls == startedCalls) {proceedFromSplash();}
                }
            });
        }

        if (finishedCalls == startedCalls) {proceedFromSplash();}

    }

    void proceedFromSplash() {
        boolean isUserLogged = sharedPreferences.getBoolean(Constant.SPKEY_USER_LOGGED_IN, false);
        int userId = sharedPreferences.getInt(Constant.SPKEY_USER_ID, 0);
        if (isUserLogged && userId != 0) {

            Constant.USER_ID = userId;
            Calls.getUser(userId, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);
                    Log.e("getUser", "SUCCESS: " + response);


                    try {
                        JSONObject job = new JSONObject(response);
                        Constant.USER_NAME = job.getString("NAME");
                        Constant.USER_LAST_NAME = job.getString("LAST_NAME");
                        Constant.USER_EMAIL = job.getString("EMAIL");

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

        } else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
	}
}
