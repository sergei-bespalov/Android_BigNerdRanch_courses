package com.bespalov.sergey.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sergey on 16.06.15.
 */
public class NerdLauncherFragment extends ListFragment {
    private static String TAG = "NerdListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Intent startUpIntent = new Intent(Intent.ACTION_MAIN);
        startUpIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startUpIntent, 0);

        Log.i(TAG, "I've found " + activities.size() + " activities. ");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString()
                );
            }
        });

        ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(
                getActivity(), 0,
                activities){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null){
                    view = getActivity().getLayoutInflater().inflate(R.layout.list_item_app, parent, false);
                    ViewHolder holder = new ViewHolder();
                    holder.mImageView = (ImageView) view.findViewById(R.id.appIcon);
                    holder.mTextView = (TextView) view.findViewById(R.id.appLabel);
                    view.setTag(holder);
                } else {
                    view = convertView;
                }

                ViewHolder holder = (ViewHolder) view.getTag();
                ResolveInfo info = getItem(position);

                holder.mTextView.setText(info.loadLabel(pm));
                holder.mImageView.setImageDrawable(info.loadIcon(pm));

                return view;
            }

            class ViewHolder{
                TextView mTextView;
                ImageView mImageView;
            }

        };

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfo resolveInfo = (ResolveInfo) getListAdapter().getItem(position);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        if (activityInfo == null) return;

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(activityInfo.packageName, activityInfo.name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_nerd_launcher, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_recent){
            Intent intent = new Intent(getActivity(), NerdRecentAppsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
