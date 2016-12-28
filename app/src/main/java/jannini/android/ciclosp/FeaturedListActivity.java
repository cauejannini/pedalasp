package jannini.android.ciclosp;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import jannini.android.ciclosp.Adapters.FeaturedListAdapter;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

public class FeaturedListActivity extends Activity implements ListView.OnItemClickListener {

    ProgressBar pbLoadingFeaturedInfo;

    Location userLocation;

    ListView listFeatured;

    ArrayList<Integer> listFeaturedIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_list);

        TextView tvWindowTitle = (TextView) findViewById(R.id.tv_featured_list_window_title);
        RelativeLayout rlBackButton = (RelativeLayout) findViewById(R.id.rl_back_button);
        rlBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pbLoadingFeaturedInfo = (ProgressBar) findViewById(R.id.pb_featured_list_loading);
        listFeatured = (ListView) findViewById(R.id.list_featured);
        listFeatured.setOnItemClickListener(this);

        Intent intent = getIntent();
        String windownTitle = intent.getStringExtra("FEATURED_WINDOW_TITLE");
        tvWindowTitle.setText(windownTitle);
        Double lat = intent.getDoubleExtra("USER_LAT",0);
        Double lng = intent.getDoubleExtra("USER_LNG",0);
        if (lat != 0 && lng != 0) {
            userLocation = new Location("userLocation");
            userLocation.setLatitude(lat);
            userLocation.setLongitude(lng);
        }

        String intentCode = intent.getStringExtra(Constant.ICODE_FEATURED_LIST);
        switch (intentCode) {

            case Constant.IEXTRA_ICODE_FEATURED_LIST_FROM_PLACE:

                String placeId = intent.getStringExtra("FEATURED_LIST_PLACE_ID");

                if (placeId != null && !placeId.equals("")) {
                    Calls.getFeaturedListForPlaceId(String.valueOf(placeId), new CallHandler() {
                        @Override
                        public void onSuccess(int responseCode, String response) {
                            super.onSuccess(responseCode, response);

                            parseFeaturedJson(response);
                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            super.onFailure(responseCode, response);
                            Utils.showServerErrorToast(FeaturedListActivity.this, response);
                        }
                    });
                } else {
                    Utils.showErrorToast(this);
                    finish();
                }
                break;
            case Constant.IEXTRA_ICODE_FEATURED_LIST_FROM_USER_LOCATION:

                String stringJarray = intent.getStringExtra("FEATURED_JARRAY");
                parseFeaturedJson(stringJarray);

                break;

            case Constant.IEXTRA_ICODE_FEATURED_LIST_ALL:

                Calls.getAllFeatured(new CallHandler() {
                    @Override
                    public void onSuccess(int responseCode, String response) {
                        super.onSuccess(responseCode, response);

                        parseFeaturedJson(response);
                    }

                    @Override
                    public void onFailure(int responseCode, String response) {
                        super.onFailure(responseCode, response);
                        Utils.showServerErrorToast(FeaturedListActivity.this, response);
                    }
                });

                break;
        }
    }

    void parseFeaturedJson(String stringJarray) {

        ArrayList<String> listFeaturedTitles = new ArrayList<>();
        ArrayList<String> listFeaturedAddresses = new ArrayList<>();
        ArrayList<String> listFeaturedPlaces = new ArrayList<>();
        ArrayList<String> listFeaturedDistances = new ArrayList<>();

        try {
            JSONArray jarray = new JSONArray(stringJarray);

            final HashMap<Integer, Integer> mapIdToDistance = new HashMap<>();
            List<Integer> nullDistanceIds = new ArrayList<>();
            List<Integer> sortedIdList = new ArrayList<>();

            HashMap<Integer, String> mapIdTitles = new HashMap<>();
            HashMap<Integer, String> mapIdPlaces = new HashMap<>();
            HashMap<Integer, String> mapIdAddresses = new HashMap<>();

            for (int i = 0; i<jarray.length(); i++) {
                JSONObject job = jarray.getJSONObject(i);

                mapIdTitles.put(job.getInt("id"),job.getString("title"));
                mapIdPlaces.put(job.getInt("id"),job.getString("place_name"));
                mapIdAddresses.put(job.getInt("id"),job.getString("address"));

                if (userLocation != null && job.getDouble("lat") != 0 && job.getDouble("lng") != 0) {
                    Location featuredLocation = new Location("featuredLocation");
                    featuredLocation.setLatitude(job.getDouble("lat"));
                    featuredLocation.setLongitude(job.getDouble("lng"));

                    int distance = (int) featuredLocation.distanceTo(userLocation);
                    sortedIdList.add(job.getInt("id"));
                    mapIdToDistance.put(job.getInt("id"), distance);
                } else {
                    nullDistanceIds.add(job.getInt("id"));
                    listFeaturedDistances.add("");
                }
            }

            Collections.sort(sortedIdList, new Comparator<Integer>() {
                public int compare(Integer one, Integer other) {
                    return mapIdToDistance.get(one).compareTo(mapIdToDistance.get(other));
                }
            });

            for (int y = 0; y < sortedIdList.size(); y++) {

                int currentId = sortedIdList.get(y);
                listFeaturedIds.add(currentId);
                String distanceOutput = String.valueOf(mapIdToDistance.get(currentId)) + " metros";
                if (mapIdToDistance.get(currentId) >= 1000) {
                    distanceOutput = String.valueOf(mapIdToDistance.get(currentId)/ 1000)+ " km";
                }
                listFeaturedDistances.add("Distância: " + distanceOutput);
                listFeaturedTitles.add(mapIdTitles.get(currentId));
                listFeaturedPlaces.add(mapIdPlaces.get(currentId));
                listFeaturedAddresses.add(mapIdAddresses.get(currentId));
            }

            for (int id: nullDistanceIds) {
                listFeaturedIds.add(id);
                listFeaturedDistances.add("");
                listFeaturedTitles.add(mapIdTitles.get(id));
                listFeaturedPlaces.add(mapIdPlaces.get(id));
                listFeaturedAddresses.add(mapIdAddresses.get(id));
            }

            String[] featuredTitles = new String[listFeaturedTitles.size()];
            featuredTitles = listFeaturedTitles.toArray(featuredTitles);
            String[] featuredAddresses = new String[listFeaturedAddresses.size()];
            featuredAddresses = listFeaturedAddresses.toArray(featuredAddresses);
            String[] featuredPlaces = new String[listFeaturedPlaces.size()];
            featuredPlaces = listFeaturedPlaces.toArray(featuredPlaces);
            String[] featuredDistances = new String[listFeaturedDistances.size()];
            featuredDistances = listFeaturedDistances.toArray(featuredDistances);

            listFeatured.setAdapter(new FeaturedListAdapter(this, featuredTitles, featuredPlaces, featuredAddresses, featuredDistances));
            pbLoadingFeaturedInfo.setVisibility(View.GONE);
            listFeatured.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showErrorToast(this);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        // Open featured details activity and load data from feature ID
        Intent intent = new Intent(FeaturedListActivity.this, FeaturedDetailsActivity.class);
        intent.putExtra("FEATURED_ID", listFeaturedIds.get(position));
        startActivityForResult(intent, Constant.REQUEST_CODE_ROUTE_FOR_FEATURED);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_ROUTE_FOR_FEATURED) {

                Double lat = i.getDoubleExtra(Constant.IEXTRA_FEATURED_LAT_DOUBLE, 0);
                Double lng = i.getDoubleExtra(Constant.IEXTRA_FEATURED_LNG_DOUBLE, 0);
                String address = i.getStringExtra(Constant.IEXTRA_FEATURED_ADDRESS);

                Intent intent = new Intent();
                intent.putExtra(Constant.IEXTRA_FEATURED_LAT_DOUBLE, lat);
                intent.putExtra(Constant.IEXTRA_FEATURED_LNG_DOUBLE, lng);
                intent.putExtra(Constant.IEXTRA_FEATURED_ADDRESS, address);
                setResult(RESULT_OK, intent);
                finish();

            }
        }
    }
}
