package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.ToastMessages;

public class PickMapLocationActivity extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {
    private GoogleMap googleMap;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_location);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pick_map_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_location) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapClick(LatLng point) {
        googleMap.addMarker(new MarkerOptions()
                .snippet("Pick a location for the notice")
                .title("Notice location")
                .position(point)
                .draggable(true));
        Log.e("On clicked","Fired");

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public void onMapLongClick(LatLng point) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
