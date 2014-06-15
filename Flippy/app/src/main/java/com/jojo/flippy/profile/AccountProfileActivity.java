package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.adapter.ProfileAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class AccountProfileActivity extends ActionBarActivity {
    //Instance of the user channel
    ListView profileChannelListView;
    //Instance of the channel item
    List<ProfileItem> rowItems;
    private Intent intent;
    private  String number;
    private String username;
    private String useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Bright Profile");
        actionBar.setSubtitle(R.string.user_tap_to_edit);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //assigning the values from the database
        number = getResources().getString(R.string.dummy_user_number);
        username= getResources().getString(R.string.dummy_user_name);
        useremail = getResources().getString(R.string.dummy_user_email);



       //Loading the list with a dummy data
        rowItems = new ArrayList<ProfileItem>();
        ProfileItem item = new ProfileItem(R.drawable.user_profile,R.drawable.default_profile_picture,username,useremail,number,"GESA KNUST","200 members");
        rowItems.add(item);
        rowItems.add(item);
        rowItems.add(item);
        rowItems.add(item);


        profileChannelListView = (ListView) findViewById(R.id.profileChannelListView);
        ProfileAdapter channelAdapter = new ProfileAdapter(AccountProfileActivity.this,
                R.layout.profile_listview, rowItems);
        profileChannelListView.setAdapter(channelAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_profile) {
            intent = new Intent(AccountProfileActivity.this,EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
