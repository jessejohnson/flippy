package com.jojo.flippy.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;

import java.util.Calendar;
import java.util.List;


public class ManageLocalPost extends Service {
    private Dao<Post, Integer> postDao;
    private int totalPost = 0;
    private int maxRowLength = 100;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Calendar calendar = Calendar.getInstance();

        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            if (!postList.isEmpty()) {
                totalPost = postList.size();
                if (totalPost > maxRowLength) {
                    //delete the rows
                }
                for (Post post : postList) {
                    int dateDiff = (int) (calendar.getTimeInMillis() - getPostLocalId(post)) / (1000 * 60 * 60 * 24);
                    if (dateDiff > 3) {
                        postDao.delete(post);
                    }
                }

            }

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();

        }

        return START_STICKY;
    }

    private long getPostLocalId(Post post) {
        return post.local_id;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
