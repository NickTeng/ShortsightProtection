package com.bignerdranch.android.shortsightprotection;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import facecam.tsface.TSFaceVerify;

/**
 * Created by alex on 2017-07-10.
 */
public class MonitorFragment extends Fragment implements Camera.PreviewCallback{

    private static final String TAG="InfoFragment";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    public static Bitmap bmp;
    private float[][] mCoordinates=new float[2][88];

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_monitor, parent, false);
        mSurfaceView=(SurfaceView)v.findViewById(R.id.Preview);
        SurfaceHolder holder= mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback(){
            public void surfaceCreated(SurfaceHolder holder){
                try{
                    if (mCamera!=null){
                        mCamera.setPreviewDisplay(holder);
                        mCamera.setOneShotPreviewCallback(MonitorFragment.this);
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
                    mCamera.setOneShotPreviewCallback(MonitorFragment.this);
                }catch (Exception e){
                    Log.e(TAG,"could not start preview", e);
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

    public void onPreviewFrame(byte[] data, Camera camera) {

//        Camera.Size size = mCamera.getParameters().getPreviewSize(); //获取预览大小
//        final int w = size.width;  //宽度
//        final int h = size.height;
//        final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
//        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
//        if(!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)){
//            return;
//        }
//        byte[] tmp = os.toByteArray();
//        BitmapFactory.Options options =new BitmapFactory.Options();
//        options.inPreferredConfig=Bitmap.Config.ARGB_8888;
//        bmp = BitmapFactory.decodeByteArray(tmp, 0,tmp.length,options);
//        Intent i=new Intent(getActivity(),Test.class);
//        startActivity(i);

        Camera.Size size = camera.getParameters().getPreviewSize();
        try{
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if(image!=null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                Intent i=new Intent(getActivity(),Test.class);
                startActivity(i);
                stream.close();

            }
        }catch(Exception ex){
            Log.e("Sys","Error:"+ex.getMessage());
        }
        //calculate(bmp);

    }








    public void calculate(Bitmap bp){
        TSFaceVerify monitorTS=new TSFaceVerify();
        monitorTS.SetFaceWidth(0,1280);
        int z=monitorTS.SetImage1(bp);
        if (z==-1){
            Log.e("ffffff","oh no");
        }

        for (int i=0; i<88; i++){
            mCoordinates[0][i]=monitorTS.GetKeyPointX(i);
            mCoordinates[1][i]=monitorTS.GetKeyPointY(i);
        }
        float mDistance=(InitialFragment.product)/MonitorFragment.getDistance(mCoordinates,17,25);
        Log.e("ffffff",""+InitialFragment.product+"/"+MonitorFragment.getDistance(mCoordinates,17,25)+"="+mDistance);
        if (mDistance<20) {
            Intent i = new Intent(getActivity(), Warning.class);
            startActivity(i);
        }
        return;
    }

    private static float getDistance(float[][] Cor,int x,int y){
        float distanceX=Cor[0][x-1]-Cor[0][y-1];
        float distanceY=Cor[1][x-1]-Cor[1][y-1];
        float distance=(float) Math.sqrt(Math.pow((double)distanceX,2)+Math.pow((double)distanceY,2));
        return distance;
    }








}

