package com.bespalov.sergey.nerdlauncher;

import android.support.v4.app.Fragment;

/**
 * Created by sergey on 17.06.15.
 */
public class NerdRecentAppsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new NerdRecentAppsFragment();
    }
}
