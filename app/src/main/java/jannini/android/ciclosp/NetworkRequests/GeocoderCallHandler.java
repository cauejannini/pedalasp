package jannini.android.ciclosp.NetworkRequests;

import android.location.Address;

/**
 * Created by cauejannini on 01/11/16.
 */
public class GeocoderCallHandler {

    public GeocoderCallHandler() {}

    public void onResponse (int code, Address address) {
        if (code == 1) {
            onSuccess(address);
        } else if (code == 2) {
            onFailure("Zero addresses found");
        } else if (code == 3) {
            onDismissedAlertView();
        }
    }

    public void onSuccess (Address address) {

    }

    public void onFailure (String reason) {

    }

    public void onDismissedAlertView () {

    }

}
