package com.jojo.flippy.core;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.adapter.CustomDrawer;
import com.jojo.flippy.adapter.DrawerItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.profile.AccountProfileActivity;
import com.jojo.flippy.util.SendParseNotification;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommunityCenterActivity extends ActionBarActivity {
    public static String regUserEmail = "";
    public static String userFirstName = "";
    public static String userLastName = "";
    public static String userAvatarThumbURL = "";
    public static String userAvatarURL = "";
    public static String userCommunityId = "";
    public static String userCommunityName = "";
    public static String regUserID = "";
    public static String userDateOfBirth = "";
    public static String userGender = "";
    public static String userAuthToken;
    private CustomDrawer adapter;
    private List<DrawerItem> dataList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private User currentUser;
    private Dao<User, Integer> userDao;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    private static String TAG = "CommunityCenterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer_main);
        //get the current user from the database
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(CommunityCenterActivity.this,
                    DatabaseHelper.class);
            userDao = databaseHelper.getUserDao();
            List<User> userList = userDao.queryForAll();
            if (userList.isEmpty()) {
                currentUser = null;
            } else {
                currentUser = userList.get(0);
                regUserEmail = currentUser.user_email;
                userFirstName = currentUser.first_name;
                userLastName = currentUser.last_name;
                userAvatarThumbURL = currentUser.avatar_thumb;
                userAvatarURL = currentUser.avatar;
                userCommunityId = currentUser.community_id;
                userCommunityName = currentUser.community_name;
                regUserID = currentUser.user_id;
                userDateOfBirth = currentUser.date_of_birth;
                userGender = currentUser.gender;
                userAuthToken = currentUser.user_auth;
            }


        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e(TAG, "Unfortunately a system error occurred");
        }
        // Initializing
        dataList = new ArrayList<DrawerItem>();
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);


        // Add Drawer Item to dataList
        dataList.add(new DrawerItem(getString(R.string.drawer_item_account), R.drawable.ic_action_group));
        dataList.add(new DrawerItem(getString(R.string.drawer_item_notice), R.drawable.ic_notices));
        dataList.add(new DrawerItem(getString(R.string.drawer_item_favourite), R.drawable.ic_action_important));
        dataList.add(new DrawerItem(getString(R.string.drawer_item_channel), R.drawable.ic_channel));
        dataList.add(new DrawerItem(getString(R.string.drawer_item_community), R.drawable.ic_action_group));
        dataList.add(new DrawerItem(getString(R.string.drawer_item_settings), R.drawable.ic_action_settings));
        adapter = new CustomDrawer(this, R.layout.custom_drawer_item,
                dataList);
        mDrawerList.setAdapter(adapter);

        //setting the onclick listeners of the drawer items using the sub class
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        getActionBar().setDisplayHomeAsUpEnabled(true);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            SelectItem(1);
        }
    }

    public void SelectItem(int position) {

        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                fragment = new FragmentNotice();
                //Starting a new activity to handle user account detail
                Intent intent = new Intent(CommunityCenterActivity.this, AccountProfileActivity.class);
                startActivity(intent);
                break;
            case 1:
                fragment = new FragmentNotice();
                break;
            case 2:
                fragment = new FragmentFavourite();
                break;
            case 3:
                fragment = new FragmentChannel();
                break;
            case 4:
                fragment = new FragmentCommunities();
                break;
            case 5:
                fragment = new FragmentSettings();
                break;
            default:
                break;
        }

        fragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        frgManager.beginTransaction().replace(R.id.content_frame, fragment)
                .setTransition(R.anim.fade_in)
                .addToBackStack("")
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(dataList.get(position).getItemName());
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_share:
                shareFlippy();
                return true;
            case R.id.action_feedback:
                sendFeedback();
                return true;
            case R.id.action_add:
                Intent intentChooseChannel = new Intent(CommunityCenterActivity.this, SelectChannelActivity.class);
                intentChooseChannel.putExtra("userId", regUserID);
                startActivity(intentChooseChannel);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(CommunityCenterActivity.this, HelpActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_refresh:
                finish();
                startActivity(getIntent());
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //Setting the selection of the context menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_detail_notice:
                //call the detail view activity passing all the intent to display
                return true;
            case R.id.action_reminder_notice:
                //call the alarm reminder of the system and set the alert
            case R.id.action_share_notice:
                //show the user his available options to share and display success toast
            case R.id.action_favourite_notice:
                //call the user favourite function
            case R.id.action_remove_notice:
                //remove the notice from the USERS_URL board
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.community_center_menu, menu);
        return true;

    }

    //Inflating the notice context menu
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.notice_context_menu, menu);


    }

    private void shareFlippy() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, userFirstName + " is inviting you to join Flippy. \n Download at ...");
        sendIntent.setType("text/plain");
        sendIntent.createChooser(sendIntent, getResources().getText(R.string.app_name));
        startActivity(sendIntent);
    }

    //The send feedback function
    public void sendFeedback() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.flippy_email)});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.flippy_email_subject));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.flippy_email_body));
        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.flippy_email_subject)));
    }

    @Override
    public void onBackPressed() {
       /* super.onBackPressed();*/
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.exit_title)
                .setMessage(R.string.exit_message)
                .setPositiveButton(R.string.exit_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton(R.string.exit_negative, null).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /* this class set the onclick listener for the various drawer items*/
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SelectItem(position);
        }
    }
}
