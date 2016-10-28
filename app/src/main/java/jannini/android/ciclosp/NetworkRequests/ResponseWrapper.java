package jannini.android.ciclosp.NetworkRequests;

/**
 * Created by cauejannini on 04/07/16.
 */
public class ResponseWrapper {

    int responseCode;
    String response;

    public ResponseWrapper(int responseCode, String response) {
        this.responseCode = responseCode;
        this.response = response;
    }
}


