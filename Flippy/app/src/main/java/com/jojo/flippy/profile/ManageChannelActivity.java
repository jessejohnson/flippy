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

public class ManageChannelActivity extends ActionBarActivity {
    private EditText editTextManageChannelChannelName, editTextFirstAdmin, editTextSecondAdmin, editTextThirdAdmin, editTextFourthAdmin;
    private ImageView imageViewEditChannelName, imageViewEditFirstAdmin, imageViewEditSecondAdmin, imageViewEditThirdAdmin, imageViewEditFourthAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channel);

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

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save_changes) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
