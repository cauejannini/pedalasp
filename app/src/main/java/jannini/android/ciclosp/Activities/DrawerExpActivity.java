package jannini.android.ciclosp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import jannini.android.ciclosp.Adapters.ListAdapterDrawerExp;
import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;


public class DrawerExpActivity extends Activity {

    //Navigation Drawer
    public ListView mDrawerList;
    ListAdapterDrawerExp myAdapter;

    SharedPreferences sharedPreferences;

    public static boolean[] states = {false, false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_exp_splash);

        states[0] = false;
        states[1] = false;
        states[2] = false;
        states[3] = false;
        states[4] = false;
        states[5] = false;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        String[] mMenuTitles = getResources().getStringArray(R.array.menu_array);
        String[] mMenuTitlesForDrawerExp = {mMenuTitles[0], mMenuTitles[1], mMenuTitles[2], mMenuTitles[3], mMenuTitles[4], mMenuTitles[5], mMenuTitles[6]};
        String[] mMenuDescriptions = getResources().getStringArray(R.array.menu_array_descriptions);
        String[] mMenuDescriptionsForDrawerExp = {mMenuDescriptions[0], mMenuDescriptions[1], mMenuDescriptions[2], mMenuDescriptions[3], mMenuDescriptions[4], mMenuDescriptions[5], mMenuDescriptions[6]};
        // Set up the drawer's list view with items and click listener.
        myAdapter = new ListAdapterDrawerExp(this, mMenuTitlesForDrawerExp, mMenuDescriptionsForDrawerExp);
        mDrawerList = (ListView) findViewById(R.id.list_drawer_exp);
        mDrawerList.setAdapter(myAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /*
        if (!splashResultCodes[0] && !splashResultCodes[1] && !splashResultCodes[2] && !splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_nenhuma_camada, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[0] && !splashResultCodes[1] && !splashResultCodes[2]) {
            Toast.makeText(this, R.string.splash_just_cs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[1] && !splashResultCodes[2] && !splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_error_places_bscs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[0] && !splashResultCodes[2] && !splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_just_places, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[0] && !splashResultCodes[1] && !splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_just_bs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[0] && !splashResultCodes[1]) {
            Toast.makeText(this, R.string.splash_somente_estacao, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[0] && !splashResultCodes[2]) {
            Toast.makeText(this, R.string.splash_just_places_cs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[0] && !splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_just_places_bs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[1] && !splashResultCodes[2]) {
            Toast.makeText(this, R.string.splash_error_places_bs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[1] && !splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_error_places_cs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[2] && !splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_error_bscs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[0]) {
            Toast.makeText(this, R.string.splash_just_places_bscs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[1]) {
            Toast.makeText(this, R.string.splash_error_places, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[2]) {
            Toast.makeText(this, R.string.splash_error_bs, Toast.LENGTH_LONG).show();
        } else if (!splashResultCodes[3]) {
            Toast.makeText(this, R.string.splash_error_cs, Toast.LENGTH_LONG).show();
        }*/

    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        int n = mDrawerList.getFirstVisiblePosition();

        if (!states[position]) {
            mDrawerList.getChildAt(position-n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
            states[position] = true;
        } else {
            mDrawerList.getChildAt(position-n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
            states[position] = false;
        }

        checkNumberOfOptionsDisplayed();

    }

    public void finishExp (View view) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("states0", states[0]);
        editor.putBoolean("states1", states[1]);
        editor.putBoolean("states2", states[2]);
        editor.putBoolean("states3", states[3]);
        editor.putBoolean("states4", states[4]);
        editor.putBoolean("states5", states[5]);
        editor.putBoolean("states6", states[6]);
        editor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void checkNumberOfOptionsDisplayed() {

        int numberOfTrues = 0;

        for (int i = 1; i < states.length; i++) {
            if (states[i]) numberOfTrues++;
        }

        if (numberOfTrues > 2) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            final AlertDialog alert = alertBuilder.create();
            View alertView = getLayoutInflater().inflate(R.layout.ad_toomuchmarkers, null);
            alert.setView(alertView);
            alert.setCancelable(false);
            Button btOk = (Button) alertView.findViewById(R.id.bt_toomuchmarkers_ok);
            final ToggleButton tbDonotWarnAgain = (ToggleButton) alertView.findViewById(R.id.tb_toomuchmarkers_donotdisplay);
            btOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sharedPreferences.edit().putBoolean(Constant.dontWarnAgainTooMuchMarkers, tbDonotWarnAgain.isChecked()).apply();

                    alert.dismiss();

                }
            });
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayShowTitleEnabled(false);
        return true;
    }

}
