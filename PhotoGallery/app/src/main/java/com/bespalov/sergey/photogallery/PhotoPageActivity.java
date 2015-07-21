package com.bespalov.sergey.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by Sergey on 21.07.2015.
 */
public class PhotoPageActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
