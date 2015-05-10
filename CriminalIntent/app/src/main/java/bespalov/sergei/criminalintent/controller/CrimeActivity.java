package bespalov.sergei.criminalintent.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

import bespalov.sergei.criminalintent.R;

/**
 * Main activity
 */
public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        UUID id = (UUID) getIntent().getExtras().getSerializable(CrimeFragment.EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(id);
    }

}
