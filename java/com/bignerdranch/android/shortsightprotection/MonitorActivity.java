package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;

/**
 * Created by alex on 2017-07-12.
 */

public class MonitorActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new MonitorFragment();
    }

}
