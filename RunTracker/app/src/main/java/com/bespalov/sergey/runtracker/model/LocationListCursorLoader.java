package com.bespalov.sergey.runtracker.model;

import android.content.Context;
import android.database.Cursor;

public class LocationListCursorLoader extends SQLLiteCursorLoader{
    long mRunId;

    public LocationListCursorLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).queryLocationsForRun(mRunId);
    }
}
