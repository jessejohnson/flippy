package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.profile.ImagePreviewActivity;
import com.jojo.flippy.services.FlippyReceiver;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ImageDecoder;
import com.jojo.flippy.util.SendParseNotification;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class NoticeDetailActivity extends ActionBarActivity {
    private final String TAG = "NoticeDetailActivity";
    private SuperToast superToast;
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
    private ImageView imageViewDeletePost, imageViewRemovePost;
    private String image_link;
    private String author_email;
    private String author_first_name = " ";
    private String author_profile = "";
    private String time_stamp = "";
    private String locationName = "";
    private String locationLat = "";
    private String locationLon = "";
    private String channelId = "";
    private String startDate;
    private String noReminder;
    private LinearLayout linearLayoutMapView;
    private Calendar calendar;
    private String noPost = "Sorry, this post has been remove";
    private final int RE_FLIP = 1;
    private Context context;
    private ProgressDialog progressDialog;
    private Dao<Post, Integer> postDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        intent = getIntent();
        noticeTitle = intent.getStringExtra("noticeTitle");
        noticeId = intent.getStringExtra("noticeId");
        noticeSubtitle = intent.getStringExtra("noticeSubtitle");
        noticeBody = intent.getStringExtra("noticeBody");
        String url = Flippy.POST_URL + noticeId + "/";
        superToast = new SuperToast(NoticeDetailActivity.this);
        context = this;

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(noticeTitle);
        }
        progressDialog = new ProgressDialog(context);
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
        imageViewDeletePost = (ImageView) findViewById(R.id.imageViewDeletePost);
        imageViewRemovePost = (ImageView) findViewById(R.id.imageViewRemovePost);
        textViewNoticeLocation.setVisibility(View.GONE);


        //place holder texts
        textViewAuthorEmailAddress.setText("");
        textViewNoticeTimeStamp.setText("");
        textViewLikes.setText("");
        textViewNoticeSubtitleChannelName.setText("");
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

        //Loading the list with data from Api call
        Ion.with(context)
                .load(url)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    showSuperToast("Sorry, this post has been removed", false);
                                    return;
                                }
                                JsonObject item = result.getAsJsonObject("author");
                                author_email = item.get("email").getAsString();
                                author_first_name = item.get("first_name").getAsString();

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
                                String adminUrl = Flippy.CHANNELS_URL + channelId + "/admins/";
                                getAdminsList(adminUrl);
                                showView();
                            }
                            if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                return;
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, "Try catch, something went wrong getting detail");
                        }


                    }
                });
        imageViewNoticeImageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("avatar", image_link);
                intent.putExtra("imageName", noticeTitle);
                intent.setClass(context, ImagePreviewActivity.class);
                startActivity(intent);
            }
        });
        imageViewNoticeCreatorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("avatar", author_profile);
                intent.putExtra("imageName", author_first_name);
                intent.setClass(context, ImagePreviewActivity.class);
                startActivity(intent);
            }
        });
        imageViewRemovePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmNoticeDelete(noticeId, false);
            }
        });
        imageViewDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmNoticeDelete(noticeId, true);
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
                String ratingURL = Flippy.POST_URL + noticeId + "/star/";
                JsonObject json = new JsonObject();
                json.addProperty("id", noticeId);
                Ion.with(context)
                        .load(ratingURL)
                        .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                try {
                                    if (result != null) {
                                        if (result.has("detail")) {
                                            Crouton.makeText(NoticeDetailActivity.this, noPost, Style.ALERT);
                                            return;
                                        } else {
                                            showSuperToast(result.get("results").getAsString(), true);
                                        }
                                    }
                                    if (e != null) {
                                        showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                    }
                                    getPostCount(noticeId);

                                } catch (Exception exception) {
                                    Log.e(TAG, "Error try catch rating a notice");
                                }
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
            if (locationName == null || locationLon == null) {
                showSuperToast("Notice not ready for re-flip, check internet connection", false);
            } else {
                intent.setClass(NoticeDetailActivity.this, SelectChannelActivity.class);
                intent.putExtra("isReFlip", true);
                startActivityForResult(intent, RE_FLIP);
            }

            return true;
        }
        if (id == R.id.action_notice_alarm) {
            setAlarm();
            return true;
        }
        if (id == R.id.action_share_all) {
            if (image_link == null || image_link.equalsIgnoreCase("")) {
                shareNoticeWithOtherApps(noticeTitle, noticeBody);
            } else {
                Log.e(TAG, image_link);
                downloadPhoto(image_link, noticeTitle, noticeBody);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSuperToast(String message, boolean isSuccess) {
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.SHORT);
        if (isSuccess) {
            superToast.setDuration(SuperToast.Duration.SHORT);
        } else {
            superToast.setBackground(SuperToast.Background.PURPLE);
        }

        superToast.setIcon(R.drawable.icon_dark_info, SuperToast.IconPosition.LEFT);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setText(message);
        superToast.show();
    }


    private void setAlarm() {
        if (noReminder == null) {
            showSuperToast("This notice has no reminder", false);
            return;
        }
        if (startDate == null || startDate.equalsIgnoreCase(Flippy.defaultDate + "T" + Flippy.defaultTime + "Z")) {
            showSuperToast("This notice has no reminder date", false);
            return;
        }
        String actualDate[] = startDate.replace("Z", "").trim().split("T");
        String date = actualDate[0];
        String time = actualDate[1];
        int month, year, day, hour, minute, seconds;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar c = Calendar.getInstance();
            Date dateConverted = dateFormat.parse(date.toString());
            if (dateConverted.compareTo(c.getTime()) < 1) {
                showSuperToast("Notice is long due", false);
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error doing date comparison to set reminder");
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
            showSuperToast("Failed to set reminder", false);
            Log.e(TAG, e.toString());
            return;
        }
        setCalenderReminder(month, year, day, hour, minute, seconds);

    }

    private void setCalenderReminder(int month, int year, int day, int hour, int minute, int seconds) {
        calendar = Calendar.getInstance();

        //getting the alarm settings
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String minutes = SP.getString("alarm_delay", "0");
        int alarmAlert = Integer.parseInt(minutes);

        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute - alarmAlert);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        Intent alarmIntent = new Intent(NoticeDetailActivity.this, FlippyReceiver.class);
        alarmIntent.putExtra("noticeId", noticeId);
        alarmIntent.putExtra("noticeSubtitle", noticeSubtitle);
        alarmIntent.putExtra("noticeBody", noticeBody);
        alarmIntent.putExtra("noticeTitle", noticeTitle);
        pendingIntent = PendingIntent.getBroadcast(NoticeDetailActivity.this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        showSuperToast("Reminder set successfully", true);
    }

    private void showView() {
        Ion.with(imageViewNoticeImageDetail)
                .animateIn(R.anim.fade_in)
                .error(R.drawable.notice_error)
                .placeholder(R.drawable.notice_place)
                .load(image_link);
        Ion.with(imageViewNoticeCreatorImage)
                .animateIn(R.anim.fade_in)
                .placeholder(R.drawable.user_place_small)
                .error(R.drawable.user_error_small)
                .load(author_profile);
        textViewAuthorEmailAddress.setText(author_email);
        textViewNoticeTimeStamp.setText(time_stamp);
        if (CommunityCenterActivity.regUserEmail.equalsIgnoreCase(author_email)) {
            imageViewDeletePost.setVisibility(View.VISIBLE);
        }

    }

    private void getPostCount(String id) {
        String URL = Flippy.POST_URL + id + "/count_ratings/";
        Ion.with(NoticeDetailActivity.this)
                .load(URL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    return;
                                }
                                String item = result.get("results").getAsString();
                                textViewLikes.setText(item + " Star(s)");
                                return;
                            }
                            if (e != null) {
                                ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                return;
                            }

                        } catch (Exception e1) {
                            Log.e(TAG, "Error getting the rating count of a notice");
                        }


                    }
                });
    }

    private void getPostLocation(String id) {
        String postLocationURL = Flippy.POST_URL + id + "/location/";
        Ion.with(NoticeDetailActivity.this)
                .load(postLocationURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    return;
                                }
                                JsonObject item = result.getAsJsonObject("results");
                                locationName = item.get("local_name").getAsString();
                                locationLat = item.get("latitude").getAsString();
                                locationLon = item.get("longitude").getAsString();
                                showMap();
                            }
                            if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                            }
                        } catch (Exception el) {
                            Log.e(TAG, "Error getting the location of a notice");
                        }
                    }
                });
    }

    private void getNoticeReminder(String id) {
        String postReminderURL = Flippy.POST_URL + id + "/reminder/";
        Ion.with(NoticeDetailActivity.this)
                .load(postReminderURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                if (result.has("detail")) {
                                    noReminder = result.get("detail").getAsString();
                                    return;
                                }
                                JsonObject item = result.getAsJsonObject("results");
                                startDate = item.get("start_date").getAsString();
                            }
                            if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                            }

                        } catch (Exception el) {
                            Log.e(TAG, "Error get the reminder " + el.toString());
                        }

                    }
                });
    }

    private void showMap() {
        //get this data from the intent
        if (locationLon.equalsIgnoreCase("") || locationLat.equalsIgnoreCase("") || locationLat.equalsIgnoreCase(Flippy.defaultLat) || locationLon.equalsIgnoreCase(Flippy.defaultLon)) {
            return;
        }
        linearLayoutMapView.setVisibility(View.VISIBLE);
        if (!locationName.equalsIgnoreCase("")) {
            textViewNoticeLocation.setVisibility(View.VISIBLE);
            textViewNoticeLocation.setText("location : " + locationName);
        }
        LatLng coordinate = new LatLng(Double.parseDouble(locationLat), Double.parseDouble(locationLon));
        googleMap.addMarker(new MarkerOptions()
                .snippet(locationName)
                .title(noticeTitle)
                .position(coordinate)
                .draggable(false));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(locationLat), Double.parseDouble(locationLon)), 5));
    }

    private void getChannelName(String id) {
        Ion.with(NoticeDetailActivity.this)
                .load(Flippy.CHANNELS_URL + id + "/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception ex, JsonObject nameResult) {
                        try {
                            if (nameResult != null) {
                                if (nameResult.has("detail")) {
                                    return;
                                }
                                textViewNoticeSubtitleChannelName.setVisibility(View.VISIBLE);
                                textViewNoticeSubtitleChannelName.setText(nameResult.get("name").getAsString());
                            }
                            if (ex != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                            }

                        } catch (Exception exception) {
                            Log.e(TAG, "Error getting the channel name " + exception.toString());
                        }

                    }
                });
    }

    private void shareNoticeWithOtherApps(String title, String body) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + "\n" + body + "\n" + "\n" + "Flippy team");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            showSuperToast("No channel selected", false);
            return;
        } else if (resultCode == RESULT_OK && requestCode == RE_FLIP) {
            String channelId = data.getStringExtra("channelId");
            String channelName = data.getStringExtra("channelName");
            Log.e(TAG, channelId + " " + channelName);
            reFlipPost(channelId, channelName);

        } else {
            showSuperToast("No channel selected", false);
            Log.e(TAG, "something went wrong on result of re-flip");
            return;
        }
    }

    private void confirmNoticeDelete(final String noticeId, final boolean isDelete) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm your action");
        alert.setIcon(R.drawable.icon_dark_info);
        if (isDelete) {
            alert.setMessage("Deleting this notice is irreversible and your CHANNELS_URL members will longer have access to the details, are you sure you want to continue ?");
        } else {
            alert.setMessage("Removing this notice is irreversible, are you sure you want to continue ?");

        }
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (isDelete) {
                    deleteNotice(noticeId);
                } else {
                    removeNoticeFormDb(noticeId);
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        });
        alert.show();
    }

    private void removeNoticeFormDb(String noticeId) {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(NoticeDetailActivity.this,
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            Post post = postDao.queryForId(Integer.parseInt(noticeId));
            if (post != null) {
                postDao.delete(post);
            }
            showSuperToast("Notice successfully removed from board", true);
            goHome();

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e(TAG, sqlE.toString());
        }
    }

    private void getAdminsList(String url) {
        final ArrayList<String> channelAdmins = new ArrayList<String>();
        Ion.with(NoticeDetailActivity.this)
                .load(url)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result.has("detail")) {
                                Log.e(TAG, "Admin list not found");
                                return;
                            } else if (e != null) {
                                Log.e(TAG, e.toString());
                                return;
                            } else if (result != null) {
                                JsonArray adminArray = result.getAsJsonArray("results");
                                for (int i = 0; i < adminArray.size(); i++) {
                                    JsonObject item = adminArray.get(i).getAsJsonObject();
                                    channelAdmins.add(item.get("id").getAsString());
                                }
                            } else {
                                Log.e(TAG, "Something else happened");
                                return;
                            }
                            //managing the channel buttons
                            String userId = CommunityCenterActivity.regUserID;
                            if (channelAdmins.contains(userId)) {
                                imageViewDeletePost.setVisibility(View.VISIBLE);
                            } else {
                                imageViewDeletePost.setVisibility(View.GONE);
                            }
                        } catch (Exception error) {
                            Log.e(TAG, "Error occurred when getting admin list " + error.toString());
                        }
                    }
                });
    }

    private void deleteNotice(final String id) {
        Ion.with(NoticeDetailActivity.this, Flippy.POST_URL + id + "/delete/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (e != null) {
                                ToastMessages.showToastLong(NoticeDetailActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            } else if (result != null) {
                                Log.e("Result", result.toString());
                                removeNoticeFormDb(id);
                            } else {
                                Log.e("Result has details", result.toString());
                            }
                        } catch (Resources.NotFoundException error) {
                            error.printStackTrace();
                            Log.e(TAG, error.toString());
                        }
                    }

                });

    }

    private void reFlipPost(final String channelId, final String channelName) {
        progressDialog.setMessage("reflipping the notice...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Ion.with(context, Flippy.POST_URL + noticeId + "/reflip/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setMultipartParameter("channel_id", channelId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        try {
                            if (result.has("detail")) {
                                ToastMessages.showToastLong(context, "Failed to reflip notice");
                                Log.e(TAG, result.toString());
                                return;
                            } else if (result != null) {
                                ToastMessages.showToastLong(context, "Notice refliped successfully into " + channelName);
                                JsonObject jsonObject = result.getAsJsonObject("results");
                                String id = jsonObject.get("id").getAsString();
                                SendParseNotification.sendMessage(noticeTitle, id, noticeBody, channelId);
                            } else {
                                ToastMessages.showToastLong(context, getResources().getString(R.string.internet_connection_error_dialog_title));
                                Log.e(TAG, e.toString());
                                return;
                            }
                        } catch (Exception exception) {
                            Log.e("Reflip post", exception.toString());
                            exception.printStackTrace();
                        }

                    }

                });


    }

    private void downloadPhoto(final String image, final String noticeTitle, final String noticeBody) {
        final ProgressDialog progressDialogShare = new ProgressDialog(context);
        progressDialogShare.setMessage("preparing notice to share ...");
        progressDialogShare.show();
        if (image == null) {
            ToastMessages.showToastLong(context, "Sharing failed");
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(image, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                progressDialogShare.dismiss();
                Log.e(TAG, response.getAbsolutePath());
                Bitmap bitmap = ImageDecoder.decodeFile(response.getAbsolutePath());
                String imagePath = ImageDecoder.saveBitmap(bitmap, "Flippy", "Media");
                shareImage(imagePath, noticeTitle, noticeBody);
                response.deleteOnExit();
            }

            @Override
            public void onFailure(Throwable e, File response) {
                progressDialogShare.dismiss();
                ToastMessages.showToastLong(context, "Sharing failed");
            }

        });
    }

    private void shareImage(String imagePath, String noticeTitle, String noticeBody) {
        Uri photoUri = Uri.parse(imagePath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, noticeTitle + "\n" + noticeBody);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "send"));
    }

    private void goHome() {
        Intent intentHome = new Intent();
        intentHome.setClass(context, CommunityCenterActivity.class);
        startActivity(intentHome);

    }

}
