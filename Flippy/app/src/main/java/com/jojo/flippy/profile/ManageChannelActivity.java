package com.jojo.flippy.profile;

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

public class ManageChannelActivity extends ActionBarActivity {
    private EditText editTextManageChannelChannelName, editTextFirstAdmin, editTextSecondAdmin, editTextThirdAdmin, editTextFourthAdmin;
    private ImageView imageViewEditChannelName, imageViewEditFirstAdmin, imageViewEditSecondAdmin, imageViewEditThirdAdmin, imageViewEditFourthAdmin;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channel);

        intent = getIntent();
        intent.putExtra("isEditing", true);

        //the edit text views
        editTextManageChannelChannelName = (EditText) findViewById(R.id.editTextManageChannelChannelName);
        editTextFirstAdmin = (EditText) findViewById(R.id.editTextFirstAdmin);
        editTextSecondAdmin = (EditText) findViewById(R.id.editTextSecondAdmin);
        editTextThirdAdmin = (EditText) findViewById(R.id.editTextThirdAdmin);
        editTextFourthAdmin = (EditText) findViewById(R.id.editTextFourthAdmin);


        //the image views
        imageViewEditChannelName = (ImageView) findViewById(R.id.imageViewEditChannelName);
        imageViewEditFirstAdmin = (ImageView) findViewById(R.id.imageViewEditFirstAdmin);
        imageViewEditSecondAdmin = (ImageView) findViewById(R.id.imageViewEditSecondAdmin);
        imageViewEditThirdAdmin = (ImageView) findViewById(R.id.imageViewEditThirdAdmin);
        imageViewEditFourthAdmin = (ImageView) findViewById(R.id.imageViewEditFourthAdmin);


        //disable all the fields
        editTextManageChannelChannelName.setEnabled(false);
        editTextFirstAdmin.setEnabled(false);
        editTextSecondAdmin.setEnabled(false);
        editTextThirdAdmin.setEnabled(false);
        editTextFourthAdmin.setEnabled(false);

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
                intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
                //adding something to the intent to detect at the other end
                startActivity(intent);

            }
        });
        imageViewEditSecondAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
                //adding something to the intent to detect at the other end
                startActivity(intent);

            }
        });
        imageViewEditThirdAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
                //adding something to the intent to detect at the other end
                startActivity(intent);

            }
        });
        imageViewEditFourthAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
                //adding something to the intent to detect at the other end
                startActivity(intent);

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

    private void channelListDialog(final CharSequence[] channelList) {
        //TODO this should line should return a list of user channels subscribed to
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageChannelActivity.this);
        builder.setTitle(R.string.choose_channel_list_dialog_title);
        builder.setItems(channelList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //get the selected option and pass it on to the next activity
                String channelToCreateNotice = channelList[item].toString();
                intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
                intent.putExtra("channelToCreateNotice", channelToCreateNotice);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
