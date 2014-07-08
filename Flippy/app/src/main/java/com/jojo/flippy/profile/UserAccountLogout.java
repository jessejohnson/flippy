package com.jojo.flippy.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jojo.flippy.app.R;

public class UserAccountLogout extends ActionBarActivity {
    private Button buttonLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_logout);


        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserAccountLogout.this);
        builder.setMessage("Are you sure you want to logout ?");
        builder.setIcon(R.drawable.ic_action_warning);
        builder.setTitle("Flippy notification");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO, delete all user settings including database information
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
