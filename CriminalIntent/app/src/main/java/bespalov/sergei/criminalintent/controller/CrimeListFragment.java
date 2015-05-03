package bespalov.sergei.criminalintent.controller;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import bespalov.sergei.criminalintent.R;
import bespalov.sergei.criminalintent.model.Crime;
import bespalov.sergei.criminalintent.model.CrimeLab;

/**
 * Created by sergei on 5/3/2015.
 */
public class CrimeListFragment extends ListFragment{
    private ArrayList<Crime> mCrimes;
    private static final String TAG = "CrimeListFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crime_title);
        mCrimes = CrimeLab.getCrimeLab(getActivity()).getCrimes();
        ArrayAdapter<Crime> crimeAdapter = new ArrayAdapter<Crime>(getActivity(),R.layout.simple_list_item,mCrimes);
        setListAdapter(crimeAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime crime = (Crime) getListAdapter().getItem(position);
        Log.d(TAG,crime.getTitle() + "was clicked");
    }
}
