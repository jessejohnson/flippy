package com.jojo.flippy.app;

/**
 * Created by bright on 6/9/14.
 */
import java.util.Arrays;

import com.facebook.widget.LoginButton;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class SignUpOptionsActivity extends FragmentActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new FacebookSigninFragment())
                    .commit();
        }
    }


}