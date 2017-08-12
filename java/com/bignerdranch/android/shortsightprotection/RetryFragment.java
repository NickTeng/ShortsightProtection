package com.bignerdranch.android.shortsightprotection;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by alex on 2017-07-27.
 */

public class RetryFragment extends Fragment {
    private Button mRetryButton;
    private Button mNoButton;
    private EditText mEditText;
    public static int userSetting=30;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retry, parent, false);
        mEditText=(EditText)v.findViewById(R.id.Settings);
        mEditText.setText("30");

        mRetryButton=(Button)v.findViewById(R.id.retry_button);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),InitialActivity.class);
                startActivity(i);
            }
        });


        mNoButton=(Button)v.findViewById(R.id.begin_button);
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    userSetting=Integer.parseInt(mEditText.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent i=new Intent(getActivity(),MonitorActivity.class);
                startActivity(i);
            }
        });


        return v;
    }
}
