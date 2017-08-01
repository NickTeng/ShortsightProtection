package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;

/**
 * Created by alex on 2017-07-27.
 */

public class RetryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new RetryFragment();
    }
}
