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
    private EditText editTextNewNoticeContent, editTextNewNoticeTitle;
    private String  noticeContent,noticeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        intent = getIntent();
        channelToCreateNotice = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");

        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(channelToCreateNotice);

        buttonNextCreateNotice = (Button) findViewById(R.id.buttonNextCreateNotice);
        editTextNewNoticeContent = (EditText) findViewById(R.id.editTextNewNoticeContent);
        editTextNewNoticeTitle = (EditText) findViewById(R.id.editTextNewNoticeTitle);
        buttonNextCreateNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(CreateNoticeActivity.this, NoticeExtrasActivity.class);
                noticeContent = editTextNewNoticeContent.getText().toString().trim();
                noticeTitle = editTextNewNoticeTitle.getText().toString().trim();

                if (editTextNewNoticeTitle.getText().toString().trim().isEmpty()) {
                    editTextNewNoticeTitle.setError("This field is required");
                } else if (editTextNewNoticeContent.getText().toString().trim().isEmpty()) {
                    editTextNewNoticeContent.setError("This field is required");
                } else {
                    editTextNewNoticeTitle.setError(null);
                    editTextNewNoticeContent.setError(null);
                    intent.putExtra("noticeContent", noticeContent);
                    intent.putExtra("noticeTitle", noticeTitle);
                    startActivity(intent);
                }

            }
        });

    }
}
