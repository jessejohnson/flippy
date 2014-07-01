package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.Notice;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URI;

public class NoticeDetailActivity extends ActionBarActivity {
    private Button buttonPublishNotice;
    private GoogleMap googleMap;
    private Intent intent;
    private String noticeTitle;
    private String noticeId;
    private String noticeBody;
    private String noticeSubtitle;


    private TextView textViewNoticeTitleDetail;
    private TextView textViewNoticeSubtitleDetail;
    private TextView textViewNoticeTextDetail;
    private TextView textViewAuthorEmailAddress;
    private TextView textViewNoticeTimeStamp;
    private ImageView imageViewNoticeImageDetail;
    private ImageView imageViewNoticeCreatorImage;


    private String image_link;
    private String author_email;
    private String author_profile = "";
    private String time_stamp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        noticeTitle = intent.getStringExtra("noticeTitle");
        noticeId = intent.getStringExtra("noticeId");
        noticeSubtitle = intent.getStringExtra("noticeSubtitle");
        noticeBody = intent.getStringExtra("noticeBody");

        actionBar.setSubtitle(noticeTitle);


        imageViewNoticeImageDetail = (ImageView) findViewById(R.id.imageViewNoticeImageDetail);
        imageViewNoticeCreatorImage = (ImageView) findViewById(R.id.imageViewNoticeCreatorImage);
        textViewNoticeTitleDetail = (TextView) findViewById(R.id.textViewNoticeTitleDetail);
        textViewNoticeTimeStamp = (TextView) findViewById(R.id.textViewNoticeTimeStamp);
        textViewAuthorEmailAddress = (TextView) findViewById(R.id.textViewAuthorEmailAddress);
        textViewNoticeSubtitleDetail = (TextView) findViewById(R.id.textViewNoticeSubtitleDetail);
        textViewNoticeTextDetail = (TextView) findViewById(R.id.textViewNoticeTextDetail);
        textViewNoticeTitleDetail.setText(noticeTitle);
        textViewNoticeTextDetail.setText(noticeBody);
        textViewNoticeSubtitleDetail.setText(noticeSubtitle);


        //place holder texts
        textViewAuthorEmailAddress.setText("");
        textViewNoticeTimeStamp.setText("");


        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.linearLayoutNoticeShowLocation))
                .getMap();


        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.linearLayoutNoticeShowLocation)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }
        //get this data from the intent
        LatLng coordinate = new LatLng(5.5500, 0.2000);
        googleMap.addMarker(new MarkerOptions()
                .snippet("this is the location of the event")
                .title(noticeTitle)
                .position(coordinate)
                .draggable(false));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(5.5500, 0.2000), 5));


        String url = Flippy.aPostURL + noticeId + "/";
        //Loading the list with data from Api call
        Ion.with(NoticeDetailActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            JsonObject item = result.getAsJsonObject("author");
                            author_email = item.get("email").getAsString();
                            if (!item.get("avatar").isJsonNull()) {
                                author_profile = item.get("avatar").getAsString();
                            }
                            String[] timestampArray = result.get("timestamp").getAsString().replace("Z", "").split("T");
                            time_stamp = timestampArray[0].toString() + " @ " + timestampArray[1].substring(0, 8);
                            image_link = "";
                            if (!result.get("image_url").isJsonNull()) {
                                image_link = result.get("image_url").getAsString();
                            }
                            showView();


                        }
                        if (e != null) {
                            ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notice_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_profile) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void showView() {
        Ion.with(imageViewNoticeImageDetail)
                .animateIn(R.anim.fade_in)
                .load(image_link);
        Ion.with(imageViewNoticeCreatorImage)
                .animateIn(R.anim.fade_in)
                .placeholder(R.color.flippy_orange)
                .load(author_profile);
        Log.e("User image URl",author_profile);
        textViewAuthorEmailAddress.setText(author_email);
        textViewNoticeTimeStamp.setText(time_stamp);

    }
}
