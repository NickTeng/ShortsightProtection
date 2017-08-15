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
import android.widget.EditText;
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
    float mDistanceToEye;
    float vertical;
    public static float final_distance;
    private SensorManager sensorMgr;
    private Sensor sensor;
    private SensorEventListener lsn;
    private boolean isClose=false;



    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_monitor, parent, false);
        sensorMgr = (SensorManager) getActivity().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY);

        lsn = new SensorEventListener() {
            @SuppressWarnings("deprecation")
            //传感器获取值改变时响应此函数
            public void onSensorChanged(SensorEvent e) {
                x_d = e.values[SensorManager.DATA_X];
                y_d = e.values[SensorManager.DATA_Y];
                z_d = e.values[SensorManager.DATA_Z];

            }

            public void onAccuracyChanged(Sensor s, int accuracy) {
                return;
            }
        };
        mSurfaceView=(SurfaceView)v.findViewById(R.id.Preview);
        SurfaceHolder holder= mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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
                Log.e("adbadb",""+mCamera.getParameters().getPreviewSize().width+"\n"+mCamera.getParameters().getPreviewSize().height);
                try{
                    mCamera.startPreview();
                    mCamera.autoFocus(new Camera.AutoFocusCallback(){
                        @Override
                        public void onAutoFocus(boolean success, Camera camera){
                            Log.i("alex","autofocua "+success);
                            if (success){
                                mCamera.setOneShotPreviewCallback(MonitorFragment.this);
                            }
                        }
                    });


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
        if (camera==null){
            return;
        }
        Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
        camera.getParameters().setAutoExposureLock(false);
        camera.getParameters().setExposureCompensation(camera.getParameters().getMaxExposureCompensation());
        camera.getParameters().setRotation(90);
        final int w = size.width;
        final int h = size.height;
        final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if(!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)){
            return;
        }
        byte[] tmp = os.toByteArray();
        BitmapFactory.Options options =new BitmapFactory.Options();
        options.inPreferredConfig=Bitmap.Config.ARGB_8888;
        bmp = rotateMyBitmap(BitmapFactory.decodeByteArray(tmp, 0,tmp.length,options));



        calculate(bmp);

        if (isClose){
            Intent i=new Intent(getActivity(),Warning.class);
            startActivity(i);
        }
        isClose=false;
    }








    public void calculate(Bitmap bp){
        TSFaceVerify monitorTS=new TSFaceVerify();
        monitorTS.SetFaceWidth(0,1280);
        int z=monitorTS.SetImage1(bp);
            Log.e("final distance",""+z);

        if (z==-1){
            return;
        }
        for (int i=0; i<88; i++){
            mCoordinates[0][i]=monitorTS.GetKeyPointX(i);
            mCoordinates[1][i]=monitorTS.GetKeyPointY(i);
        }
///////////The /2 is because the pixels of IniF is not the same with MonF
        mDistanceToEye=(InitialFragment.product)/getDistance(mCoordinates,17,25)/2;

        // a,b,c,d and e are based on "the" graph

        float b_p=(float)Math.sqrt((double)mCoordinates[1][17]*(double)mCoordinates[1][25]);
        Log.e("final distance","b_p="+b_p);
        float a_p=mCamera.getParameters().getPreviewSize().height-b_p;
        Log.e("final distance","a_p="+a_p);
        ///////////////TEST!!!!!///////////////////
        float a=a_p*InitialFragment.mRealEyeDistance/getDistance(mCoordinates,17,25);
        float b=b_p*InitialFragment.mRealEyeDistance/getDistance(mCoordinates,17,25);
        Log.e("final distance","a="+a);
        Log.e("final distance","b="+b);
        Log.e("final diatance","c="+mDistanceToEye);
        //////////////////////////////////////////////////////////
        Log.e("xxxxxx","x="+x_d);
        Log.e("xxxxxx","y="+y_d);
        Log.e("xxxxxx","z="+z_d);
        float theta=(float)(Math.PI/2-Math.atan(y_d/z_d));
        Log.e("final distance","theta="+theta/Math.PI*180);
        /////////////////////

        int x=0;
        float temp_af=(float)Math.sqrt(Math.pow(mDistanceToEye,2)-Math.pow(b+x,2));
        float temp_ae=(float)Math.sqrt(Math.pow(temp_af,2)+Math.pow(x,2));
        float temp_ab=(float)Math.sqrt(Math.pow(temp_af,2)+Math.pow(a+b+x,2));
        float min=(float)Math.abs(Math.tan(theta)-((x+((a+b)*temp_ae/(temp_ae+temp_ab)))/temp_af));
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
        vertical=min_x+b;
        float mDeskDistance=(float)Math.sqrt(Math.pow(mDistanceToEye,2)-Math.pow(vertical,2));
        Log.e("final disatnce","vertical="+min_x+b);


        float mPresentNeck=getDistance(mCoordinates,34,49)/getDistance(mCoordinates,17,25)*InitialFragment.mRealEyeDistance;
        Log.e("debuuuug","mPresentNeck"+mPresentNeck);
        Log.e("debuuuug","mOriginalNeck"+InitialFragment.mOriginalneck);

        float middle;

        float y=0;
        middle=y;
        float min_y=(float)Math.abs(Math.pow((mPresentNeck-y*(vertical-mPresentNeck)/mDeskDistance),2)+Math.pow(y,2)-Math.pow(InitialFragment.mOriginalneck,2));
        while (y<=150){
            float temp_min_y=(float)Math.abs(Math.pow((mPresentNeck-y*(vertical-mPresentNeck)/mDeskDistance),2)+Math.pow(y,2)-Math.pow(InitialFragment.mOriginalneck,2));
            Log.e("equation","sub="+temp_min_y);
            if (temp_min_y<min_y){
                min_y=temp_min_y;
                middle=y;
            }
            y=(float)(y+0.1);
        }

        if (middle==0){
            middle=1;
        }

        final_distance=(float)(InitialFragment.mOriginalneck*vertical/middle);
        Log.e("final distance",""+final_distance);



        if (final_distance<RetryFragment.userSetting){
            Log.e("is working","<30!!!");
            isClose=true;
        }
        Log.e("is working",">30");

        return;
    }

    private static float getDistance(float[][] Cor,int x,int y){
        float distanceX=Cor[0][x-1]-Cor[0][y-1];
        float distanceY=Cor[1][x-1]-Cor[1][y-1];
        float distance=(float) Math.sqrt(Math.pow((double)distanceX,2)+Math.pow((double)distanceY,2));
        return distance;
    }














    public Bitmap rotateMyBitmap(Bitmap bmp){
        //*****旋转一下
        Matrix matrix = new Matrix();
        matrix.postRotate(270);

        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);

        Bitmap nbmp2 = Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(),  bmp.getHeight(), matrix, true);
        return nbmp2;

    };
}

