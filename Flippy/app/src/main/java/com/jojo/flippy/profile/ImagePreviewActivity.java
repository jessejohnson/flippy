package com.jojo.flippy.profile;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.jojo.flippy.app.R;

public class ImagePreviewActivity extends ActionBarActivity {
    private ImageView imageViewPreviewShare;
    private Uri imageToShare =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        imageViewPreviewShare = (ImageView)findViewById(R.id.imageViewPreviewShare);

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

    private void sharePhoto(Uri uriToImage ){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.image_preview_menu)));
    }

}
