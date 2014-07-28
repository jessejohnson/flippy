package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;

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

public class ManageChannelActivity extends ActionBarActivity {
    private static final int PICK_FROM_CAMERA = 5;
    private static final int CROP_FROM_CAMERA = 6;
    private static final int PICK_FROM_FILE = 7;
    private static final int PROMOTE_USER = 8;
    private EditText editTextManageChannelChannelName;
    private ImageView imageViewChannelManageEdit, imageViewEditChannelName;
    private Intent intent;
    private String channelName, image_url;
    public static String creatorId;
    private Uri mImageCaptureUri;
    private AlertDialog dialog;
    private Button buttonAddAdmin;
    private ProgressBar progressBarLoadAdmin;
    private SuperToast superToast;
    private ListView listViewChannelAdmins;
    private List<AdminPerson> rowItems;
    private AdminAdapter adminAdapter;
    public static String channelId;
    private ProgressDialog progressDialog;


    private static String TAG = "ManageChannelActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channel);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        image_url = intent.getStringExtra("image_url");
        creatorId = intent.getStringExtra("creatorId");

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(channelName);
        }
        View header = getLayoutInflater().inflate(R.layout.activity_manage_channel_header, null);
        listViewChannelAdmins = (ListView) findViewById(R.id.listViewChannelAdmins);
        listViewChannelAdmins.addHeaderView(header);
        progressDialog = new ProgressDialog(ManageChannelActivity.this);
        superToast = new SuperToast(ManageChannelActivity.this);
        progressBarLoadAdmin = (ProgressBar) findViewById(R.id.progressBarLoadAdmin);
        buttonAddAdmin = (Button) findViewById(R.id.buttonAddAdminChannel);
        buttonAddAdmin.setVisibility(View.GONE);
        editTextManageChannelChannelName = (EditText) findViewById(R.id.editTextManageChannelChannelName);
        editTextManageChannelChannelName.setText(channelName);
        //textViewChannelAdmins.setText(channelName + " channel administrators");
        rowItems = new ArrayList<AdminPerson>();
        adminAdapter = new AdminAdapter(ManageChannelActivity.this,
                R.layout.channel_admis_listview, rowItems);
        listViewChannelAdmins.setAdapter(adminAdapter);
        imageViewChannelManageEdit = (ImageView) findViewById(R.id.imageViewChannelManageEdit);
        imageViewEditChannelName = (ImageView) findViewById(R.id.imageViewEditChannelName);
        String adminURL = Flippy.channels + channelId + "/admins/";
        getAdminsList(adminURL);
        showDialog();

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
        String noMember = "No member selected";
        if (requestCode == RESULT_CANCELED) {
            Log.e(TAG, "Cancelled");
            showSuperToast(noMember, false);
            return;
        }
        if (data == null) {
            showSuperToast(noMember, false);
            Log.e(TAG, "Null data");
            return;
        }
        if (requestCode == PROMOTE_USER) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG, data.getStringExtra("memberId") + " " + data.getStringExtra("memberEmail"));
                promoteUser(data.getStringExtra("memberId"));
            }
        } else if(requestCode ==PICK_FROM_FILE) {

        }

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
                        try {
                            if (result.has("detail")) {
                                showSuperToast("sorry, an error occurred", false);
                                Log.e("Error form get admin list", "Admin list not found");
                                return;
                            } else if (result != null) {
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
        return super.onOptionsItemSelected(item);
    }

    private void deleteChannel(String channelId) {
        String url = Flippy.channels + channelId + "/";
        StringRequest delete = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showSuperToast("successfully removed", true);
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
        String url = Flippy.channels + channelId + "/promote_user/";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", memberId);
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
                            if (result != null) {
                                showSuperToast(result.get("result").getAsString(), true);
                                intent.setClass(ManageChannelActivity.this, CommunityCenterActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (e != null) {
                                showSuperToast(getResources().getString(R.string.internet_connection_error_dialog_title), false);
                                Log.e("Error promoting user", e.toString());
                                return;
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
}
