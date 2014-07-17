package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MemberDetailActivity extends ActionBarActivity {

    private String memberId;
    private String memberEmailReceived;
    private String memberFirstNameNew;
    private String memberLastNameNew;
    private String memberFullName;
    private String memberFullNameReceived;
    private String memberEmail;
    private String memberCommunity;
    private String memberCommunityName;
    private String avatar = "";

    private Intent intent;
    private ImageView imageViewMemberAnotherUserProfilePic;
    private TextView textViewAnotherUserEmail;
    private TextView textViewAnotherUserName;
    private TextView textViewAnotherFirstName;
    private TextView textViewAnotherUseLastName;
    private TextView textViewUserCommunityName;

    private TextView textViewUserTotalNumberOfCircles;
    private String TotalChannels;
    private String userDetailURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        intent = getIntent();
        memberId = intent.getStringExtra("memberId");
        memberEmailReceived = intent.getStringExtra("memberEmail");
        memberFullNameReceived = intent.getStringExtra("memberFullName");


        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(memberEmailReceived + "'s profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        userDetailURL = Flippy.users + memberId + "/";
        intent.setClass(MemberDetailActivity.this, ImagePreviewActivity.class);
        textViewAnotherUserEmail = (TextView) findViewById(R.id.textViewAnotherUserEmail);
        textViewUserCommunityName = (TextView) findViewById(R.id.textViewUserCommunityName);
        textViewAnotherUserName = (TextView) findViewById(R.id.textViewAnotherUserName);
        textViewAnotherUseLastName = (TextView) findViewById(R.id.textViewAnotherUseLastName);
        textViewAnotherFirstName = (TextView) findViewById(R.id.textViewAnotherFirstName);
        textViewUserTotalNumberOfCircles = (TextView) findViewById(R.id.textViewUserTotalNumberOfCircles);
        imageViewMemberAnotherUserProfilePic = (ImageView) findViewById(R.id.imageViewMemberAnotherUserProfilePic);
        textViewAnotherUserEmail.setText(memberEmailReceived);
        textViewAnotherUserName.setText(memberFullNameReceived);

        hideViews();
        loadMemberData();

        imageViewMemberAnotherUserProfilePic = (ImageView) findViewById(R.id.imageViewMemberAnotherUserProfilePic);
        imageViewMemberAnotherUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(MemberDetailActivity.this, ImagePreviewActivity.class);
                intent.putExtra("avatar", avatar);
                startActivity(intent);
            }
        });
    }

    private void loadMemberData() {
        Ion.with(MemberDetailActivity.this)
                .load(userDetailURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.e("Url", userDetailURL);
                        if (result != null) {
                            if (result.has("detail")) {
                                Crouton.makeText(MemberDetailActivity.this, result.get("detail").toString(), Style.INFO).show();
                                return;
                            }
                            memberEmail = result.get("email").getAsString();
                            memberCommunity = result.get("community").getAsString();
                            memberFirstNameNew = result.get("first_name").getAsString();
                            memberLastNameNew = result.get("last_name").getAsString();
                            if (!result.get("avatar").isJsonNull()) {
                                avatar = result.get("avatar").getAsString();
                            }
                            getCommunityName(memberCommunity);
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(MemberDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    private void hideViews() {
        textViewAnotherUseLastName.setVisibility(View.GONE);
        textViewUserTotalNumberOfCircles.setVisibility(View.GONE);
        textViewAnotherFirstName.setVisibility(View.GONE);
        textViewUserCommunityName.setVisibility(View.GONE);
    }

    private void memberTotalChannels() {
        Ion.with(MemberDetailActivity.this)
                .load(Flippy.channels + memberId + "/subscriptions/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result.has("detail")) {
                            Log.e("Error", result.get("detail").toString());
                        }
                        if (result != null) {
                            JsonArray subscriptionArray = result.getAsJsonArray("results");
                            textViewUserTotalNumberOfCircles.setVisibility(View.VISIBLE);
                            TotalChannels = " channels " + "(" + subscriptionArray.size() + ")";
                            textViewUserTotalNumberOfCircles.setText(TotalChannels);
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(MemberDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.member_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_full_screen) {
            intent.setClass(MemberDetailActivity.this, ImagePreviewActivity.class);
            intent.putExtra("avatar", avatar);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void getCommunityName(String communityId) {
        Ion.with(MemberDetailActivity.this)
                .load(Flippy.communitiesURL + communityId + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (!result.isJsonNull()) {
                            memberCommunityName = result.get("name").getAsString();
                            showViews();
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(MemberDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }
                        memberTotalChannels();
                    }
                });

    }

    private void showViews() {
        textViewAnotherFirstName.setText(memberFirstNameNew);
        textViewAnotherUseLastName.setText(memberLastNameNew);
        textViewAnotherFirstName.setVisibility(View.VISIBLE);
        textViewAnotherUseLastName.setVisibility(View.VISIBLE);
        textViewUserCommunityName.setText(memberCommunityName);
        textViewUserCommunityName.setVisibility(View.VISIBLE);
        Ion.with(imageViewMemberAnotherUserProfilePic)
                .placeholder(R.drawable.default_profile_picture)
                .animateIn(R.anim.fade_in)
                .error(R.drawable.default_profile_picture)
                .load(avatar);

    }

    private void promoteUser(String channelId) {
        String URL = Flippy.channels + memberId + "/promote_user/";
        Ion.with(MemberDetailActivity.this)
                .load(URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            if (result.has("detail")) {
                                Crouton.makeText(MemberDetailActivity.this, result.get("detail").toString(), Style.ALERT);
                                return;
                            }
                            Crouton.makeText(MemberDetailActivity.this, result.get("results").toString(), Style.CONFIRM);
                            return;
                        }
                        if (e != null) {
                            ToastMessages.showToastShort(MemberDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            return;
                        }

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {

        } else {
            if (requestCode == 1 && resultCode == 1) {
                promoteUser(data.getStringExtra("channelId"));
            }
        }
    }
}

