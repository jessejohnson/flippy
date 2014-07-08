package com.jojo.flippy.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SplashScreenActivity extends ActionBarActivity {
    Timer timer = new Timer();
    private User currentUser;
    private Intent intent;
    private Dao<User, Integer> userDao;
    private Dao<Post, Integer> postDao;
    private boolean noCommunity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        intent = new Intent();


        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(SplashScreenActivity.this,
                    DatabaseHelper.class);
            userDao = databaseHelper.getUserDao();
            List<User> userList = userDao.queryForAll();
            if (userList.isEmpty()) {
                currentUser = null;
            } else {
                currentUser = userList.get(0);
                if (currentUser.community_id == null || currentUser.community_id == "") {
                    noCommunity = true;
                }
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
                if (noCommunity) {
                    intent.setClass(SplashScreenActivity.this, SelectCommunityActivity.class);
                    intent.putExtra("regUserEmail",currentUser.user_email);
                    intent.putExtra("regUserAuthToken",currentUser.user_auth);
                    intent.putExtra("regUserID",currentUser.user_id);
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
        timer.cancel();
        finish();
    }
}
