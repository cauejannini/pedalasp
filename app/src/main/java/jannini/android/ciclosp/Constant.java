package jannini.android.ciclosp;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by cauejannini on 23/06/16.
 */
public class Constant {

    public static String SPKEY_DEVICE_ID = "jannini.android.ciclosp.SPKEY_DEVICE_ID";

    public static LatLng latLngCity = new LatLng(-23.550765, -46.630437);
    public static double llLat = -24.124366;
    public static double llLng = -47.059433;
    public static double urLat = -23.215394;
    public static double urLng = -46.164888;

    public static String url_obter_dados = "http://pedalaspapp.com.br/dbaccess/get_data.php";
    public static String url_obter_bikesampa = "http://pedalaspapp.com.br/dbaccess/obter_bikesampa.php";
    public static String url_obter_ciclosampa = "http://pedalaspapp.com.br/dbaccess/obter_ciclosampa.php";
    public static String url_report = "http://pedalaspapp.com.br/dbaccess/report_bikelane_problem.php";
    public static String url_send_parkedHere = "http://pedalaspapp.com.br/dbaccess/send_parkedHere.php";
    public static String url_send_email = "http://pedalaspapp.com.br/dbaccess/send_email.php";
    public static String url_notify_solved = "http://pedalaspapp.com.br/dbaccess/notify_solved_report.php";
    public static String url_send_originDestination = "http://pedalaspapp.com.br/dbaccess/send_origin_destination.php";
    public static String url_create_device = "http://pedalaspapp.com.br/dbaccess/create_device.php";
    public static String urlGetElevationForUrls = "http://www.pedalaspapp.com.br/dbaccess/get_elevations.php";
    public static String urlGetDirections = "http://www.pedalaspapp.com.br/dbaccess/get_directions.php";
    public static String url_add_place = "http://pedalaspapp.com.br/dbaccess/add_place.php";
    public static String url_get_places_icons_paths = "http://pedalaspapp.com.br/dbaccess/get_places_icons_paths.php";
    public static String url_get_categories = "http://pedalaspapp.com.br/dbaccess/get_categories.php";
    public static String baseurl_images = "http://pedalaspapp.com.br/images/";
    public static String url_get_places = "http://pedalaspapp.com.br/dbaccess/get_places.php";
    public static String url_add_paraciclo = "http://pedalaspapp.com.br/dbaccess/add_paraciclo.php";
    public static String urlGetImageForPlaceId = "http://pedalaspapp.com.br/dbaccess/get_image_for_place_id.php";
    public static String url_get_deal_list_for_location = "http://pedalaspapp.com.br/dbaccess/get_deal_list_for_location.php";
    public static String url_get_deal_for_id = "http://pedalaspapp.com.br/dbaccess/get_deal_for_id.php";
    public static String url_get_deal_list_for_place_id = "http://pedalaspapp.com.br/dbaccess/get_deal_list_for_place_id.php";
    public static String url_get_all_deals = "http://pedalaspapp.com.br/dbaccess/get_all_deals.php";
    public static String url_get_op_hours_for_place_id = "http://pedalaspapp.com.br/dbaccess/get_place_ophours.php";
    public static String url_update_place_information = "http://pedalaspapp.com.br/dbaccess/update_place_information.php";
    public static String url_flag_place_inexistent = "http://pedalaspapp.com.br/dbaccess/flag_place_inexistent.php";
    public static String url_get_voucher_for_user_id = "http://pedalaspapp.com.br/dbaccess/voucher_get_for_user_id.php";

    public static String spJobGeral = "jannini.android.ciclosp.jsonGeralString";
    public static String spJobBS = "jannini.android.ciclosp.jsonBSString";
    public static String spJobCS = "jannini.android.ciclosp.jsonCSString";
    public static String spJobPlaces = "jannini.android.ciclosp.jsonPlacesString";
    public static String spParkedHereListSize = "jannini.android.ciclosp.spParkedHereListSize";
    public static String spParkedHereLat = "jannini.android.ciclosp.parkedHereLat";
    public static String spParkedHereLng = "jannini.android.ciclosp.parkedHereLng";

    public static String spUpdateTimeBS = "jannini.android.ciclosp.updateTimeBS";
    public static String spUpdateTimeCS = "jannini.android.ciclosp.updateTimeCS";

    public static String dontWarnAgainTooMuchMarkers = "jannini.android.ciclosp.dontWarnAgainTooMuchMarkers";

    public static String SPKEY_SHARED_PREFERENCES = "jannini.android.ciclosp.SHARED_PREFERENCES";
    public static String SPKEY_ROUTE_PRIORITY = "jannini.android.ciclosp.ROUTE_PRIORITY";
    public static String PRIORITY_MOST_BIKE_LANES = "mostBikeLanes";
    public static String PRIORITY_FASTEST = "fastest";
    public static String PRIORITY_FLATTEST= "flattest";
    public static String SPKEY_USER_NAME = "jannini.android.ciclosp.USER_NAME";
    public static String SPKEY_USER_EMAIL = "jannini.android.ciclosp.USER_EMAIL";

