package com.jojo.flippy.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.ToastMessages;
import com.jojo.flippy.util.Validator;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URI;


public class RegisterActivity extends Activity {
    private  EditText editTextRegisterEmail, editTextFirstName, editTextLastName, editTextPassword;
    private TextView textViewSignIn;
    private CheckBox checkBoxTerms;
    private String regURL = "http://test-flippy-rest-api.herokuapp.com/api/v1.0/users/signup/";
    private ProgressDialog registerProgress;

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

                    String userEmail =  editTextRegisterEmail.getText().toString().trim();
                    String first_name =  editTextFirstName.getText().toString().trim();
                    String last_name =  editTextLastName.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    //setting the user parameters
                    RequestParams params = new RequestParams();
                    params.put("email",userEmail);
                    params.put("first_name", first_name);
                    params.put("last_name",last_name);
                    params.put("password",password);

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(regURL, params,new ResponseHandlerInterface() {
                        @Override
                        public void sendResponseMessage(HttpResponse response) throws IOException {
                            Log.e("Response Sender",response.toString());
                        }

                        @Override
                        public void sendStartMessage() {
                            Log.e("Send started","Just started");
                            registerProgress = ProgressDialog.show(RegisterActivity.this, "", "Loading...");
                         }

                        @Override
                        public void sendFinishMessage() {

                        }

                        @Override
                        public void sendProgressMessage(int bytesWritten, int bytesTotal) {
                            ToastMessages.showToastLong(RegisterActivity.this, "Processing");
                        }

                        @Override
                        public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
                            //Create intent to start next activity
                            Log.e("Response",responseBody.toString());
                            Toast.makeText(RegisterActivity.this,
                                    "Good job!",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, CommunityCenterActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            ToastMessages.showToastLong(RegisterActivity.this,"Failed");
                            Log.e("Error",statusCode + "");                        }

                        @Override
                        public void sendRetryMessage() {

                        }

                        @Override
                        public URI getRequestURI() {
                            return null;
                        }

                        @Override
                        public Header[] getRequestHeaders() {
                            return new Header[0];
                        }

                        @Override
                        public void setRequestURI(URI requestURI) {

                        }

                        @Override
                        public void setRequestHeaders(Header[] requestHeaders) {

                        }

                        @Override
                        public void setUseSynchronousMode(boolean useSynchronousMode) {

                        }
                    });

                }
            }
        });


    }
}
