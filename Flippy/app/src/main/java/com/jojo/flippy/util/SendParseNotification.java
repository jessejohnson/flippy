package com.jojo.flippy.util;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bright on 8/8/14.
 */
public class SendParseNotification {

    public static void sendMessage() {
        ParseQuery<ParseInstallation> userQuery = ParseInstallation.getQuery();
        JSONObject data = null;
        try {
            data = new JSONObject("{\"title\" : \"Hush!\"," +
                    "\"intent\" : \"PushedNotices\"," +
                    "\"action\" : \"com.jojo.flippy.app.PUSH_NOTIFICATION\"," +
                    "\"chatId\" :" + 1 + "}");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParsePush push = new ParsePush();
        push.setQuery(userQuery);
        push.setData(data);
        push.setMessage("New notice sent");
    }
}
