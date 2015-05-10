package bespalov.sergei.criminalintent.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        ArrayAdapter<Crime> crimeAdapter = new CrimeAdapter(mCrimes);

        setListAdapter(crimeAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime crime = (Crime) getListAdapter().getItem(position);
        Log.d(TAG,crime.getTitle() + "was clicked");
        Intent intent = new Intent(getActivity(),CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID,crime.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class CrimeAdapter extends ArrayAdapter<Crime>{

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if(convertView == null){
                view = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime,parent,false);
            }else{
                view = convertView;
            }
            Crime crime = getItem(position);
            ((TextView)view.findViewById(R.id.crime_list_item_crimeTitle)).setText(crime.getTitle());
            ((TextView)view.findViewById(R.id.crime_list_item_crimeDate)).setText(crime.getDate().toString());
            ((CheckBox)view.findViewById(R.id.crime_list_item_solvedCheckBox)).setChecked(crime.isSolved());
            return view;
        }
    }
}
