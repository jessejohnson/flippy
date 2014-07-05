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
    private String noticeTitle;
    private String noticeBody;
    private String noticeId;
    private String noticeSubtitle;


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent receiverService =intent.setClass(context, FlippyAlarmService.class);
        context.startService(receiverService);

    }
}

