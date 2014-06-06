package com.jojo.flippy.app;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jojo.flippy.util.Validator;


public class SignInActivity extends ActionBarActivity {
    private TextView textViewSignIn;
    private Button signGetStartedButton;
    private EditText signInEmail, signInPassword;

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

        signGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allFieldsValid = false;
                if(!Validator.validateEmailOrPhoneNumber(signInEmail.getText().toString())){
                    signInEmail.setError(getString(R.string.registration_error_email));
                } else {
                    signInEmail.setError(null);
                    allFieldsValid = true;
                }
                if(signInPassword.getText().toString().length() == 0){
                    signInPassword.setError(getString(R.string.registration_error_password));
                } else {
                    signInPassword.setError(null);
                    allFieldsValid = true;
                }
                if(allFieldsValid){
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
<<<<<<< HEAD

=======
>>>>>>> 313861e0c48802ccc2a65567976d398619cb9292
}
