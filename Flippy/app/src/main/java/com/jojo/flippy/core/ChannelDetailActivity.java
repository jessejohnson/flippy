package com.jojo.flippy.core;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.ChannelMemberAdapter;
import com.jojo.flippy.adapter.ChannelPostAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.ImagePreviewActivity;
import com.jojo.flippy.profile.ManageChannelActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


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
    private ImageView imageViewChannelLarge, imageViewCreator;
    private TextView textViewChannelNameDetail;
    private TextView textViewChannelBio;
    private TextView textViewNameChannelDetailFullName, textViewChannelCreatorEmail, textViewNameSomePost;
    private LinearLayout linearLayoutChannelDetailContent;
    private ProgressBar progressBarLoadChannelDetail;
    private Button buttonSubscribeToChannel, buttonManageToChannel, buttonUnSubscribeToChannel;
    private LinearLayout linearLayoutSubscriptions;

    private ListView listViewChannelPost;
    private ChannelPostAdapter ChannelsPostAdapter;
    private List<ProfileItem> userChannelItem;
    private String channelDetailsURL;

    private SuperToast superToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);

        intent = getIntent();
        superToast = new SuperToast(ChannelDetailActivity.this);
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        channelDetailsURL = Flippy.channels + channelId + "/";

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(channelName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        imageViewChannelLarge = (ImageView) findViewById(R.id.imageViewChannelLarge);
        imageViewCreator = (ImageView) findViewById(R.id.imageViewCreator);
        textViewChannelNameDetail = (TextView) findViewById(R.id.textViewChannelNameDetail);
        textViewNameSomePost = (TextView) findViewById(R.id.textViewNameSomePost);
        textViewChannelBio = (TextView) findViewById(R.id.textViewChannelBio);
        textViewNameChannelDetailFullName = (TextView) findViewById(R.id.textViewNameChannelDetailFullName);
        textViewChannelCreatorEmail = (TextView) findViewById(R.id.textViewChannelCreatorEmail);
        linearLayoutChannelDetailContent = (LinearLayout) findViewById(R.id.linearLayoutChannelDetailContent);
        progressBarLoadChannelDetail = (ProgressBar) findViewById(R.id.progressBarLoadChannelDetail);
        buttonSubscribeToChannel = (Button) findViewById(R.id.buttonSubscribeToChannel);
        buttonManageToChannel = (Button) findViewById(R.id.buttonManageToChannel);
        buttonUnSubscribeToChannel = (Button) findViewById(R.id.buttonUnSubscribeToChannel);
        linearLayoutSubscriptions = (LinearLayout) findViewById(R.id.linearLayoutSubscriptions);
        linearLayoutSubscriptions.setVisibility(View.GONE);
        buttonUnSubscribeToChannel.setVisibility(View.GONE);
        linearLayoutChannelDetailContent.setVisibility(View.GONE);
        buttonSubscribeToChannel.setVisibility(View.GONE);
        textViewNameChannelDetailFullName.setVisibility(View.GONE);
        textViewChannelCreatorEmail.setVisibility(View.GONE);
        imageViewCreator.setVisibility(View.GONE);
        textViewNameSomePost.setVisibility(View.GONE);

        listViewChannelPost = (ListView) findViewById(R.id.listViewChannelPost);
        userChannelItem = new ArrayList<ProfileItem>();
        ChannelsPostAdapter = new ChannelPostAdapter(ChannelDetailActivity.this,
                R.layout.channel_post_listview, userChannelItem);
        listViewChannelPost.setAdapter(ChannelsPostAdapter);

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
                            if (result.has("detail")) {
                                showSuperToast("Sorry, channel has been removed");
                                finish();
                                return;
                            }
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
                            getCommunityName(communityId);
                        }
                        if (e != null) {

                        }

                    }
                });
        //load the post i this channel
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL + "posts/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadChannelDetail.setVisibility(View.GONE);
                        if (result != null) {
                            if (result.has("detail")) {
                                return;
                            } else {
                                //showing only the first ten posts
                                int arrayEnd = 10;
                                JsonArray channelPostsArray = result.getAsJsonArray("results");
                                if (channelPostsArray.size() < arrayEnd) {
                                    arrayEnd = channelPostsArray.size();
                                }
                                for (int i = 0; i < arrayEnd; i++) {
                                    JsonObject item = channelPostsArray.get(i).getAsJsonObject();
                                    String title = item.get("title").getAsString();
                                    String content = item.get("content").getAsString();
                                    String url = "";
                                    if (!item.get("image_thumbnail_url").isJsonNull()) {
                                        url = item.get("image_thumbnail_url").getAsString();
                                    }
                                    ProfileItem channelItem = new ProfileItem(URI.create(url), title, content, "");
                                    userChannelItem.add(channelItem);

                                }
                                updateChannelPostAdapter();
                            }

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
                setSubscribe();
            }

        });
        buttonUnSubscribeToChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUnSubscribe();
            }

        });
        imageViewChannelLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("avatar", image_url);
                intent.setClass(ChannelDetailActivity.this, ImagePreviewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCommunityName(String communityURL) {
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL + communityURL + "/")
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

    private void updateChannelPostAdapter() {
        ChannelsPostAdapter.notifyDataSetChanged();
        if (!ChannelsPostAdapter.isEmpty()) {
            textViewNameSomePost.setVisibility(View.VISIBLE);
        }
    }

    private void showViews() {
        linearLayoutChannelDetailContent.setVisibility(View.VISIBLE);
        progressBarLoadChannelDetail.setVisibility(View.GONE);
        Ion.with(imageViewChannelLarge)
                .placeholder(R.color.flippy_light_header)
                .animateIn(R.anim.fade_in)
                .load(image_url);
        Ion.with(imageViewCreator)
                .placeholder(R.drawable.default_profile_picture)
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
            showManageButton();
        } else {
            showSubscribeButton();
        }
    }

    private void setSubscribe() {
        if (CommunityCenterActivity.userAuthToken.equals("")) {
            ToastMessages.showToastLong(ChannelDetailActivity.this, "Sorry, request cannot be made");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("id", CommunityCenterActivity.regUserID);
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL + "/subscribe/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            if (result.has("results")) {
                                ToastMessages.showToastLong(ChannelDetailActivity.this, result.get("results").getAsString());
                            }
                            if (result.has("detail")) {
                                ToastMessages.showToastLong(ChannelDetailActivity.this, "Sorry, channel has been removed");
                                finish();
                                return;
                            }

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(ChannelDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }
                    }

                });
    }

    private void setUnSubscribe() {
        if (CommunityCenterActivity.userAuthToken.equals("")) {
            ToastMessages.showToastLong(ChannelDetailActivity.this, "Sorry, request cannot be made");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("id", CommunityCenterActivity.regUserID);
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL + "/unsubscribe/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            if (result.has("results")) {
                                ToastMessages.showToastLong(ChannelDetailActivity.this, result.get("results").getAsString());
                            }
                            if (result.has("detail")) {
                                ToastMessages.showToastLong(ChannelDetailActivity.this, result.get("detail").getAsString());
                            }
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(ChannelDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }
                    }

                    ;
                });
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

    private void showSubscribeButton() {
        linearLayoutSubscriptions.setVisibility(View.VISIBLE);
        buttonSubscribeToChannel.setVisibility(View.VISIBLE);
        buttonManageToChannel.setVisibility(View.GONE);
        buttonUnSubscribeToChannel.setVisibility(View.VISIBLE);
    }

    private void showManageButton() {
        linearLayoutSubscriptions.setVisibility(View.GONE);
        buttonSubscribeToChannel.setVisibility(View.GONE);
        buttonManageToChannel.setVisibility(View.VISIBLE);
        buttonUnSubscribeToChannel.setVisibility(View.GONE);
    }
    private void showSuperToast(String message){
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setBackground(SuperToast.Background.RED);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setIcon(R.drawable.ic_action_warning, SuperToast.IconPosition.LEFT);
        superToast.setText(message);
        superToast.show();
    }
}
