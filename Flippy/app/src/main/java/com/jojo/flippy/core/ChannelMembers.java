package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.jojo.flippy.app.R;

/**
 * Created by bright on 6/12/14.
 */
public class ChannelMembers extends Activity {
    private Intent intent;
    private String channelName = null;
    private  String totalMembers = null;
    private ListView membershipList;

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
        }

    }
}
