package com.bespalov.sergey.runtracker.model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;


public class RunManager {
    private static final String TAG = "RunManager";
    public static final String ACTION_LOCATION = "com.bignerdranch.android.runtracker.ACTION_LOCATION";
    private static RunManager sRunManager;
    private Location mLastLocation;

    private Context mAppContext;
    private LocationManager mLocationManager;

    public static RunManager get(Context context) {
        if (sRunManager == null){
            sRunManager = new RunManager(context.getApplicationContext());
        }
        return sRunManager;
    }

    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate){
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flag = shouldCreate? 0: PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flag);
    }

    public void startLocationUpdates(){
        String provider = LocationManager.GPS_PROVIDER;

        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }

    public void stopLocationUpdates(){
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null){
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun(){
        return getLocationPendingIntent(false) != null;
    }
}
