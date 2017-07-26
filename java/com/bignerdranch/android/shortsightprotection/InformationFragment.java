package com.bignerdranch.android.shortsightprotection;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by alex on 2017-07-10.
 */
public class InformationFragment extends Fragment implements Camera.PreviewCallback{

    private static final String TAG="InfoFragment";
    private Camera mCamera;
    private SurfaceView mSurfaceView;

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_information, parent, false);
        mSurfaceView=(SurfaceView)v.findViewById(R.id.Preview);
        SurfaceHolder holder= mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera=Camera.open(1);
        mCamera.setDisplayOrientation(90);
        if (mCamera!=null) {
            Toast.makeText(getActivity(),"",Toast.LENGTH_LONG).show();
            mCamera.setOneShotPreviewCallback(this);
        }else{
            Toast.makeText(getActivity(),"mCamera is null",Toast.LENGTH_LONG).show();
        }

        holder.addCallback(new SurfaceHolder.Callback(){
            public void surfaceCreated(SurfaceHolder holder){
                try{
                    if (mCamera!=null){
                        mCamera.setPreviewDisplay(holder);
                    }
                }catch(IOException exception){
                    Log.e(TAG,"Error setting up preview",exception);
                }
            }
            public void surfaceDestroyed(SurfaceHolder holder){
                if (mCamera!= null){
                    mCamera.stopPreview();
                }
            }
            public void surfaceChanged(SurfaceHolder holder,int format, int w, int h){
                if (mCamera==null){
                    return;
                }
                Camera.Parameters parameters=mCamera.getParameters();
                Camera.Size s=getBestSupportedSize(parameters.getSupportedPreviewSizes(),w,h);
                parameters.setPreviewSize(s.width,s.height);
                mCamera.setParameters(parameters);
                try{
                    mCamera.startPreview();
                }catch (Exception e){
                    Log.e(TAG,"could not strat preview", e);
                    mCamera.release();
                    mCamera=null;
                }
            }
        });






        return v;
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height){
        Camera.Size bestSize=sizes.get(0);
        int largestArea=bestSize.width*bestSize.height;
        for (Camera.Size s:sizes){
            int area=s.width*s.height;
            if (area>largestArea){
                bestSize=s;
                largestArea=area;
            }
        }
        return bestSize;
    }

    @TargetApi(9)
    @Override
    public void onResume(){
        super.onResume();
        mCamera=Camera.open(1);
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

    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Toast.makeText(getActivity(),"lalalalalalala",Toast.LENGTH_LONG).show();
    }
}

