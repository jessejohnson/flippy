package com.jojo.flippy.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.core.NoticeDetailActivity;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.util.Flippy;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by bright on 8/8/14.
 */
public class CustomParseReceiver extends BroadcastReceiver {
    public static final String ACTION = "com.jojo.flippy.app.PUSH_NOTIFICATION";
    public static final String TAG = "CustomParseReceiver";
    public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
    public static final String PARSE_JSON_ALERT_CHANNEL = "com.parse.Channel";
    private NotificationManager notificationManager;
    public static int numMessages = 0;
    private Dao<Post, Integer> postDao;
    private Dao<Channels, Integer> channelDao;
    private List<Channels> channelList;
    private static ArrayList<String> channelIdList = new ArrayList<String>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, ">>onReceive()<< RECEIVED PUSH NOTIFICATION!");
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class);
            channelDao = databaseHelper.getChannelDao();
            channelList = channelDao.queryForAll();
            for (Channels channels : channelList) {
                channelIdList.add(channels.channel_id);
            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e("ChannelDetailActivity", "Error getting all user channels");
        }
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString(PARSE_JSON_ALERT_CHANNEL);
            JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
            Log.e(TAG, "got action " + action + " on channel " + channel + " with:");
            if (action.equalsIgnoreCase(ACTION)) {
                String id = "";
                String channelId = "";
                if (json.has("title")) {
                    id = json.getString("noticeId");
                    channelId = json.getString("channelId");
                }
                if (channelIdList.contains(channelId)) {
                    getANotice(context, id);
                }
            } else {
                Log.e(TAG, "Not the same intent");
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }

    private void generateNotification(Context context, String id, String title, String body, String subTitle) {
        numMessages = 0;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_alarms)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                        .setContentTitle("Flippy notice")
                        .setAutoCancel(true)
                        .setContentInfo(title)
                        .setContentText(body);

        Intent notificationIntent = new Intent();
        notificationIntent.putExtra("noticeId", id);
        notificationIntent.putExtra("noticeTitle", title);
        notificationIntent.putExtra("noticeSubtitle", subTitle);
        notificationIntent.putExtra("noticeBody", body);
        notificationIntent.setClass(context, NoticeDetailActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setLights(R.color.flippy_orange, 500, 500);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        builder.setVibrate(pattern);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setStyle(new NotificationCompat.InboxStyle());
        Uri alarmSound = Uri.parse("android.resource://" + context.getPackageName() + "flippy.mp3");
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        builder.setSound(alarmSound);
        //Add as notification
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void getANotice(final Context context, final String id) {
        Ion.with(context)
                .load(Flippy.POST_URL + id + "/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    return;
                                }
                                JsonObject item = result.getAsJsonObject("author");
                                String author_email = item.get("email").getAsString();
                                String author_id = item.get("id").getAsString();
                                String author_first_name = item.get("first_name").getAsString();
                                String author_last_name = item.get("last_name").getAsString();
                                String avatar = "";
                                if (!item.get("avatar").isJsonNull()) {
                                    avatar = item.get("avatar").getAsString();
                                }
                                String avatar_thumb = "";
                                if (!item.get("avatar_thumb").isJsonNull()) {
                                    avatar_thumb = item.get("avatar_thumb").getAsString();
                                }
                                String channelId = result.get("channel").getAsString();
                                String content = result.get("content").getAsString();
                                String title = result.get("title").getAsString();
                                String time_stamp = result.get("timestamp").getAsString();
                                String image_link = "";
                                if (!result.get("image_url").isJsonNull()) {
                                    image_link = result.get("image_url").getAsString();
                                }
                                persistPost(context, id, title, content, image_link, time_stamp, author_email, author_id, author_first_name, author_last_name, avatar, avatar_thumb, channelId);
                            }
                            if (e != null) {
                                return;
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, "Try catch, something went wrong getting detail");
                        }
                    }
                });
    }

    private void persistPost(Context context,
                             String notice_id, String notice_title, String notice_body, String notice_image, String start_date, String author_email, String author_id, String author_first_name, String author_last_name, String authorAvatar, String authorAvatarThumb, String channel_id) {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            Calendar calendar = Calendar.getInstance();
            Post post = new Post(notice_id, notice_title, notice_body, notice_image, start_date, author_email, author_id, author_first_name, author_last_name, authorAvatar, authorAvatarThumb, channel_id, calendar.getTimeInMillis());
            postDao.create(post);
            String subtitle = author_first_name + ", " + author_last_name;
            generateNotification(context, notice_id, notice_title, notice_body, subtitle);

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();

        }

    }
}

