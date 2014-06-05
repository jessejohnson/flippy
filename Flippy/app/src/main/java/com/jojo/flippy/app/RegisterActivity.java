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
                // TODO call the validation class here and post the data to the next activity

                if (registerEmail.getText().toString().length() <= 0){
                    registerEmail.setError("email or phone number required");
                 }else{
                    registerEmail.setError(null);
                }
                if (firstName.getText().toString().length() <= 0){
                    firstName.setError("first name required");
                }else{
                    firstName.setError(null);
                }
                if (lastName.getText().toString().length() <= 0){
                    lastName.setError("last name required");
                }else{
                    lastName.setError(null);
                }
                if (password.getText().toString().length() <= 0){
                    password.setError("password required");
                }else{
                    password.setError(null);
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
