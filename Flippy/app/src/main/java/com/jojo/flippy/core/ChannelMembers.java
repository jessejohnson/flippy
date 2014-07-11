package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Channel;
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
    private boolean isManage = false;


    private String userId;
    private String userEmail;
    private String userFullName;

    private TextView textViewNoChannelMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_members);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        isManage = intent.getBooleanExtra("isManage", false);
        String channelDetailsURL = Flippy.channelMembersURL + channelId + membersURL;


        actionBar = getActionBar();
        if (channelName != null && actionBar != null && totalMembers != null) {
            actionBar.setTitle(channelName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //finding all the views with their appropriate ids
        ChannelMemberItem = new ArrayList<ProfileItem>();
        membershipList = (ListView) findViewById(R.id.listViewChannelMembers);
        textViewNoChannelMember = (TextView) findViewById(R.id.textViewNoChannelMember);
        textViewNoChannelMember.setVisibility(View.GONE);
        progressBarMemberChannelLoader = (ProgressBar) findViewById(R.id.progressBarMemberChannelLoader);
        channelMemberAdapter = new ChannelMemberAdapter(ChannelMembers.this,
                R.layout.channel_members_listview, ChannelMemberItem, isManage);
        membershipList.setAdapter(channelMemberAdapter);


        //load the channels members
        Ion.with(ChannelMembers.this)
                .load(channelDetailsURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarMemberChannelLoader.setVisibility(View.GONE);
                        if (result != null) {
                            if (result.has("detail")) {

                                textViewNoChannelMember.setVisibility(View.VISIBLE);
                                textViewNoChannelMember.setText("Sorry, channel has been removed");
                                return;
                            }
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
                                ProfileItem profileItem = new ProfileItem(URI.create(url), item.get("email").getAsString(), memberFirstName + ", " + item.get("last_name").getAsString(), memberId);
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
        if (isManage) {
            membershipList.setClickable(false);
        }


        membershipList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items

                intent.setClass(ChannelMembers.this, MemberDetailActivity.class);
                TextView textViewMemberId = (TextView) view.findViewById(R.id.textViewMemberId);
                TextView textViewMemberEmail = (TextView) view.findViewById(R.id.textViewMemberEmail);
                TextView textViewMemberFullName = (TextView) view.findViewById(R.id.textViewMemberFullName);
                userId = textViewMemberId.getText().toString();
                userEmail = textViewMemberEmail.getText().toString();
                userFullName = textViewMemberFullName.getText().toString();
                if (isManage) {
                    ToastMessages.showToastLong(ChannelMembers.this, "clicked");
                    intent.putExtra("memberEmail", userEmail);
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                } else {
                    intent.putExtra("memberId", userId);
                    intent.putExtra("memberEmail", userEmail);
                    intent.putExtra("memberFullName", userFullName);
                    startActivity(intent);
                }


            }
        });


    }

    private void updateAdapter() {
        channelMemberAdapter.notifyDataSetChanged();
        actionBar.setSubtitle(totalMembers + " " + "member(s)");
        if (channelMemberAdapter.isEmpty()) {
            textViewNoChannelMember.setVisibility(View.VISIBLE);
            textViewNoChannelMember.setText("Channel has no member");
        }

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
