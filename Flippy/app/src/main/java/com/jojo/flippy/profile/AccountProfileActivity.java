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
import com.jojo.flippy.app.R;

import java.net.URI;
import java.util.ArrayList;

public class AccountProfileActivity extends ActionBarActivity {
<<<<<<< HEAD
=======
    //Instance of the user channel
    ListView userChannelListView;
    //Instance of the channel item
    List<Channel> rowItems;
    private Intent intent;
>>>>>>> faed05382c1a11eeff5f1bdb4488cd40c6aaec55

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Bright Profile");
        actionBar.setSubtitle(R.string.user_tap_to_edit);
        actionBar.setDisplayHomeAsUpEnabled(true);

<<<<<<< HEAD
        //Loading the list with a dummy data
        ArrayList<Channel> rowItems = new ArrayList<Channel>();
        Channel item = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "GESA KNUST", "200 members","active");
        Channel item1 = new Channel(URI.create("http://www.ugsrc.com/wp-content/uploads/2013/08/6.jpg"), "SRC Legon, 2015", "4000 members","admin");
        Channel item2 = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "PENSA KNUST", "200 members","active");
        Channel item3 = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "QUEENS HALL", "200 members","active");
=======
       //Loading the list with a dummy data
        rowItems = new ArrayList<Channel>();
        Channel item = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"), "GESA KNUST", "200 members","active");
        Channel item1 = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"), "SRC Legon, 2015", "4000 members","admin");
        Channel item2 = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"), "GESA KNUST", "200 members","active");
        Channel item3 = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"), "SRC Legon, 2015", "4000 members","admin");
>>>>>>> faed05382c1a11eeff5f1bdb4488cd40c6aaec55
        rowItems.add(item);
        rowItems.add(item1);
        rowItems.add(item2);
        rowItems.add(item3);

<<<<<<< HEAD
        ListView profileChannelList = (ListView) findViewById(R.id.profileChannelListView);
        ChannelAdapter channelAdapter = new ChannelAdapter(AccountProfileActivity.this,
                R.layout.channel_listview, rowItems, true);
        profileChannelList.setAdapter(channelAdapter);
=======

        userChannelListView = (ListView) findViewById(R.id.listViewUserChannels);
        CustomUserChannel adapter = new CustomUserChannel(this,
                R.layout.user_channel_list_item, rowItems);
        userChannelListView.setAdapter(adapter);


>>>>>>> faed05382c1a11eeff5f1bdb4488cd40c6aaec55
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
