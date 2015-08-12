package com.bespalov.sergey.runtracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bespalov.sergey.runtracker.model.Run;
import com.bespalov.sergey.runtracker.model.RunDatabaseHelper;
import com.bespalov.sergey.runtracker.model.RunManager;
import com.bespalov.sergey.runtracker.model.SQLLiteCursorLoader;

public class RunListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int REAQUEST_NEW_RUN = 0;

    private RunDatabaseHelper.RunCursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0,null,this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REAQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REAQUEST_NEW_RUN == requestCode) {
            getLoaderManager().restartLoader(0,null,this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivityForResult(i, REAQUEST_NEW_RUN);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        //create adapter that referenced to this cursor
        RunCursorAdapter adapter = new RunCursorAdapter(
                getActivity(), (RunDatabaseHelper.RunCursor)cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        setListAdapter(null);
    }

    private static class RunCursorAdapter extends CursorAdapter {

        private RunDatabaseHelper.RunCursor mRunCursor;

        public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;

        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Run run = mRunCursor.getRun();

            TextView startDateTextView = (TextView) view;
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            RunManager manager = RunManager.get(context);
            if (manager.isTrackingRun(run)){
                startDateTextView.setTextColor(Color.GREEN);
            }else {
                startDateTextView.setTextColor(Color.DKGRAY);
            }
            startDateTextView.setText(cellText);
        }
    }

    private static class RunListCursorLoader extends SQLLiteCursorLoader{

        public RunListCursorLoader(Context context){
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            //get query list
            return RunManager.get(getContext()).queryRuns();
        }
    }
}
