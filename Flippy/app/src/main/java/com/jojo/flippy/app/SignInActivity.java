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

import com.google.gson.JsonObject;
import com.jojo.flippy.util.Validator;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class SignInActivity extends ActionBarActivity {
    private TextView textViewSignIn;
    private Button signGetStartedButton;
    private EditText signInEmail, signInPassword;
    private CheckBox signInCheckBox;
    private Intent intent;
    private ProgressDialog signInDialog;
    private String signInURL = "http://test-flippy-rest-api.herokuapp.com:80/api/v1.0/users/login/";
    private String regUserEmail;
    private String regUserAuthToken;
    private String regUserID;

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
        signInCheckBox = (CheckBox) findViewById(R.id.checkBoxRegisterAgreement);

        intent = new Intent();
        signInDialog = new ProgressDialog(SignInActivity.this);

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
                if (signInCheckBox.isChecked()) {
                    signInCheckBox.setError(null);
                    allFieldsValid = true;
                } else {
                    signInCheckBox.setError(getString(R.string.registration_error_checkbox));
                    allFieldsValid = false;
                }

                if (true) {

                    String email = signInEmail.getText().toString();
                    String password = signInPassword.getText().toString();
                    //setting the user parameters
                    signInDialog.setMessage("Signing in... " + email);
                    //show dialog
                    signInDialog.show();

                    JsonObject json = new JsonObject();
                    json.addProperty("email", email);
                    json.addProperty("password", password);

                    Ion.with(SignInActivity.this)
                            .load(signInURL)
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    //cancel the dialog
                                    signInDialog.cancel();
                                    if (e != null) {
                                        Log.e("Error", e.toString());
                                    } else {
                                        Log.e("user exist", result.toString());
                                        if (result.has("detail")) {
                                            signInEmail.setError("email or password incorrect");
                                            Crouton.makeText(SignInActivity.this, "email or password incorrect", Style.ALERT)
                                                    .show();
                                            return;
                                        }
                                        Log.e("result", result.get("id").getAsString());
                                        Log.e("result", result.get("auth_token").getAsString());
                                        Log.e("result", result.get("email").getAsString());
                                        regUserEmail = result.get("email").getAsString();
                                        regUserAuthToken = result.get("auth_token").getAsString();
                                        regUserID = result.get("id").getAsString();
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
