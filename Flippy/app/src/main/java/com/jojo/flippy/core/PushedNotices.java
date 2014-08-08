package com.jojo.flippy.core;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.jojo.flippy.app.R;
import com.parse.ParseAnalytics;

public class PushedNotices extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushed_notices);
        ParseAnalytics.trackAppOpened(getIntent());
    }
}
