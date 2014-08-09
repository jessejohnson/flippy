package com.jojo.flippy.profile;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.jojo.flippy.app.R;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ImageDecoder;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ImagePreviewActivity extends ActionBarActivity {
    private ImageView imageViewPreviewShare;
    private TextView textViewImageDescription;
    private Intent intent;
    private ProgressBar progressBarLoadUserImage;
    private String avatar, description = "";
    private String TAG = "ImagePreviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        intent = getIntent();
        avatar = intent.getStringExtra("avatar");
        if (intent.getStringExtra("imageName") != null) {
            description = intent.getStringExtra("imageName");
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(description);
        }
        imageViewPreviewShare = (ImageView) findViewById(R.id.imageViewPreviewShare);
        progressBarLoadUserImage = (ProgressBar) findViewById(R.id.progressBarLoadUserImage);
        textViewImageDescription = (TextView) findViewById(R.id.textViewImageDescription);


        if (avatar == null || avatar.equals("")) {
            progressBarLoadUserImage.setVisibility(View.GONE);
            ToastMessages.showToastLong(ImagePreviewActivity.this, "This image cannot be preview");
            return;
        }

        Ion.with(ImagePreviewActivity.this)
                .load(avatar)
                .intoImageView(imageViewPreviewShare);
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
        textViewImageDescription.setText(description);


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

    private void sharePhoto(final String image) {
        if (image == null) {
            ToastMessages.showToastLong(ImagePreviewActivity.this, "Sharing failed");
            return;
        }
        progressBarLoadUserImage.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(image, new FileAsyncHttpResponseHandler(ImagePreviewActivity.this) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                progressBarLoadUserImage.setVisibility(View.GONE);
                Log.e(TAG, response.getAbsolutePath());
                Bitmap bitmap = ImageDecoder.decodeFile(response.getAbsolutePath());
                String imagePath = ImageDecoder.saveBitmap(bitmap, "Flippy", "Media");
                shareImage(imagePath);
                response.deleteOnExit();
            }

            @Override
            public void onFailure(Throwable e, File response) {
                progressBarLoadUserImage.setVisibility(View.GONE);
                ToastMessages.showToastLong(ImagePreviewActivity.this, "Sharing failed");
            }

        });

    }

    private void shareImage(String imagePath) {
        if (imagePath != null && imagePath != "") {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri photoUri = Uri.parse(imagePath);
            shareIntent.setData(photoUri);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_TEXT, description);
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
            startActivity(Intent.createChooser(shareIntent, "Share Via"));

        } else {
            ToastMessages.showToastLong(ImagePreviewActivity.this, "Sharing failed");
        }
    }
}

