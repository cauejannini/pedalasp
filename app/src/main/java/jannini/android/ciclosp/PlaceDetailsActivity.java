package jannini.android.ciclosp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import jannini.android.ciclosp.CustomItemClasses.Place;
import jannini.android.ciclosp.NetworkRequests.BitmapCallHandler;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

import static jannini.android.ciclosp.Constant.REQUEST_CODE_ROUTE_FOR_DEAL;
import static jannini.android.ciclosp.Constant.mapCategoriesIcons;
import static jannini.android.ciclosp.MainActivity.ListPlaces;

public class PlaceDetailsActivity extends Activity {

    Place place;

    LinearLayout llAddress, llPhone, llDeals;
    ImageView ivPlaceLogo, ivPlaceImage, ivServices;
    TextView tvName, tvServices, tvAddress, tvPhone, tvShortDesc, tvHasDeals;

    RelativeLayout rlBackButton;

    int placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        llAddress = (LinearLayout) findViewById(R.id.ll_place_detail_address);
        llPhone = (LinearLayout) findViewById(R.id.ll_place_detail_phone);
        llDeals = (LinearLayout) findViewById(R.id.ll_place_detail_deals);
        ivPlaceLogo = (ImageView) findViewById(R.id.iv_place_detail_logo);
        ivPlaceImage = (ImageView) findViewById(R.id.iv_place_detail_image);
        tvName = (TextView) findViewById(R.id.tv_place_detail_name);
        ivServices = (ImageView) findViewById(R.id.iv_place_detail_services);
        tvServices = (TextView) findViewById(R.id.tv_place_detail_services);
        tvAddress = (TextView) findViewById(R.id.tv_place_detail_address);
        tvPhone = (TextView) findViewById(R.id.tv_place_detail_phone);
        tvShortDesc = (TextView) findViewById(R.id.tv_place_detail_short_desc);
        tvHasDeals = (TextView) findViewById(R.id.tv_place_detail_has_deals);

