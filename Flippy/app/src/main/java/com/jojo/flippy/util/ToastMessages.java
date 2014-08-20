package com.jojo.flippy.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.jojo.flippy.app.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
