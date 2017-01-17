package jannini.android.ciclosp.NetworkRequests;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;

import jannini.android.ciclosp.R;

/**
 * Created by cauejannini on 04/07/16.
 */
public class Utils {

    public Utils() {
    }

    public static String newline = System.getProperty("line.separator");

    public static void showNetworkAlertDialog (final Context context) {
        AlertDialog.Builder network_alert = new AlertDialog.Builder(context);
        network_alert.setTitle(context.getString(R.string.network_alert_title))
                .setMessage(context.getString(R.string.network_alert_dialog))
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(context.getString(R.string.network_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        network_alert.show();
    }

    public static void showSimpleAlertDialog (final Context context, String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        alert.show();
    }

    public static float getPixelValue (Context c, float value) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (value * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}




