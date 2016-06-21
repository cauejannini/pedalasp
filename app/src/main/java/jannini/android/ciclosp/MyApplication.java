package jannini.android.ciclosp;

import java.util.HashMap;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

@ReportsCrashes(
		formKey="",
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
	
	public enum TrackerName {
	    APP_TRACKER, // Tracker used only in this app.
	  }

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	  
	synchronized Tracker getTracker(TrackerName trackerName) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		    Tracker t = analytics.newTracker("UA-54096754-1");
		    mTrackers.put(trackerName, t);
		
		return mTrackers.get(trackerName);
	}
	
	
	
}