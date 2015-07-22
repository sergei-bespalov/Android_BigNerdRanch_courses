package com.bespalov.sergey.draganddraw;

import android.support.v4.app.Fragment;

/**
 * Created by Sergey on 22.07.2015.
 */
public class DragAndDrawActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DragAndDrawFragment();
    }
}
