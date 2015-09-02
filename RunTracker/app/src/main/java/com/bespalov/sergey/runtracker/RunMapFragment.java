package com.bespalov.sergey.runtracker;


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Location;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bespalov.sergey.runtracker.model.LocationListCursorLoader;
import com.bespalov.sergey.runtracker.model.LocationReceiver;
import com.bespalov.sergey.runtracker.model.RunDatabaseHelper;
import com.bespalov.sergey.runtracker.model.RunManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RunMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATIONS = 0;
    private static final String TAG = "RunMapFragment";
    private RunManager mRunManager;

    private GoogleMap mGoogleMap;
    private RunDatabaseHelper.LocationCursor mLocationCursor;

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf = new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRunManager = RunManager.get(getActivity());
        Bundle args = getArguments();
        if (args != null){
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_LOCATIONS, args, this);
            }
        }
    }

    private void updateUI(){
        if (mGoogleMap == null || mLocationCursor == null) return;
        mGoogleMap.clear();
        PolylineOptions line = new PolylineOptions();
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        mLocationCursor.moveToFirst();
        while (!mLocationCursor.isAfterLast()){
            Location loc = mLocationCursor.getLocation();
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

            Resources r = getResources();

            if (mLocationCursor.isFirst()){
                String startDate = new Date(loc.getTime()).toString();
                MarkerOptions startMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(r.getString(R.string.run_start))
                        .snippet(r.getString(R.string.run_started_at_format, startDate));
                mGoogleMap.addMarker(startMarkerOptions);
            }else if (mLocationCursor.isLast()){
                String endDate = new Date(loc.getTime()).toString();
                MarkerOptions finishMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(r.getString(R.string.run_finish))
                        .snippet(r.getString(R.string.run_finished_at_format, endDate));
                mGoogleMap.addMarker(finishMarkerOptions);
            }

            line.add(latLng);
            latLngBuilder.include(latLng);
            mLocationCursor.moveToNext();
        }
        mGoogleMap.addPolyline(line);
        Point display = new Point();
        LatLngBounds latLngBounds = latLngBuilder.build();
        if (Build.VERSION.SDK_INT < 13){
            Display display1 = getActivity().getWindowManager().getDefaultDisplay();
            //noinspection deprecation
            display.x = display1.getWidth();
            //noinspection deprecation
            display.y = display1.getHeight();
        }else {
            getActivity().getWindowManager().getDefaultDisplay().getSize(display);
        }
        Log.d(TAG, "Display width: " + String.valueOf(display.x));
        Log.d(TAG,"Display height: " + String.valueOf(display.y));
        CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBounds,
                display.x, display.y, 15);
        mGoogleMap.moveCamera(movement);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mGoogleMap = getMap();
        mGoogleMap.setMyLocationEnabled(true);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(), runId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mLocationCursor = (RunDatabaseHelper.LocationCursor) cursor;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLocationCursor.close();
        mLocationCursor = null;
    }

    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            super.onLocationReceived(context, loc);
            Bundle args = RunMapFragment.this.getArguments();
            if (args != null){
                long runId = args.getLong(ARG_RUN_ID, -1);
                if (runId != -1) {
                    LoaderManager lm = getLoaderManager();
                    lm.restartLoader(LOAD_LOCATIONS, args, RunMapFragment.this);
                }
            }
        }
    };
}
