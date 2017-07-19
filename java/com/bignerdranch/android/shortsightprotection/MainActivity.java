package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new MainFragment();
    }

}
