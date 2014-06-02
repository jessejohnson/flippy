package com.jojo.flippy.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;


public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //TODO Load relevant resources from API here

        /*onCreate variables*/
        int DELAY = 1000;

        Timer timer = new Timer();
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
}
