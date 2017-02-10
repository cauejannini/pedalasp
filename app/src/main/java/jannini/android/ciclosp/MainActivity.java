package jannini.android.ciclosp;


import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jannini.android.ciclosp.Adapters.InfoWindowActivity;
import jannini.android.ciclosp.Adapters.MyListAdapter;
import jannini.android.ciclosp.Adapters.RoutePrioritySpinnerAdapter;
import jannini.android.ciclosp.Adapters.ToggleServicesListAdapter;
import jannini.android.ciclosp.CustomItemClasses.Bicicletario;
import jannini.android.ciclosp.CustomItemClasses.Ciclovia;
import jannini.android.ciclosp.CustomItemClasses.CyclingPath;
import jannini.android.ciclosp.CustomItemClasses.Estacao;
import jannini.android.ciclosp.CustomItemClasses.Parque;
import jannini.android.ciclosp.CustomItemClasses.Place;
import jannini.android.ciclosp.CustomItemClasses.Report;
import jannini.android.ciclosp.MyApplication.TrackerName;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.GeocoderCallHandler;
import jannini.android.ciclosp.NetworkRequests.GetRouteInterface;
import jannini.android.ciclosp.NetworkRequests.NotifySolvedReport;
import jannini.android.ciclosp.NetworkRequests.Route;
import jannini.android.ciclosp.NetworkRequests.Utils;

import static com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition;
import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;
import static jannini.android.ciclosp.Constant.PERMISSION_REQUEST_CODE_CALL_PHONE;
import static jannini.android.ciclosp.Constant.mapCategoriesIcons;
import static jannini.android.ciclosp.R.id.map;


