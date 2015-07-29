package com.bespalov.sergey.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bespalov.sergey.runtracker.model.LocationReceiver;
import com.bespalov.sergey.runtracker.model.Run;
import com.bespalov.sergey.runtracker.model.RunManager;

public class RunFragment extends Fragment {

    public final static String TAG = "RunFragment";

    private Button mStartButton, mStopButton;
    private TextView mStartedTextView,
            mLatitudeTextView,
            mLongitudeTextView,
            mAltitudeTextView,
            mDurationTextView;
    private Run mRun;
    private RunManager mRunManager;
    private Location mLastLocation;
    private BroadcastReceiver mLocationReceiver = new LocationReceiver(){
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            super.onLocationReceived(context, loc);
            mLastLocation = loc;
            if (isVisible()){
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            super.onProviderEnabledChanged(enabled);
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.get(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run,container,false);
        mStartButton = (Button) view.findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mRunManager.startLocationUpdates();
                mRun = new Run();
                updateUI();
            }
        });
        mStopButton = (Button) view.findViewById(R.id.stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopLocationUpdates();
                updateUI();
            }
        });
        mStartedTextView = (TextView) view.findViewById(R.id.started);
        mLatitudeTextView = (TextView) view.findViewById(R.id.latitude);
        mLongitudeTextView = (TextView) view.findViewById(R.id.longitude);
        mAltitudeTextView = (TextView) view.findViewById(R.id.altitude);
        mDurationTextView = (TextView) view.findViewById(R.id.elapsedTime);

        return view;
    }

    private void updateUI(){
        boolean started = mRunManager.isTrackingRun();
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started);
        if (mRun != null)
            mStartedTextView.setText(mRun.getStartDate().toString());
        int durationSeconds = 0;
        if (mRun != null && mLastLocation != null) {
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            Log.d(TAG, "Time: " + durationSeconds);
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            Log.d(TAG, "Latitude: " + Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            Log.d(TAG, "Longitude: " + Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
            Log.d(TAG, "Altitude: " + Double.toString(mLastLocation.getAltitude()));
        }
        mDurationTextView.setText(Run.formatDuration(durationSeconds));
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
}
