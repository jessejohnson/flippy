package com.jojo.flippy.persistence;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.jojo.flippy.persistence.Post;
import com.jojo.flippy.persistence.User;
import com.jojo.flippy.util.Flippy;
import com.jojo.flippy.util.ToastMessages;

import java.util.List;

/**
 * Created by bright on 7/6/14.
 */
public class SavePost {
    private Context context;
    private String Id;


    public SavePost(Context context,String Id){
        this.context =context;
        this.Id = Id;
    }

    private void save(){
        try {
            Dao<Post, Integer> postDao = ((Flippy) context).postDao;
            List<Post> postList = postDao.queryForAll();
            if(!postList.isEmpty()){
                postDao.delete(postList);
            }


        } catch (java.sql.SQLException sqlE) {
            sqlE.printStackTrace();
            ToastMessages.showToastLong(context, "Sorry, Unable to create user account");
            return;
        }

    }



}
