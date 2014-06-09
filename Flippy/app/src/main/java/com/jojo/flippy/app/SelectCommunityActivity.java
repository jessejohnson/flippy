package com.jojo.flippy.app;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.jojo.flippy.core.CommunityCenterActivity;

import java.util.ArrayList;
import java.util.List;


public class SelectCommunityActivity extends ActionBarActivity {
    private Spinner spinnerSelectCommunity;
    private Button buttonGetStartedFromCommunity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_community);

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.last_step));

        buttonGetStartedFromCommunity = (Button) findViewById(R.id.buttonGetStartedCommunity);
        buttonGetStartedFromCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO load all the needed data and send it with the intent
                Intent intent = new Intent(SelectCommunityActivity.this,CommunityCenterActivity.class);
                startActivity(intent);
            }
        });

        //Identify the spinner from the layout and call the function to add items
        spinnerSelectCommunity = (Spinner) findViewById(R.id.spinnerSelectCommunity);
        addItemsOnCommunitySpinner();



    }

    // add items into spinner dynamically
    public void addItemsOnCommunitySpinner() {
        List<String> communityList = new ArrayList<String>();
        //TODO run a loop to add the list of communities available
        communityList.add("University of Ghana");
        communityList.add("KNUST");
        communityList.add("University of Cape coast");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.flippy_spinner_item, communityList);
        dataAdapter.setDropDownViewResource(R.layout.flippy_spinner_dropdown_item);
        spinnerSelectCommunity.setAdapter(dataAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
