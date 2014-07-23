package com.jojo.flippy.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jojo.flippy.services.FlippyAlarmService;

public class FlippyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent receiverService =intent.setClass(context, FlippyAlarmService.class);
        context.startService(receiverService);

    }
}

