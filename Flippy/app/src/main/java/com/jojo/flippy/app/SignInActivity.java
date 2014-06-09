package com.jojo.flippy.app;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jojo.flippy.util.Validator;


public class SignInActivity extends ActionBarActivity {
    private TextView textViewSignIn;
    private Button signGetStartedButton;
    private EditText signInEmail, signInPassword;
    private CheckBox signInCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.register_title_few_things));


        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        signGetStartedButton = (Button)findViewById(R.id.signGetStartedButton);
        signInEmail = (EditText) findViewById(R.id.editTextSigninEmail);
        signInPassword = (EditText) findViewById(R.id.editTextSigninPassword);
        signInCheckBox = (CheckBox) findViewById(R.id.checkBoxRegisterAgreement);

        signGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allFieldsValid;
                if(Validator.isValidEmailOrPhoneNumber(signInEmail.getText().toString())){
                    signInEmail.setError(null);
                    allFieldsValid = true;
                } else {
                    signInEmail.setError(getString(R.string.registration_error_email));
                    allFieldsValid = false;
                }
                if(Validator.isValidPassword(signInPassword.getText().toString())){
                    signInPassword.setError(null);
                    allFieldsValid = true;
                } else {
                    signInPassword.setError(getString(R.string.registration_error_password));
                    allFieldsValid = false;
                }
                if(signInCheckBox.isChecked()){
                    signInCheckBox.setError(null);
                    allFieldsValid = true;
                } else {
                    signInCheckBox.setError(getString(R.string.registration_error_checkbox));
                    allFieldsValid = false;
                }

                if(true){
                    Intent intent = new Intent(SignInActivity.this,SelectCommunityActivity.class);
                    startActivity(intent);
                }
            }
        });


        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
