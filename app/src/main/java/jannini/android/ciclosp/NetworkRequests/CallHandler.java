package jannini.android.ciclosp.NetworkRequests;

/**
 * Created by cauejannini on 04/07/16.
 */
public class CallHandler {

    public CallHandler() {
    }

    public void onResponse (int responseCode, String response) {
        if (responseCode == 200) {
            onSuccess(responseCode, response);
        } else {
            onFailure(responseCode, response);
        }
    }

    public void onSuccess (int responseCode, String response) {

    }

    public void onFailure (int responseCode, String response) {

    }

}
