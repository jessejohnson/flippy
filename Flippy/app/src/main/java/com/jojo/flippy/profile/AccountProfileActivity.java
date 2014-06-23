package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jojo.flippy.adapter.ProfileAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class AccountProfileActivity extends ActionBarActivity {
    //Instance of the user channel
    ListView profileChannelListView;
    //Instance of the channel item
    List<ProfileItem> rowItems;
    private Intent intent;
    private  String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userFullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);


        //assigning the values from the database
        userFirstName = CommunityCenterActivity.userFirstName;
        userLastName = CommunityCenterActivity.userLastName;
        userEmail = CommunityCenterActivity.regUserEmail;
        userFullName = userFirstName  + ", "+ userLastName;

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(userFirstName);
        actionBar.setSubtitle(R.string.user_tap_to_edit);
        actionBar.setDisplayHomeAsUpEnabled(true);


        intent = getIntent();
       //Loading the list with a dummy data
        rowItems = new ArrayList<ProfileItem>();

        ProfileItem item = new ProfileItem(URI.create(CommunityCenterActivity.userAvatarURL),
                URI.create("http://facultyandstaff.colostate.edu/images/feature/campus1.jpg"),
                userFullName,userEmail,userFirstName,"GESA KNUST","200 members");

        rowItems.add(item);
        rowItems.add(item);
        rowItems.add(item);
        rowItems.add(item);


        profileChannelListView = (ListView) findViewById(R.id.profileChannelListView);
        ProfileAdapter channelAdapter = new ProfileAdapter(AccountProfileActivity.this,
                R.layout.profile_listview, rowItems);
        profileChannelListView.setAdapter(channelAdapter);

        profileChannelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //setting the click action for each of the items
                switch(position){
                    case 0:
                        intent.setClass(AccountProfileActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    default:
                        break;

                }

            }
        });
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
