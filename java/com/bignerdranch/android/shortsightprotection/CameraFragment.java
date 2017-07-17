package com.bignerdranch.android.shortsightprotection;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.IOException;
import java.util.List;

import facecam.tsface.TSFaceVerify;

/**
 * Created by alex on 2017-07-13.
 */

public class CameraFragment extends Fragment {
    private static final String TAG="CameraFragment";
    private android.hardware.Camera mCamera;
    private SurfaceView mSurfaceView;
    private TSFaceVerify mFace1=new TSFaceVerify();
    private TSFaceVerify mFace2=new TSFaceVerify();

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);
        ImageButton beginButton = (ImageButton) v.findViewById(R.id.camera_button);
        beginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCamera!=null) {
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inPreferredConfig=Bitmap.Config.ARGB_8888;
                    Bitmap bm1=BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.me,options);
                    if (bm1==null){
                        Log.e(TAG,"first is null");
                    }
                    Bitmap bm2=BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.me2,options);
                    if (bm2==null){
                        Log.e(TAG,"second is null");
                    }
                    calculate(bm1, bm2);
                    //mCamera.takePicture(null,null,null);
                }
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

    public void calculate(Bitmap initialBitmap, Bitmap finalBitmap){
        int a=mFace1.SetImage1(initialBitmap);
        if (a==-1){
            Toast.makeText(getActivity(),"-1",Toast.LENGTH_LONG).show();
        }else{
            if (a==0){
                Toast.makeText(getActivity(),"0",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(),"1",Toast.LENGTH_LONG).show();
            }
        }
//        mFace2.SetImage2(finalBitmap);
//        if ((mFace1.GetY1()-mFace1.GetY2())/(mFace1.GetX1()-mFace1.GetX2())>(mFace2.GetY1()-mFace2.GetY2())/(mFace2.GetX1()-mFace2.GetX2())){
//            Toast.makeText(getActivity(),"lalala",Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(getActivity(),"hahaha",Toast.LENGTH_LONG).show();
//        }

    }
}
