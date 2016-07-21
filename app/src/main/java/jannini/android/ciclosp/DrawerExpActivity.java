package jannini.android.ciclosp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import jannini.android.ciclosp.Adapters.MyListAdapterSemQuantidades;


public class DrawerExpActivity extends Activity {

    //Navigation Drawer
    public static String[] mMenuTitles;
    public static String[] mMenuDescriptions;
    public static ListView mDrawerList;
    MyListAdapterSemQuantidades myAdapter;

    public static boolean[] states = {false, false, false, false, false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_exp_splash);

        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mMenuDescriptions = getResources().getStringArray(R.array.menu_array_descriptions);
        // Set up the drawer's list view with items and click listener.
        myAdapter = new MyListAdapterSemQuantidades(this, mMenuTitles, mMenuDescriptions);
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
            mDrawerList.getChildAt(position-n).setBackgroundResource(R.drawable.splash_list_item_bg_on);
            states[position] = true;
        } else {
            mDrawerList.getChildAt(position-n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
            states[position] = false;
        }

    }

    public void finishExp (View view) {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("states0", states[0]);
        editor.putBoolean("states1", states[1]);
        editor.putBoolean("states2", states[2]);
        editor.putBoolean("states3", states[3]);
        editor.putBoolean("states4", states[4]);
        editor.putBoolean("states5", states[5]);
        editor.putBoolean("states6", states[6]);
        editor.putBoolean("states7", states[7]);
        editor.putBoolean("states8", states[8]);
        editor.putBoolean("states9", states[9]);
        editor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayShowTitleEnabled(false);
        return true;
    }

}
