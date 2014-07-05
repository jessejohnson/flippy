package com.jojo.flippy.util;

/**
 * Created by bright on 7/3/14.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.core.NoticeDetailActivity;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.persistence.User;

import java.util.Calendar;
import java.util.List;


public class FlippyAlarmService extends Service {

    private NotificationManager notificationManager;
    private String noticeTitle = "Flippy reminder";
    private String noticeBody = "Flippy reminder";
    private String noticeId = "1";
    private String noticeSubtitle = "";

    private boolean isValidReminder = false;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        try {
            Calendar c = Calendar.getInstance();
            Log.e("current mills", c.getTimeInMillis() + "");
            Dao<Post, Integer> postDao = ((Flippy) getApplication()).postDao;
            QueryBuilder<Post, Integer> queryBuilder = postDao.queryBuilder();
            queryBuilder.where().eq("id", c.getTimeInMillis());
            PreparedQuery<Post> preparedQuery = queryBuilder.prepare();
            List<Post> postList = postDao.query(preparedQuery);

            if (!postList.isEmpty()) {
                Log.e("The entire notice list", postList.toString());
                Post post = postList.get(0);
                noticeTitle = post.notice_title;
                noticeBody = post.notice_body;
                noticeId = post.notice_id;
                noticeSubtitle = post.notice_subtitle;
                isValidReminder = true;
            }

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
        }


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_alarms)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentTitle("Flippy reminder")
                        .setContentInfo(noticeTitle)
                        .setContentText(noticeBody);

        Intent notificationIntent = new Intent();
        if (!isValidReminder) {
            notificationIntent.setClass(this, CommunityCenterActivity.class);
        } else {
            notificationIntent.putExtra("noticeId", noticeId);
            notificationIntent.putExtra("noticeTitle", noticeTitle);
            notificationIntent.putExtra("noticeSubtitle", noticeSubtitle);
            notificationIntent.putExtra("noticeBody", noticeBody);
            notificationIntent.setClass(this, NoticeDetailActivity.class);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(false);
        builder.setLights(R.color.flippy_orange, 500, 500);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        builder.setVibrate(pattern);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setStyle(new NotificationCompat.InboxStyle());
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        builder.setSound(alarmSound);
        //Add as notification
        notificationManager = (NotificationManager) getSystemService(this.getApplication().NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}