package com.jojo.flippy.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.Validator;


public class RegisterActivity extends Activity {
    private  EditText editTextRegisterEmail, editTextFirstName, editTextLastName, editTextPassword;
    private TextView textViewSignIn;
    private CheckBox checkBoxTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.register_title_few_things));

        editTextRegisterEmail = (EditText)findViewById(R.id.registerEmailEditText);
        editTextFirstName = (EditText)findViewById(R.id.editTextRegisterFirstName);
        editTextLastName = (EditText)findViewById(R.id.editTextRegisterLastName);
        editTextPassword =(EditText)findViewById(R.id.editTextRegisterPassword);
        textViewSignIn = (TextView)findViewById(R.id.textViewSignIn);
        checkBoxTerms = (CheckBox) findViewById(R.id.checkBoxRegisterAgreement);


        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,SignInActivity.class);
                startActivity(intent);

            }
        });


        Button registrationNext = (Button)findViewById(R.id.registerNextButton);
        registrationNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allFieldsValid;

                if (Validator.isValidEmailOrPhoneNumber(editTextRegisterEmail.getText().toString())){
                    editTextRegisterEmail.setError(null);
                    allFieldsValid = true;
                }else{
                    editTextRegisterEmail.setError(getString(R.string.registration_error_email));
                    allFieldsValid = false;
                }
                if (Validator.isValidNameString(editTextFirstName.getText().toString())){
                    editTextFirstName.setError(null);
                    allFieldsValid = true;
                }else{
                    editTextFirstName.setError(getString(R.string.registration_error_firstname));
                    allFieldsValid = false;
                }
                if (Validator.isValidNameString(editTextLastName.getText().toString())){
                    editTextLastName.setError(null);
                    allFieldsValid = true;
                }else{
                    editTextLastName.setError(getString(R.string.registration_error_lastname));
                    allFieldsValid = false;
                }
                if (Validator.isValidPassword(editTextPassword.getText().toString())){
                    editTextPassword.setError(null);
                    allFieldsValid = true;
                }else{
                    editTextPassword.setError(getString(R.string.registration_error_password));
                    allFieldsValid = false;
                }
                if (checkBoxTerms.isChecked()){
                    checkBoxTerms.setError(null);
                    allFieldsValid = true;
                }else {
                    checkBoxTerms.setError(getString(R.string.registration_error_checkbox));
                    allFieldsValid = false;
                }

                if(allFieldsValid){
                    //Create intent to start next activity
                    Toast.makeText(RegisterActivity.this,
                            "Good job!",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, CommunityCenterActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
