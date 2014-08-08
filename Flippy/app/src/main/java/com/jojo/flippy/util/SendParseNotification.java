package com.jojo.flippy.util;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bright on 8/8/14.
 */
public class SendParseNotification {
    public static final String ACTION = "com.jojo.flippy.app.PUSH_NOTIFICATION";
    public static final String TAG = "SendParseNotification";

    public static void sendMessage(final String title, String id) {
        ParsePush push = new ParsePush();
        push.setData(getJSONDataMessageForIntent(title, id));
        push.setMessage(title);
        push.setChannel("notice");
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("Error", e.toString());
                } else {
                    Log.e("Success", title);
                }

            }
        });

    }

    private static JSONObject getJSONDataMessageForIntent(String title, String id) {
        JSONObject data = new JSONObject();
        try {
            //Notice alert is not required
            //data.put("alert", content);
            //instead action is used
            data.put("action", ACTION);
            data.put("intent", "PushedNotices");
            data.put("title", title);
            data.put("chatId", id);
            return data;
        } catch (JSONException x) {
            Log.e(TAG, x.toString());
            return data;
        }
    }
}
