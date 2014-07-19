package com.jojo.flippy.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class PreviewPost extends ActionBarActivity {
    private Intent intent;
    private String channelId, noticeTitle, noticeContent, lat, lon, channelName, noticeLocation;
    private TextView textViewPreviewNoticeTitleDetail, textViewPreviewNoticeSubtitle,
            textViewPreviewNoticeTextDetail,
            textViewPreviewNoticeChannelName, textViewPreviewAuthorEmailAddress, textViewNoticeLocation;

    private ImageView imageViewPreviewNoticeCreatorImage;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutPreviewMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_post);

        intent = getIntent();
        channelId = intent.getStringExtra("channelId");
        noticeTitle = intent.getStringExtra("noticeTitle");
        noticeContent = intent.getStringExtra("noticeContent");
        channelName = intent.getStringExtra("channelName");
        noticeLocation = intent.getStringExtra("noticeLocation");
        lat = intent.getStringExtra("lat");
        lon = intent.getStringExtra("lon");


        textViewPreviewNoticeTitleDetail = (TextView) findViewById(R.id.textViewPreviewNoticeTitleDetail);
        textViewPreviewNoticeSubtitle = (TextView) findViewById(R.id.textViewPreviewNoticeSubtitle);
        textViewPreviewNoticeTextDetail = (TextView) findViewById(R.id.textViewPreviewNoticeTextDetail);
        textViewPreviewNoticeChannelName = (TextView) findViewById(R.id.textViewPreviewNoticeChannelName);
        textViewPreviewAuthorEmailAddress = (TextView) findViewById(R.id.textViewPreviewAuthorEmailAddress);
        textViewNoticeLocation = (TextView) findViewById(R.id.textViewNoticeLocation);
        imageViewPreviewNoticeCreatorImage = (ImageView) findViewById(R.id.imageViewPreviewNoticeCreatorImage);


        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.linearLayoutPreviewNoticeShowLocation))
                .getMap();
        linearLayoutPreviewMapView = (LinearLayout) findViewById(R.id.linearLayoutPreviewMapView);
        linearLayoutPreviewMapView.setVisibility(View.GONE);


        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.linearLayoutNoticeShowLocation)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }


        textViewPreviewNoticeTitleDetail.setText(noticeTitle);
        textViewPreviewNoticeTextDetail.setText(noticeContent);
        textViewPreviewNoticeChannelName.setText(channelName);
        textViewPreviewAuthorEmailAddress.setText(CommunityCenterActivity.regUserEmail);
        textViewNoticeLocation.setText("location: " + noticeLocation);
        textViewPreviewNoticeSubtitle.setText(CommunityCenterActivity.userFirstName + ", " + CommunityCenterActivity.userLastName);
        Ion.with(imageViewPreviewNoticeCreatorImage)
                .error(R.drawable.default_profile_picture)
                .placeholder(R.drawable.default_profile_picture)
                .load(CommunityCenterActivity.userAvatarURL);
        showMap();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.preview_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_publish_post) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean createPostWithoutImage(String title, String body, String channel) {
        JsonObject json = new JsonObject();
        json.addProperty("title", title);
        json.addProperty("content", body);
        json.addProperty("channel_id", channel);
        Ion.with(PreviewPost.this, Flippy.allPostURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            ToastMessages.showToastLong(PreviewPost.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        } else {


                        }
                    }

                });

        return true;
    }

    private void showMap() {
        //get this data from the intent
        if (lat.equalsIgnoreCase("") || lon.equalsIgnoreCase("") || lat == null || lon == null) {
            return;
        }
        linearLayoutPreviewMapView.setVisibility(View.VISIBLE);
        LatLng coordinate = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        googleMap.addMarker(new MarkerOptions()
                .snippet(noticeLocation)
                .title(noticeTitle)
                .position(coordinate)
                .draggable(false));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), 5));
    }
}
