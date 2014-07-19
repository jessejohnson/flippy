package com.jojo.flippy.core;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jojo.flippy.app.R;

public class CreateNoticeActivity extends ActionBarActivity {
    private String channelToCreateNotice;
    private String channelId;
    private Intent intent;
    private Button buttonNextCreateNotice;
    private EditText editTextNewNoticeContent, editTextNewNoticeTitle, editTextNewNoticeLocation;
    private String noticeContent, noticeTitle, noticeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        intent = getIntent();
        channelToCreateNotice = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(channelToCreateNotice);
        }


        buttonNextCreateNotice = (Button) findViewById(R.id.buttonNextCreateNotice);
        editTextNewNoticeContent = (EditText) findViewById(R.id.editTextNewNoticeContent);
        editTextNewNoticeLocation = (EditText) findViewById(R.id.editTextNewNoticeLocation);
        editTextNewNoticeTitle = (EditText) findViewById(R.id.editTextNewNoticeTitle);
        buttonNextCreateNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(CreateNoticeActivity.this, NoticeExtrasActivity.class);
                noticeContent = editTextNewNoticeContent.getText().toString().trim();
                noticeTitle = editTextNewNoticeTitle.getText().toString().trim();
                noticeLocation = editTextNewNoticeLocation.getText().toString().trim();

                if (editTextNewNoticeTitle.getText().toString().trim().isEmpty()) {
                    editTextNewNoticeTitle.setError("Add a notice title");
                } else if (editTextNewNoticeContent.getText().toString().trim().isEmpty()) {
                    editTextNewNoticeContent.setError("Add the notice content");
                } else if (editTextNewNoticeLocation.getText().toString().trim().isEmpty()) {
                    editTextNewNoticeLocation.setError("Set a location ");
                } else {
                    editTextNewNoticeTitle.setError(null);
                    editTextNewNoticeContent.setError(null);
                    intent.putExtra("noticeContent", noticeContent);
                    intent.putExtra("noticeTitle", noticeTitle);
                    intent.putExtra("noticeLocation", noticeLocation);
                    startActivity(intent);
                }

            }
        });

    }
}
