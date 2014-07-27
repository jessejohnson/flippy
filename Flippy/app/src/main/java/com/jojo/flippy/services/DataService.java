package com.jojo.flippy.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.InternetConnectionDetector;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DataService extends Service {
    private Dao<Post, Integer> postDao;
    private ArrayList<String> savedPostIds;
    private static String TAG = "DataService";
    private String minutes;


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

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        minutes = SP.getString("sync_frequency", "180");
        long millis = Integer.parseInt(minutes) * 60 * 1000;


        savedPostIds = new ArrayList<String>();
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            if (!postList.isEmpty()) {
                for (Post post : postList) {
                    savedPostIds.add(post.notice_id);
                }
            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e(TAG, "Error occurred retrieving post ids");
        }
        InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        if (internetConnectionDetector.isConnectingToInternet()) {
            Timer dataTimer = new Timer();
            dataTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getNewPost();
                }
            }, millis, millis);
        }


        return START_STICKY;
    }

    private void getNewPost() {
        Ion.with(getApplicationContext())
                .load(Flippy.allPostURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
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
                                    String avatar = "flip";
                                    String avatarThumb = "flip";
                                    if (!item.get("image_url").isJsonNull()) {
                                        image_link = item.get("image_url").getAsString();
                                    }
                                    if (!author.get("avatar").isJsonNull()) {
                                        avatar = author.get("avatar").getAsString();
                                        avatarThumb = author.get("avatar_thumb").getAsString();
                                    }
                                    String authorEmail = author.get("email").getAsString();
                                    String authorId = author.get("id").getAsString();
                                    String authorFirstName = author.get("first_name").getAsString();
                                    String authorLastName = author.get("last_name").getAsString();
                                    Calendar calendar = Calendar.getInstance();
                                    Post new_post = new Post(id, title, content, image_link, startDate,
                                            authorEmail, authorId, authorFirstName, authorLastName, avatar, avatarThumb, channel, calendar.getTimeInMillis());
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
                                Log.e(TAG, "Error occurred internet connection");
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, "Try catch errors getting post data from server");
                        }

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
