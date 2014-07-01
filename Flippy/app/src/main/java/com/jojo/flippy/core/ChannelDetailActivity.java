package com.jojo.flippy.core;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.jojo.flippy.profile.ManageChannelActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class ChannelDetailActivity extends ActionBarActivity {
    private Intent intent;
    private String name;
    private String id;
    private String bio;
    private String creatorName;
    private String creatorAvatarURL = "";
    private String creatorEmail;
    private String communityId;
    private String image_thumbnail_url;
    private String image_url;
    private String communityName;
    private String channelId;
    private String channelName;
    String channelCommunityDetail = Flippy.channelsInCommunityURL;

    private ImageView imageViewChannelLarge, imageViewCreator;
    private TextView textViewChannelNameDetail;
    private TextView textViewChannelBio;
    private TextView textViewNameChannelDetailFullName, textViewChannelCreatorEmail;
    private LinearLayout linearLayoutChannelDetailContent;
    private ProgressBar progressBarLoadChannelDetail;
    private Button buttonSubscribeToChannel, buttonManageToChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        String channelDetailsURL = Flippy.channelDetailURL + channelId + "/";
        final String channelDetailSubscribeURL = Flippy.channelSubscribeURL + channelId + "/subscribe/";


        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(channelName);
        actionBar.setDisplayHomeAsUpEnabled(true);


        imageViewChannelLarge = (ImageView) findViewById(R.id.imageViewChannelLarge);
        imageViewCreator = (ImageView) findViewById(R.id.imageViewCreator);
        textViewChannelNameDetail = (TextView) findViewById(R.id.textViewChannelNameDetail);
        textViewChannelBio = (TextView) findViewById(R.id.textViewChannelBio);
        textViewNameChannelDetailFullName = (TextView) findViewById(R.id.textViewNameChannelDetailFullName);
        textViewChannelCreatorEmail = (TextView) findViewById(R.id.textViewChannelCreatorEmail);
        linearLayoutChannelDetailContent = (LinearLayout) findViewById(R.id.linearLayoutChannelDetailContent);
        progressBarLoadChannelDetail = (ProgressBar) findViewById(R.id.progressBarLoadChannelDetail);
        buttonSubscribeToChannel = (Button) findViewById(R.id.buttonSubscribeToChannel);
        buttonManageToChannel = (Button) findViewById(R.id.buttonManageToChannel);
        linearLayoutChannelDetailContent.setVisibility(View.GONE);
        buttonSubscribeToChannel.setVisibility(View.GONE);
        textViewNameChannelDetailFullName.setVisibility(View.GONE);
        textViewChannelCreatorEmail.setVisibility(View.GONE);
        imageViewCreator.setVisibility(View.GONE);

        textViewChannelBio.setText("");
        textViewChannelNameDetail.setText(channelName);

        //load the details of a channel
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadChannelDetail.setVisibility(View.GONE);
                        if (result != null) {
                            name = result.get("name").getAsString();
                            id = result.get("id").getAsString();
                            JsonObject creator = result.getAsJsonObject("creator");
                            creatorName = creator.get("first_name").getAsString() + " " + creator.get("last_name").getAsString();
                            creatorEmail = creator.get("email").getAsString();
                            if (!creator.get("avatar").isJsonNull()) {
                                creatorAvatarURL = creator.get("avatar").getAsString();
                            }
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
        buttonManageToChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("image_url", image_url);
                intent.setClass(ChannelDetailActivity.this, ManageChannelActivity.class);
                startActivity(intent);
            }
        });

        buttonSubscribeToChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JsonObject json = new JsonObject();
                json.addProperty("id", CommunityCenterActivity.regUserID);
                Ion.with(ChannelDetailActivity.this)
                        .load(channelDetailSubscribeURL)
                        .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null) {
                                    ToastMessages.showToastLong(ChannelDetailActivity.this, result.get("detail").getAsString());
                                    if (e != null) {
                                        ToastMessages.showToastLong(ChannelDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                    }

                                }
                            }

                            ;
                        });
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
                        progressBarLoadChannelDetail.setVisibility(View.GONE);
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
        Ion.with(imageViewChannelLarge)
                .placeholder(R.color.flippy_light_header)
                .animateIn(R.anim.fade_in)
                .load(image_url);
        Ion.with(imageViewCreator)
                .placeholder(R.color.flippy_light_header)
                .animateIn(R.anim.fade_in)
                .load(creatorAvatarURL);
        textViewChannelNameDetail.setText(channelName + " @ " + communityName);
        textViewNameChannelDetailFullName.setText(creatorName);
        textViewChannelCreatorEmail.setText(creatorEmail);
        textViewChannelBio.setText(bio);

        textViewChannelNameDetail.setVisibility(View.VISIBLE);
        textViewNameChannelDetailFullName.setVisibility(View.VISIBLE);
        textViewChannelCreatorEmail.setVisibility(View.VISIBLE);
        textViewChannelBio.setVisibility(View.VISIBLE);
        imageViewCreator.setVisibility(View.VISIBLE);

        if (CommunityCenterActivity.regUserEmail.equals(creatorEmail)) {
            buttonManageToChannel.setVisibility(View.VISIBLE);
            buttonSubscribeToChannel.setVisibility(View.GONE);
        } else {
            buttonSubscribeToChannel.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_channel_members) {
            intent.setClass(ChannelDetailActivity.this, ChannelMembers.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
