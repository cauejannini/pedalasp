package jannini.android.ciclosp;


import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.achartengine.GraphicalView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jannini.android.ciclosp.Adapters.InfoWindowActivity;
import jannini.android.ciclosp.Adapters.MyListAdapter;
import jannini.android.ciclosp.CustomItemClasses.Bicicletario;
import jannini.android.ciclosp.CustomItemClasses.Ciclovia;
import jannini.android.ciclosp.CustomItemClasses.CyclingPath;
import jannini.android.ciclosp.CustomItemClasses.Estacao;
import jannini.android.ciclosp.CustomItemClasses.Parque;
import jannini.android.ciclosp.CustomItemClasses.Report;
import jannini.android.ciclosp.Fragments.SwipeFragment;
import jannini.android.ciclosp.MyApplication.TrackerName;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Directions;
import jannini.android.ciclosp.NetworkRequests.JSONParser;
import jannini.android.ciclosp.NetworkRequests.NotifySolvedReport;


public class MainActivity extends FragmentActivity
        implements
        LocationListener,
        OnMapReadyCallback,
        SwipeFragment.SwipeFragmentInteractionListener {

	// Location Manager and Provider
	private LocationManager locationManager;
	private String bestAvailableProvider;

	// Google Map
	private GoogleMap googleMap;

	// General Geocoder
	public Geocoder geocoder;

	// Analytics tracker
	Tracker t;

    static boolean savedInstanceStateHasRun = false;
    boolean isViewPagerVisible = false;
    String[] stringGraphPointMarkerInfo;
    LatLng latLngGraphPointMarker;

	// Calendars
	public Calendar rightNow;
	public Calendar sundaySeven;
	public Calendar sundaySixteen;

	// Bike lanes Arrays
	public ArrayList<Polyline> cicloviasLineList = new ArrayList<>();

	public ArrayList<Polyline> ciclofaixasLineList = new ArrayList<>();

	public static ArrayList<PolylineOptions> ciclorrotasOptionsList = new ArrayList<>();
	public ArrayList<Polyline> ciclorrotasLineList = new ArrayList<>();

	public static String newline = System.getProperty("line.separator");

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	// APP STORED PREFERENCES

	// Boolean to check if this is the first time app is being opened
	Boolean betaRouteWarningWasShown;
	Boolean elevGraphExpWasShown;

	// Criando listas de itens de cada tabela da DB
	public static ArrayList<Estacao> ListEstacoesITAU = new ArrayList<>();
	public static ArrayList<Estacao> ListEstacoesBRA = new ArrayList<>();
	public static ArrayList<Parque> ListParques = new ArrayList<>();
	public static ArrayList<Bicicletario> ListBicicletarios = new ArrayList<>();
	public static ArrayList<Ciclovia> ListCiclovias = new ArrayList<>();
	public static ArrayList<Ciclovia> ListCiclofaixas = new ArrayList<>();
	public static ArrayList<MarkerOptions> ListWifi = new ArrayList<>();
	public static ArrayList<Report> ListAlerts = new ArrayList<>();
    public static ArrayList<MarkerOptions> ListAcesso = new ArrayList<>();

	//Criando listas dos marcadores de mapa para os itens de cada tabela da DB
	ArrayList<Marker> ListMarkersITAU = new ArrayList<>();
	ArrayList<Marker> ListMarkersBRA = new ArrayList<>();
	ArrayList<Marker> ListMarkersParques = new ArrayList<>();
	ArrayList<Marker> ListMarkersBicicletarios = new ArrayList<>();
	ArrayList<Marker> ListMarkersWifi = new ArrayList<>();
	ArrayList<Marker> ListMarkersAlerts = new ArrayList<>();
    ArrayList<Marker> ListMarkersAcessos = new ArrayList<>();

    ArrayList<String> ListMarkersAlertsIds = new ArrayList<>();
	List<Marker> listMarker = new ArrayList<>();

	//Navigation Drawer
	public DrawerLayout mDrawerLayout;
	public ListView mDrawerList;
	public ActionBarDrawerToggle mDrawerToggle;
	public MyListAdapter myAdapter;

	// Header for Search and Route
	LinearLayout header;

	// Search header views
	View searchHeaderView;

	ProgressBar pBarSearch;
	Button btLupa;
	Button btClearSearch;
	EditText etSearch;

	// ROUTE
	// RouteHeaderView
	View routeHeaderView;

	LinearLayout routeHeader;

	TextView tvOrigin;
	TextView tvDestination;
	EditText etDestination;
	EditText etOrigin;
	Button btRoute;
	Button btSwitchAddresses;
	ProgressBar pBarRoute;

	Boolean isRouteModeOn = false;

	List<Address> addressList = new ArrayList<>();
	List<Address> addressListBase = new ArrayList<>();
	Marker markerDestination, markerOrigin, markerSearch;

    LatLng latLngMarkerOrigin;
    LatLng latLngMarkerDestination;

	ImageView iv;
	Menu mymenu;
	MenuItem menu_item;

	double[] current_latlng;
	float zoom;

	public SharedPreferences sharedPreferences;

	static ArrayList<Polyline> polylineRoutesList = new ArrayList<>();

	Criteria criteria = new Criteria();

	LatLng user_latlng = null;

    //LinearLayout llPlaceOptions;
	Button notifyButton, btParkedHere, btRemovePlace;//, btParkedHereSmall, btPlaceFavorite;
	Marker activeMarker;
    ArrayList<Marker> parkedHereMarkerList = new ArrayList<>();
    //ArrayList<Marker> favoritePlacesMarkerList = new ArrayList<>();

	Map<String, String> alertMap = new HashMap<>();

	// ROUTE variables
	public static ArrayList<CyclingPath> cyclingPathList = new ArrayList<>();

	// Auxiliar list that only stores the selected cyclingPath
	ArrayList<CyclingPath> selectedCyclingPath = new ArrayList<>();

	// Auxiliar marker to show point selected on Graph
	Marker graph_point_marker = null;

	// AsyncTasks
	AsyncTask<String, String, String> geocodeOriginASY;
	AsyncTask<String, String, String> geocodeDestinationASY;
	AsyncTask<String, Integer, ArrayList<CyclingPath>> getRoutesASY;

	ViewPager viewPager;
	Integer viewPagerPosition;
	ObjectAnimator hideViewPager;

	ToggleButton btRouteMode;

	//aChartEngine
    static ArrayList<LinearLayout> aChartLinearLayoutList = new ArrayList<>();
    static ArrayList<GraphicalView> graphViewArray = new ArrayList<>();

	AlertDialog adLoadingRoute;
	ProgressBar pbLoadingRoute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_drawer);

		geocoder = new Geocoder(this);

		// CALENDAR VARIABLES
		// Get information of current time
		rightNow = Calendar.getInstance();

		// Create calendars to compare
		sundaySeven = Calendar.getInstance();
		sundaySeven.set(Calendar.HOUR_OF_DAY, 7);
		sundaySeven.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		sundaySixteen = Calendar.getInstance();
		sundaySixteen.set(Calendar.HOUR_OF_DAY, 16);
		sundaySixteen.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		// Get tracker
		t = ((MyApplication) this.getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName("MainActivity");
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);

		betaRouteWarningWasShown = sharedPreferences.getBoolean("betaRouteWarningWasShown", false);
		elevGraphExpWasShown = sharedPreferences.getBoolean("elevGraphExpWasShown", false);

		//Create criteria to decide what is the best location provider. Store this information in "provider
		criteria.setSpeedRequired(false);
		criteria.setAltitudeRequired(false);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// Get States from sharedPreferences
		Constant.states[0] = sharedPreferences.getBoolean("states0", true);
        Constant.states[1] = sharedPreferences.getBoolean("states1", true);
        Constant.states[2] = sharedPreferences.getBoolean("states2", true);
        Constant.states[3] = sharedPreferences.getBoolean("states3", true);
        Constant.states[4] = sharedPreferences.getBoolean("states4", true);
        Constant.states[5] = sharedPreferences.getBoolean("states5", true);

        Constant.bikeLanesStates[0] = sharedPreferences.getBoolean("bikeLanesStates0", true);
        Constant.bikeLanesStates[1] = sharedPreferences.getBoolean("bikeLanesStates1", true);
        Constant.bikeLanesStates[2] = sharedPreferences.getBoolean("bikeLanesStates2", true);

        Constant.sharingSystemsStates[0] = sharedPreferences.getBoolean("sharingSystemsStates0", true);
        Constant.sharingSystemsStates[1] = sharedPreferences.getBoolean("sharingSystemsStates1", true);

        header = (LinearLayout) findViewById(R.id.header);

		// SEARCH HEADER VARIABLES
		searchHeaderView = getLayoutInflater().inflate(R.layout.search_header, null);
		searchHeaderView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

		pBarSearch = (ProgressBar) searchHeaderView.findViewById(R.id.progress_bar_search);
		btLupa = (Button) searchHeaderView.findViewById(R.id.search_button);
		btClearSearch = (Button) searchHeaderView.findViewById(R.id.clear_search);
		etSearch = (EditText) searchHeaderView.findViewById(R.id.et_search);
		etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
					findAddress(etSearch);
				}
				return false;
			}
		});

		// Start Header with searchHeaderView
		header.addView(searchHeaderView);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// ROUTE HEADER VARIABLES
		routeHeaderView = getLayoutInflater().inflate(R.layout.route_header, null);

		routeHeader = (LinearLayout) routeHeaderView.findViewById(R.id.route_header);
		routeHeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

		tvDestination = (TextView) routeHeaderView.findViewById(R.id.tv_destination);
		tvOrigin = (TextView) routeHeaderView.findViewById(R.id.tv_origin);
		//btCancelRouteMode = (Button) routeHeaderView.findViewById(R.id.cancel_route_mode);
		btRoute = (Button) routeHeaderView.findViewById(R.id.bt_route);
		btSwitchAddresses = (Button) routeHeaderView.findViewById(R.id.switch_addresses);
		etOrigin = (EditText) routeHeaderView.findViewById(R.id.et_origin);
		etDestination = (EditText) routeHeaderView.findViewById(R.id.et_destination);
		pBarRoute = (ProgressBar) routeHeaderView.findViewById(R.id.progress_bar_route);

		// Listener to editTexts on RouteMode (etOrigin and etDestination)
		EditText.OnEditorActionListener etRouteClickListener = new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
					getDirections(v);
				}
				return false;
			}
		};

		etOrigin.setOnEditorActionListener(etRouteClickListener);
		etDestination.setOnEditorActionListener(etRouteClickListener);

		// Define RouteButton Animation
		btRouteMode = (ToggleButton) findViewById(R.id.route_bt);

		// Define NotifyButton Animation and Animation listener
		notifyButton = (Button) findViewById(R.id.notify_solved);
        btParkedHere = (Button) findViewById(R.id.bt_parked_here);
        btRemovePlace = (Button) findViewById(R.id.bt_remove_parked_here);
        //llPlaceOptions = (LinearLayout) findViewById(R.id.ll_place_options);
        //btParkedHereSmall = (Button) findViewById(R.id.bt_parked_here_small);
        //btPlaceFavorite = (Button) findViewById(R.id.bt_place_favorite);

		iv = (ImageView) findViewById(R.id.iv_action_refresh);

		//AlertView for calculating routes
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
		adLoadingRoute = alertBuilder.create();
		adLoadingRoute.setCancelable(false);
		View calculatingRouteAV = getLayoutInflater().inflate(R.layout.ad_loading_route, null);
		adLoadingRoute.setView(calculatingRouteAV);
		Button btCancelCalculatingRoute = (Button) calculatingRouteAV.findViewById(R.id.cancel_loading_routes);
		pbLoadingRoute = (ProgressBar) calculatingRouteAV.findViewById(R.id.pb_loading_route);

		// DRAWER VARIABLES
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set up the drawer's list view with items and click listener.
		myAdapter = new MyListAdapter(this,
				getResources().getStringArray(R.array.menu_array),
				getResources().getStringArray(R.array.menu_array_descriptions));
		mDrawerList.setAdapter(myAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
		// A linha de c�digo a seguir estava pedindo API m�n. 14. Como tirar ela n�o mudou nada, tirei. getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.string.drawer_open,  // "open drawer" description for accessibility
				R.string.drawer_close  // "close drawer" description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// GET VIEWPAGER
		viewPager = (ViewPager) findViewById(R.id.route_panel);

		hideViewPager = ObjectAnimator.ofFloat(viewPager, "translationY", 0, 600);
		hideViewPager.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				viewPager.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});

        // INITIALIZE MAP
        try {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

		// Bloco abaixo foi adicionado pra evitar o erro de null em CameraUpdateFactory e IBitMapDescriptorFactory
		try {
			MapsInitializer.initialize(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    /* DRAWER FUNCTIONALITY */

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		int n = mDrawerList.getFirstVisiblePosition();

		switch (position) {
            // Bike Lanes
			case 0:
                if (!Constant.states[0]) {

                    mDrawerList.getChildAt(0).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                    Constant.states[0] = true;
                    displayBikeLanes();

                } else {
                    mDrawerList.getChildAt(0).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    Constant.states[0] = false;

                    // Set Ciclovias not visible
                    for (int i = 0; i < cicloviasLineList.size(); i++) {
                        cicloviasLineList.get(i).setVisible(false);
                    }

                    for (Marker marker : ListMarkersAcessos) {
                        marker.setVisible(false);
                    }

                    // Set ciclofaixas not visible
                    for (int i = 0; i < ciclofaixasLineList.size(); i++) {
                        ciclofaixasLineList.get(i).setVisible(false);
                    }

                    // Set Ciclorrotas not visible
                    for (int i = 0; i < ciclorrotasLineList.size(); i++) {
                        ciclorrotasLineList.get(i).setVisible(false);
                    }

                }

                sharedPreferences.edit().putBoolean("states0", Constant.states[0]).apply();

				break;

            // Sharing Systems
			case 1:
                if (!Constant.states[1]) {

                    mDrawerList.getChildAt(1 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                    Constant.states[1] = true;
                    displaySharingSystems();

                } else {
                    mDrawerList.getChildAt(1 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    Constant.states[1] = false;

                    // Set BikeSampa not visible
                    for (int i = 0; i < ListMarkersITAU.size(); i++) {
                        ListMarkersITAU.get(i).setVisible(false);
                    }

                    // Set CicloSampa not visible
                    for (int i = 0; i < ListMarkersBRA.size(); i++) {
                        ListMarkersBRA.get(i).setVisible(false);
                    }
                }

                sharedPreferences.edit().putBoolean("states1", Constant.states[1]).apply();

				break;

            // Parking
            case 2:

                if (!Constant.states[2]) {

                    mDrawerList.getChildAt(2 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                    Constant.states[2] = true;

                    if (!ListMarkersBicicletarios.isEmpty()) {

                        for (int i = 0; i < ListMarkersBicicletarios.size(); i++) {
                            ListMarkersBicicletarios.get(i).setVisible(true);
                        }

                    } else {
                        drawBicicletarios(true);
                    }

                } else {

                    Constant.states[2] = false;
                    mDrawerList.getChildAt(2 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    for (int i = 0; i < ListMarkersBicicletarios.size(); i++) {
                        ListMarkersBicicletarios.get(i).setVisible(false);
                    }

                    hideBottomButton(btParkedHere);
                }

                sharedPreferences.edit().putBoolean("states2", Constant.states[2]).apply();

                break;

            // Parks
			case 3:

                if (!Constant.states[3]) {

                    Constant.states[3] = true;
                    mDrawerList.getChildAt(3 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);

                    if (!ListMarkersParques.isEmpty()) {

                        mDrawerList.getChildAt(3 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
                        for (int i = 0; i < ListMarkersParques.size(); i++) {
                            ListMarkersParques.get(i).setVisible(true);
                        }

                    } else {
                        drawParques(true);
                    }

                } else {

                    Constant.states[3] = false;
                    mDrawerList.getChildAt(3 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    for (int i = 0; i < ListMarkersParques.size(); i++) {
                        ListMarkersParques.get(i).setVisible(false);
                    }
                }

                sharedPreferences.edit().putBoolean("states3", Constant.states[3]).apply();

                break;

            // Wifi
			case 4:

                if (!Constant.states[4]) {

                    Constant.states[4] = true;
                    mDrawerList.getChildAt(4 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);

                    if (!ListMarkersWifi.isEmpty()) {

                        for (int i = 0; i < ListMarkersWifi.size(); i++) {
                            ListMarkersWifi.get(i).setVisible(true);
                        }

                    } else {
                        drawWifi(true);
                    }
                } else {

                    Constant.states[4] = false;
                    mDrawerList.getChildAt(4 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    for (int i = 0; i < ListMarkersWifi.size(); i++) {
                        ListMarkersWifi.get(i).setVisible(false);
                    }
                }

                sharedPreferences.edit().putBoolean("states4", Constant.states[4]).apply();

                break;

            // Alerts
			case 5:

                if (!Constant.states[5]) {

                    Constant.states[5] = true;
                    mDrawerList.getChildAt(5 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);

                    if (!ListMarkersAlerts.isEmpty()) {
                        for (int i = 0; i < ListMarkersAlerts.size(); i++) {
                            ListMarkersAlerts.get(i).setVisible(true);
                        }
                    } else {
                        drawAlerts(true);
                    }
                } else {
                    Constant.states[5] = false;
                    mDrawerList.getChildAt(5 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    for (int i = 0; i < ListMarkersAlerts.size(); i++) {
                        ListMarkersAlerts.get(i).setVisible(false);
                    }
                    hideBottomButton(notifyButton);
                }

                sharedPreferences.edit().putBoolean("states5", Constant.states[5]).apply();

                break;
			case 6:
				startActivity(new Intent(MainActivity.this, SugestaoActivity.class));
				break;
		}

        if (!sharedPreferences.getBoolean(Constant.dontWarnAgainTooMuchMarkers, false)) {
            checkNumberOfOptionsDisplayed();
        }
	}

	public void selectBikeLaneTypes() {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
		final AlertDialog alert = alertBuilder.create();
		View alertView = getLayoutInflater().inflate(R.layout.ad_selectbikelanes, null);
		alert.setView(alertView);
        alert.setCancelable(false);
		ToggleButton tbPermanentes = (ToggleButton) alertView.findViewById(R.id.tb_bikelanes_permanentes);
        tbPermanentes.setChecked(Constant.bikeLanesStates[0]);
		tbPermanentes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.bikeLanesStates[0] = isChecked;
			}
		});
		ToggleButton tbLazer = (ToggleButton) alertView.findViewById(R.id.tb_bikelanes_lazer);
        tbLazer.setChecked(Constant.bikeLanesStates[1]);
		tbLazer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.bikeLanesStates[1] = isChecked;
			}
		});
		ToggleButton tbPreferenciais = (ToggleButton) alertView.findViewById(R.id.tb_bikelanes_preferenciais);
        tbPreferenciais.setChecked(Constant.bikeLanesStates[2]);
		tbPreferenciais.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.bikeLanesStates[2] = isChecked;
			}
		});
		Button btOk = (Button) alertView.findViewById(R.id.bt_bikelanes_ok);
		btOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("bikeLanesStates0", Constant.bikeLanesStates[0]);
                editor.putBoolean("bikeLanesStates1", Constant.bikeLanesStates[1]);
                editor.putBoolean("bikeLanesStates2", Constant.bikeLanesStates[2]);
                editor.apply();

				if (Constant.states[0]) {
					displayBikeLanes();
				}
			}
		});

		alert.show();
	}

	public void displayBikeLanes() {

        if (Constant.bikeLanesStates[0]) {

            // Se não estiverem desenhadas, desenhar
            if (cicloviasLineList.isEmpty()) {

                    drawPermanentes(true);
            // Se já estiverem desenhadas, apenas tornar visível
            } else {

                // Set Polylines and Markers visible
                for (int i = 0; i < cicloviasLineList.size(); i++) {
                    cicloviasLineList.get(i).setVisible(true);
                }
                for (Marker marker : ListMarkersAcessos) {
                    marker.setVisible(true);
                }
            }

        } else {
            // Set Ciclovias not visible
            for (int i = 0; i < cicloviasLineList.size(); i++) {
                cicloviasLineList.get(i).setVisible(false);
            }
            for (Marker marker : ListMarkersAcessos) {
                marker.setVisible(false);
            }
        }
		 // Caso não estejam desenhadas, desenhar!

        if (Constant.bikeLanesStates[1]) {
            if (ciclofaixasLineList.isEmpty()) {

                drawTemporarias(true);

            } else {

                for (int i = 0; i < ciclofaixasLineList.size(); i++) {
                    ciclofaixasLineList.get(i).setVisible(true);
                }
            }

        } else {
            for (int i = 0; i < ciclofaixasLineList.size(); i++) {
                ciclofaixasLineList.get(i).setVisible(false);
            }
        }


        if (Constant.bikeLanesStates[2]) {
            if (ciclorrotasLineList.isEmpty()) {

                drawPreferenciais(true);

            } else {
                for (int i = 0; i < ciclorrotasLineList.size(); i++) {
                    ciclorrotasLineList.get(i).setVisible(true);
                }
            }

        } else {
            for (int i = 0; i < ciclorrotasLineList.size(); i++) {
                ciclorrotasLineList.get(i).setVisible(false);
            }
        }

	}

    public void selectSharingSystems() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alert = alertBuilder.create();
        View alertView = getLayoutInflater().inflate(R.layout.ad_selectsharingsystems, null);
        alert.setView(alertView);
        alert.setCancelable(false);
        ToggleButton tbPermanentes = (ToggleButton) alertView.findViewById(R.id.tb_sharingsystems_bs);
        tbPermanentes.setChecked(Constant.sharingSystemsStates[0]);
        tbPermanentes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.sharingSystemsStates[0] = isChecked;
            }
        });
        ToggleButton tbLazer = (ToggleButton) alertView.findViewById(R.id.tb_sharingsystems_cs);
        tbLazer.setChecked(Constant.sharingSystemsStates[1]);
        tbLazer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.sharingSystemsStates[1] = isChecked;
            }
        });
        Button btOk = (Button) alertView.findViewById(R.id.bt_sharingsystems_ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("sharingSystemsStates0", Constant.sharingSystemsStates[0]);
                editor.putBoolean("sharingSystemsStates1", Constant.sharingSystemsStates[1]);
                editor.apply();

                if (Constant.states[1]) {
                    displaySharingSystems();
                }
            }
        });

        alert.show();
    }

    public void displaySharingSystems() {

        if (Constant.sharingSystemsStates[0]) {
            if (ListMarkersITAU.isEmpty()) {

                drawBikeSampa(true);

            } else {

                for (int i = 0; i < ListMarkersITAU.size(); i++) {
                    ListMarkersITAU.get(i).setVisible(true);
                }
            }

        } else {
            for (int i = 0; i < ListMarkersITAU.size(); i++) {
                ListMarkersITAU.get(i).setVisible(false);
            }
        }

        if (Constant.sharingSystemsStates[1]) {
            if (ListMarkersBRA.isEmpty()) {

                drawCicloSampa(true);

            } else {

                for (int i = 0; i < ListMarkersBRA.size(); i++) {
                    ListMarkersBRA.get(i).setVisible(true);
                }

            }
        } else {
            for (int i = 0; i < ListMarkersBRA.size(); i++) {
                ListMarkersBRA.get(i).setVisible(false);
            }
        }
    }

    /* END DRAWER FUNCTIONALITY */
    /* LOAD MAP AND BASIC LOCATION FUNCTIONALITY */

	@Override
	public void onMapReady(GoogleMap gMap) {
		googleMap = gMap;
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
		} else {
			googleMap.setMyLocationEnabled(true);

			// GET LOCATION MANAGER
			locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);

			Location userLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (userLoc != null) {
				user_latlng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
			}
			//Get Best Location Provider
			bestAvailableProvider = locationManager.getBestProvider(criteria, false);
			if (bestAvailableProvider != null) {
				if (locationManager.isProviderEnabled(bestAvailableProvider)) {
					locationManager.requestLocationUpdates(bestAvailableProvider, 0, 0, this);
				}  else {
					Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
			}
		}
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		googleMap.setInfoWindowAdapter(new InfoWindowActivity(getLayoutInflater()));
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		googleMap.getUiSettings().setCompassEnabled(true);

        setMapEvents();
        if (!savedInstanceStateHasRun) {

            Log.e("SIShasRun", "FALSE");

            setUserLocation();

            try {
                createBaseArrays();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isNetworkAvailable()) {
                getDataFromDB();
            } else {
                showNetworkAlertDialog();
            }

        } else {

            Log.e("SIShasRun", "TRUE");

            redrawOnMapAfterSavedInstance();

        }

		int parkedHereSize = Integer.valueOf(sharedPreferences.getString(Constant.spParkedHereListSize, "0"));
		parkedHereMarkerList = new ArrayList<>();
		for (int i = 0; i < parkedHereSize; i++) {
			double lat = Double.valueOf(sharedPreferences.getString(Constant.spParkedHereLat+i, ""));
			double lng = Double.valueOf(sharedPreferences.getString(Constant.spParkedHereLng+i, ""));
			if (lat != 0 && lng != 0) {
				parkedHereMarkerList.add(googleMap.addMarker(new MarkerOptions()
						.title(getString(R.string.your_bike_is_here))
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_parked_here))
						.anchor(0.5f, 1f)
						.position(new LatLng(lat, lng))));
			}
		}

		/*
		int favoritePlacesSize = Integer.valueOf(sharedPreferences.getString(Constant.spFavoritePlaceListSize, "0"));
		Log.e("Favorite SIZE", String.valueOf(favoritePlacesSize));
		for (int i = 0; i < favoritePlacesSize; i++) {
			Log.e("Favorite I", String.valueOf(i));
			double lat = Double.valueOf(sharedPreferences.getString(Constant.spFavoritePlaceLat+i, ""));
			double lng = Double.valueOf(sharedPreferences.getString(Constant.spFavoritePlaceLng+i, ""));
			if (lat != 0 && lng != 0) {
				favoritePlacesMarkerList.add(googleMap.addMarker(new MarkerOptions()
						.title(getString(R.string.saved_place))
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_favorite_place))
						.anchor(0.5f, 0.5f)
						.position(new LatLng(lat, lng))));
			}
		}*/

		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.null_map), Toast.LENGTH_SHORT)
					.show();
		}
	}

	// Method to find user location and centers map on it
	public void setUserLocation() {

		CameraUpdate cameraUpdate;

		LatLng latLng_sp = new LatLng(-23.550765, -46.630437);

		if (user_latlng != null) {

			double lat = user_latlng.latitude;
			double lng = user_latlng.longitude;

			if (lat > -23.778678 && lat < -23.400375 && lng > -46.773075 && lng < -46.355934) {
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 16);
			} else {
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng_sp, 12);
			}
		} else {
			cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng_sp, 12);
		}
		googleMap.moveCamera(cameraUpdate);

	}

	public void findMe(View view) {

		if (user_latlng != null) {

			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 16);
			googleMap.animateCamera(cameraUpdate);

		} else {

			// If provider is enabled, check if location permission is granted
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

				// if not granted, ask permission
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

			} else {

				if (locationManager.isProviderEnabled(bestAvailableProvider)) {

					// If granted, request location update

					Toast.makeText(this, R.string.loc_buscando_localizacao, Toast.LENGTH_SHORT).show();
					locationManager.requestSingleUpdate(bestAvailableProvider, new LocationListener() {
						@Override
						public void onLocationChanged(Location location) {

							user_latlng = new LatLng(location.getLatitude(), location.getLongitude());
							CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 16);
							googleMap.animateCamera(cameraUpdate);
						}

						@Override
						public void onStatusChanged(String provider, int status, Bundle extras) {}
						@Override
						public void onProviderEnabled(String provider) {}
						@Override
						public void onProviderDisabled(String provider) {}
					}, null);

				} else {
					Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void setMapEvents() {

		googleMap.setOnMapClickListener(new OnMapClickListener() {

			public void onMapClick(LatLng point) {

                hideAllBottomButtons();

				for (Marker marker : listMarker) {
					marker.remove();
				}
				listMarker.clear();

				// Remove graph_point_marker

				if (graph_point_marker != null) {
					graph_point_marker.remove();
				}
				// Remove markerSearch from old search
				if (markerSearch != null) {
					markerSearch.remove();
					markerSearch = null;
				}

				Projection projection = googleMap.getProjection();
				Point pointScreen = projection.toScreenLocation(point);
				Point testPointScreen = new Point(pointScreen.x + 30, pointScreen.y);
				LatLng testPoint = projection.fromScreenLocation(testPointScreen);

				Location pointLocation = new Location("pointLocation");
				pointLocation.setLatitude(point.latitude);
				pointLocation.setLongitude(point.longitude);

				Location testPointLocation = new Location("testPointLocation");
				testPointLocation.setLatitude(testPoint.latitude);
				testPointLocation.setLongitude(testPoint.longitude);

				double maxDistance = pointLocation.distanceTo(testPointLocation);

				CheckClick checking = new CheckClick();

				if (!cicloviasLineList.isEmpty()) {
					if (cicloviasLineList.get(0).isVisible()) {

						for (int i = 0; i < cicloviasLineList.size(); i++) {
							List<LatLng> list = cicloviasLineList.get(i).getPoints();
							LatLng closestPoint = checking.checkClick(point, list, maxDistance);
							if (closestPoint != null) {
								listMarker.add(googleMap.addMarker(new MarkerOptions()
										.position(new LatLng(closestPoint.latitude, closestPoint.longitude))
										.title(ListCiclovias.get(i).Nome)
										.snippet(ListCiclovias.get(i).Info + newline + newline
												+ getString(R.string.distancia_total) + " " + ListCiclovias.get(i).Dist + " km")
										.anchor(0.5f, 0.0f)
										.alpha(0)));
								listMarker.get(0).showInfoWindow();

								// Center map in clicked point
								CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(closestPoint);
								googleMap.animateCamera(cameraUpdate);
							}
						}
					}
				}

				if (!ciclofaixasLineList.isEmpty()) {
					if (ciclofaixasLineList.get(0).isVisible()) {

						for (int i = 0; i < ciclofaixasLineList.size(); i++) {
							List<LatLng> list = ciclofaixasLineList.get(i).getPoints();
							LatLng closestPoint = checking.checkClick(point, list, maxDistance);
							if (closestPoint != null) {
								Marker marker = googleMap.addMarker(new MarkerOptions()
										.position(new LatLng(closestPoint.latitude, closestPoint.longitude))
										.title(ListCiclofaixas.get(i).Nome)
										.anchor(0.5f, 0.0f)
										.alpha(0));
								if (rightNow.after(sundaySeven) && rightNow.before(sundaySixteen)) {
									marker.setSnippet(getString(R.string.open) + newline
											+ ListCiclofaixas.get(i).Info + newline + newline
											+ getString(R.string.distancia_total) + " " + ListCiclofaixas.get(i).Dist + " km");
								} else {
									marker.setSnippet(getString(R.string.closed) + newline
											+ ListCiclofaixas.get(i).Info + newline + newline
											+ getString(R.string.distancia_total) + " " + ListCiclofaixas.get(i).Dist + " km");
								}
								listMarker.add(marker);
								listMarker.get(0).showInfoWindow();

								// Center map in clicked point
								CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(closestPoint);
								googleMap.animateCamera(cameraUpdate);
							}
						}
					}
				}

				if (!polylineRoutesList.isEmpty()) {
					for (int i = 0; i < polylineRoutesList.size(); i++) {
						List<LatLng> list = polylineRoutesList.get(i).getPoints();
						LatLng closestPoint = checking.checkClick(point, list, maxDistance);
						if (closestPoint != null) {
							for (Polyline p : polylineRoutesList) {
								p.setColor(getApplicationContext().getResources().getColor(R.color.not_selected_route_blue));
							}
							polylineRoutesList.get(i).setColor(getApplicationContext().getResources().getColor(R.color.selected_route_blue));
							viewPager.setCurrentItem(i);
						}
					}
				}


			}
		});

		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				activeMarker = marker;

				String markerTitle = marker.getTitle();
				t.send(new HitBuilders.EventBuilder()
						.setCategory("markerClick")
						.setAction(markerTitle)
						.build());

				// Remove markerSearch from old search
				if (markerSearch != null) {
					markerSearch.remove();
					markerSearch = null;
				}

				// Remove graph_point_marker
				if (graph_point_marker != null) {
					graph_point_marker.remove();
				}

                if (ListMarkersAlerts.contains(marker)) {
                    showBottomButton(notifyButton);
                } else {
                    hideBottomButton(notifyButton);
                }

				if (ListMarkersBicicletarios.contains(marker)) {
                    showBottomButton(btParkedHere);
				} else {
                    hideBottomButton(btParkedHere);
                    //hideBottomButton(llPlaceOptions);
                }

                if (marker.getTitle().equals(getString(R.string.your_bike_is_here))
					//|| marker.getTitle().equals(getString(R.string.saved_place))
						) {
                    showBottomButton(btRemovePlace);
                } else {
                    hideBottomButton(btRemovePlace);
                    //hideBottomButton(llPlaceOptions);
                }

				// Funcionalidades padrões para quando se clica em qualquer marcador
				marker.showInfoWindow();
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 200, null);
				return true;
			}

		});

		googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {

                hideAllBottomButtons();

				// Remove graph_point_marker
				if (graph_point_marker != null) {
					graph_point_marker.remove();
				}

				// Geocode LatLng to Address
				final LatLng ll = latLng;

				new AsyncTask<String, Void, String>() {
					protected void onPreExecute() {
						btLupa.setVisibility(View.GONE);
						pBarSearch.setVisibility(View.VISIBLE);
						btRoute.setVisibility(View.GONE);
						pBarRoute.setVisibility(View.VISIBLE);
						if (isRouteModeOn) {
							setUpdating();
						}
					}

					@Override
					protected String doInBackground(String... params) {
						String sAddress = "";
						List<Address> adList = new ArrayList<>();
						try {
							adList = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (!adList.isEmpty()) {
							Address address = adList.get(0);
							sAddress = address.getAddressLine(0);
							for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
								sAddress = sAddress + ", " + address.getAddressLine(i);
							}
						}
						return sAddress;
					}

					protected void onPostExecute(String sAddress) {

						pBarSearch.setVisibility(View.GONE);
						btLupa.setVisibility(View.VISIBLE);
						pBarRoute.setVisibility(View.GONE);
						btRoute.setVisibility(View.VISIBLE);

						if (isRouteModeOn) {

							resetUpdating();

							View v = getCurrentFocus();
							if (v != null && v.getId() == R.id.et_origin) {

								// Remove markerOrigin, if there's one, and add again.
								if (markerOrigin != null) {
									markerOrigin.remove();
								}

								etOrigin.setVisibility(View.GONE);
								tvOrigin.setVisibility(View.VISIBLE);

								// If sAddress is an empty string, replace with "Marcador inserido"
								if (sAddress.equals("")) {
									tvOrigin.setText(getString(R.string.marcador_inserido));
								} else {
									tvOrigin.setText(sAddress);
								}

								markerOrigin = googleMap.addMarker(new MarkerOptions()
										.position(ll)
										.title(getString(R.string.partida)));

								if (tvDestination.getVisibility() == View.VISIBLE) {
									getRoutes();
								}

							} else {

								if (etDestination.getVisibility() == View.VISIBLE) {
									etDestination.setVisibility(View.GONE);
									tvDestination.setVisibility(View.VISIBLE);
								}

								// If sAddress is an empty string, replace with "Marcador inserido"
								if (sAddress.equals("")) {
									tvDestination.setText(getString(R.string.marcador_inserido));
								} else {
									tvDestination.setText(sAddress);
								}

								// Remove markerDestination, if there's one, and add again.
								if (markerDestination != null) {
									markerDestination.remove();
								}
                                markerDestination = googleMap.addMarker(new MarkerOptions()
										.position(ll)
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_chegada))
										.anchor(0.0f, 1.0f)
										.title(getString(R.string.chegada)));

								if (tvOrigin.getVisibility() == View.VISIBLE) {
									getRoutes();
								}

							}

						} else {

							btClearSearch.setVisibility(View.VISIBLE);

							// Remove markerSearch, if there's one, and add again.
							if (markerSearch != null) {
								markerSearch.remove();
								markerSearch = null;
							}

							if (sAddress.equals("")) {
								etSearch.setText(getString(R.string.marcador_inserido));
								activeMarker = markerSearch = googleMap.addMarker(new MarkerOptions()
										.position(ll)
										.title(getString(R.string.marcador_inserido)));
							} else {
								etSearch.setText(sAddress);
								activeMarker = markerSearch = googleMap.addMarker(new MarkerOptions()
										.position(ll)
										.title(sAddress));
							}

                            showBottomButton(btParkedHere);
						}
					}
				}.execute();
			}
		});

		googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				marker.hideInfoWindow();
                hideAllBottomButtons();
			}
		});
	}

	public void findAddress(View view) {

		// Hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

		addressList.clear();
		addressListBase.clear();

		// Get the string from the EditText
		final String s_address = etSearch.getText().toString();

		new AsyncTask<String, String, String>() {

			@Override
			protected void onPreExecute() {

				// Limpar marcadores antigos de outras buscas, antes de criar um novo.
				if (markerSearch != null) {
					markerSearch.remove();
					markerSearch = null;
				}

				btLupa.setVisibility(View.GONE);
				pBarSearch.setVisibility(View.VISIBLE);

				if (!isNetworkAvailable()) {
					cancel(true);
				}
			}

			@Override
			protected String doInBackground(String... params) {

				//Checar primeiro se algo foi digitado.
				if (!s_address.trim().equals("")) {
					try {
						addressListBase = geocoder.getFromLocationName(s_address, 20, Constant.llLat, Constant.llLng, Constant.urLat, Constant.urLng);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					cancel(true);
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				pBarSearch.setVisibility(View.GONE);
				btLupa.setVisibility(View.VISIBLE);

				final ArrayList<Address> addressList = new ArrayList<>();

				int u = 0;

				for (int i = 0; i < addressListBase.size(); i++) {

					Address ad = addressListBase.get(i);

					// Place locations in São Paulo on top of list
					if (i != 0 && ad.getLocality() != null) {
						if (ad.getLocality().equalsIgnoreCase("São Paulo")) {
							addressList.add(addressList.get(u));
							addressList.set(u, ad);
							u++;
						}
					} else if (i != 0 && ad.getSubAdminArea() != null) {
						if (ad.getSubAdminArea().equalsIgnoreCase("São Paulo")) {
							addressList.add(addressList.get(u));
							addressList.set(u, ad);
							u++;
						}
					} else {
						addressList.add(ad);
					}
				}

				String[] s_addressList = null;
				ArrayList<String> array_address = new ArrayList<>();

				// Checar se o endere�o n�o por acaso foi encontrado. Caso negativo, ent�o lan�ar o AlertDialog no "else".
				if (!addressList.isEmpty()) {

					// Create String[] with addresses, limiting to 5 addresses.
					for (int i = 0; i < addressList.size() && i < 5; i++) {

						// Check number of AddressLine before using the second
						if (addressList.get(i).getMaxAddressLineIndex() > 0) {
							array_address.add(addressList.get(i).getAddressLine(0) + ", "
									+ addressList.get(i).getAddressLine(1));
						} else {
							array_address.add(addressList.get(i).getAddressLine(0));
						}

						s_addressList = new String[array_address.size()];
						s_addressList = array_address.toArray(s_addressList);
					}

					if (addressList.size() == 1) {
						Address address = addressList.get(0);
						if (address.hasLatitude() && address.hasLongitude()) {
							double lat = address.getLatitude();
							double lng = address.getLongitude();
							LatLng coordinate = new LatLng(lat, lng);

							CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
							CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.6f);
							googleMap.moveCamera(center);
							googleMap.animateCamera(zoom);
                            activeMarker = markerSearch = googleMap.addMarker(new MarkerOptions()
									.position(new LatLng(lat, lng))
									.title(address.getAddressLine(0)));

							// Display btRouteMode
							//showRouteButton.start();

							// Set the text on etSearch to be the complete address
							String finalStringAddress = address.getAddressLine(0);
							for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
								finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
							}
							etSearch.setText(finalStringAddress);
							btClearSearch.setVisibility(View.VISIBLE);
                            showBottomButton(btParkedHere);
						}

					} else {
						AlertDialog.Builder alert_enderecos = new AlertDialog.Builder(MainActivity.this);
						alert_enderecos.setTitle(getString(R.string.which_address))
								.setItems(s_addressList, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Address address = addressList.get(which);
										if (address.hasLatitude() && address.hasLongitude()) {
											double lat = address.getLatitude();
											double lng = address.getLongitude();
											LatLng coordinate = new LatLng(lat, lng);

											CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
											CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.6f);
											googleMap.moveCamera(center);
											googleMap.animateCamera(zoom);
                                            activeMarker = markerSearch = googleMap.addMarker(new MarkerOptions()
													.position(new LatLng(lat, lng))
													.title(address.getAddressLine(0)));

											// Set the text on etSearch to be the complete address
											String finalStringAddress = address.getAddressLine(0);
											for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
												finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
											}
											etSearch.setText(finalStringAddress);
											btClearSearch.setVisibility(View.VISIBLE);
                                            showBottomButton(btParkedHere);
										}
									}
								});
						alert_enderecos.show();
					}
				} else {
					AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
					alert.setTitle(getString(R.string.end_nao_encontrado_titulo))
							.setMessage(getString(R.string.end_nao_encontrado_mensagem))
							.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});
					// Create the AlertDialog object and return it
					alert.show();
				}
			}

			protected void onCancelled() {

				pBarSearch.setVisibility(View.GONE);
				btLupa.setVisibility(View.VISIBLE);

				showNetworkAlertDialog();
			}
		}.execute();
	}

	public void showNetworkAlertDialog () {
		AlertDialog.Builder network_alert = new AlertDialog.Builder(MainActivity.this);
		network_alert.setTitle(getString(R.string.network_alert_title))
				.setMessage(getString(R.string.network_alert_dialog))
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				})
				.setNegativeButton(getString(R.string.network_settings), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		network_alert.show();
	}

	public void clearSearch(View view) {
		etSearch.setText("");
		if (markerSearch != null) {
			markerSearch.remove();
			markerSearch = null;
		}
		btClearSearch.setVisibility(View.GONE);

	}

    /* END LOAD MAP AND BASIC LOCATION FUNCIONALITY */
	/* ADD SOMETHING */

	public void addFunction (View view) {

        Intent i = new Intent(MainActivity.this, ReportActivity.class);
        if (user_latlng != null) {
            i.putExtra("latitude", user_latlng.latitude);
            i.putExtra("longitude", user_latlng.longitude);
        }
        startActivity(i);
	}

    /* REPORT MANAGING */

	public void notifySolved (View view) {

		boolean b = false;

		if (isNetworkAvailable()) {
			for (int i = 0; i < ListMarkersAlerts.size(); i++) {

				if (ListMarkersAlerts.get(i).isInfoWindowShown()) {
					String timestamp = alertMap.get(ListMarkersAlerts.get(i).getId());

					NotifySolvedReport notifyObj = new NotifySolvedReport();
					try {
						notifyObj.sendReport(timestamp);
					} catch (Exception e) {
						e.printStackTrace();
					}

					ListMarkersAlerts.get(i).hideInfoWindow();
					hideBottomButton(notifyButton);

					b = true;
				}
			}

			if (b) {
				Toast.makeText(this, getString(R.string.obrigado_por_avisar), Toast.LENGTH_SHORT).show();
			} else {
				Toast t = Toast.makeText(this, getString(R.string.nenhum_alerta_selecionado), Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
			}
		} else {

			AlertDialog.Builder network_alert = new AlertDialog.Builder(MainActivity.this);
			network_alert.setTitle(getString(R.string.network_alert_title))
					.setMessage(getString(R.string.network_alert_dialog))
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					})
					.setNegativeButton(getString(R.string.network_settings), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
					});
			network_alert.show();
		}

	}

	public void setParkedHere (View view) {

        String deviceID = sharedPreferences.getString(Constant.deviceID, "");
        if (activeMarker != null) {
            parkedHereMarkerList.add(googleMap.addMarker(new MarkerOptions()
                    .position(activeMarker.getPosition())
                    .title(getString(R.string.your_bike_is_here))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_parked_here))
                    .anchor(0.5f, 1f)));
            Calls.sendParkedHere(deviceID, String.valueOf(activeMarker.getPosition().latitude), String.valueOf(activeMarker.getPosition().longitude), null);

            hideBottomButton(view);

        }

        int lastIndex = parkedHereMarkerList.size() - 1;

        sharedPreferences.edit().putString(Constant.spParkedHereListSize, String.valueOf(parkedHereMarkerList.size()))
                .putString(Constant.spParkedHereLat + lastIndex, String.valueOf(parkedHereMarkerList.get(lastIndex).getPosition().latitude))
                .putString(Constant.spParkedHereLng + lastIndex, String.valueOf(parkedHereMarkerList.get(lastIndex).getPosition().longitude))
                .apply();

    }

    public void removePlace(View view) {
		hideBottomButton(btRemovePlace);
        if (activeMarker != null) { activeMarker.remove();}
        // Remove marker from list
        for (int i = 0 ; i < parkedHereMarkerList.size() ; i++) {
            if (parkedHereMarkerList.get(i).getId().equals(activeMarker.getId())) {
                parkedHereMarkerList.remove(i);
				parkedHereMarkerList.trimToSize();
            }
        }

        // Update all spParkedHere info on SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.spParkedHereListSize, String.valueOf(parkedHereMarkerList.size()));
        for (int i = 0 ; i < parkedHereMarkerList.size() ; i++) {
            editor.putString(Constant.spParkedHereLat + i, String.valueOf(parkedHereMarkerList.get(i).getPosition().latitude))
                    .putString(Constant.spParkedHereLng + i, String.valueOf(parkedHereMarkerList.get(i).getPosition().longitude));
        }
        editor.apply();

    }

    /* END REPORT MANAGING */
    /* SETTING UP INFO FROM DB */

	public void getDataFromDB() {

		setUpdating();

		Calls.jsonRequest(Constant.url_obter_dados, getDataHandler);
		Calls.jsonRequest(Constant.url_obter_bikesampa, getBikeSampaHandler);
		Calls.jsonRequest(Constant.url_obter_ciclosampa, getCicloSampaHandler);
	}

	CallHandler getDataHandler = new CallHandler() {
		@Override
		public void onSuccess(int code, String response) {
			Log.e("getDataHandler", code +": "+response);

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(Constant.spJobGeral, response);
			editor.apply();

			try {
				createBaseArrays();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	CallHandler getBikeSampaHandler = new CallHandler() {
		@Override
		public void onSuccess(int code, String response) {
			Log.e("getBikeSampasHandler", code +": "+response);

			Calendar now = Calendar.getInstance();
			String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
			String minutes = String.valueOf(now.get(Calendar.MINUTE));
			if (minutes.length() == 1) {
				minutes = "0" + minutes;
			}
			String updateTimeBS = hours + ":" + minutes;

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(Constant.spJobBS, response);
			editor.putString(Constant.spUpdateTimeBS, updateTimeBS);
			editor.apply();

			try {
				createBikeSampaArray();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	CallHandler getCicloSampaHandler = new CallHandler() {
		@Override
		public void onSuccess(int code, String response) {
			Log.e("getCicloSampaHandler", code +": "+response);

			Calendar now = Calendar.getInstance();
			String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
			String minutes = String.valueOf(now.get(Calendar.MINUTE));
			if (minutes.length() == 1) {
				minutes = "0" + minutes;
			}
			String updateTimeCS = hours + ":" + minutes;

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(Constant.spJobCS, response);
			editor.putString(Constant.spUpdateTimeCS, updateTimeCS);
			editor.apply();

			try {
				createCicloSampaArray();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	public void createBaseArrays() throws JSONException {

		// Dados gerais

		String strJobGeral = sharedPreferences.getString(Constant.spJobGeral, null);

		if (strJobGeral != null) {

			JSONObject job = new JSONObject(strJobGeral);

			try {

				JSONArray ParquesJSArray = job.getJSONArray("PARQUES");

				//Clear list before adding updated items
				ListParques.clear();

				// looping por todos os Estacionamentos
				for (int i = 0; i < ParquesJSArray.length(); i++) {
					JSONObject c = ParquesJSArray.getJSONObject(i);

					// Storing each json item in variable
					String nome = c.getString("nome");
					String endereco = c.getString("endereco");
					String lat = c.getString("lat");
					String lng = c.getString("lng");
					String descricao = c.getString("descricao");
					String funcionamento = c.getString("funcionamento");
					String ciclovia = c.getString("ciclovia");
					String contato = c.getString("contato");
					int wifi = c.getInt("wifi");

					double latitude = Double.parseDouble(lat);
					double longitude = Double.parseDouble(lng);

					Parque item_parque = new Parque(nome, endereco, latitude, longitude, descricao, funcionamento, ciclovia, contato, wifi);

					ListParques.add(item_parque);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray BicicletariosJSArray = job.getJSONArray("BICICLETARIOS");

				//Clear list before adding updated items
				ListBicicletarios.clear();

				// looping por todos os itens
				for (int i = 0; i < BicicletariosJSArray.length(); i++) {
					JSONObject c = BicicletariosJSArray.getJSONObject(i);

					// Storing each json item in variable
					String nome = c.getString("nome");
					String lat = c.getString("lat");
					String lng = c.getString("lng");
					String address = c.getString("address");
					String tipo = c.getString("tipo");
					int vagas = c.getInt("vagas");
					String emprestimo = c.getString("emprestimo");
					String horario = c.getString("horario");

					double latitude = Double.parseDouble(lat);
					double longitude = Double.parseDouble(lng);

					Bicicletario item_bicicletario = new Bicicletario(nome, latitude, longitude, address, tipo, vagas, emprestimo, horario);

					ListBicicletarios.add(item_bicicletario);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray WifiJSArray = job.getJSONArray("WIFI");

				//Clear list before adding updated items
				ListWifi.clear();

				// looping por todos os Estacionamentos
				for (int i = 0; i < WifiJSArray.length(); i++) {
					JSONObject c = WifiJSArray.getJSONObject(i);

					// Storing each json item in variable
					String nome = c.getString("nome");
					String lat = c.getString("lat");
					String lng = c.getString("lng");
					String end = c.getString("endereco");

					double latitude = Double.parseDouble(lat);
					double longitude = Double.parseDouble(lng);

					MarkerOptions item_wifi = new MarkerOptions()
							.title(nome)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_wifi))
							.snippet(end)
							.position(new LatLng(latitude, longitude));

					ListWifi.add(item_wifi);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray ReportsJSArray = job.getJSONArray("REPORTS");

				//Clear list before adding updated items
				ListAlerts.clear();

				// looping por todos os Estacionamentos
				for (int i = 0; i < ReportsJSArray.length(); i++) {
					JSONObject c = ReportsJSArray.getJSONObject(i);

					// Storing each json item in variable
					String tipo = c.getString("tipo");
					String lat = c.getString("lat");
					String lng = c.getString("lng");
					String desc = c.getString("descricao");
					String timestamp = c.getString("timestamp");
					String endereco = c.getString("endereco");

					double latitude = Double.parseDouble(lat);
					double longitude = Double.parseDouble(lng);

					Report report = new Report(tipo, endereco, latitude, longitude, desc, timestamp);

					ListAlerts.add(report);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray cicloviasJSArray = job.getJSONArray("CICLOVIAS");

				// Clear list before adding updated items
				ListCiclovias.clear();

				for (int i = 0; i < cicloviasJSArray.length(); i++) {

					JSONObject cicloviaJSObject = cicloviasJSArray.getJSONObject(i);

					JSONArray pontosJSArray = cicloviaJSObject.getJSONArray("ciclovia");

					JSONObject first_object = pontosJSArray.getJSONObject(0);
					String nome = first_object.getString("nome");
					String info = first_object.getString("info");
					String dist_string = first_object.getString("distancia");
					double dist = Double.parseDouble(dist_string);
					String tipoString = first_object.getString("tipo");
					int tipo = Integer.valueOf(tipoString);
					ArrayList<LatLng> list = new ArrayList<>();

					// looping por todos os pontos da Ciclovia
					for (int y = 0; y < pontosJSArray.length(); y++) {
						JSONObject c = pontosJSArray.getJSONObject(y);

						String lat = c.getString("lat");
						String lng = c.getString("lng");

						double latitude = Double.parseDouble(lat);
						double longitude = Double.parseDouble(lng);
						LatLng latLng = new LatLng(latitude, longitude);

						list.add(latLng);
					}
					Ciclovia ciclovia = new Ciclovia(nome, info, dist, list, tipo);

					ListCiclovias.add(ciclovia);

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray ciclofaixasJSArray = job.getJSONArray("CICLOFAIXAS");

				// Clear list before adding updated items
				ListCiclofaixas.clear();

				for (int i = 0; i < ciclofaixasJSArray.length(); i++) {

					JSONObject ciclofaixasJSObject = ciclofaixasJSArray.getJSONObject(i);

					JSONArray pontosJSArray = ciclofaixasJSObject.getJSONArray("ciclofaixa");

					JSONObject first_object = pontosJSArray.getJSONObject(0);
					String nome = first_object.getString("nome");
					String info = first_object.getString("info");
					String dist_string = first_object.getString("distancia");
					double dist = Double.parseDouble(dist_string);
					ArrayList<LatLng> list = new ArrayList<>();

					// looping por todos os pontos da Ciclovia
					for (int y = 0; y < pontosJSArray.length(); y++) {
						JSONObject c = pontosJSArray.getJSONObject(y);

						String lat = c.getString("lat");
						String lng = c.getString("lng");

						double latitude = Double.parseDouble(lat);
						double longitude = Double.parseDouble(lng);
						LatLng latLng = new LatLng(latitude, longitude);

						list.add(latLng);
					}
					Ciclovia ciclofaixa = new Ciclovia(nome, info, dist, list, 1);

					ListCiclofaixas.add(ciclofaixa);

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray ciclorrotasJSArray = job.getJSONArray("CICLORROTAS");

				// Clear list before adding updated elements
				ciclorrotasOptionsList.clear();

				for (int i = 0; i < ciclorrotasJSArray.length(); i++) {

					JSONObject ciclorrotasJSObject = ciclorrotasJSArray.getJSONObject(i);

					JSONArray pontosJSArray = ciclorrotasJSObject.getJSONArray("ciclorrota");

					ArrayList<LatLng> list = new ArrayList<>();

					// looping por todos os pontos da Ciclovia
					for (int y = 0; y < pontosJSArray.length(); y++) {
						JSONObject c = pontosJSArray.getJSONObject(y);

						String lat = c.getString("lat");
						String lng = c.getString("lng");

						double latitude = Double.parseDouble(lat);
						double longitude = Double.parseDouble(lng);
						LatLng latLng = new LatLng(latitude, longitude);

						list.add(latLng);
					}
					PolylineOptions polyline = new PolylineOptions();
					polyline.addAll(list);

					ciclorrotasOptionsList.add(polyline);

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

            try {

                JSONArray AccessJSArray = job.getJSONArray("ACESSOS");

                //Clear list before adding updated items
                ListAcesso.clear();

                // looping por todos os Estacionamentos
                for (int i = 0; i < AccessJSArray.length(); i++) {
                    JSONObject c = AccessJSArray.getJSONObject(i);

                    // Storing each json item in variable
                    String nome = c.getString("name");
                    String lat = c.getString("lat");
                    String lng = c.getString("lng");
                    String details = c.getString("details");

                    double latitude = Double.parseDouble(lat);
                    double longitude = Double.parseDouble(lng);

                    MarkerOptions item_acesso = new MarkerOptions()
                            .title(getResources().getString(R.string.acesso_ciclovia_marginal) + newline + nome)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_access))
                            .snippet(details)
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(latitude, longitude));

                    ListAcesso.add(item_acesso);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


			myAdapter.notifyDataSetChanged();

			drawPermanentes((Constant.states[0] && Constant.bikeLanesStates[0]));

			drawTemporarias((Constant.states[0] && Constant.bikeLanesStates[1]));

			drawPreferenciais((Constant.states[0] && Constant.bikeLanesStates[2]));

            drawBicicletarios(Constant.states[2]);

			drawParques(Constant.states[3]);

			drawWifi(Constant.states[4]);

			drawAlerts(Constant.states[5]);

            resetUpdating();
		}

	}

	public void createBikeSampaArray() throws JSONException {

		String strJobBS = sharedPreferences.getString(Constant.spJobBS, null);

		if (strJobBS != null) {

			JSONObject jsonObjBS = new JSONObject(strJobBS);

			try {

				JSONArray BSjSonArray = jsonObjBS.getJSONArray("ESTACOES_ITAU");

				//Clear list before adding updated items
				ListEstacoesITAU.clear();

				// looping por todos os Estacionamentos
				for (int i = 0; i < BSjSonArray.length() - 1; i++) {
					JSONObject c = BSjSonArray.getJSONObject(i);

					// Storing each json item in variable
					int numero = c.getInt("numero");
					String nome = c.getString("nome");
					String descricao = c.getString("descricao");
					String lat = c.getString("lat");
					String lng = c.getString("lng");
					String status1 = c.getString("status1");
					String status2 = c.getString("status2");
					int bikes = c.getInt("bikes");
					int tamanho = c.getInt("tamanho");


					double latitude = Double.parseDouble(lat);
					double longitude = Double.parseDouble(lng);

					Estacao item_estacao = new Estacao(numero, nome, descricao, latitude, longitude, status1, status2, bikes, tamanho);

					ListEstacoesITAU.add(item_estacao);
				}

				if (!ListEstacoesITAU.isEmpty()) {

					myAdapter.notifyDataSetChanged();

					drawBikeSampa((Constant.states[1] && Constant.sharingSystemsStates[0]));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public void createCicloSampaArray() throws JSONException {

		String strJobCS = sharedPreferences.getString(Constant.spJobCS, null);

		if (strJobCS != null) {

			JSONObject jsonObjCS = new JSONObject(strJobCS);

			try {

				JSONArray CSjSonArray = jsonObjCS.getJSONArray("ESTACOES_BRADESCO");

				//Clear list before adding updated items
				ListEstacoesBRA.clear();

				// looping por todos os Estacionamentos
				for (int i = 0; i < CSjSonArray.length() - 1; i++) {
					JSONObject c = CSjSonArray.getJSONObject(i);

					// Storing each json item in variable
					int numero = c.getInt("numero");
					String nome = c.getString("nome");
					String descricao = c.getString("descricao");
					String lat = c.getString("lat");
					String lng = c.getString("lng");
					String status1 = c.getString("status1");
					String status2 = c.getString("status2");
					int bikes = c.getInt("bikes");
					int tamanho = c.getInt("tamanho");


					double latitude = Double.parseDouble(lat);
					double longitude = Double.parseDouble(lng);

					Estacao item_estacao = new Estacao(numero, nome, descricao, latitude, longitude, status1, status2, bikes, tamanho);

					ListEstacoesBRA.add(item_estacao);
				}

				if (!ListEstacoesBRA.isEmpty()) {

					myAdapter.notifyDataSetChanged();

					drawCicloSampa((Constant.states[1] && Constant.sharingSystemsStates[1]));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public void drawParques(Boolean visibility) {

		if (ListMarkersParques != null) {
			for (Marker marker : ListMarkersParques) marker.remove();
		}

		for (int i = 0; i < ListParques.size(); i++) {
			LatLng ll = ListParques.get(i).getLatLng();

			Marker marker = googleMap.addMarker(new MarkerOptions()
					.position(ll)
					.title(ListParques.get(i).Nome)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_park))
					.visible(visibility)
					.anchor(0.5f, 0.5f));
			if (ListParques.get(i).Wifi == 0) {
				marker.setSnippet(ListParques.get(i).Endereco
						+ newline + ListParques.get(i).Funcionamento
						+ newline + newline + ListParques.get(i).Descricao
						+ newline + getString(R.string.extensao_ciclovia) + " " + ListParques.get(i).Ciclovia
						+ newline + ListParques.get(i).Contato
						+ newline + newline + getString(R.string.tem_wifi_nao));
			} else {
				marker.setSnippet(ListParques.get(i).Endereco
						+ newline + ListParques.get(i).Funcionamento
						+ newline + newline + ListParques.get(i).Descricao
						+ newline + getString(R.string.extensao_ciclovia) + " " + ListParques.get(i).Ciclovia
						+ newline + ListParques.get(i).Contato
						+ newline + newline + getString(R.string.tem_wifi));
			}
			ListMarkersParques.add(marker);
		}
	}

	public void drawCicloSampa(Boolean visibility) {

		if (ListMarkersBRA != null) {
			for (Marker marker : ListMarkersBRA) marker.remove();
		}

		String updateTimeCS = sharedPreferences.getString(Constant.spUpdateTimeCS, "out of date");

		for (int i = 0; i < ListEstacoesBRA.size(); i++) {
			Estacao estacao = ListEstacoesBRA.get(i);

			LatLng ll = estacao.getLatLng();

			MarkerOptions estacaoMOpt = new MarkerOptions()
					.position(ll)
					.title(getString(R.string.estacao_ciclo_sampa) + newline + estacao.Numero + " - " + estacao.Nome)
					.anchor(0.5f, 0.5f)
					.visible(visibility);

			int vagasLivres = estacao.tamanho - estacao.bikes;

			if (estacao.status1.equals("A") && estacao.status2.equals("EO")) {

				if (estacao.bikes == 0) {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_vazia));
				} else if (vagasLivres == 0) {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_cheia));
				} else {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_operando));
				}

			} else if (estacao.status1.equals("I") && estacao.status2.equals("EO")) {
				estacaoMOpt.snippet(getString(R.string.offline)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_offline));
			} else {
				estacaoMOpt.snippet(getString(R.string.em_manutencao_implantacao)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_manutencao));
			}

			ListMarkersBRA.add(googleMap.addMarker(estacaoMOpt));
		}
	}

	public void drawBikeSampa(Boolean visibility) {

		if (ListMarkersITAU != null) {
			for (Marker marker : ListMarkersITAU) marker.remove();
		}

		String updateTimeBS = sharedPreferences.getString(Constant.spUpdateTimeBS, "out of date");

		for (int i = 0; i < ListEstacoesITAU.size(); i++) {

			Estacao estacao = ListEstacoesITAU.get(i);

			LatLng ll = estacao.getLatLng();

			MarkerOptions estacaoMOpt = new MarkerOptions()
					.position(ll)
					.title(getString(R.string.estacao_bike_sampa) + newline + estacao.Numero + " - " + estacao.Nome)
					.anchor(0.5f, 0.5f)
					.visible(visibility);

			int vagasLivres = estacao.tamanho - estacao.bikes;

			if (estacao.status1.equals("A") && estacao.status2.equals("EO")) {

				if (estacao.bikes == 0) {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_vazia));
				} else if (vagasLivres == 0) {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_cheia));
				} else {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_operando));
				}

			} else if (estacao.status1.equals("I") && estacao.status2.equals("EO")) {
				estacaoMOpt.snippet(getString(R.string.offline)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_offline));
			} else {
				estacaoMOpt.snippet(getString(R.string.em_manutencao_implantacao)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_manutencao));
			}

			ListMarkersITAU.add(googleMap.addMarker(estacaoMOpt));

		}
	}

	public void drawBicicletarios(Boolean visibility) {

		if (ListMarkersBicicletarios != null) {
			for (Marker marker : ListMarkersBicicletarios) marker.remove();
		}

		for (int i = 0; i < ListBicicletarios.size(); i++) {
			LatLng ll = ListBicicletarios.get(i).getLatLng();

			Marker marker = googleMap.addMarker(new MarkerOptions()
					.position(ll)
					.title(ListBicicletarios.get(i).Nome)
					.visible(visibility));

			if (ListBicicletarios.get(i).Tipo.equals("b")) {

                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_parking_b));
				marker.setAnchor(0.5f, 1.0f);
				if (ListBicicletarios.get(i).hasEmprestimo()) {
					marker.setSnippet(getString(R.string.vagas) + " " + ListBicicletarios.get(i).Vagas
							+ newline + newline + getString(R.string.possui_emprestimo_bicicleta)
							+ newline + newline + getString(R.string.funcionamento) + " " + ListBicicletarios.get(i).Horario);
				} else {
					marker.setSnippet(getString(R.string.vagas) + " " + ListBicicletarios.get(i).Vagas
							+ newline + newline + getString(R.string.possui_emprestimo_bicicleta_nao)
							+ newline + newline + getString(R.string.funcionamento) + " " + ListBicicletarios.get(i).Horario);
				}
			} else {

				marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_parking_p));
				marker.setAnchor(0.5f, 1.0f);
				marker.setSnippet(getString(R.string.vagas) + " " + ListBicicletarios.get(i).Vagas
                        + newline + getString(R.string.endereco_aproximado)+": " + ListBicicletarios.get(i).address);
			}
			ListMarkersBicicletarios.add(marker);
		}
	}

	public void drawWifi(Boolean visibility) {

		if (ListMarkersWifi != null) {
			for (Marker marker : ListMarkersWifi) marker.remove();
		}

		for (int i = 0; i < ListWifi.size(); i++) {
			// Aqui eu não adiciono o MarkerOptions inteiro de uma vez porque dava o erro esquisito do IObectjWrapper. 
			// Criando um MarkerOptions novo e puxando atributo por atributo da ListWifi não deu erro, então deve ficar assim.
			ListMarkersWifi.add
					(googleMap.addMarker(new MarkerOptions()
							.position(ListWifi.get(i).getPosition())
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_wifi))
							.title(ListWifi.get(i).getTitle())
							.snippet(ListWifi.get(i).getSnippet())
							.visible(visibility)
							.anchor(0.5f, 0.5f)));
		}
	}

	public void drawAlerts(Boolean visibility) {

		if (ListMarkersAlerts != null) {
			for (Marker marker : ListMarkersAlerts) marker.remove();
		}

		if (ListMarkersAlertsIds != null) {
			ListMarkersAlertsIds.clear();
		}

		if (alertMap != null) {
			alertMap.clear();
		}

		hideBottomButton(notifyButton);

		for (int i = 0; i < ListAlerts.size(); i++) {
			// Aqui eu não adiciono o MarkerOptions inteiro de uma vez porque dava o erro esquisito do IObectjWrapper. 
			// Criando um MarkerOptions novo e puxando atributo por atributo da ListWifi não deu erro, então deve ficar assim.

			MarkerOptions mOptions = new MarkerOptions()
					.snippet(ListAlerts.get(i).Endereco + newline + getString(R.string.details) + " " + ListAlerts.get(i).Descricao + newline + getString(R.string.alerta_em) + " " + ListAlerts.get(i).timestamp)
					.position(new LatLng(ListAlerts.get(i).Lat, ListAlerts.get(i).Lng))
					.visible(visibility)
					.anchor(0.5f, 1.0f);

			if (ListAlerts.get(i).Tipo.equals("bu")) {
				mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert_pothole));
				mOptions.title(getString(R.string.via_esburacada));
			} else if (ListAlerts.get(i).Tipo.equals("si")) {
				mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert_signalling));
				mOptions.title(getString(R.string.problema_sinalizacao));
			} else {
				mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert));
				mOptions.title(getString(R.string.alerta));
			}

			Marker marker = googleMap.addMarker(mOptions);
			alertMap.put(marker.getId(), ListAlerts.get(i).timestamp);
			ListMarkersAlerts.add(marker);
			ListMarkersAlertsIds.add(marker.getId());
		}
	}

	public void drawPermanentes(Boolean visibility) {

		if (!ListCiclovias.isEmpty()) {

            drawAcessos(visibility);

			if (!cicloviasLineList.isEmpty()) {
				for (Polyline polyline : cicloviasLineList) polyline.remove();
				cicloviasLineList.clear();
			}

			//if (!cicloviasOptionsList.isEmpty()) { cicloviasOptionsList.clear(); }

			for (int i = 0; i < ListCiclovias.size(); i++) {

				ArrayList<LatLng> LatLngList = ListCiclovias.get(i).latLngList;
				PolylineOptions polylineOpt = new PolylineOptions();
				for (int y = 0; y < LatLngList.size(); y++) {
					polylineOpt.add(LatLngList.get(y));
				}
				if (ListCiclovias.get(i).tipo == 0) {
					polylineOpt.color(this.getResources().getColor(R.color.ciclovia));
				} else {
					polylineOpt.color(this.getResources().getColor(R.color.ciclofaixa));
				}

				//cicloviasOptionsList.add(polylineOpt);
				//cicloviasLineList.add(googleMap.addPolyline(cicloviasOptionsList.get(i).visible(visibility).zIndex(10).width(5.0f)));
				cicloviasLineList.add(googleMap.addPolyline(polylineOpt.visible(visibility).zIndex(10).width(3.0f)));
			}
		}
	}

	public void drawTemporarias(Boolean visibility) {

		if (!ListCiclofaixas.isEmpty()) {

			if (ciclofaixasLineList != null) {
				for (Polyline polyline : ciclofaixasLineList) polyline.remove();
				ciclofaixasLineList.clear();
			}

			for (int i = 0; i < ListCiclofaixas.size(); i++) {

				ArrayList<LatLng> LatLngList = ListCiclofaixas.get(i).latLngList;
				PolylineOptions polylineOpt = new PolylineOptions();
				for (int y = 0; y < LatLngList.size(); y++) {
					polylineOpt.add(LatLngList.get(y));
				}
				polylineOpt.color(this.getResources().getColor(R.color.ciclofaixaLazer));

				ciclofaixasLineList.add(googleMap.addPolyline(polylineOpt.visible(visibility).zIndex(5).width(3.0f)));

				//ciclofaixasOptionsList.add(polylineOpt);
				//ciclofaixasLineList.add(googleMap.addPolyline(ciclofaixasOptionsList.get(i).visible(visibility).zIndex(5).width(5.0f)));
			}
		}
	}

	public void drawPreferenciais(Boolean visibility) {

		if (!ciclorrotasOptionsList.isEmpty()) {

			if (ciclorrotasLineList != null) {
				for (Polyline polyline : ciclorrotasLineList) polyline.remove();
			}

			for (int i = 0; i < ciclorrotasOptionsList.size(); i++) {
				ciclorrotasOptionsList.get(i).color(this.getResources().getColor(R.color.ciclorrota));
				ciclorrotasLineList.add(googleMap.addPolyline(ciclorrotasOptionsList.get(i).visible(visibility).zIndex(1).width(3.0f)));
			}
		}
	}

    public void drawAcessos(Boolean visibility) {

        if (ListMarkersAcessos != null) {
            for (Marker marker : ListMarkersAcessos) marker.remove();
        }

        for (int i = 0; i < ListAcesso.size(); i++) {
            // Aqui eu não adiciono o MarkerOptions inteiro de uma vez porque dava o erro esquisito do IObectjWrapper.
            // Criando um MarkerOptions novo e puxando atributo por atributo da ListWifi não deu erro, então deve ficar assim.
            ListMarkersAcessos.add(googleMap.addMarker(ListAcesso.get(i).visible(visibility)));
        }
    }

	public void refreshData(final MenuItem item) {

		// Trigger CarregarDB
		//new CarregarDB().execute();
		getDataFromDB();
	}

	public void setUpdating() {
		if (menu_item != null) {
			menu_item.setActionView(R.layout.pb_actionbar);
		}
	}

	public void resetUpdating() {
		// Get our refresh item from the menu
		if (menu_item != null) {
			//MenuItem menu_item = mymenu.findItem(R.id.action_refresh);
			if (menu_item.getActionView() != null) {
				menu_item.setActionView(null);
			}
		}
	}

    /* END SETTING UP INFO FROM DB */
    /* ROUTING */

	public void onRouteButtonClick(View v) {

		boolean isChecked = btRouteMode.isChecked();

		if (isChecked) {
			if (!betaRouteWarningWasShown) {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
				alertBuilder.setMessage(getString(R.string.beta_route_mode_warning))
						.setTitle(getString(R.string.beta_route_mode_title))
						.setPositiveButton(getString(R.string.entendi), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								turnOnRouteMode();
							}
						})
						.show();
				betaRouteWarningWasShown = true;
                SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("betaRouteWarningWasShown", true);
				editor.apply();
			} else {
				turnOnRouteMode();
			}
		} else {
			turnOffRouteMode();
		}
	}

	public void turnOnRouteMode() {

		isRouteModeOn = true;
        hideAllBottomButtons();

		if (markerSearch != null) {
			String destination_string = etSearch.getText().toString();
			LatLng destination_latlng = markerSearch.getPosition();

			// Change searchHeader for routeHeader
			header.removeAllViews();
			header.addView(routeHeader);

			etDestination.setVisibility(View.GONE);
			tvDestination.setVisibility(View.VISIBLE);
			tvDestination.setText(destination_string);

			markerSearch.remove();
			markerSearch = null;

			if (markerDestination != null) {
				markerDestination.remove();
			}

			markerDestination = googleMap.addMarker(new MarkerOptions()
					.position(destination_latlng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_chegada))
					.anchor(0.0f, 1.0f)
					.title(getString(R.string.chegada)));

			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
			final AlertDialog alert = alertBuilder.create();
			View alertView = getLayoutInflater().inflate(R.layout.ad_from_which_location, null);
			alert.setView(alertView);
			alert.setCancelable(true);
			Button btFromMyLocation = (Button) alertView.findViewById(R.id.from_my_location);
			Button btFromAnotherLocation = (Button) alertView.findViewById(R.id.from_another_location);

			btFromMyLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (user_latlng != null) {
						// Set markerOrigin
						if (markerOrigin != null) {
							markerOrigin.remove();
						}
						markerOrigin = googleMap.addMarker(new MarkerOptions()
								.position(user_latlng)
								.title(getString(R.string.partida)));
						alert.dismiss();

						tvOrigin.setText(getString(R.string.seu_local));

						getRoutes();

					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.loc_selecione_manualmente), Toast.LENGTH_SHORT).show();
						tvOrigin.setVisibility(View.GONE);
						etOrigin.setVisibility(View.VISIBLE);
						etOrigin.requestFocus();
						alert.dismiss();
					}
				}
			});
			btFromAnotherLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					tvOrigin.setVisibility(View.GONE);
					etOrigin.setVisibility(View.VISIBLE);
					etOrigin.requestFocus();
					alert.dismiss();
				}
			});

			alert.show();

		} else {
			header.removeAllViews();
			header.addView(routeHeader);

			tvOrigin.setVisibility(View.GONE);
			etOrigin.setVisibility(View.VISIBLE);
			tvDestination.setVisibility(View.GONE);
			etDestination.setVisibility(View.VISIBLE);

			if (user_latlng != null) {
				// Set markerOrigin
				if (markerOrigin != null) {
					markerOrigin.remove();
				}
				markerOrigin = googleMap.addMarker(new MarkerOptions()
						.position(user_latlng)
						.title(getString(R.string.partida)));

				etOrigin.setVisibility(View.GONE);
				tvOrigin.setVisibility(View.VISIBLE);
				tvOrigin.setText(getString(R.string.seu_local));

			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.loc_selecione_manualmente), Toast.LENGTH_SHORT).show();
			}

		}

	}

	public void turnOffRouteMode() {

		isRouteModeOn = false;

		if (geocodeOriginASY != null) {
			geocodeOriginASY.cancel(true);
		}
		if (geocodeDestinationASY != null) {
			geocodeDestinationASY.cancel(true);
		}
		if (getRoutesASY != null) {
			getRoutesASY.cancel(true);
		}

		etDestination.setText("");
		etOrigin.setText("");
		tvDestination.setText("");
		tvOrigin.setText("");

		header.removeAllViews();
		header.addView(searchHeaderView);

		etSearch.setText("");
		btClearSearch.setVisibility(View.GONE);

        viewPager.setAdapter(null);

		hideViewPager.start();

		// Hide btRouteMode
		//if (btRouteMode.getTranslationX() == 0) {hideRouteButton.start();}

		// Clean polyline on map
		if (!polylineRoutesList.isEmpty()) {
			for (Polyline polyline : polylineRoutesList) polyline.remove();
		}
		// Remove marker that points elevation for selected route step
		if (graph_point_marker != null) {
			graph_point_marker.remove();
		}
		if (markerOrigin != null) {
			markerOrigin.remove();
		}
		if (markerDestination != null) {
			markerDestination.remove();
		}

		// Hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etOrigin.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(etDestination.getWindowToken(), 0);
	}

	public void editDestination(View view) {
		tvDestination.setVisibility(View.GONE);
		etDestination.setVisibility(View.VISIBLE);
		etDestination.requestFocus();

		if (markerDestination != null) {
			markerDestination.remove();
			markerDestination = null;
		}
	}

	public void editOrigin(View view) {
		tvOrigin.setVisibility(View.GONE);
		etOrigin.setVisibility(View.VISIBLE);
		etOrigin.requestFocus();


		if (markerOrigin != null) {
			markerOrigin.remove();
			markerOrigin = null;
		}
	}

	public void getDirections(View view) {
		if (etOrigin.getVisibility() == View.VISIBLE) {
			geocodeOrigin();
		} else if (etDestination.getVisibility() == View.VISIBLE) {
			geocodeDestination();
		} else {
			getRoutes();
		}
	}

	public void geocodeOrigin() {

		addressList.clear();
		addressListBase.clear();

		// Get the string from the EditText
		final String s_address = etOrigin.getText().toString();

		//Checar primeiro se algo foi digitado.
		if (!s_address.trim().equals("")) {

			geocodeOriginASY = new AsyncTask<String, String, String>() {

				@Override
				protected void onPreExecute() {

					btRoute.setVisibility(View.GONE);
					pBarRoute.setVisibility(View.VISIBLE);

					setUpdating();

					if (!isNetworkAvailable()) {
						cancel(true);
					}
				}

				@Override
				protected String doInBackground(String... params) {

					try {
						addressListBase = geocoder.getFromLocationName(s_address, 5);
					} catch (IOException e) {
						e.printStackTrace();
					}

					return null;
				}

				@Override
				protected void onPostExecute(String result) {

					Double latLL = -23.863142;
					Double lngLL = -46.942720;
					Double latUR = -23.316943;
					Double lngUR = -46.357698;

					pBarRoute.setVisibility(View.GONE);
					btRoute.setVisibility(View.VISIBLE);

					resetUpdating();

					final ArrayList<Address> addressList = new ArrayList<>();

					// Manual bounding box for addresses
					for (int i = 0; i < addressListBase.size(); i++) {
						//Double lat = addressListBase.get(i).getLatitude();
						//Double lng = addressListBase.get(i).getLongitude();
						//if (lat>latLL && lat<latUR && lng>lngLL && lng<lngUR ) {
						addressList.add(addressListBase.get(i));
						//} else {}
					}
					String[] s_addressList = null;
					ArrayList<String> array_address = new ArrayList<>();

					// Checar se o endere�o n�o por acaso foi encontrado. Caso negativo, ent�o lan�ar o AlertDialog no "else".
					if (!addressList.isEmpty()) {

						// Create String[] with addresses
						for (int i = 0; i < addressList.size(); i++) {

							// Check number of AddressLine before using the second
							if (addressList.get(i).getMaxAddressLineIndex() > 0) {
								array_address.add(addressList.get(i).getAddressLine(0) + ", "
										+ addressList.get(i).getAddressLine(1));
							} else {
								array_address.add(addressList.get(i).getAddressLine(0));
							}

							s_addressList = new String[array_address.size()];
							s_addressList = array_address.toArray(s_addressList);
						}

						if (addressList.size() == 1) {
							Address address = addressList.get(0);
							if (address.hasLatitude() && address.hasLongitude()) {
								double lat = address.getLatitude();
								double lng = address.getLongitude();

								if (markerOrigin != null) {
									markerOrigin.remove();
								}
								markerOrigin = googleMap.addMarker(new MarkerOptions()
										.position(new LatLng(lat, lng))
										.title(getString(R.string.partida)));
							}
							// Set the text on etOrigin to be the complete address
							String finalStringAddress = address.getAddressLine(0);
							for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
								finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
							}
							etOrigin.setVisibility(View.GONE);
							tvOrigin.setVisibility(View.VISIBLE);
							tvOrigin.setText(finalStringAddress);
							if (etDestination.getVisibility() == View.VISIBLE && !etDestination.getText().toString().equals("")) {
								geocodeDestination();
							} else if (tvDestination.getVisibility() == View.VISIBLE) {
								getRoutes();
							}

						} else {
							AlertDialog.Builder alert_enderecos = new AlertDialog.Builder(MainActivity.this);
							alert_enderecos.setTitle(getString(R.string.which_origin))
									.setItems(s_addressList, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											Address address = addressList.get(which);
											if (address.hasLatitude() && address.hasLongitude()) {
												double lat = address.getLatitude();
												double lng = address.getLongitude();

												if (markerOrigin != null) {
													markerOrigin.remove();
												}
												markerOrigin = googleMap.addMarker(new MarkerOptions()
														.position(new LatLng(lat, lng))
														.title(getString(R.string.partida)));
											}
											// Set the text on etOrigin to be the complete address
											String finalStringAddress = address.getAddressLine(0);
											for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
												finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
											}
											etOrigin.setVisibility(View.GONE);
											tvOrigin.setVisibility(View.VISIBLE);
											tvOrigin.setText(finalStringAddress);
											if (etDestination.getVisibility() == View.VISIBLE && !etDestination.getText().toString().equals("")) {
												geocodeDestination();
											} else if (tvDestination.getVisibility() == View.VISIBLE) {
												getRoutes();
											}
										}
									});
							alert_enderecos.show();
						}
					} else {
						AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
						alert.setTitle(getString(R.string.end_origem_nao_encontrado_titulo))
								.setMessage(getString(R.string.end_nao_encontrado_mensagem))
								.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
									}
								});
						alert.show();
					}
				}

				protected void onCancelled() {

					pBarRoute.setVisibility(View.GONE);
					btRoute.setVisibility(View.VISIBLE);

					resetUpdating();

					AlertDialog.Builder network_alert = new AlertDialog.Builder(MainActivity.this);
					network_alert.setTitle(getString(R.string.network_alert_title))
							.setMessage(getString(R.string.network_alert_dialog))
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							})
							.setNegativeButton(getString(R.string.network_settings), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
								}
							});
					network_alert.show();
				}
			};

			geocodeOriginASY.execute();

		} else {
			AlertDialog.Builder emptyAddressDialog = new AlertDialog.Builder(MainActivity.this);
			emptyAddressDialog.setTitle(getString(R.string.empty_address_dialog_title))
					.setMessage(getString(R.string.empty_address_dialog_message))
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			emptyAddressDialog.show();
		}

	}

	public void geocodeDestination() {

		addressList.clear();
		addressListBase.clear();

		// Get the string from the EditText
		final String s_address = etDestination.getText().toString();

		//Checar primeiro se algo foi digitado.
		if (!s_address.trim().equals("")) {

			geocodeDestinationASY = new AsyncTask<String, String, String>() {

				@Override
				protected void onPreExecute() {

					btRoute.setVisibility(View.GONE);
					pBarRoute.setVisibility(View.VISIBLE);

					setUpdating();

					if (!isNetworkAvailable()) {
						cancel(true);
					}
				}

				@Override
				protected String doInBackground(String... params) {

					try {
						addressListBase = geocoder.getFromLocationName(s_address, 5);
					} catch (IOException e) {
						e.printStackTrace();
					}

					return null;
				}

				@Override
				protected void onPostExecute(String result) {

					Double latLL = -23.863142;
					Double lngLL = -46.942720;
					Double latUR = -23.316943;
					Double lngUR = -46.357698;

					pBarRoute.setVisibility(View.GONE);
					btRoute.setVisibility(View.VISIBLE);

					resetUpdating();

					final ArrayList<Address> addressList = new ArrayList<>();

					// Manual bounding box for addresses
					for (int i = 0; i < addressListBase.size(); i++) {
						//Double lat = addressListBase.get(i).getLatitude();
						//Double lng = addressListBase.get(i).getLongitude();
						//if (lat>latLL && lat<latUR && lng>lngLL && lng<lngUR ) {
						addressList.add(addressListBase.get(i));
						//} else {}
					}
					String[] s_addressList = null;
					ArrayList<String> array_address = new ArrayList<>();

					// Checar se o endere�o n�o por acaso foi encontrado. Caso negativo, ent�o lan�ar o AlertDialog no "else".
					if (!addressList.isEmpty()) {

						// Create String[] with addresses
						for (int i = 0; i < addressList.size(); i++) {

							// Check number of AddressLine before using the second
							if (addressList.get(i).getMaxAddressLineIndex() > 0) {
								array_address.add(addressList.get(i).getAddressLine(0) + ", "
										+ addressList.get(i).getAddressLine(1));
							} else {
								array_address.add(addressList.get(i).getAddressLine(0));
							}

							s_addressList = new String[array_address.size()];
							s_addressList = array_address.toArray(s_addressList);
						}

						if (addressList.size() == 1) {
							Address address = addressList.get(0);
							if (address.hasLatitude() && address.hasLongitude()) {
								double lat = address.getLatitude();
								double lng = address.getLongitude();

								if (markerDestination != null) {
									markerDestination.remove();
								}
								markerDestination = googleMap.addMarker(new MarkerOptions()
										.position(new LatLng(lat, lng))
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_chegada))
										.anchor(0.0f, 1.0f)
										.title(getString(R.string.chegada)));
							}
							String finalStringAddress = address.getAddressLine(0);
							for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
								finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
							}
							etDestination.setVisibility(View.GONE);
							tvDestination.setVisibility(View.VISIBLE);
							tvDestination.setText(finalStringAddress);
							getRoutes();

						} else {
							AlertDialog.Builder alert_enderecos = new AlertDialog.Builder(MainActivity.this);
							alert_enderecos.setTitle(getString(R.string.which_destination))
									.setItems(s_addressList, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											Address address = addressList.get(which);
											if (address.hasLatitude() && address.hasLongitude()) {
												double lat = address.getLatitude();
												double lng = address.getLongitude();

												if (markerSearch != null) {
													markerSearch.remove();
													markerSearch = null;
												}
												if (markerDestination != null) {
													markerDestination.remove();
												}
												markerDestination = googleMap.addMarker(new MarkerOptions()
														.position(new LatLng(lat, lng))
														.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_chegada))
														.anchor(0.0f, 1.0f)
														.title(getString(R.string.chegada)));
											}
											String finalStringAddress = address.getAddressLine(0);
											for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
												finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
											}
											etDestination.setVisibility(View.GONE);
											tvDestination.setVisibility(View.VISIBLE);
											tvDestination.setText(finalStringAddress);
											getRoutes();
										}
									});
							alert_enderecos.show();
						}

						// Hide keyboard
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(etOrigin.getWindowToken(), 0);
						imm.hideSoftInputFromWindow(etDestination.getWindowToken(), 0);


					} else {
						AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
						alert.setTitle(getString(R.string.end_destino_nao_encontrado_titulo))
								.setMessage(getString(R.string.end_nao_encontrado_mensagem))
								.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
									}
								});
						alert.show();
					}
				}

				protected void onCancelled() {

					pBarRoute.setVisibility(View.GONE);
					btRoute.setVisibility(View.VISIBLE);

					resetUpdating();

					AlertDialog.Builder network_alert = new AlertDialog.Builder(MainActivity.this);
					network_alert.setTitle(getString(R.string.network_alert_title))
							.setMessage(getString(R.string.network_alert_dialog))
							.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							})
							.setNegativeButton(getString(R.string.network_settings), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
								}
							});
					network_alert.show();
				}
			};

			geocodeDestinationASY.execute();

		} else {
			AlertDialog.Builder emptyAddressDialog = new AlertDialog.Builder(MainActivity.this);
			emptyAddressDialog.setTitle(getString(R.string.empty_address_dialog_title))
					.setMessage(getString(R.string.empty_address_dialog_message))
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
			emptyAddressDialog.show();
		}
	}

	public void getRoutes() {

		final LatLng origin = markerOrigin.getPosition();
		final LatLng destination = markerDestination.getPosition();

		// find LatLngBound to animate camera and show the entire route.

		Double oLat = origin.latitude;
		Double oLng = origin.longitude;
		Double dLat = destination.latitude;
		Double dLng = destination.longitude;

		LatLng oLatLng = new LatLng(oLat, oLng);
		LatLng dLatLng = new LatLng(dLat, dLng);
		LatLng oLatdLng = new LatLng(oLat, dLng);
		LatLng dLatoLng = new LatLng(dLat, oLng);

		LatLngBounds bounds = null;

		if (oLat < dLat && oLng < dLng) {
			bounds = new LatLngBounds(oLatLng, dLatLng);
		} else if (oLat < dLat && dLng < oLng) {
			bounds = new LatLngBounds(oLatdLng, dLatoLng);
		} else if (dLat < oLat && oLng < dLng) {
			bounds = new LatLngBounds(dLatoLng, oLatdLng);
		} else if (dLat < oLat && dLng < oLng) {
			bounds = new LatLngBounds(dLatLng, oLatLng);
		}

		CameraUpdate cUpd = CameraUpdateFactory.newLatLngBounds(bounds, 200);
		googleMap.animateCamera(cUpd);

		getRoutesASY = new AsyncTask<String, Integer, ArrayList<CyclingPath>>() {

			@Override
			protected void onPreExecute() {

				if (polylineRoutesList != null) {
					for (Polyline polyline : polylineRoutesList) polyline.remove();
					polylineRoutesList.clear();
				}

				adLoadingRoute.show();

				pbLoadingRoute.setProgress(10);

				setUpdating();

				if (graph_point_marker != null) {
					graph_point_marker.remove();
				}

				if (viewPager.getVisibility() == View.VISIBLE) {
					hideViewPager.start();
				}

				if (!isNetworkAvailable()) {
					cancel(true);
					AlertDialog.Builder network_alert = new AlertDialog.Builder(MainActivity.this);
					network_alert.setTitle(getString(R.string.network_alert_title))
							.setMessage(getString(R.string.route_error_check_connection))
							.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
							});
					network_alert.show();
				}

			}

			@Override
			protected ArrayList<CyclingPath> doInBackground(String... params) {

				ArrayList<CyclingPath> returnList = new ArrayList<>();

				Directions md = new Directions();

				Document doc = md.getDocument(origin, destination, Directions.MODE_DRIVING);

				ArrayList<PolylineOptions> pOptList = md.getDirection(doc);

				ArrayList<Integer> distancesArray = md.getDistanceValues(doc);

				// Clean cyclingPathList before adding new elements
				if (!cyclingPathList.isEmpty()) {
					cyclingPathList.clear();
				}

				publishProgress(25);
				//runOnUiThread(new Runnable() {public void run() { pbLoadingRoute.setProgress(25); }});

				// Check if any routes were found
				if (!pOptList.isEmpty()) {

					for (int i = 0; i < pOptList.size(); i++) {

						if (isCancelled()) {
							break;
						}

						final int numberOfRoutes = pOptList.size();

						PolylineOptions pOpt = pOptList.get(i);

						// Criando lista de LatLng pro objeto CyclingPath
						ArrayList<LatLng> latLngList = new ArrayList<>();
						for (int y = 0; y < pOpt.getPoints().size(); y++) {

							if (isCancelled()) {
								break;
							}

							latLngList.add(pOpt.getPoints().get(y));
						}

						// Get distances array from Directions, in case the MapQuest API doesn't work.
						Double distanceKms = null;
						if (!distancesArray.isEmpty()) {
							distanceKms = (double) distancesArray.get(i) / 1000;
						}

						CyclingPath cp = getCompleteCyclingPath(pOpt);

						if (cp != null) {
							returnList.add(cp);
						} else if (distanceKms != null) {
							CyclingPath cpSimple = new CyclingPath(latLngList, distanceKms);
							returnList.add(cpSimple);
						} else {
							CyclingPath cpSimple = new CyclingPath(latLngList);
							returnList.add(cpSimple);
						}

						publishProgress(25 + (75 / numberOfRoutes) * (i + 1));

						//runOnUiThread(new Runnable() {public void run() { pbLoadingRoute.setProgress(25+(75/numberOfRoutes)*(iValue+1)); }});

					}
				} else {
					// Cancel task if zero routes were found
					cancel(true);
				}

				return returnList;
			}

			protected void onProgressUpdate(Integer... progress) {
				pbLoadingRoute.setProgress(progress[0]);
			}

			@Override
			protected void onPostExecute(ArrayList<CyclingPath> list) {

				pbLoadingRoute.setProgress(100);

				adLoadingRoute.dismiss();

				resetUpdating();

				cyclingPathList = list;

				// Reorder cyclingPathList so the min inclination is the first object
				Collections.sort(cyclingPathList, new CustomComparator());

				// Define the first "selectedCyclingPath"
				if (!selectedCyclingPath.isEmpty()) {
					selectedCyclingPath.clear();
				}
				selectedCyclingPath.add(cyclingPathList.get(0));

                String deviceID = sharedPreferences.getString(Constant.deviceID, "");
                if (!deviceID.equals("")){
                    Calls.sendOriginDestination(deviceID, markerOrigin.getPosition(), markerDestination.getPosition(),
                            selectedCyclingPath.get(0).totalDistance, selectedCyclingPath.get(0).maxInclination, null);
                }

				drawRoutes();

			}

			protected void onCancelled() {

				adLoadingRoute.dismiss();

				resetUpdating();
			}

		};

		getRoutesASY.execute();
	}

	public void cancelLoadingRoute(View v) {

		if (geocodeOriginASY != null) {
			geocodeOriginASY.cancel(true);
		}
		if (geocodeDestinationASY != null) {
			geocodeDestinationASY.cancel(true);
		}
		if (getRoutesASY != null) {
			getRoutesASY.cancel(true);
		}

		resetUpdating();

		//pBarRoute.setVisibility(View.GONE);
		//btRoute.setVisibility(View.VISIBLE);

		adLoadingRoute.dismiss();
	}

	// Inicializado por: getRoutes(). Solicita elevações e distâncias do mapQuest e retorna CyclingPath
	public CyclingPath getCompleteCyclingPath(PolylineOptions pOpt) {

		// Variáveis que serão populadas para comporem o CyclingPath final
		ArrayList<LatLng> pathLatLng = (ArrayList<LatLng>) pOpt.getPoints();
		ArrayList<Double> elevationList = new ArrayList<>();
		ArrayList<Double> referenceDistanceList = new ArrayList<>();

		// CyclingPath que será retornado no final
		CyclingPath cp = null;

		String base_url = "http://open.mapquestapi.com/elevation/v1/profile?key=Fmjtd%7Cluu821utnd%2Cr0%3Do5-94b5gr&shapeFormat=raw&useFilter=true&latLngCollection=";

		DecimalFormat dc = new DecimalFormat("##.#####", new DecimalFormatSymbols(Locale.US));

		ArrayList<String> urls = new ArrayList<>();

		int limit = 195; // Limite de pontos LatLng pra procurar por URL (220 * 9 * 2 = 3960) Url máxima = 4096
		int y = (pathLatLng.size() / limit);

		for (int i = 0; i < y; i++) {
			String pathUrl = String.valueOf(pathLatLng.get(i * limit).latitude) + "," + String.valueOf(pathLatLng.get(i * limit).longitude);
			for (int x = ((i * limit) + 1); x < (i + 1) * limit; x++) {
				pathUrl = pathUrl + "," + dc.format(pathLatLng.get(x).latitude) + "," + dc.format(pathLatLng.get(x).longitude);
			}
			urls.add(base_url + pathUrl);

		}

		if (pathLatLng.size() > (y * limit)) {
			String pathUrlFinal = String.valueOf(pathLatLng.get(y * limit).latitude) + "," + String.valueOf(pathLatLng.get(y * limit).longitude);
			for (int x = ((y * limit) + 1); x < pathLatLng.size(); x++) {
				pathUrlFinal = pathUrlFinal + "," + dc.format(pathLatLng.get(x).latitude) + "," + dc.format(pathLatLng.get(x).longitude);
			}
			urls.add(base_url + pathUrlFinal);
		}

		// Making requests for MAPQUEST

		// Variável que começa zerada e guarda a referenceDistance da última distância de cada trecho pra que seja somado às distâncias no próximo trecho.
		Double startingDistance = 0.0;

		//JSONParser jParser = new JSONParser();

		for (int i = 0; i < urls.size(); i++) {

			JSONArray elevationProfileJson = null;

			String jsonString = "";

			//HttpClient client = new DefaultHttpClient();
			//HttpPost post = new HttpPost(urls.get(i));

			try {

				JSONObject jsonObject = jParser.makeHttpRequest(urls.get(i));
				elevationProfileJson = jsonObject.getJSONArray("elevationProfile");

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (elevationProfileJson != null) {
				try {
					for (int x = 0; x < elevationProfileJson.length(); x++) {

						JSONObject obj = elevationProfileJson.getJSONObject(x);
						double height = Double.parseDouble(obj.getString("height"));
						double distance = startingDistance + Double.parseDouble(obj.getString("distance"));

						if (x == elevationProfileJson.length() - 1) {
							startingDistance = distance;
						}

						elevationList.add(height);
						referenceDistanceList.add(distance);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		if (!pathLatLng.isEmpty() && !elevationList.isEmpty() && !referenceDistanceList.isEmpty()) {
			cp = new CyclingPath(pathLatLng, elevationList, referenceDistanceList);

		}
		return cp;

	}

	// Reorders cyclingPathList by maxInclination
	public class CustomComparator implements Comparator<CyclingPath> {
		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		public int compare(CyclingPath cp1, CyclingPath cp2) {
			return Double.compare(cp1.maxInclination, cp2.maxInclination);
		}
	}

	public void drawRoutes() {

		// Create PolylineOptions list to be populated
		ArrayList<PolylineOptions> routesOptionList = new ArrayList<>();

		// Clear lists that will be populated, if they are not empty.
		if (!polylineRoutesList.isEmpty()) {
			for (Polyline polyline : polylineRoutesList) polyline.remove();
		}
		if (!polylineRoutesList.isEmpty()) {
			polylineRoutesList.clear();
		}

		// aChartEngine: Clear list of graphs
		if (!graphViewArray.isEmpty()) {
			graphViewArray.clear();
		}
        Log.e("CYCLINGPATHSIZE", String.valueOf(cyclingPathList.size()));
		// For each other cycling path stored on cyclingPathList
		for (int i = 0; i < cyclingPathList.size(); i++) {

			// Create PolylineOptions to the current cyclingPath. Add to list.

			PolylineOptions polylineOptions = new PolylineOptions();
			polylineOptions.addAll(cyclingPathList.get(i).pathLatLng);
			polylineOptions.zIndex(9);

			//Change to bright blue if this is the cyclingPath with minimun elevation
			if (i == 0) {
				polylineOptions.color(this.getResources().getColor(R.color.selected_route_blue));
			} else {
				polylineOptions.color(this.getResources().getColor(R.color.not_selected_route_blue));
			}

			// Add it to the PolylineOption list
			routesOptionList.add(polylineOptions);
		}

		// Add routesOptionList to map
		for (PolylineOptions routesOption : routesOptionList) {
			polylineRoutesList.add(googleMap.addPolyline(routesOption));
		}

		// Set up view pager
		viewPager.setVisibility(View.VISIBLE);
		ObjectAnimator objAnim = ObjectAnimator.ofFloat(viewPager, "translationY", 0);
		objAnim.start();

        SwipeFragmentPagerAdapter swipeFragmentPagerAdapter = new SwipeFragmentPagerAdapter(this, getSupportFragmentManager());

		viewPager.setAdapter(swipeFragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {

                Log.e("onPageSelected", "RAN, position: " + String.valueOf(position));
				for (Polyline polyline : polylineRoutesList) {
					polyline.setColor(getResources().getColor(R.color.not_selected_route_blue));
				}
				polylineRoutesList.get(position).setColor(getResources().getColor(R.color.selected_route_blue));
				selectedCyclingPath.set(0, cyclingPathList.get(position));

				// When swiped, remove marker that points elevation for previous selected route step
				if (graph_point_marker != null) {
					graph_point_marker.remove();
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});

		if (viewPagerPosition != null) {
			viewPager.setCurrentItem(viewPagerPosition);
		}

		// Hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etOrigin.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(etDestination.getWindowToken(), 0);

		if (!elevGraphExpWasShown) {
			Intent intent = new Intent(this, RouteDetailSplashScreen.class);
			startActivity(intent);
			elevGraphExpWasShown = true;
            SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("elevGraphExpWasShown", true);
			editor.apply();
		}

	}

	public class SwipeFragmentPagerAdapter extends FragmentPagerAdapter {

        Context context;

		public SwipeFragmentPagerAdapter(Context c, FragmentManager fm) {
			super(fm);
            context = c;
		}

		@Override
		public int getCount() {
			return cyclingPathList.size();
		}

		@Override
		public Fragment getItem(int position) {
			//SwipeFragment fragment = new SwipeFragment();
			return SwipeFragment.newInstance(context, position);
		}

	}

	public void switchAddresses(View view) {
		if (tvDestination.getVisibility() == View.VISIBLE && tvOrigin.getVisibility() == View.VISIBLE) {
			String oString = tvOrigin.getText().toString();
			String dString = tvDestination.getText().toString();
			LatLng oLatLng = markerOrigin.getPosition();
			LatLng dLatLng = markerDestination.getPosition();

			tvOrigin.setText(dString);
			tvDestination.setText(oString);
			markerOrigin.setPosition(dLatLng);
			markerDestination.setPosition(oLatLng);

			getRoutes();
		} else if (etOrigin.getVisibility() == View.VISIBLE && etDestination.getVisibility() == View.VISIBLE) {

			String oString = etOrigin.getText().toString();
			String dString = etDestination.getText().toString();

			etOrigin.setText(dString);
			etDestination.setText(oString);

		} else if (etOrigin.getVisibility() == View.VISIBLE && tvDestination.getVisibility() == View.VISIBLE) {
			String oString = etOrigin.getText().toString();
			String dString = tvDestination.getText().toString();

			etOrigin.setVisibility(View.GONE);
			tvOrigin.setVisibility(View.VISIBLE);
			tvOrigin.setText(dString);

			tvDestination.setVisibility(View.GONE);
			etDestination.setVisibility(View.VISIBLE);
			etDestination.setText(oString);

			LatLng dLatLng = markerDestination.getPosition();

			markerDestination.remove();

			markerOrigin = googleMap.addMarker(new MarkerOptions()
					.position(dLatLng)
					.title(getString(R.string.partida)));

		} else if (tvOrigin.getVisibility() == View.VISIBLE && etDestination.getVisibility() == View.VISIBLE) {

			String oString = tvOrigin.getText().toString();
			String dString = etDestination.getText().toString();

			tvOrigin.setVisibility(View.GONE);
			etOrigin.setVisibility(View.VISIBLE);
			etOrigin.setText(dString);

			etDestination.setVisibility(View.GONE);
			tvDestination.setVisibility(View.VISIBLE);
			tvDestination.setText(oString);

			LatLng oLatLng = markerOrigin.getPosition();

			markerOrigin.remove();

			markerDestination = googleMap.addMarker(new MarkerOptions()
					.position(oLatLng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_chegada))
					.anchor(0.0f, 1.0f)
					.title(getString(R.string.chegada)));

		}
	}

    /* END ROUTING */
    /* MISCELLANEOUS */

    public void showBottomButton (final View viewToAnimate) {
        ObjectAnimator showAnimation = ObjectAnimator.ofFloat(viewToAnimate, "translationY", 0);
        viewToAnimate.setVisibility(View.VISIBLE);
        showAnimation.start();

    }

    public void hideBottomButton(final View viewToAnimate) {
        ObjectAnimator hideAnimation = ObjectAnimator.ofFloat(viewToAnimate, "translationY", 0, 600);
        hideAnimation.addListener(new AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                viewToAnimate.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        hideAnimation.start();
    }

    public void hideAllBottomButtons () {
        hideBottomButton(notifyButton);
        hideBottomButton(btParkedHere);
        hideBottomButton(btRemovePlace);
        //hideBottomButton(llPlaceOptions);
    }

    public void checkNumberOfOptionsDisplayed() {

        int numberOfTrues = 0;

        for (int i = 1; i < Constant.states.length; i++) {
            if (Constant.states[i]) numberOfTrues++;
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

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
	}

	@Override
	public void onBackPressed() {

		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		getActionBar().setDisplayShowTitleEnabled(false);
		mymenu = menu;

		menu_item = mymenu.findItem(R.id.action_refresh);

		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		user_latlng = new LatLng(location.getLatitude(), location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Initialize location manager
                    locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
                    //Get Best Location Provider
                    bestAvailableProvider = locationManager.getBestProvider(criteria, false);
                    if (bestAvailableProvider != null) {
                        if (locationManager.isProviderEnabled(bestAvailableProvider)) {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
								locationManager.requestLocationUpdates(bestAvailableProvider, 0, 0, this);
                                Location user_loc = locationManager.getLastKnownLocation(bestAvailableProvider);
                                user_latlng = new LatLng(user_loc.getLatitude(), user_loc.getLongitude());
                            }
                        }  else {
                            Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
                    }

				} else {

					AlertDialog.Builder ad = new AlertDialog.Builder(this);
					ad.setTitle(getString(R.string.loc_no_location_permission));
					ad.setMessage(getString(R.string.loc_no_loc_permission_exp));
					ad.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
				}
                break;
            case 2:
				if (googleMap != null) {
					googleMap.setMyLocationEnabled(true);
				}
				break;
            case 3:
                if (locationManager != null) {
					locationManager.removeUpdates(this);
				}
				break;
        }
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		super.onResume();

		MyApplication.activityResumed();

		// If permission is not granted, request it
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

		} else {
			//Request location update
            if (locationManager != null) {
                if (locationManager.isProviderEnabled(bestAvailableProvider)) {

                    locationManager.requestLocationUpdates(bestAvailableProvider, 0, 0, this);
                } else {
                    Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
                }
            }
        }
	}

	@Override
	protected void onPause() {
		super.onPause();

		MyApplication.activityPaused();

        Log.e("ONPAUSE", "SHOT");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        } else {
            if (locationManager != null) {
                locationManager.removeUpdates(this);
            }
        }

	}

    public void redrawOnMapAfterSavedInstance () {
        if (Constant.states[0] && Constant.bikeLanesStates[0]) {drawPermanentes(true);}
        if (Constant.states[0] && Constant.bikeLanesStates[1]) {drawTemporarias(true);}
        if (Constant.states[0] && Constant.bikeLanesStates[2]) {drawPreferenciais(true);}
        if (Constant.states[1] && Constant.sharingSystemsStates[0]) {drawBikeSampa(true);}
        if (Constant.states[1] && Constant.sharingSystemsStates[1]) {drawCicloSampa(true);}
        if (Constant.states[2]) {drawBicicletarios(true);}
        if (Constant.states[3]) {drawParques(true);}
        if (Constant.states[4]) {drawWifi(true);}
        if (Constant.states[5]) {drawAlerts(true);}

        if (current_latlng != null) {
            LatLng position = new LatLng(current_latlng[0], current_latlng[1]);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoom);
            googleMap.moveCamera(cameraUpdate);
        }

        if (latLngMarkerOrigin != null) {
            markerOrigin = googleMap.addMarker(new MarkerOptions()
                    .position(latLngMarkerOrigin)
                    .title(getString(R.string.partida)));
        }

        if (latLngMarkerDestination != null) {
            markerDestination = googleMap.addMarker(new MarkerOptions()
                    .position(latLngMarkerDestination)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_chegada))
                    .anchor(0.0f, 1.0f)
                    .title(getString(R.string.chegada)));
        }

        if (isViewPagerVisible) {

            drawRoutes();

            if (stringGraphPointMarkerInfo != null && latLngGraphPointMarker != null) {
                graph_point_marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLngGraphPointMarker)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.graph_blue_ball))
                        .title(stringGraphPointMarkerInfo[0])
                        .snippet(stringGraphPointMarkerInfo[1])
                );
            }
        }
    }

	protected void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {

        savedInstanceStateHasRun = true;

        Constant.states = savedInstanceState.getBooleanArray("STATES");
        Constant.bikeLanesStates = savedInstanceState.getBooleanArray("BIKELANESSTATES");
        Constant.sharingSystemsStates = savedInstanceState.getBooleanArray("SHARINGSYSTEMSSTATES");
		current_latlng = savedInstanceState.getDoubleArray("LATLNG");

        zoom = savedInstanceState.getFloat("ZOOM");
		if (savedInstanceState.getBoolean("isDrawerOpen")) {
			mDrawerLayout.openDrawer(Gravity.LEFT);
            etSearch.clearFocus();
        }

		ListCiclovias = savedInstanceState.getParcelableArrayList("CICLOVIAS_LIST");

		ListCiclofaixas = savedInstanceState.getParcelableArrayList("CICLOFAIXAS_LIST");

		ciclorrotasOptionsList = savedInstanceState.getParcelableArrayList("CICLORROTAS_LIST");

		ListEstacoesITAU = savedInstanceState.getParcelableArrayList("ITAU_LIST");

		ListEstacoesBRA = savedInstanceState.getParcelableArrayList("BRA_LIST");

		ListParques = savedInstanceState.getParcelableArrayList("PARQUES_LIST");

		ListBicicletarios = savedInstanceState.getParcelableArrayList("BICICLETARIOS_LIST");

		ListWifi = savedInstanceState.getParcelableArrayList("WIFI_LIST");

		ListAlerts = savedInstanceState.getParcelableArrayList("REPORTS_LIST");

		isRouteModeOn = savedInstanceState.getBoolean("isRouteModeOn");

		if (isRouteModeOn) {
            btRouteMode.setChecked(true);
			header.removeAllViews();
			header.addView(routeHeaderView);

			if (savedInstanceState.getBoolean("tvOriginVIS")) {
				etOrigin.setVisibility(View.GONE);
				tvOrigin.setVisibility(View.VISIBLE);
				tvOrigin.setText(savedInstanceState.getString("tvOriginSTRING"));

			} else {
				tvOrigin.setVisibility(View.GONE);
				etOrigin.setVisibility(View.VISIBLE);
				etOrigin.setText(savedInstanceState.getString("etOriginSTRING"));}

			if (savedInstanceState.getBoolean("tvDestinationVIS")) {
				etDestination.setVisibility(View.GONE);
				tvDestination.setVisibility(View.VISIBLE);
				tvDestination.setText(savedInstanceState.getString("tvDestinationSTRING"));

			} else {
				tvDestination.setVisibility(View.GONE);
				etDestination.setVisibility(View.VISIBLE);
				etDestination.setText(savedInstanceState.getString("etDestinationSTRING"));}

            if (savedInstanceState.getParcelable("markerOriginLatLng") != null) {
                latLngMarkerOrigin = savedInstanceState.getParcelable("markerOriginLatLng");
            }
            if (savedInstanceState.getParcelable("markerDestinationLatLng") != null) {
                latLngMarkerDestination = savedInstanceState.getParcelable("markerDestinationLatLng");
            }

			if (savedInstanceState.getBoolean("viewPagerVIS")) {

                for (LinearLayout ll : aChartLinearLayoutList) {
                    ll.removeAllViews();
                }
                aChartLinearLayoutList.clear();

				cyclingPathList = savedInstanceState.getParcelableArrayList("cyclingPathList");
				selectedCyclingPath = savedInstanceState.getParcelableArrayList("selectedCyclingPath");
				viewPagerPosition = savedInstanceState.getInt("viewPagerPOSITION");
				isViewPagerVisible = true;
				if (savedInstanceState.getBoolean("graph_point_markerVIS")) {
					stringGraphPointMarkerInfo = savedInstanceState.getStringArray("graph_point_markerINFO");
                    latLngGraphPointMarker = savedInstanceState.getParcelable("graph_point_markerLATLNG");
				}
			}
		}
        etSearch.clearFocus();

	}

	public void onSaveInstanceState (Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		double[] latlng = {googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude};
		savedInstanceState.putDoubleArray("LATLNG", latlng);
		savedInstanceState.putFloat("ZOOM", googleMap.getCameraPosition().zoom);
        savedInstanceState.putParcelableArrayList("CICLOVIAS_LIST", ListCiclovias);
        savedInstanceState.putParcelableArrayList("CICLOFAIXAS_LIST", ListCiclofaixas);
        savedInstanceState.putParcelableArrayList("CICLORROTAS_LIST", ciclorrotasOptionsList);
		savedInstanceState.putParcelableArrayList("ITAU_LIST", ListEstacoesITAU);
		savedInstanceState.putParcelableArrayList("BRA_LIST", ListEstacoesBRA);
		savedInstanceState.putParcelableArrayList("PARQUES_LIST", ListParques);
		savedInstanceState.putParcelableArrayList("BICICLETARIOS_LIST", ListBicicletarios);
		savedInstanceState.putParcelableArrayList("WIFI_LIST", ListWifi);
		savedInstanceState.putParcelableArrayList("REPORTS_LIST", ListAlerts);

		savedInstanceState.putBoolean("isDrawerOpen", mDrawerLayout.isDrawerOpen(Gravity.LEFT));

        savedInstanceState.putBooleanArray("STATES", Constant.states);
        savedInstanceState.putBooleanArray("BIKELANESSTATES", Constant.bikeLanesStates);
        savedInstanceState.putBooleanArray("SHARINGSYSTEMSSTATES", Constant.sharingSystemsStates);

		// ROUTEMODE
		savedInstanceState.putBoolean("isRouteModeOn", isRouteModeOn);

		if (isRouteModeOn) {
			// Get visibilities of header views
			savedInstanceState.putBoolean("pBarRouteVIS", pBarRoute.getVisibility() == View.VISIBLE);
			savedInstanceState.putBoolean("btRouteVIS", btRoute.getVisibility() == View.VISIBLE);

			savedInstanceState.putBoolean("tvOriginVIS", tvOrigin.getVisibility() == View.VISIBLE);
			savedInstanceState.putBoolean("etOriginVIS", etOrigin.getVisibility() == View.VISIBLE);
			savedInstanceState.putBoolean("tvDestinationVIS", tvDestination.getVisibility() == View.VISIBLE);
			savedInstanceState.putBoolean("etDestinationVIS", etDestination.getVisibility() == View.VISIBLE);
			savedInstanceState.putString("tvOriginSTRING", tvOrigin.getText().toString());
			savedInstanceState.putString("etOriginSTRING", etOrigin.getText().toString());
			savedInstanceState.putString("tvDestinationSTRING", tvDestination.getText().toString());
			savedInstanceState.putString("etDestinationSTRING", etDestination.getText().toString());

			// Get cyclingPathList, viewPager and graph_point_marker

			savedInstanceState.putParcelableArrayList("cyclingPathList", cyclingPathList);
			savedInstanceState.putParcelableArrayList("selectedCyclingPath", selectedCyclingPath);

            if (markerOrigin != null) {
                savedInstanceState.putParcelable("markerOriginLatLng", markerOrigin.getPosition());
            }
            if (markerDestination != null) {
                savedInstanceState.putParcelable("markerDestinationLatLng", markerDestination.getPosition());
            }

			// Save ViewPager State
			if (viewPager.getVisibility() == View.VISIBLE) {
				savedInstanceState.putBoolean("viewPagerVIS", true);
				savedInstanceState.putInt("viewPagerPOSITION", viewPager.getCurrentItem());

				// Save graph_point_marker State (marcador que indica qual ponto da rota foi clicado
				if (graph_point_marker != null) {
					savedInstanceState.putBoolean("graph_point_markerVIS", true);
					savedInstanceState.putParcelable("graph_point_markerLATLNG", graph_point_marker.getPosition());
					String[] s = new String[2];
					s[0] = graph_point_marker.getTitle();
					s[1] = graph_point_marker.getSnippet();
					savedInstanceState.putStringArray("graph_point_markerINFO", s);
				} else {
					savedInstanceState.putBoolean("graph_point_markerVIS", false);
				}
			} else {
				savedInstanceState.putBoolean("viewPagerVIS", false);
			}
		}
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } // Else, handle action buttons, if are ther any...
        else {
            switch (item.getItemId()) {
                case R.id.action_refresh:
                    refreshData(item);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwipeFragmentInteraction(Double clickedRefDistance) {

        Log.e("onMainActivity", "HERE");

        if (graph_point_marker != null) {
            graph_point_marker.remove();
        }

        graph_point_marker = googleMap.addMarker(new MarkerOptions()
                .position(selectedCyclingPath.get(0).getLatLngFromRefDistance(clickedRefDistance))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.graph_blue_ball))
                .title(getString(R.string.inclinacao) + " " + String.valueOf(selectedCyclingPath.get(0).getElevationFromRefDistance(clickedRefDistance)) + "°")
                .snippet(getString(R.string.posicao) + " " + Double.valueOf(String.format(Locale.US, "%.2f", clickedRefDistance)) + " km")
        );

        graph_point_marker.showInfoWindow();

        // display information of the clicked point
        /** "Chart element in series index " + seriesSelection.getSeriesIndex()
         + " data point index " + seriesSelection.getPointIndex() + " was clicked"
         + " closest point value X=" + seriesSelection.getXValue() + ", Y="
         + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();*/
    }

}