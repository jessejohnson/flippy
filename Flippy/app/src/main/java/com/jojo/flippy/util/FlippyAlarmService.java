package com.jojo.flippy.util;

/**
 * Created by bright on 7/3/14.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.core.NoticeDetailActivity;


public class FlippyAlarmService extends Service {

    private NotificationManager mManager;

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

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent alarmServiceIntent = new Intent(this.getApplicationContext(), NoticeDetailActivity.class);

        Notification notification = new Notification(R.drawable.ic_launcher, "This is a test message!", System.currentTimeMillis());
        alarmServiceIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, alarmServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), "AlarmManagerDemo", "This is a test message!", pendingNotificationIntent);

        mManager.notify(0, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}