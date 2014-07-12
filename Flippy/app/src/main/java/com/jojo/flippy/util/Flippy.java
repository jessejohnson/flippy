package com.jojo.flippy.util;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.services.DataService;

import java.util.List;

/**
 * Created by bright on 6/20/14.
 */
public class Flippy extends Application {
    public Dao<User, Integer> userDao;
    public Dao<Post, Integer> postDao;
    public User thisUser;

    private static Flippy sInstance;
    private RequestQueue mRequestQueue;
    //make the url accessible to all the activities
    public static String channels = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String userBasicURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/me/";
    public static String userCommunityURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/";
    public static String regURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/signup/";
    public static String signInURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/users/login/";
    public static String channelsURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String channelsInCommunityURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/communities/";
    public static String userChannelsSubscribedURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/users/";
    public static String channelDetailURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String communitiesURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/communities/";
    public static String channelMembersURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String channelSubscribeURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String allPostURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/posts/";


    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;

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

        Intent dataServiceIntent = new Intent(getApplicationContext(), DataService.class);
        startService(dataServiceIntent);
    }

    public synchronized static Flippy getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
