package com.jojo.flippy.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

public class ImagePreviewActivity extends ActionBarActivity {
    private ImageView imageViewPreviewShare;
    private Uri imageToShare = null;
    private Intent intent;
    private ProgressBar progressBarLoadUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        intent = getIntent();
        String avatar = intent.getStringExtra("avatar");
        imageViewPreviewShare = (ImageView) findViewById(R.id.imageViewPreviewShare);
        progressBarLoadUserImage = (ProgressBar)findViewById(R.id.progressBarLoadUserImage);

        Ion.with(imageViewPreviewShare)
                .placeholder(R.color.flippy_light_header)
                .animateIn(R.anim.fade_in)
                .load(avatar)
                .setCallback(new FutureCallback<ImageView>() {

                    @Override
                    public void onCompleted(Exception ex, ImageView iv) {
                        progressBarLoadUserImage.setVisibility(View.GONE);
                        if(ex != null){
                            ToastMessages.showToastLong(ImagePreviewActivity.this,getResources().getString(R.string.internet_connection_error_dialog_message));
                        }

                    }
                });
        imageToShare = Uri.parse(avatar);

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
            sharePhoto(imageToShare);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sharePhoto(Uri uriToImage) {
       // ToastMessages.showToastLong(this,uriToImage.toString());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.image_preview_menu)));


    }

}
