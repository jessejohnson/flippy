package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jojo.flippy.adapter.AdminAdapter;
import com.jojo.flippy.adapter.AdminPerson;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.ChannelMembers;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ManageChannelActivity extends ActionBarActivity {
    private static final int PICK_FROM_CAMERA = 5;
    private static final int CROP_FROM_CAMERA = 6;
    private static final int PICK_FROM_FILE = 7;
    private EditText editTextManageChannelChannelName;
    private ImageView imageViewChannelManageEdit, imageViewEditChannelName;
    private Intent intent;
    private String channelName, channelId, image_url;
    private Uri mImageCaptureUri;
    private AlertDialog dialog;
    private Button buttonAddAdmin;
    private ProgressBar progressBarLoadAdmin;
    private SuperToast superToast;

    private ListView listViewChannelAdmins;
    private List<AdminPerson> rowItems;
    private AdminAdapter adminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channel);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        image_url = intent.getStringExtra("image_url");

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(channelName);
        }

        //the edit text views
        superToast = new SuperToast(ManageChannelActivity.this);
        progressBarLoadAdmin = (ProgressBar) findViewById(R.id.progressBarLoadAdmin);
        buttonAddAdmin = (Button) findViewById(R.id.buttonAddAdminChannel);
        buttonAddAdmin.setVisibility(View.GONE);
        editTextManageChannelChannelName = (EditText) findViewById(R.id.editTextManageChannelChannelName);
        editTextManageChannelChannelName.setText(channelName);

        //the image views
        rowItems = new ArrayList<AdminPerson>();
        listViewChannelAdmins = (ListView) findViewById(R.id.listViewChannelAdmins);
        adminAdapter = new AdminAdapter(ManageChannelActivity.this,
                R.layout.channel_admis_listview, rowItems);
        listViewChannelAdmins.setAdapter(adminAdapter);
        imageViewChannelManageEdit = (ImageView) findViewById(R.id.imageViewChannelManageEdit);
        imageViewEditChannelName = (ImageView) findViewById(R.id.imageViewEditChannelName);
        String adminURL = Flippy.channels + channelId + "/admins/";
        getAdminsList(adminURL);
        showDialog();

        Ion.with(imageViewChannelManageEdit)
                .placeholder(R.drawable.channel_bg)
                .animateIn(R.anim.fade_in)
                .load(image_url);
        imageViewChannelManageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        //disable all the fields
        editTextManageChannelChannelName.setEnabled(false);
        intent.setClass(ManageChannelActivity.this, ChannelMembers.class);
        intent.putExtra("isManage", true);

        //enable the edit text for the channel name on click
        imageViewEditChannelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextManageChannelChannelName.setEnabled(true);
                editTextManageChannelChannelName.setFocusable(true);
            }
        });

        listViewChannelAdmins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


    }

    private void updateAdapter() {
        adminAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String noMember = "No member selected";
        // check if the request code is same as what is passed  here it is 2
        if (requestCode != RESULT_OK) {
            ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            return;
        }
        if (data == null) {
            ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            return;
        }
        ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
    }

    private void showDialog() {
        final String[] items = new String[]{"Take from camera",
                "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManageChannelActivity.this,
                android.R.layout.select_dialog_item, items);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ManageChannelActivity.this);
        builder.setTitle("Select image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mImageCaptureUri = Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), "tmp_avatar_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg"));
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                            mImageCaptureUri);
                    try {
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (position == 1) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Complete action using"), PICK_FROM_FILE);
                } else {
                    ToastMessages.showToastLong(ManageChannelActivity.this, "No option selected");
                }
            }
        });
        dialog = builder.create();
    }

    private void goToMainActivity() {
        Intent intent = getIntent();
        intent.setClass(ManageChannelActivity.this, CommunityCenterActivity.class);
        startActivity(intent);
    }

    private void getAdminsList(String url) {
        Ion.with(ManageChannelActivity.this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressBarLoadAdmin.setVisibility(View.GONE);
                        if (result.has("detail")) {
                            showSuperToast("sorry, an error occurred");
                            return;
                        }
                        if (result != null) {
                            JsonArray adminArray = result.getAsJsonArray("results");
                            if (adminArray.size() < 5) {
                                buttonAddAdmin.setVisibility(View.VISIBLE);
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
                        }
                        if (e != null) {
                            showSuperToast("sorry, internet connection occurred");
                        }

                    }
                });
    }

    private void showSuperToast(String message) {
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setBackground(SuperToast.Background.PURPLE);
        superToast.setIcon(R.drawable.icon_dark_info, SuperToast.IconPosition.LEFT);
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
            String url = Flippy.channels + channelId + "/";
            StringRequest delete = new StringRequest(Request.Method.DELETE, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showSuperToast("successfully removed");
                            goToMainActivity();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", error.toString());
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

        return super.onOptionsItemSelected(item);
    }

    private void promoteUser(String memberId) {
        String URL = Flippy.channels + memberId + "/promote_user/";
        Ion.with(ManageChannelActivity.this)
                .load(URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            if (result.has("detail")) {
                                Crouton.makeText(ManageChannelActivity.this, result.get("detail").toString(), Style.ALERT);
                                return;
                            }
                            Crouton.makeText(ManageChannelActivity.this, result.get("results").toString(), Style.CONFIRM);
                            return;
                        }
                        if (e != null) {
                            ToastMessages.showToastShort(ManageChannelActivity.this, getResources().getString(R.string.internet_connection_error_dialog_title));
                            return;
                        }

                    }
                });

    }


}
