package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jojo.flippy.adapter.ChannelMemberAdapter;
import com.jojo.flippy.adapter.SettingsAdapter;
import com.jojo.flippy.adapter.SettingsItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.MemberDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/12/14.
 */
public class ChannelMembers extends Activity {
    private Intent intent;
    private String channelName = null;
    private  String totalMembers = null;
    private ListView membershipList;
    //Instance of the channel item
    List<SettingsItem> ChannelMemberItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_members);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        totalMembers = intent.getStringExtra("totalMembers");


        ActionBar actionBar = getActionBar();
        if (channelName!=null && actionBar!=null && totalMembers != null){
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


        membershipList = (ListView)findViewById(R.id.listViewChannelMembers);
        ChannelMemberAdapter adapter = new ChannelMemberAdapter(ChannelMembers.this,
                R.layout.channel_members_listview, ChannelMemberItem);
        membershipList.setAdapter(adapter);



        //Setting the click listener for the member list
        membershipList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items
                intent.setClass(ChannelMembers.this,MemberDetailActivity.class);
                startActivity(intent);


            }
        });

    }
}
