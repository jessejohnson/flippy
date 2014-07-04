package com.jojo.flippy.core;

import android.app.ActionBar;
import android.preference.PreferenceActivity;
import android.os.Bundle;

import com.jojo.flippy.app.R;

public class GeneralSettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
}