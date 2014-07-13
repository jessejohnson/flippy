package com.jojo.flippy.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
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

import java.util.List;


public class RegisterActivity extends Activity {
    private EditText editTextRegisterEmail, editTextFirstName, editTextLastName, editTextPassword;
    private TextView textViewSignIn;
    private CheckBox checkBoxTerms;
    private String regUserEmail, regUserAuthToken, regUserID, regFirstName, regLastName;
    private Intent intent;
    private Dao<User, Integer> userDao;
    private SuperToast superToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionbar = getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setSubtitle(getString(R.string.register_title_few_things));
        }


        intent = new Intent();
        superToast = new SuperToast(RegisterActivity.this);

        editTextRegisterEmail = (EditText) findViewById(R.id.registerEmailEditText);
        editTextFirstName = (EditText) findViewById(R.id.editTextRegisterFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextRegisterLastName);
        editTextPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        checkBoxTerms = (CheckBox) findViewById(R.id.checkBoxRegisterAgreement);

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(intent);

            }
        });


        final Button registrationNext = (Button) findViewById(R.id.registerNextButton);
        registrationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allFieldsValid;

                if (Validator.isValidEmailOrPhoneNumber(editTextRegisterEmail.getText().toString())) {
                    editTextRegisterEmail.setError(null);
                    allFieldsValid = true;
                } else {
                    editTextRegisterEmail.setError(getString(R.string.registration_error_email));
                    allFieldsValid = false;
                }
                if (Validator.isValidNameString(editTextFirstName.getText().toString())) {
                    editTextFirstName.setError(null);
                    allFieldsValid = true;
                } else {
                    editTextFirstName.setError(getString(R.string.registration_error_first_name));
                    allFieldsValid = false;
                }
                if (Validator.isValidNameString(editTextLastName.getText().toString())) {
                    editTextLastName.setError(null);
                    allFieldsValid = true;
                } else {
                    editTextLastName.setError(getString(R.string.registration_error_last_name));
                    allFieldsValid = false;
                }
                if (Validator.isValidPassword(editTextPassword.getText().toString())) {
                    editTextPassword.setError(null);
                    allFieldsValid = true;
                } else {
                    editTextPassword.setError(getString(R.string.registration_error_password));
                    allFieldsValid = false;
                }
                if (checkBoxTerms.isChecked()) {
                    checkBoxTerms.setError(null);
                    allFieldsValid = true;
                } else {
                    checkBoxTerms.setError(getString(R.string.registration_error_checkbox));
                    allFieldsValid = false;
                }

                if (allFieldsValid) {
                    String userEmail = editTextRegisterEmail.getText().toString().trim();
                    String first_name = editTextFirstName.getText().toString().trim();
                    String last_name = editTextLastName.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    registrationNext.setEnabled(false);
                    registrationNext.setText("Registering ...");
                    JsonObject json = new JsonObject();
                    json.addProperty("email", userEmail);
                    json.addProperty("first_name", first_name);
                    json.addProperty("last_name", last_name);
                    json.addProperty("password", password);

                    Ion.with(RegisterActivity.this)
                            .load(Flippy.users + "signup/")
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    registrationNext.setEnabled(true);
                                    registrationNext.setText(getText(R.string.register_next));
                                    if (e != null) {
                                        ToastMessages.showToastLong(RegisterActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                        Log.e("Error", e.toString());
                                    } else {
                                        if (result.has("detail")) {
                                            editTextRegisterEmail.setError("email already in use");
                                            superToast.setAnimations(SuperToast.Animations.FLYIN);
                                            superToast.setDuration(SuperToast.Duration.LONG);
                                            superToast.setBackground(SuperToast.Background.RED);
                                            superToast.setTextSize(SuperToast.TextSize.MEDIUM);
                                            superToast.setText("email already in use");
                                            superToast.show();
                                            return;
                                        }
                                        regUserAuthToken = result.get("auth_token").getAsString();
                                        regUserID = result.get("id").getAsString();
                                        regFirstName = result.get("first_name").getAsString();
                                        regUserEmail = result.get("email").getAsString();
                                        regLastName = result.get("last_name").getAsString();
                                        try {
                                            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RegisterActivity.this,
                                                    DatabaseHelper.class);
                                            userDao = databaseHelper.getUserDao();
                                            List<User> userList = userDao.queryForAll();
                                            if (!userList.isEmpty()) {
                                                userDao.delete(userList);
                                            }
                                            User user = new User(regUserID, regUserAuthToken, regUserEmail, regFirstName, regLastName);
                                            userDao.create(user);
                                        } catch (java.sql.SQLException sqlE) {
                                            sqlE.printStackTrace();
                                            superToast.setAnimations(SuperToast.Animations.FLYIN);
                                            superToast.setDuration(SuperToast.Duration.LONG);
                                            superToast.setBackground(SuperToast.Background.RED);
                                            superToast.setIcon(R.drawable.icon_dark_info, SuperToast.IconPosition.LEFT);
                                            superToast.setTextSize(SuperToast.TextSize.MEDIUM);
                                            superToast.setText("sorry, user creation failed");
                                            superToast.show();
                                            return;
                                        }

                                        intent.putExtra("regUserEmail", regUserEmail);
                                        intent.putExtra("regUserAuthToken", regUserAuthToken);
                                        intent.putExtra("regUserID", regUserID);
                                        intent.setClass(RegisterActivity.this, SelectCommunityActivity.class);
                                        startActivity(intent);

                                    }
                                }

                            });

                } else {
                    return;
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

}
