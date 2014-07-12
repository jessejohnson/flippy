package com.jojo.flippy.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.persistence.User;

import java.util.List;

public class UserAccountLogout extends ActionBarActivity {
    private Button buttonLogOut;
    private Dao<Post, Integer> postDao;
    private Dao<User, Integer> userDao;

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
        builder.setPositiveButton("Yes, continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAllPost();
                deleteUser();
                dialog.dismiss();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        builder.setNegativeButton("No, cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void deleteAllPost(){
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(),
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            List<Post> postList = postDao.queryForAll();
            if (!postList.isEmpty()) {
               postDao.delete(postList);
            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();

        }
    }
    private void deleteUser(){
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(),
                    DatabaseHelper.class);
            userDao = databaseHelper.getUserDao();
            List<User> userList = userDao.queryForAll();
            if (!userList.isEmpty()) {
                userDao.delete(userList);
            }
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();

        }
    }
}
