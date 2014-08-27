package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ImageDecoder;
import com.jojo.flippy.util.ToastMessages;
import com.jojo.flippy.util.Validator;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;


public class EditProfileActivity extends ActionBarActivity {
    private static final int SELECT_PICTURE = 1;
    private ImageView imageViewMemberEdit;
    private EditText editTextEditProfileFirstName;
    private EditText editTextEditProfileLastName;
    private EditText editTextEditProfileEmail;
    private EditText editTextEditProfileDateOfBirth;
    private Spinner genderSpinner;
    private ImageView imageViewUploadPhoto;
    private String NewFirstNameUpdate;
    private String NewLastNameUpdate;
    private String NewEmailUpdate;
    private String NewDateOfBirthUpdate;
    private String NewGenderUpdate;
    private String selectedImagePath;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userAvatar = "";
    private String userGender = "";
    private String userDateOfBirth = "";
    private String userAvatarThumb = "";
    private ProgressBar progressBarUpdateAvatar;
    private Dao<User, Integer> userDao;
    private Context context;
    private ActionBar actionBar;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        actionBar = getActionBar();
        imageViewMemberEdit = (ImageView) findViewById(R.id.imageViewMemberEdit);
        editTextEditProfileFirstName = (EditText) findViewById(R.id.editTextEditProfileFirstName);
        editTextEditProfileLastName = (EditText) findViewById(R.id.editTextEditProfileLastName);
        editTextEditProfileEmail = (EditText) findViewById(R.id.editTextEditProfileEmail);
        editTextEditProfileEmail.setEnabled(false);
        editTextEditProfileDateOfBirth = (EditText) findViewById(R.id.editTextEditProfileDateOfBirth);
        progressBarUpdateAvatar = (ProgressBar) findViewById(R.id.progressBarUpdateAvatar);
        progressBarUpdateAvatar.setVisibility(View.GONE);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinner);
        editTextEditProfileFirstName.setText(CommunityCenterActivity.userFirstName);
        editTextEditProfileLastName.setText(CommunityCenterActivity.userLastName);
        editTextEditProfileEmail.setText(CommunityCenterActivity.regUserEmail);
        editTextEditProfileDateOfBirth.setText(CommunityCenterActivity.userDateOfBirth);
        imageViewUploadPhoto = (ImageView) findViewById(R.id.imageViewUploadPhoto);
        context = this;

        loadProfile();

        try {
            if (CommunityCenterActivity.userGender.isEmpty() || CommunityCenterActivity.userGender.equalsIgnoreCase("")) {
                genderSpinner.setSelection(0);
            }
            if (CommunityCenterActivity.userGender.equalsIgnoreCase("M")) {
                genderSpinner.setSelection(1);
            }
            if (CommunityCenterActivity.userGender.equalsIgnoreCase("F")) {
                genderSpinner.setSelection(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        imageViewUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Environment.getExternalStorageState().equals("mounted")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageDecoder.getPath(context, selectedImageUri);
                if (selectedImagePath == null) {
                    showSuperToast("Please sorry, choose another image", false);
                    return;
                }
                Bitmap bm = ImageDecoder.decodeFile(selectedImagePath);
                imageViewMemberEdit.setImageBitmap(bm);
                uploadAvatar(selectedImagePath);

            } else {
                showSuperToast("sorry, unable to upload image", false);
                return;
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
            getUserData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserData() {
        NewFirstNameUpdate = editTextEditProfileFirstName.getText().toString().trim();
        NewLastNameUpdate = editTextEditProfileLastName.getText().toString().trim();
        NewEmailUpdate = editTextEditProfileEmail.getText().toString().trim();
        String dateBefore = editTextEditProfileDateOfBirth.getText().toString().trim();
        NewDateOfBirthUpdate = editTextEditProfileDateOfBirth.getText().toString().trim() + "T00:00:00Z";
        if (!Validator.isValidFirstName(NewFirstNameUpdate)) {
            editTextEditProfileFirstName.setError("Wrong name format");
            return;
        }
        if (!Validator.isValidLastName(NewLastNameUpdate)) {
            editTextEditProfileLastName.setError("Wrong name format");
            return;
        }
        if (!Validator.isValidDate(dateBefore)) {
            editTextEditProfileDateOfBirth.setError("Wrong name format");
            return;
        }
        if (genderSpinner.getSelectedItemPosition() == 0) {
            ToastMessages.showToastShort(context, "Select your gender");
            return;
        }
        NewGenderUpdate = genderSpinner.getSelectedItem().toString().substring(0, 1).toUpperCase();
        updateUserStringDetails(NewEmailUpdate, NewFirstNameUpdate, NewLastNameUpdate, NewGenderUpdate, NewDateOfBirthUpdate);

    }

    private void goToMainActivity() {
        Intent intent = getIntent();
        intent.setClass(EditProfileActivity.this, CommunityCenterActivity.class);
        startActivity(intent);
    }


    private void uploadAvatar(String filePath) {
        if (filePath == null) {
            ToastMessages.showToastShort(EditProfileActivity.this, "Browse a new image first");
            return;
        }
        progressBarUpdateAvatar.setVisibility(View.VISIBLE);
        Ion.with(EditProfileActivity.this, Flippy.USERS_URL + "upload-avatar/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setMultipartFile("avatar", new File(filePath))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarUpdateAvatar.setVisibility(View.GONE);
                        try {
                            if (result == null) {
                                showSuperToast("Failed to upload avatar", false);
                                return;
                            } else if (result != null && !result.has("detail")) {
                                getUserInfo();
                            } else if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                return;
                            } else {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                return;
                            }

                        } catch (Exception exception) {
                            Log.e("Error try catch", "Error while updating profile image");
                            showSuperToast("Failed to upload avatar", false);
                            return;
                        }

                    }
                });
    }

    private void updateUser() {
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this,
                    DatabaseHelper.class);

            userDao = databaseHelper.getUserDao();
            user = userDao.queryForAll().get(0);
            user.user_email = userEmail;
            user.avatar = userAvatar;
            user.date_of_birth = userDateOfBirth;
            user.avatar_thumb = userAvatarThumb;
            user.first_name = userFirstName;
            user.last_name = userLastName;
            user.gender = userGender;
            userDao.update(user);
            userDao.refresh(user);
            ToastMessages.showToastShort(context, "Update successful");
            goToMainActivity();
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            showSuperToast("sorry, try later", false);
            return;

        }
    }

    private void getUserInfo() {
        Ion.with(EditProfileActivity.this)
                .load(Flippy.USERS_URL + CommunityCenterActivity.regUserID + "/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result.has("details")) {
                                showSuperToast("Failed to update user", false);
                                return;
                            } else if (result != null && !result.has("details")) {
                                if (!result.get("avatar").isJsonNull()) {
                                    userAvatar = result.get("avatar").getAsString();
                                }
                                if (!result.get("avatar_thumb").isJsonNull()) {
                                    userAvatarThumb = result.get("avatar_thumb").getAsString();
                                }
                                userEmail = result.get("email").getAsString();
                                //String userId = result.get("id").getAsString();
                                userLastName = result.get("last_name").getAsString();
                                userFirstName = result.get("first_name").getAsString();
                                if (!result.get("gender").isJsonNull()) {
                                    userGender = result.get("gender").getAsString();
                                }
                                if (!result.get("date_of_birth").isJsonNull()) {
                                    userDateOfBirth = result.get("date_of_birth").getAsString().substring(0, 10).trim();
                                }
                                updateUser();

                            } else {
                                ToastMessages.showToastLong(EditProfileActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                                return;
                            }
                        } catch (Exception e1) {
                            Log.e("Error updating user", "Occurred when updating user");
                        }

                    }

                });
    }

    private void loadProfile() {
        Ion.with(imageViewMemberEdit)
                .placeholder(R.drawable.user_place_large)
                .error(R.drawable.user_error_large)
                .animateIn(R.anim.fade_in)
                .load(CommunityCenterActivity.userAvatarURL);
    }

    private void updateUserStringDetails(final String email, final String firstName, final String lastName, final String gender, final String dateOfBirth) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating user information...");
        progressDialog.show();
        String url = Flippy.USERS_URL + "me/";
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("gender", gender);
        params.put("date_of_birth", dateOfBirth);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken);
        client.put(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                progressDialog.dismiss();
                Log.e("Status code success", statusCode + "");
                Log.e("Response success", responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Log.e("Result", jsonObject.toString());
                    getUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                progressDialog.dismiss();
                Log.e("Status code error", statusCode + "");
                ToastMessages.showToastLong(context, "Failed to update, try later");

            }


        });
    }

    private void showSuperToast(String message, boolean isSuccess) {
        SuperToast superToast = new SuperToast(EditProfileActivity.this);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.VERY_SHORT);
        if (isSuccess) {
            superToast.setBackground(SuperToast.Background.BLUE);
        } else {
            superToast.setBackground(SuperToast.Background.RED);
        }

        superToast.setIcon(R.drawable.icon_dark_info, SuperToast.IconPosition.LEFT);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setText(message);
        superToast.show();

    }
}
