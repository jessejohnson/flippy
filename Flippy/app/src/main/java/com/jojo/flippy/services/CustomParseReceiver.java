package com.jojo.flippy.services;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jojo.flippy.app.R;
import com.jojo.flippy.core.PushedNotices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bright on 8/8/14.
 */
public class CustomParseReceiver extends BroadcastReceiver {
    public static final String ACTION = "com.jojo.flippy.app.PUSH_NOTIFICATION";
    public static final String TAG = "CustomParseReceiver";
    public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
    public static final String PARSE_JSON_ALERT_CHANNEL = "com.parse.Channel";
    private static final int NOTIFICATION_ID = 1;
    public static int numMessages = 0;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, ">>onReceive()<< RECEIVED PUSH NOTIFICATION!");
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString(PARSE_JSON_ALERT_CHANNEL);
            JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
            Log.e(TAG, "got action " + action + " on channel " +channel+  " with:");
            if (action.equalsIgnoreCase(ACTION)) {
                String title = "title";
                if (json.has("title"))
                    title = json.getString("title");
                generateNotification(context, title, json);
            }else{
                Log.e(TAG,"Not the same intent");
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
        Log.d(ACTION, ">>onReceive()<< Vibrate Phone!");
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(300);
        }
    }

    private void generateNotification(Context context, String title, JSONObject json) {
        Intent intent = new Intent(context, PushedNotices.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        numMessages = 0;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(json.toString())
                        .setNumber(++numMessages);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

