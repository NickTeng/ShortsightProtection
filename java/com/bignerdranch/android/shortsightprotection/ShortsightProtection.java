package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;

public class ShortsightProtection extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new InitialFragment();
    }

}
