package jannini.android.ciclosp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

import static jannini.android.ciclosp.Constant.PERMISSION_REQUEST_CODE_CALL_PHONE;
import static jannini.android.ciclosp.R.id.tv_deal_title;

public class DealDetailsActivity extends Activity {

    TextView tvTitle, tvAddress,tvDescription, tvPlaceName, tvPlacePhone, tvPlaceCurrentOpenStatus;
    int placeId;
    LinearLayout llContainer;
    ProgressBar pbLoading;

    String deviceId;

    LatLng dealLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_details);

        llContainer = (LinearLayout) findViewById(R.id.ll_deal_container);
        pbLoading = (ProgressBar) findViewById(R.id.pb_deal_details_loading);
        RelativeLayout rlBackButton = (RelativeLayout) findViewById(R.id.rl_back_button);
        rlBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(tv_deal_title);
        tvAddress = (TextView) findViewById(R.id.tv_deal_address);
        tvDescription = (TextView) findViewById(R.id.tv_deal_description);

        tvPlaceName = (TextView) findViewById(R.id.tv_deal_place_name);
        tvPlacePhone = (TextView) findViewById(R.id.tv_deal_place_phone);
        tvPlaceCurrentOpenStatus = (TextView) findViewById(R.id.tv_deal_place_current_open_status);

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        deviceId = sharedPreferences.getString(Constant.SPKEY_DEVICE_ID, "");

        Intent intent = getIntent();
        final String dealId = String.valueOf(intent.getIntExtra("DEAL_ID", 0));

        if (!dealId.equals("0")) {

            Calls.getDealForId(dealId, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {

                    Log.e("success", response);
                    try {
                        JSONObject job = new JSONObject(response);

                        String title = job.getString("title");
                        placeId = job.getInt("place_id");
                        String placeName = job.getString("place_name");
                        String placePhone = job.getString("place_phone");
                        String placeCurrentOpenStatus = job.getString("place_current_open_status");
                        String address = job.getString("address");
                        String description = job.getString("description");
                        Double lat = job.getDouble("lat");
                        Double lng = job.getDouble("lng");
                        dealLatLng = new LatLng(lat, lng);

                        tvTitle.setText(title);
                        tvAddress.setText(address);
                        tvDescription.setText(description);
                        tvPlaceName.setText(placeName);
                        tvPlacePhone.setText(placePhone);

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

                        Utils.showErrorToast(DealDetailsActivity.this);
                        finish();
                    }
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    Log.e("failure", response);
                    Utils.showErrorToast(DealDetailsActivity.this);
                    finish();
                }
            });
        }

        LinearLayout llDealPlaceName = (LinearLayout) findViewById(R.id.ll_deal_place_name);
        llDealPlaceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DealDetailsActivity.this, PlaceDetailsActivity.class);
                i.putExtra(Constant.IEXTRA_PLACE_ID_INT, placeId);
                startActivity(i);
                // START ACTIVITY FOR RESULT IF WANT TO BE ABLE TO MAKE ROUTE FROM PLACE DETAILS ACTIVITY
            }
        });

        LinearLayout llDealPlacePhone = (LinearLayout) findViewById(R.id.ll_deal_place_phone);
        llDealPlacePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DealDetailsActivity.this);
                alert.setMessage(getString(R.string.confirm_dial))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (ActivityCompat.checkSelfPermission(DealDetailsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(DealDetailsActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE_CALL_PHONE);
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

        LinearLayout llDealAddress = (LinearLayout) findViewById(R.id.ll_deal_address);
        llDealAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dealLatLng != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(DealDetailsActivity.this);
                    alert.setMessage(getString(R.string.confirm_route_to_deal_creation))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent();
                                    i.putExtra(Constant.IEXTRA_DEAL_LAT_DOUBLE, dealLatLng.latitude);
                                    i.putExtra(Constant.IEXTRA_DEAL_LNG_DOUBLE, dealLatLng.longitude);
                                    i.putExtra(Constant.IEXTRA_DEAL_ADDRESS, tvAddress.getText());
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
                    Utils.showErrorToast(DealDetailsActivity.this);
                }

            }
        });

        // Check if user already has voucher for this deal and if so, replace llGenerateVoucher for llShowVoucher. Maybe use localized progressbar to indicate this server activity.

        LinearLayout llGetVoucher = (LinearLayout) findViewById(R.id.ll_get_voucher);
        llGetVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deviceId.equals("")) {

                    Calls.getVoucherForDeviceId(deviceId, dealId, generateVoucherHandler);

                } else {

                    Calls.createDevice(new CallHandler(){
                        @Override
                        public void onSuccess(int responseCode, String response) {
                            super.onSuccess(responseCode, response);
                            sharedPreferences.edit().putString(Constant.SPKEY_DEVICE_ID, response).apply();
                            Calls.getVoucherForDeviceId(response, dealId, generateVoucherHandler);
                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            super.onFailure(responseCode, response);
                            Utils.showServerErrorToast(DealDetailsActivity.this, response);
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

            Intent intent = new Intent(DealDetailsActivity.this, ShowVoucherActivity.class);
            intent.putExtra(Constant.IEXTRA_VOUCHER_JSON, response);
            startActivity(intent);

        }

        @Override
        public void onFailure(int responseCode, String response) {
            super.onFailure(responseCode, response);

            Utils.showServerErrorToast(DealDetailsActivity.this, response);
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
