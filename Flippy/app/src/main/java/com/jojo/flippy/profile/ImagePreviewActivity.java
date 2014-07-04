package com.jojo.flippy.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import java.io.File;
import java.io.FileOutputStream;

public class ImagePreviewActivity extends ActionBarActivity {
    private ImageView imageViewPreviewShare;
    private Intent intent;
    private ProgressBar progressBarLoadUserImage;
    private ProgressDialog progressDialog;
    private String imagePath;
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
                .placeholder(R.color.flippy_light_header)
                .animateIn(R.anim.fade_in)
                .load(avatar)
                .setCallback(new FutureCallback<ImageView>() {

                    @Override
                    public void onCompleted(Exception ex, ImageView iv) {
                        progressBarLoadUserImage.setVisibility(View.GONE);
                        if (ex != null) {
                            ToastMessages.showToastLong(ImagePreviewActivity.this, getResources().getString(R.string.internet_connection_error_dialog_message));
                        }

                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_preview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share_photo) {
            if(avatar==null){
                ToastMessages.showToastLong(ImagePreviewActivity.this,"Failed to download image");
                return true;
            }
            saveImage(avatar);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sharePhoto(Uri uriToImage) {
        ToastMessages.showToastLong(this, uriToImage.toString());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.image_preview_menu)));
    }

   private void saveImage(String imageLink) {
        Ion.with(ImagePreviewActivity.this)
                .load("http:/flippy.flips.s3.amazonaws.com/media/posts/mecca.JPG")
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(int downloaded, int total) {
                        progressDialog.setMessage("Downloading " + total + "%");
                        progressDialog.show();
                    }
                })
                .write(new File(imageLink.trim()))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        progressDialog.cancel();
                        if (e != null) {
                            Log.e("Image error", e.toString());
                            return;
                        }
                        imagePath = file.getAbsolutePath();
                        sharePhoto(Uri.parse(imagePath));
                    }
                });
    }

}
