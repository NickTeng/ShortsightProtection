package com.bignerdranch.android.shortsightprotection;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.HardwarePropertiesManager;
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
    float x_d,y_d,z_d=-1;
    float final_distance;


    SensorManager sensorMgr = (SensorManager) getActivity().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
    final Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    SensorEventListener lsn = new SensorEventListener() {
        @SuppressWarnings("deprecation")
        //传感器获取值改变时响应此函数
        public void onSensorChanged(SensorEvent e) {
            Log.e("xxxxx","xxxxx");
            x_d = e.values[SensorManager.DATA_X];
            y_d = e.values[SensorManager.DATA_Y];
            z_d = e.values[SensorManager.DATA_Z];

        }

        public void onAccuracyChanged(Sensor s, int accuracy) {
            return;
        }
    };


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
                Log.e("adbadb",""+mCamera.getParameters().getPreviewSize().width+"\n"+mCamera.getParameters().getPreviewSize().height);
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
        sensorMgr.registerListener(lsn,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onPause(){
        super.onPause();
        if (mCamera!=null){
            mCamera.release();
            mCamera=null;
            sensorMgr.unregisterListener(lsn);
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
                stream.close();

            }
        }catch(Exception ex){
            Log.e("Sys","Error:"+ex.getMessage());
        }
        calculate(bmp);
//        Intent i=new Intent(getActivity(),Warning.class);
//        startActivity(i);
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

        float mDistanceToEye=(InitialFragment.product)/MonitorFragment.getDistance(mCoordinates,17,25);

        // a,b,c,d and e are based on "the" graph

        float b_p=(float)Math.sqrt((double)mCoordinates[1][17]*(double)mCoordinates[1][25]);
        float a_p=mCamera.getParameters().getPreviewSize().height-b_p;
        float a=a_p*InitialFragment.mRealEyeDistance/getDistance(mCoordinates,17,25);
        float b=b_p*InitialFragment.mRealEyeDistance/getDistance(mCoordinates,17,25);
        /////This is wrong!!!
        Log.e("xxxxx","x="+x_d);
        Log.e("xxxxx","y="+y_d);
        Log.e("xxxxx","z="+z_d);
        float theta=0;
        /////////////////////
        int x=0;
        float temp_af=(float)Math.sqrt(Math.pow(mDistanceToEye,2)-Math.pow(b+x,2));
        float temp_ae=(float)Math.sqrt(Math.pow(temp_af,2)+Math.pow(x,2));
        float temp_ab=(float)Math.sqrt(Math.pow(temp_af,2)+Math.pow(a+b+x,2));
        float min=(float)Math.abs(Math.tan(theta)-((x+((a+b)*temp_ae/(temp_ae+temp_af)))/temp_af));
        int min_x=x;
        while (x<=150){
            temp_af=(float)Math.sqrt(Math.pow(mDistanceToEye,2)-Math.pow(b+x,2));
            temp_ae=(float)Math.sqrt(Math.pow(temp_af,2)+Math.pow(x,2));
            temp_ab=(float)Math.sqrt(Math.pow(temp_af,2)+Math.pow(a+b+x,2));
            float temp_min=(float)Math.abs(Math.tan(theta)-((x+((a+b)*temp_ae/(temp_ae+temp_ab)))/temp_af));

            if (temp_min<min){
                min=temp_min;
                min_x=x;
            }
            x++;
        }
        float mPresentNeck=getDistance(mCoordinates,34,49)/getDistance(mCoordinates,17,25)*InitialFragment.mRealEyeDistance;
        final_distance=(float)(InitialFragment.mOriginalneck*(min_x+b)/Math.sqrt(Math.pow(InitialFragment.mOriginalneck,2)+Math.pow(mPresentNeck,2)));

        return;
    }

    private static float getDistance(float[][] Cor,int x,int y){
        float distanceX=Cor[0][x-1]-Cor[0][y-1];
        float distanceY=Cor[1][x-1]-Cor[1][y-1];
        float distance=(float) Math.sqrt(Math.pow((double)distanceX,2)+Math.pow((double)distanceY,2));
        return distance;
    }















}

