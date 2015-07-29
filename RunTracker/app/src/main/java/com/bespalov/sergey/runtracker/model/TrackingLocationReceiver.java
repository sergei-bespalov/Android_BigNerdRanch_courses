package com.bespalov.sergey.runtracker.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class TrackingLocationReceiver extends LocationReceiver {
    private static final String TAG = "LocationReceiver";

    @Override
    protected void onLocationReceived(Context context, Location loc) {
        Log.d(TAG, "new Loacation recieved");
        RunManager.get(context).insertLocation(loc);
    }
}
