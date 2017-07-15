package com.bignerdranch.android.shortsightprotection;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.List;

/**
 * Created by alex on 2017-07-13.
 */

public class CameraFragment extends Fragment {
    private static final String TAG="CameraFragment";

    private android.hardware.Camera mCamera;
    private SurfaceView mSurfaceView;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);

        ImageButton beginButton = (ImageButton) v.findViewById(R.id.camera_button);
        beginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mSurfaceView = (SurfaceView) v.findViewById(R.id.camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);



        holder.addCallback(new SurfaceHolder.Callback(){
            public void surfaceCreated(SurfaceHolder holder){
                try{
                    if (mCamera!=null){
                        mCamera.setPreviewDisplay(holder);
                    }
                }catch (IOException exception){
                    Log.e(TAG,"error setting up preview display",exception);
                }
            }
            public void surfaceDestroyed(SurfaceHolder holder){
                if (mCamera!=null){
                    mCamera.stopPreview();
                }
            }
            public void surfaceChanged(SurfaceHolder holder,int format,int w,int h){
                if (mCamera==null){
                    return;
                }
                android.hardware.Camera.Parameters parameters=mCamera.getParameters();
                android.hardware.Camera.Size s = getBestSupportSize(parameters.getSupportedPreviewSizes(),w,h);
                parameters.setPreviewSize(s.width,s.height);
                mCamera.setParameters(parameters);
                try{
                    mCamera.startPreview();
                }catch(Exception e){
                    Log.e(TAG,"could not start preview",e);
                    mCamera.release();
                    mCamera=null;
                }
            }
        });
        return v;
    }


    @TargetApi(9)
    @Override
    public void onResume(){
        super.onResume();
            mCamera= android.hardware.Camera.open(1);
        mCamera.setDisplayOrientation(90);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mCamera!=null){
            mCamera.release();
            mCamera=null;
        }
    }

    private android.hardware.Camera.Size getBestSupportSize(List<android.hardware.Camera.Size> sizes, int width, int height){
        android.hardware.Camera.Size bestSize= sizes.get(0);
        int largestArea=bestSize.width*bestSize.height;
        for (android.hardware.Camera.Size s:sizes){
            int area=s.width*s.height;
            if (area>largestArea){
                bestSize=s;
                largestArea=area;
            }
        }
        return bestSize;
    }
}
