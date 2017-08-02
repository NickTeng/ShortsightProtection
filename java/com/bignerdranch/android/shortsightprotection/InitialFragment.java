package com.bignerdranch.android.shortsightprotection;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import facecam.tsface.TSFaceVerify;
import java.io.File;
import java.text.DecimalFormat;

public class InitialFragment extends Fragment implements View.OnClickListener {

  private static final int CAMERA_RQ = 6969;
  private static final int PERMISSION_RQ = 84;
  static File saveDir = null;
  private File mJPG=null;
  private float[][] coordinates=new float[2][88];

  private static float DIS20 = -1 ;
  public static float product;


  public InitialFragment() {}

  public static InitialFragment getInstance() {
    InitialFragment fragment = new InitialFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v=inflater.inflate(R.layout.fragment_initial, container, false);
    return v;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    bindViews(view);
  }

  private void bindViews(View view) {
    view.findViewById(R.id.launchCamera).setOnClickListener(this);

    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED) {
      // Request permission to save videos in external storage
      ActivityCompat.requestPermissions(
          getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override
  public void onClick(View view) {


    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED) {
      // Only use external storage directory if permission is granted, otherwise cache directory is used by default
      saveDir = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
      saveDir.mkdirs();
    }

    MaterialCamera materialCamera =
        new MaterialCamera(this)
            .autoSubmit(true)
            .saveDir(saveDir)
            .showPortraitWarning(false)
            .allowRetry(true)
            .defaultToFrontFacing(true);

    materialCamera.stillShot();
    materialCamera.start(CAMERA_RQ);

  }

  private String readableFileSize(long size) {
    if (size <= 0) return size + " B";
    final String[] units = new String[] {"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups))
        + " "
        + units[digitGroups];
  }

  private String fileSize(File file) {
    return readableFileSize(file.length());
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Received recording or error from MaterialCamera
    if (requestCode == CAMERA_RQ) {
      if (resultCode == Activity.RESULT_OK) {
        final File file = new File(data.getData().getPath());
        mJPG=file;
      } else if (data != null) {
        Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
        if (e != null) {
          e.printStackTrace();
          Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    }

    if (mJPG == null) {
      Log.e("save error","saveDir is null");
      return;
    }


    Bitmap bp=j2b(mJPG);
    calculate(bp);



  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
      // Sample was denied WRITE_EXTERNAL_STORAGE permission
      Toast.makeText(
              getActivity(),
              "Videos will be saved in a cache directory instead of an external storage directory since permission was denied.",
              Toast.LENGTH_LONG)
          .show();
    }

  }










  public Bitmap j2b(File mJpg)
  {
    BitmapFactory.Options options=new BitmapFactory.Options();
    options.inPreferredConfig=Bitmap.Config.ARGB_8888;
    String path=mJpg.getPath();
    Bitmap bp= BitmapFactory.decodeFile(path,options);
    return bp;
  }

    public void calculate(Bitmap bp){
    TSFaceVerify ts=new TSFaceVerify();
    ts.SetFaceWidth(10,1280);
    int z=ts.SetImage1(bp);

    if (z!=1){
      Toast.makeText(getActivity(),"you have no face,please retry!",Toast.LENGTH_LONG).show();
      MaterialCamera materialCamera =
              new MaterialCamera(this)
                      .autoSubmit(true)
                      .saveDir(saveDir)
                      .showPortraitWarning(false)
                      .allowRetry(false)
                      .defaultToFrontFacing(true);

      materialCamera.stillShot();
      materialCamera.start(CAMERA_RQ);
      return;
    }


    for (int i=0; i<88; i++){
      coordinates[0][i]=ts.GetKeyPointX(i);
      coordinates[1][i]=ts.GetKeyPointY(i);
    }

      DIS20=getDistance(17,25);
      product=DIS20*20;


      //Toast.makeText(getActivity(),"",Toast.LENGTH_LONG).show();

      Intent i=new Intent(getActivity(),RetryActivity.class);
      startActivity(i);
      return;
  }


//输入坐标返回距离
  private float getDistance(int x,int y){
    float distanceX=coordinates[0][x-1]-coordinates[0][y-1];
    float distanceY=coordinates[1][x-1]-coordinates[1][y-1];
    float distance=(float) Math.sqrt(Math.pow((double)distanceX,2)+Math.pow((double)distanceY,2));
    return distance;
    }


}
