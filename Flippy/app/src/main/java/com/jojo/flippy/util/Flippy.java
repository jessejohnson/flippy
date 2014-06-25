package com.jojo.flippy.util;

import android.app.Application;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;

import java.util.List;

/**
 * Created by bright on 6/20/14.
 */
public class Flippy  extends Application{
    public Dao<User, Integer> userDao;
    public User thisUser;
    //make the url accessible to all the activities
    public static String regURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/signup/";
    public static String signInURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/users/login/";
    public static String channelsURL= "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static  String channelsInCommunityURL ="http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/communities/";
    public static  String userChannelsSubscribedURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/users/";
    public static  String channelDetailURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/channels/";
    public static String communitiesURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/communities/";


    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this,
                DatabaseHelper.class);
        try{
            userDao = databaseHelper.getUserDao();
            List<User> userList = userDao.queryForAll();
            if(userList.isEmpty()){
                thisUser = null;
                return;
            }
            thisUser = userList.get(0);
        }catch(java.sql.SQLException sqlE){
            sqlE.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
