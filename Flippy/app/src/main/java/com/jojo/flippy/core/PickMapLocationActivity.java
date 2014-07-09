package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jojo.flippy.app.R;

public class PickMapLocationActivity extends ActionBarActivity {
    private GoogleMap googleMap;
    private Location mLocation;
    private String lat, lon;
    private Intent intent;

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_location);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();


        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.linearLayoutNoticeLocationCoordinate))
                .getMap();

        try {
            if (googleMap == null) {
                googleMap = ((SupportMapFragment) getSupportFragmentManager().
                        findFragmentById(R.id.linearLayoutNoticeLocationCoordinate)).getMap();

            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            mLocation = googleMap.getMyLocation();

        } catch (Exception e) {
            e.printStackTrace();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                        .title("Notice location")
                        .snippet("Choose the location for the notice")
                        .position(new LatLng(4.000, -5.00))
        );
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker.setPosition(latLng);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_map_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_location) {
            lat = marker.getPosition().latitude + "";
            lon = marker.getPosition().longitude + "";
            String location = lat + "," + lon;
            intent.putExtra("location", location);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
