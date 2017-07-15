package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ShortsightProtection extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new InitialFragment();
    }

}
