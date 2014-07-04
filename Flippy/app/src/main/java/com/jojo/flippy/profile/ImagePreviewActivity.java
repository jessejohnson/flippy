package com.jojo.flippy.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jojo.flippy.app.R;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ImagePreviewActivity extends ActionBarActivity {
    private ImageView imageViewPreviewShare;
    private Intent intent;
    private ProgressBar progressBarLoadUserImage;
    private ProgressDialog progressDialog;
    private String avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        intent = getIntent();
        avatar = intent.getStringExtra("avatar");
        imageViewPreviewShare = (ImageView) findViewById(R.id.imageViewPreviewShare);
        progressBarLoadUserImage = (ProgressBar) findViewById(R.id.progressBarLoadUserImage);
        progressDialog = new ProgressDialog(ImagePreviewActivity.this);

        Ion.with(imageViewPreviewShare)
                .placeholder(R.color.flippy_dark_header)
                .animateIn(R.anim.fade_in)
                .load(avatar)
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception ex, ImageView imageLoaded) {
                        progressBarLoadUserImage.setVisibility(View.GONE);
                        if (ex != null) {
                            ToastMessages.showToastLong(ImagePreviewActivity.this, getResources().getString(R.string.internet_connection_error_dialog_message));
                            return;
                        }
                  }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_preview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share_photo) {
            if (avatar == null) {
                ToastMessages.showToastLong(ImagePreviewActivity.this, "Failed to share image");
                return true;
            }
            sharePhoto(avatar);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sharePhoto(String image) {
        if (image == null) {
            ToastMessages.showToastLong(ImagePreviewActivity.this, "Sharing failed");
            return;
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Download flippy resource at :" + image);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.app_name)));
    }

}

