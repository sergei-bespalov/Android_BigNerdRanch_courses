package com.bespalov.sergey.runtracker;

import android.support.v4.app.Fragment;

public class RunMapActivity extends SingleFragmentActivity{

    public static final String EXTRA_RUN_ID = "com.bespalov.sergey.runtracker.RunMapActivity.ExtraRunId";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1){
            return RunMapFragment.newInstance(runId);
        }else {
            return new RunMapFragment();
        }
    }
}