        rlBackButton = (RelativeLayout) findViewById(R.id.rl_back_button);
        rlBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });

        Intent i = getIntent();
        placeId = i.getIntExtra(Constant.IEXTRA_PLACE_ID_INT, 0);
        for (Place p : ListPlaces) {
            if (p.id == placeId) {
                place = p;
            }
        }

        if (place == null) {
            finish();
        }

        final Double userLat = i.getDoubleExtra("USER_LAT", 0);
        final Double userLng = i.getDoubleExtra("USER_LNG", 0);

        tvName.setText(place.name);
        tvAddress.setText(place.address);
        tvPhone.setText(place.phone);
        tvShortDesc.setText(place.short_desc);
        tvServices.setText(place.displayServices);

        if (Constant.mapPlacesImages.get(place.logoId) != null) {
            ivPlaceLogo.setImageBitmap(Constant.mapPlacesImages.get(place.logoId));
        } else {
            ivPlaceLogo.setImageBitmap(Constant.mapPlacesImages.get(3));
        }

        if (!mapCategoriesIcons.isEmpty() && !place.categoryIdList.isEmpty()) {
            ArrayList<Bitmap> bitmapArray = new ArrayList<>();
            for (int id: place.categoryIdList) {
                bitmapArray.add(mapCategoriesIcons.get(id));
            }
            ivServices.setImageBitmap(Utils.combineImages(this, bitmapArray));
            tvServices.setVisibility(View.GONE);
            ivServices.setVisibility(View.VISIBLE);

            ivServices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ivServices.setVisibility(View.GONE);
                    tvServices.setVisibility(View.VISIBLE);
                }
            });

            tvServices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ivServices.setVisibility(View.VISIBLE);
                    tvServices.setVisibility(View.GONE);
                }
            });
        } else {
            ivServices.setVisibility(View.GONE);
            tvServices.setVisibility(View.VISIBLE);
        }

        final ProgressBar pbImageLoading = (ProgressBar) findViewById(R.id.pb_place_detail_image_loading);
        pbImageLoading.setVisibility(View.VISIBLE);
        Calls.getImageForPlaceId(this, place.id, new BitmapCallHandler() {
            @Override
            public void onSuccess(Bitmap bitmap, int imageId) {

                pbImageLoading.setVisibility(View.GONE);

                ivPlaceImage.setImageBitmap(bitmap);
                ivPlaceImage.setVisibility(View.VISIBLE);
                ivPlaceImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }

            @Override
            public void onFailure(int imageId) {
                pbImageLoading.setVisibility(View.GONE);
                ImageView ivPlaceImageStandard = (ImageView) findViewById(R.id.iv_place_detail_image_standard);
                ivPlaceImageStandard.setVisibility(View.VISIBLE);
            }
        });

        Calls.getPlaceOpeningHours(String.valueOf(place.id), new CallHandler(){
            @Override
            public void onSuccess(int responseCode, String response) {
                super.onSuccess(responseCode, response);

                Log.e("RESPONSE", response);
                ProgressBar pbLoadingOpHours = (ProgressBar) findViewById(R.id.pb_loading_ophours);

                try {

                    JSONArray jarray = new JSONArray(response);

                    if (jarray.length() == 7) {

                        LinearLayout llOpHoursContainer = (LinearLayout) findViewById(R.id.ll_ophours_container);
                        TextView tvSun = (TextView) findViewById(R.id.tv_ophours_sun);
                        TextView tvMon = (TextView) findViewById(R.id.tv_ophours_mon);
                        TextView tvTue = (TextView) findViewById(R.id.tv_ophours_tue);
                        TextView tvWed = (TextView) findViewById(R.id.tv_ophours_wed);
                        TextView tvThu = (TextView) findViewById(R.id.tv_ophours_thu);
                        TextView tvFri = (TextView) findViewById(R.id.tv_ophours_fri);
                        TextView tvSat = (TextView) findViewById(R.id.tv_ophours_sat);

                        if (jarray.getString(0).equals("CLOSED")) {tvSun.setText(getString(R.string.sun)+": "+getString(R.string.closed));} else {tvSun.setText(getString(R.string.sun)+": "+jarray.getString(0));}
                        if (jarray.getString(1).equals("CLOSED")) {tvMon.setText(getString(R.string.mon)+": "+getString(R.string.closed));} else {tvMon.setText(getString(R.string.mon)+": "+jarray.getString(1));}
                        if (jarray.getString(2).equals("CLOSED")) {tvTue.setText(getString(R.string.tue)+": "+getString(R.string.closed));} else {tvTue.setText(getString(R.string.tue)+": "+jarray.getString(2));}
                        if (jarray.getString(3).equals("CLOSED")) {tvWed.setText(getString(R.string.wed)+": "+getString(R.string.closed));} else {tvWed.setText(getString(R.string.wed)+": "+jarray.getString(3));}
                        if (jarray.getString(4).equals("CLOSED")) {tvThu.setText(getString(R.string.thu)+": "+getString(R.string.closed));} else {tvThu.setText(getString(R.string.thu)+": "+jarray.getString(4));}
                        if (jarray.getString(5).equals("CLOSED")) {tvFri.setText(getString(R.string.fri)+": "+getString(R.string.closed));} else {tvFri.setText(getString(R.string.fri)+": "+jarray.getString(5));}
                        if (jarray.getString(6).equals("CLOSED")) {tvSat.setText(getString(R.string.sat)+": "+getString(R.string.closed));} else {tvSat.setText(getString(R.string.sat)+": "+jarray.getString(6));}

                        pbLoadingOpHours.setVisibility(View.GONE);
                        llOpHoursContainer.setVisibility(View.VISIBLE);
                    } else {
                        TextView tvOpHoursNotAvailable = (TextView) findViewById(R.id.tv_ophours_not_available);
                        tvOpHoursNotAvailable.setVisibility(View.VISIBLE);
                        pbLoadingOpHours.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    TextView tvOpHoursNotAvailable = (TextView) findViewById(R.id.tv_ophours_not_available);
                    tvOpHoursNotAvailable.setVisibility(View.VISIBLE);
                    pbLoadingOpHours.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int responseCode, String response) {
                super.onFailure(responseCode, response);

                ProgressBar pbLoadingOpHours = (ProgressBar) findViewById(R.id.pb_loading_ophours);
                TextView tvOpHoursNotAvailable = (TextView) findViewById(R.id.tv_ophours_not_available);
                tvOpHoursNotAvailable.setVisibility(View.VISIBLE);
                pbLoadingOpHours.setVisibility(View.GONE);
            }
        });

        if (place.hasDeals) {
            llDeals.setVisibility(View.VISIBLE);
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PlaceDetailsActivity.this, DealListActivity.class);
                    intent.putExtra(Constant.ICODE_DEAL_LIST, Constant.IEXTRA_ICODE_DEAL_LIST_FROM_PLACE);
                    intent.putExtra("DEAL_LIST_PLACE_ID", String.valueOf(place.id));
                    intent.putExtra("DEAL_WINDOW_TITLE", "Ofertas de " + place.name);
                    intent.putExtra("USER_LAT", userLat);
                    intent.putExtra("USER_LNG", userLng);
                    startActivityForResult(intent, REQUEST_CODE_ROUTE_FOR_DEAL);
                }
            });
        } else {
            llDeals.setVisibility(View.GONE);
        }

        llAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (place.latLng != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PlaceDetailsActivity.this);
                    alert.setMessage(getString(R.string.confirm_route_to_place_creation))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent();
                                    i.putExtra(Constant.IEXTRA_PLACE_LAT_DOUBLE, place.latLng.latitude);
                                    i.putExtra(Constant.IEXTRA_PLACE_LNG_DOUBLE, place.latLng.longitude);
                                    i.putExtra(Constant.IEXTRA_PLACE_ADDRESS, place.address);
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
                    Utils.showSimpleAlertDialog(PlaceDetailsActivity.this, "Erro", getString(R.string.toast_error));
                }
            }
        });

        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(PlaceDetailsActivity.this);
                alert.setMessage(getString(R.string.confirm_dial))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (ActivityCompat.checkSelfPermission(PlaceDetailsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(PlaceDetailsActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 4);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText().toString().trim()));
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 4:
                if (ActivityCompat.checkSelfPermission(PlaceDetailsActivity.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText().toString().trim()));
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}
