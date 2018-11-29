package jannini.android.ciclosp;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
	    reportType = org.acra.sender.HttpSender.Type.JSON,
	    formUri = "https://cauej.cloudant.com/acra-ciclosp/_design/acra-storage/_update/report",
	    formUriBasicAuthLogin = "owneripichervilysedellle",
	    formUriBasicAuthPassword = "37WIMFxcT58vGrQyXwD7c7xJ")
	   
	    
public class MyApplication extends Application {

	private static boolean activityVisible;

	@Override
	public void onCreate() {
	    // The following line triggers the initialization of ACRA
	    super.onCreate();
	    ACRA.init(this);
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