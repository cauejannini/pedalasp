package jannini.android.ciclosp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import jannini.android.ciclosp.Adapters.ListAdapterDrawerExp;


public class DrawerExpActivity extends Activity {

    //Navigation Drawer
    public static String[] mMenuTitles;
    public static String[] mMenuDescriptions;
    public static ListView mDrawerList;
    ListAdapterDrawerExp myAdapter;

    SharedPreferences sharedPreferences;

    public static boolean[] states = {false, false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_exp_splash);

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);

        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mMenuDescriptions = getResources().getStringArray(R.array.menu_array_descriptions);
        // Set up the drawer's list view with items and click listener.
        myAdapter = new ListAdapterDrawerExp(this, mMenuTitles, mMenuDescriptions);
        mDrawerList = (ListView) findViewById(R.id.list_drawer_exp);
        mDrawerList.setAdapter(myAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        boolean[] check_result_code = SplashScreen.result_code;

        Log.d("RESULT_CODE", String.valueOf(check_result_code[0]) + ", " + String.valueOf(check_result_code[1]) + ", " + String.valueOf(check_result_code[2]));

        if (!check_result_code[0] && !check_result_code[1] && !check_result_code[2]) {
            Toast.makeText(this, R.string.splash_nenhuma_camada, Toast.LENGTH_LONG).show();
        } else if (!check_result_code[0] && !check_result_code[1]) {
            Toast.makeText(this, R.string.splash_somente_ciclosampa, Toast.LENGTH_LONG).show();
        } else if (!check_result_code[1] && !check_result_code[2]) {
            Toast.makeText(this, R.string.splash_nenhuma_estacao, Toast.LENGTH_LONG).show();
        } else if (!check_result_code[0] && !check_result_code[2]) {
            Toast.makeText(this, R.string.splash_somente_bikesampa, Toast.LENGTH_LONG).show();
        } else if (!check_result_code[0]) {
            Toast.makeText(this, R.string.splash_somente_estacao, Toast.LENGTH_LONG).show();
        } else if (!check_result_code[1]) {
            Toast.makeText(this, R.string.splash_sem_bikesampa, Toast.LENGTH_LONG).show();
        } else if (!check_result_code[2]) {
            Toast.makeText(this, R.string.splash_sem_ciclosampa, Toast.LENGTH_LONG).show();
        }

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
