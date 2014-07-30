package com.jojo.flippy.profile;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
    private ProgressBar progressBarUpdateAvatar;
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
        progressBarUpdateAvatar = (ProgressBar) findViewById(R.id.progressBarUpdateAvatar);
        progressBarUpdateAvatar.setVisibility(View.GONE);
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
                    showSuperToast("Please sorry, choose another image", false);
                    return;
                }
                imagePath.getBytes();
                path = imagePath.toString();
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
                imageViewMemberEdit.setAdjustViewBounds(true);
                imageViewMemberEdit.setMaxHeight(imageViewMemberEdit.getHeight());
                imageViewMemberEdit.setMaxWidth(imageViewMemberEdit.getWidth());
                imageViewMemberEdit.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageViewMemberEdit.setImageBitmap(bm);

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
            NewFirstNameUpdate = editTextEditProfileFirstName.getText().toString().trim();
            NewLastNameUpdate = editTextEditProfileLastName.getText().toString().trim();
            NewEmailUpdate = editTextEditProfileEmail.getText().toString().trim();
            NewDateOfBirthUpdate = editTextEditProfileDateOfBirth.getText().toString().trim();
            NewGenderUpdate = genderSpinner.getSelectedItem().toString();
            updateUserStringDetails(NewEmailUpdate, NewFirstNameUpdate, NewLastNameUpdate, NewGenderUpdate);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToMainActivity() {
        Intent intent = getIntent();
        intent.setClass(EditProfileActivity.this, CommunityCenterActivity.class);
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
        progressBarUpdateAvatar.setVisibility(View.VISIBLE);
        Ion.with(EditProfileActivity.this, Flippy.users + "upload-avatar/")
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
                            } else if (result != null) {
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
            UpdateBuilder<User, Integer> updateBuilder = userDao.updateBuilder();
            updateBuilder.where().eq("user_email", userEmail);
            updateBuilder.updateColumnValue("avatar", userAvatar);
            updateBuilder.updateColumnValue("avatar_thumb", userAvatarThumb);
            updateBuilder.update();
            goToMainActivity();
        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            showSuperToast("sorry, try later", false);
            return;

        }
    }

    private void getUserInfo() {
        Ion.with(EditProfileActivity.this, Flippy.users + CommunityCenterActivity.regUserID + "/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result.has("details")) {
                                showSuperToast("Failed to update user", false);
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
                        } catch (Exception e1) {
                            Log.e("Error updating user", "Occurred when updating user");
                        }
                        updateUser();
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

    private void updateUserStringDetails(final String email, final String firstName, final String lastName, final String gender) {
        String url = Flippy.users + "me/";
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        ToastMessages.showToastLong(EditProfileActivity.this, "Profile updated");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        if (error instanceof NetworkError) {
                        } else if (error instanceof ServerError) {
                            showSuperToast("Sorry server error occurred", false);
                        } else if (error instanceof AuthFailureError) {

                        } else if (error instanceof ParseError) {

                        } else if (error instanceof NoConnectionError) {
                            showSuperToast("Sorry internet connection error", false);
                        } else if (error instanceof TimeoutError) {
                            showSuperToast("Sorry server timeout", false);
                        }
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("gender", gender);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token " + CommunityCenterActivity.userAuthToken);
                return headers;
            }

        };
        Flippy.getInstance().getRequestQueue().add(putRequest);

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
