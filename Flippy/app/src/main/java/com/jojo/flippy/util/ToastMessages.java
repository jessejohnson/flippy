package com.jojo.flippy.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by bright on 6/4/14.
 */
public class  ToastMessages {

    private  static Context context;

    public  ToastMessages(Context context){
        this.context = context;
    }

    public static void showToastLong(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        return;
    }
    public static void showToastShort(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        return;
    }


}
