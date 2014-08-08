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
import com.jojo.flippy.adapter.ChannelSelectItem;
import com.jojo.flippy.adapter.ChannelSelectionItemAdapter;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SelectChannelActivity extends ActionBarActivity {
    private Intent intent;
    private ChannelSelectionItemAdapter userChannelsAdapter;
    private ListView userChannelList;
    private List<ChannelSelectItem> userChannelItem;
    private ProgressBar progressBarLoadUserChannels;
    private TextView textViewNoChannel;
    private TextView textViewNoChannelHelp;
    private String channelId;
    private String channelName;
    private String channelBio;
    private String subTitle = "a step to more to go";
    private boolean isReFlip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_channel);


        intent = getIntent();
        isReFlip = intent.getBooleanExtra("isReFlip", false);
        String url = Flippy.USERS_URL + CommunityCenterActivity.regUserID + "/admin_channels/";
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subTitle);
        }

        //finding all the views with their appropriate ids
        userChannelItem = new ArrayList<ChannelSelectItem>();
        userChannelList = (ListView) findViewById(R.id.listViewUserChannelSelect);
        textViewNoChannel = (TextView) findViewById(R.id.textViewNoChannel);
        textViewNoChannel.setVisibility(View.GONE);
        textViewNoChannelHelp = (TextView) findViewById(R.id.textViewNoChannelHelp);
        textViewNoChannelHelp.setVisibility(View.GONE);
        progressBarLoadUserChannels = (ProgressBar) findViewById(R.id.progressBarLoadUserChannels);
        userChannelsAdapter = new ChannelSelectionItemAdapter(SelectChannelActivity.this,
                R.layout.channel_select_listview, userChannelItem);
        userChannelList.setAdapter(userChannelsAdapter);
        //load the CHANNELS_URL of user
        Ion.with(SelectChannelActivity.this)
                .load(url)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadUserChannels.setVisibility(View.INVISIBLE);
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    Log.e("SelectChannelActivity",result.toString());
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
                                    channelBio = item.get("bio").getAsString();
                                    String url = "";
                                    if (!item.get("image_thumbnail_url").isJsonNull()) {
                                        url = item.get("image_thumbnail_url").getAsString();
                                    }
                                    channelName = item.get("name").getAsString();
                                    if (creatorId.equals(CommunityCenterActivity.regUserID)) {
                                        ChannelSelectItem channelItem = new ChannelSelectItem(URI.create(url), channelId, channelName, channelBio);
                                        userChannelItem.add(channelItem);
                                    }
                                }
                                updateAdapter();

                            }
                            if (e != null) {
                                textViewNoChannelHelp.setVisibility(View.VISIBLE);
                                textViewNoChannel.setVisibility(View.VISIBLE);
                                return;
                            }

                        } catch (Exception exception) {
                            Log.e("Error occurred", "Error retrieving a list of user admin CHANNELS_URL");
                        }


                    }
                });

        userChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView textViewChannelId = (TextView) view.findViewById(R.id.textViewChannelId);
                TextView textViewChannelName = (TextView) view.findViewById(R.id.textViewChannelName);
                String channelId = textViewChannelId.getText().toString();
                String channelName = textViewChannelName.getText().toString();
                intent.putExtra("channelId", channelId);
                intent.putExtra("channelName", channelName);
                if (isReFlip) {
                    intent.setClass(SelectChannelActivity.this, NoticeDetailActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    intent.setClass(SelectChannelActivity.this, CreateNoticeActivity.class);
                    startActivity(intent);
                }


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


}
