package com.bespalov.sergey.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bespalov.sergey.photogallery.model.FlickrFetchr;
import com.bespalov.sergey.photogallery.model.GalleryItem;

import java.util.ArrayList;

/**
 * Created by Sergey on 12.07.2015.
 */
public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 15;
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";
    public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent:" + intent);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvaible = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
        if (!isNetworkAvaible) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
        String lastResultId = prefs.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);

        ArrayList<GalleryItem> items;
        if (query != null) {
            items = new FlickrFetchr().search(query);
        } else {
            items = new FlickrFetchr().fetchItems();
        }
        if (items.size() == 0)
            return;
        String resultId = items.get(0).getId();
        if (!resultId.equals(lastResultId)) {
            Log.i(TAG, "Got a new result: " + resultId);

            Resources resources = getResources();
            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, PhotoGalleryActivity.class), 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getText(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getText(R.string.new_pictures_title))
                    .setContentText(resources.getText(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            showBackgroundNotification(0, notification);

            /*NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(0, notification);

            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);*/
        } else {
            Log.i(TAG, "Got an old result: " + resultId);
        }

        prefs.edit()
                .putString(FlickrFetchr.PREF_LAST_RESULT_ID, resultId)
                .apply();
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent intent = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn){
            alarmManager.setRepeating(
                    alarmManager.RTC,
                    System.currentTimeMillis(),
                    POLL_INTERVAL,
                    pi);
        }else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PollService.PREF_IS_ALARM_ON, isOn)
                .commit();
    }

    public static boolean isServiceAlarmOn(Context context){
        Intent intent = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context,0,intent,PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra("REQUEST_CODE", requestCode);
        i.putExtra("NOTIFICATION", notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }



}
