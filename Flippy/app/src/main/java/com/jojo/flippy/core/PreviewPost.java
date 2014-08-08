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
import com.jojo.flippy.util.SendParseNotification;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

public class PreviewPost extends ActionBarActivity {
    private Intent intent;
    private String channelId, noticeTitle, noticeContent, lat = Flippy.defaultLat, lon = Flippy.defaultLon, channelName, noticeLocation;
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
    private String datePicked = Flippy.defaultDate, timePicked = Flippy.defaultTime;

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
        datePicked = intent.getStringExtra("datePicked");
        timePicked = intent.getStringExtra("timePicked");
        lat = intent.getStringExtra("lat");
        lon = intent.getStringExtra("lon");


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
        imageViewPreviewNoticeImageDetail.setVisibility(View.GONE);

        if (intent.getStringExtra("noticeImage") != null && !intent.getStringExtra("noticeImage").equalsIgnoreCase("")) {
            imagePath = intent.getStringExtra("noticeImage");
            imageViewPreviewNoticeImageDetail.setVisibility(View.VISIBLE);
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
                .error(R.drawable.user_error_small)
                .placeholder(R.drawable.user_place_small)
                .load(CommunityCenterActivity.userAvatarURL);
        showMap();


        buttonPublishPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reminderDateTime = datePicked + "T" + timePicked + "Z";
                if (imagePath == null || imagePath == "") {
                    createPost(noticeTitle, noticeContent, channelId, noticeLocation, lat, lon, reminderDateTime);
                } else {
                    createPost(noticeTitle, noticeContent, channelId, imagePath, noticeLocation, lat, lon, reminderDateTime);
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.preview_post, menu);
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

    private void createPost(final String title, final String body, final String channelId, String location, String latitude, String longitude, String reminder) {
        progressDialog.setMessage("publishing post ...");
        progressDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("title", title);
        json.addProperty("content", body);
        json.addProperty("channel_id", channelId);
        json.addProperty("location_name", location);
        json.addProperty("latitude", latitude);
        json.addProperty("longitude", longitude);
        json.addProperty("reminder_date", reminder);
        Ion.with(PreviewPost.this, Flippy.POST_URL)
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
                                Log.e(TAG, e.toString());
                            } else if (result != null && !result.has("detail")) {
                                ToastMessages.showToastLong(PreviewPost.this, "Notice created successfully");

                                String title = result.get("title").getAsString().trim();
                                String id = result.get("id").getAsString();
                                String content = result.get("content").getAsString();

                                SendParseNotification.sendMessage(title,content);

                                goToHome();
                                Log.e("Result", result.toString());
                            } else {
                                ToastMessages.showToastLong(PreviewPost.this, "sorry,unable to create notice");
                                Log.e("Result has details", result.toString());
                            }
                        } catch (Resources.NotFoundException error) {
                            error.printStackTrace();
                            Log.e(TAG, error.toString());
                        }
                    }

                });

    }

    private void createPost(String noticeTitle, String noticeContent, String channelId, final String image, String location, String latitude, String longitude, String reminder) {
        progressDialog.setMessage("creating the notice...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Ion.with(PreviewPost.this, Flippy.POST_URL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setMultipartParameter("title", noticeTitle)
                .setMultipartParameter("content", noticeContent)
                .setMultipartParameter("channel_id", channelId)
                .setMultipartParameter("location_name", location)
                .setMultipartParameter("latitude", latitude)
                .setMultipartParameter("longitude", longitude)
                .setMultipartParameter("reminder_date", reminder)
                .setMultipartFile("image", new File(image))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.e("Notice image", image);
                        buttonPublishPost.setEnabled(true);
                        buttonPublishPost.setText(getText(R.string.preview_post_publish_post));
                        progressDialog.dismiss();
                        try {
                            if (result.has("detail")) {
                                ToastMessages.showToastLong(PreviewPost.this, "Failed to create post");
                                Log.e(TAG, result.toString());
                                return;
                            } else if (result != null) {
                                ToastMessages.showToastLong(PreviewPost.this, "Notice created successfully");
                                Log.e(TAG, result.toString());
                                goToHome();
                            } else {
                                ToastMessages.showToastLong(PreviewPost.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                Log.e(TAG, e.toString());
                                return;
                            }
                        } catch (Exception exception) {
                            Log.e("Preview post", exception.toString());
                            exception.printStackTrace();
                        }

                    }

                });


    }

    private void showMap() {
        //get this data from the intent
        if (lat.equalsIgnoreCase(Flippy.defaultLat) || lon.equalsIgnoreCase(Flippy.defaultLon)) {
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

    private void goToHome() {
        Intent intentHome = new Intent(PreviewPost.this, CommunityCenterActivity.class);
        startActivity(intentHome);
    }
}
