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
import android.widget.TextView;

import com.jojo.flippy.app.R;

public class MemberDetailActivity extends ActionBarActivity {
    private ImageView imageViewMemberAnotherUserProfilePic;
    private Intent intent;
    private Button buttonAddAsAdmin;
    private int requestCode = 0;
    private boolean isManageActivity= false;
    private TextView textViewAnotherUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Bright Profile");
        actionBar.setSubtitle(R.string.user_tap_to_edit);
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        requestCode = intent.getIntExtra("requestCode",0);
        isManageActivity = intent.getBooleanExtra("isManageActivity", false);

        intent.setClass(MemberDetailActivity.this,ImagePreviewActivity.class);
        textViewAnotherUserEmail = (TextView)findViewById(R.id.textViewAnotherUserEmail);
        buttonAddAsAdmin = (Button)findViewById(R.id.buttonAddAsAdmin);
        buttonAddAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] channelList = {"GESA KNUST", "SRC Legon", "Flippy Group","Another Group"};
                channelListDialog(channelList);
            }
        });
        imageViewMemberAnotherUserProfilePic = (ImageView)findViewById(R.id.imageViewMemberAnotherUserProfilePic);
        imageViewMemberAnotherUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.member_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_full_screen) {
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
}

