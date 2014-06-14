package com.jojo.flippy.profile;

import android.app.ActionBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Bright Profile");
        actionBar.setSubtitle(R.string.user_tap_to_edit);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Loading the list with a dummy data
        ArrayList<Channel> rowItems = new ArrayList<Channel>();
        Channel item = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "GESA KNUST", "200 members","active");
        Channel item1 = new Channel(URI.create("http://www.ugsrc.com/wp-content/uploads/2013/08/6.jpg"), "SRC Legon, 2015", "4000 members","admin");
        Channel item2 = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "PENSA KNUST", "200 members","active");
        Channel item3 = new Channel(URI.create("http://images-mediawiki-sites.thefullwiki.org/02/1/0/0/73473104099591446.jpg"),
                "QUEENS HALL", "200 members","active");
        rowItems.add(item);
        rowItems.add(item1);
        rowItems.add(item2);
        rowItems.add(item3);

        ListView profileChannelList = (ListView) findViewById(R.id.profileChannelListView);
        ChannelAdapter channelAdapter = new ChannelAdapter(AccountProfileActivity.this,
                R.layout.channel_listview, rowItems, true);
        profileChannelList.setAdapter(channelAdapter);
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
