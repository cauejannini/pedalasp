package jannini.android.ciclosp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import jannini.android.ciclosp.MyApplication;
import jannini.android.ciclosp.Adapters.MyListAdapter;

public class ConnectionChangeReceiver extends BroadcastReceiver{
	
	MyListAdapter adapter;
	
	@Override
	public void onReceive( final Context context, Intent intent ) {
		
		if (MyApplication.isActivityVisible()) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			//NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE );
			
			/*if (activeNetInfo != null) {
				if (activeNetInfo.isConnected())
				{
					adapter = new MyListAdapter(context.getApplicationContext(), MainActivity.mMenuTitles, MainActivity.mMenuDescriptions, MainActivity.mMenuQuantidades);
					MainActivity.mDrawerList.setAdapter(adapter);
				} else {
					adapter = new MyListAdapter(context.getApplicationContext(), MainActivity.mMenuTitles, MainActivity.mMenuDescriptionsOffline, MainActivity.mMenuQuantidades);
					MainActivity.mDrawerList.setAdapter(adapter);
				}
			} else {
				adapter = new MyListAdapter(context.getApplicationContext(), MainActivity.mMenuTitles, MainActivity.mMenuDescriptionsOffline, MainActivity.mMenuQuantidades);
				MainActivity.mDrawerList.setAdapter(adapter);
			}*/
		}
	}
}