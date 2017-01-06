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

import jannini.android.ciclosp.Adapters.DealListAdapter;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

public class DealListActivity extends Activity implements ListView.OnItemClickListener {

    ProgressBar pbLoadingDealInfo;

    Location userLocation;

    ListView listDeals;

    ArrayList<Integer> listDealsIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_list);

        TextView tvWindowTitle = (TextView) findViewById(R.id.tv_deal_list_window_title);
        RelativeLayout rlBackButton = (RelativeLayout) findViewById(R.id.rl_back_button);
        rlBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pbLoadingDealInfo = (ProgressBar) findViewById(R.id.pb_deal_list_loading);
        listDeals = (ListView) findViewById(R.id.list_deal);
        listDeals.setOnItemClickListener(this);

        Intent intent = getIntent();
        String windownTitle = intent.getStringExtra("DEAL_WINDOW_TITLE");
        tvWindowTitle.setText(windownTitle);
        Double lat = intent.getDoubleExtra("USER_LAT",0);
        Double lng = intent.getDoubleExtra("USER_LNG",0);
        if (lat != 0 && lng != 0) {
            userLocation = new Location("userLocation");
            userLocation.setLatitude(lat);
            userLocation.setLongitude(lng);
        }

        String intentCode = intent.getStringExtra(Constant.ICODE_DEAL_LIST);
        switch (intentCode) {

            case Constant.IEXTRA_ICODE_DEAL_LIST_FROM_PLACE:

                String placeId = intent.getStringExtra("DEAL_LIST_PLACE_ID");

                if (placeId != null && !placeId.equals("")) {
                    Calls.getDealListForPlaceId(String.valueOf(placeId), new CallHandler() {
                        @Override
                        public void onSuccess(int responseCode, String response) {
                            super.onSuccess(responseCode, response);

                            parseDealsJson(response);
                        }

                        @Override
                        public void onFailure(int responseCode, String response) {
                            super.onFailure(responseCode, response);
                            Utils.showServerErrorToast(DealListActivity.this, response);
                        }
                    });
                } else {
                    Utils.showErrorToast(this);
                    finish();
                }
                break;
            case Constant.IEXTRA_ICODE_DEAL_LIST_FROM_USER_LOCATION:

                String stringJarray = intent.getStringExtra("DEAL_JARRAY");
                parseDealsJson(stringJarray);

                break;

            case Constant.IEXTRA_ICODE_DEAL_LIST_ALL:

                Calls.getAllDeals(new CallHandler() {
                    @Override
                    public void onSuccess(int responseCode, String response) {
                        super.onSuccess(responseCode, response);

                        parseDealsJson(response);
                    }

                    @Override
                    public void onFailure(int responseCode, String response) {
                        super.onFailure(responseCode, response);
                        Utils.showServerErrorToast(DealListActivity.this, response);
                    }
                });

                break;
        }
    }

    void parseDealsJson(String stringJarray) {

        ArrayList<String> listDealsTitles = new ArrayList<>();
        ArrayList<String> listDealsAddresses = new ArrayList<>();
        ArrayList<String> listDealsPlaces = new ArrayList<>();
        ArrayList<String> listDealsDistances = new ArrayList<>();

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
                    Location dealLocation = new Location("dealLocation");
                    dealLocation.setLatitude(job.getDouble("lat"));
                    dealLocation.setLongitude(job.getDouble("lng"));

                    int distance = (int) dealLocation.distanceTo(userLocation);
                    sortedIdList.add(job.getInt("id"));
                    mapIdToDistance.put(job.getInt("id"), distance);
                } else {
                    nullDistanceIds.add(job.getInt("id"));
                    listDealsDistances.add("");
                }
            }

            Collections.sort(sortedIdList, new Comparator<Integer>() {
                public int compare(Integer one, Integer other) {
                    return mapIdToDistance.get(one).compareTo(mapIdToDistance.get(other));
                }
            });

            for (int y = 0; y < sortedIdList.size(); y++) {

                int currentId = sortedIdList.get(y);
                listDealsIds.add(currentId);
                String distanceOutput = String.valueOf(mapIdToDistance.get(currentId)) + " metros";
                if (mapIdToDistance.get(currentId) >= 1000) {
                    distanceOutput = String.valueOf(mapIdToDistance.get(currentId)/ 1000)+ " km";
                }
                listDealsDistances.add("Distância: " + distanceOutput);
                listDealsTitles.add(mapIdTitles.get(currentId));
                listDealsPlaces.add(mapIdPlaces.get(currentId));
                listDealsAddresses.add(mapIdAddresses.get(currentId));
            }

            for (int id: nullDistanceIds) {
                listDealsIds.add(id);
                listDealsDistances.add("");
                listDealsTitles.add(mapIdTitles.get(id));
                listDealsPlaces.add(mapIdPlaces.get(id));
                listDealsAddresses.add(mapIdAddresses.get(id));
            }

            String[] dealsTitles = new String[listDealsTitles.size()];
            dealsTitles = listDealsTitles.toArray(dealsTitles);
            String[] dealsAddresses = new String[listDealsAddresses.size()];
            dealsAddresses = listDealsAddresses.toArray(dealsAddresses);
            String[] dealsPlaces = new String[listDealsPlaces.size()];
            dealsPlaces = listDealsPlaces.toArray(dealsPlaces);
            String[] dealsDistances = new String[listDealsDistances.size()];
            dealsDistances = listDealsDistances.toArray(dealsDistances);

            listDeals.setAdapter(new DealListAdapter(this, dealsTitles, dealsPlaces, dealsAddresses, dealsDistances));
            pbLoadingDealInfo.setVisibility(View.GONE);
            listDeals.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showErrorToast(this);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        // Open deal details activity and load data from feature ID
        Intent intent = new Intent(DealListActivity.this, DealDetailsActivity.class);
        intent.putExtra("DEAL_ID", listDealsIds.get(position));
        startActivityForResult(intent, Constant.REQUEST_CODE_ROUTE_FOR_DEAL);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_ROUTE_FOR_DEAL) {

                Double lat = i.getDoubleExtra(Constant.IEXTRA_DEAL_LAT_DOUBLE, 0);
                Double lng = i.getDoubleExtra(Constant.IEXTRA_DEAL_LNG_DOUBLE, 0);
                String address = i.getStringExtra(Constant.IEXTRA_DEAL_ADDRESS);

                Intent intent = new Intent();
                intent.putExtra(Constant.IEXTRA_DEAL_LAT_DOUBLE, lat);
                intent.putExtra(Constant.IEXTRA_DEAL_LNG_DOUBLE, lng);
                intent.putExtra(Constant.IEXTRA_DEAL_ADDRESS, address);
                setResult(RESULT_OK, intent);
                finish();

            }
        }
    }
}
