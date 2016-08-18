package com.example.cloudtable.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.cloudtable.R;

/**
 * Created by Lenovo on 10/08/2016.
 * this class to displayed message from server in other layout
 */
public class NotUpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        TextView textView = (TextView)findViewById(R.id.message);

        Intent i = getIntent();
        String message = i.getStringExtra("message");
        textView.setText(message);
    }
}
