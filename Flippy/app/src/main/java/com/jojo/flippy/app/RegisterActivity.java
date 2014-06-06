package com.jojo.flippy.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jojo.flippy.util.ToastMessages;
import com.jojo.flippy.util.Validator;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class RegisterActivity extends Activity {
    private  EditText registerEmail,firstName,lastName,password;
    private TextView textViewSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setSubtitle(getString(R.string.register_title_few_things));


        Crouton.showText( this,getString(R.string.app_name),Style.CONFIRM);

        registerEmail = (EditText)findViewById(R.id.registerEmailEditText);
        firstName = (EditText)findViewById(R.id.editTextRegisterFirstName);
        lastName = (EditText)findViewById(R.id.editTextRegisterLastName);
        password =(EditText)findViewById(R.id.editTextRegisterPassword);
        textViewSignIn = (TextView)findViewById(R.id.textViewSignIn);


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
                boolean allFieldsValid = false;

                if (!Validator.validateEmail(registerEmail.getText().toString())){
                    registerEmail.setError(getString(R.string.registration_error_email));
                 }else{
                    registerEmail.setError(null);
                    allFieldsValid = true;
                }
                if (!Validator.validateNameString(firstName.getText().toString())){
                    firstName.setError(getString(R.string.registration_error_firstname));
                }else{
                    firstName.setError(null);
                    allFieldsValid = true;
                }
                if (!Validator.validateNameString(lastName.getText().toString())){
                    lastName.setError(getString(R.string.registration_error_lastname));
                }else{
                    lastName.setError(null);
                    allFieldsValid = true;
                }
                if (!Validator.validatePassword(password.getText().toString())){
                    password.setError(getString(R.string.registration_error_password));
                }else{
                    password.setError(null);
                    allFieldsValid = true;
                }

                if(allFieldsValid){
                    //Create intent to start next activity
                }
            }
        });


    }
<<<<<<< HEAD


=======
>>>>>>> 313861e0c48802ccc2a65567976d398619cb9292
}
