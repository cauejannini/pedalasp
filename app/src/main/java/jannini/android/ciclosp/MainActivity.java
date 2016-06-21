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
import android.graphics.Color;
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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
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
import jannini.android.ciclosp.MyApplication.TrackerName;
import jannini.android.ciclosp.NetworkRequests.Directions;
import jannini.android.ciclosp.NetworkRequests.JSONParser;
import jannini.android.ciclosp.NetworkRequests.NotifySolvedReport;


public class MainActivity extends FragmentActivity implements LocationListener {

	// Location Manager and Provider
	private LocationManager locationManager;
	private String bestAvailableProvider;

	// Google Map
	private GoogleMap googleMap;

	// General Geocoder
	public Geocoder geocoder;

	// Analytics tracker
	Tracker t;

	// Calendars
	public Calendar rightNow;
	public Calendar sundaySeven;
	public Calendar sundaySixteen;

	// String with time of last update Bike Sampa and Ciclo Sampa
	public String updateTimeBS;
	public String updateTimeCS;

	// Bike lanes Arrays
	public ArrayList<Polyline> cicloviasLineList = new ArrayList<>();

	public ArrayList<Polyline> ciclofaixasLineList = new ArrayList<>();

	public static ArrayList<PolylineOptions> ciclorrotasOptionsList = new ArrayList<>();
	public ArrayList<Polyline> ciclorrotasLineList = new ArrayList<>();

	// Markers for entrance in Marg. Pinheiros
	Marker pinheiros1marker;
	Marker pinheiros2marker;
	Marker pinheiros3marker;
	Marker pinheiros4marker;
	Marker pinheiros5marker;

	public static String newline = System.getProperty("line.separator");

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	// APP STORED PREFERENCES
	// Strings that stores in device all jSon info
	String jsonObjString;
	String jsonObjBSString;
	String jsonObjCSString;

	// Boolean to check if this is the first time app is being opened
	Boolean betaRouteWarningWasShown;
	Boolean elevGraphExpWasShown;
	Boolean drawerExpWasShown;

	// url to get all products list
	String url_obter_dados = "http://pedalasp.org/dbaccess/obter_dados.php";
	String url_obter_bikesampa = "http://pedalasp.org/dbaccess/obter_bikesampa.php";
	String url_obter_ciclosampa = "http://pedalasp.org/dbaccess/obter_ciclosampa.php";

	// IDEstacionamentos JSONArray
	JSONArray BSjSonArray = null;
	JSONArray CSjSonArray = null;
	JSONArray ParquesJSArray = null;
	JSONArray BicicletariosJSArray = null;
	JSONArray WifiJSArray = null;
	JSONArray ReportsJSArray = null;

	// Criando listas de itens de cada tabela da DB
	public static ArrayList<Estacao> ListEstacoesITAU = new ArrayList<>();
	public static ArrayList<Estacao> ListEstacoesBRA = new ArrayList<>();
	public static ArrayList<Parque> ListParques = new ArrayList<>();
	public static ArrayList<Bicicletario> ListBicicletarios = new ArrayList<>();
	public static ArrayList<Ciclovia> ListCiclovias = new ArrayList<>();
	public static ArrayList<Ciclovia> ListCiclofaixas = new ArrayList<>();
	public static ArrayList<MarkerOptions> ListWifi = new ArrayList<>();
	public static ArrayList<Report> ListReports = new ArrayList<>();

	//Criando listas dos marcadores de mapa para os itens de cada tabela da DB
	ArrayList<Marker> ListMarkersITAU = new ArrayList<>();
	ArrayList<Marker> ListMarkersBRA = new ArrayList<>();
	ArrayList<Marker> ListMarkersParques = new ArrayList<>();
	ArrayList<Marker> ListMarkersBicicletarios = new ArrayList<>();
	ArrayList<Marker> ListMarkersWifi = new ArrayList<>();
	ArrayList<Marker> ListMarkersReports = new ArrayList<>();

	ArrayList<String> ListMarkersReportsIds = new ArrayList<>();

	List<Marker> listMarker = new ArrayList<>();

	//Navigation Drawer
	public static String[] mMenuTitles;
	public static String[] mMenuDescriptions;
	public static String[] mMenuQuantidades = new String[]{"", "", "", "", "", "", "", "", ""};
	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private MyListAdapter myAdapter;

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
	Marker markerDestination = null;
	Marker markerOrigin = null;
	Marker markerSearch = null;

	ImageView iv;
	Menu mymenu;
	MenuItem menu_item;

	double[] current_latlng;
	float zoom;

	public static SharedPreferences pref;
	public static SharedPreferences.Editor editor;

	public static boolean[] states = {true, true, true, true, true, true, true, true, true};

	static ArrayList<Polyline> polylineRoutesList = new ArrayList<>();

	Criteria criteria = new Criteria();

	LatLng user_latlng = null;

	// Notify solved variables

	Button notifyButton;
	ObjectAnimator hideNotifyButton;
	String clickedMarkerId;
	Marker clickedMarker;

	Map<String, String> reportMap = new HashMap<>();

	// ROUTE variables
	static ArrayList<CyclingPath> cyclingPathList = new ArrayList<>();

	// Auxiliar list that only stores the selected cyclingPath
	ArrayList<CyclingPath> selectedCyclingPath = new ArrayList<>();

	// Auxiliar marker to show point selected on Graph
	Marker graph_point_marker = null;

	// AsyncTasks
	AsyncTask<String, String, String> geocodeOriginASY;
	AsyncTask<String, String, String> geocodeDestinationASY;
	AsyncTask<String, Integer, ArrayList<CyclingPath>> getRoutesASY;

	// ViewPager for Route Details
	SwipeFragmentPagerAdapter swipeFragmentPagerAdapter;
	ViewPager viewPager;
	Integer viewPagerPosition = null;
	ObjectAnimator hideViewPager;

	ToggleButton btRouteMode;

	//aChartEngine
	static LinearLayout aChartParentView;
	static ArrayList<GraphicalView> graphViewArray = new ArrayList<>();
	XYSeriesRenderer renderer = new XYSeriesRenderer();
	XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

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

