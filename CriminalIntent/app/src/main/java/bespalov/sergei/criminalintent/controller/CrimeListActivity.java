package bespalov.sergei.criminalintent.controller;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import bespalov.sergei.criminalintent.R;
import bespalov.sergei.criminalintent.model.Crime;

/**
 * Created by sergei on 5/3/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callback, CrimeFragment.Callback {

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detailsFragmentContainer) == null){
            Intent intent = new Intent(this, CrimePagerActivity.class);
            intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
            startActivity(intent);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment oldDetail = fragmentManager.findFragmentById(R.id.detailsFragmentContainer);
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (oldDetail != null){
                transaction.remove(oldDetail);
            }
            transaction.add(R.id.detailsFragmentContainer, newDetail);
            transaction.commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment fragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
        fragment.updateUI();
    }
}
