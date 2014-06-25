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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.InternetConnectionDetector;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLDataException;
import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SelectCommunityActivity extends Activity {
    private Spinner spinnerSelectCommunity;
    private Button buttonGetStartedFromCommunity;
    private String defaultSpinnerItem = "Choose a community";
    //TODO get communityKeyURL
    private String communityKeyURL = "";
    private EditText editTextCommunityKey;
    private Intent intent;
    private String selectedCommunityID;
    private String communitySelected;
    private String regUserEmail;
    private ProgressBar progressBarLoadCommunity;
    private ScrollView scrollViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_community);


        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.last_step));
        final ArrayList<String> communityListAdapt = new ArrayList<String>();
        final ArrayList<String> communityListId = new ArrayList<String>();
        communityListId.add("flippy01");
        communityListAdapt.add(defaultSpinnerItem);
        progressBarLoadCommunity = (ProgressBar)findViewById(R.id.progressBarLoadCommunity);
        buttonGetStartedFromCommunity = (Button) findViewById(R.id.buttonGetStartedCommunity);
        scrollViewLogin = (ScrollView)findViewById(R.id.scrollViewLogin);
        scrollViewLogin.setVisibility(View.GONE);
        buttonGetStartedFromCommunity.setVisibility(View.GONE);

        intent = getIntent();
        regUserEmail = intent.getStringExtra("regUserEmail");

        editTextCommunityKey = (EditText) findViewById(R.id.editTextCommunityKey);
        spinnerSelectCommunity = (Spinner) findViewById(R.id.spinnerSelectCommunity);
        spinnerSelectCommunity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                selectedCommunityID = communityListId.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        });
        final InternetConnectionDetector internetConnectionDetector = new InternetConnectionDetector(this);

        Ion.with(SelectCommunityActivity.this)
                .load(Flippy.communitiesURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        showViews();
                        progressBarLoadCommunity.setVisibility(View.GONE);
                        if (result != null) {
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i = 0; i < communityArray.size(); i++) {
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                communityListAdapt.add(item.get("name").getAsString());
                                communityListId.add(item.get("id").getAsString());
                            }

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(SelectCommunityActivity.this, "Check internet connection");
                            Log.e("error", e.toString());
                        }

                    }
                });
        addItemsOnCommunitySpinner(communityListAdapt);
        buttonGetStartedFromCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communitySelected = (String) spinnerSelectCommunity.getSelectedItem();
                if (communitySelected.equals(defaultSpinnerItem) || editTextCommunityKey.getText().toString() == "") {
                    Crouton.makeText(SelectCommunityActivity.this, "Flippy, please select a community or enter a community key", Style.ALERT)
                            .show();
                    return;
                }
                //TODO submit community key to API. On success, set communitySelected & selectedCommunityID
                if (!editTextCommunityKey.getText().toString().equalsIgnoreCase("")) {
                    String communityKey = editTextCommunityKey.getText().toString();
                    //TODO add parameters for POST
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("key", "value");

                    Ion.with(SelectCommunityActivity.this)
                            .load(communityKeyURL)
                            .setJsonObjectBody(jsonObject)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if (e != null) {
                                        ToastMessages.showToastLong(SelectCommunityActivity.this, "Check internet connection");
                                        Log.e("Error", e.toString());
                                    } else {
                                        //TODO set communitySelected & selectedCommunityID
                                    }
                                }
                            });
                }
               /* update the user with the selected community id and name*/
                try {
                    Dao<User, Integer> userDao = ((Flippy) getApplication()).userDao;
                    //this user
                    UpdateBuilder<User, Integer> updateBuilder = userDao.updateBuilder();
                    updateBuilder.where().eq("user_email", regUserEmail);
                    updateBuilder.updateColumnValue("community_id" , selectedCommunityID);
                    updateBuilder.updateColumnValue("community_name" ,communitySelected);
                    updateBuilder.update();
                } catch (java.sql.SQLException sqlE) {
                    sqlE.printStackTrace();
                }
                intent.setClass(SelectCommunityActivity.this, CommunityCenterActivity.class);
                intent.putExtra("communitySelected", communitySelected);
                intent.putExtra("selectedCommunityID", selectedCommunityID);
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
    private void showViews(){
        buttonGetStartedFromCommunity.setVisibility(View.VISIBLE);
        scrollViewLogin.setVisibility(View.VISIBLE);


    }

}
