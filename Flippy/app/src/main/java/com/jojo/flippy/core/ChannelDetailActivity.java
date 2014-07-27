package com.jojo.flippy.core;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.adapter.ChannelPostAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
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
    private String communityName = "";
    private String channelId;
    private String channelName;
    private ImageView imageViewChannelLarge, imageViewCreator;
    private TextView textViewChannelNameDetail, textViewChannelBio;
    private TextView textViewNameChannelDetailFullName, textViewChannelCreatorEmail, textViewNameSomePost;
    private Button buttonSubscribeToChannel, buttonManageToChannel, buttonUnSubscribeToChannel;
    private LinearLayout linearLayoutSubscriptions;
    private ContentLoadingProgressBar progressLoadChannel;
    private TabHost tabHost;
    private ListView listViewChannelPost;
    private ChannelPostAdapter ChannelsPostAdapter;
    private List<ProfileItem> userChannelItem;
    private String channelDetailsURL;

    private SuperToast superToast;
    private static String TAG = "ChannelDetailActivity";
    private ArrayList<String> channelAdmins = new ArrayList<String>();

    private Dao<Channels, Integer> channelDao;
    private List<Channels> channelList;

    private static ArrayList<String> channelIdList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);

        intent = getIntent();
        superToast = new SuperToast(ChannelDetailActivity.this);
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        channelDetailsURL = Flippy.channels + channelId + "/";
        String adminURL = Flippy.channels + channelId + "/admins/";
        getUserSubscribedChannelId();

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(channelName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressLoadChannel = (ContentLoadingProgressBar) findViewById(R.id.progressLoadChannel);
        imageViewChannelLarge = (ImageView) findViewById(R.id.imageViewChannelLarge);
        imageViewCreator = (ImageView) findViewById(R.id.imageViewCreator);
        textViewChannelNameDetail = (TextView) findViewById(R.id.textViewChannelNameDetail);
        textViewNameSomePost = (TextView) findViewById(R.id.textViewNameSomePost);
        textViewChannelBio = (TextView) findViewById(R.id.textViewChannelBio);
        textViewNameChannelDetailFullName = (TextView) findViewById(R.id.textViewNameChannelDetailFullName);
        textViewChannelCreatorEmail = (TextView) findViewById(R.id.textViewChannelCreatorEmail);
        buttonSubscribeToChannel = (Button) findViewById(R.id.buttonSubscribeToChannel);
        buttonManageToChannel = (Button) findViewById(R.id.buttonManageToChannel);
        buttonUnSubscribeToChannel = (Button) findViewById(R.id.buttonUnSubscribeToChannel);
        linearLayoutSubscriptions = (LinearLayout) findViewById(R.id.linearLayoutSubscriptions);
        linearLayoutSubscriptions.setVisibility(View.GONE);
        buttonUnSubscribeToChannel.setVisibility(View.GONE);
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

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec detailTabSpec = tabHost.newTabSpec("Details");
        detailTabSpec.setContent(R.id.tab1);
        detailTabSpec.setIndicator("Details", getResources().getDrawable(R.drawable.ic_action_about));

        TabHost.TabSpec postsTabSpec = tabHost.newTabSpec("Posts");
        postsTabSpec.setContent(R.id.tab2);
        postsTabSpec.setIndicator("Posts", getResources().getDrawable(R.drawable.ic_notices));


        tabHost.addTab(detailTabSpec);
        tabHost.addTab(postsTabSpec);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String specString) {
                setTabColor(tabHost);
            }
        });
        setTabColor(tabHost);

        getAdminsList(adminURL);
        //load the details of a channel
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    showSuperToast("Sorry, channel has been removed");
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
                                progressLoadChannel.setVisibility(View.GONE);
                                showViews();
                            } else if (e != null) {
                                Log.e("Error from channel detail", e.toString());
                                return;
                            } else {
                                Log.e(TAG, "Something else went wrong getting channel detail");
                            }

                        } catch (Exception exception) {
                            Log.e(TAG, "Error getting channel details " + channelId);
                        }
                    }
                });
        //load the post in this channel
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL + "posts/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    return;
                                } else if (result != null) {
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

                            } else if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title));
                                Log.e(TAG, "Error from Post " + e.toString());
                            } else {
                                Log.e(TAG, "Something else went wrong getting post in a channel");
                            }

                        } catch (Exception exception) {
                            Log.e(TAG, "Error getting post from the channel " + exception.toString());
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
                intent.putExtra("ImageName", channelName);
                intent.setClass(ChannelDetailActivity.this, ImagePreviewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCommunityName(String communityId) {
        Ion.with(ChannelDetailActivity.this)
                .load(Flippy.communitiesURL + communityId + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            communityName = result.get("name").getAsString();
                            textViewChannelNameDetail.setText(channelName + " @ " + communityName);
                        }
                        if (e != null) {
                            Log.e("Error from community", e.toString());
                            showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    private void updateChannelPostAdapter() {
        ChannelsPostAdapter.notifyDataSetChanged();
        Log.e("Total items", ChannelsPostAdapter.getCount() + "");
        if (!ChannelsPostAdapter.isEmpty()) {
            textViewNameSomePost.setVisibility(View.VISIBLE);
        }
    }

    private void showViews() {
        Ion.with(imageViewChannelLarge)
                .placeholder(R.color.flippy_light_header)
                .animateIn(R.anim.fade_in)
                .load(image_url);
        Ion.with(imageViewCreator)
                .placeholder(R.drawable.default_profile_picture)
                .animateIn(R.anim.fade_in)
                .load(creatorAvatarURL);
        textViewChannelNameDetail.setText(channelName);
        textViewNameChannelDetailFullName.setText(creatorName);
        textViewChannelCreatorEmail.setText(creatorEmail);
        textViewChannelBio.setText(bio);

        textViewChannelNameDetail.setVisibility(View.VISIBLE);
        textViewNameChannelDetailFullName.setVisibility(View.VISIBLE);
        textViewChannelCreatorEmail.setVisibility(View.VISIBLE);
        textViewChannelBio.setVisibility(View.VISIBLE);
        imageViewCreator.setVisibility(View.VISIBLE);

    }

    private void setSubscribe() {
        if (CommunityCenterActivity.userAuthToken.equals("")) {
            showSuperToast("sorry, request could not be made");
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("id", CommunityCenterActivity.regUserID);
        Ion.with(ChannelDetailActivity.this)
                .load(channelDetailsURL + "subscribe/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("results")) {
                                    showSuperToast(result.get("results").getAsString());
                                }
                                if (result.has("detail")) {
                                    showSuperToast("sorry, channel has been removed");
                                    return;
                                }

                            }
                            if (e != null) {
                                Log.e("Error", e.toString());
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title));
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, "Error subscribing to channel " + channelId + exception.toString());

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
                .load(channelDetailsURL + "unsubscribe/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("results")) {
                                    showSuperToastSuccess(result.get("results").getAsString());
                                } else if (result.has("detail")) {
                                    showSuperToast(result.get("detail").getAsString());
                                }
                            }
                            if (e != null) {
                                Log.e("Error", e.toString());
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title));
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, "Error un-subscribing to channel " + channelId + exception.toString());
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
        buttonManageToChannel.setVisibility(View.GONE);
        if (channelIdList.contains(channelId)) {
            buttonSubscribeToChannel.setVisibility(View.GONE);
            buttonUnSubscribeToChannel.setVisibility(View.VISIBLE);
        } else {
            buttonSubscribeToChannel.setVisibility(View.VISIBLE);
            buttonUnSubscribeToChannel.setVisibility(View.GONE);
        }


    }

    private void showManageButton() {
        linearLayoutSubscriptions.setVisibility(View.GONE);
        buttonSubscribeToChannel.setVisibility(View.GONE);
        buttonManageToChannel.setVisibility(View.VISIBLE);
        buttonUnSubscribeToChannel.setVisibility(View.GONE);
    }

    private void showSuperToast(String message) {
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setBackground(SuperToast.Background.RED);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setIcon(R.drawable.ic_action_warning_light, SuperToast.IconPosition.LEFT);
        superToast.setText(message);
        superToast.show();
    }

    private void showSuperToastSuccess(String message) {
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setBackground(SuperToast.Background.BLUE);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setIcon(R.drawable.icon_light_info, SuperToast.IconPosition.LEFT);
        superToast.setText(message);
        superToast.show();
    }

    public void setTabColor(TabHost tabhost) {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#E47E26"));
        }

        TextView tv = (TextView) tabhost.getCurrentTabView().findViewById(android.R.id.title);
        tv.setTextColor(Color.parseColor("#E47E26"));
    }

    private void getAdminsList(String url) {
        Ion.with(ChannelDetailActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result.has("detail")) {
                                Log.e(TAG, "Admin list not found");
                                return;
                            } else if (e != null) {
                                Log.e(TAG, e.toString());
                                return;
                            } else if (result != null) {
                                JsonArray adminArray = result.getAsJsonArray("results");
                                for (int i = 0; i < adminArray.size(); i++) {
                                    JsonObject item = adminArray.get(i).getAsJsonObject();
                                    channelAdmins.add(item.get("id").getAsString());
                                }
                            } else {
                                Log.e(TAG, "Something else happened");
                                return;
                            }
                            //managing the channel buttons
                            String userId = CommunityCenterActivity.regUserID;
                            if (channelAdmins.contains(userId)) {
                                showManageButton();
                            } else {
                                showSubscribeButton();
                            }
                        } catch (Exception error) {
                            Log.e(TAG, "Error occurred when getting admin list " + error.toString());
                        }
                    }
                });
    }

    private void getUserSubscribedChannelId() {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ChannelDetailActivity.this,
                    DatabaseHelper.class);
            channelDao = databaseHelper.getChannelDao();
            channelList = channelDao.queryForAll();
            channelIdList = new ArrayList<String>();
            if (!channelList.isEmpty()) {
                for (Channels channels : channelList) {
                    channelIdList.add(channels.channel_id);
                }
            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e(TAG, "Error getting all user channels");
        }
    }
}
