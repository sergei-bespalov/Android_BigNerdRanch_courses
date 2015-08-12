package com.bespalov.sergey.runtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bespalov.sergey.runtracker.model.LastLocationLoader;
import com.bespalov.sergey.runtracker.model.LocationReceiver;
import com.bespalov.sergey.runtracker.model.Run;
import com.bespalov.sergey.runtracker.model.RunLoader;
import com.bespalov.sergey.runtracker.model.RunManager;

public class RunFragment extends Fragment {

    public final static String TAG = "RunFragment";

    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_RUN = 0;
    private static final int LOAD_LOCATION = 1;

    private Button mStartButton, mStopButton;
    private TextView mStartedTextView,
            mLatitudeTextView,
            mLongitudeTextView,
            mAltitudeTextView,
            mDurationTextView;
    private Run mRun;
    private RunManager mRunManager;
    private Location mLastLocation;
    private NotificationManager mNotificationManager;
    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            super.onLocationReceived(context, loc);
            if (!mRunManager.isTrackingRun(mRun)) {
                return;
            }
            mLastLocation = loc;
            if (mRun != null) {
                long durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
                Log.d(TAG, "Time: " + durationSeconds);
            }
            Log.d(TAG, "Latitude: " + Double.toString(mLastLocation.getLatitude()));
            Log.d(TAG, "Longitude: " + Double.toString(mLastLocation.getLongitude()));
            Log.d(TAG, "Altitude: " + Double.toString(mLastLocation.getAltitude()));

            Intent i = new Intent(MyApp.getContext(), RunActivity.class);
            i.putExtra(RunActivity.EXTRA_RUN_ID, mRun.getId());

            PendingIntent pi = PendingIntent.getActivity(MyApp.getContext(),0, i,0);

            Notification notification = new NotificationCompat.Builder(MyApp.getContext())
                    .setTicker("is Tracking")
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setContentTitle("New location: ")
                    .setContentText(getString(R.string.latitude) + " " + Double.toString(mLastLocation.getLatitude())
                            + " " + getString(R.string.longitude) + " " + Double.toString(mLastLocation.getLongitude())
                            + " " + getString(R.string.altitude) + " " + Double.toString(mLastLocation.getAltitude()))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            if (mNotificationManager == null){
                mNotificationManager = (NotificationManager) MyApp.getContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mNotificationManager.notify(0,notification);
            if (isVisible()) {
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

    public static RunFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment rf = new RunFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.get(getActivity());

        //check Run id and get object
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID);
            if (runId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallback());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);
        mStartButton = (Button) view.findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRun == null) {
                    mRun = mRunManager.startNewRun();
                } else {
                    mRunManager.startTrackingRun(mRun);
                }
                updateUI();
            }
        });
        mStopButton = (Button) view.findViewById(R.id.stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopRun();
                updateUI();
            }
        });
        mStartedTextView = (TextView) view.findViewById(R.id.started);
        mLatitudeTextView = (TextView) view.findViewById(R.id.latitude);
        mLongitudeTextView = (TextView) view.findViewById(R.id.longitude);
        mAltitudeTextView = (TextView) view.findViewById(R.id.altitude);
        mDurationTextView = (TextView) view.findViewById(R.id.elapsedTime);
        updateUI();
        return view;
    }

    private void updateUI() {
        boolean started = mRunManager.isTrackingRun();
        boolean trackingThisRun = mRunManager.isTrackingRun(mRun);
        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started && trackingThisRun);
        if (mRun != null)
            mStartedTextView.setText(mRun.getStartDate().toString());
        int durationSeconds = 0;
        if (mRun != null && mLastLocation != null) {
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
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

    public class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run>{

        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(MyApp.getContext(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run run) {
            mRun = run;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {
            //do nothing
        }
    }

    public class LocationLoaderCallback implements LoaderManager.LoaderCallbacks<Location>{

        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location location) {
            mLastLocation = location;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {
            //do nothing
        }
    }
}
