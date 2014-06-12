package com.jojo.flippy.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.jojo.flippy.adapter.CustomDrawer;
import com.jojo.flippy.adapter.DrawerItem;
import com.jojo.flippy.app.R;
import com.jojo.flippy.profile.AccountProfileActivity;
import com.jojo.flippy.util.ToastMessages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bright on 6/9/14.
 */
public class CommunityCenterActivity extends Activity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ShareActionProvider shareActionProvider;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    CustomDrawer adapter;

    List<DrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer_main);

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
                invalidateOptionsMenu(); // creates call to
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
                break;
            case 2:
                fragment = new FragmentChannel();
                break;
            case 3:
                fragment = new FragmentCommunities();
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
                return  true;
            default:
                return super.onOptionsItemSelected(item);
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
        // Return true to display menu
        return true;

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
    public void sendFeedback(){
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
                // Do something with the selection
                //get the selected option and pass it on to the next activity
                ToastMessages.showToastLong(CommunityCenterActivity.this,channelList[item].toString());
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
