package jannini.android.ciclosp;

import android.app.Application;
	   
	    
public class MyApplication extends Application {

	private static boolean activityVisible;

	@Override
	public void onCreate() {
	    // The following line triggers the initialization of ACRA
	    super.onCreate();
	}

	public static boolean isActivityVisible() {
		return activityVisible;
	}  

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
	}
	
}