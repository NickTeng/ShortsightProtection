package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;

public class InitialActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new InitialFragment();
    }

}
