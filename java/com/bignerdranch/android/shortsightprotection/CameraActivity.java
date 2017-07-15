package com.bignerdranch.android.shortsightprotection;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * Created by alex on 2017-07-13.
 */

public class CameraActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new CameraFragment();
    }

}
