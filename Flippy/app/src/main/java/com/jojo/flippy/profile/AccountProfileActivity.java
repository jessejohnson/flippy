package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.ProfileAdapter;
import com.jojo.flippy.adapter.ProfileItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.Flippy;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

;

public class AccountProfileActivity extends ActionBarActivity {
    ListView profileChannelListView;
    List<ProfileItem> rowItems;
    private Intent intent;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userFullName;
    private String userCommunityName;
    private String userCommunityImageLink;
    private String userChannels = "/subscriptions/";
    private ProfileAdapter profileAdapter;
    private ImageView imageViewProfilePic;
    private ImageView imageViewProfileUserCommunity;
    private TextView textViewProfileUserNameNew;
    private TextView textViewProfileUserEmailNew;
    private TextView textViewProfileUserCommunity;


    private LinearLayout linearLayoutUserProfile;
    private ProgressBar progressBarUserChannelLoad;

    private SuperToast superToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);


        //assigning the values from the database
        userFirstName = CommunityCenterActivity.userFirstName;
        userLastName = CommunityCenterActivity.userLastName;
        userEmail = CommunityCenterActivity.regUserEmail;
        userFullName = userFirstName + ", " + userLastName;
        userCommunityName = CommunityCenterActivity.userCommunityName;

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(userFirstName);
            actionBar.setSubtitle(R.string.user_tap_to_edit);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        superToast = new SuperToast(AccountProfileActivity.this);
        final String url = Flippy.users + CommunityCenterActivity.regUserID + userChannels;
        intent = getIntent();
        rowItems = new ArrayList<ProfileItem>();
        profileChannelListView = (ListView) findViewById(R.id.profileChannelListView);
        textViewProfileUserEmailNew = (TextView) findViewById(R.id.textViewProfileUserEmailNew);
        textViewProfileUserNameNew = (TextView) findViewById(R.id.textViewProfileUserNameNew);
        textViewProfileUserCommunity = (TextView) findViewById(R.id.textViewProfileUserCommunity);
        textViewProfileUserCommunity.setVisibility(View.GONE);
        linearLayoutUserProfile = (LinearLayout) findViewById(R.id.linearLayoutUserProfile);
        imageViewProfilePic = (ImageView) findViewById(R.id.imageViewProfilePic);
        imageViewProfileUserCommunity = (ImageView) findViewById(R.id.imageViewProfileUserCommunity);
        progressBarUserChannelLoad = (ProgressBar) findViewById(R.id.progressBarUserChannelLoad);


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
                .placeholder(R.drawable.default_profile_picture)
                .animateIn(R.anim.fade_in)
                .error(R.drawable.default_profile_picture)
                .load(CommunityCenterActivity.userAvatarURL);

        textViewProfileUserNameNew.setText(userFullName);
        textViewProfileUserEmailNew.setText(userEmail);
        getCommunityImage(CommunityCenterActivity.userCommunityId);

        //load the channels user subscribed to
        Ion.with(AccountProfileActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null && !result.has("detail")) {
                            JsonArray profileArray = result.getAsJsonArray("results");
                            for (int i = 0; i < profileArray.size(); i++) {
                                JsonObject item = profileArray.get(i).getAsJsonObject();
                                JsonObject creator = item.getAsJsonObject("creator");
                                ProfileItem profileItem = new ProfileItem(URI.create(item.get("image_url").getAsString()), item.get("name").getAsString(), creator.get("email").getAsString(), "");
                                rowItems.add(profileItem);
                            }
                            updateAdapter();
                        }
                        if (e != null) {
                            progressBarUserChannelLoad.setVisibility(View.GONE);
                            superToast.setAnimations(SuperToast.Animations.FLYIN);
                            superToast.setDuration(SuperToast.Duration.SHORT);
                            superToast.setBackground(SuperToast.Background.PURPLE);
                            superToast.setIcon(R.drawable.icon_dark_info, SuperToast.IconPosition.LEFT);
                            superToast.setTextSize(SuperToast.TextSize.MEDIUM);
                            superToast.setText("sorry, failed to load your communities");
                            superToast.show();
                            Log.e("Error loading channel", e.toString());
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
        progressBarUserChannelLoad.setVisibility(View.GONE);
    }

    private void getCommunityImage(String id) {
        Ion.with(AccountProfileActivity.this)
                .load(Flippy.communitiesURL + id + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            userCommunityImageLink = result.get("image_url").getAsString();
                            userCommunityName = result.get("name").getAsString();
                            loadCommunityImage(userCommunityImageLink);
                        }
                        if (e != null) {
                            superToast.setAnimations(SuperToast.Animations.FLYIN);
                            superToast.setDuration(SuperToast.Duration.LONG);
                            superToast.setBackground(SuperToast.Background.PURPLE);
                            superToast.setIcon(R.drawable.icon_dark_info, SuperToast.IconPosition.LEFT);
                            superToast.setTextSize(SuperToast.TextSize.MEDIUM);
                            superToast.setText("sorry, failed to get community image");
                            superToast.show();
                            Log.e("error", e.toString());
                        }

                    }
                });
    }

    private void loadCommunityImage(String url) {
        if (url == null) {
            superToast.setAnimations(SuperToast.Animations.FLYIN);
            superToast.setDuration(SuperToast.Duration.LONG);
            superToast.setBackground(SuperToast.Background.PURPLE);
            superToast.setIcon(R.drawable.icon_dark_info, SuperToast.IconPosition.LEFT);
            superToast.setTextSize(SuperToast.TextSize.MEDIUM);
            superToast.setText("sorry, internal error occurred");
            superToast.show();
            return;
        }
        textViewProfileUserCommunity.setText("Community: " + userCommunityName);
        textViewProfileUserCommunity.setVisibility(View.VISIBLE);
        Ion.with(imageViewProfileUserCommunity)
                .placeholder(R.drawable.default_profile_picture)
                .error(R.color.flippy_light_header)
                .animateIn(R.anim.fade_in)
                .load(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
