package com.jojo.flippy.profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.util.ToastMessages;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends ActionBarActivity {
    private ImageView imageViewMemberEdit;
    private EditText editTextEditProfileFirstName;
    private EditText editTextEditProfileLastName;
    private EditText editTextEditProfileEmail;
    private EditText editTextEditProfileDateOfBirth;
    private EditText editTextEditProfileNumber;
    private Spinner genderSpinner;


    private String NewFirstNameUpdate;
    private String NewLastNameUpdate;
    private String NewNumberUpdate;
    private String NewEmailUpdate;
    private String NewDateOfBirthUpdate;
    private String NewGenderUpdate;
    private String NewAvatarUpdate;


    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private String imagePath;
    private String fileManagerString;
    private int column_index;
    private Cursor cursor;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        imageViewMemberEdit =  (ImageView)findViewById(R.id.imageViewMemberEdit);
        editTextEditProfileFirstName =(EditText)findViewById(R.id.editTextEditProfileFirstName);
        editTextEditProfileLastName= (EditText)findViewById(R.id.editTextEditProfileLastName);
        editTextEditProfileEmail= (EditText)findViewById(R.id.editTextEditProfileEmail);
        editTextEditProfileDateOfBirth= (EditText)findViewById(R.id.editTextEditProfileDateOfBirth);
        editTextEditProfileNumber= (EditText)findViewById(R.id.editTextEditProfileNumber);
        genderSpinner= (Spinner)findViewById(R.id.genderSpinner);


        Ion.with(imageViewMemberEdit)
                .placeholder(R.color.flippy_orange)
                .animateIn(R.anim.fade_in)
                .load(CommunityCenterActivity.userAvatarURL);
        editTextEditProfileFirstName.setText(CommunityCenterActivity.userFirstName);
        editTextEditProfileLastName.setText(CommunityCenterActivity.userLastName);
        editTextEditProfileEmail.setText(CommunityCenterActivity.regUserEmail);
        editTextEditProfileDateOfBirth.setText(CommunityCenterActivity.userDateOfBirth);

        if(CommunityCenterActivity.userGender.isEmpty()){
            genderSpinner.setSelection(0);
        }
        if(CommunityCenterActivity.userGender.equalsIgnoreCase("male")){
            genderSpinner.setSelection(1);
        }
        if(CommunityCenterActivity.userGender.equalsIgnoreCase("female")){
            genderSpinner.setSelection(2);
        }


        imageViewMemberEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                fileManagerString = selectedImageUri.getPath();
                selectedImagePath = getPath(selectedImageUri);
                // img.setImageURI(selectedImageUri);
                imagePath.getBytes();
                path = imagePath.toString();
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
                imageViewMemberEdit.setImageBitmap(bm);

            }else{
                ToastMessages.showToastLong(EditProfileActivity.this, "Image not uploaded");
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile_done) {
            //Save the data back to server
            NewFirstNameUpdate = editTextEditProfileFirstName.getText().toString().trim();
            NewLastNameUpdate = editTextEditProfileLastName.getText().toString().trim();
            NewNumberUpdate = editTextEditProfileNumber.getText().toString().trim();
            NewEmailUpdate = editTextEditProfileEmail.getText().toString().trim();
            NewDateOfBirthUpdate = editTextEditProfileDateOfBirth.getText().toString().trim();
            NewGenderUpdate = genderSpinner.getSelectedItem().toString();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //get the new image path
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }
}
