package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Calendar;

public class NoticeExtrasActivity extends ActionBarActivity {
    private static final int BROWSE_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int START_MAP = 3;
    public static int reminderYear, reminderMonth, reminderDay, reminderHour, reminderMinute;
    protected static FragmentManager timerSupport;
    private static String datePicked;
    private static String timePicked;
    Uri imageUri;
    private Intent intent;
    private String channelToCreateNotice;
    private String noticeTitle;
    private Button buttonAddImageToNotice, buttonPreviewCreateNotice, buttonAddMapToNotice;
    private AlertDialog levelDialog;
    private String lat, lon;
    Uri selectedImageUri = null;
    private String filePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_extras);

        intent = getIntent();
        channelToCreateNotice = intent.getStringExtra("channelId");
        noticeTitle = intent.getStringExtra("noticeTitle");

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(noticeTitle);
        }

        timerSupport = getSupportFragmentManager();


        //the image upload button
        buttonPreviewCreateNotice = (Button) findViewById(R.id.buttonPreviewCreateNotice);
        buttonPreviewCreateNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("channelId", channelToCreateNotice);
                intent.putExtra("noticeTitle", noticeTitle);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("datePicked", datePicked);
                intent.putExtra("timePicked", timePicked);
                intent.putExtra("noticeImage",filePath);
                intent.setClass(NoticeExtrasActivity.this, PreviewPost.class);
                startActivity(intent);
            }
        });
        buttonAddImageToNotice = (Button) findViewById(R.id.buttonAddImageToNotice);
        buttonAddImageToNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUploadOptions();
            }
        });

        buttonAddMapToNotice = (Button) findViewById(R.id.buttonAddMapToNotice);
        buttonAddMapToNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(NoticeExtrasActivity.this, PickMapLocationActivity.class);
                startActivityForResult(intent, START_MAP);
            }
        });
    }

    //a method that evokes the date picker
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // a method that shows the time picker
    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void imageUploadOptions() {
        final CharSequence[] items = {"Browse gallery", " Take photo "};
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.optional_notice_choose);
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        // Your code when first option selected
                        try {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select Picture"),
                                    BROWSE_IMAGE);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            Log.e(e.getClass().getName(), e.getMessage(), e);
                        }
                        break;
                    case 1:
                        // Your code when 2nd  option selected
                        //define the file-name to save photo taken by Camera activity
                        String fileName = "new-photo-name.jpg";
                        //create parameters for Intent with filename
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, fileName);
                        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                        //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        //create new Intent
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        startActivityForResult(intent, TAKE_PHOTO);
                        break;
                }
                levelDialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case BROWSE_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                } else {
                    Toast.makeText(this, "Picture was not uploaded", Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    selectedImageUri = imageUri;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
            case START_MAP:
                if (resultCode == RESULT_OK) {
                    Log.e("From location", data.getStringExtra("location"));
                    String[] location = data.getStringExtra("location").split(",");
                    lat = location[0];
                    lon = location[1];

                } else {
                    Toast.makeText(this, "No location selected", Toast.LENGTH_SHORT).show();
                }
        }

        if (selectedImageUri != null) {
            try {
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else {
                    Toast.makeText(getApplicationContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

            /*    if (filePath != null) {
                    decodeFile(filePath);
                } else {
                    bitmapNotice = null;
                }
            */
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Sorry, Something went wrong",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            reminderYear = year;
            reminderMonth = month;
            reminderDay = day;
            datePicked = day + ": " + month + " : " + year;
            ToastMessages.showToastLong(getActivity(), "Date picked is  " + year + ": " + month + " : " + day);
            DialogFragment timer = new TimePickerFragment();
            timer.show(timerSupport, "timePicker");

        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            reminderHour = hourOfDay;
            reminderMinute = minute;
            timePicked = hourOfDay + ": " + minute;
            ToastMessages.showToastLong(getActivity(), "Time picked is  " + hourOfDay + ": " + minute);
        }
    }

}
