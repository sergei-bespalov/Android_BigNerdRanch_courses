package com.bespalov.sergey.nerdlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sergey on 17.06.15.
 */
public class NerdRecentAppsFragment extends ListFragment {
    ActivityManager mActivityManager;

    private static final String TAG = "ListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityManager = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> info = mActivityManager.getRunningTasks(1000);

        final PackageManager pm = getActivity().getPackageManager();

        ArrayAdapter<ActivityManager.RunningTaskInfo> adapter = new ArrayAdapter<ActivityManager.RunningTaskInfo>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                info
        ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                try {
                    ApplicationInfo rInfo = pm.getApplicationInfo(info.get(position).baseActivity.getPackageName(), PackageManager.GET_META_DATA);
                    textView.setText(rInfo.loadLabel(pm));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return view;
            }
        };

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo) getListAdapter().getItem(position);
        mActivityManager.moveTaskToFront(info.id, 0);
    }
}
