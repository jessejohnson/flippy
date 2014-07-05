package com.jojo.flippy.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SplashScreenActivity extends ActionBarActivity {
    Timer timer = new Timer();
    private User currentUser;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        intent = new Intent();


        try {
            Dao<User, Integer> userDao = ((Flippy) getApplication()).userDao;
            List<User> userList = userDao.queryForAll();
            if (userList.isEmpty()) {
                currentUser = null;
            } else {
                currentUser = userList.get(0);
             }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
        }

        int DELAY = 2000;

        timer.schedule(new TimerTask() {
            public void run() {
                if (currentUser == null) {
                    intent.setClass(SplashScreenActivity.this, OnBoardingActivity.class);
                    startActivity(intent);
                    return;
                }
                intent.setClass(SplashScreenActivity.this, CommunityCenterActivity.class);
                startActivity(intent);
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