public class MainActivity extends FragmentActivity
		implements
		LocationListener,
		OnMapReadyCallback,
		SensorEventListener {

	// Location Manager and Provider
	private LocationManager locationManager;
	private String bestAvailableProvider;

	// Google Map
	SupportMapFragment mapFragment;
	private GoogleMap googleMap;

	// General Geocoder
	public Geocoder geocoder;

	// Analytics tracker
	Tracker t;

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

	// APP STORED PREFERENCES

	// Boolean to check if this is the first time app is being opened
	Boolean betaRouteWarningWasShown = false;
	Boolean elevGraphExpWasShown = false;

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
	public static ArrayList<Place> ListPlaces = new ArrayList<>();

	//Criando listas dos marcadores de mapa para os itens de cada tabela da DB
	ArrayList<Marker> ListMarkersITAU = new ArrayList<>();
	ArrayList<Marker> ListMarkersBRA = new ArrayList<>();
	ArrayList<Marker> ListMarkersParques = new ArrayList<>();
	ArrayList<Marker> ListMarkersBicicletarios = new ArrayList<>();
	ArrayList<Marker> ListMarkersWifi = new ArrayList<>();
	ArrayList<Marker> ListMarkersAlerts = new ArrayList<>();
	ArrayList<Marker> ListMarkersAcessos = new ArrayList<>();

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

	RelativeLayout rlBottomContainer;

	// ROUTE
	// RouteHeaderView
	View routeHeaderView;

	LinearLayout routeHeader;

	LinearLayout llEditRoute, llEditOrigin, llEditDestination;

	TextView tvOrigin;
	TextView tvDestination;
	Button btSwitchAddresses;

	Boolean isRouteModeOn = false;

	List<Address> addressList = new ArrayList<>();
	List<Address> addressListBase = new ArrayList<>();
	Marker markerDestination, markerOrigin, markerSearch;

	ImageView iv;
	Menu mymenu;
	MenuItem menu_item;

	public SharedPreferences sharedPreferences;

	Criteria criteria = new Criteria();

	LatLng user_updated_latlng = null;

	//LinearLayout llPlaceOptions;
	Button notifyButton, btParkedHere, btRemovePlace;//, btParkedHereSmall, btPlaceFavorite;
	Marker activeMarker;
	ArrayList<Marker> parkedHereMarkerList = new ArrayList<>();
	//ArrayList<Marker> favoritePlacesMarkerList = new ArrayList<>();

	Map<String, String> alertMap = new HashMap<>();

	// ROUTE variables
	public static ArrayList<CyclingPath> cyclingPathList = new ArrayList<>();
	public int routeRequestId = 0;

	// Auxiliar list that only stores the selected cyclingPath
	ArrayList<CyclingPath> selectedCyclingPath = new ArrayList<>();

	// Route panel, details and subViews
	RelativeLayout rlBottomButtons;
	LinearLayout llRoutePanel;
	LinearLayout llRouteDetailFragment;
	RelativeLayout rlRouteDetails;
	TextView tvRouteDistance, tvRouteDuration, tvRoutePercentageOnBikeLanes, tvElevationUnavailable;
	LinearLayout llChart;

	Spinner spinnerRoutePriority;

	ToggleButton btRouteMode;

	//AlertDialog adLoadingRoute;
	ProgressBar pbLoadingRoute;

	// NAVIGATION

	ToggleButton tbSwitchNavigation;
	Button btMyLocation;
	boolean navigationIsOn = false;
	Marker markerNavigation;
	Circle circleAccuracy;
	SensorManager sensorManager;
	float azimuth = 0;

	// Add function
	RelativeLayout rlAddToMap;
	TextView tvAddAlert, tvAddParaciclo, tvAddEstabelecimento;
	ImageView ivAddAlert, ivAddParaciclo, ivAddEstabelecimento;

	public static boolean placesIsLoading = false;
	RelativeLayout rlPlacePanelNV;
	LinearLayout llPlaceDetailsNV;
	RelativeLayout rlPlacePanelV;
	LinearLayout llPlaceDetailsV;

	String placeTelToCall;

	// DEALS

	LinearLayout llDeals;
	TextView tvDeals;
	ObjectAnimator showDeals, hideDeals;
	boolean shouldGetDeals = true;

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

		sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

		betaRouteWarningWasShown = sharedPreferences.getBoolean("betaRouteWarningWasShown", false);
		elevGraphExpWasShown = sharedPreferences.getBoolean("elevGraphExpWasShown", false);

		//Create criteria to decide what is the best location provider. Store this information in "provider
		criteria.setSpeedRequired(false);
		criteria.setAltitudeRequired(false);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// Get States from sharedPreferences
		Constant.States[0] = sharedPreferences.getBoolean("states0", true);
		Constant.States[1] = sharedPreferences.getBoolean("states1", true);
		Constant.States[2] = sharedPreferences.getBoolean("states2", true);
		Constant.States[3] = sharedPreferences.getBoolean("states3", true);
		Constant.States[4] = sharedPreferences.getBoolean("states4", true);
		Constant.States[5] = sharedPreferences.getBoolean("states5", true);
		Constant.States[6] = sharedPreferences.getBoolean("states6", true);

		Constant.BikeLanesStates[0] = sharedPreferences.getBoolean(Constant.SPKEY_BikeLaneStates0, true);
		Constant.BikeLanesStates[1] = sharedPreferences.getBoolean(Constant.SPKEY_BikeLaneStates1, true);
		Constant.BikeLanesStates[2] = sharedPreferences.getBoolean(Constant.SPKEY_BikeLaneStates2, true);

		Constant.SharingSystemsStates[0] = sharedPreferences.getBoolean(Constant.SPKEY_SharingSystemsStates0, true);
		Constant.SharingSystemsStates[1] = sharedPreferences.getBoolean(Constant.SPKEY_SharingSystemsStates1, true);

		Integer categoriesLenght = sharedPreferences.getInt(Constant.SPKEY_NUMBER_OF_STORED_CATEGORIES, 0);
		for (int i = 0; i<categoriesLenght; i++) {
			Integer categoryId = sharedPreferences.getInt(Constant.SPKEY_PLACE_CATEGORIES_IDS+i, 0);
			Boolean categoryState = sharedPreferences.getBoolean(Constant.SPKEY_PLACE_CATEGORIES_STATES+categoryId, true);
			Constant.PlaceCategoriesStates.put(categoryId,categoryState);
		}

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

		// Hide keyboard
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// ROUTE HEADER VARIABLES
		routeHeaderView = getLayoutInflater().inflate(R.layout.route_header, null);

		routeHeader = (LinearLayout) routeHeaderView.findViewById(R.id.route_header);
		routeHeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

		tvDestination = (TextView) routeHeaderView.findViewById(R.id.tv_destination);
		tvOrigin = (TextView) routeHeaderView.findViewById(R.id.tv_origin);
		btSwitchAddresses = (Button) routeHeaderView.findViewById(R.id.switch_addresses);

		llEditRoute = (LinearLayout) findViewById(R.id.ll_edit_route);
		llEditOrigin = (LinearLayout) findViewById(R.id.ll_edit_origin);
		llEditDestination = (LinearLayout) findViewById(R.id.ll_edit_destination);

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

		// DRAWER VARIABLES
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set up the drawer's list view with items and click listener.
		String userGreeting = getString(R.string.hello)+ " "+ Constant.USER_NAME+"!";
		myAdapter = new MyListAdapter(this, getResources().getStringArray(R.array.menu_array), getResources().getStringArray(R.array.menu_array_descriptions), userGreeting);
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
				/*getActionBar().setTitle(getTitle());
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			*/}

			public void onDrawerOpened(View drawerView) {
				/*getActionBar().setTitle(getTitle());
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			*/}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// BOTTOM PANEL CONTAINER
		rlBottomContainer = (RelativeLayout) findViewById(R.id.rl_bottom_panels_container);

		// ROUTE PANEL
		rlBottomButtons = (RelativeLayout) findViewById(R.id.rl_bottom_options);

		llRoutePanel = (LinearLayout) findViewById(R.id.ll_route_panel);
		spinnerRoutePriority = (Spinner) findViewById(R.id.spinner_route_priority);
		spinnerRoutePriority.setAdapter(new RoutePrioritySpinnerAdapter(this));
		spinnerRoutePriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

				SharedPreferences.Editor edit = sharedPreferences.edit();

				switch (position) {
					case 0:
						edit.putString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_MOST_BIKE_LANES);
						for (CyclingPath cp : cyclingPathList) {
							if (cp.mostBikeLanes) selectCyclingPath(cp, false);
						}
						break;
					case 1:
						edit.putString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_FASTEST);
						for (CyclingPath cp : cyclingPathList) {
							if (cp.fastest) selectCyclingPath(cp, false);
						}
						break;
					case 2:
						for (CyclingPath cp : cyclingPathList) {
							if (cp.flattest) selectCyclingPath(cp, false);
						}
						edit.putString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_FLATTEST);
						break;
				}
				edit.apply();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		ImageView ivSpinnerArrow = (ImageView) findViewById(R.id.iv_spinner_arrow);
		ivSpinnerArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				spinnerRoutePriority.performClick();
				Log.e("ivSpinnerArrow", "CLICKED");
			}
		});

		// ROUTE DETAILS
		rlRouteDetails = (RelativeLayout) findViewById(R.id.rl_route_details);
		llRouteDetailFragment = (LinearLayout) findViewById(R.id.ll_route_details);
		tvRouteDistance = (TextView) findViewById(R.id.tv_route_distance);
		tvRouteDuration = (TextView) findViewById(R.id.tv_route_duration);
		tvRoutePercentageOnBikeLanes = (TextView) findViewById(R.id.tv_route_percentage_on_lanes);
		llChart = (LinearLayout) findViewById(R.id.aChart);
		tvElevationUnavailable = (TextView) findViewById(R.id.tv_elevation_unavailable);

		pbLoadingRoute = (ProgressBar) findViewById(R.id.pb_loading_route);

		// Place details panel and details
		rlPlacePanelNV = (RelativeLayout) findViewById(R.id.rl_place_panel_nv);
		llPlaceDetailsNV = (LinearLayout) findViewById(R.id.ll_place_details_nv);
		rlPlacePanelV = (RelativeLayout) findViewById(R.id.rl_place_panel_v);
		llPlaceDetailsV = (LinearLayout) findViewById(R.id.ll_place_details_v);

		btMyLocation = (Button) findViewById(R.id.bt_my_location);
		tbSwitchNavigation = (ToggleButton) findViewById(R.id.tb_navigation_switch);
		tbSwitchNavigation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				setNavigationOn(b);
			}
		});

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		rlAddToMap = (RelativeLayout) findViewById(R.id.rl_add_to_map);
		tvAddAlert = (TextView) findViewById(R.id.tv_add_alert);
		tvAddParaciclo = (TextView) findViewById(R.id.tv_add_paraciclo);
		tvAddEstabelecimento = (TextView) findViewById(R.id.tv_add_estabelecimento);
		ivAddAlert = (ImageView) findViewById(R.id.iv_add_alert);
		ivAddParaciclo = (ImageView) findViewById(R.id.iv_add_paraciclo);
		ivAddEstabelecimento = (ImageView) findViewById(R.id.iv_add_estabelecimento);

		llDeals = (LinearLayout) findViewById(R.id.ll_deals);
		showDeals = ObjectAnimator.ofFloat(llDeals, "TranslationX", 0);
		showDeals.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				llDeals.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animator) {

			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});

		hideDeals = ObjectAnimator.ofFloat(llDeals, "TranslationX", Utils.getPixelValue(this, -300));
		hideDeals.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				llDeals.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
		tvDeals = (TextView) findViewById(R.id.tv_deals);

		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);

		// INITIALIZE MAP
		try {
			mapFragment.getMapAsync(this);
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

	private void selectItem(int position) 	{

		int n = mDrawerList.getFirstVisiblePosition();

		switch (position) {
			// Bike Lanes
			case Constant.LISTPOS_BIKE_LANE:
				if (!Constant.States[0]) {

					mDrawerList.getChildAt(Constant.LISTPOS_BIKE_LANE - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					Constant.States[0] = true;
					displayBikeLanes();

				} else {
					mDrawerList.getChildAt(Constant.LISTPOS_BIKE_LANE - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
					Constant.States[0] = false;

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

				sharedPreferences.edit().putBoolean("states0", Constant.States[0]).apply();

				break;

			// Places
			case Constant.LISTPOS_PLACES:
				if (!Constant.States[1]) {

					mDrawerList.getChildAt(Constant.LISTPOS_PLACES - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					Constant.States[1] = true;

					if (!ListPlaces.isEmpty() && !Constant.mapPlacesImages.isEmpty()) {

						displayPlaces();

					} else {
						// Get places icons and then places
						setListItemLoading(Constant.LISTPOS_PLACES, true);
						Calls.getPlacesIconsAndCategories(this, getPlacesIconsCallHandler);
					}

				} else {
					mDrawerList.getChildAt(Constant.LISTPOS_PLACES - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
					Constant.States[1] = false;

					hideBottomPanel();

					// Set Estabelecimentos Markers not visible
					for (Place place : ListPlaces) {
						place.setVisible(false);
					}
				}

				sharedPreferences.edit().putBoolean("states1", Constant.States[1]).apply();

				break;

			// Sharing Systems
			case Constant.LISTPOS_SHARING_STATIONS:
				if (!Constant.States[2]) {

					mDrawerList.getChildAt(Constant.LISTPOS_SHARING_STATIONS - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					Constant.States[2] = true;
					displaySharingSystems();

				} else {
					mDrawerList.getChildAt(Constant.LISTPOS_SHARING_STATIONS - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
					Constant.States[2] = false;

					// Set BikeSampa not visible
					for (int i = 0; i < ListMarkersITAU.size(); i++) {
						ListMarkersITAU.get(i).setVisible(false);
					}

					// Set CicloSampa not visible
					for (int i = 0; i < ListMarkersBRA.size(); i++) {
						ListMarkersBRA.get(i).setVisible(false);
					}
				}

				sharedPreferences.edit().putBoolean("states2", Constant.States[2]).apply();

				break;

			// Parking
			case Constant.LISTPOS_PARKING:

				if (!Constant.States[3]) {

					mDrawerList.getChildAt(Constant.LISTPOS_PARKING - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					Constant.States[3] = true;

                    if (!ListMarkersBicicletarios.isEmpty()) {
                        for (int i = 0; i < ListMarkersBicicletarios.size(); i++) {
                            ListMarkersBicicletarios.get(i).setVisible(true);
                        }
                    } else {
                        drawBicicletarios(true);
                    }

                } else {

                    Constant.States[3] = false;
                    mDrawerList.getChildAt(Constant.LISTPOS_PARKING - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    for (int i = 0; i < ListMarkersBicicletarios.size(); i++) {
                        ListMarkersBicicletarios.get(i).setVisible(false);
                    }

                    hideBottomButton(btParkedHere);
                }

                sharedPreferences.edit().putBoolean("states3", Constant.States[3]).apply();

                break;

            // Parks
			case Constant.LISTPOS_PARKS:

				if (!Constant.States[4]) {

					Constant.States[4] = true;
					mDrawerList.getChildAt(Constant.LISTPOS_PARKS - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);

                    if (!ListMarkersParques.isEmpty()) {

                        for (int i = 0; i < ListMarkersParques.size(); i++) {
                            ListMarkersParques.get(i).setVisible(true);
                        }

                    } else {
                        drawParques(true);
                    }

                } else {

                    Constant.States[4] = false;
                    mDrawerList.getChildAt(Constant.LISTPOS_PARKS - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    for (int i = 0; i < ListMarkersParques.size(); i++) {
                        ListMarkersParques.get(i).setVisible(false);
                    }
                }

                sharedPreferences.edit().putBoolean("states4", Constant.States[4]).apply();

                break;

			// Wifi
			case Constant.LISTPOS_WIFI:

				if (!Constant.States[5]) {

					Constant.States[5] = true;
					mDrawerList.getChildAt(Constant.LISTPOS_WIFI - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);

                    if (!ListMarkersWifi.isEmpty()) {

                        for (int i = 0; i < ListMarkersWifi.size(); i++) {
                            ListMarkersWifi.get(i).setVisible(true);
                        }

                    } else {
                        drawWifi(true);
                    }
                } else {

                    Constant.States[5] = false;
                    mDrawerList.getChildAt(Constant.LISTPOS_WIFI - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
                    for (int i = 0; i < ListMarkersWifi.size(); i++) {
                        ListMarkersWifi.get(i).setVisible(false);
                    }
                }

                sharedPreferences.edit().putBoolean("states5", Constant.States[5]).apply();

                break;

            // Alerts
			case Constant.LISTPOS_ALERTS:

				if (!Constant.States[6]) {

					Constant.States[6] = true;
					mDrawerList.getChildAt(Constant.LISTPOS_ALERTS - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);

                    if (!ListMarkersAlerts.isEmpty()) {
                        for (int i = 0; i < ListMarkersAlerts.size(); i++) {
                            ListMarkersAlerts.get(i).setVisible(true);
                        }
                    } else {
                        drawAlerts(true);
                    }
                } else {
                    Constant.States[6] = false;
                    mDrawerList.getChildAt(Constant.LISTPOS_ALERTS - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);

                    for (int i = 0; i < ListMarkersAlerts.size(); i++) {
                        ListMarkersAlerts.get(i).setVisible(false);
                    }
                    hideBottomButton(notifyButton);
                }

                sharedPreferences.edit().putBoolean("states6", Constant.States[6]).apply();

                break;
			case Constant.LISTPOS_MY_ACCOUNT:
				startActivity(new Intent(MainActivity.this, UserAccount.class));
				break;

			case Constant.LISTPOS_WRITE_FOR_US:
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
		tbPermanentes.setChecked(Constant.BikeLanesStates[0]);
		tbPermanentes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Constant.BikeLanesStates[0] = isChecked;
			}
		});
		ToggleButton tbLazer = (ToggleButton) alertView.findViewById(R.id.tb_bikelanes_lazer);
		tbLazer.setChecked(Constant.BikeLanesStates[1]);
		tbLazer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Constant.BikeLanesStates[1] = isChecked;
			}
		});
		ToggleButton tbPreferenciais = (ToggleButton) alertView.findViewById(R.id.tb_bikelanes_preferenciais);
		tbPreferenciais.setChecked(Constant.BikeLanesStates[2]);
		tbPreferenciais.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Constant.BikeLanesStates[2] = isChecked;
			}
		});
		Button btOk = (Button) alertView.findViewById(R.id.bt_bikelanes_ok);
		btOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();

				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean(Constant.SPKEY_BikeLaneStates0, Constant.BikeLanesStates[0]);
				editor.putBoolean(Constant.SPKEY_BikeLaneStates1, Constant.BikeLanesStates[1]);
				editor.putBoolean(Constant.SPKEY_BikeLaneStates2, Constant.BikeLanesStates[2]);
				editor.apply();

				if (Constant.States[0]) {
					displayBikeLanes();
				}
			}
		});

		alert.show();
	}

	public void displayBikeLanes() {

		if (Constant.BikeLanesStates[0]) {

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

		if (Constant.BikeLanesStates[1]) {
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


		if (Constant.BikeLanesStates[2]) {
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
		tbPermanentes.setChecked(Constant.SharingSystemsStates[0]);
		tbPermanentes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Constant.SharingSystemsStates[0] = isChecked;
			}
		});
		ToggleButton tbLazer = (ToggleButton) alertView.findViewById(R.id.tb_sharingsystems_cs);
		tbLazer.setChecked(Constant.SharingSystemsStates[1]);
		tbLazer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Constant.SharingSystemsStates[1] = isChecked;
			}
		});
		Button btOk = (Button) alertView.findViewById(R.id.bt_sharingsystems_ok);
		btOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();

				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean(Constant.SPKEY_SharingSystemsStates0, Constant.SharingSystemsStates[0]);
				editor.putBoolean(Constant.SPKEY_SharingSystemsStates1, Constant.SharingSystemsStates[1]);
				editor.apply();

				if (Constant.States[2]) {
					displaySharingSystems();
				}
			}
		});

		alert.show();
	}

	public void displaySharingSystems() {

		if (Constant.SharingSystemsStates[0]) {
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

		if (Constant.SharingSystemsStates[1]) {
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

	public void selectPlacesCategories() {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
		final AlertDialog alert = alertBuilder.create();
		View alertView = getLayoutInflater().inflate(R.layout.ad_select_places, null);
		alert.setView(alertView);
		alert.setCancelable(false);
		final ListView listToggleServices = (ListView) alertView.findViewById(R.id.list_toggle_services);

		final ArrayList<Integer> categoriesIdsArray = new ArrayList<>(Constant.mapPlaceCategories.keySet());
		Collections.sort(categoriesIdsArray);
		ArrayList<String> categoriesNames = new ArrayList<>();
		for (int i = 0; i < categoriesIdsArray.size(); i++) {
			categoriesNames.add(Constant.mapPlaceCategories.get(categoriesIdsArray.get(i)));
		}
		String[] categoriesArray = new String[categoriesNames.size()];
		categoriesArray = categoriesNames.toArray(categoriesArray);

		final ToggleServicesListAdapter adapter = new ToggleServicesListAdapter(this, categoriesArray, categoriesIdsArray);
		listToggleServices.setAdapter(adapter);
		listToggleServices.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

				Constant.PlaceCategoriesStates.put(categoriesIdsArray.get(position), !Constant.PlaceCategoriesStates.get(categoriesIdsArray.get(position)));
				adapter.notifyDataSetChanged();
			}
		});

		Button btOk = (Button) alertView.findViewById(R.id.bt_select_places_ok);
		btOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();

				SharedPreferences.Editor editor = sharedPreferences.edit();

				Integer[] categoriesMapKeyList = Constant.mapPlaceCategories.keySet().toArray(new Integer[Constant.mapPlaceCategories.keySet().size()]);
				editor.putInt(Constant.SPKEY_NUMBER_OF_STORED_CATEGORIES, categoriesMapKeyList.length);
				for (int i = 0; i < categoriesMapKeyList.length; i++) {
					editor.putInt(Constant.SPKEY_PLACE_CATEGORIES_IDS + i, categoriesMapKeyList[i]);
					editor.putBoolean(Constant.SPKEY_PLACE_CATEGORIES_STATES + categoriesMapKeyList[i], Constant.PlaceCategoriesStates.get(categoriesMapKeyList[i]));
				}
				editor.apply();

				if (Constant.States[1]) {
					hideBottomPanel();
					displayPlaces();
				}
			}
		});

		alert.show();
	}

	public void displayPlaces() {

		for (Place place : ListPlaces) {

			place.setVisible(false);

			if (Constant.States[1]) {

				Integer[] categoriesMapKeyList = Constant.mapPlaceCategories.keySet().toArray(new Integer[Constant.mapPlaceCategories.keySet().size()]);
				for (Integer intKey : categoriesMapKeyList) {
					if (Constant.PlaceCategoriesStates.get(intKey)) {
						if (place.categoryIdList.contains(intKey)) {
							if (googleMap.getCameraPosition().zoom > Constant.ZOOM_FOR_NOT_FEATURED_PLACES || place.isFeatured) {
								if (place.isDrawn) {
									place.setVisible(true);
								} else {
									place.drawOnMap(googleMap);
								}
							}
						}
					}
				}
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
			googleMap.setMyLocationEnabled(false);
			googleMap.setBuildingsEnabled(false);

			// GET LOCATION MANAGER
			locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);

			Location userLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (userLoc != null) {
				markerNavigation = googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(userLoc.getLatitude(), userLoc.getLongitude()))
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_navigation_off))
						.anchor(0.5f, 0.5f)
				);
				markerNavigation.setTag(new String[]{"markerNavigation"});
			}
			//Get Best Location Provider
			bestAvailableProvider = locationManager.getBestProvider(criteria, false);
			if (bestAvailableProvider != null) {
				if (locationManager.isProviderEnabled(bestAvailableProvider)) {
					locationManager.requestLocationUpdates(bestAvailableProvider, 0, 0, this);
				} else {
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

		setUserLocation();

		try {
			if (SplashScreen.placesImagesAndCategoriesAreLoaded) {
				createPlacesArray();
			}
			createBaseArrays();
			createBikeSampaArray();
			createCicloSampaArray();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (isNetworkAvailable()) {
			synchronizePlaces();
			getAllDataFromDB();
		} else {
			Utils.showNetworkAlertDialog(this);
		}


		int parkedHereSize = Integer.valueOf(sharedPreferences.getString(Constant.spParkedHereListSize, "0"));
		parkedHereMarkerList = new ArrayList<>();
		for (int i = 0; i < parkedHereSize; i++) {
			double lat = Double.valueOf(sharedPreferences.getString(Constant.spParkedHereLat + i, ""));
			double lng = Double.valueOf(sharedPreferences.getString(Constant.spParkedHereLng + i, ""));
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

		if (user_updated_latlng != null) {

			double lat = user_updated_latlng.latitude;
			double lng = user_updated_latlng.longitude;

			if (lat > -23.778678 && lat < -23.400375 && lng > -46.773075 && lng < -46.355934) {
				cameraUpdate = newLatLngZoom(user_updated_latlng, 16);
			} else {
				cameraUpdate = newLatLngZoom(latLng_sp, 12);
			}
		} else {
			cameraUpdate = newLatLngZoom(latLng_sp, 12);
		}
		googleMap.moveCamera(cameraUpdate);

	}

	public void setNavigationOn(boolean bool) {
		if (bool) {

			markerNavigation.setRotation(0);
			// Orient map accordingly to sensor (updates in onSensorChanged) and turnNavigationOn at the end of animation
			if (user_updated_latlng != null) {
				CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(user_updated_latlng, 18, 50, azimuth));
				googleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
					@Override
					public void onFinish() {
						navigationIsOn = true;
						markerNavigation.setRotation(0);
					}

					@Override
					public void onCancel() {}
				});
			} else {
				navigationIsOn = true;
			}

			// Start listening to orientation changes (happening in turnOnRouteMode

			// setMyLocationEnabled(false) when routeMode is on and replace myLocationDot for navigation mapicon
		} else {
			navigationIsOn = false;

			// Conform map orientation.
			goToMyLocation(null);

			// Stop listening to orientation changes.

			// setNavigationOn(false) on map move camera
		}
		// Start and stop listening to sensor on onResume and onPause, checking always whether navigationIsOn is true
	}

	public void goToMyLocation(View view) {

		if (user_updated_latlng != null) {

			CameraUpdate cameraUpdate = newCameraPosition(new CameraPosition(user_updated_latlng, 16, 0, 0));
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

							user_updated_latlng = new LatLng(location.getLatitude(), location.getLongitude());
							CameraUpdate cameraUpdate = newLatLngZoom(user_updated_latlng, 16);
							googleMap.animateCamera(cameraUpdate);
						}

						@Override
						public void onStatusChanged(String provider, int status, Bundle extras) {
						}

						@Override
						public void onProviderEnabled(String provider) {
						}

						@Override
						public void onProviderDisabled(String provider) {
						}
					}, null);

				} else {
					Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void setMapEvents() {

		googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
			@Override
			public void onCameraIdle() {

				displayPlaces();
			}
		});

		googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
			@Override
			public void onCameraMoveStarted(int i) {
				if (i == REASON_GESTURE) {
					navigationIsOn = false;
					tbSwitchNavigation.setChecked(false);
				}
			}
		});

		googleMap.setOnMapClickListener(new OnMapClickListener() {

			public void onMapClick(LatLng point) {

			hideAllBottomButtons();

			for (Marker marker : listMarker) {
				marker.remove();
			}
			listMarker.clear();

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

			if (!cyclingPathList.isEmpty()) {
				for (int i = 0; i < cyclingPathList.size(); i++) {
					ArrayList<LatLng> list = cyclingPathList.get(i).pathLatLng;
					LatLng closestPoint = checking.checkClick(point, list, maxDistance);
					if (closestPoint != null) {
						selectCyclingPath(cyclingPathList.get(i), true);
						return;
					}
				}
			}

			hideBottomPanel();

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
							return;
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
								marker.setSnippet(getString(R.string.open_now) + newline
										+ ListCiclofaixas.get(i).Info + newline + newline
										+ getString(R.string.distancia_total) + " " + ListCiclofaixas.get(i).Dist + " km");
							} else {
								marker.setSnippet(getString(R.string.closed_now) + newline
										+ ListCiclofaixas.get(i).Info + newline + newline
										+ getString(R.string.distancia_total) + " " + ListCiclofaixas.get(i).Dist + " km");
							}
							listMarker.add(marker);
							listMarker.get(0).showInfoWindow();

							// Center map in clicked point
							CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(closestPoint);
							googleMap.animateCamera(cameraUpdate);
							return;
						}
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

				if (marker.getTitle() != null) {
					if (marker.getTitle().equals(getString(R.string.your_bike_is_here))
						//|| marker.getTitle().equals(getString(R.string.saved_place))
							) {
						showBottomButton(btRemovePlace);
					} else {
						hideBottomButton(btRemovePlace);
						//hideBottomButton(llPlaceOptions);
					}
				}

				if (marker.getTag() != null) {
					String[] tag = (String[]) marker.getTag();
					if (tag[0].equals("place")) {

						markerSearch = googleMap.addMarker(new MarkerOptions()
						.position(marker.getPosition()));

						handlePlaceClick(Integer.valueOf(tag[1]));
					} else if (tag[0].equals("markerNavigation")) {
						hideBottomPanel();
					}
				} else {
					marker.showInfoWindow();
					hideBottomPanel();
				}

				// Funcionalidades padrões para quando se clica em qualquer marcador
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 200, null);
				return true;
			}
		});

		googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {

			hideBottomPanel();

			if (activeMarker != null) activeMarker.hideInfoWindow();

			// Remove markerSearch, if there's one, and add again.
			if (markerSearch != null) {
				markerSearch.remove();
				markerSearch = null;
			}

			activeMarker = markerSearch = googleMap.addMarker(new MarkerOptions()
					.position(latLng)
					.title(getString(R.string.marcador_inserido)));

			final ArrayList<ObjectAnimator> hideAnimationsList = hideAllBottomButtons();

			// Geocode LatLng to Address
			final LatLng ll = latLng;

			new AsyncTask<String, Void, String>() {
				protected void onPreExecute() {
					btClearSearch.setVisibility(View.GONE);
					pBarSearch.setVisibility(View.VISIBLE);
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
					resetUpdating();

					btClearSearch.setVisibility(View.VISIBLE);

					if (sAddress.equals("")) {
						etSearch.setText(getString(R.string.marcador_inserido));

					} else {
						etSearch.setText(sAddress);
						if (markerSearch != null) {
							markerSearch.setTitle(sAddress);
						}
						activeMarker = markerSearch;
					}

					hideAnimationsList.get(1).cancel();
					showBottomButton(btParkedHere);
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

	public void showBottomPanel(String type) {

		// Reset expanded layout changes
		llPlaceDetailsV.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
		llPlaceDetailsV.requestLayout();

		int yValueToAnimateContainer = 0;
		int yValueToAnimateButtons = 0;

		rlBottomContainer.bringToFront();

		switch (type) {
			case "PLACE_FEATURED":
				yValueToAnimateButtons = -llPlaceDetailsV.getHeight();
				rlPlacePanelV.setVisibility(View.VISIBLE);
				rlPlacePanelNV.setVisibility(View.INVISIBLE);
				llRoutePanel.setVisibility(View.INVISIBLE);
				btRouteMode.animate().translationX(Utils.getPixelValue(this,200)).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
				break;
			case "PLACE_NOT_FEATURED":
				yValueToAnimateContainer = rlBottomContainer.getHeight() - rlPlacePanelNV.getHeight();
				yValueToAnimateButtons = -llRoutePanel.getHeight();
				rlPlacePanelV.setVisibility(View.INVISIBLE);
				rlPlacePanelNV.setVisibility(View.VISIBLE);
				llRoutePanel.setVisibility(View.INVISIBLE);
				btRouteMode.animate().translationX(0).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
				break;
			case "ROUTE":
				yValueToAnimateContainer = rlBottomContainer.getHeight() - llRoutePanel.getHeight();
				yValueToAnimateButtons = -llRoutePanel.getHeight();
				rlPlacePanelV.setVisibility(View.INVISIBLE);
				rlPlacePanelNV.setVisibility(View.INVISIBLE);
				llRoutePanel.setVisibility(View.VISIBLE);
				btRouteMode.animate().translationX(0).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
				break;
		}

		rlBottomButtons.animate().translationY(yValueToAnimateButtons).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
		rlBottomContainer.animate().translationY(yValueToAnimateContainer).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);

		/*ValueAnimator anim = ValueAnimator.ofInt(mapFragment.getView().getHeight(), mapFragment.getView().getHeight()-rlBottomContainer.getHeight()+distanceToAnimate);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int val = (Integer) valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = mapFragment.getView().getLayoutParams();
				layoutParams.height = val;
				mapFragment.getView().setLayoutParams(layoutParams);
			}
		});
		anim.setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
		anim.start();*/
	}

	public void hideBottomPanel() {

		btRouteMode.animate().translationX(0).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
		rlBottomButtons.animate().translationY(0).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
		rlBottomContainer.animate().translationY(rlBottomContainer.getHeight()).setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION).withEndAction(new Runnable() {
			@Override
			public void run() {
				rlPlacePanelNV.setVisibility(View.INVISIBLE);
				rlPlacePanelV.setVisibility(View.INVISIBLE);
				llRoutePanel.setVisibility(View.INVISIBLE);
				llRouteDetailFragment.setVisibility(View.INVISIBLE);
			}
		});

		/*
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		int screenHeight = size.y;

		ValueAnimator anim = ValueAnimator.ofInt(mapFragment.getView().getHeight(), screenHeight);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int val = (Integer) valueAnimator.getAnimatedValue();
				ViewGroup.LayoutParams layoutParams = mapFragment.getView().getLayoutParams();
				layoutParams.height = val;
				mapFragment.getView().setLayoutParams(layoutParams);
			}
		});
		anim.setDuration(Constant.DURATION_BOTTOM_PANEL_ANIMATION);
		anim.start();*/
	}

	public void findAddress(View view) {

		// Hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

		addressList.clear();
		addressListBase.clear();

		// Get the string from the EditText
		final String s_address = etSearch.getText().toString();
		if (!s_address.trim().equals("")) {
			if (!isNetworkAvailable()) {
				Utils.showNetworkAlertDialog(this);
			} else {

				// Limpar marcadores antigos de outras buscas, antes de criar um novo.
				if (markerSearch != null) {
					markerSearch.remove();
					markerSearch = null;
				}

				btClearSearch.setVisibility(View.GONE);
				pBarSearch.setVisibility(View.VISIBLE);

				Calls.getAddressFromString(this, s_address, new GeocoderCallHandler() {
					@Override
					public void onSuccess(Address address) {
						super.onSuccess(address);

						LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

						CameraUpdate cu = newLatLngZoom(latLng, 15f);
						googleMap.animateCamera(cu);
						activeMarker = markerSearch = googleMap.addMarker(new MarkerOptions()
								.position(latLng)
								.title(address.getAddressLine(0)));

						// Set the text on etSearch to be the complete address
						String finalStringAddress = address.getAddressLine(0);
						for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
							finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
						}
						etSearch.setText(finalStringAddress);
						btClearSearch.setVisibility(View.VISIBLE);
						pBarSearch.setVisibility(View.GONE);
						showBottomButton(btParkedHere);
					}

					@Override
					public void onFailure(String reason) {
						super.onFailure(reason);

						pBarSearch.setVisibility(View.GONE);
					}

					@Override
					public void onDismissedAlertView() {
						super.onDismissedAlertView();

						pBarSearch.setVisibility(View.GONE);
					}
				});
			}
		} else {
			etSearch.setError(getString(R.string.mandatory_field));
		}
	}

	public void clearSearch(View view) {
		etSearch.setText("");
		if (markerSearch != null) {
			markerSearch.remove();
			markerSearch = null;
		}
		btClearSearch.setVisibility(View.GONE);
		hideAllBottomButtons();

	}

    /* END LOAD MAP AND BASIC LOCATION FUNCIONALITY */
	/* ADD SOMETHING */

	public void addFunction(View view) {

		LinearLayout llAddAlert = (LinearLayout) findViewById(R.id.ll_add_alert);
		LinearLayout llAddParaciclo = (LinearLayout) findViewById(R.id.ll_add_paraciclo);
		LinearLayout llAddEstabelecimento = (LinearLayout) findViewById(R.id.ll_add_estabelecimento);

		final ObjectAnimator alphaIn = ObjectAnimator.ofFloat(rlAddToMap,"alpha", 0, 1);

		ObjectAnimator opacityTV1 = ObjectAnimator.ofFloat(tvAddEstabelecimento, "alpha", 0, 1);
		ObjectAnimator scaleIn1X = ObjectAnimator.ofFloat(ivAddEstabelecimento, "scaleX", 0, 1);
		ObjectAnimator scaleIn1Y = ObjectAnimator.ofFloat(ivAddEstabelecimento, "scaleY", 0, 1);
		AnimatorSet scaleIn1 = new AnimatorSet();
		scaleIn1.playTogether(scaleIn1X, scaleIn1Y, opacityTV1);

		ObjectAnimator opacityTV2 = ObjectAnimator.ofFloat(tvAddParaciclo, "alpha", 0, 1);
		ObjectAnimator scaleIn2X = ObjectAnimator.ofFloat(ivAddParaciclo, "scaleX", 0, 1);
		ObjectAnimator scaleIn2Y = ObjectAnimator.ofFloat(ivAddParaciclo, "scaleY", 0, 1);
		AnimatorSet scaleIn2 = new AnimatorSet();
		scaleIn2.setStartDelay(60);
		scaleIn2.playTogether(scaleIn2X, scaleIn2Y, opacityTV2);

		ObjectAnimator opacityTV3 = ObjectAnimator.ofFloat(tvAddAlert, "alpha", 0, 1);
		ObjectAnimator scaleIn3X = ObjectAnimator.ofFloat(ivAddAlert, "scaleX", 0, 1);
		ObjectAnimator scaleIn3Y = ObjectAnimator.ofFloat(ivAddAlert, "scaleY", 0, 1);
		AnimatorSet scaleIn3 = new AnimatorSet();
		scaleIn3.setStartDelay(120);
		scaleIn3.playTogether(scaleIn3X, scaleIn3Y, opacityTV3);

		AnimatorSet scaleIn = new AnimatorSet();
		scaleIn.playTogether(scaleIn1, scaleIn2, scaleIn3);
		scaleIn.start();

		rlAddToMap.setVisibility(View.VISIBLE);
		alphaIn.start();

		rlAddToMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				hideAddLayout();
			}
		});

		llAddAlert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, ReportActivity.class);
				if (user_updated_latlng != null) {
					i.putExtra("latitude", user_updated_latlng.latitude);
					i.putExtra("longitude", user_updated_latlng.longitude);
				}
				startActivity(i);
			}
		});

		llAddParaciclo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, AddToMapActivity.class);
				if (user_updated_latlng != null) {
					i.putExtra("lat", user_updated_latlng.latitude);
					i.putExtra("lng", user_updated_latlng.longitude);
					i.putExtra("SELECTED_FUNCTION", "PARACICLO");
				}
				startActivity(i);
			}
		});

		llAddEstabelecimento.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent i = new Intent(MainActivity.this, AddToMapActivity.class);
				if (user_updated_latlng != null) {
					i.putExtra("lat", user_updated_latlng.latitude);
					i.putExtra("lng", user_updated_latlng.longitude);
					i.putExtra("SELECTED_FUNCTION", "PLACE");
				}
				startActivity(i);

			}
		});
	}

	void hideAddLayout() {
		ObjectAnimator alphaOut = ObjectAnimator.ofFloat(rlAddToMap,"alpha", 1, 0);
		alphaOut.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				rlAddToMap.setVisibility(View.GONE);
				ivAddAlert.setScaleX(0);
				ivAddAlert.setScaleY(0);
				ivAddParaciclo.setScaleX(0);
				ivAddParaciclo.setScaleY(0);
				ivAddEstabelecimento.setScaleX(0);
				ivAddEstabelecimento.setScaleY(0);
				tvAddEstabelecimento.setAlpha(0);
				tvAddParaciclo.setAlpha(0);
				tvAddAlert.setAlpha(0);
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
		alphaOut.start();
	}

    /* REPORT MANAGING */

	public void notifySolved(View view) {

		boolean b = false;

		if (isNetworkAvailable()) {
			for (int i = 0; i < ListMarkersAlerts.size(); i++) {

				if (ListMarkersAlerts.get(i).isInfoWindowShown()) {
					String snippet = ListMarkersAlerts.get(i).getSnippet();
					String timestamp = snippet.substring(snippet.length()-19, snippet.length());
					Log.e("timestamp", timestamp);

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

			Utils.showNetworkAlertDialog(this);
		}

	}

	public void setParkedHere(View view) {

		String deviceID = sharedPreferences.getString(Constant.SPKEY_DEVICE_ID, "");
		if (activeMarker != null) {
			parkedHereMarkerList.add(googleMap.addMarker(new MarkerOptions()
					.position(activeMarker.getPosition())
					.title(getString(R.string.your_bike_is_here))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_parked_here))
					.zIndex(110)
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
		if (activeMarker != null) {
			activeMarker.remove();
		}
		// Remove marker from list
		for (int i = 0; i < parkedHereMarkerList.size(); i++) {
			if (parkedHereMarkerList.get(i).getId().equals(activeMarker.getId())) {
				parkedHereMarkerList.remove(i);
				parkedHereMarkerList.trimToSize();
			}
		}

		// Update all spParkedHere info on SharedPreferences
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(Constant.spParkedHereListSize, String.valueOf(parkedHereMarkerList.size()));
		for (int i = 0; i < parkedHereMarkerList.size(); i++) {
			editor.putString(Constant.spParkedHereLat + i, String.valueOf(parkedHereMarkerList.get(i).getPosition().latitude))
					.putString(Constant.spParkedHereLng + i, String.valueOf(parkedHereMarkerList.get(i).getPosition().longitude));
		}
		editor.apply();

	}

    /* END REPORT MANAGING */
    /* SETTING UP INFO FROM DB */

	public void getAllDataFromDB() {

		setUpdating();

		// Update other stuff just after places are being loaded (priority to places because other stuff is probably stored offline)
		Calls.jsonRequest(Constant.url_obter_dados, new CallHandler() {
			@Override
			public void onSuccess(int responseCode, String response) {
				Log.e("getDataHandler", responseCode + ": " + response);

				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(Constant.spJobGeral, response);
				editor.apply();

				try {
					createBaseArrays();
				} catch (JSONException 	e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int responseCode, String response) {
				super.onFailure(responseCode, response);
				Log.e("getDataHandler Failure", responseCode + ": " + response);
			}
		});

		Calls.jsonRequest(Constant.url_obter_bikesampa, new CallHandler() {
			@Override
			public void onSuccess(int code, String response) {
				Log.e("getBSHandler", code + ": " + response);

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

			@Override
			public void onFailure(int responseCode, String response) {
				super.onFailure(responseCode, response);
				Log.e("getBSHandler Failure", responseCode + ": " + response);
			}
		});

		Calls.jsonRequest(Constant.url_obter_ciclosampa, new CallHandler() {
			@Override
			public void onSuccess(int code, String response) {
				Log.e("getCicloSampaHandler", code + ": " + response);

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
				resetUpdating();
			}

			@Override
			public void onFailure(int responseCode, String response) {
				super.onFailure(responseCode, response);
				Log.e("getCSHandler Failure", responseCode + ": " + response);
				resetUpdating();
			}
		});

	}

	public void synchronizePlaces() {
		// Get places icons, then places and, just then, other data
		setListItemLoading(Constant.LISTPOS_PLACES, true);
		placesIsLoading = true;
		Calls.getPlacesIconsAndCategories(this, new CallHandler() {
			@Override
			public void onSuccess(int responseCode, String response) {
				Log.e("getIcons", response);

				// Get Places
				Calls.jsonRequest(Constant.url_get_places, new CallHandler() {
					@Override
					public void onSuccess(int responseCode, String response) {

						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString(Constant.spJobPlaces, response);
						editor.apply();

						setListItemLoading(Constant.LISTPOS_PLACES, false);
						placesIsLoading = false;

						try {
							createPlacesArray();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int responseCode, String response) {
						Log.e("getPlaces fail", response);
						Toast.makeText(MainActivity.this, getString(R.string.error_loading_places), Toast.LENGTH_LONG).show();
						resetUpdating();
						setListItemLoading(Constant.LISTPOS_PLACES, false);
					}
				});
			}
		});
	}

	public void setListItemLoading(int position, boolean visible) {

		placesIsLoading = visible;

		if (mDrawerList != null) {
			int n = mDrawerList.getFirstVisiblePosition();

			if (mDrawerList.getChildCount() > 0) {
				if (visible) {
					mDrawerList.getChildAt(position-n).findViewById(R.id.pb_loading_listitem).setVisibility(View.VISIBLE);
				} else {
					mDrawerList.getChildAt(position-n).findViewById(R.id.pb_loading_listitem).setVisibility(View.GONE);
				}
			}
		}
	}

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

			drawPermanentes((Constant.States[0] && Constant.BikeLanesStates[0]));

			drawTemporarias((Constant.States[0] && Constant.BikeLanesStates[1]));

			drawPreferenciais((Constant.States[0] && Constant.BikeLanesStates[2]));

			if (Constant.States[3]) drawBicicletarios(true);

			if (Constant.States[4]) drawParques(true);

			if (Constant.States[5]) drawWifi(true);

			if (Constant.States[6]) drawAlerts(true);

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

					drawBikeSampa((Constant.States[2] && Constant.SharingSystemsStates[0]));
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

					drawCicloSampa((Constant.States[2] && Constant.SharingSystemsStates[1]));

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public void createPlacesArray() throws JSONException {

		for (Place place : ListPlaces) {
			place.removeFromMap();
		}
		ListPlaces.clear();

		String stringPlacesJson = sharedPreferences.getString(Constant.spJobPlaces, null);

		if (stringPlacesJson != null) {
			try {

				JSONArray jarrayPlaces = new JSONArray(stringPlacesJson);
				for (int i = 0; i < jarrayPlaces.length(); i++) {
					JSONObject jobPlace = jarrayPlaces.getJSONObject(i);
					int placeId = jobPlace.getInt("id");
					String name = jobPlace.getString("name");
					String address = jobPlace.getString("address");
					Double lat = jobPlace.getDouble("lat");
					Double lng = jobPlace.getDouble("lng");
					String phone = jobPlace.getString("phone");
					String site = jobPlace.getString("site");
					String publicEmail = jobPlace.getString("public_email");
					String currentOpenStatus = jobPlace.getString("current_open_status");
					String shortDesc = jobPlace.getString("short_desc");
					int iconId = jobPlace.getInt("icon_id");
					int logoId = jobPlace.getInt("logo_id");
					String displayServices = jobPlace.getString("display_services");

					LatLng latlng = new LatLng(lat, lng);
					boolean isVerified = (jobPlace.getInt("verified") == 1);
					boolean isFeatured = (jobPlace.getInt("featured") == 1);
					boolean hasDeals = (jobPlace.getInt("has_deals") == 1);

					String categories = jobPlace.getString("categories");
					String[] categoriesStringArray = categories.split(",");
					ArrayList<Integer> categoriesIntArray = new ArrayList<>();
					for (String categoryId : categoriesStringArray){
						categoriesIntArray.add(Integer.valueOf(categoryId));
					}

					Place place = new Place(this, placeId, name, latlng, address, phone, site, publicEmail, currentOpenStatus, shortDesc, displayServices, categoriesIntArray, isVerified, isFeatured, hasDeals, iconId, logoId);
					ListPlaces.add(place);
				}

				displayPlaces();

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

	public void drawBicicletarios(final Boolean visibility) {

		if (ListMarkersBicicletarios != null) {
			for (Marker marker : ListMarkersBicicletarios) marker.remove();
		}

		for (int i = 0; i < ListBicicletarios.size(); i++) {
			LatLng ll = ListBicicletarios.get(i).getLatLng();

			MarkerOptions markerOpt = new MarkerOptions()
					.position(ll)
					.title(ListBicicletarios.get(i).Nome)
					.visible(visibility);

			if (ListBicicletarios.get(i).Tipo.equals("b")) {

				markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_parking_b));
				markerOpt.anchor(0.5f, 1.0f);
				if (ListBicicletarios.get(i).hasEmprestimo()) {
					markerOpt.snippet(getString(R.string.vagas) + " " + ListBicicletarios.get(i).Vagas
							+ newline + newline + getString(R.string.possui_emprestimo_bicicleta)
							+ newline + newline + getString(R.string.funcionamento) + " " + ListBicicletarios.get(i).Horario);
				} else {
					markerOpt.snippet(getString(R.string.vagas) + " " + ListBicicletarios.get(i).Vagas
							+ newline + newline + getString(R.string.possui_emprestimo_bicicleta_nao)
							+ newline + newline + getString(R.string.funcionamento) + " " + ListBicicletarios.get(i).Horario);
				}
			} else {

				markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_parking_p));
				markerOpt.anchor(0.5f, 0.5f);
				markerOpt.snippet(getString(R.string.numero_de_paraciclos) + " " + (ListBicicletarios.get(i).Vagas / 2)
						+ newline + getString(R.string.endereco_aproximado) + ": " + ListBicicletarios.get(i).address);
			}

			ListMarkersBicicletarios.add(googleMap.addMarker(markerOpt));
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

	public void drawAlerts(final Boolean visibility) {

		if (ListMarkersAlerts != null) {
			for (Marker marker : ListMarkersAlerts) marker.remove();
		}

		hideBottomButton(notifyButton);

		for (int i = 0; i < ListAlerts.size(); i++) {

			MarkerOptions markerOpt = new MarkerOptions()
					.snippet(ListAlerts.get(i).Endereco + newline + getString(R.string.details) + " " + ListAlerts.get(i).Descricao + newline + getString(R.string.alerta_em) + " " + ListAlerts.get(i).timestamp)
					.position(new LatLng(ListAlerts.get(i).Lat, ListAlerts.get(i).Lng))
					.visible(visibility)
					.anchor(0.5f, 1.0f);

			if (ListAlerts.get(i).Tipo.equals("bu")) {
				markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert_pothole));
				markerOpt.title(getString(R.string.via_esburacada));
			} else if (ListAlerts.get(i).Tipo.equals("si")) {
				markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert_signalling));
				markerOpt.title(getString(R.string.problema_sinalizacao));
			} else {
				markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_alert));
				markerOpt.title(getString(R.string.alerta));
			}

			ListMarkersAlerts.add(googleMap.addMarker(markerOpt));
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
				cicloviasLineList.add(googleMap.addPolyline(polylineOpt.visible(visibility).zIndex(10).width(Utils.getPixelValue(this, Constant.bikeLaneWidth))));
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

				ciclofaixasLineList.add(googleMap.addPolyline(polylineOpt.visible(visibility).zIndex(5).width(Utils.getPixelValue(this, Constant.bikeLaneWidth))));

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
				ciclorrotasLineList.add(googleMap.addPolyline(ciclorrotasOptionsList.get(i).visible(visibility).zIndex(1).width(Utils.getPixelValue(this, Constant.bikeLaneWidth))));
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
		getAllDataFromDB();
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
				turnOnRouteMode();
		} else {
			turnOffRouteMode();
		}
	}

	public void turnOnRouteMode() {

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
		}

		btRouteMode.setChecked(true);
		isRouteModeOn = true;
		hideAllBottomButtons();

		btMyLocation.setVisibility(View.GONE);
		tbSwitchNavigation.setVisibility(View.VISIBLE);
		// Register listener for the Rotation Vector sensor
		if (sensorManager != null) {
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
		}
		// Change searchHeader for routeHeader
		header.removeAllViews();
		header.addView(routeHeader);

		if (markerSearch != null) {

			// Set destination to "inserted marker" only if etSearch is empty
			String destination_string = getString(R.string.marcador_inserido);
			if (!etSearch.getText().toString().trim().equals("")) {
				destination_string = etSearch.getText().toString();
			}

			LatLng destination_latlng = markerSearch.getPosition();

			tvDestination.setText(destination_string);

			markerSearch.remove();
			markerSearch = null;

			if (markerDestination != null) {
				markerDestination.remove();
			}

			markerDestination = googleMap.addMarker(new MarkerOptions()
					.position(destination_latlng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route_destination))
					.anchor(0.5f, 0.5f)
					.title(getString(R.string.chegada)));

			if (user_updated_latlng != null) {
				// Set markerOrigin
				if (markerOrigin != null) {
					markerOrigin.remove();
				}
				markerOrigin = googleMap.addMarker(new MarkerOptions()
						.position(user_updated_latlng)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route_origin))
						.anchor(0.5f, 0.5f)
						.title(getString(R.string.partida)));

				tvOrigin.setText(getString(R.string.seu_local));

				getRoutes(markerOrigin.getPosition(), markerDestination.getPosition());

			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.loc_selecione_manualmente), Toast.LENGTH_SHORT).show();

				editOrigin(null);
			}
		} else {

			if (user_updated_latlng != null) {
				// Set markerOrigin
				if (markerOrigin != null) {
					markerOrigin.remove();
				}
				markerOrigin = googleMap.addMarker(new MarkerOptions()
						.position(user_updated_latlng)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route_origin))
						.anchor(0.5f, 0.5f)
						.title(getString(R.string.partida)));

				tvOrigin.setText(getString(R.string.seu_local));

				editDestination(null);

			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.loc_selecione_manualmente), Toast.LENGTH_SHORT).show();
				editOrigin(null);
			}
		}
	}

	public void turnOffRouteMode() {

		isRouteModeOn = false;
		navigationIsOn = false;

		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(googleMap.getCameraPosition().target, googleMap.getCameraPosition().zoom, 0, 0)));

		btMyLocation.setVisibility(View.VISIBLE);
		tbSwitchNavigation.setVisibility(View.GONE);
		tbSwitchNavigation.setChecked(false);

		tvDestination.setText("");
		tvOrigin.setText("");

		header.removeAllViews();
		header.addView(searchHeaderView);

		etSearch.setText("");
		btClearSearch.setVisibility(View.GONE);

		hideBottomPanel();

		// Clean polyline on map
		if (!cyclingPathList.isEmpty()) {
			for (CyclingPath cp : cyclingPathList) {
				cp.routePolyline.remove();
				for (Polyline p : cp.intersectionPolylines) {
					p.remove();
				}
			}
		}
		if (markerOrigin != null) {
			markerOrigin.remove();
			markerOrigin = null;
		}
		if (markerDestination != null) {
			markerDestination.remove();
			markerDestination = null;
		}
	}

	public void editOrigin(View view) {

		llEditRoute.setVisibility(View.VISIBLE);
		llEditOrigin.setVisibility(View.VISIBLE);
		PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0, 1);
		ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(llEditRoute, pvhAlpha);
		alpha.start();

		final EditText etOrigin = (EditText) findViewById(R.id.et_origin);
		etOrigin.requestFocus();
		etOrigin.setText(tvOrigin.getText().toString());
		etOrigin.selectAll();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(etOrigin, 0);

		final ProgressBar pbEditOrigin = (ProgressBar) findViewById(R.id.pb_edit_origin);

		etOrigin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etOrigin.getWindowToken(), 0);

				if (isNetworkAvailable()) {

					pbEditOrigin.setVisibility(View.VISIBLE);
					pbEditOrigin.animate();

					Calls.getAddressFromString(MainActivity.this, etOrigin.getText().toString(), new GeocoderCallHandler() {
						@Override
						public void onSuccess (Address address) {

							pbEditOrigin.setVisibility(View.GONE);
							pbEditOrigin.clearAnimation();

							PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0);
							ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(llEditRoute, pvhAlpha);
							alpha.addListener(new AnimatorListener() {
								@Override
								public void onAnimationStart(Animator animator) {}

								@Override
								public void onAnimationEnd(Animator animator) {
									llEditRoute.setVisibility(View.GONE);
									llEditOrigin.setVisibility(View.INVISIBLE);
								}

								@Override
								public void onAnimationCancel(Animator animator) {}

								@Override
								public void onAnimationRepeat(Animator animator) {}
							});
							alpha.start();

							String finalStringAddress = address.getAddressLine(0);
							for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
								finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
							}

							setOriginOrDestination("origin", finalStringAddress, new LatLng(address.getLatitude(), address.getLongitude()));
						}

						@Override
						public void onFailure(String reason) {

							pbEditOrigin.setVisibility(View.GONE);
							pbEditOrigin.clearAnimation();

							AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
							alert.setTitle(getString(R.string.end_origem_nao_encontrado_titulo))
									.setMessage(getString(R.string.end_nao_encontrado_mensagem))
									.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
										}
									});
							alert.show();
						}

						@Override
						public void onDismissedAlertView() {
							pbEditOrigin.setVisibility(View.GONE);
							pbEditOrigin.clearAnimation();
						}
					});
				} else {
					Utils.showNetworkAlertDialog(MainActivity.this);
				}
				return true;
			}
		});

		LinearLayout llEditOriginBack = (LinearLayout) findViewById(R.id.ll_edit_origin_back);
		llEditOriginBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0);
				ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(llEditRoute, pvhAlpha);
				alpha.addListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {}

					@Override
					public void onAnimationEnd(Animator animator) {
						llEditRoute.setVisibility(View.GONE);
						llEditOrigin.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onAnimationCancel(Animator animator) {}

					@Override
					public void onAnimationRepeat(Animator animator) {}
				});
				alpha.start();

				// Hide keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etOrigin.getWindowToken(), 0);
			}
		});

	}

	public void editDestination(View view){

		llEditRoute.setVisibility(View.VISIBLE);
		llEditDestination.setVisibility(View.VISIBLE);
		PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0, 1);
		ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(llEditRoute, pvhAlpha);
		alpha.start();

		final EditText etDestination = (EditText) findViewById(R.id.et_destination);
		etDestination.requestFocus();
		etDestination.setText(tvDestination.getText().toString());
		etDestination.selectAll();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(etDestination, 0);

		final ProgressBar pbEditDestination = (ProgressBar) findViewById(R.id.pb_edit_destination);

		etDestination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etDestination.getWindowToken(), 0);

				if (isNetworkAvailable()) {

					pbEditDestination.setVisibility(View.VISIBLE);
					pbEditDestination.animate();

					Calls.getAddressFromString(MainActivity.this, etDestination.getText().toString(), new GeocoderCallHandler() {
						@Override
						public void onSuccess(Address address) {

							pbEditDestination.setVisibility(View.GONE);
							pbEditDestination.clearAnimation();

							PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0);
							ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(llEditRoute, pvhAlpha);
							alpha.addListener(new AnimatorListener() {
								@Override
								public void onAnimationStart(Animator animator) {}

								@Override
								public void onAnimationEnd(Animator animator) {
									llEditRoute.setVisibility(View.GONE);
									llEditDestination.setVisibility(View.INVISIBLE);
								}

								@Override
								public void onAnimationCancel(Animator animator) {}

								@Override
								public void onAnimationRepeat(Animator animator) {}
							});
							alpha.start();

							String finalStringAddress = address.getAddressLine(0);
							for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
								finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
							}



							setOriginOrDestination("destination", finalStringAddress, new LatLng(address.getLatitude(), address.getLongitude()));
						}

						@Override
						public void onFailure(String reason) {

							pbEditDestination.setVisibility(View.GONE);
							pbEditDestination.clearAnimation();

							AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
							alert.setTitle(getString(R.string.end_destino_nao_encontrado_titulo))
									.setMessage(getString(R.string.end_nao_encontrado_mensagem))
									.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
										}
									});
							alert.show();
						}

						@Override
						public void onDismissedAlertView() {
							pbEditDestination.setVisibility(View.GONE);
							pbEditDestination.clearAnimation();
						}
					});
				} else {
					Utils.showNetworkAlertDialog(MainActivity.this);
				}
				return true;
			}
		});

		LinearLayout llEditDestinationBack = (LinearLayout) findViewById(R.id.ll_edit_destination_back);
		llEditDestinationBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0);
				ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(llEditRoute, pvhAlpha);
				alpha.addListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {}

					@Override
					public void onAnimationEnd(Animator animator) {
						llEditRoute.setVisibility(View.GONE);
						llEditDestination.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onAnimationCancel(Animator animator) {}

					@Override
					public void onAnimationRepeat(Animator animator) {}
				});
				alpha.start();

				// Hide keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etDestination.getWindowToken(), 0);
			}
		});

	}

	public void setOriginOrDestination (String whatToSet, String address, LatLng latLng) {

		switch (whatToSet) {
			case "origin":

				if (markerOrigin != null) { markerOrigin.remove(); }
				markerOrigin = googleMap.addMarker(new MarkerOptions()
						.position(latLng)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route_origin))
						.anchor(0.5f, 0.5f)
						.title(getString(R.string.partida)));

				tvOrigin.setText(address);

				break;

			case "destination":

				if (markerDestination != null) { markerDestination.remove(); }
				markerDestination = googleMap.addMarker(new MarkerOptions()
						.position(latLng)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route_destination))
						.anchor(0.5f, 0.5f)
						.title(getString(R.string.chegada)));

				tvDestination.setText(address);

				break;
		}

		if (markerOrigin != null && markerDestination != null) {
			getRoutes(markerOrigin.getPosition(), markerDestination.getPosition());
		} else {
			googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		}
	}

	public void getRoutes(LatLng origin, LatLng destination) {

		// find LatLngBound to animate camera and show the entire route.

		Double oLat = origin.latitude;
		Double oLng = origin.longitude;
		Double dLat = destination.latitude;
		Double dLng = destination.longitude;

		// Get distance between origin and destination to check
		Location originLocation = new Location("originLocation");
		originLocation.setLatitude(oLat);
		originLocation.setLongitude(oLng);
		Location destinationLocation = new Location("destinationLocation");
		destinationLocation.setLatitude(dLat);
		destinationLocation.setLongitude(dLng);

		// Check if origin and destination are more than 50 km from each other
		if (originLocation.distanceTo(destinationLocation) > 25000) {

			Utils.showSimpleAlertDialog(this, getString(R.string.error), getString(R.string.too_distant_route));
			pbLoadingRoute.setVisibility(View.GONE);
			hideBottomPanel();

		} else {

			llRouteDetailFragment.setVisibility(View.GONE);
			pbLoadingRoute.setVisibility(View.VISIBLE);

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

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			CameraUpdate cUpd = CameraUpdateFactory.newLatLngBounds(bounds, width/2, height / 3, 0);
			googleMap.animateCamera(cUpd);

			// Clean polyline on map
			if (!cyclingPathList.isEmpty()) {
				for (CyclingPath cp : cyclingPathList) {
					cp.routePolyline.remove();
					for (Polyline p : cp.intersectionPolylines) {
						p.remove();
					}
				}
			}

			// Select route according to user's priority configuration
			if (sharedPreferences.getString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_MOST_BIKE_LANES).equals(Constant.PRIORITY_MOST_BIKE_LANES)) {
				spinnerRoutePriority.setSelection(0);
			} else if (sharedPreferences.getString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_MOST_BIKE_LANES).equals(Constant.PRIORITY_FASTEST)) {
				spinnerRoutePriority.setSelection(1);
			} else if (sharedPreferences.getString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_MOST_BIKE_LANES).equals(Constant.PRIORITY_FLATTEST)) {
				spinnerRoutePriority.setSelection(2);
			}

			// Show view pager loading
			showBottomPanel("ROUTE");

			if (!isNetworkAvailable()) {
				Utils.showNetworkAlertDialog(this);
			} else {

				routeRequestId ++;

				Route.getRoute(this, routeRequestId, origin, destination, googleMap, new GetRouteInterface() {
					@Override
					public void onFinished(int requestId, ArrayList<CyclingPath> cyclingPaths) {

						if (requestId == routeRequestId && btRouteMode.isChecked()) {

							if (cyclingPaths.size() > 0) {

								cyclingPathList.clear();
								cyclingPathList = cyclingPaths;

								for (CyclingPath cp: cyclingPathList) {
									cp.drawOnMap();
								}

								CyclingPath cpMostBikeLanes = cyclingPathList.get(cyclingPathList.size() - 1);

								// Send to db infos about cycling path with mostBikeLanes
								String deviceID = sharedPreferences.getString(Constant.SPKEY_DEVICE_ID, "");
								if (!deviceID.equals("")) {
									Calls.sendOriginDestination(deviceID, cpMostBikeLanes.pathLatLng.get(0), cpMostBikeLanes.pathLatLng.get(cpMostBikeLanes.pathLatLng.size() - 1),
											cpMostBikeLanes.totalDistanceInKm, cpMostBikeLanes.maxInclination, null);
								}

								// Select route according to spinner's selected priority
								switch (spinnerRoutePriority.getSelectedItemPosition()) {
									case 0:
										for (CyclingPath cp : cyclingPathList) {
											if (cp.mostBikeLanes) selectCyclingPath(cp, false);
										}
										break;
									case 1:
										for (CyclingPath cp : cyclingPathList) {
											if (cp.fastest) selectCyclingPath(cp, false);
										}
										break;
									case 2:
										for (CyclingPath cp : cyclingPathList) {
											if (cp.flattest) selectCyclingPath(cp, false);
										}
										break;
								}
							} else {
								pbLoadingRoute.setVisibility(View.GONE);
								hideBottomPanel();
								Toast.makeText(MainActivity.this, getString(R.string.route_nenhuma_rota_encontrada), Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
			}
		}
	}

	public void selectCyclingPath(CyclingPath cyclingPath, boolean selectedFromMapClick) {

		showBottomPanel("ROUTE");

		if (selectedFromMapClick) {
			// Set spinner item for selected bike lane and update Shared Preferences with user's preferred priority
			SharedPreferences.Editor edit = sharedPreferences.edit();
			if (cyclingPath.mostBikeLanes) {
				spinnerRoutePriority.setSelection(0);
				edit.putString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_MOST_BIKE_LANES);
			} else if (cyclingPath.fastest) {
				spinnerRoutePriority.setSelection(1);
				edit.putString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_FASTEST);
			} else if (cyclingPath.flattest) {
				spinnerRoutePriority.setSelection(2);
				edit.putString(Constant.SPKEY_ROUTE_PRIORITY, Constant.PRIORITY_FLATTEST);
			}
			edit.apply();
		}

        for (CyclingPath cp : cyclingPathList) {
            cp.setSelected(false);
        }
        cyclingPath.setSelected(true);

        // Define the first "selectedCyclingPath"
        if (!selectedCyclingPath.isEmpty()) {
            selectedCyclingPath.clear();
        }
        selectedCyclingPath.add(cyclingPathList.get(cyclingPathList.size() - 1));

		// Add Route details
		pbLoadingRoute.setVisibility(View.GONE);
        llRouteDetailFragment.setVisibility(View.VISIBLE);

        tvRouteDistance.setText(cyclingPath.totalDistanceInKm + " km");
        tvRouteDuration.setText(cyclingPath.getReadableDuration());
        tvRoutePercentageOnBikeLanes.setText(cyclingPath.percentageOnBikeLanes + "%");

        if (!cyclingPath.pathElevation.isEmpty()) {

            XYSeriesRenderer renderer = new XYSeriesRenderer();
            XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();renderer.setLineWidth(30);
            renderer.setColor(getResources().getColor(R.color.water_blue));
            renderer.setDisplayBoundingPoints(true);
            renderer.setPointStyle(PointStyle.CIRCLE);
            renderer.setPointStrokeWidth(22);
            XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BELOW);
            fill.setColor(getResources().getColor(R.color.water_blue));
            renderer.addFillOutsideLine(fill);

            mRenderer.addSeriesRenderer(renderer);
            mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
            mRenderer.setMargins(new int[]{15, 0, 0, 0});
            mRenderer.setPanEnabled(false, false);
            mRenderer.setZoomEnabled(false, false);
            mRenderer.setShowGrid(false);
            mRenderer.setShowAxes(false);
            mRenderer.setShowLabels(false);
            mRenderer.setShowLegend(false);
            mRenderer.setDisplayValues(false);
            mRenderer.setClickEnabled(true);
            mRenderer.setXLabelsPadding(100);
            mRenderer.setYLabelsPadding(100);

            // aChartEngine
            XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
            XYSeries xySeries = new XYSeries("MySerie");

            // Get cyclingPath's elevations list
            ArrayList<Double> elevations = cyclingPath.pathElevation;

            // Add each elevation point to the elevationSeries1 variable
            for (int i = 0; i < elevations.size(); i++) {
                xySeries.add(i, elevations.get(i));
            }

            //aChartEngine
            mDataset.addSeries(xySeries);

            GraphicalView gView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
            gView.setClickable(true);
            gView.setPadding(0, 0, 0, 0);

            llChart.addView(gView, 0);
        } else {
            tvElevationUnavailable.setVisibility(View.VISIBLE);
        }
    }

	public void switchAddresses(final View view) {

		ObjectAnimator oa = ObjectAnimator.ofFloat(view,"rotation", 180);
		oa.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {}
			@Override
			public void onAnimationEnd(Animator animator) {
				view.setRotation(0);
			}
			@Override
			public void onAnimationCancel(Animator animator) {}
			@Override
			public void onAnimationRepeat(Animator animator) {}
		});
		oa.start();

		String oString = tvOrigin.getText().toString();
		String dString = tvDestination.getText().toString();
		tvOrigin.setText(dString);
		tvDestination.setText(oString);

		if (markerOrigin != null && markerDestination != null) {

			LatLng oLatLng = markerOrigin.getPosition();
			LatLng dLatLng = markerDestination.getPosition();

			markerOrigin.setPosition(dLatLng);
			markerDestination.setPosition(oLatLng);

			getRoutes(dLatLng, oLatLng);
		} else if (markerOrigin != null) {

			markerDestination = googleMap.addMarker(new MarkerOptions()
					.position(markerOrigin.getPosition())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route_destination))
					.anchor(0.5f, 0.5f)
					.title(getString(R.string.chegada)));

			markerOrigin.remove();
			markerOrigin = null;

		} else if (markerDestination != null) {

			markerOrigin = googleMap.addMarker(new MarkerOptions()
					.position(markerDestination.getPosition())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route_origin))
					.anchor(0.5f, 0.5f)
					.title(getString(R.string.partida)));

			markerDestination.remove();
			markerDestination = null;

		}
	}

    /* END ROUTING */
	/* PLACES */

	public void handlePlaceClick (final int placeId) {

		for (final Place place: ListPlaces) {
			if (place.id == placeId){

				if (!place.isFeatured) {

					TextView tvPlaceNameNV = (TextView) findViewById(R.id.tv_place_name_nv);
					TextView tvPlaceServicesNV = (TextView) findViewById(R.id.tv_place_services_nv);
					final TextView tvPlaceAddressNV = (TextView) findViewById(R.id.tv_place_address_nv);
					TextView tvPlacePhoneNV = (TextView) findViewById(R.id.tv_place_phone_nv);
					LinearLayout btPlaceMenuNV = (LinearLayout) findViewById(R.id.bt_place_menu_nv);
					TextView tvPlaceNotVerifiedNV = (TextView) findViewById(R.id.tv_place_not_verified_nv);
					if (place.isVerified) {
						tvPlaceNotVerifiedNV.setVisibility(View.GONE);
						tvPlaceNameNV.setTextColor(getResources().getColor(R.color.dark_text));
					} else {
						tvPlaceNotVerifiedNV.setVisibility(View.VISIBLE);
						tvPlaceNameNV.setTextColor(getResources().getColor(R.color.light_text));
					}

					final LinearLayout llPlaceMenuNV = (LinearLayout) findViewById(R.id.ll_place_menu_nv);
					llPlaceMenuNV.setVisibility(View.GONE);

					tvPlaceNameNV.setText(place.name);
					tvPlaceServicesNV.setText(place.displayServices);
					tvPlaceAddressNV.setText(getString(R.string.address) + ": " + place.address);
					tvPlacePhoneNV.setText(getString(R.string.phone) + ": " + place.phone);

					btPlaceMenuNV.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {

							if (llPlaceMenuNV.getVisibility() == View.VISIBLE) {
								Log.e("CLICKED", "VISIBLE");
								llPlaceMenuNV.setVisibility(View.INVISIBLE);
							} else {
								Log.e("CLICKED", "GONE");
								llPlaceMenuNV.setVisibility(View.VISIBLE);

								TextView tvPlaceOwner = (TextView) findViewById(R.id.tv_place_owner);
								tvPlaceOwner.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Intent i = new Intent(MainActivity.this, SugestaoActivity.class);
										i.putExtra(Constant.IEXTRA_PLACE_NAME, place.name);
										i.putExtra(Constant.IEXTRA_PLACE_ID_INT, place.id);
										startActivity(i);
									}
								});

								TextView tvCorrectInfo = (TextView) findViewById(R.id.tv_correct_place_info);
								if (place.isVerified) {
									tvCorrectInfo.setVisibility(View.GONE);
								} else {
									tvCorrectInfo.setVisibility(View.VISIBLE);
									tvCorrectInfo.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View view) {
											Intent i = new Intent(MainActivity.this, AddToMapActivity.class);
											i.putExtra("SELECTED_FUNCTION", "EDIT_PLACE");
											i.putExtra(Constant.IEXTRA_PLACE_ID_INT, place.id);
											i.putExtra(Constant.IEXTRA_PLACE_NAME, place.name);
											i.putExtra(Constant.IEXTRA_PLACE_PHONE, place.phone);
											i.putExtra(Constant.IEXTRA_PLACE_PUBLIC_EMAIL, place.publicEmail);
											i.putExtra(Constant.IEXTRA_PLACE_LAT_DOUBLE, place.latLng.latitude);
											i.putExtra(Constant.IEXTRA_PLACE_LNG_DOUBLE, place.latLng.longitude);
											i.putExtra(Constant.IEXTRA_PLACE_CATEGORY_ID_LIST, place.categoryIdList);

											startActivity(i);
										}
									});
								}


								TextView tvFlagInexistent = (TextView) findViewById(R.id.tv_flag_place_inexistent);
								tvFlagInexistent.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										llPlaceMenuNV.setVisibility(View.GONE);
										Calls.flagInexistentPlace(String.valueOf(placeId), new CallHandler(){
											@Override
											public void onSuccess(int responseCode, String response) {
												super.onSuccess(responseCode, response);
												Utils.showThanksToast(MainActivity.this);
											}

											@Override
											public void onFailure(int responseCode, String response) {
												super.onFailure(responseCode, response);
												Utils.showServerErrorToast(MainActivity.this, response);
											}
										});
									}
								});

							}
						}
					});

					llPlaceDetailsNV.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							llPlaceMenuNV.setVisibility(View.GONE);
						}
					});

					showBottomPanel("PLACE_NOT_FEATURED");

				} else {

					llPlaceDetailsV.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent i = new Intent(MainActivity.this, PlaceDetailsActivity.class);
							i.putExtra(Constant.IEXTRA_PLACE_ID_INT, place.id);
							if (user_updated_latlng != null) {
								i.putExtra("USER_LAT", user_updated_latlng.latitude);
								i.putExtra("USER_LNG", user_updated_latlng.longitude);
							}
							startActivityForResult(i, Constant.REQUEST_CODE_ROUTE_FOR_PLACE);
						}
					});

					ImageView ivPlaceLogoV = (ImageView) findViewById(R.id.iv_place_logo_v);
					if (Constant.mapPlacesImages.get(place.logoId) != null) {
						ivPlaceLogoV.setImageBitmap(Constant.mapPlacesImages.get(place.logoId));
					} else {
						ivPlaceLogoV.setImageBitmap(Constant.mapPlacesImages.get(3));
					}
					TextView tvPlaceNameV = (TextView) findViewById(R.id.tv_place_name_v);
					final TextView tvPlaceServicesV = (TextView) findViewById(R.id.tv_place_services_v);
					TextView tvPlaceShortDescV = (TextView) findViewById(R.id.tv_place_short_desc_v);
					TextView tvPlaceIsOpenV = (TextView) findViewById(R.id.tv_place_isopen_v);
					final ImageView ivPlaceServicesV = (ImageView) findViewById(R.id.iv_place_services_v);

					tvPlaceServicesV.setText(place.displayServices);

					if (!mapCategoriesIcons.isEmpty()) {
						ArrayList<Bitmap> bitmapArray = new ArrayList<>();
						for (int id: place.categoryIdList) {
							bitmapArray.add(mapCategoriesIcons.get(id));
						}
						ivPlaceServicesV.setImageBitmap(Utils.combineImages(this, bitmapArray));
						tvPlaceServicesV.setVisibility(View.GONE);
						ivPlaceServicesV.setVisibility(View.VISIBLE);

						/*ivPlaceServicesV.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								ivPlaceServicesV.setVisibility(View.GONE);
								tvPlaceServicesV.setVisibility(View.VISIBLE);
							}
						});

						tvPlaceServicesV.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								ivPlaceServicesV.setVisibility(View.VISIBLE);
								tvPlaceServicesV.setVisibility(View.GONE);
							}
						});*/
					} else {
						ivPlaceServicesV.setVisibility(View.GONE);
						tvPlaceServicesV.setVisibility(View.VISIBLE);
					}

					tvPlaceNameV.setText(place.name);
					tvPlaceShortDescV.setText(place.short_desc);
					tvPlaceIsOpenV.setText(place.currentOpenStatus);
					if (place.currentOpenStatus.contains("FECHADO")) {
						tvPlaceIsOpenV.setTextColor(getResources().getColor(R.color.app_red));
					} else {
						tvPlaceIsOpenV.setTextColor(getResources().getColor(R.color.app_green));
					}

					RelativeLayout rlClosePlacePanel = (RelativeLayout) findViewById(R.id.rl_place_panel_close);
					rlClosePlacePanel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							hideBottomPanel();
							markerSearch.remove();
						}
					});

					RelativeLayout rlPlaceOpenDetails = (RelativeLayout) findViewById(R.id.rl_place_open_details);
					rlPlaceOpenDetails.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {

							Intent i = new Intent(MainActivity.this, PlaceDetailsActivity.class);
							i.putExtra(Constant.IEXTRA_PLACE_ID_INT, place.id);
							if (user_updated_latlng != null) {
								i.putExtra("USER_LAT", user_updated_latlng.latitude);
								i.putExtra("USER_LNG", user_updated_latlng.longitude);
							}
							startActivityForResult(i, Constant.REQUEST_CODE_ROUTE_FOR_PLACE);
						}
					});

					showBottomPanel("PLACE_FEATURED");
				}
			}
		}
	}

	/* END PLACES */
	/* DEALS */

	public void showAllDeals() {
		Intent intent = new Intent(MainActivity.this, DealListActivity.class);
		intent.putExtra(Constant.ICODE_DEAL_LIST, Constant.IEXTRA_ICODE_DEAL_LIST_ALL);
		intent.putExtra("DEAL_WINDOW_TITLE", getString(R.string.deals));
		if (user_updated_latlng != null) {
			intent.putExtra("USER_LAT", user_updated_latlng.latitude);
			intent.putExtra("USER_LNG", user_updated_latlng.longitude);
		}
		startActivityForResult(intent, Constant.REQUEST_CODE_ROUTE_FOR_DEAL);
	}

	/* END DEALS */
    /* MISCELLANEOUS */

    public void showBottomButton (final View viewToAnimate) {
        ObjectAnimator showAnimation = ObjectAnimator.ofFloat(viewToAnimate, "translationY", 0);
		showAnimation.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				viewToAnimate.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animator) {
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
        showAnimation.start();
    }

    public ObjectAnimator hideBottomButton(final View viewToAnimate) {
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
		return hideAnimation;
    }

    public ArrayList<ObjectAnimator> hideAllBottomButtons () {

		ArrayList<ObjectAnimator> returnArray = new ArrayList<>();

		returnArray.add(hideBottomButton(notifyButton));
		returnArray.add(hideBottomButton(btParkedHere));
		returnArray.add(hideBottomButton(btRemovePlace));
        //hideBottomButton(llPlaceOptions);

		return returnArray;
    }

    public void checkNumberOfOptionsDisplayed() {

        int numberOfTrues = 0;

        for (int i = 1; i < Constant.States.length; i++) {
            if (Constant.States[i]) numberOfTrues++;
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

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
	}

	@Override
	public void onBackPressed() {

		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else if (rlAddToMap.getVisibility() == View.VISIBLE) {
			hideAddLayout();
		} else if (llEditRoute.getVisibility() == View.VISIBLE){
			PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0);
			ObjectAnimator alpha = ObjectAnimator.ofPropertyValuesHolder(llEditRoute, pvhAlpha);
			alpha.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {}

				@Override
				public void onAnimationEnd(Animator animator) {
					llEditRoute.setVisibility(View.GONE);
					llEditDestination.setVisibility(View.INVISIBLE);
					llEditOrigin.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onAnimationCancel(Animator animator) {}

				@Override
				public void onAnimationRepeat(Animator animator) {}
			});
			alpha.start();
		} else if (rlBottomContainer.getTranslationY() != rlBottomContainer.getHeight()) {
			hideBottomPanel();
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

	float[] orientation = new float[3];
	float[] rMat = new float[9];

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {

		// If the sensor data is unreliable return
		if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;

		if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ) {

			// calculate th rotation matrix
			SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);

			azimuth = (float) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;

			// If navigation is On, turn map
			if (navigationIsOn) {
				if (user_updated_latlng != null) {
					googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(user_updated_latlng, 18, 50, azimuth)));
				}
			} else {
				// If navigation is off, but markerNavigation is on, just turn marker navigation
				if (markerNavigation != null) {
					markerNavigation.setRotation(azimuth);
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {}

	@Override
	public void onLocationChanged(Location location) {
		user_updated_latlng = new LatLng(location.getLatitude(), location.getLongitude());

		if (shouldGetDeals) {
			Calls.getDealsForLocation(user_updated_latlng, getDealsForLocationHandler);
			shouldGetDeals = false;
		}
		// If markerNavigation created, update position. If not created, create
		if (markerNavigation != null) {
			markerNavigation.setPosition(user_updated_latlng);
			markerNavigation.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.btic_navigation));
		} else {

			markerNavigation = googleMap.addMarker(new MarkerOptions()
					.position(user_updated_latlng)
					.zIndex(100)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.btic_navigation))
					.anchor(0.5f, 0.5f)
			);
			markerNavigation.setTag(new String[]{"markerNavigation"});
		}

		if (circleAccuracy != null) {
			circleAccuracy.setCenter(user_updated_latlng);
			circleAccuracy.setRadius(location.getAccuracy());
		} else {
			circleAccuracy = googleMap.addCircle(new CircleOptions()
					.center(user_updated_latlng)
					.radius(location.getAccuracy())
					.strokeColor(getResources().getColor(R.color.transparent))
					.zIndex(-10f)
					.fillColor(getResources().getColor(R.color.water_blue_translucent)));
		}

		if (navigationIsOn) {
			if (googleMap != null) {
				googleMap.animateCamera(newLatLngZoom(user_updated_latlng, 18));
			}
		}
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
                                user_updated_latlng = new LatLng(user_loc.getLatitude(), user_loc.getLongitude());
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
			case PERMISSION_REQUEST_CODE_CALL_PHONE:

				if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
					if (placeTelToCall != null) {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + placeTelToCall.trim()));
						startActivity(intent);
					}
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

		if (user_updated_latlng != null) {
			Calls.getDealsForLocation(user_updated_latlng, getDealsForLocationHandler);
		} else {
			shouldGetDeals = true;
		}
		MyApplication.activityResumed();

		synchronizePlaces();

		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);

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

		// Register this class as a listener for the Rotation Vector sensor
		sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));

        Log.e("ONPAUSE", "SHOT");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
        } else {
            if (locationManager != null) {
                locationManager.removeUpdates(this);
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
				case R.id.action_all_deals:
					showAllDeals();
					return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		super.onActivityResult(requestCode, resultCode, i);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {
				case Constant.REQUEST_CODE_ROUTE_FOR_PLACE:

					Double latPlace = i.getDoubleExtra(Constant.IEXTRA_PLACE_LAT_DOUBLE, 0);
					Double lngPlace = i.getDoubleExtra(Constant.IEXTRA_PLACE_LNG_DOUBLE, 0);
					String addressPlace = i.getStringExtra(Constant.IEXTRA_PLACE_ADDRESS);
					if (latPlace != 0 && lngPlace != 0) {
						etSearch.setText(addressPlace);
						markerSearch.remove();
						markerSearch = googleMap.addMarker(new MarkerOptions()
								.position(new LatLng(latPlace, lngPlace))
								.title(addressPlace));
						turnOnRouteMode();
					}

					break;
				case Constant.REQUEST_CODE_ROUTE_FOR_DEAL:

					Double latDeal = i.getDoubleExtra(Constant.IEXTRA_DEAL_LAT_DOUBLE, 0);
					Double lngDeal = i.getDoubleExtra(Constant.IEXTRA_DEAL_LNG_DOUBLE, 0);
					String addressDeal = i.getStringExtra(Constant.IEXTRA_DEAL_ADDRESS);
					if (latDeal != 0 && lngDeal != 0) {
						etSearch.setText(addressDeal);
						markerSearch.remove();
						markerSearch = googleMap.addMarker(new MarkerOptions()
								.position(new LatLng(latDeal, lngDeal))
								.title(addressDeal));
						turnOnRouteMode();
					}

					break;
			}
		}
	}

	CallHandler getPlacesIconsCallHandler = new CallHandler() {
		@Override
		public void onSuccess(int responseCode, String response) {
			Log.e("getIcons", response);

			// Get Places
			Calls.jsonRequest(Constant.url_get_places, new CallHandler() {
				@Override
				public void onSuccess(int responseCode, String response) {

					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString(Constant.spJobPlaces, response);
					editor.apply();

					setListItemLoading(Constant.LISTPOS_PLACES, false);

					try {
						createPlacesArray();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int responseCode, String response) {
					Log.e("getPlaces fail", response);
					Toast.makeText(MainActivity.this, getString(R.string.error_loading_places), Toast.LENGTH_LONG).show();
					resetUpdating();
					setListItemLoading(Constant.LISTPOS_PLACES, false);
				}
			});

		}
		@Override
		public void onFailure(int responseCode, String response) {
			Log.e("getIcons", response);
			Toast.makeText(MainActivity.this, getString(R.string.error_loading_places), Toast.LENGTH_LONG).show();
			resetUpdating();
			setListItemLoading(Constant.LISTPOS_PLACES, false);
			placesIsLoading = false;
		}
	};

	CallHandler getDealsForLocationHandler = new CallHandler() {
		@Override
		public void onSuccess (int responseCode, final String response) {
			try {
				JSONArray jarray = new JSONArray(response);

				switch (jarray.length()) {
					case 0:
						break;
					case 1:
						tvDeals.setText(jarray.length() + " OFERTA LEGAL PERTO DE VOCÊ");
						if (llDeals.getTranslationX() != 0) {
							showDeals.start();
						}
						break;
					default:
						tvDeals.setText(jarray.length() + " OFERTAS LEGAIS PERTO DE VOCÊ");
						if (llDeals.getTranslationX() != 0) {
							showDeals.start();
						}
						break;
				}

				final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener(){
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

						if (e1.getX() - e2.getX() > 0 ) {
							hideDeals.start();
						} else if (e2.getX() - e1.getX() > 0 && Math.abs(velocityX) > 0) {
							// Swipe right
						}
						return true;

					}
				});

				llDeals.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent motionEvent) {
						return gestureDetector.onTouchEvent(motionEvent);
					};
				});

				llDeals.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						showAllDeals();
					}
				});

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure (int responseCode, String response) {
		}
	};
}