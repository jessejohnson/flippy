package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.profile.ImagePreviewActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.FlippyReceiver;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class NoticeDetailActivity extends ActionBarActivity {
    private Button buttonPublishNotice;
    private GoogleMap googleMap;
    private Intent intent;
    private PendingIntent pendingIntent;
    private String noticeTitle;
    private String noticeId;
    private String noticeBody;
    private String noticeSubtitle;


    private TextView textViewNoticeTitleDetail;
    private TextView textViewNoticeSubtitleDetail;
    private TextView textViewNoticeTextDetail;
    private TextView textViewLikes;
    private TextView textViewNoticeLocation;
    private TextView textViewAuthorEmailAddress;
    private TextView textViewNoticeTimeStamp;
    private TextView textViewNoticeSubtitleChannelName;
    private ImageView imageViewNoticeImageDetail;
    private ImageView imageViewNoticeCreatorImage;
    private ImageView imageViewStarDetail;


    private String image_link;
    private String author_email;
    private String author_profile = "";
    private String time_stamp = "";
    private String locationName = "";
    private String locationLat = "";
    private String locationLon = "";
    private String channelId = "";
    private String startDate;
    private String endDate;
    private int reminderInterval;
    private String noReminder;

    private LinearLayout linearLayoutMapView;

    private Calendar calendar;

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
        imageViewStarDetail = (ImageView) findViewById(R.id.imageViewStarDetail);
        imageViewNoticeCreatorImage = (ImageView) findViewById(R.id.imageViewNoticeCreatorImage);
        textViewNoticeTitleDetail = (TextView) findViewById(R.id.textViewNoticeTitleDetail);
        textViewNoticeLocation = (TextView) findViewById(R.id.textViewNoticeLocation);
        textViewLikes = (TextView) findViewById(R.id.textViewLikes);
        textViewNoticeTimeStamp = (TextView) findViewById(R.id.textViewNoticeTimeStamp);
        textViewAuthorEmailAddress = (TextView) findViewById(R.id.textViewAuthorEmailAddress);
        textViewNoticeSubtitleDetail = (TextView) findViewById(R.id.textViewNoticeSubtitleDetail);
        textViewNoticeTextDetail = (TextView) findViewById(R.id.textViewNoticeTextDetail);
        textViewNoticeSubtitleChannelName = (TextView) findViewById(R.id.textViewNoticeSubtitleChannelName);
        textViewNoticeTitleDetail.setText(noticeTitle);
        textViewNoticeTextDetail.setText(noticeBody);
        textViewNoticeSubtitleDetail.setText(noticeSubtitle);
        textViewNoticeSubtitleChannelName.setText("");


        //place holder texts
        textViewAuthorEmailAddress.setText("");
        textViewNoticeTimeStamp.setText("");
        textViewLikes.setText("");
        textViewNoticeLocation.setText("");
        imageViewNoticeImageDetail.setVisibility(View.GONE);
        textViewNoticeSubtitleChannelName.setVisibility(View.GONE);


        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.linearLayoutNoticeShowLocation))
                .getMap();
        linearLayoutMapView = (LinearLayout) findViewById(R.id.linearLayoutMapView);
        linearLayoutMapView.setVisibility(View.GONE);


        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.linearLayoutNoticeShowLocation)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }


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
                            channelId = result.get("channel").getAsString();
                            String[] timestampArray = result.get("timestamp").getAsString().replace("Z", "").split("T");
                            time_stamp = timestampArray[0].toString() + " @ " + timestampArray[1].substring(0, 8);
                            image_link = "";
                            if (!result.get("image_url").isJsonNull()) {
                                imageViewNoticeImageDetail.setVisibility(View.VISIBLE);
                                image_link = result.get("image_url").getAsString();
                            }
                            getChannelName(channelId);
                            showView();
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
        imageViewNoticeImageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("avatar", image_link);
                intent.setClass(NoticeDetailActivity.this, ImagePreviewActivity.class);
                startActivity(intent);
            }
        });

        //loads the post rating from the api
        getPostCount(noticeId);
        getPostLocation(noticeId);
        getNoticeReminder(noticeId);

        //rating a notice on the click of the the star button
        imageViewStarDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ratingURL = Flippy.allPostURL + noticeId + "/star/";
                JsonObject json = new JsonObject();
                json.addProperty("id", noticeId);
                Ion.with(NoticeDetailActivity.this)
                        .load(ratingURL)
                        .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null) {
                                    ToastMessages.showToastLong(NoticeDetailActivity.this, result.get("results").getAsString());
                                }
                                if (e != null) {
                                    ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                }
                                getPostCount(noticeId);

                            }

                        });
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings_re_flip) {
            intent.setClass(NoticeDetailActivity.this, SelectChannelActivity.class);
            //pass data along and create a notice based on the selected channel
            startActivity(intent);
            ToastMessages.showToastLong(NoticeDetailActivity.this, "sharing in other channels");
            return true;
        }
        if (id == R.id.action_notice_alarm) {
            setAlarm();
            return true;
        }
        if (id == R.id.action_share_all) {
            shareNoticeWithOtherApps(noticeTitle, noticeBody, image_link, getString(R.string.splash_screen_url));
            ToastMessages.showToastLong(NoticeDetailActivity.this, "Notice shared");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAlarm() {

        if (noReminder != null) {
            ToastMessages.showToastLong(NoticeDetailActivity.this, "This notice has no reminder date");
            return;
        }
        if (startDate == null) {
            ToastMessages.showToastLong(NoticeDetailActivity.this, "This notice has no reminder date");
            return;
        }
        String actualDate[] = startDate.replace("Z", "").trim().split("T");
        String date = actualDate[0];
        String time = actualDate[1];
        int month, year, day, hour, minute, seconds = 0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar c = Calendar.getInstance();
            Date dateConverted = dateFormat.parse(date.toString());
            if (dateConverted.compareTo(c.getTime()) < 1) {
                ToastMessages.showToastLong(NoticeDetailActivity.this, "Notice is long due");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            String dateArray[] = date.toString().split("-");
            String timeArray[] = time.toString().split(":");
            month = Integer.parseInt(dateArray[1]);
            year = Integer.parseInt(dateArray[0]);
            day = Integer.parseInt(dateArray[2]);
            hour = Integer.parseInt(timeArray[0]);
            minute = Integer.parseInt(timeArray[1]);
            seconds = Integer.parseInt(timeArray[2]);
        } catch (Exception e) {
            Crouton.makeText(NoticeDetailActivity.this, "Failed to set reminder", Style.ALERT)
                    .show();
            return;
        }
        setCalenderReminder(month, year, day, hour, minute, seconds);

    }

    private void setCalenderReminder(int month, int year, int day, int hour, int minute, int seconds) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        Intent alarmIntent = new Intent(NoticeDetailActivity.this, FlippyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(NoticeDetailActivity.this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        if (saveNoticeAlarm(calendar.getTimeInMillis())) {
            ToastMessages.showToastLong(NoticeDetailActivity.this, "Reminder set successfully");
            return;
        }

    }

    private void showView() {
        Ion.with(imageViewNoticeImageDetail)
                .animateIn(R.anim.fade_in)
                .error(R.color.flippy_orange)
                .placeholder(R.drawable.channel_bg)
                .load(image_link);
        Ion.with(imageViewNoticeCreatorImage)
                .animateIn(R.anim.fade_in)
                .placeholder(R.drawable.channel_bg)
                .error(R.color.flippy_orange)
                .load(author_profile);
        textViewAuthorEmailAddress.setText(author_email);
        textViewNoticeTimeStamp.setText(time_stamp);

    }

    private boolean saveNoticeAlarm(long id) {
        boolean set = false;
        try {
            Dao<Post, Integer> postDao = ((Flippy) getApplication()).postDao;
            Post post = new Post(id, noticeId, noticeTitle, noticeSubtitle, noticeBody);
            postDao.create(post);
            set = true;
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            ToastMessages.showToastLong(NoticeDetailActivity.this, "Sorry, this notice is already on reminder");
            set = false;
        }
        return set;
    }

    private void getPostCount(String id) {
        String URL = Flippy.allPostURL + id + "/count_ratings/";
        Ion.with(NoticeDetailActivity.this)
                .load(URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            String item = result.get("results").getAsString();
                            textViewLikes.setText(item + " Star(s)");
                            return;
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            return;
                        }

                    }
                });
    }

    private void getPostLocation(String id) {
        String postLocationURL = Flippy.allPostURL + id + "/location/";
        Ion.with(NoticeDetailActivity.this)
                .load(postLocationURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            JsonObject item = result.getAsJsonObject("results");
                            locationName = item.get("local_name").getAsString();
                            locationLat = item.get("latitude").getAsString();
                            locationLon = item.get("longitude").getAsString();
                            showMap();
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    private void getNoticeReminder(String id) {
        String postReminderURL = Flippy.allPostURL + id + "/reminder/";
        Ion.with(NoticeDetailActivity.this)
                .load(postReminderURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            if (result.has("detail")) {
                                noReminder = result.get("detail").getAsString();
                                return;
                            }
                            JsonObject item = result.getAsJsonObject("results");
                            startDate = item.get("start_date").getAsString();
                            endDate = item.get("end_date").getAsString();
                            reminderInterval = item.get("repeat_interval").getAsInt();
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    private void showMap() {
        //get this data from the intent
        if (locationLon.equalsIgnoreCase("") || locationLat.equalsIgnoreCase("")) {
            return;
        }
        linearLayoutMapView.setVisibility(View.VISIBLE);
        textViewNoticeLocation.setText("location : " + locationName);
        LatLng coordinate = new LatLng(Double.parseDouble(locationLat), Double.parseDouble(locationLon));
        googleMap.addMarker(new MarkerOptions()
                .snippet(locationName)
                .title(noticeTitle)
                .position(coordinate)
                .draggable(false));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(locationLat), Double.parseDouble(locationLon)), 5));
    }

    private void getChannelName(String id) {
        //load the channel name using the channel id
        Ion.with(NoticeDetailActivity.this)
                .load(Flippy.channelDetailURL + id + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception ex, JsonObject nameResult) {

                        if (nameResult != null) {
                            textViewNoticeSubtitleChannelName.setVisibility(View.VISIBLE);
                            textViewNoticeSubtitleChannelName.setText(nameResult.get("name").getAsString());
                        }
                        if (ex != null) {
                            ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    private void shareNoticeWithOtherApps(String title, String body, String imageLink, String footer) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + body + "\n" + imageLink + "\n" + footer);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share Flippy notice via ..."));
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
}
