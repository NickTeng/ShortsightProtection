package com.bignerdranch.android.shortsightprotection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alex on 2017-08-01.
 */

public class Test extends Activity {
    private SurfaceView mSurfaceview;
    FileOutputStream out;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        mSurfaceview=(SurfaceView)findViewById(R.id.test_surface_view);



        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".jpg");
        try {
            out = new FileOutputStream(file);
            MonitorFragment.bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            System.out.println("___________保存的__sd___下_______________________");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(Test.this,"保存已经至"+Environment.getExternalStorageDirectory()+"下", Toast.LENGTH_SHORT).show();
    }
}

