package bespalov.sergei.criminalintent.controller;

import android.support.v4.app.Fragment;

/**
 * Created by sergei on 5/31/2015.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
