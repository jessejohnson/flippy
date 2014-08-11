package com.jojo.flippy.services;

/**
 * Created by bright on 8/10/14.
 */

import java.io.File;
import java.util.List;


import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.util.ImageDecoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;


public class UpdateWidgetService extends Service {
    private static final String ACTION = "com.jojo.flippy.core.CommunityCenterActivity";
    private Dao<Post, Integer> postDao;
    private String postTitle = "";
    private String postContent = "";
    private String postImage = "";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());

        final ComponentName thisWidget = new ComponentName(getApplicationContext(),
                FlippyWidget.class);

        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            if (!postList.isEmpty()) {
                Post post = postList.get(postList.size() - 1);
                postTitle = post.notice_title;
                postContent = post.notice_body;
                postImage = post.notice_image;
            } else {
                postTitle = "Flippy notices";
                postContent = "Flippy will shortly start updating you, cheers";
                postImage = "";
            }

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e("UpdateWidgetService", sqlE.toString());
        }


        final RemoteViews remoteViews = new RemoteViews(this
                .getApplicationContext().getPackageName(),
                R.layout.widget_layout
        );

        remoteViews.setTextViewText(R.id.textViewWidgetNoticeBody,
                postContent);

        remoteViews.setTextViewText(R.id.textViewWidgetNoticeTitle,
                postTitle);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(postImage, new FileAsyncHttpResponseHandler(getApplicationContext()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Bitmap bitmap = ImageDecoder.decodeFile(response.getAbsolutePath());
                remoteViews.setImageViewBitmap(R.id.imageViewWidgetNotice, bitmap);
                response.deleteOnExit();
            }

            @Override
            public void onFailure(Throwable e, File response) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                Intent clickIntent = new Intent(getApplicationContext(),
                        FlippyWidget.class);

                clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                        thisWidget);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.imageViewWidgetNotice, pendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                stopSelf();
            }
        });


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}