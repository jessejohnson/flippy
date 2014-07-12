package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.ChannelMembers;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ManageChannelActivity extends ActionBarActivity {
    private EditText editTextManageChannelChannelName, editTextFirstAdmin, editTextSecondAdmin, editTextThirdAdmin, editTextFourthAdmin;
    private ImageView imageViewChannelManageEdit, imageViewEditChannelName, imageViewEditFirstAdmin, imageViewEditSecondAdmin, imageViewEditThirdAdmin, imageViewEditFourthAdmin;
    private Intent intent;
    private String channelName, channelId, image_url;
    private boolean isManage = true;
    private final int ADMIN_ONE = 1;
    private final int ADMIN_TWO = 2;
    private final int ADMIN_THREE = 3;
    private final int ADMIN_FOUR = 4;

    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 5;
    private static final int CROP_FROM_CAMERA = 6;
    private static final int PICK_FROM_FILE = 7;

    private AlertDialog dialog;
    private Button buttonRemoveChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_channel);

        intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channelId = intent.getStringExtra("channelId");
        image_url = intent.getStringExtra("image_url");

        ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(channelName);

        //the edit text views
        editTextManageChannelChannelName = (EditText) findViewById(R.id.editTextManageChannelChannelName);
        editTextManageChannelChannelName.setText(channelName);
        editTextFirstAdmin = (EditText) findViewById(R.id.editTextFirstAdmin);
        editTextSecondAdmin = (EditText) findViewById(R.id.editTextSecondAdmin);
        editTextThirdAdmin = (EditText) findViewById(R.id.editTextThirdAdmin);
        editTextFourthAdmin = (EditText) findViewById(R.id.editTextFourthAdmin);


        //the image views
        imageViewChannelManageEdit = (ImageView) findViewById(R.id.imageViewChannelManageEdit);
        imageViewEditChannelName = (ImageView) findViewById(R.id.imageViewEditChannelName);
        imageViewEditFirstAdmin = (ImageView) findViewById(R.id.imageViewEditFirstAdmin);
        imageViewEditSecondAdmin = (ImageView) findViewById(R.id.imageViewEditSecondAdmin);
        imageViewEditThirdAdmin = (ImageView) findViewById(R.id.imageViewEditThirdAdmin);
        imageViewEditFourthAdmin = (ImageView) findViewById(R.id.imageViewEditFourthAdmin);
        buttonRemoveChannel = (Button) findViewById(R.id.buttonRemoveChannel);
        //called the method without the show
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
        editTextFirstAdmin.setEnabled(false);
        editTextSecondAdmin.setEnabled(false);
        editTextThirdAdmin.setEnabled(false);
        editTextFourthAdmin.setEnabled(false);

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

        imageViewEditFirstAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_ONE);
            }
        });
        imageViewEditSecondAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_TWO);

            }
        });
        imageViewEditThirdAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_THREE);

            }
        });
        imageViewEditFourthAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, ADMIN_FOUR);

            }
        });
        buttonRemoveChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Flippy.channels + channelId + "/";
                StringRequest delete = new StringRequest(Request.Method.DELETE, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(ManageChannelActivity.this, channelName+ " successfully removed", Toast.LENGTH_LONG).show();
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
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save_changes) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        String adminEmail = data.getStringExtra("memberEmail");
        if (requestCode == ADMIN_ONE) {
            if (null != data) {
                editTextFirstAdmin.setText(adminEmail);
            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
            return;

        }
        if (requestCode == ADMIN_TWO) {
            if (null != data) {
                editTextSecondAdmin.setText(adminEmail);
            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
            return;
        }
        if (requestCode == ADMIN_THREE) {
            if (null != data) {
                editTextThirdAdmin.setText(adminEmail);

            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
            return;
        }
        if (requestCode == ADMIN_FOUR) {
            if (null != data) {
                editTextFourthAdmin.setText(adminEmail);
            } else {
                ToastMessages.showToastLong(ManageChannelActivity.this, noMember);
            }
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

}
