package com.jojo.flippy.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class PreviewPost extends ActionBarActivity {
    private Intent intent;
    private String channelId,noticeTitle,noticeContent,lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_post);

        intent = getIntent();
        channelId = intent.getStringExtra("channelId");
        noticeTitle = intent.getStringExtra("noticeTitle");
        noticeContent = intent.getStringExtra("noticeContent");





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.preview_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_publish_post) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean createPostWithoutImage(String title, String body, String channel) {
        JsonObject json = new JsonObject();
        json.addProperty("title", title);
        json.addProperty("content", body);
        json.addProperty("channel_id", channel);
        Ion.with(PreviewPost.this, Flippy.allPostURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            ToastMessages.showToastLong(PreviewPost.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        } else {


                        }
                    }

                });

        return true;
    }
}
