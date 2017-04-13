package jannini.android.ciclosp.Models;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by cauejannini on 12/04/17.
 */

abstract class MapElement {

    protected Context context;
    protected boolean isDrawn;

    protected MapElement (Context context) {
        if (context!= null) {
            this.context = context;
        } else {
            throw new IllegalArgumentException("MapElement constructor: context is null");
        }
    }

    abstract String getTitle();

    abstract String getDescription();

    public boolean isDrawn() {
        return isDrawn;
    }

    abstract void drawOnMap(GoogleMap map);

    abstract void removeFromMap();

    abstract void setVisible(boolean visibility);

}
