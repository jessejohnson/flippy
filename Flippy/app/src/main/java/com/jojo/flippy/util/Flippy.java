package com.jojo.flippy.util;

import android.app.Application;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.core.NoticeDetailActivity;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.services.DataService;
import com.jojo.flippy.services.ManageLocalPost;

import java.util.List;

public class Flippy extends Application {
    //make the url accessible to all the activities
    public static String CHANNELS_URL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String USERS_URL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/";
    public static String COMMUNITIES_URL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/communities/";
    public static String POST_URL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/posts/";
    private static Flippy sInstance;
    public static String defaultDate = "2090-01-01";
    public static String defaultTime = "00:00:00";
    public static String defaultLat = "75.7667";
    public static String defaultLon = "99.7833";
    public Dao<User, Integer> userDao;
    public Dao<Post, Integer> postDao;
    public User thisUser;
    private RequestQueue mRequestQueue;

    public synchronized static Flippy getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;

        //starting the manage service activity
        Intent serviceIntent = new Intent(this, ManageLocalPost.class);
        startService(serviceIntent);

        Intent dataServiceIntent = new Intent(this, DataService.class);
        startService(dataServiceIntent);


        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this,
                DatabaseHelper.class);
        try {
            userDao = databaseHelper.getUserDao();
            postDao = databaseHelper.getPostDao();
            List<User> userList = userDao.queryForAll();
            if (userList.isEmpty()) {
                thisUser = null;
                return;
            }
            thisUser = userList.get(0);
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
        }
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
