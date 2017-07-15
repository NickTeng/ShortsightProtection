package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by alex on 2017-07-10.
 */

public class InitialFragment extends Fragment {

    private ImageButton mExit;
    private ImageButton mInformation;
    private ImageButton mBegin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_initial,parent,false);

        //to use the finish method of the parent activity
        mExit = (ImageButton)v.findViewById(R.id.exit_button);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShortsightProtection sp=(ShortsightProtection)getActivity();
                sp.finish();
            }
        });


        //To set a monitor for information method
        mInformation=(ImageButton)v.findViewById(R.id.information_button);
        mInformation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i=new Intent(getActivity(),InformationActivity.class);
                startActivity(i);

            }
        });

        //To set a monitor for begin button
        mBegin=(ImageButton)v.findViewById(R.id.begin_button);
        mBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CameraActivity.class);
                startActivity(i);
            }
        });


        return v;
    }
}
