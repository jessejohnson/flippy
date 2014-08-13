package com.jojo.flippy.util;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class SendParseNotification {
    public static final String ACTION = "com.jojo.flippy.app.PUSH_NOTIFICATION";
    public static final String TAG = "SendParseNotification";

    public static void sendMessage(final String title, String id, String body, String channelId) {
        ParsePush push = new ParsePush();
        //push.setMessage("");
        push.setData(getJSONDataMessageForIntent(title, id, body, channelId));
        push.setChannel("notice");
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.toString());
                } else {
                    Log.e(TAG, "Success : " + title);
                }

            }
        });

    }

    private static JSONObject getJSONDataMessageForIntent(String title, String id, String body, String channelId) {
        JSONObject data = new JSONObject();
        try {
            data.put("action", ACTION);
            data.put("message", title);
            data.put("noticeId", id);
            data.put("body", body);
            data.put("channelId", channelId);
            return data;
        } catch (JSONException x) {
            Log.e(TAG, x.toString());
            return data;
        }
    }
}
