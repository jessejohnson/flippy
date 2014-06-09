package com.jojo.flippy.app;

/**
 * Created by bright on 6/9/14.
 */

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;

public class SignUpOptionsActivity extends FragmentActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_signup_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new FacebookSigninFragment())
                    .commit();
        }
    }
}