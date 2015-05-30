package bespalov.sergei.criminalintent.controller;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created by sergei on 5/3/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
