package jannini.android.ciclosp.NetworkRequests;

import java.util.ArrayList;

import jannini.android.ciclosp.Models.CyclingPath;

/**
 * Created by cauejannini on 04/07/16.
 */
public interface GetRouteInterface {

    void onFinished(int resultCode, int requestID, ArrayList<CyclingPath> cyclingPathReturnList);
}