		//aChartEngine
		renderer.setLineWidth(30);
		renderer.setColor(Color.BLUE);
		renderer.setDisplayBoundingPoints(true);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setPointStrokeWidth(22);
		XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BELOW);
		fill.setColor(Color.BLUE);
		renderer.addFillOutsideLine(fill);

		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		mRenderer.setMargins(new int[]{15, 0, 0, 0});
		mRenderer.setBackgroundColor(Color.YELLOW);
		mRenderer.setGridColor(Color.GREEN);
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

		// Get tracker
		t = ((MyApplication) this.getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName("MainActivity");
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		pref = getApplicationContext().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
		editor = pref.edit();
		// Those next two lines makes Splash initialize everytime. It's just for testing on emulator purposes. They have to be removed
		//editor.remove("jsonObjString");
		//editor.apply();
		jsonObjString = pref.getString("jsonObjString", null);
		jsonObjBSString = pref.getString("jsonObjBSString", null);
		jsonObjCSString = pref.getString("jsonObjCSString", null);
		updateTimeBS = pref.getString("updateTimeBS", null);
		updateTimeCS = pref.getString("updateTimeCS", null);

		betaRouteWarningWasShown = pref.getBoolean("betaRouteWarningWasShown", false);
		elevGraphExpWasShown = pref.getBoolean("elevGraphExpWasShown", false);
		drawerExpWasShown = pref.getBoolean("drawerExpWasShown", false);

		//Create criteria to decide what is the best location provider. Store this information in "provider
		criteria.setSpeedRequired(false);
		criteria.setAltitudeRequired(false);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// If provider is enabled, request location update
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
			return;

		} else {
			// GET LOCATION MANAGER
			locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
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


		// Get States from pref
		states[0] = pref.getBoolean("states0", true);
		states[1] = pref.getBoolean("states1", true);
		states[2] = pref.getBoolean("states2", true);
		states[3] = pref.getBoolean("states3", true);
		states[4] = pref.getBoolean("states4", true);
		states[5] = pref.getBoolean("states5", true);
		states[6] = pref.getBoolean("states6", true);
		states[7] = pref.getBoolean("states7", true);
		states[8] = pref.getBoolean("states8", true);


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

		etSearch.clearFocus();

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

		hideNotifyButton = ObjectAnimator.ofFloat(notifyButton, "translationY", 0, 600);
		hideNotifyButton.addListener(new AnimatorListener() {

			@Override
			public void onAnimationEnd(Animator animation) {
				notifyButton.setVisibility(View.GONE);
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

		iv = (ImageView) findViewById(R.id.iv_action_refresh);

		//AlertView for calculating routes
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
		adLoadingRoute = alertBuilder.create();
		adLoadingRoute.setCancelable(false);
		View calculatingRouteAV = getLayoutInflater().inflate(R.layout.ad_loading_route, null);
		adLoadingRoute.setView(calculatingRouteAV);
		Button btCancelCalculatingRoute = (Button) calculatingRouteAV.findViewById(R.id.cancel_loading_routes);
		pbLoadingRoute = (ProgressBar) calculatingRouteAV.findViewById(R.id.pb_loading_route);

		// INITIALIZE MAP
		try {
			initializeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// DRAWER VARIABLES
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mTitle = mDrawerTitle = getTitle();

		// DRAWER LIST VARIABLES
		mMenuTitles = getResources().getStringArray(R.array.menu_array);
		mMenuDescriptions = getResources().getStringArray(R.array.menu_array_descriptions);

		// Set a custom shadow that overlays the main content when the drawer opens
		//mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// Set up the drawer's list view with items and click listener.
		myAdapter = new MyListAdapter(this, mMenuTitles, mMenuDescriptions, mMenuQuantidades);
		mDrawerList.setAdapter(myAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Setar padding para ListView porque depois quando se troca o fundo do ListItem por um Drawable, perde-se todos os paddings setados no layout.
		//mDrawerList.setPadding(11, 30, 11, 11);

		// Enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// A linha de c�digo a seguir estava pedindo API m�n. 14. Como tirar ela n�o mudou nada, tirei. getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  // host Activity
				mDrawerLayout,         // DrawerLayout object
				R.drawable.ic_drawer,  // nav drawer image to replace 'Up' caret
				R.string.drawer_open,  // "open drawer" description for accessibility
				R.string.drawer_close  // "close drawer" description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
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

		// Check savedInstanceState
		if (savedInstanceState == null) {

			try {
				setUserLocation();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Check if Splash was initialized before this activity
			Intent i = this.getIntent();
			boolean extra = i.getBooleanExtra("WasSplashInitialized", false);
			if (!extra) {

				// Check if there's jSon String saved on device, else, initialize Splash
				if (jsonObjString == null && jsonObjBSString == null && jsonObjCSString == null) {

					Intent intent = new Intent(this, SplashScreen.class);
					startActivity(intent);
					finish();

				} else {

					try {
						createBaseArrays();
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					try {
                        setUpdating();
						new CarregarDB().execute();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} else {
				try {
					createBaseArrays();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// Bloco abaixo foi adicionado pra evitar o erro de null em CameraUpdateFactory e IBitMapDescriptorFactory
		try {
			MapsInitializer.initialize(getApplicationContext());
		} catch (Exception e) {
		}

		if (googleMap != null) {
			setMapEvents();
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
				case R.id.sugestao:
					Intent intent_sugestao = new Intent(MainActivity.this, SugestaoActivity.class);
					intent_sugestao.putExtra("EXTRA", "Sugestão Pedala SP");
					startActivity(intent_sugestao);
					return true;
				case R.id.action_refresh:
					refreshData(item);
					return true;
            /*case R.id.rss:
                Intent intent_rss = new Intent(MainActivity.this, RSSActivity.class);
                startActivity(intent_rss);
                return true;*/
			/*case R.id.create_route:
				Intent intent_createRoute = new Intent(MainActivity.this, CreateRouteActivity.class);
				startActivity(intent_createRoute);
				return true;*/
			}
		}
		return super.onOptionsItemSelected(item);
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
			case 0:
				// Verificar se as ciclovias já estão desenhadas
				if (!cicloviasLineList.isEmpty()) {
					if (!cicloviasLineList.get(0).isVisible()) {
						mDrawerList.getChildAt(0).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						// Set Polylines and Markers visible
						for (int i = 0; i < cicloviasLineList.size(); i++) {
							cicloviasLineList.get(i).setVisible(true);
						}
						pinheiros1marker.setVisible(true);
						pinheiros2marker.setVisible(true);
						pinheiros3marker.setVisible(true);
						pinheiros4marker.setVisible(true);
						pinheiros5marker.setVisible(true);
						states[0] = true;
					} else {
						mDrawerList.getChildAt(0).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						// Set Ciclovias not visible
						for (int i = 0; i < cicloviasLineList.size(); i++) {
							cicloviasLineList.get(i).setVisible(false);
						}
						pinheiros1marker.setVisible(false);
						pinheiros2marker.setVisible(false);
						pinheiros3marker.setVisible(false);
						pinheiros4marker.setVisible(false);
						pinheiros5marker.setVisible(false);
						states[0] = false;
					}
				} // Caso não estejam desenhadas, desenhar!
				else {
					mDrawerList.getChildAt(0).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawPermanentes(true);
					states[0] = true;
				}
				break;

			case 1:
				// Verificar se as ciclofaixas de lazer já estão desenhadas
				if (!ciclofaixasLineList.isEmpty()) {
					if (!ciclofaixasLineList.get(0).isVisible()) {
						mDrawerList.getChildAt(1 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						// Set Ciclofaixas visible
						for (int i = 0; i < ciclofaixasLineList.size(); i++) {
							ciclofaixasLineList.get(i).setVisible(true);
						}
						states[1] = true;
					} else {
						mDrawerList.getChildAt(1 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						// Set Ciclofaixas not visible
						for (int i = 0; i < ciclofaixasLineList.size(); i++) {
							ciclofaixasLineList.get(i).setVisible(false);
						}
						states[1] = false;
					}
				} else {
					mDrawerList.getChildAt(1 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawTemporarias(true);
					states[1] = true;
				}
				break;

			case 2:
				if (!ciclorrotasLineList.isEmpty()) {
					if (!ciclorrotasLineList.get(0).isVisible()) {
						mDrawerList.getChildAt(2 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						// Set Ciclorrotas visible
						for (int i = 0; i < ciclorrotasLineList.size(); i++) {
							ciclorrotasLineList.get(i).setVisible(true);
						}
						states[2] = true;
					} else {
						mDrawerList.getChildAt(2 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						// Set Ciclorrotas not visible
						for (int i = 0; i < ciclorrotasLineList.size(); i++) {
							ciclorrotasLineList.get(i).setVisible(false);
						}
						states[2] = false;
					}
				} else {
					mDrawerList.getChildAt(2 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawPreferenciais(true);
					states[2] = true;
				}
				break;

			case 3:
				if (!ListMarkersITAU.isEmpty()) {
					if (!ListMarkersITAU.get(0).isVisible()) {
						mDrawerList.getChildAt(3 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);

						for (int i = 0; i < ListMarkersITAU.size(); i++) {
							ListMarkersITAU.get(i).setVisible(true);
						}

						states[3] = true;
					} else {
						mDrawerList.getChildAt(3 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);

						for (int i = 0; i < ListMarkersITAU.size(); i++) {
							ListMarkersITAU.get(i).setVisible(false);
						}

						states[3] = false;
					}
				} else {
					mDrawerList.getChildAt(3 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawBikeSampa(true);
					states[3] = true;
				}
				break;

			case 4:
				if (!ListMarkersBRA.isEmpty()) {
					if (!ListMarkersBRA.get(0).isVisible()) {
						mDrawerList.getChildAt(4 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						for (int i = 0; i < ListMarkersBRA.size(); i++) {
							ListMarkersBRA.get(i).setVisible(true);
						}
						states[4] = true;
					} else {
						mDrawerList.getChildAt(4 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						for (int i = 0; i < ListMarkersBRA.size(); i++) {
							ListMarkersBRA.get(i).setVisible(false);
						}
						states[4] = false;
					}
				} else {
					mDrawerList.getChildAt(4 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawCicloSampa(true);
					states[4] = true;
				}
				break;

			case 5:
				if (!ListMarkersParques.isEmpty()) {
					if (!ListMarkersParques.get(0).isVisible()) {
						mDrawerList.getChildAt(5 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						for (int i = 0; i < ListMarkersParques.size(); i++) {
							ListMarkersParques.get(i).setVisible(true);
						}
						states[5] = true;
					} else {
						mDrawerList.getChildAt(5 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						for (int i = 0; i < ListMarkersParques.size(); i++) {
							ListMarkersParques.get(i).setVisible(false);
						}
						states[5] = false;
					}
				} else {
					mDrawerList.getChildAt(5 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawParques(true);
					states[5] = true;
				}
				break;

			case 6:
				if (!ListMarkersBicicletarios.isEmpty()) {
					if (!ListMarkersBicicletarios.get(0).isVisible()) {
						mDrawerList.getChildAt(6 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						for (int i = 0; i < ListMarkersBicicletarios.size(); i++) {
							ListMarkersBicicletarios.get(i).setVisible(true);
						}
						states[6] = true;
					} else {
						mDrawerList.getChildAt(6 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						for (int i = 0; i < ListMarkersBicicletarios.size(); i++) {
							ListMarkersBicicletarios.get(i).setVisible(false);
						}
						states[6] = false;
					}
				} else {
					mDrawerList.getChildAt(6 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawBicicletarios(true);
					states[6] = true;
				}
				break;
			case 7:
				if (!ListMarkersWifi.isEmpty()) {
					if (!ListMarkersWifi.get(0).isVisible()) {
						mDrawerList.getChildAt(7 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						for (int i = 0; i < ListMarkersWifi.size(); i++) {
							ListMarkersWifi.get(i).setVisible(true);
						}
						states[7] = true;
					} else {
						mDrawerList.getChildAt(7 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						for (int i = 0; i < ListMarkersWifi.size(); i++) {
							ListMarkersWifi.get(i).setVisible(false);
						}
						states[7] = false;
					}
				} else {
					mDrawerList.getChildAt(7 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawWifi(true);
					states[7] = true;
				}
				break;
			case 8:
				if (!ListMarkersReports.isEmpty()) {
					if (!ListMarkersReports.get(0).isVisible()) {
						mDrawerList.getChildAt(8 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
						for (int i = 0; i < ListMarkersReports.size(); i++) {
							ListMarkersReports.get(i).setVisible(true);
						}
						states[8] = true;
					} else {
						mDrawerList.getChildAt(8 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_off);
						for (int i = 0; i < ListMarkersReports.size(); i++) {
							ListMarkersReports.get(i).setVisible(false);
						}
						states[8] = false;
					}
				} else {
					mDrawerList.getChildAt(8 - n).setBackgroundResource(R.drawable.drawer_list_item_bg_on);
					drawReports(true);
					states[8] = true;
				}
				break;
		}
	}

	public void openDrawer(View view) {
		mDrawerLayout.openDrawer(Gravity.LEFT);
	}

    /* END DRAWER FUNCTIONALITY */
    /* LOAD MAP AND BASIC LOCATION FUNCTIONALITY */

	private void initializeMap() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(false);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.setInfoWindowAdapter(new InfoWindowActivity(getLayoutInflater()));
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.null_map), Toast.LENGTH_SHORT)
						.show();
			}

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
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 14);
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

			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 14);
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
							CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user_latlng, 14);
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

				hideNotifyButton.start();

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

				// Check if clicked on notifyButton
				clickedMarkerId = marker.getId();

				boolean b = false;

				for (int i = 0; i < ListMarkersReportsIds.size(); i++) {

					if (clickedMarkerId.equals(ListMarkersReportsIds.get(i))) {

						hideNotifyButton.end();

						ObjectAnimator objAnim = ObjectAnimator.ofFloat(notifyButton, "translationY", 0);
						notifyButton.setVisibility(View.VISIBLE);
						objAnim.start();
						b = true;
						clickedMarker = marker;

					}

				}
				if (!b) {
					hideNotifyButton.start();
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

				// Hide notifyButton
				hideNotifyButton.start();

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
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_chegada))
										.title(getString(R.string.chegada)));

								if (tvOrigin.getVisibility() == View.VISIBLE) {
									getRoutes();
								}

							}

						} else {

							if (sAddress.equals("")) {
								etSearch.setText(getString(R.string.marcador_inserido));
							} else {
								etSearch.setText(sAddress);
							}

							btClearSearch.setVisibility(View.VISIBLE);

							// Remove markerSearch, if there's one, and add again.
							if (markerSearch != null) {
								markerSearch.remove();
								markerSearch = null;
							}
							markerSearch = googleMap.addMarker(new MarkerOptions()
									.position(ll));
						}
					}
				}.execute();
			}
		});

		googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				marker.hideInfoWindow();
				hideNotifyButton.start();
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
						addressListBase = geocoder.getFromLocationName(s_address, 5);
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

				Double latLL = -23.863142;
				Double lngLL = -46.942720;
				Double latUR = -23.316943;
				Double lngUR = -46.357698;

				pBarSearch.setVisibility(View.GONE);
				btLupa.setVisibility(View.VISIBLE);

				final ArrayList<Address> addressList = new ArrayList<>();

				// Manual bounding box for addresses
				for (int i = 0; i < addressListBase.size(); i++) {
					addressList.add(addressListBase.get(i));
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
							LatLng coordinate = new LatLng(lat, lng);

							CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
							CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.6f);
							googleMap.moveCamera(center);
							googleMap.animateCamera(zoom);
							markerSearch = googleMap.addMarker(new MarkerOptions()
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
											markerSearch = googleMap.addMarker(new MarkerOptions()
													.position(new LatLng(lat, lng))
													.title(address.getAddressLine(0)));

											// Set the text on etSearch to be the complete address
											String finalStringAddress = address.getAddressLine(0);
											for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
												finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
											}
											etSearch.setText(finalStringAddress);
											btClearSearch.setVisibility(View.VISIBLE);
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
		}.execute();
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
    /* REPORT MANAGING */

	public void openReport(View view) {

		Intent i = new Intent(MainActivity.this, ReportActivity.class);
		if (user_latlng != null) {
			i.putExtra("latitude", user_latlng.latitude);
			i.putExtra("longitude", user_latlng.longitude);
		}
		startActivity(i);
	}

	public void notifySolved(View view) {

		boolean b = false;

		if (isNetworkAvailable()) {
			for (int i = 0; i < ListMarkersReports.size(); i++) {

				if (ListMarkersReports.get(i).isInfoWindowShown()) {
					String timestamp = reportMap.get(ListMarkersReports.get(i).getId());

					NotifySolvedReport notifyObj = new NotifySolvedReport();
					try {
						notifyObj.sendReport(timestamp);
					} catch (Exception e) {
						e.printStackTrace();
					}

					ListMarkersReports.get(i).hideInfoWindow();
					hideNotifyButton.start();

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

    /* END REPORT MANAGING */
    /* SETTING UP INFO FROM DB */

	// Background Async Task to Load all app data
	class CarregarDB extends AsyncTask<String, String, String> {

		// Checa conexão com a internet antes de começar as tarefas em background
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setUpdating();

			if (!isNetworkAvailable()) {
				cancel(true);
			}
		}

		//Retorna todos os estacionamentos da URL e gaurda na ListEstacionamentos (Lista de objetos de classe "Estacionamento")
		protected String doInBackground(String... args) {

			if (!isCancelled()) {

				// Building Parameters
				//List<NameValuePair> params = new ArrayList<>();=

				JSONObject jObjGeral = jParser.makeHttpRequest(url_obter_dados);
				JSONObject jObjBikeSampa = jParser.makeHttpRequest(url_obter_bikesampa);
				JSONObject jObjCicloSampa = jParser.makeHttpRequest(url_obter_ciclosampa);

				if (jObjGeral != null) {

					try {

						int success = jObjGeral.getInt("success");
						if (success == 1) {
							jsonObjString = jObjGeral.toString();

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					editor.putString("jsonObjString", jsonObjString);
					editor.commit();
				} else {
					cancel(true);
				}

				if (jObjBikeSampa != null && !isCancelled()) {

					try {

						int success = jObjBikeSampa.getInt("success");
						if (success == 1) {
							jsonObjBSString = jObjBikeSampa.toString();

							Calendar now = Calendar.getInstance();
							String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
							String minutes = String.valueOf(now.get(Calendar.MINUTE));
							if (minutes.length() == 1) {
								minutes = "0" + minutes;
							}
							updateTimeBS = hours + ":" + minutes;

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					editor.putString("jsonObjBSString", jsonObjBSString);
					editor.putString("updateTimeBS", updateTimeBS);
					editor.commit();
				} else {
					cancel(true);
				}

				if (jObjCicloSampa != null && !isCancelled()) {

					try {

						int success = jObjCicloSampa.getInt("success");
						if (success == 1) {
							jsonObjCSString = jObjCicloSampa.toString();

							Calendar now = Calendar.getInstance();
							String hours = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
							String minutes = String.valueOf(now.get(Calendar.MINUTE));
							if (minutes.length() == 1) {
								minutes = "0" + minutes;
							}
							updateTimeCS = hours + ":" + minutes;

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					editor.putString("jsonObjCSString", jsonObjCSString);
					editor.putString("updateTimeCS", updateTimeCS);
					editor.commit();
				} else {
					cancel(true);
				}

			}

			return null;
		}

		// Adiciona marcadores no mapa para todos os objetos encontrados.
		protected void onPostExecute(String file_url) {

			try {
				createBaseArrays();

			} catch (JSONException e) {
				e.printStackTrace();
			}

			//resetUpdating();
		}

		protected void onCancelled(String file_url) {
			resetUpdating();

			AlertDialog.Builder network_alert = new AlertDialog.Builder(MainActivity.this);
			network_alert.setTitle(getString(R.string.network_alert_title))
					.setMessage(getString(R.string.network_update_alert_dialog))
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

	public void createBaseArrays() throws JSONException {

		// Bike Sampa

		createBikeSampaArray();

		// Ciclo Sampa

		createCicloSampaArray();

		// Dados gerais

		if (jsonObjString != null) {

			JSONObject jsonObj = new JSONObject(jsonObjString);
			JSONArray jsonRespostaArray = jsonObj.getJSONArray("Resposta");

			try {

				ParquesJSArray = jsonRespostaArray.getJSONObject(6).getJSONArray("PARQUES");

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

				BicicletariosJSArray = jsonRespostaArray.getJSONObject(3).getJSONArray("BICICLETARIOS");

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

				WifiJSArray = jsonRespostaArray.getJSONObject(8).getJSONArray("WIFI");

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
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_wifi))
							.snippet(end)
							.position(new LatLng(latitude, longitude));

					ListWifi.add(item_wifi);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				ReportsJSArray = jsonRespostaArray.getJSONObject(7).getJSONArray("REPORTS");

				//Clear list before adding updated items
				ListReports.clear();

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

					ListReports.add(report);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray cicloviasJSArray = jsonRespostaArray.getJSONObject(0).getJSONArray("CICLOVIAS");

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

				mMenuQuantidades[0] = cicloviasJSArray.getJSONObject(0).getJSONArray("ciclovia").getJSONObject(1).getString("distancia") + " km";

			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray ciclofaixasJSArray = jsonRespostaArray.getJSONObject(1).getJSONArray("CICLOFAIXAS");

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

				mMenuQuantidades[1] = ciclofaixasJSArray.getJSONObject(0).getJSONArray("ciclofaixa").getJSONObject(1).getString("distancia") + " km";

			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {

				JSONArray ciclorrotasJSArray = jsonRespostaArray.getJSONObject(2).getJSONArray("CICLORROTAS");

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

				mMenuQuantidades[2] = ciclorrotasJSArray.getJSONObject(0).getJSONArray("ciclorrota").getJSONObject(1).getString("distancia") + " km";

			} catch (JSONException e) {
				e.printStackTrace();
			}

			mMenuQuantidades[4] = String.valueOf(ListEstacoesBRA.size());
			mMenuQuantidades[5] = String.valueOf(ListParques.size());
			mMenuQuantidades[6] = String.valueOf(ListBicicletarios.size());
			mMenuQuantidades[7] = String.valueOf(ListWifi.size());
			mMenuQuantidades[8] = String.valueOf(ListReports.size());
			myAdapter.notifyDataSetChanged();

			drawPermanentes(states[0]);

			drawTemporarias(states[1]);

			drawPreferenciais(states[2]);

			drawCicloSampa(states[4]);

			drawParques(states[5]);

			drawBicicletarios(states[6]);

			drawWifi(states[7]);

			drawReports(states[8]);

            resetUpdating();
		}

	}

	public void createBikeSampaArray() throws JSONException {

		if (jsonObjBSString != null) {

			JSONObject jsonObjBS = new JSONObject(jsonObjBSString);

			try {

				BSjSonArray = jsonObjBS.getJSONArray("ESTACOES_ITAU");

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

					mMenuQuantidades[3] = String.valueOf(ListEstacoesITAU.size());
					myAdapter.notifyDataSetChanged();

					drawBikeSampa(states[3]);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public void createCicloSampaArray() throws JSONException {

		if (jsonObjCSString != null) {

			JSONObject jsonObjCS = new JSONObject(jsonObjCSString);

			try {

				CSjSonArray = jsonObjCS.getJSONArray("ESTACOES_BRADESCO");

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

					mMenuQuantidades[4] = String.valueOf(ListEstacoesBRA.size());
					myAdapter.notifyDataSetChanged();

					drawCicloSampa(states[4]);
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
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.tree))
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
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ciclosampa_vazia));
				} else if (vagasLivres == 0) {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ciclosampa_cheia));
				} else {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ciclosampa_operando));
				}

			} else if (estacao.status1.equals("I") && estacao.status2.equals("EO")) {
				estacaoMOpt.snippet(getString(R.string.offline)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ciclosampa_offline));
			} else {
				estacaoMOpt.snippet(getString(R.string.em_manutencao_implantacao)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeCS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ciclosampa_manutencao));
			}

			ListMarkersBRA.add(googleMap.addMarker(estacaoMOpt));
		}
	}

	public void drawBikeSampa(Boolean visibility) {

		if (ListMarkersITAU != null) {
			for (Marker marker : ListMarkersITAU) marker.remove();
		}

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
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_bikesampa_vazia));
				} else if (vagasLivres == 0) {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_bikesampa_cheia));
				} else {
					estacaoMOpt.snippet(getString(R.string.em_operacao)
							+ newline + newline + estacao.Descricao
							+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
							+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
							+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_bikesampa_operando));
				}

			} else if (estacao.status1.equals("I") && estacao.status2.equals("EO")) {
				estacaoMOpt.snippet(getString(R.string.offline)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_bikesampa_offline));
			} else {
				estacaoMOpt.snippet(getString(R.string.em_manutencao_implantacao)
						+ newline + newline + estacao.Descricao
						+ newline + getString(R.string.bikes_disponiveis) + " " + estacao.bikes
						+ newline + getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + getString(R.string.atualizado_as) + " " + updateTimeBS)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_bikesampa_manutencao));
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

                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bicicletario));
				marker.setAnchor(0.5f, 0.5f);
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

				marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.paraciclo_new));
				marker.setAnchor(0.5f, 1.0f);
				marker.setSnippet(getString(R.string.endereco)+": " + ListBicicletarios.get(i).address
                        + newline + getString(R.string.vagas) + " " + ListBicicletarios.get(i).Vagas);
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
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_wifi))
							.title(ListWifi.get(i).getTitle())
							.snippet(ListWifi.get(i).getSnippet())
							.visible(visibility)
							.anchor(0.5f, 0.5f)));
		}
	}

	public void drawReports(Boolean visibility) {

		if (ListMarkersReports != null) {
			for (Marker marker : ListMarkersReports) marker.remove();
		}

		if (ListMarkersReportsIds != null) {
			ListMarkersReportsIds.clear();
		}

		if (reportMap != null) {
			reportMap.clear();
		}

		hideNotifyButton.start();

		for (int i = 0; i < ListReports.size(); i++) {
			// Aqui eu não adiciono o MarkerOptions inteiro de uma vez porque dava o erro esquisito do IObectjWrapper. 
			// Criando um MarkerOptions novo e puxando atributo por atributo da ListWifi não deu erro, então deve ficar assim.

			MarkerOptions mOptions = new MarkerOptions()
					.snippet(ListReports.get(i).Endereco + newline + getString(R.string.details) + " " + ListReports.get(i).Descricao + newline + getString(R.string.alerta_em) + " " + ListReports.get(i).timestamp)
					.position(new LatLng(ListReports.get(i).Lat, ListReports.get(i).Lng))
					.visible(visibility)
					.anchor(0.5f, 1.0f);

			if (ListReports.get(i).Tipo.equals("bu")) {
				mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_buraco));
				mOptions.title(getString(R.string.via_esburacada));
			} else if (ListReports.get(i).Tipo.equals("si")) {
				mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_sinalizacao));
				mOptions.title(getString(R.string.problema_sinalizacao));
			} else {
				mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_outro));
				mOptions.title(getString(R.string.alerta));
			}

			Marker marker = googleMap.addMarker(mOptions);
			reportMap.put(marker.getId(), ListReports.get(i).timestamp);
			ListMarkersReports.add(marker);
			ListMarkersReportsIds.add(marker.getId());
		}
	}

	public void drawPermanentes(Boolean visibility) {

		if (!ListCiclovias.isEmpty()) {
			pinheiros1marker = googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(-23.69560, -46.68497))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_acesso))
					.title(getString(R.string.acesso_ciclovia_marginal))
					.anchor(0.5f, 0.5f)
					.visible(visibility));

			pinheiros2marker = googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(-23.677504, -46.702454))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_acesso))
					.title(getString(R.string.acesso_ciclovia_marginal))
					.anchor(0.5f, 0.5f)
					.visible(visibility));

			pinheiros3marker = googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(-23.655609, -46.719791))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_acesso))
					.title(getString(R.string.acesso_ciclovia_marginal))
					.anchor(0.5f, 0.5f)
					.visible(visibility));

			pinheiros4marker = googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(-23.593094, -46.692753))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_acesso))
					.title(getString(R.string.acesso_ciclovia_marginal))
					.anchor(0.5f, 0.5f)
					.visible(visibility));

			pinheiros5marker = googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(-23.558264, -46.711334))
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_acesso))
					.title(getString(R.string.acesso_ciclovia_marginal))
					.anchor(0.5f, 0.5f)
					.visible(visibility));

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

	public void refreshData(final MenuItem item) {

		//item.setActionView(R.layout.progress_bar);
		setUpdating();

		// Trigger CarregarDB
		new CarregarDB().execute();
	}

	public void setUpdating() {
		if (menu_item != null) {
			menu_item.setActionView(R.layout.progress_bar);
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
				editor.putBoolean("betaRouteWarningWasShown", true);
				editor.apply();
			} else {
				turnOnRouteMode();
			}
		} else {
			cancelRouteMode();
		}
	}

	public void turnOnRouteMode() {

		isRouteModeOn = true;

		if (markerSearch != null) {
			String destination_string = etSearch.getText().toString();
			LatLng destination_latlng = markerSearch.getPosition();

			// Change searchHeader for routeHeader
			header.removeAllViews();
			header.addView(routeHeader);

			tvDestination.setText(destination_string);

			markerSearch.remove();
			markerSearch = null;

			if (markerDestination != null) {
				markerDestination.remove();
			}

			markerDestination = googleMap.addMarker(new MarkerOptions()
					.position(destination_latlng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_chegada))
					.title(getString(R.string.chegada)));

			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
			final AlertDialog alert = alertBuilder.create();
			View alertView = getLayoutInflater().inflate(R.layout.ad_from_which_location, null);
			alert.setView(alertView);
			alert.setCancelable(false);
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

	public void cancelRouteMode() {

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
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_chegada))
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
														.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_chegada))
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
		;

		// For each other cycling path stored on cyclingPathList
		for (int i = 0; i < cyclingPathList.size(); i++) {

			CyclingPath cp = cyclingPathList.get(i);

			/** Create line graph series to the current cyclingPath, if there's. Add to list. */

			if (!cp.referenceDistances.isEmpty() && !cp.pathElevation.isEmpty()) {

				// aChartEngine
				XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
				XYSeries xySeries = new XYSeries("Minha série");

				// Get cyclingPath's elevations list
				ArrayList<Double> elevations = cp.pathElevation;

				// Get cyclingPath's reference distances list
				ArrayList<Double> referenceDistances = cp.referenceDistances;
				// Add each elevation point to the elevationSeries1 variable
				for (int y = 0; y < elevations.size(); y++) {

					//aChartEngine
					xySeries.add(referenceDistances.get(y), elevations.get(y));

				}

				//aChartEngine
				mDataset.addSeries(xySeries);

				final GraphicalView gView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
				gView.setClickable(true);
				gView.setPadding(0, 0, 0, 0);

				gView.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// handle the click event on the chart
						SeriesSelection seriesSelection = gView.getCurrentSeriesAndPoint();
						if (seriesSelection == null) {
							//Toast.makeText(getApplicationContext(), "Nenhum ponto clicado", Toast.LENGTH_SHORT).show();
						} else {

							if (graph_point_marker != null) {
								graph_point_marker.remove();
							}

							Double clickedRefDistance = seriesSelection.getXValue();

							graph_point_marker = googleMap.addMarker(new MarkerOptions()
											.position(selectedCyclingPath.get(0).getLatLngFromRefDistance(clickedRefDistance))
											.anchor(0.5f, 0.5f)
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_ball))
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
				});

				graphViewArray.add(gView);
			}

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

		swipeFragmentPagerAdapter = new SwipeFragmentPagerAdapter(getSupportFragmentManager());

		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(swipeFragmentPagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
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
			editor.putBoolean("elevGraphExpWasShown", true);
			editor.apply();
		}

	}

	public static class SwipeFragmentPagerAdapter extends FragmentPagerAdapter {
		public SwipeFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return cyclingPathList.size();
		}

		@Override
		public Fragment getItem(int position) {
			//SwipeFragment fragment = new SwipeFragment();
			return SwipeFragment.newInstance(position);
		}
	}

	public static class SwipeFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			// Get views
			View swipeView = inflater.inflate(R.layout.route_detail_fragment, container, false);
			TextView timeTV = (TextView) swipeView.findViewById(R.id.route_line_time);
			TextView distanceTV = (TextView) swipeView.findViewById(R.id.route_line_distance);
			TextView inclinationTV = (TextView) swipeView.findViewById(R.id.route_line_inclination);

			// Get current position
			Bundle args = getArguments();
			int position = args.getInt("position");

			aChartParentView = (LinearLayout) swipeView.findViewById(R.id.aChart);
			aChartParentView.removeView(graphViewArray.get(position));

			if (!graphViewArray.isEmpty()) {
				aChartParentView.addView(graphViewArray.get(position), 0);
				// Aqui tava dando o problema de "Child already has a parent".
				// Tentei resolver tornando aChartParentView uma variável única (declarada no início do app).
				// Antes, um novo Linear Layout era sempre criado nesse método.
			} else {
				TextView textView = (TextView) swipeView.findViewById(R.id.textViewChartWarn);
				textView.setVisibility(View.VISIBLE);
			}

			timeTV.setText(cyclingPathList.get(position).getEstimatedTime() + " min");
			distanceTV.setText(cyclingPathList.get(position).totalDistance + " km");
			inclinationTV.setText(cyclingPathList.get(position).maxInclination + "°");
			// cyclingPathList.get(position).referenceDistanceForMaxInclination + " km");

			return swipeView;
		}

		static SwipeFragment newInstance(int position) {
			SwipeFragment swipeFragment = new SwipeFragment();
			Bundle args = new Bundle();
			args.putInt("position", position);
			swipeFragment.setArguments(args);
			return swipeFragment;
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
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_chegada))
					.title(getString(R.string.chegada)));

		}
	}

    /* END ROUTING */
    /* MISCELLANEOUS */

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
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

	public static class ViewHolder {
		public TextView text;
		public TextView description;
		public TextView quantidade;
		public ImageView image;
		public View background;
		public int position;
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

					// Request location updates
					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					} else {
						locationManager.requestLocationUpdates(bestAvailableProvider, 0, 0, this);
						Location user_loc = locationManager.getLastKnownLocation(bestAvailableProvider);
						user_latlng = new LatLng(user_loc.getLatitude(), user_loc.getLongitude());
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
			if (locationManager.isProviderEnabled(bestAvailableProvider)) {

				locationManager.requestLocationUpdates(bestAvailableProvider, 0, 0, this);
			} else {
				Toast.makeText(this, getString(R.string.loc_verifique_gps), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		MyApplication.activityPaused();

		editor.putBoolean("states0", states[0]);
		editor.putBoolean("states1", states[1]);
		editor.putBoolean("states2", states[2]);
		editor.putBoolean("states3", states[3]);
		editor.putBoolean("states4", states[4]);
		editor.putBoolean("states5", states[5]);
		editor.putBoolean("states6", states[6]);
		editor.putBoolean("states7", states[7]);
		editor.putBoolean("states8", states[8]);
		editor.commit();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			locationManager.removeUpdates(this);
		}

	}

	protected void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {

		//long start = System.currentTimeMillis();

		states = savedInstanceState.getBooleanArray("BUTTONSTATE");
		current_latlng = savedInstanceState.getDoubleArray("LATLNG");
		LatLng position = new LatLng(current_latlng[0], current_latlng[1]);
		zoom = savedInstanceState.getFloat("ZOOM");
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoom);
		googleMap.moveCamera(cameraUpdate);
		if (savedInstanceState.getBoolean("isDrawerOpen")) {
			mDrawerLayout.openDrawer(Gravity.LEFT); }



		ListCiclovias = savedInstanceState.getParcelableArrayList("CICLOVIAS_LIST");

		ListCiclofaixas = savedInstanceState.getParcelableArrayList("CICLOFAIXAS_LIST");

		ciclorrotasOptionsList = savedInstanceState.getParcelableArrayList("CICLORROTAS_LIST");

		ListEstacoesITAU = savedInstanceState.getParcelableArrayList("ITAU_LIST");

		ListEstacoesBRA = savedInstanceState.getParcelableArrayList("BRA_LIST");

		ListParques = savedInstanceState.getParcelableArrayList("PARQUES_LIST");

		ListBicicletarios = savedInstanceState.getParcelableArrayList("BICICLETARIOS_LIST");

		ListWifi = savedInstanceState.getParcelableArrayList("WIFI_LIST");

		ListReports = savedInstanceState.getParcelableArrayList("REPORTS_LIST");

		if (states[0]) {drawPermanentes(true);}
		if (states[1]) {drawTemporarias(true);}
		if (states[2]) {drawPreferenciais(true);}
		if (states[3]) {drawBikeSampa(true);}
		if (states[4]) {drawCicloSampa(true);}
		if (states[5]) {drawParques(true);}
		if (states[6]) {drawBicicletarios(true);}
		if (states[7]) {drawWifi(true);}
		if (states[8]) {drawReports(true);}

		// Demorava 3 segs pra desenhar todos os arrays
		/*
		drawPermanentes(states[0]);
		drawTemporarias(states[1]);
		drawPreferenciais(states[2]);
		drawBikeSampa(states[3]);
		drawCicloSampa(states[4]);
		drawParques(states[5]);
		drawBicicletarios(states[6]);
		drawWifi(states[7]);
		drawReports(states[8]);*/

		isRouteModeOn = savedInstanceState.getBoolean("isRouteModeOn");

		if (isRouteModeOn) {
			header.removeAllViews();
			header.addView(routeHeaderView);

			Log.d("ROUTEMODE:", isRouteModeOn.toString());

			if (savedInstanceState.getBoolean("tvOriginVIS")) {
				etOrigin.setVisibility(View.GONE);
				tvOrigin.setVisibility(View.VISIBLE);
				tvOrigin.setText(savedInstanceState.getString("tvOriginSTRING"));

				Log.d("TVORIGIN", String.valueOf(savedInstanceState.getBoolean("tvOriginVIS")));

			} else {
				tvOrigin.setVisibility(View.GONE);
				etOrigin.setVisibility(View.VISIBLE);
				etOrigin.setText(savedInstanceState.getString("etOriginSTRING"));}

			if (savedInstanceState.getBoolean("tvDestinationVIS")) {
				etDestination.setVisibility(View.GONE);
				tvDestination.setVisibility(View.VISIBLE);
				tvDestination.setText(savedInstanceState.getString("tvDestinationSTRING"));

				Log.d("TVDESTINATION:", String.valueOf(savedInstanceState.getBoolean("tvDestinationVIS")));

			} else {
				tvDestination.setVisibility(View.GONE);
				etDestination.setVisibility(View.VISIBLE);
				etDestination.setText(savedInstanceState.getString("etDestinationSTRING"));}

			if (savedInstanceState.getBoolean("viewPagerVIS")) {
				cyclingPathList = savedInstanceState.getParcelableArrayList("cyclingPathList");
				selectedCyclingPath = savedInstanceState.getParcelableArrayList("selectedCyclingPath");
				viewPagerPosition = savedInstanceState.getInt("viewPagerPOSITION");
				drawRoutes();
				if (savedInstanceState.getBoolean("graph_point_markerVIS")) {
					String[] s = savedInstanceState.getStringArray("graph_point_markerINFO");
					graph_point_marker = googleMap.addMarker(new MarkerOptions()
									.position((LatLng) savedInstanceState.getParcelable("graph_point_markerLATLNG"))
									.anchor(0.5f, 0.5f)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_ball))
									.title(s[0])
									.snippet(s[1])
					);
				}
			}
		}

		/*long end = System.currentTimeMillis();
		long elapsed = end - start;
		Log.d("onRestoreInstanceState", String.valueOf(elapsed));*/

	}

	public void onSaveInstanceState (Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		boolean bra_bool;
		boolean itau_bool;
		boolean parques_bool;
		boolean bicicletarios_bool;
		boolean wifi_bool;
		boolean reports_bool;
		boolean ciclovias_bool;
		boolean ciclofaixas_bool;
		boolean ciclorrotas_bool;

		double[] latlng = {googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude};
		savedInstanceState.putDoubleArray("LATLNG", latlng);
		savedInstanceState.putFloat("ZOOM", googleMap.getCameraPosition().zoom);
		savedInstanceState.putParcelableArrayList("ITAU_LIST", ListEstacoesITAU);
		savedInstanceState.putParcelableArrayList("BRA_LIST", ListEstacoesBRA);
		savedInstanceState.putParcelableArrayList("PARQUES_LIST", ListParques);
		savedInstanceState.putParcelableArrayList("BICICLETARIOS_LIST", ListBicicletarios);
		savedInstanceState.putParcelableArrayList("CICLOVIAS_LIST", ListCiclovias);
		savedInstanceState.putParcelableArrayList("CICLOFAIXAS_LIST", ListCiclofaixas);
		savedInstanceState.putParcelableArrayList("CICLORROTAS_LIST", ciclorrotasOptionsList);
		savedInstanceState.putParcelableArrayList("WIFI_LIST", ListWifi);
		savedInstanceState.putParcelableArrayList("REPORTS_LIST", ListReports);

		savedInstanceState.putBoolean("isDrawerOpen", mDrawerLayout.isDrawerOpen(Gravity.LEFT));

		// Save visibility of each item
		if (!ListMarkersBRA.isEmpty()) {bra_bool = ListMarkersBRA.get(0).isVisible();} else {bra_bool = false;}
		if (!ListMarkersITAU.isEmpty()) {itau_bool = ListMarkersITAU.get(0).isVisible();} else {itau_bool = false;}
		if (!ListMarkersParques.isEmpty()) {parques_bool = ListMarkersParques.get(0).isVisible();} else {parques_bool = false;}
		if (!ListMarkersBicicletarios.isEmpty()) {bicicletarios_bool = ListMarkersBicicletarios.get(0).isVisible();} else {bicicletarios_bool = false;}
		if (!cicloviasLineList.isEmpty()) {ciclovias_bool = cicloviasLineList.get(0).isVisible();} else {ciclovias_bool = false;}
		if (!ciclofaixasLineList.isEmpty()) {ciclofaixas_bool = ciclofaixasLineList.get(0).isVisible();} else {ciclofaixas_bool = false;}
		if (!ciclorrotasLineList.isEmpty()) {ciclorrotas_bool = ciclorrotasLineList.get(0).isVisible();} else {ciclorrotas_bool = false;}
		if (!ListMarkersWifi.isEmpty()) {wifi_bool = ListMarkersWifi.get(0).isVisible();} else {wifi_bool = false;}
		if (!ListMarkersReports.isEmpty()) {reports_bool = ListMarkersReports.get(0).isVisible();} else {reports_bool = false;}

		boolean[] buttonsState = {ciclovias_bool, ciclofaixas_bool,
				ciclorrotas_bool, itau_bool, bra_bool, parques_bool, bicicletarios_bool, wifi_bool, reports_bool};

		savedInstanceState.putBooleanArray("BUTTONSTATE", buttonsState);

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

}