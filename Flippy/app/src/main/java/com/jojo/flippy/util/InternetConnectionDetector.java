package com.jojo.flippy.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by odette on 6/3/14.
 */
public class InternetConnectionDetector {
    private Context context;

    public InternetConnectionDetector(Context c){
        this.context = c;
    }

    //This checks for all possible Internet providers
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null){
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if(infos != null){
                for(int i = 0; i < infos.length; i++){
                    if(infos[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
