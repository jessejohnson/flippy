package com.jojo.flippy.core;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.AdminPerson;
import com.jojo.flippy.adapter.ChannelMemberAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.MemberDetailActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SelectChannelActivity extends ActionBarActivity {
    private Intent intent;
    private ChannelMemberAdapter userChannelsAdapter;

    private ListView userChannelList;
    private List<ProfileItem> userChannelItem;
    private String userChannels = "/subscriptions/";
    private ProgressBar progressBarLoadUserChannels;
    private TextView textViewNoChannel;
    private TextView textViewNoChannelHelp;
    private String channelId;
    private String channelName;
    private String subTitle = "a step to more to go";
    private boolean isReFLIP;
    private ArrayList<String> admins = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_channel);


        intent = getIntent();
        isReFLIP = intent.getBooleanExtra("isReFLIP", false);
        String url = Flippy.users + CommunityCenterActivity.regUserID + userChannels;
        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);


        //finding all the views with their appropriate ids
        userChannelItem = new ArrayList<ProfileItem>();
        userChannelList = (ListView) findViewById(R.id.listViewUserChannelSelect);
        textViewNoChannel = (TextView) findViewById(R.id.textViewNoChannel);
        textViewNoChannel.setVisibility(View.GONE);
        textViewNoChannelHelp = (TextView) findViewById(R.id.textViewNoChannelHelp);
        textViewNoChannelHelp.setVisibility(View.GONE);
        progressBarLoadUserChannels = (ProgressBar) findViewById(R.id.progressBarLoadUserChannels);
        userChannelsAdapter = new ChannelMemberAdapter(SelectChannelActivity.this,
                R.layout.channel_members_listview, userChannelItem, false);
        userChannelList.setAdapter(userChannelsAdapter);

        //getAdminList(Flippy.channels+channelId+"/admins/");
        //load the channels of user
        Ion.with(SelectChannelActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadUserChannels.setVisibility(View.INVISIBLE);
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    Crouton.makeText(SelectChannelActivity.this, "The requested channel was not found", Style.ALERT)
                                            .show();
                                    return;
                                }
                                JsonArray channelArray = result.getAsJsonArray("results");
                                for (int i = 0; i < channelArray.size(); i++) {
                                    JsonObject item = channelArray.get(i).getAsJsonObject();
                                    JsonObject creator = item.get("creator").getAsJsonObject();
                                    String creatorId = creator.get("id").getAsString();
                                    channelId = item.get("id").getAsString();
                                    String url = "";
                                    if (!item.get("image_thumbnail_url").isJsonNull()) {
                                        url = item.get("image_thumbnail_url").getAsString();
                                    }
                                    channelName = item.get("name").getAsString();
                                    if (creatorId.equals(CommunityCenterActivity.regUserID)) {
                                        ProfileItem channelItem = new ProfileItem(URI.create(url), channelName, channelId, "");
                                        userChannelItem.add(channelItem);
                                    }
                                }
                                updateAdapter();

                            }
                            if (e != null) {
                                textViewNoChannelHelp.setVisibility(View.VISIBLE);
                                textViewNoChannel.setVisibility(View.VISIBLE);
                                ToastMessages.showToastLong(SelectChannelActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                return;
                            }

                        } catch (Exception exception) {
                            Log.e("Error occurred", "Error retrieving a list of user subscribed channels");
                        }


                    }
                });

        userChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView textViewMemberLastName = (TextView) view.findViewById(R.id.textViewMemberFullName);
                TextView textViewMemberFirstName = (TextView) view.findViewById(R.id.textViewMemberEmail);
                String channelId = textViewMemberLastName.getText().toString();
                String channelName = textViewMemberFirstName.getText().toString();
                if (isReFLIP) {
                    intent.setClass(SelectChannelActivity.this, NoticeDetailActivity.class);
                    intent.putExtra("channelId", channelId);
                    setResult(1, intent);
                }
                intent.setClass(SelectChannelActivity.this, CreateNoticeActivity.class);
                intent.putExtra("channelId", channelId);
                intent.putExtra("channelName", channelName);
                startActivity(intent);


            }
        });
        textViewNoChannelHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(SelectChannelActivity.this, CreateChannelActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateAdapter() {
        userChannelsAdapter.notifyDataSetChanged();
        if (userChannelsAdapter.isEmpty()) {
            textViewNoChannelHelp.setVisibility(View.VISIBLE);
            textViewNoChannel.setVisibility(View.VISIBLE);
        }

    }

    private void getAdminList(String url) {
        Ion.with(SelectChannelActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result.has("detail")) {
                                Log.e("Error form get admin list", "Admin list not found");
                                return;
                            }
                            if (result != null) {
                                JsonArray adminArray = result.getAsJsonArray("results");
                                for (int i = 0; i < adminArray.size(); i++) {
                                    JsonObject item = adminArray.get(i).getAsJsonObject();
                                    admins.add(item.get("id").getAsString());
                                }
                            }
                            if (e != null) {
                                Log.e("Error", "sorry, internet connection occurred");
                            }
                        } catch (Exception error) {
                            Log.e("Error try catch", "Error occurred when getting admin list");
                        }
                    }
                });
    }

}
