package com.bespalov.sergey.nerdlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sergey on 17.06.15.
 */
public class NerdRecentAppsFragment extends ListFragment {

    private static final String TAG = "ListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityManager am = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> info = am.getRunningTasks(1000);

        ArrayAdapter<ActivityManager.RunningTaskInfo> adapter = new ArrayAdapter<ActivityManager.RunningTaskInfo>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                info
        ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setText(info.get(position).baseActivity.toString());
                return view;
            }
        };

        setListAdapter(adapter);

    }
}
