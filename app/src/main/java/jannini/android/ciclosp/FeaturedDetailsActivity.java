package jannini.android.ciclosp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

import static jannini.android.ciclosp.Constant.PERMISSION_REQUEST_CODE_CALL_PHONE;
import static jannini.android.ciclosp.Constant.mapCategoriesIcons;
import static jannini.android.ciclosp.R.id.tv_featured_title;

public class FeaturedDetailsActivity extends Activity {

    TextView tvTitle, tvAddress,tvDescription, tvPlaceServices, tvPlaceName, tvPlacePhone, tvPlaceCurrentOpenStatus;
    ImageView ivPlaceServices;
    LinearLayout llContainer;
    ProgressBar pbLoading;

    String deviceId;

    LatLng featuredLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_details);

        llContainer = (LinearLayout) findViewById(R.id.ll_featured_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_featured_details_loading);
        RelativeLayout rlBackButton = (RelativeLayout) findViewById(R.id.rl_back_button);
        rlBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(tv_featured_title);
        tvAddress = (TextView) findViewById(R.id.tv_featured_address);
        tvDescription = (TextView) findViewById(R.id.tv_featured_description);

        ivPlaceServices = (ImageView) findViewById(R.id.iv_featured_place_services);
        tvPlaceServices = (TextView) findViewById(R.id.tv_featured_place_services);
        tvPlaceName = (TextView) findViewById(R.id.tv_featured_place_name);
        tvPlacePhone = (TextView) findViewById(R.id.tv_featured_place_phone);
        tvPlaceCurrentOpenStatus = (TextView) findViewById(R.id.tv_featured_place_current_open_status);

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        deviceId = sharedPreferences.getString(Constant.SPKEY_DEVICE_ID, "");

        Intent intent = getIntent();
        final String featId = String.valueOf(intent.getIntExtra("FEATURED_ID", 0));

        if (!featId.equals("0")) {

            Calls.getFeaturedForId(featId, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {

                    Log.e("success", response);
                    try {
                        JSONObject job = new JSONObject(response);

                        String title = job.getString("title");
                        String placeName = job.getString("place_name");
                        String placeDisplayServices = job.getString("place_display_services");
                        String placePhone = job.getString("place_phone");
                        String placeCurrentOpenStatus = job.getString("place_current_open_status");
                        String address = job.getString("address");
                        String description = job.getString("description");
                        Double lat = job.getDouble("lat");
                        Double lng = job.getDouble("lng");
                        featuredLatLng = new LatLng(lat, lng);

                        String placeCategories = job.getString("place_categories");
                        String[] categoriesStringArray = placeCategories.split(",");
                        ArrayList<Integer> categoriesIntArray = new ArrayList<>();
                        for (String categoryId : categoriesStringArray){
                            categoriesIntArray.add(Integer.valueOf(categoryId));
                        }

                        tvTitle.setText(title);
                        tvAddress.setText(address);
                        tvDescription.setText(description);
                        tvPlaceName.setText(placeName);
                        tvPlacePhone.setText(placePhone);
                        if (!placeDisplayServices.equals("")) {
                            tvPlaceServices.setText(placeDisplayServices);
                        } else {
                            tvPlaceServices.setVisibility(View.GONE);
                        }

                        if (!mapCategoriesIcons.isEmpty() && !categoriesIntArray.isEmpty()) {
                            ArrayList<Bitmap> bitmapArray = new ArrayList<>();
                            for (int id: categoriesIntArray) {
                                bitmapArray.add(mapCategoriesIcons.get(id));
                            }
                            ivPlaceServices.setImageBitmap(Utils.combineImages(FeaturedDetailsActivity.this, bitmapArray));
                            tvPlaceServices.setVisibility(View.GONE);
                            ivPlaceServices.setVisibility(View.VISIBLE);

                            ivPlaceServices.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ivPlaceServices.setVisibility(View.GONE);
                                    tvPlaceServices.setVisibility(View.VISIBLE);
                                }
                            });

                            tvPlaceServices.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ivPlaceServices.setVisibility(View.VISIBLE);
                                    tvPlaceServices.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            ivPlaceServices.setVisibility(View.GONE);
                            tvPlaceServices.setVisibility(View.VISIBLE);
                        }

                        if (!placeCurrentOpenStatus.equals("")) {
                            tvPlaceCurrentOpenStatus.setText(placeCurrentOpenStatus);
                            if (placeCurrentOpenStatus.contains("FECHADO")) {
                                tvPlaceCurrentOpenStatus.setTextColor(getResources().getColor(R.color.app_red));
                            }
                        } else {
                            tvPlaceCurrentOpenStatus.setVisibility(View.GONE);
                        }

                        pbLoading.setVisibility(View.GONE);
                        llContainer.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();

                        Utils.showErrorToast(FeaturedDetailsActivity.this);
                        finish();
                    }
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    Log.e("failure", response);
                    Utils.showErrorToast(FeaturedDetailsActivity.this);
                    finish();
                }
            });
        }

        LinearLayout llFeaturedPlacePhone = (LinearLayout) findViewById(R.id.ll_featured_place_phone);
        llFeaturedPlacePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(FeaturedDetailsActivity.this);
                alert.setMessage(getString(R.string.confirm_dial))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (ActivityCompat.checkSelfPermission(FeaturedDetailsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(FeaturedDetailsActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE_CALL_PHONE);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPlacePhone.getText().toString().trim()));
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                alert.show();
            }
        });

        LinearLayout llFeaturedAddress = (LinearLayout) findViewById(R.id.ll_featured_address);
        llFeaturedAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (featuredLatLng != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(FeaturedDetailsActivity.this);
                    alert.setMessage(getString(R.string.confirm_route_to_featured_creation))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent();
                                    i.putExtra(Constant.IEXTRA_FEATURED_LAT_DOUBLE, featuredLatLng.latitude);
                                    i.putExtra(Constant.IEXTRA_FEATURED_LNG_DOUBLE, featuredLatLng.longitude);
                                    i.putExtra(Constant.IEXTRA_FEATURED_ADDRESS, tvAddress.getText());
                                    setResult(RESULT_OK, i);
                                    finish();
                                }
                            })
                            .setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    alert.show();
                } else {
                    Utils.showErrorToast(FeaturedDetailsActivity.this);
                }

            }
        });

        // Check if user already has voucher for this featured and if so, replace llGenerateVoucher for llShowVoucher. Maybe use localized progressbar to indicate this server activity.

        LinearLayout llGetVoucher = (LinearLayout) findViewById(R.id.ll_get_voucher);
        llGetVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deviceId.equals("")) {

                    Calls.getVoucherForDeviceId(deviceId, featId, generateVoucherHandler);

                } else {

                    Calls.createDevice(new CallHandler(){
                        @Override
                        public void onSuccess(int responseCode, String response) {
                            super.onSuccess(responseCode, response);
                            sharedPreferences.edit().putString(Constant.SPKEY_DEVICE_ID, response).apply();
                            Calls.getVoucherForDeviceId(response, featId, generateVoucherHandler);
                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            super.onFailure(responseCode, response);
                            Utils.showServerErrorToast(FeaturedDetailsActivity.this, response);
                        }
                    });
                }
            }

        });
    }

    CallHandler generateVoucherHandler = new CallHandler() {
        @Override
        public void onSuccess(int responseCode, String response) {
            super.onSuccess(responseCode, response);

            Log.e("VOUCHER RESPONSE", response);

            Intent intent = new Intent(FeaturedDetailsActivity.this, ShowVoucherActivity.class);
            intent.putExtra(Constant.IEXTRA_VOUCHER_JSON, response);
            startActivity(intent);

        }

        @Override
        public void onFailure(int responseCode, String response) {
            super.onFailure(responseCode, response);

            Utils.showServerErrorToast(FeaturedDetailsActivity.this, response);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CALL_PHONE:

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    if (!tvPlacePhone.getText().toString().trim().equals("")) {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPlacePhone.getText().toString().trim())));
                    }
                }

                break;
        }
    }
}
