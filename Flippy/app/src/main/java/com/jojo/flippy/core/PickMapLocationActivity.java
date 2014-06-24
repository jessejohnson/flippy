package com.jojo.flippy.core;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.ToastMessages;

public class PickMapLocationActivity extends FragmentActivity {
    private GoogleMap map;
    double latitude = 17.385044;
    double longitude = 78.486671;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_map_location);

        try {
            // Loading map
            setUpMapIfNeeded();
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Flippy office");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            map.addMarker(marker);

        } catch (Exception e) {
            // Check if we were successful in obtaining the map.
            ToastMessages.showToastLong(getApplication(), getResources().getString(R.string.internet_connection_error_dialog_title));
            e.printStackTrace();
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.linearLayoutNoticeLocationCoordinate))
                    .getMap();
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(true);
            if (map != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }
    }
    private void changeCameraPosition(double lat,double lon){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lon)).zoom(12).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pick_map_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_location) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pick_map_location, container, false);
            //check internet connection
            return rootView;
        }
    }
}
