package com.jojo.flippy.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;

import java.sql.SQLDataException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SplashScreenActivity extends ActionBarActivity {
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //TODO Load relevant resources from API here

        /*onCreate variables*/
        int DELAY = 2000;

        timer.schedule(new TimerTask() {
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, OnboardingActivity.class));
            }
        }, DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //kill timer to prevent next activity from starting
        timer.cancel();
        finish();
    }
}
