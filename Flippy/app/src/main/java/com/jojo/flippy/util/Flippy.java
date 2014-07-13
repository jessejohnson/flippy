package com.jojo.flippy.util;

import android.app.Application;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.services.DataService;
import com.jojo.flippy.services.ManageLocalPost;

import java.util.List;

public class Flippy extends Application {
    //make the url accessible to all the activities
    public static String channels = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String users = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/";
    public static String channelsInCommunityURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/communities/";
    public static String communitiesURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/communities/";
    public static String allPostURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/posts/";
    private static Flippy sInstance;
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

        //letter start the data synchronization intent
        Intent dataServiceIntent = new Intent(getApplicationContext(), DataService.class);
        startService(dataServiceIntent);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
