package jannini.android.ciclosp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import jannini.android.ciclosp.Fragments.ParacicloF;
import jannini.android.ciclosp.Fragments.PlaceCategoryF;
import jannini.android.ciclosp.Fragments.PlaceInfoF;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

public class AddToMapActivity extends Activity
        implements  PlaceInfoF.OnEIFragmentInteractionListener,
        PlaceCategoryF.OnECFragmentInteractionListener,
        ParacicloF.OnParacicloFragmentInteractionListener {

    public static String name, phone, email, address, locality = "";
    public static String functionSelected = "";
    public static ArrayList<Integer> categoryIdList;
    public static int placeId;

    public static LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estabelecimento);

        Double userLat = getIntent().getDoubleExtra("lat", Constant.latLngCity.latitude);
        Double userLng = getIntent().getDoubleExtra("lng", Constant.latLngCity.longitude);
        location = new LatLng(userLat, userLng);

        functionSelected = getIntent().getStringExtra("SELECTED_FUNCTION");

        FragmentManager fragmentManager = getFragmentManager();

        if (functionSelected.equals("PLACE")) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, PlaceInfoF.newInstance(), "InfoFragment").commit();
        } else if (functionSelected.equals("PARACICLO")) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, ParacicloF.newInstance(), "ParacicloFragment").commit();
        } else if (functionSelected.equals("EDIT_PLACE")) {
            placeId = getIntent().getIntExtra(Constant.IEXTRA_PLACE_ID_INT, 0);
            name = getIntent().getStringExtra(Constant.IEXTRA_PLACE_NAME);
            phone = getIntent().getStringExtra(Constant.IEXTRA_PLACE_PHONE);
            email = getIntent().getStringExtra(Constant.IEXTRA_PLACE_PUBLIC_EMAIL);
            Double lat = getIntent().getDoubleExtra(Constant.IEXTRA_PLACE_LAT_DOUBLE, 0);
            Double lng = getIntent().getDoubleExtra(Constant.IEXTRA_PLACE_LNG_DOUBLE, 0);
            location = new LatLng(lat, lng);
            categoryIdList = getIntent().getIntegerArrayListExtra(Constant.IEXTRA_PLACE_CATEGORY_ID_LIST);
            fragmentManager.beginTransaction()
                    .add(R.id.container, PlaceInfoF.newInstance(), "InfoFragment").commit();
        }
    }

    @Override
    public void onEIFragmentInteraction(String strName, String strPhone, String strEmail, String strAddress, String strLocality, LatLng latLng) {
        name = strName;
        phone = strPhone;
        email = strEmail;
        address = strAddress;
        location = latLng;
        locality = strLocality;

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceCategoryF.newInstance(), "CategoryFragment").addToBackStack(null).commit();
    }

    @Override
    public void onECFragmentInteraction(String stringCategoriesIds, String otherServices) {

        Log.e("otherServices", otherServices);

        if (location != null) {

            if (functionSelected.equals("PLACE")) {

                Calls.addEstabelecimento(name, address, locality, String.valueOf(location.latitude), String.valueOf(location.longitude), phone, email, stringCategoriesIds, otherServices, new CallHandler() {
                    @Override
                    public void onSuccess(int responseCode, String response) {
                        Log.e("addPlaceHandler", "SUCCESS" + ": " + response);
                        Utils.showThanksToast(AddToMapActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(int responseCode, String response) {
                        Log.e("addPlaceHandler", "FAIL: " + responseCode + " " + response);
                        Utils.showServerErrorToast(AddToMapActivity.this, response);
                    }

                });
            } else if (functionSelected.equals("EDIT_PLACE")) {

                if (placeId != 0) {

                    Calls.updatePlaceInformation(String.valueOf(placeId), name, address, locality, String.valueOf(location.latitude), String.valueOf(location.longitude), phone, email, stringCategoriesIds, otherServices, new CallHandler() {
                        @Override
                        public void onSuccess(int responseCode, String response) {
                            Log.e("updatePlaceHandler", "SUCCESS" + ": " + response);
                            Utils.showThanksToast(AddToMapActivity.this);
                            finish();
                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            Log.e("updatePlaceHandler", "FAIL: " + responseCode + " " + response);
                            Utils.showServerErrorToast(AddToMapActivity.this, response);
                        }

                    });
                } else {
                    Utils.showErrorToast(this);
                }
            }
        } else {
            Utils.showErrorToast(this);
        }
    }

    @Override
    public void onParacicloFragmentInteraction(int quantity, String strAddress, LatLng latLng) {

        Calls.addParaciclo(strAddress, String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), quantity, new CallHandler(){

            @Override
            public void onSuccess (int responseCode, String response) {
                Log.e("addParaciclo", "SUCCESS" + ": " + response);
                Utils.showThanksToast(AddToMapActivity.this);
                finish();
            }

            @Override
            public void onFailure (int responseCode, String response) {
                Log.e("addParaciclo", "FAIL: " + responseCode + " " + response);
                Utils.showServerErrorToast(AddToMapActivity.this, response);
            }

        });

    }

    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getFragmentManager();

        PlaceCategoryF categoryFragment = (PlaceCategoryF) fragmentManager.findFragmentByTag("CategoryFragment");

        if (categoryFragment != null && categoryFragment.isVisible()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceInfoF.newInstance(), "InfoFragment").addToBackStack(null).commit();
        } else {
            finish();
        }
    }
}
