package jannini.android.ciclosp.NetworkRequests;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import jannini.android.ciclosp.R;

/**
 * Created by cauejannini on 04/07/16.
 */
public class Utils {

    public Utils() {
    }

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

    public static void showConfirmAlertDialog (final Context context, String title, String message) {
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

    public static String getDeviceDensityString(Context c) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        Log.e("DENSITY", String.valueOf(density));
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";

            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";

            case DisplayMetrics.DENSITY_HIGH: // 240
                return "hdpi";

            case DisplayMetrics.DENSITY_260:
                return "hdpi";

            case DisplayMetrics.DENSITY_280:
                return "hdpi";

            case DisplayMetrics.DENSITY_300:
                return "hdpi";

            case DisplayMetrics.DENSITY_XHIGH: // 320
                return "xhdpi";

            case DisplayMetrics.DENSITY_340:
                return "xhdpi";

            case DisplayMetrics.DENSITY_360:
                return "xhdpi";

            case DisplayMetrics.DENSITY_400:
                return "xhdpi";

            case DisplayMetrics.DENSITY_420:
                return "xhdpi";

            case DisplayMetrics.DENSITY_XXHIGH: // 480
                return "xxhdpi";

            case DisplayMetrics.DENSITY_560:
                return "xxhdpi";

            case DisplayMetrics.DENSITY_XXXHIGH: // 640
                return "xxxhdpi";

            default:
                return "hdpi"; // THIS IS NOT THE BEST SOLUTION, BUT I DON'T KNOW WHAT ELSE TO DO
        }
    }

    public static String newline = System.getProperty("line.separator");

    public static void showThanksToast (Context context) {
        Toast.makeText(context, context.getString(R.string.toast_thanks), Toast.LENGTH_SHORT).show();
    }

    public static void showErrorToast (Context context) {
        Toast.makeText(context, context.getString(R.string.toast_error), Toast.LENGTH_SHORT).show();
    }

    public static void showServerErrorToast (Context context, String response) {
        Toast.makeText(context, context.getString(R.string.toast_server_error) + response, Toast.LENGTH_SHORT).show();
    }

    public void expandView (View view) {
        view.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int viewHeight = view.getMeasuredHeight();

    }

    public static Bitmap combineImages(Context context, ArrayList<Bitmap> bitmapArray) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom

        int width = 0;
        int height = 0;

        for (Bitmap b : bitmapArray) {
            if (width != 0) {
                width += 40;
            }
            width += b.getWidth();
            if (b.getHeight()> height) {
                height = b.getHeight();
            }
        }

        Bitmap finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(finalBitmap);

        int widthUsed = 0;
        for (int i = 0; i < bitmapArray.size(); i++) {
            comboImage.drawBitmap(bitmapArray.get(i), widthUsed, 0f, null);
            widthUsed += bitmapArray.get(i).getWidth()+40;
        }

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
        /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

        OutputStream os = null;
        try {
          os = new FileOutputStream(loc + tmpImg);
          cs.compress(CompressFormat.PNG, 100, os);
        } catch(IOException e) {
          Log.e("combineImages", "problem combining images", e);
        }*/

        return finalBitmap;
    }

}




