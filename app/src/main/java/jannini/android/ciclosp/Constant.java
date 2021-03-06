package jannini.android.ciclosp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cauejannini on 23/06/16.
 */
public class Constant {

    public static String deviceID = "jannini.android.ciclosp.deviceID";

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
    public static String urlGetElevationForUrls = "http://www.pedalaspapp.com.br/dbaccess/get_elevations_2.php";
    public static String urlGetDirections = "http://www.pedalaspapp.com.br/dbaccess/get_directions.php";

    public static String spJobGeral = "jannini.android.ciclosp.jsonGeralString";
    public static String spJobBS = "jannini.android.ciclosp.jsonBSString";
    public static String spJobCS = "jannini.android.ciclosp.jsonCSString";
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

    public static boolean[] states = {true, true, true, true, true, true};
    public static boolean[] bikeLanesStates = {true, true, true};
    public static boolean[] sharingSystemsStates = {true, true};

    public static int totalMaxDistanceForLower = 25000;
    public static int distanceBetweenElevationSamplesLower = 50;
    public static int distanceBetweenElevationSamplesHigher = 75;
    public static int routeIntersectionTolerance = 45;
    public static float bikeLaneWidth = 1.5f;
    public static float selectedPolylineWidth = 7f;
    public static float unSelectedPolylineWidth = 7f;

    public static String elevationAuthKey = "AIzaSyCjD8Rpz0_cOWredDecWSx1DKPX2GN1mhU";

}
