package com.jojo.flippy.core;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class ChannelDetailActivity extends ActionBarActivity {
    private Intent intent;
    private String name;
    private String id;
    private String bio;
    private String creator;
    private String communityId;
    private String image_thumbnail_url;
    private String image_url;
    private String communityName;
    private String channelId;
    private String channelName;
    String channelCommunityDetail = Flippy.channelsInCommunityURL;

    private ImageView imageViewChannelLarge;
    private TextView textViewChannelNameDetail;
    private TextView textViewChannelBio;
    private TextView textViewLikes,textViewChannelCreator;
    private LinearLayout linearLayoutChannelDetailContent;
    private ProgressBar progressBarLoadChannelDetail;
    private Button buttonSubscribeToChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        String channelDetailsURL = Flippy.channelDetailURL + channelId + "/";


        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(channelName);
        actionBar.setDisplayHomeAsUpEnabled(true);


        imageViewChannelLarge = (ImageView) findViewById(R.id.imageViewChannelLarge);
        textViewChannelNameDetail = (TextView) findViewById(R.id.textViewChannelNameDetail);
        textViewChannelBio = (TextView) findViewById(R.id.textViewChannelBio);
        textViewLikes = (TextView) findViewById(R.id.textViewStatus);
        textViewChannelCreator = (TextView) findViewById(R.id.textViewChannelCreator);
        linearLayoutChannelDetailContent = (LinearLayout)findViewById(R.id.linearLayoutChannelDetailContent);
        progressBarLoadChannelDetail = (ProgressBar)findViewById(R.id.progressBarLoadChannelDetail);
        buttonSubscribeToChannel = (Button)findViewById(R.id.buttonSubscribeToChannel);
        linearLayoutChannelDetailContent.setVisibility(View.GONE);
        buttonSubscribeToChannel.setVisibility(View.GONE);

        textViewChannelBio.setText("");
        textViewLikes.setText("");
        textViewChannelCreator.setText("");
        textViewChannelNameDetail.setText(channelName);


        //load the details of a channel
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            name = result.get("name").getAsString();
                            id = result.get("id").getAsString();
                            creator = result.get("creator").getAsString();
                            bio = result.get("bio").getAsString();
                            communityId = result.get("community").getAsString();
                            image_thumbnail_url = result.get("image_thumbnail_url").getAsString();
                            image_url = result.get("image_url").getAsString();
                            secondAsyncTask(communityId);
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(ChannelDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });

    }

    private void secondAsyncTask(String communityURL) {
        //load the creator of the channel
        Ion.with(ChannelDetailActivity.this)
                .load(channelCommunityDetail + communityURL + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            communityName = result.get("name").getAsString();
                            showViews();

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(ChannelDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    private void showViews() {
        linearLayoutChannelDetailContent.setVisibility(View.VISIBLE);
        progressBarLoadChannelDetail.setVisibility(View.GONE);
        buttonSubscribeToChannel.setVisibility(View.VISIBLE);
        Ion.with(imageViewChannelLarge)
                .placeholder(R.color.flippy_light_header)
                .load(image_url);

        textViewChannelNameDetail.setText(channelName);
        textViewChannelBio.setText(bio);
        textViewLikes.setText("In " + communityName);
        textViewChannelCreator.setText(creator);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_subscribe) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
