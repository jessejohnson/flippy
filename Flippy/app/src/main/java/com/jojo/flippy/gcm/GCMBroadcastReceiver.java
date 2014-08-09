package com.jojo.flippy.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
    public GCMBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.e("GCM", "google delivered as promised");

        for(String str : intent.getExtras().keySet()){
            Log.e("GCM key>>", str ) ;
            Log.e("GCM value >>",intent.getStringExtra(str)) ;
        }

        // an Intent broadcast.
        // Explicitly specify that GcmMessageHandlerHigherVersion will handle the intent.
        ComponentName comp ;
        comp = new ComponentName(context.getPackageName() ,
                    GcmMessageHandler.class.getName());


        Log.e("GCM", "after package" + comp.getPackageName()) ;
        Log.e("GCM", "after class" + comp.getClassName()) ;

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
