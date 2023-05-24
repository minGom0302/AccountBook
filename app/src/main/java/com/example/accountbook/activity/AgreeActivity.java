package com.example.accountbook.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.example.accountbook.R;

public class AgreeActivity extends Activity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);

        findViewById(R.id.agree_closeBtn).setOnClickListener(v -> finish());
    }
}