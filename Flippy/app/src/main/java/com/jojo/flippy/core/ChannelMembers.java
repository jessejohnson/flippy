package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jojo.flippy.adapter.ChannelMemberAdapter;
import com.jojo.flippy.adapter.SettingsAdapter;
import com.jojo.flippy.adapter.SettingsItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.MemberDetailActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/12/14.
 */
public class ChannelMembers extends Activity {
    private Intent intent;
    private String channelName = null;
    private String totalMembers = null;
    private ListView membershipList;
    //Instance of the channel item
    List<SettingsItem> ChannelMemberItem;
    private String isManageActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_members);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        totalMembers = intent.getStringExtra("totalMembers");
        isManageActivity = intent.getStringExtra("isManageActivity");


        ActionBar actionBar = getActionBar();
        if (channelName != null && actionBar != null && totalMembers != null) {
            actionBar.setTitle(channelName);
            actionBar.setSubtitle(totalMembers + " members");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        //Loading the list with a dummy data
        ChannelMemberItem = new ArrayList<SettingsItem>();
        SettingsItem firstMember = new SettingsItem(R.drawable.sample_user, getResources().getString(R.string.dummy_user_name), getResources().getString(R.string.dummy_user_number));
        SettingsItem secondMember = new SettingsItem(R.drawable.sample_user, getResources().getString(R.string.dummy_user_name), getResources().getString(R.string.dummy_user_number));
        ChannelMemberItem.add(firstMember);
        ChannelMemberItem.add(secondMember);

        AsyncHttpClient client = new AsyncHttpClient();
        String request = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/";
        client.get(request, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                try {
                    JSONArray list = response.getJSONArray("results");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        ChannelMemberItem.add(createNewUser(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
            }
        });


        membershipList = (ListView) findViewById(R.id.listViewChannelMembers);
        ChannelMemberAdapter adapter = new ChannelMemberAdapter(ChannelMembers.this,
                R.layout.channel_members_listview, ChannelMemberItem);
        membershipList.setAdapter(adapter);

        //checking if the intent came from the channel management activity, process and sent result
        if (isManageActivity.toString().trim().equalsIgnoreCase("true")) {
            membershipList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    TextView memberEmail =(TextView) view.findViewById(R.id.textViewMemberFirstName);
                    intent.setClass(ChannelMembers.this, MemberDetailActivity.class);
                    intent.putExtra("EMAIL", memberEmail.getText().toString());
                    setResult(1, intent);
                    finish();
                }
            });
        } else {
            //Setting the click listener for the member list
            membershipList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    //setting the click action for each of the items
                    intent.setClass(ChannelMembers.this, MemberDetailActivity.class);
                    startActivity(intent);

                }
            });
        }


    }

    SettingsItem createNewUser(JSONObject object) {
        String title = "";
        String sub = "";
        try {
            title = object.getString("first_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            sub = object.getString("phone_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new SettingsItem(R.drawable.sample_user, title, sub);
    }
}
