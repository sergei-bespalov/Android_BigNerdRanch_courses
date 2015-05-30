package bespalov.sergei.criminalintent.controller;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;

import bespalov.sergei.criminalintent.R;
import bespalov.sergei.criminalintent.model.Crime;
import bespalov.sergei.criminalintent.model.CrimeLab;

/**
 * Created by sergei on 5/3/2015.
 */
public class CrimeListFragment extends SherlockListFragment {
    private Button mNewCrimeButton;
    private ListView mListView;
    private ArrayList<Crime> mCrimes;
    private static final String TAG = "CrimeListFragment";
    private boolean mSubtitleVisible;
    private boolean inAction;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getSherlockActivity().setTitle(R.string.crime_title);

        mCrimes = CrimeLab.getCrimeLab(getActivity()).getCrimes();
        ArrayAdapter<Crime> crimeAdapter = new CrimeAdapter(mCrimes);

        setListAdapter(crimeAdapter);

        setRetainInstance(true);
        mSubtitleVisible = false;
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        if (mSubtitleVisible) {
            getSherlockActivity().getSupportActionBar().setSubtitle(R.string.subtitle);
        }


        mNewCrimeButton = (Button) view.findViewById(R.id.new_crime_button);
        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewCrime();
            }
        });

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG,"Long click received");

                getSherlockActivity().startActionMode(new Callback() {

                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        actionMode.getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
                        inAction = true;
                        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_item_delete_crime:
                                CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
                                CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
                                for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                    if (getListView().isItemChecked(i)) {
                                        crimeLab.deleteCrime(adapter.getItem(i));
                                    }
                                }
                                actionMode.finish();
                                adapter.notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        inAction = false;
                        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    }
                });
                return true;
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!inAction) {
            Crime crime = (Crime) getListAdapter().getItem(position);
            Log.d(TAG, crime.getTitle() + "was clicked");
            Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
            intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        com.actionbarsherlock.view.MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                createNewCrime();
                return true;
            case R.id.menu_item_show_subtitle:
                if (getSherlockActivity().getSupportActionBar().getSubtitle() == null) {
                    getSherlockActivity().getSupportActionBar().setSubtitle(R.string.subtitle);
                    mSubtitleVisible = true;
                    item.setTitle(R.string.hide_subtitle);
                } else {
                    getSherlockActivity().getSupportActionBar().setSubtitle(null);
                    mSubtitleVisible = false;
                    item.setTitle(R.string.show_subtitle);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    private void createNewCrime() {
        Crime crime = new Crime();
        CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
        startActivityForResult(intent, 0);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, parent, false);
            } else {
                view = convertView;
            }
            Crime crime = getItem(position);
            ((TextView) view.findViewById(R.id.crime_list_item_crimeTitle)).setText(crime.getTitle());
            ((TextView) view.findViewById(R.id.crime_list_item_crimeDate)).setText(crime.getDate().toString());
            ((CheckBox) view.findViewById(R.id.crime_list_item_solvedCheckBox)).setChecked(crime.isSolved());
            return view;
        }
    }
}
