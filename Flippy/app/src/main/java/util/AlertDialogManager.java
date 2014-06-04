package util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.jojo.flippy.app.R;

/**
 * Created by odette on 6/3/14.
 */
public class AlertDialogManager {

    public void showAlertDialog(Context context, String title, String messgage, Boolean status){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(messgage);

        if(status != null){
            //TODO change drawables from indicator circles to actual success/failure drawables
            //alertDialog.setIcon((status)? R.drawable.indicator_circle_active : R.drawable.indicator_circle);
        }

        alertDialog.setButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int which){

            }
        });

        alertDialog.show();
    }
}
