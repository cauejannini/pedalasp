package jannini.android.ciclosp.NetworkRequests;

import android.graphics.Bitmap;

/**
 * Created by cauejannini on 01/11/16.
 */
public class BitmapCallHandler {

    public BitmapCallHandler() {}

    public void onResponse (int code, Bitmap bitmap, int imageId) {
        if (code == 200) {
            onSuccess(bitmap, imageId);
        } else {
            onFailure(imageId);
        }
    }

    public void onSuccess (Bitmap bitmap, int imageId) {

    }

    public void onFailure (int imageId) {

    }
}
