package com.jojo.flippy.util;

/**
 * Created by bright on 7/3/14.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class FlippyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent receiverService = new Intent(context, FlippyAlarmService.class);
        context.startService(receiverService);

    }
}

