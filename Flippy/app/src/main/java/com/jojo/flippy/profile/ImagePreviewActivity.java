package com.jojo.flippy.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jojo.flippy.app.R;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ImagePreviewActivity extends ActionBarActivity {
    private ImageView imageViewPreviewShare;
    private Intent intent;
    private ProgressBar progressBarLoadUserImage;
    private String avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        intent = getIntent();
        avatar = intent.getStringExtra("avatar");
        imageViewPreviewShare = (ImageView) findViewById(R.id.imageViewPreviewShare);
        progressBarLoadUserImage = (ProgressBar) findViewById(R.id.progressBarLoadUserImage);


        if (avatar == null || avatar.equals("")) {
            progressBarLoadUserImage.setVisibility(View.GONE);
            Crouton.makeText(ImagePreviewActivity.this, "The request cannot be processed", Style.ALERT);
            return;
        }

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

