package com.jojo.flippy.core;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.jojo.flippy.app.R;
import com.jojo.flippy.util.ToastMessages;

import java.util.Calendar;

public class NoticeExtrasActivity extends ActionBarActivity {
    private Intent intent;
    private String channelToCreateNotice;
    private String noticeContent, noticeTitle;
    public static int reminderYear, reminderMonth, reminderDay, reminderHour, reminderMinute;
    protected  static FragmentManager timerSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_extras);

        intent = getIntent();
        channelToCreateNotice = intent.getStringExtra("channelToCreateNotice");
        noticeTitle = intent.getStringExtra("noticeTitle");

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(channelToCreateNotice);
        actionBar.setSubtitle(noticeTitle);
        timerSupport = getSupportFragmentManager();
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            reminderYear = year;
            reminderMonth = month;
            reminderDay = day;
            ToastMessages.showToastLong(getActivity(),"Date picked is  " + year + ": " + month +" : "+ day);
            DialogFragment timer = new TimePickerFragment();
            timer.show(timerSupport,"timePicker");

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
            // collect the chosen date ready to be sent to server
            reminderHour = hourOfDay;
            reminderMinute = minute;
            ToastMessages.showToastLong(getActivity(),"Time picked is  " + hourOfDay + ": " + minute);
        }
    }

}
