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
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this,
                DatabaseHelper.class);
        try{
            userDao = databaseHelper.getUserDao();
            User user = new User(System.currentTimeMillis()+"");
            userDao.create(user);

            List<User> userList = userDao.queryForAll();

            Log.e("userList", userList.get(0).toString()) ;

        }catch(java.sql.SQLException sqlE){
            sqlE.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
