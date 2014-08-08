package com.jojo.flippy.core;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.jojo.flippy.app.R;
import com.parse.ParseAnalytics;

public class PushedNotices extends ActionBarActivity {
    private String TAG = "PushedNotices";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushed_notices);
        ParseAnalytics.trackAppOpened(getIntent());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");
        Log.e(TAG, jsonData);
    }
}
