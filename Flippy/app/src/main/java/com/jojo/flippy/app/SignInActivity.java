package com.jojo.flippy.app;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.jojo.flippy.util.Validator;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SignInActivity extends ActionBarActivity {
    private TextView textViewSignIn;
    private Button signGetStartedButton;
    private EditText signInEmail, signInPassword;
    private CheckBox signInCheckBox;
    private Intent intent;
    private String regUserEmail;
    private String regUserAuthToken;
    private String regUserID;
    private String regFirstName;
    private String regLastName;
    private String avatar, avatar_thumb, date_of_birth, gender;
    private Dao<User, Integer> userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.register_title_few_things));


        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        signGetStartedButton = (Button) findViewById(R.id.signGetStartedButton);
        signInEmail = (EditText) findViewById(R.id.editTextSigninEmail);
        signInPassword = (EditText) findViewById(R.id.editTextSigninPassword);
        intent = new Intent();

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
                if (Validator.isValidPassword(signInPassword.getText().toString())) {
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
                            .load(Flippy.signInURL)
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {

                                    signGetStartedButton.setEnabled(true);
                                    signGetStartedButton.setText(getText(R.string.start));
                                    if (e != null) {
                                        ToastMessages.showToastLong(SignInActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                        Log.e("Error", e.toString());
                                        return;
                                    } else {
                                        //If there is no error returned
                                        try {
                                            if (result.has("detail")) {
                                                signInEmail.setError(result.get("detail").getAsString());
                                                Crouton.makeText(SignInActivity.this, result.get("detail").getAsString(), Style.ALERT)
                                                        .show();
                                                return;
                                            }
                                            regUserAuthToken = result.get("auth_token").getAsString();
                                            regUserID = result.get("id").getAsString();
                                            regFirstName = result.get("first_name").getAsString();
                                            regUserEmail = result.get("email").getAsString();
                                            regLastName = result.get("last_name").getAsString();
                                            if (result.get("avatar").isJsonNull()) {
                                                avatar = "";
                                            } else {
                                                avatar = result.get("avatar").getAsString();
                                            }
                                            avatar_thumb = result.get("avatar_thumb").getAsString();
                                            gender = result.get("gender").getAsString();
                                            if (result.get("date_of_birth").isJsonNull()) {
                                                date_of_birth = "";
                                            } else {
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
                                            if(!userList.isEmpty()){
                                               userDao.delete(userList);
                                            }
                                            User user = new User(regUserID, regUserAuthToken, regUserEmail, regFirstName, regLastName, avatar, avatar_thumb, gender, date_of_birth);
                                            userDao.createOrUpdate(user);
                                           } catch (java.sql.SQLException sqlE) {
                                            sqlE.printStackTrace();
                                            Crouton.makeText(SignInActivity.this, "Sorry, Try again later", Style.ALERT)
                                                    .show();
                                            return;
                                        }
                                        intent.putExtra("regUserEmail", regUserEmail);
                                        intent.putExtra("regUserAuthToken", regUserAuthToken);
                                        intent.putExtra("regUserID", regUserID);
                                        intent.setClass(SignInActivity.this, SelectCommunityActivity.class);
                                        startActivity(intent);
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
}
