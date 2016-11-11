package jannini.android.ciclosp.NetworkRequests;

/**
 * Created by cauejannini on 04/07/16.
 */
class ResponseWrapper {

    int responseCode;
    String response;

    ResponseWrapper(int responseCode, String response) {
        this.responseCode = responseCode;
        this.response = response;
    }
}


