package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MemberDetailActivity extends ActionBarActivity {

    private String memberId;
    private String memberFirstName;
    private String memberFirstNameNew;
    private String memberLastNameNew;
    private String memberFullName;
    private String memberEmail;
    private String memberCommunity;
    private String memberCommunityName;
    private String avatar = "";

    private Intent intent;
    private Button buttonAddAsAdmin;
    private ScrollView scrollViewUserDetail;
    private ImageView imageViewMemberAnotherUserProfilePic;
    private TextView textViewAnotherUserEmail;
    private TextView textViewAnotherUserName;
    private TextView textViewAnotherFirstName;
    private TextView textViewAnotherUseLastName;
    private TextView textViewUserCommunityName;


    private boolean isManageActivity =false;
    private int requestCode =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        ActionBar actionBar = getActionBar();


        intent = getIntent();
        memberId = intent.getStringExtra("memberId");
        isManageActivity = intent.getBooleanExtra("isManageActivity",false);
        requestCode = intent.getIntExtra("requestCode",0);
        memberFirstName = intent.getStringExtra("memberFirstName");
        actionBar.setTitle(memberFirstName + "'s profile");
        actionBar.setDisplayHomeAsUpEnabled(true);


        String userDetailURL = Flippy.userChannelsSubscribedURL + memberId + "/";

        intent.setClass(MemberDetailActivity.this, ImagePreviewActivity.class);
        textViewAnotherUserEmail = (TextView) findViewById(R.id.textViewAnotherUserEmail);
        textViewUserCommunityName = (TextView) findViewById(R.id.textViewUserCommunityName);
        textViewAnotherUserName = (TextView) findViewById(R.id.textViewAnotherUserName);
        textViewAnotherUseLastName = (TextView) findViewById(R.id.textViewAnotherUseLastName);
        textViewAnotherFirstName = (TextView) findViewById(R.id.textViewAnotherFirstName);
        imageViewMemberAnotherUserProfilePic = (ImageView) findViewById(R.id.imageViewMemberAnotherUserProfilePic);
        imageViewMemberAnotherUserProfilePic.setVisibility(View.GONE);
        textViewAnotherUseLastName.setVisibility(View.GONE);
        textViewAnotherUserEmail.setVisibility(View.GONE);
        textViewAnotherFirstName.setVisibility(View.GONE);
        textViewAnotherUserName.setVisibility(View.GONE);
        textViewUserCommunityName.setVisibility(View.GONE);
        buttonAddAsAdmin = (Button) findViewById(R.id.buttonAddAsAdmin);


        //load the details of a member
        Ion.with(MemberDetailActivity.this)
                .load(userDetailURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            memberEmail = result.get("email").getAsString();
                            memberCommunity = result.get("community").getAsString();
                            memberFirstNameNew = result.get("first_name").getAsString();
                            memberLastNameNew = result.get("last_name").getAsString();
                            if (!result.get("avatar").isJsonNull()) {
                                avatar = result.get("avatar").getAsString();
                            }
                            secondAsyncTask(memberCommunity);
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(MemberDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
        buttonAddAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isManageActivity){
                    intent.putExtra("memberEmail",memberEmail);
                    intent.setClass(MemberDetailActivity.this,ManageChannelActivity.class);
                    setResult(requestCode,intent);
                    return;

                }
                final CharSequence[] channelList = {"GESA KNUST", "SRC Legon", "Flippy Group", "Another Group"};
                channelListDialog(channelList);
            }
        });
        imageViewMemberAnotherUserProfilePic = (ImageView) findViewById(R.id.imageViewMemberAnotherUserProfilePic);
        imageViewMemberAnotherUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(MemberDetailActivity.this,ImagePreviewActivity.class);
                intent.putExtra("avatar",avatar);
                startActivity(intent);
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
            intent.setClass(MemberDetailActivity.this,ImagePreviewActivity.class);
            intent.putExtra("avatar",avatar);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void channelListDialog(final CharSequence[] channelList) {
        //TODO this should line should return a list of user channels subscribed to
        AlertDialog.Builder builder = new AlertDialog.Builder(MemberDetailActivity.this);
        builder.setTitle(R.string.choose_channel_list_dialog_title);
        builder.setItems(channelList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //get the selected option and pass it on to the next activity
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

    private void secondAsyncTask(String communityId) {
        //load the creator of the channel
        Ion.with(MemberDetailActivity.this)
                .load(Flippy.channelsInCommunityURL + communityId + "/")
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

                    }
                });

    }

    private void showViews() {
        memberFullName = memberFirstNameNew + ", " + memberLastNameNew;
        textViewAnotherUserName.setText(memberFullName);
        textViewAnotherUserName.setVisibility(View.VISIBLE);
        textViewAnotherFirstName.setText(memberFirstNameNew);
        textViewAnotherUseLastName.setText(memberLastNameNew);
        textViewAnotherFirstName.setVisibility(View.VISIBLE);
        textViewAnotherUseLastName.setVisibility(View.VISIBLE);
        textViewAnotherUserEmail.setVisibility(View.VISIBLE);
        textViewAnotherUserEmail.setText(memberEmail);
        textViewUserCommunityName.setText(memberCommunityName);
        textViewUserCommunityName.setVisibility(View.VISIBLE);
        imageViewMemberAnotherUserProfilePic.setVisibility(View.VISIBLE);
        Ion.with(imageViewMemberAnotherUserProfilePic)
                .placeholder(R.color.flippy_white)
                .load(avatar);

    }
}

