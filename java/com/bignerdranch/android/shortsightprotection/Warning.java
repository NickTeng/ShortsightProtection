package com.bignerdranch.android.shortsightprotection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alex on 2017-07-25.
 */

public class Warning extends Activity {
    private Button mButton;
    private TextView mTextview;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warning);

        mButton=(Button) findViewById(R.id.ok_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Warning.this,MonitorActivity.class);
                startActivity(i);
            }
        });

        mTextview=(TextView)findViewById(R.id.distance);
        mTextview.setText(""+MonitorFragment.final_distance);

    }
}
