package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;

/**
 * Created by alex on 2017-07-13.
 */

public class CameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new CameraFragment();

    }

}
