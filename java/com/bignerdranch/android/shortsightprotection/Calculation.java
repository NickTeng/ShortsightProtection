package com.bignerdranch.android.shortsightprotection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;

import java.io.File;

/**
 * Created by alex on 2017-07-19.
 */

public class Calculation {

    public static Bitmap j2b(File mJpg)
    {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig=Bitmap.Config.ARGB_8888;
        String path=mJpg.getPath();
        Bitmap bp= BitmapFactory.decodeFile(path,options);
        return bp;
    }

    public static void calculation(Bitmap bp){

    }
}
