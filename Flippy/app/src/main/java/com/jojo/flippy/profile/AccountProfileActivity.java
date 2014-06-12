package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.jojo.flippy.adapter.ChannelItem;
import com.jojo.flippy.adapter.CustomUserChannel;
import com.jojo.flippy.app.R;

import java.util.ArrayList;
import java.util.List;

public class AccountProfileActivity extends ActionBarActivity {
    //Instance of the user channel
    ListView userChannelListView;
    //Instance of the channel item
    List<ChannelItem> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Bright Profile");
        actionBar.setSubtitle(R.string.user_tap_to_edit);
        actionBar.setDisplayHomeAsUpEnabled(true);

       //Loading the list with a dummy data
        rowItems = new ArrayList<ChannelItem>();
        ChannelItem item = new ChannelItem(R.drawable.sample_user, "GESA KNUST", "200 members","active");
        ChannelItem item1 = new ChannelItem(R.drawable.sample_user, "SRC Legon, 2015", "4000 members","admin");
        rowItems.add(item);
        rowItems.add(item1);


        userChannelListView = (ListView) findViewById(R.id.listViewUserChannels);
        CustomUserChannel adapter = new CustomUserChannel(this,
                R.layout.user_channel_list_item, rowItems);
        userChannelListView.setAdapter(adapter);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
