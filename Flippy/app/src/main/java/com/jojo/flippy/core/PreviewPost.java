package com.jojo.flippy.core;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    private ImageView imageViewPreviewNoticeCreatorImage, imageViewPreviewNoticeImageDetail;
    private GoogleMap googleMap;
    private LinearLayout linearLayoutPreviewMapView;
    private Bitmap bitmap;
    private Button buttonPublishPost;
    private String imagePath;
    private static String TAG = "PreviewPost";
    private ProgressDialog progressDialog;

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
        if (intent.getStringExtra("lat") != null) {
            lat = intent.getStringExtra("lat");
            lon = intent.getStringExtra("lon");
        }

        imageViewPreviewNoticeImageDetail = (ImageView) findViewById(R.id.imageViewPreviewNoticeImageDetail);
        textViewPreviewNoticeTitleDetail = (TextView) findViewById(R.id.textViewPreviewNoticeTitleDetail);
        textViewPreviewNoticeSubtitle = (TextView) findViewById(R.id.textViewPreviewNoticeSubtitle);
        textViewPreviewNoticeTextDetail = (TextView) findViewById(R.id.textViewPreviewNoticeTextDetail);
        textViewPreviewNoticeChannelName = (TextView) findViewById(R.id.textViewPreviewNoticeChannelName);
        textViewPreviewAuthorEmailAddress = (TextView) findViewById(R.id.textViewPreviewAuthorEmailAddress);
        textViewNoticeLocation = (TextView) findViewById(R.id.textViewNoticeLocation);
        imageViewPreviewNoticeCreatorImage = (ImageView) findViewById(R.id.imageViewPreviewNoticeCreatorImage);
        buttonPublishPost = (Button) findViewById(R.id.buttonPublishPost);
        progressDialog = new ProgressDialog(PreviewPost.this);

        if (intent.getStringExtra("noticeImage") != null) {
            imagePath = intent.getStringExtra("noticeImage");
            if (imagePath == null || imagePath == "") {
                imageViewPreviewNoticeImageDetail.setVisibility(View.GONE);
            }
            decodeFile(imagePath);
        }

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


        buttonPublishPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagePath == null || imagePath == "") {
                    createPostWithoutImage(noticeTitle, noticeContent, channelId);
                }
            }
        });


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
        progressDialog.setMessage("publishing post ...");
        progressDialog.show();
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
                        progressDialog.dismiss();
                        try {
                            if (e != null) {
                                ToastMessages.showToastLong(PreviewPost.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            } else if (result != null) {
                                Log.e("Result", result.toString());
                            } else {
                                Log.e("Result has details", result.toString());
                            }
                        } catch (Resources.NotFoundException error) {
                            error.printStackTrace();
                            Log.e(TAG, error.toString());
                        }
                    }

                });

        return true;
    }

    private void showMap() {
        //get this data from the intent
        if (lat == null || lon == null || lat.equalsIgnoreCase("") || lon.equalsIgnoreCase("")) {
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

    public void decodeFile(String filePath) {
        Log.e("File path", filePath);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        final int REQUIRED_SIZE = 1024;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);
        imageViewPreviewNoticeImageDetail.setImageBitmap(bitmap);
    }
}
