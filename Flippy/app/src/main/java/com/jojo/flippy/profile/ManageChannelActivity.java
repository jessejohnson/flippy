package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.adapter.AdminAdapter;
import com.jojo.flippy.adapter.AdminPerson;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.ChannelMembers;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.core.CreateChannelActivity;
import com.jojo.flippy.core.CreateNoticeActivity;
import com.jojo.flippy.persistence.Channels;
import com.jojo.flippy.persistence.DatabaseHelper;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ImageDecoder;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageChannelActivity extends ActionBarActivity {
    private static final int PICK_FROM_FILE = 7;
    private static final int PROMOTE_USER = 8;
    private TextView textViewChannelNameEdit;
    private ImageView imageViewChannelManageEdit, imageViewChannelEdit;
    private Intent intent;
    private String channelName, image_url;
    public static String creatorId;
    private Button buttonAddAdmin, buttonEditChannelName;
    private ProgressBar progressBarLoadAdmin, progressBarUploadChannelImage;
    private SuperToast superToast;
    private ListView listViewChannelAdmins;
    private List<AdminPerson> rowItems;
    private AdminAdapter adminAdapter;
    public static String channelId;
    private ProgressDialog progressDialog;
    private Dao<Channels, Integer> channelDao;
    private Context context;
    private String filePath;
    private ActionBar actionBar;
    private static String TAG = "ManageChannelActivity";
    private String adminURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channel);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        image_url = intent.getStringExtra("image_url");
        creatorId = intent.getStringExtra("creatorId");

        actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(channelName);
        }
        context = this;
        View header = getLayoutInflater().inflate(R.layout.activity_manage_channel_header, null);
        listViewChannelAdmins = (ListView) findViewById(R.id.listViewChannelAdmins);
        listViewChannelAdmins.addHeaderView(header);
        progressDialog = new ProgressDialog(ManageChannelActivity.this);
        superToast = new SuperToast(ManageChannelActivity.this);
        progressBarLoadAdmin = (ProgressBar) findViewById(R.id.progressBarLoadAdmin);
        progressBarUploadChannelImage = (ProgressBar) findViewById(R.id.progressBarUploadChannelImage);
        buttonAddAdmin = (Button) findViewById(R.id.buttonAddAdminChannel);
        buttonAddAdmin.setVisibility(View.GONE);
        textViewChannelNameEdit = (TextView) findViewById(R.id.textViewChannelNameEdit);
        textViewChannelNameEdit.setText(channelName);
        //textViewChannelAdmins.setText(channelName + " channel administrators");
        rowItems = new ArrayList<AdminPerson>();
        adminAdapter = new AdminAdapter(ManageChannelActivity.this,
                R.layout.channel_admis_listview, rowItems);
        listViewChannelAdmins.setAdapter(adminAdapter);
        imageViewChannelManageEdit = (ImageView) findViewById(R.id.imageViewChannelManageEdit);
        imageViewChannelEdit = (ImageView) findViewById(R.id.imageViewChannelEdit);
        buttonEditChannelName = (Button) findViewById(R.id.buttonEditChannelName);
        adminURL = Flippy.CHANNELS_URL + channelId + "/admins/";
        getAdminsList(adminURL);


        listViewChannelAdmins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textViewAdminEmail = (TextView) view.findViewById(R.id.textViewAdminEmail);
                TextView textViewAdminFullName = (TextView) view.findViewById(R.id.textViewAdminFullName);
                TextView textViewAdminId = (TextView) view.findViewById(R.id.textViewAdminId);
                String memberEmail = textViewAdminEmail.getText().toString();
                String memberFullName = textViewAdminFullName.getText().toString();
                String memberId = textViewAdminId.getText().toString();
                intent.putExtra("memberEmail", memberEmail);
                intent.putExtra("memberFullName", memberFullName);
                intent.putExtra("memberId", memberId);
                intent.setClass(ManageChannelActivity.this, MemberDetailActivity.class);
                startActivity(intent);
            }
        });
        Ion.with(imageViewChannelManageEdit)
                .placeholder(R.drawable.channel_place)
                .error(R.drawable.channel_error)
                .animateIn(R.anim.fade_in)
                .load(image_url);
        imageViewChannelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto(view);
            }
        });

        intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
        intent.putExtra("isManage", true);

        //enable the edit text for the channel name on click
        buttonEditChannelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editChannelName();
            }
        });

        buttonAddAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start an activity for result and promote the user
                intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
                intent.putExtra("isManage", true);
                startActivityForResult(intent, PROMOTE_USER);
            }
        });


    }


    private void updateAdapter() {
        adminAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROMOTE_USER && resultCode == RESULT_OK && null != data) {
            promoteUser(data.getStringExtra("memberId"));
        }
        if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            filePath = ImageDecoder.getPath(context, selectedImage);
            if (filePath != null) {
                uploadNewChannelImage(filePath);
            } else {
                ToastMessages.showToastLong(context, "Sorry image upload failed");
            }
        } else {
            // ToastMessages.showToastLong(context, cancel);
        }

    }

    public void pickPhoto(View view) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), PICK_FROM_FILE);
        }
    }


    private void goToMainActivity() {
        Intent intent = getIntent();
        intent.setClass(ManageChannelActivity.this, CommunityCenterActivity.class);
        startActivity(intent);
    }

    private void getAdminsList(String url) {
        rowItems.clear();
        Ion.with(ManageChannelActivity.this)
                .load(url)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadAdmin.setVisibility(View.GONE);
                        try {
                            if (result.has("detail")) {
                                showSuperToast("sorry, an error occurred", false);
                                Log.e("Error form get admin list", "Admin list not found");
                                return;
                            } else if (result != null) {
                                JsonArray adminArray = result.getAsJsonArray("results");
                                if (adminArray.size() < 5) {
                                    buttonAddAdmin.setVisibility(View.VISIBLE);
                                } else {
                                    listViewChannelAdmins.setPadding(10, 10, 10, 0);
                                }
                                for (int i = 0; i < adminArray.size(); i++) {
                                    JsonObject item = adminArray.get(i).getAsJsonObject();
                                    String avatar = "";
                                    if (!item.get("avatar").isJsonNull()) {
                                        avatar = item.get("avatar").getAsString();
                                    }
                                    AdminPerson profileItem = new AdminPerson(URI.create(avatar), item.get("email").getAsString(), item.get("first_name").getAsString() + ", " + item.get("last_name").getAsString(), item.get("id").getAsString());
                                    rowItems.add(profileItem);
                                }
                                updateAdapter();
                            } else if (e != null) {
                                showSuperToast("sorry, internet connection occurred", false);
                            } else {
                                Log.e(TAG, "something else went wrong");
                            }
                        } catch (Exception error) {
                            Log.e("Error try catch", "Error occurred when getting admin list " + error.toString());
                        }
                    }
                });
    }

    private void showSuperToast(String message, boolean isSuccess) {
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.SHORT);
        if (isSuccess) {
            superToast.setBackground(SuperToast.Background.BLUE);
        } else {
            superToast.setBackground(SuperToast.Background.PURPLE);
        }

        superToast.setIcon(R.drawable.icon_light_info, SuperToast.IconPosition.LEFT);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setText(message);
        superToast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_remove_channel) {
            confirmChannelDelete(channelId);
        }
        if (id == R.id.action_create_channel) {
            intent.setClass(this, CreateNoticeActivity.class);
            intent.putExtra("channelName", channelName);
            intent.putExtra("channelId", channelId);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteChannel(final String channelId) {
        String url = Flippy.CHANNELS_URL + channelId + "/";
        StringRequest delete = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showSuperToast("successfully removed", true);
                        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(ManageChannelActivity.this,
                                DatabaseHelper.class);

                        try {
                            channelDao = databaseHelper.getChannelDao();
                            channelDao.deleteById(Integer.parseInt(channelId));

                        } catch (SQLException e1) {
                            e1.printStackTrace();
                            Log.e(TAG, "Error removing channel");
                        }
                        goToMainActivity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error from removing post", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token " + CommunityCenterActivity.userAuthToken);
                return headers;
            }
        };
        Flippy.getInstance().getRequestQueue().add(delete);
    }

    private void promoteUser(final String memberId) {
        progressDialog.setMessage("Promoting user...");
        progressDialog.show();
        String url = Flippy.CHANNELS_URL + channelId + "/promote_user/";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", memberId);
        Ion.with(ManageChannelActivity.this)
                .load(url)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();

                        try {

                            if (result.has("detail")) {
                                ToastMessages.showToastLong(ManageChannelActivity.this, result.get("detail").getAsString());
                                Log.e(TAG, "Something else went wrong promoting a user");

                            } else if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                Log.e("Error promoting user", e.toString());
                                return;
                            } else if (result != null) {
                                Log.e(TAG, result.toString());
                                ToastMessages.showToastLong(ManageChannelActivity.this, result.get("results").getAsString());
                                getAdminsList(adminURL);
                            } else {
                                Log.e(TAG, "Something else went wrong promoting a user");
                            }

                        } catch (Exception exception) {
                            Log.e(TAG, "Error promoting the user " + memberId + " " + exception.toString());
                        }

                    }
                });
    }


    private void confirmChannelDelete(final String channelId) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm your action");
        alert.setIcon(R.drawable.icon_dark_info);
        alert.setMessage("Removing this channel is irreversible, are you sure you want to continue ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteChannel(channelId);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        });
        alert.show();
    }


    private void uploadNewChannelImage(final String filePath) {
        if (filePath == null) {
            ToastMessages.showToastShort(context, "Browse a new image");
            return;
        }
        progressBarUploadChannelImage.setVisibility(View.VISIBLE);
        Ion.with(context, Flippy.CHANNELS_URL + channelId + "/upload-image/")
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setMultipartFile("image", new File(filePath))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarUploadChannelImage.setVisibility(View.GONE);
                        try {
                            if (result != null && !result.has("detail")) {
                                showSuperToast("Channel image changed", false);
                                Bitmap bitmap = ImageDecoder.decodeFile(filePath);
                                imageViewChannelManageEdit.setImageBitmap(bitmap);
                                return;

                            } else if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                return;
                            } else {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                return;
                            }

                        } catch (Exception exception) {
                            Log.e("Error try catch", "Error while updating channel image");
                            showSuperToast("Failed to upload image", false);
                            return;
                        }

                    }
                });
    }

    private void updateChannelName(String channelNewName) {
        buttonEditChannelName.setText("Saving...");
        buttonEditChannelName.setEnabled(false);
        String url = Flippy.CHANNELS_URL + channelId + "/";
        RequestParams params = new RequestParams();
        params.put("name", channelNewName);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken);
        client.put(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Log.e("Response success", responseBody);
                ToastMessages.showToastLong(context, "Name changed successfully");
                goToMainActivity();
                refresh();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                buttonEditChannelName.setEnabled(true);
                buttonEditChannelName.setText("Edit");
                Log.e("Status code error", statusCode + "");
                ToastMessages.showToastLong(context, "Failed to update, try later");

            }
        });
    }

    private void editChannelName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Change channel name");
        alert.setMessage("This change will be seen by people in this channel");
        final EditText input = new EditText(this);
        input.setText(channelName);
        alert.setView(input);
        alert.setCancelable(false);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (!value.equalsIgnoreCase(""))
                    updateChannelName(value);
                else {
                    input.setError("Channel name is required");
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void refresh() {
        finish();
        startActivity(getIntent());
    }
}
