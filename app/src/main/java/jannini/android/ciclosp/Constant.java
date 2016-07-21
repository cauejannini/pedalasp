package jannini.android.ciclosp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cauejannini on 23/06/16.
 */
public class Constant {

    public static LatLng latLngCity = new LatLng(-23.550765, -46.630437);
    public static double llLat = -24.124366;
    public static double llLng = -47.059433;
    public static double urLat = -23.215394;
    public static double urLng = -46.164888;

    public static String url_obter_dados = "http://pedalaspapp.com.br/dbaccess/get_data.php";
    public static String url_obter_bikesampa = "http://pedalaspapp.com.br/dbaccess/obter_bikesampa.php";
    public static String url_obter_ciclosampa = "http://pedalaspapp.com.br/dbaccess/obter_ciclosampa.php";
    public static String url_report = "http://pedalaspapp.com.br/dbaccess/report_bikelane_problem.php";
    public static String url_add_estabelecimento = "http://pedalaspapp.com.br/dbaccess/add_estabelecimento.php";
    public static String url_send_email = "http://pedalaspapp.com.br/dbaccess/send_email.php";
    public static String url_notify_solved = "http://pedalaspapp.com.br/dbaccess/notify_solved_report.php";

    public static String spJobGeral = "jannini.android.ciclosp.jsonGeralString";
    public static String spJobBS = "jannini.android.ciclosp.jsonBSString";
    public static String spJobCS = "jannini.android.ciclosp.jsonCSString";

    public static String spUpdateTimeBS = "jannini.android.ciclosp.updateTimeBS";
    public static String spUpdateTimeCS = "jannini.android.ciclosp.updateTimeCS";

}
