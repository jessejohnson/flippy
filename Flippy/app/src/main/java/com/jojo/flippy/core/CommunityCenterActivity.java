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
import android.widget.ShareActionProvider;

import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.adapter.CustomDrawer;
import com.jojo.flippy.adapter.DrawerItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.profile.AccountProfileActivity;
import com.jojo.flippy.profile.ManageChannelActivity;
import com.jojo.flippy.util.Flippy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/9/14.
 */
public class CommunityCenterActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ShareActionProvider shareActionProvider;
    private boolean notice = true;
    private boolean community = false;
    private boolean channel = false;
    private Intent intent;
    public static String regUserEmail="";
    public static  String userFirstName ="";
    public static  String userLastName ="";
    public static  String userAvatarThumbURL ="";
    public static  String userAvatarURL ="";
    public static  String userCommunityId="";
    public static  String userCommunityName="";
    public static  String regUserID ;
    private User currentUser;


    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    CustomDrawer adapter;

    List<DrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer_main);

        intent = getIntent();


        //get the current user from the database
        try {
            Dao<User, Integer> userDao = ((Flippy) getApplication()).userDao;
            List<User> userList = userDao.queryForAll();
            if (userList.isEmpty()) {
                currentUser = null;
            } else {
                currentUser = userList.get(0);
                regUserEmail = currentUser.user_email;
                userFirstName = currentUser.first_name;
                userLastName = currentUser.last_name;
                userAvatarThumbURL =currentUser.avatar_thumb;
                userAvatarURL = currentUser.avatar;
                userCommunityId =currentUser.community_id;
                userCommunityName = currentUser.community_name;
                regUserID = currentUser.id;
            }

        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
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
                // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
                //onPrepareOptionsMenu();
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
                notice = true;
                community = false;
                channel = false;
                break;
            case 2:
                fragment = new FragmentChannel();
                notice = false;
                community = false;
                channel = true;
                break;
            case 3:
                fragment = new FragmentCommunities();
                notice = false;
                community = true;
                channel = false;
                break;
            case 4:
                fragment = new FragmentSettings();
                break;
            default:
                break;
        }

        fragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        frgManager.beginTransaction().replace(R.id.content_frame, fragment)
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
                // write code to execute when clicked on this option
                shareFlippy();
                return true;
            case R.id.action_feedback:
                sendFeedback();
                return true;
            case R.id.action_add:
                channelListDialog();
                return true;
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
                //remove the notice from the users board
            case R.id.action_channel_manage_channel:
                Intent intent1 = new Intent();
                intent1.setClass(CommunityCenterActivity.this, ManageChannelActivity.class);
                startActivity(intent1);
                return  true;
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


    /* this class set the onclick listener for the various drawer items*/
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SelectItem(position);


        }
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
        if (community) {
            inflater.inflate(R.menu.community_context_menu, menu);
        } else if (channel) {
            inflater.inflate(R.menu.channel_context_menu, menu);
        } else {
            inflater.inflate(R.menu.notice_context_menu, menu);
        }

    }

    private void shareFlippy() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name));
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

    //Alert dialog showing a list of channel user belongs to
    private void channelListDialog() {
        //TODO this should line should return a list of user channels subscribed to
        final CharSequence[] channelList = {"SRC channel", "Class of 2015, CS", "AAESS Group"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_channel_list_dialog_title);
        builder.setItems(channelList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //get the selected option and pass it on to the next activity
                String channelToCreateNotice = channelList[item].toString();
                Intent intent = new Intent(CommunityCenterActivity.this, CreateNoticeActivity.class);
                intent.putExtra("channelToCreateNotice", channelToCreateNotice);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
}
