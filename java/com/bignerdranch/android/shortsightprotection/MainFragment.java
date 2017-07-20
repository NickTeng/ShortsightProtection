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

public class MainFragment extends Fragment implements View.OnClickListener {

  private static final int CAMERA_RQ = 6969;
  private static final int PERMISSION_RQ = 84;
  private Button mExit;
  private Button mInformation;
  static File saveDir = null;
  private File mJPG=null;

  public MainFragment() {}

  public static MainFragment getInstance() {
    MainFragment fragment = new MainFragment();
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
    View v=inflater.inflate(R.layout.fragment_main, container, false);
    //to use the finish method of the parent activity
    mExit = (Button)v.findViewById(R.id.exit_button);
    mExit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MainActivity sp=(MainActivity)getActivity();
        sp.finish();
      }
    });


    //To set a monitor for information method
    mInformation=(Button)v.findViewById(R.id.information_button);
    mInformation.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){
        Intent i=new Intent(getActivity(),InformationActivity.class);
        startActivity(i);

      }
    });
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
        != PackageManager.PERMISSION_GRANTED) {
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
    ts.SetFaceWidth(100,1080);
    int z=ts.SetImage1(bp);
    Toast.makeText(getActivity(),""+z,Toast.LENGTH_LONG).show();
  }



}
