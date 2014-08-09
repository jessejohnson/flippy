package com.jojo.flippy.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;

/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GcmMessageHandler extends IntentService {
    private final String LOG_TAG = getClass().getSimpleName();
    Context appInstance ;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        appInstance = getApplication();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        Log.e("GCMHandler>>", "intent hit" + intent) ;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);


        for(String str : intent.getExtras().keySet()){
            Log.e(LOG_TAG +" key>>", str ) ;
            try {
                Log.e(LOG_TAG + "GCM value >>", intent.getStringExtra(str + ""));
            }catch (Exception e){
                e.printStackTrace();
            }

            if("unregistered".equalsIgnoreCase(str)){

                String regid = null;
                try {
                    regid = gcm.register( "Kuuloffer.PROJECT_ID ");
                    String msg = "Device registered, registration ID= " + regid;
                    Log.e(LOG_TAG," registeration sent" );
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if( "registration_id".equalsIgnoreCase(str) ){
                String new_gcm_id = intent.getStringExtra(str);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
                    return ;
                }
                try {
                    //Log.e(LOG_TAG," thisUser old gcm_id>>>>" + User.getThisUser().gcm_id );
                    //User.getThisUser().gcm_id = new_gcm_id ;
                    //User.userDao.update(User.getThisUser()) ;

                    //Log.e(LOG_TAG + " thisUser new gcm_id", ">>>>" + User.getThisUser().gcm_id );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.

        if( !extras.isEmpty() ){
            //Log.e(LOG_TAG + " gcm", "the intent is >>" + intent ) ;

            String messageType = gcm.getMessageType(intent);
            Log.e(LOG_TAG, "messageType >>" + messageType );
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(LOG_TAG, "received error >>" + extras.toString() );
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(LOG_TAG, "received deleted >>" + extras.toString() );
                // If it's a regular GCM message, do some work.
            } else if (
                    GoogleCloudMessaging.
                            MESSAGE_TYPE_MESSAGE.equals(messageType)
                            ||
                            (messageType == null)
                    ) {
                Log.i( LOG_TAG, "Received: " + extras.toString());
                String msgTitle = extras.getString("title");
                String msg = extras.getString("message");
                //=========

                Log.e("GCM", "Received : (" +messageType+")  "+ msg + extras.getString("message"));
                if((msg == null) ){
                    msg = "getMsgs" ;
                }

                JSONObject json = new JSONObject() ;

//                handler = appInstance.chatService.getListener().wakefulHandler ;
//                Message msgObj = handler.obtainMessage() ;
//                //handler.obtainMessage();
//                Bundle b = new Bundle();
//                b.putString("message", msg);
//                msgObj.setData(b);
//                handler.sendMessage(msgObj);

                Log.e("GCM", " handler fired with message " + msg );

            }

        }else{
            Log.e(LOG_TAG, "the extras is nul ");
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);

    }

}