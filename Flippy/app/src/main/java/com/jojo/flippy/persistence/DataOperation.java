package com.jojo.flippy.persistence;


import android.content.Context;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import java.util.Calendar;

public class DataOperation {
    private static Dao<Post, Integer> postDao;
    private Context context;

    public DataOperation(Context context) {
        this.context = context;
    }

    public static boolean savePost(Context context, String notice_id, String notice_title, String notice_body, String notice_image, String start_date, String author_email, String author_id, String author_first_name, String author_last_name, String authorAvatar, String authorAvatarThumb, String channel_id) {
        boolean success = false;
        try {
            DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context,
                    DatabaseHelper.class);
            postDao = databaseHelper.getPostDao();
            Calendar calendar = Calendar.getInstance();
            Post post = new Post(notice_id, notice_title, notice_body, notice_image, start_date, author_email, author_id, author_first_name, author_last_name, authorAvatar, authorAvatarThumb, channel_id, calendar.getTimeInMillis());
            postDao.createIfNotExists(post);
            success = true;
        } catch (java.sql.SQLException sqlE) {
            Log.e("Saving to database ", sqlE.toString());
        }
        return success;
    }
}
