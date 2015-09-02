package com.bespalov.sergey.runtracker.model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;


public class RunManager {
    public static final String ACTION_LOCATION = "com.bignerdranch.android.runtracker.ACTION_LOCATION";
    private static final String TAG = "RunManager";
    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";
    private static RunManager sRunManager;
    private Location mLastLocation;

    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;

    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    public static RunManager get(Context context) {
        if (sRunManager == null) {
            sRunManager = new RunManager(context.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flag = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flag);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }

    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == mCurrentRunId;
    }

    public boolean isTrackingId(long id){
        return id == mCurrentRunId;
    }

    public Run startNewRun(){
        //inserts an object Run into a database
        Run run = insertRun();
        //starts tracking
        startTrackingRun(run);
        return  run;
    }

    public void startTrackingRun(Run run){
        // gets index
        mCurrentRunId = run.getId();
        //saves it in common preferences
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).apply();
        //starts location updates
        startLocationUpdates();
    }

    public void stopRun(){
        stopLocationUpdates();
        mCurrentRunId = - 1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).apply();
    }

    private Run insertRun(){
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public Run getRun(long id){
        Run run = null;
        RunDatabaseHelper.RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) run = cursor.getRun();
        cursor.close();
        return run;
    }

    public RunDatabaseHelper.RunCursor queryRuns() {
        return mHelper.queryRuns();
    }

    public void insertLocation(Location loc){
        if (mCurrentRunId != -1){
            mHelper.insertLocation(mCurrentRunId, loc);
        }else {
            Log.e(TAG, "Location received with no tracking run; ignoring.");
        }
    }

    public Location getLastLocationForRun(long runId){
        Location location = null;
        RunDatabaseHelper.LocationCursor cursor = mHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        if (!cursor.isAfterLast())  location = cursor.getLocation();
        cursor.close();
        return  location;
    }

    public RunDatabaseHelper.LocationCursor queryLocationsForRun(long runId){
        return mHelper.queryLocationForRun(runId);
    }
}
