package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jojo.flippy.app.R;

public class NoticeDetailActivity extends ActionBarActivity {
    private Button buttonPublishNotice;
    private GoogleMap googleMap;
    private Intent intent;
    private boolean isAttachedWithImage = true;
    private String noticeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        noticeTitle = intent.getStringExtra("noticeTitle");
        isAttachedWithImage = intent.getBooleanExtra("isAttachedWithImage",true);

        ImageView imageViewNoticeImageDetail = (ImageView)findViewById(R.id.imageViewNoticeImageDetail);
        if(!isAttachedWithImage){
            imageViewNoticeImageDetail.setVisibility(View.GONE);
        }

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
                .title("Notice title here")
                .position(coordinate)
                .draggable(false));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(5.5500, 0.2000), 5));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notice_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit_profile) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
