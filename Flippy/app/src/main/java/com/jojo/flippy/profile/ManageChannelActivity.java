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
import android.widget.EditText;
import android.widget.ImageView;

import com.jojo.flippy.app.R;
import com.jojo.flippy.core.ChannelMembers;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.ion.Ion;

public class ManageChannelActivity extends ActionBarActivity {
    private EditText editTextManageChannelChannelName, editTextFirstAdmin, editTextSecondAdmin, editTextThirdAdmin, editTextFourthAdmin;
    private ImageView imageViewChannelManageEdit, imageViewEditChannelName, imageViewEditFirstAdmin, imageViewEditSecondAdmin, imageViewEditThirdAdmin, imageViewEditFourthAdmin;
    private Intent intent;
    private String channelName, channelId, image_url;
    private boolean isManage = true;
    private final int ADMIN_ONE = 1;
    private final int ADMIN_TWO = 2;
    private final int ADMIN_THREE = 3;
    private final int ADMIN_FOUR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channel);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        image_url = intent.getStringExtra("image_url");

        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(channelName);

        //the edit text views
        editTextManageChannelChannelName = (EditText) findViewById(R.id.editTextManageChannelChannelName);
        editTextManageChannelChannelName.setText(channelName);
        editTextFirstAdmin = (EditText) findViewById(R.id.editTextFirstAdmin);
        editTextSecondAdmin = (EditText) findViewById(R.id.editTextSecondAdmin);
        editTextThirdAdmin = (EditText) findViewById(R.id.editTextThirdAdmin);
        editTextFourthAdmin = (EditText) findViewById(R.id.editTextFourthAdmin);


        //the image views
        imageViewChannelManageEdit = (ImageView) findViewById(R.id.imageViewChannelManageEdit);
        imageViewEditChannelName = (ImageView) findViewById(R.id.imageViewEditChannelName);
        imageViewEditFirstAdmin = (ImageView) findViewById(R.id.imageViewEditFirstAdmin);
        imageViewEditSecondAdmin = (ImageView) findViewById(R.id.imageViewEditSecondAdmin);
        imageViewEditThirdAdmin = (ImageView) findViewById(R.id.imageViewEditThirdAdmin);
        imageViewEditFourthAdmin = (ImageView) findViewById(R.id.imageViewEditFourthAdmin);

        Ion.with(imageViewChannelManageEdit)
                .placeholder(R.drawable.channel_bg)
                .animateIn(R.anim.fade_in)
                .load(image_url);

        imageViewChannelManageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload a new image for the user to change his channel image
            }
        });


        //disable all the fields
        editTextManageChannelChannelName.setEnabled(false);
        editTextFirstAdmin.setEnabled(false);
        editTextSecondAdmin.setEnabled(false);
        editTextThirdAdmin.setEnabled(false);
        editTextFourthAdmin.setEnabled(false);

        intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
        intent.putExtra("isManage", true);

        //enable the edit text for the channel name on click
        imageViewEditChannelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextManageChannelChannelName.setEnabled(true);
                editTextManageChannelChannelName.setFocusable(true);
            }
        });

        imageViewEditFirstAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_ONE);
            }
        });
        imageViewEditSecondAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_TWO);

            }
        });
        imageViewEditThirdAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_THREE);

            }
        });
        imageViewEditFourthAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_FOUR);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save_changes) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String noMember = "No member selected";
        // check if the request code is same as what is passed  here it is 2
        if (requestCode != RESULT_OK) {
            ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            return;

        }
        if (data == null) {
            ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            return;
        }
        String adminEmail = data.getStringExtra("memberEmail");
        if (requestCode == ADMIN_ONE) {
            if (null != data) {
                editTextFirstAdmin.setText(adminEmail);
            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
            return;

        }
        if (requestCode == ADMIN_TWO) {
            if (null != data) {
                editTextSecondAdmin.setText(adminEmail);
            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
            return;
        }
        if (requestCode == ADMIN_THREE) {
            if (null != data) {
                editTextThirdAdmin.setText(adminEmail);

            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
            return;
        }
        if (requestCode == ADMIN_FOUR) {
            if (null != data) {
                editTextFourthAdmin.setText(adminEmail);
            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
            return;
        }
        ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
    }

}
