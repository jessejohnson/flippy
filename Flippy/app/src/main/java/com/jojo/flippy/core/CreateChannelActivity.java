package com.jojo.flippy.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.sql.SQLException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CreateChannelActivity extends ActionBarActivity {
    private static final int SELECT_PICTURE = 1;
    private ImageView imageViewCreateChannel;
    private EditText editTextNewChannelName;
    private EditText editTextNewChannelOneLiner;
    private CheckBox checkBoxChannelIsPublic;
    private Button buttonCreateNewChannel;
    private String selectedImagePath;
    private int column_index;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private Dao<Channels, Integer> channelDao;
    private Channels channels;
    private final String TAG ="CreateChannelActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);


        imageViewCreateChannel = (ImageView) findViewById(R.id.imageViewCreateChannel);
        checkBoxChannelIsPublic = (CheckBox) findViewById(R.id.checkBoxChannelIsPublic);
        checkBoxChannelIsPublic.setChecked(true);

        editTextNewChannelName = (EditText) findViewById(R.id.editTextNewChannelName);
        editTextNewChannelOneLiner = (EditText) findViewById(R.id.editTextNewChannelOneLiner);
        buttonCreateNewChannel = (Button) findViewById(R.id.buttonCreateNewChannel);

        progressBar = (ProgressBar) findViewById(R.id.progressBarUpload);
        progressDialog = new ProgressDialog(CreateChannelActivity.this);


        imageViewCreateChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

        buttonCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChannel();
            }
        });
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(CreateChannelActivity.this,
                    DatabaseHelper.class);
            channelDao = databaseHelper.getChannelDao();
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Log.e("Fragment", "Error getting all user CHANNELS_URL");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath == null) {
                    ToastMessages.showToastLong(CreateChannelActivity.this, "Choose an image with local source");
                    return;
                }
                selectedImagePath.getBytes();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
                imageViewCreateChannel.setImageBitmap(bm);

            } else {
                ToastMessages.showToastLong(CreateChannelActivity.this, "No image selected");
            }

        } else {
            ToastMessages.showToastLong(CreateChannelActivity.this, "No image selected");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create_channel) {
            createChannel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void createChannel() {
        final String channelName = editTextNewChannelName.getText().toString().trim();
        String channelBio = editTextNewChannelOneLiner.getText().toString().trim();
        String is_public = "true";
        if (!checkBoxChannelIsPublic.isChecked()) {
            is_public = "false";
        }
        if (channelName.isEmpty()) {
            editTextNewChannelName.setError("Channel name is required");
            return;
        }
        if (channelBio.isEmpty()) {
            editTextNewChannelOneLiner.setError("Provide a brief description here");
            return;
        }
        if (selectedImagePath == null) {
            ToastMessages.showToastLong(CreateChannelActivity.this, "Image is required");
            return;
        }
        buttonCreateNewChannel.setEnabled(false);
        Ion.with(CreateChannelActivity.this, Flippy.CHANNELS_URL)
                .uploadProgressBar(progressBar)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(int downloaded, int total) {
                        progressBar.setProgress(downloaded);
                        buttonCreateNewChannel.setText("Please wait ... ");
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Creating " + channelName);
                        progressDialog.show();
                    }
                })
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setMultipartParameter("community_id", CommunityCenterActivity.userCommunityId)
                .setMultipartParameter("bio", channelBio)
                .setMultipartParameter("creator", CommunityCenterActivity.regUserID)
                .setMultipartParameter("name", channelName)
                .setMultipartParameter("is_public", is_public)
                .setMultipartFile("image", new File(selectedImagePath))
                .asJsonObject()

                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.e("file", selectedImagePath);
                        buttonCreateNewChannel.setEnabled(true);
                        buttonCreateNewChannel.setText(getText(R.string.channel_create));
                        progressDialog.dismiss();
                        try {
                            if (result.has("details")) {
                                Crouton.makeText(CreateChannelActivity.this, "Failed to create channel", Style.ALERT)
                                        .show();
                                return;
                            }
                            if (result != null && !result.has("details")) {
                                JsonObject channelObject = result.getAsJsonObject("results");
                                channels = new Channels(channelObject.get("id").getAsString());
                                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(CreateChannelActivity.this,
                                        DatabaseHelper.class);
                                try {
                                    channelDao = databaseHelper.getChannelDao();
                                    channelDao.createOrUpdate(channels);
                                    channelDao.refresh(channels);
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                                ToastMessages.showToastLong(CreateChannelActivity.this, "Channel " + channelName + " Created successfully");
                                goToMainActivity();
                            }
                            if (e != null) {
                                ToastMessages.showToastLong(CreateChannelActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            }
                        } catch (Resources.NotFoundException e1) {
                            e1.printStackTrace();
                        }

                    }

                });
    }

    private void goToMainActivity() {
        Intent intent = getIntent();
        intent.setClass(CreateChannelActivity.this, CommunityCenterActivity.class);
        startActivity(intent);
    }
}
