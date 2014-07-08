package com.jojo.flippy.profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.jojo.flippy.app.R;
import com.jojo.flippy.app.SelectCommunityActivity;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class EditProfileActivity extends ActionBarActivity {
    private ImageView imageViewMemberEdit;
    private EditText editTextEditProfileFirstName;
    private EditText editTextEditProfileLastName;
    private EditText editTextEditProfileEmail;
    private EditText editTextEditProfileDateOfBirth;
    private EditText editTextEditProfileNumber;
    private Spinner genderSpinner;
    private ImageView imageViewUploadPhoto;


    private String NewFirstNameUpdate;
    private String NewLastNameUpdate;
    private String NewNumberUpdate;
    private String NewEmailUpdate;
    private String NewDateOfBirthUpdate;
    private String NewGenderUpdate;
    private String NewAvatarUpdate;


    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private String imagePath;
    private String fileManagerString;
    private int column_index;
    private Cursor cursor;
    private String path;


    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userAvatar = "";
    private String userGender = "";
    private String userDateOfBirth = "";
    private String userAvatarThumb = "";
    private String userCommunityId;

    private boolean save = false;

    private Dao<User, Integer> userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        imageViewMemberEdit = (ImageView) findViewById(R.id.imageViewMemberEdit);
        editTextEditProfileFirstName = (EditText) findViewById(R.id.editTextEditProfileFirstName);
        editTextEditProfileLastName = (EditText) findViewById(R.id.editTextEditProfileLastName);
        editTextEditProfileEmail = (EditText) findViewById(R.id.editTextEditProfileEmail);
        editTextEditProfileDateOfBirth = (EditText) findViewById(R.id.editTextEditProfileDateOfBirth);
        editTextEditProfileNumber = (EditText) findViewById(R.id.editTextEditProfileNumber);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        editTextEditProfileFirstName.setText(CommunityCenterActivity.userFirstName);
        editTextEditProfileLastName.setText(CommunityCenterActivity.userLastName);
        editTextEditProfileEmail.setText(CommunityCenterActivity.regUserEmail);
        editTextEditProfileDateOfBirth.setText(CommunityCenterActivity.userDateOfBirth);
        imageViewUploadPhoto = (ImageView) findViewById(R.id.imageViewUploadPhoto);
        loadProfile();

        try {
            if (CommunityCenterActivity.userGender.isEmpty()) {
                genderSpinner.setSelection(0);
            }
            if (CommunityCenterActivity.userGender.equalsIgnoreCase("male")) {
                genderSpinner.setSelection(1);
            }
            if (CommunityCenterActivity.userGender.equalsIgnoreCase("female")) {
                genderSpinner.setSelection(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        imageViewMemberEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

        imageViewUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAvatar(selectedImagePath);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                fileManagerString = selectedImageUri.getPath();
                selectedImagePath = getPath(selectedImageUri);
                if (imagePath == null) {
                    ToastMessages.showToastShort(EditProfileActivity.this, "No image selected");
                    return;
                }
                imagePath.getBytes();
                path = imagePath.toString();
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
                imageViewMemberEdit.setImageBitmap(bm);

            } else {
                ToastMessages.showToastLong(EditProfileActivity.this, "Image not uploaded");
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile_done) {
            NewFirstNameUpdate = editTextEditProfileFirstName.getText().toString().trim();
            NewLastNameUpdate = editTextEditProfileLastName.getText().toString().trim();
            NewNumberUpdate = editTextEditProfileNumber.getText().toString().trim();
            NewEmailUpdate = editTextEditProfileEmail.getText().toString().trim();
            NewDateOfBirthUpdate = editTextEditProfileDateOfBirth.getText().toString().trim();
            NewGenderUpdate = genderSpinner.getSelectedItem().toString();
            if (updateUserStringDetails(NewEmailUpdate, NewFirstNameUpdate, NewLastNameUpdate, NewGenderUpdate)) {
                ToastMessages.showToastLong(EditProfileActivity.this, "Profile updated");
                goToAccountProfile();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToAccountProfile() {
        Intent intent = getIntent();
        intent.setClass(EditProfileActivity.this, AccountProfileActivity.class);
        startActivity(intent);
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    private void uploadAvatar(String filePath) {
        if (filePath == null) {
            ToastMessages.showToastShort(EditProfileActivity.this, "Browse a new image first");
            return;
        }
        Ion.with(EditProfileActivity.this, Flippy.userChannelsSubscribedURL + "upload-avatar/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setMultipartFile("avatar", new File(filePath))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result.has("details")) {
                            Crouton.makeText(EditProfileActivity.this, "Failed to upload picture", Style.ALERT)
                                    .show();
                            return;
                        }
                        if (result != null && !result.has("details")) {
                            getUserInfo();
                            ToastMessages.showToastLong(EditProfileActivity.this, result.get("results").getAsString());
                        }
                        if (e != null) {
                            ToastMessages.showToastLong(EditProfileActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        }

                    }
                });
    }

    private void updateUser() {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this,
                    DatabaseHelper.class);
            userDao = databaseHelper.getUserDao();
            UpdateBuilder<User, Integer> updateBuilder = userDao.updateBuilder();
            updateBuilder.where().eq("user_email", userEmail);
            updateBuilder.updateColumnValue("avatar", userAvatar);
            updateBuilder.updateColumnValue("avatar_thumb", userAvatarThumb);
            updateBuilder.update();
            goToAccountProfile();
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            Crouton.makeText(EditProfileActivity.this, "Sorry, Try again later", Style.ALERT)
                    .show();
            return;

        }
    }

    private void getUserInfo() {
        Ion.with(EditProfileActivity.this, Flippy.userChannelsSubscribedURL + CommunityCenterActivity.regUserID + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result.has("details")) {
                            Crouton.makeText(EditProfileActivity.this, "Failed to update user", Style.ALERT)
                                    .show();
                            return;
                        }
                        if (result != null && !result.has("details")) {
                            if (!result.get("avatar").isJsonNull()) {
                                userAvatar = result.get("avatar").getAsString();
                            }
                            if (!result.get("avatar_thumb").isJsonNull()) {
                                userAvatarThumb = result.get("avatar_thumb").getAsString();
                            }
                            userEmail = result.get("email").getAsString();
                            userCommunityId = result.get("community").getAsString();
                            userLastName = result.get("last_name").getAsString();
                            userFirstName = result.get("first_name").getAsString();
                            if (!result.get("gender").isJsonNull()) {
                                userGender = result.get("gender").getAsString();
                            }
                            if (!result.get("date_of_birth").isJsonNull()) {
                                userDateOfBirth = result.get("date_of_birth").getAsString();
                            }

                        }
                        if (e != null) {
                            ToastMessages.showToastLong(EditProfileActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            return;
                        }
                        updateUser();
                    }

                });
    }

    private void loadProfile() {
        Ion.with(imageViewMemberEdit)
                .placeholder(R.color.flippy_orange)
                .animateIn(R.anim.fade_in)
                .load(CommunityCenterActivity.userAvatarURL);
    }
    private boolean updateUserStringDetails(String email, String firstName, String lastName, String gender) {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("first_name", firstName);
        json.addProperty("last_name", lastName);
        json.addProperty("gender", gender);

        Ion.with(EditProfileActivity.this)
                .load(Flippy.userBasicURL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            ToastMessages.showToastLong(EditProfileActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                        } else {
                            if (result.has("detail")) {
                                Crouton.makeText(EditProfileActivity.this, "email already in use", Style.ALERT)
                                        .show();
                                return;
                            }
                            String email = result.get("email").getAsString();
                            String first_name = result.get("first_name").getAsString();
                            String last_name = result.get("last_name").getAsString();
                            String date_of_birth = "";
                            if (!result.get("date_of_birth").isJsonNull()) {
                                date_of_birth = result.get("date_of_birth").getAsString();
                            }
                            String gender = "";
                            if (!result.get("gender").isJsonNull()) {
                                gender = result.get("gender").getAsString();
                            }
                            String community = "";
                            if (!result.get("community").isJsonNull()) {
                                gender = result.get("community").getAsString();
                            }

                            try {
                                DatabaseHelper databaseHelper = OpenHelperManager.getHelper(EditProfileActivity.this,
                                        DatabaseHelper.class);
                                userDao = databaseHelper.getUserDao();
                                UpdateBuilder<User, Integer> updateBuilder = userDao.updateBuilder();
                                updateBuilder.where().eq("user_email", email);
                                updateBuilder.updateColumnValue("first_name", first_name);
                                updateBuilder.updateColumnValue("last_name", last_name);
                                updateBuilder.updateColumnValue("date_of_birth", date_of_birth);
                                updateBuilder.updateColumnValue("gender", gender);
                                updateBuilder.updateColumnValue("community_id", community);
                                updateBuilder.update();
                                save = true;
                            } catch (java.sql.SQLException sqlE) {
                                sqlE.printStackTrace();
                                Crouton.makeText(EditProfileActivity.this, "Sorry, Try again later", Style.ALERT)
                                        .show();
                                save = false;
                                return;

                            }

                        }
                    }

                });

        return save;

    }
}
