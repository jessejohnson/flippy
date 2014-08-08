package com.jojo.flippy.app;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.Validator;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.sql.SQLException;
import java.util.List;


public class SignInActivity extends ActionBarActivity {
    private TextView textViewSignIn;
    private Button signGetStartedButton;
    private EditText signInEmail, signInPassword;
    private Intent intent;
    private String regUserEmail;
    private String regUserAuthToken;
    private String regUserID;
    private String regFirstName;
    private String regLastName;
    private String regUserCommunity;
    private String avatar = "", avatar_thumb = "", date_of_birth = "", gender = "";
    private Dao<User, Integer> userDao;
    private User user;
    private Dao<Channels, Integer> channelDao;
    private List<Channels> channelList;
    private Channels channels;
    private SuperToast superToast;
    private final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ActionBar actionbar = getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setSubtitle(getString(R.string.register_title_few_things));
        }


        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        signGetStartedButton = (Button) findViewById(R.id.signGetStartedButton);
        signInEmail = (EditText) findViewById(R.id.editTextSigninEmail);
        signInPassword = (EditText) findViewById(R.id.editTextSigninPassword);
        intent = new Intent();
        superToast = new SuperToast(SignInActivity.this);

        signGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allFieldsValid;
                if (Validator.isValidEmailOrPhoneNumber(signInEmail.getText().toString())) {
                    signInEmail.setError(null);
                    allFieldsValid = true;
                } else {
                    signInEmail.setError(getString(R.string.registration_error_email));
                    allFieldsValid = false;

                }
                if (true) {
                    signInPassword.setError(null);
                    allFieldsValid = true;
                } else {
                    signInPassword.setError(getString(R.string.registration_error_password));
                    allFieldsValid = false;

                }

                if (allFieldsValid) {

                    String email = signInEmail.getText().toString();
                    String password = signInPassword.getText().toString();

                    signGetStartedButton.setEnabled(false);
                    signGetStartedButton.setText("Signing in ...");

                    JsonObject json = new JsonObject();
                    json.addProperty("email", email);
                    json.addProperty("password", password);



                    Ion.with(SignInActivity.this)
                            .load(Flippy.USERS_URL + "login/")
                            .setHeader("Authorization", "Token "+Flippy.DEFAULT_TOKEN)
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    signGetStartedButton.setEnabled(true);
                                    signGetStartedButton.setText("get started");
                                    if (e != null) {
                                        showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title));
                                        return;
                                    } else {
                                        try {
                                            if (result.has("detail")) {
                                                signInEmail.setError(result.get("detail").getAsString());
                                                showSuperToast(result.get("detail").getAsString());
                                                return;
                                            }
                                            regUserAuthToken = result.get("auth_token").getAsString();
                                            regUserID = result.get("id").getAsString();
                                            regFirstName = result.get("first_name").getAsString();
                                            regUserEmail = result.get("email").getAsString();
                                            regLastName = result.get("last_name").getAsString();
                                            if (!result.get("community").isJsonNull()) {
                                                regUserCommunity = result.get("community").getAsString();
                                            } else if (result.get("community").isJsonNull()){
                                                //if community is null, user may not have set community
                                                //allow user to choose community again
                                                Toast.makeText(SignInActivity.this, "No community", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(SignInActivity.this, SelectCommunityActivity.class));
                                            }
                                            if (!result.get("avatar").isJsonNull()) {
                                                avatar = result.get("avatar").getAsString();
                                                avatar_thumb = result.get("avatar_thumb").getAsString();
                                            }
                                            if (!result.get("gender").isJsonNull()) {
                                                gender = result.get("gender").getAsString();
                                            }
                                            if (!result.get("date_of_birth").isJsonNull()) {
                                                date_of_birth = result.get("date_of_birth").getAsString();
                                            }
                                        } catch (UnsupportedOperationException e1) {
                                            e1.printStackTrace();
                                        }
                                        //Save the information in the database
                                        try {
                                            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(SignInActivity.this,
                                                    DatabaseHelper.class);
                                            userDao = databaseHelper.getUserDao();
                                            List<User> userList = userDao.queryForAll();
                                            if (!userList.isEmpty()) {
                                                userDao.delete(userList);
                                            }
                                            user = new User(regUserID, regUserAuthToken, regUserEmail, regFirstName, regLastName, avatar, avatar_thumb, gender, date_of_birth);
                                            userDao.createOrUpdate(user);
                                        } catch (java.sql.SQLException sqlE) {
                                            sqlE.printStackTrace();
                                            showSuperToast("sorry, try again");
                                            Log.e("Error saving to db login", sqlE.toString());
                                            return;
                                        }
                                        intent.putExtra("regUserEmail", regUserEmail);
                                        intent.putExtra("regUserAuthToken", regUserAuthToken);
                                        intent.putExtra("regUserID", regUserID);
                                        //get user from db
                                        if (regUserCommunity != null) {
                                            user.community_id = regUserCommunity;
                                            try {
                                                userDao.update(user);
                                            } catch (SQLException e1) {
                                                e1.printStackTrace();
                                            }
                                            saveUserChannels();
                                        } else {
                                            signGetStartedButton.setEnabled(true);
                                            signGetStartedButton.setText("get started");
                                            intent.setClass(SignInActivity.this, SelectCommunityActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }

                            });
                }
            }
        });


        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showSuperToast(String message) {
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setBackground(SuperToast.Background.PURPLE);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setText(message);
        superToast.show();
    }

    private void saveUserChannels() {
        String url = Flippy.USERS_URL + regUserID + "/subscriptions/";
        if (regUserID == null || regUserID == "") {
            showSuperToast("Unfortunately something went wrong, try again later");
            return;
        }
        Ion.with(SignInActivity.this)
                .load(url)
                .setHeader("Authorization", "Token "+Flippy.DEFAULT_TOKEN)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        signGetStartedButton.setEnabled(true);
                        signGetStartedButton.setText("get started");
                        try {
                            if (result != null) {
                                JsonArray channelArray = result.getAsJsonArray("results");
                                for (int i = 0; i < channelArray.size(); i++) {
                                    JsonObject item = channelArray.get(i).getAsJsonObject();
                                    String channel_id = item.get("id").getAsString();
                                    try {
                                        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(SignInActivity.this,
                                                DatabaseHelper.class);
                                        channelDao = databaseHelper.getChannelDao();
                                        channels = new Channels(channel_id);
                                        channelDao.createOrUpdate(channels);
                                    } catch (java.sql.SQLException sqlE) {
                                        sqlE.printStackTrace();
                                        Log.e("Sign in activity", "Error getting all user CHANNELS_URL");
                                    }
                                }
                                intent.setClass(SignInActivity.this, CommunityCenterActivity.class);
                                startActivity(intent);
                            } else if (e != null) {
                                showSuperToast("Internet connection error occurred");
                            } else {
                                Log.e(TAG, "something else went wrong");
                                return;
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, "Error loading channels " + exception.toString());
                        }
                    }
                });

    }
}