    public static boolean[] States = {true, true, true, true, true, true, true};
    public static boolean[] BikeLanesStates = {true, true, true};
    public static String SPKEY_BikeLaneStates0 = "jannini.android.ciclosp.BikeLaneStates0";
    public static String SPKEY_BikeLaneStates1 = "jannini.android.ciclosp.BikeLaneStates1";
    public static String SPKEY_BikeLaneStates2 = "jannini.android.ciclosp.BikeLaneStates2";
    public static boolean[] SharingSystemsStates = {true, true};
    public static String SPKEY_SharingSystemsStates0 = "jannini.android.ciclosp.SharingSystemsStates0";
    public static String SPKEY_SharingSystemsStates1 = "jannini.android.ciclosp.SharingSystemsStates1";


    public static HashMap<Integer, Bitmap> mapPlaceIcon = new HashMap<>();
    public static HashMap<Integer, String> mapPlaceCategories = new HashMap<>();
    public static HashMap<Integer, Boolean> PlaceCategoriesStates = new HashMap<>();
    public static HashMap<Integer, Bitmap> mapCategoriesIcons = new HashMap<>();
    public static String SPKEY_NUMBER_OF_STORED_CATEGORIES = "jannini.android.ciclosp.NUMBER_OF_CATEGORIES";
    public static String SPKEY_PLACE_CATEGORIES_IDS = "jannini.android.ciclosp.PLACE_CATEGORIES_IDS";
    public static String SPKEY_PLACE_CATEGORIES_STATES = "jannini.android.ciclosp.PLACE_CATEGORIES_STATES";

    public static int distanceBetweenElevationSamples = 50;
    public static int routeIntersectionTolerance = 45;
    public static float bikeLaneWidth = 1.5f;
    public static float selectedPolylineWidth = 7f;
    public static float unSelectedPolylineWidth = 7f;
    public static int ZOOM_FOR_UNVERIFIED_PLACES = 10;

    //public static final String[] PlaceServices = {"NEW_BIKES","USED_BIKES","WORKSHOP","PARTS","ACCESSORIES","SHOWER","COFFEE","RESTAURANT","BAR"};
    //public static final int[] PlaceServicesImages = {R.drawable.ic_store, R.drawable.ic_store, R.drawable.ic_workshop, R.drawable.ic_store,R.drawable.ic_store, R.drawable.ic_shower, R.drawable.ic_coffee,R.drawable.ic_store,R.drawable.ic_store};

    public static String IEXTRA_PLACE_ID_INT = "PLACE_ID_INT";
    public static String IEXTRA_PLACE_NAME = "PLACE_NAME";
    public static String IEXTRA_PLACE_SERVICES = "PLACE_SERVICES";
    public static String IEXTRA_PLACE_PHONE = "PLACE_PHONE";
    public static String IEXTRA_PLACE_PUBLIC_EMAIL = "PLACE_PUBLIC_EMAIL";
    public static String IEXTRA_PLACE_HAS_DEALS = "PLACE_HAS_DEAL";
    public static String IEXTRA_PLACE_CATEGORY_ID_LIST = "PLACE_CATEGORY_ID_LIST";
    public static String IEXTRA_PLACE_ADDRESS = "PLACE_ADDRESS";
    public static String IEXTRA_PLACE_SHORT_DESC = "PLACE_SHORT_DESC";
    public static String IEXTRA_PLACE_LAT_DOUBLE = "PLACE_LAT";
    public static String IEXTRA_PLACE_LNG_DOUBLE = "PLACE_LNG";
    public static String IEXTRA_DEAL_ADDRESS = "DEAL_ADDRESS";
    public static String IEXTRA_DEAL_LAT_DOUBLE = "DEAL_LAT";
    public static String IEXTRA_DEAL_LNG_DOUBLE = "DEAL_LNG";
    public static String IEXTRA_VOUCHER_JSON = "VOUCHER_JSON";

    public final static String ICODE_DEAL_LIST = "ICODE_DEAL_LIST";
    public final static String IEXTRA_ICODE_DEAL_LIST_FROM_PLACE = "DEAL_LIST_FROM_PLACE";
    public final static String IEXTRA_ICODE_DEAL_LIST_FROM_USER_LOCATION = "DEAL_LIST_FROM_USER_LOCATION";
    public final static String IEXTRA_ICODE_DEAL_LIST_ALL = "DEAL_LIST_ALL";

    public static final int REQUEST_CODE_ROUTE_FOR_PLACE = 0;
    public static final int REQUEST_CODE_ROUTE_FOR_DEAL = 1;

    public static final int PERMISSION_REQUEST_CODE_CALL_PHONE = 4;

    public static final String API_RESPONSE_CODE_VOUCHER_OK = "VOUCHER_OK";
    public static final String API_RESPONSE_CODE_VOUCHER_LIMIT_REACHED_FOR_DEAL = "VOUCHER_LIMIT_REACHED_FOR_DEAL";
    public static final String API_RESPONSE_CODE_VOUCHER_LIMIT_REACHED_FOR_USER = "VOUCHER_LIMIT_REACHED_FOR_USER";

    public static int DURATION_BOTTOM_PANEL_ANIMATION = 250;

    public static String elevationAuthKey = "AIzaSyCjD8Rpz0_cOWredDecWSx1DKPX2GN1mhU";

}
