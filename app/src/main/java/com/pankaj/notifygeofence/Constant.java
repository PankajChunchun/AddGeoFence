package com.pankaj.notifygeofence;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;


/**
 * Created by Pankaj Kumar on 8/16/2017.
 * pankaj.arrah@gmail.com
 */
public final class Constant {

    private Constant() {
        throw new RuntimeException("Can not be instantiated");
    }

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 20;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();

    static {
        // AUNDH, Pune India
        LANDMARKS.put("Aundh Pune", new LatLng(18.562622, 73.808723));

        // Viman Nagar.
        LANDMARKS.put("Viman Nagar", new LatLng(18.5679, 73.9143));

        // Koregaon Park
        LANDMARKS.put("KP", new LatLng(18.536207, 73.893974));
    }
}