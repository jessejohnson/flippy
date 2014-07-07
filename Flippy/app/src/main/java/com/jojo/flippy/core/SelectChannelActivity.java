package com.jojo.flippy.core;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.ChannelMemberAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_channel);


        intent = getIntent();
        String url = Flippy.userChannelsSubscribedURL + CommunityCenterActivity.regUserID + userChannels;
        ActionBar actionBar = getActionBar();



        //finding all the views with their appropriate ids
        userChannelItem = new ArrayList<ProfileItem>();
        userChannelList = (ListView) findViewById(R.id.listViewUserChannelSelect);
        textViewNoChannel = (TextView) findViewById(R.id.textViewNoChannel);
        textViewNoChannel.setVisibility(View.GONE);
        textViewNoChannelHelp = (TextView) findViewById(R.id.textViewNoChannelHelp);
        textViewNoChannelHelp.setVisibility(View.GONE);
        progressBarLoadUserChannels = (ProgressBar) findViewById(R.id.progressBarLoadUserChannels);
        userChannelsAdapter = new ChannelMemberAdapter(SelectChannelActivity.this,
                R.layout.channel_members_listview, userChannelItem);
        userChannelList.setAdapter(userChannelsAdapter);


        //load the channels of user
        Ion.with(SelectChannelActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadUserChannels.setVisibility(View.INVISIBLE);
                        if(result.has("detail")){
                            Crouton.makeText(SelectChannelActivity.this, "The requested user was not found", Style.ALERT)
                                    .show();
                            return;
                        }
                        if (result != null && !result.has("detail")) {
                            JsonArray channelArray = result.getAsJsonArray("results");
                            for (int i = 0; i < channelArray.size(); i++) {
                                JsonObject item = channelArray.get(i).getAsJsonObject();
                                channelId = item.get("id").getAsString();
                                String url = "";
                                if (!item.get("image_thumbnail_url").isJsonNull()) {
                                    url = item.get("image_thumbnail_url").getAsString();
                                }
                                channelName = item.get("name").getAsString();
                                ProfileItem channelItem = new ProfileItem(URI.create(url), channelName, channelId);
                                userChannelItem.add(channelItem);
                            }
                            updateAdapter();
                            if (channelArray.size() == 0) {
                                textViewNoChannelHelp.setVisibility(View.VISIBLE);
                                textViewNoChannel.setVisibility(View.VISIBLE);
                            }
                        }
                        if (e != null) {
                            textViewNoChannelHelp.setVisibility(View.VISIBLE);
                            textViewNoChannel.setVisibility(View.VISIBLE);
                            ToastMessages.showToastLong(SelectChannelActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            return;
                        }

                    }
                });

        userChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView textViewMemberLastName = (TextView)view.findViewById(R.id.textViewMemberLastName);
                TextView textViewMemberFirstName = (TextView)view.findViewById(R.id.textViewMemberFirstName);
                String channelId = textViewMemberLastName.getText().toString();
                String channelName = textViewMemberFirstName.getText().toString();
                intent.setClass(SelectChannelActivity.this,CreateNoticeActivity.class);
                intent.putExtra("channelId",channelId);
                intent.putExtra("channelName",channelName);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
