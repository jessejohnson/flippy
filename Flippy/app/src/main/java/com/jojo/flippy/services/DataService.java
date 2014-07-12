package com.jojo.flippy.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.InternetConnectionDetector;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bright on 7/8/14.
 */
public class DataService extends Service {
    private Dao<Post, Integer> postDao;
    private ArrayList<String> savedPostIds;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        savedPostIds = new ArrayList<String>();
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            if (!postList.isEmpty()) {
                for (int i = 0; i < postList.size(); i++) {
                    Post post = postList.get(i);
                    savedPostIds.add(post.notice_id);
                }
            }

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();

        }
        InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        if (internetConnectionDetector.isConnectingToInternet()) {

        }
        Timer dataTimer = new Timer();
        dataTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Ion.with(getApplicationContext())
                        .load(Flippy.allPostURL)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null && result.has("results")) {
                                    JsonArray postArray = result.getAsJsonArray("results");
                                    for (int i = 0; i < postArray.size(); i++) {
                                        JsonObject item = postArray.get(i).getAsJsonObject();
                                        JsonObject author = item.getAsJsonObject("author");
                                        String startDate = item.get("timestamp").getAsString();
                                        String title = item.get("title").getAsString();
                                        String id = item.get("id").getAsString();
                                        String content = item.get("content").getAsString();
                                        String channel = item.get("channel").getAsString();
                                        String image_link = "flip";
                                        if (!item.get("image_url").isJsonNull()) {
                                            image_link = item.get("image_url").getAsString();
                                        }
                                        String authorEmail = author.get("email").getAsString();
                                        String authorId = author.get("id").getAsString();
                                        String authorFirstName = author.get("first_name").getAsString();
                                        String authorLastName = author.get("last_name").getAsString();
                                        Calendar calendar = Calendar.getInstance();
                                        Post new_post = new Post(id, title, content, image_link, startDate,
                                                authorEmail, authorId, authorFirstName, authorLastName, channel, calendar.getTimeInMillis() + "");
                                        try {
                                            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(),
                                                    DatabaseHelper.class);
                                            postDao = databaseHelper.getPostDao();
                                            if (!savedPostIds.contains(id)) {
                                                postDao.createOrUpdate(new_post);
                                            }
                                        } catch (java.sql.SQLException sqlE) {
                                            sqlE.printStackTrace();

                                        }
                                    }

                                    Intent postIntent = new Intent();
                                    postIntent.setAction("newPostArrived");
                                    sendBroadcast(postIntent);
                                }
                                if (e != null) {
                                    ToastMessages.showToastLong(getApplicationContext(), getResources().getString(R.string.internet_connection_error_dialog_title));
                                }
                            }
                        });
            }
        }, 30 * 60 * 1000, 30 * 60 * 1000);

        return START_STICKY;

    }

    ;

    void makeApiCall() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
