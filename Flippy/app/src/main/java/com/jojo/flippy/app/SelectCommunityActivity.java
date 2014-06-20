package com.jojo.flippy.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.InternetConnectionDetector;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SelectCommunityActivity extends Activity {
    private Spinner spinnerSelectCommunity;
    private Button buttonGetStartedFromCommunity;
    private String defaultSpinnerItem = "Choose a community";
    private String communitiesURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/communities/";
    private ProgressDialog loadingCommunityDialog;
    private EditText editTextCommunityKey;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_community);


        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.last_step));
        final ArrayList<String> communityListAdapt = new ArrayList<String>();
        communityListAdapt.add(defaultSpinnerItem);

        intent = getIntent();

        loadingCommunityDialog = new ProgressDialog(SelectCommunityActivity.this);
        editTextCommunityKey = (EditText) findViewById(R.id.editTextCommunityKey);
        final InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(this);


        Ion.with(SelectCommunityActivity.this)
                .load(communitiesURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result!=null){
                            Log.e("response",result.toString());
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i=0 ; i<communityArray.size();i++){
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                communityListAdapt.add(item.get("name").getAsString());
                                Log.e("Item",item.get("name").getAsString());
                            }

                        }
                        if(e!=null){
                            Log.e("error",e.toString());
                        }

                    }
                });

        //Identify the spinner from the layout and call the function to add items
        spinnerSelectCommunity = (Spinner) findViewById(R.id.spinnerSelectCommunity);
        addItemsOnCommunitySpinner(communityListAdapt);


        buttonGetStartedFromCommunity = (Button) findViewById(R.id.buttonGetStartedCommunity);
        buttonGetStartedFromCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String communitySelected = (String) spinnerSelectCommunity.getSelectedItem();
                if (communitySelected.equals(defaultSpinnerItem) || editTextCommunityKey.getText().toString()=="") {
                    Crouton.makeText(SelectCommunityActivity.this, "Flippy, please select a community or enter a community key", Style.ALERT)
                            .show();
                    return;
                }
                if (!internetConnectionDetector.isConnectingToInternet()) {
                    onCreateDialog();
                    return;
                }
                intent.setClass(SelectCommunityActivity.this, CommunityCenterActivity.class);
                intent.putExtra("communitySelected", communitySelected);
                startActivity(intent);
            }
        });
    }

    // add items into spinner dynamically
    public void addItemsOnCommunitySpinner(ArrayList<String> communityList) {
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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectCommunityActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = SelectCommunityActivity.this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.customize_alert_dialog, null))
                // Add action buttons
                .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });
        return builder.create();
    }

}
