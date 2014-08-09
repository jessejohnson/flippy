package com.jojo.flippy.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by bright on 8/8/14.
 */
public class ImageDecoder {


    public static Bitmap decodeFile(String filePath) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        final int REQUIRED_SIZE = 1024;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);

        return bitmap;
    }
    public static String getPath(Context context,Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(
                uri, filePathColumn, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public static String saveBitmap(Bitmap bitmap, String dir, String baseName) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File pictureDir = new File(sdCard, dir);
            pictureDir.mkdirs();
            File f;
            Calendar calendar = Calendar.getInstance();
            baseName = baseName + calendar.getTimeInMillis() + ".png";
            f = new File(pictureDir, baseName);

            if (!f.exists()) {
                String name = f.getAbsolutePath();
                FileOutputStream fos = new FileOutputStream(name);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                return f.getAbsolutePath();
            }

        } catch (Exception e) {
        } finally {

        }
        return null;
    }
}
