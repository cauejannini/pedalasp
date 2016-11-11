package jannini.android.ciclosp.NetworkRequests;

import java.util.ArrayList;

import jannini.android.ciclosp.CustomItemClasses.CyclingPath;

/**
 * Created by cauejannini on 04/07/16.
 */
public interface GetRouteInterface {

    void onFinished(int requestID, ArrayList<CyclingPath> cyclingPathReturnList);
}


