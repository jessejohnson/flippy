package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.ChannelMemberAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.ManageChannelActivity;
import com.jojo.flippy.profile.MemberDetailActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ChannelMembers extends ActionBarActivity {

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
        String channelDetailsURL = Flippy.CHANNELS_URL + channelId + membersURL;


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
        membershipList.setTextFilterEnabled(true);


        //load the CHANNELS_URL members
        Ion.with(ChannelMembers.this)
                .load(channelDetailsURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarMemberChannelLoader.setVisibility(View.GONE);
                        try {
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
                                    if (isManage && !CommunityCenterActivity.regUserID.equalsIgnoreCase(memberId)) {
                                        ChannelMemberItem.add(profileItem);
                                    } else if (!isManage) {
                                        ChannelMemberItem.add(profileItem);
                                    }

                                }
                                updateAdapter();
                            }
                            if (e != null) {
                                ToastMessages.showToastLong(ChannelMembers.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                return;
                            }
                        } catch (Exception exception) {
                            Log.e("something went wrong", "Loading channel members");
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
                    intent.setClass(ChannelMembers.this, ManageChannelActivity.class);
                    intent.putExtra("memberEmail", userEmail);
                    intent.putExtra("memberId", userId);
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
        if (actionBar != null && !isManage) {
            actionBar.setSubtitle(totalMembers + " " + "member(s)");
        } else if (actionBar != null && isManage) {
            actionBar.setSubtitle("select a member");
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_members, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_channel_members_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                channelMemberAdapter.getFilter().filter(newText);
                Log.e("The channel members", "on text change text: " + newText);
                channelMemberAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                channelMemberAdapter.getFilter().filter(query);
                Log.e("The query", "on query submit: " + query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return true;
    }

}
