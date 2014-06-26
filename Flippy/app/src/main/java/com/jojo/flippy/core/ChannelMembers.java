package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.ChannelMemberAdapter;
import com.jojo.flippy.adapter.ProfileAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.adapter.SettingsAdapter;
import com.jojo.flippy.adapter.SettingsItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.ManageChannelActivity;
import com.jojo.flippy.profile.MemberDetailActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/12/14.
 */
public class ChannelMembers extends Activity {

    private String channelName;
    private String channelId;
    private String memberId;
    private String memberFirstName;
    private String totalMembers = "";

    private ChannelMemberAdapter channelMemberAdapter;

    private Intent intent;
    private ListView membershipList;
    private List<ProfileItem> ChannelMemberItem;
    private String membersURL = "/members/";
    private ActionBar actionBar;
    private ProgressBar progressBarMemberChannelLoader;
    private TextView textViewNoMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_members);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        String channelDetailsURL = Flippy.channelMembersURL + channelId + membersURL;


        actionBar = getActionBar();
        if (channelName != null && actionBar != null && totalMembers != null) {
            actionBar.setTitle(channelName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //finding all the views with their appropriate ids
        ChannelMemberItem = new ArrayList<ProfileItem>();
        membershipList = (ListView) findViewById(R.id.listViewChannelMembers);
        textViewNoMember = (TextView) findViewById(R.id.textViewNoMember);
        membershipList.setEmptyView(textViewNoMember);
        progressBarMemberChannelLoader = (ProgressBar) findViewById(R.id.progressBarMemberChannelLoader);
        channelMemberAdapter = new ChannelMemberAdapter(ChannelMembers.this,
                R.layout.channel_members_listview, ChannelMemberItem);
        membershipList.setAdapter(channelMemberAdapter);


        //load the channels members
        Ion.with(ChannelMembers.this)
                .load(channelDetailsURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarMemberChannelLoader.setVisibility(View.GONE);
                        if (!result.isJsonNull()) {
                            JsonArray profileArray = result.getAsJsonArray("results");
                            totalMembers = profileArray.size() + "";
                            for (int i = 0; i < profileArray.size(); i++) {
                                JsonObject item = profileArray.get(i).getAsJsonObject();
                                memberId = item.get("id").getAsString();
                                String url = "";
                                if (!item.get("avatar").isJsonNull()) {
                                    url = item.get("avatar").getAsString();
                                }
                                memberFirstName = item.get("first_name").getAsString();
                                ProfileItem profileItem = new ProfileItem(URI.create(url), item.get("email").getAsString(), memberFirstName + ", " + item.get("last_name").getAsString());
                                ChannelMemberItem.add(profileItem);
                            }
                            updateAdapter();


                        }
                        if (e != null) {
                            ToastMessages.showToastLong(ChannelMembers.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            return;
                        }

                    }
                });

        membershipList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items
                intent.setClass(ChannelMembers.this, MemberDetailActivity.class);
                intent.putExtra("memberId", memberId);
                intent.putExtra("memberFirstName", memberFirstName);
                startActivity(intent);

            }
        });


    }

    private void updateAdapter() {
        channelMemberAdapter.notifyDataSetChanged();
        actionBar.setSubtitle(totalMembers + " " + "member(s)");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
