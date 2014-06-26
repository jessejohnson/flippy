package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.adapter.ProfileAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class AccountProfileActivity extends ActionBarActivity {
    //Instance of the user channel
    ListView profileChannelListView;
    //Instance of the channel item
    List<ProfileItem> rowItems;
    private Intent intent;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userFullName;
    private String userChannels = "/subscriptions/";
    private ProfileAdapter profileAdapter;
    private ImageView imageViewProfilePic;
    private TextView textViewProfileUserNameNew;
    private TextView textViewProfileUserEmailNew;


    private LinearLayout linearLayoutUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);


        //assigning the values from the database
        userFirstName = CommunityCenterActivity.userFirstName;
        userLastName = CommunityCenterActivity.userLastName;
        userEmail = CommunityCenterActivity.regUserEmail;
        userFullName = userFirstName + ", " + userLastName;

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(userFirstName);
        actionBar.setSubtitle(R.string.user_tap_to_edit);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String url = Flippy.userChannelsSubscribedURL + CommunityCenterActivity.regUserID + userChannels;


        intent = getIntent();
        rowItems = new ArrayList<ProfileItem>();
        profileChannelListView = (ListView) findViewById(R.id.profileChannelListView);
        textViewProfileUserEmailNew = (TextView) findViewById(R.id.textViewProfileUserEmailNew);
        textViewProfileUserNameNew = (TextView) findViewById(R.id.textViewProfileUserNameNew);
        linearLayoutUserProfile = (LinearLayout) findViewById(R.id.linearLayoutUserProfile);
        imageViewProfilePic = (ImageView) findViewById(R.id.imageViewProfilePic);
        profileAdapter = new ProfileAdapter(AccountProfileActivity.this,
                R.layout.profile_listview, rowItems);
        profileChannelListView.setAdapter(profileAdapter);

        linearLayoutUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(AccountProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        //set the user profile
        Ion.with(imageViewProfilePic)
                .placeholder(R.color.flippy_light_header)
                .load(CommunityCenterActivity.userAvatarURL);

        textViewProfileUserNameNew.setText(userFullName);
        textViewProfileUserEmailNew.setText(userEmail);

        //load the channels user subscribed to
        Ion.with(AccountProfileActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            JsonArray profileArray = result.getAsJsonArray("results");
                            for (int i = 0; i < profileArray.size(); i++) {
                                JsonObject item = profileArray.get(i).getAsJsonObject();
                                JsonObject creator = item.getAsJsonObject("creator");
                                ProfileItem profileItem = new ProfileItem(URI.create(item.get("image_url").getAsString()), item.get("name").getAsString(), creator.get("email").getAsString());
                                rowItems.add(profileItem);
                            }
                            updateAdapter();


                        }
                        if (e != null) {
                            ToastMessages.showToastLong(AccountProfileActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });


        profileChannelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

            }
        });
    }

    private void updateAdapter() {
        profileAdapter.notifyDataSetChanged();
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
            intent = new Intent(AccountProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
