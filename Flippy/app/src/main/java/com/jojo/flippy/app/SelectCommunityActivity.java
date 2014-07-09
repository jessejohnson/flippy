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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.jojo.flippy.adapter.Channel;
import com.jojo.flippy.adapter.ChannelAdapter;
import com.jojo.flippy.adapter.Community;
import com.jojo.flippy.adapter.CommunityAdapter;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.DatabaseHelper;
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

import java.net.URI;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SelectCommunityActivity extends Activity {
    private Button buttonGetStartedFromCommunity;
    //TODO get communityKeyURL
    private String communityKeyURL = "";
    private EditText editTextCommunityKey;
    private Intent intent;
    private String regUserEmail;
    private ProgressBar progressBarLoadCommunity;
    private Dao<User, Integer> userDao;


    ListView listViewCommunities;
    List<Community> rowItems;
    private CommunityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_community);


        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.last_step));
        actionbar.setTitle("Select a community");

        progressBarLoadCommunity = (ProgressBar) findViewById(R.id.progressBarLoadCommunity);
        buttonGetStartedFromCommunity = (Button) findViewById(R.id.buttonGetStartedCommunity);

        intent = getIntent();
        regUserEmail = intent.getStringExtra("regUserEmail");

        editTextCommunityKey = (EditText) findViewById(R.id.editTextCommunityKey);


        rowItems = new ArrayList<Community>();
        listViewCommunities = (ListView) findViewById(R.id.listViewCommunities);
        progressBarLoadCommunity = (ProgressBar) findViewById(R.id.progressBarLoadCommunity);
        adapter = new CommunityAdapter(SelectCommunityActivity.this,
                R.layout.select_community_listview, rowItems);
        listViewCommunities.setAdapter(adapter);


        Ion.with(SelectCommunityActivity.this)
                .load(Flippy.communitiesURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadCommunity.setVisibility(View.GONE);
                        if (result != null) {
                            JsonArray communityArray = result.getAsJsonArray("results");
                            for (int i = 0; i < communityArray.size(); i++) {
                                JsonObject item = communityArray.get(i).getAsJsonObject();
                                String communityName = item.get("name").getAsString();
                                String communityId = item.get("id").getAsString();
                                String communityBio = item.get("bio").getAsString();
                                String communityImage = item.get("image_url").getAsString();
                                Community communityItem = new Community(URI.create(communityImage), communityId, communityName,communityBio);
                                rowItems.add(communityItem);
                            }
                            updateAdapter();
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(SelectCommunityActivity.this, "Check internet connection");
                            Log.e("error", e.toString());
                        }

                    }
                });
     listViewCommunities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             TextView textViewCommunityId = (TextView)view.findViewById(R.id.textViewCommunityId);
             TextView textViewCommunityName = (TextView)view.findViewById(R.id.textViewCommunityName);
             final String communityId = textViewCommunityId.getText().toString();
             final String communityName = textViewCommunityName.getText().toString();
             JsonObject jsonObject = new JsonObject();
             jsonObject.addProperty("community_id", communityId);
             Ion.with(SelectCommunityActivity.this)
                     .load(Flippy.userCommunityURL + intent.getStringExtra("regUserID") + "/community/")
                     .setHeader("Authorization", "Token " + intent.getStringExtra("regUserAuthToken"))
                     .setJsonObjectBody(jsonObject)
                     .asJsonObject()
                     .setCallback(new FutureCallback<JsonObject>() {
                         @Override
                         public void onCompleted(Exception e, JsonObject result) {
                             if (result != null) {
                                 try {
                                     DatabaseHelper databaseHelper = OpenHelperManager.getHelper(SelectCommunityActivity.this,
                                             DatabaseHelper.class);
                                     userDao = databaseHelper.getUserDao();
                                     UpdateBuilder<User, Integer> updateBuilder = userDao.updateBuilder();
                                     updateBuilder.where().eq("user_email", regUserEmail);
                                     updateBuilder.updateColumnValue("community_id", communityId);
                                     updateBuilder.updateColumnValue("community_name", communityName);
                                     updateBuilder.update();
                                     intent.setClass(SelectCommunityActivity.this, CommunityCenterActivity.class);
                                     intent.putExtra("communitySelected", communityName);
                                     intent.putExtra("selectedCommunityID", communityId);
                                     startActivity(intent);
                                 } catch (java.sql.SQLException sqlE) {
                                     sqlE.printStackTrace();
                                     Log.e("Community error", sqlE.toString());
                                     Crouton.makeText(SelectCommunityActivity.this, "sorry user registration  failed", Style.ALERT)
                                             .show();
                                     return;
                                 }
                             }
                             if (e != null) {
                                 ToastMessages.showToastLong(SelectCommunityActivity.this, "Check internet connection");
                                 return;

                             }

                         }
                     });

         }
     });
        buttonGetStartedFromCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String communityKey = editTextCommunityKey.getText().toString();
                if(communityKey==""){
                    editTextCommunityKey.setError("Community key is required");
                    return;
                }
            }
        });
    }

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
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


}
