package com.jojo.flippy.util;

/**
 * Created by bright on 7/28/14.
 */
public class StripCharacter {

    public static String getFirstLetter(String name) {
        String letter;
        if (name == null || name == "") {
            letter = "";
        } else {
            letter = name.trim().substring(0, 1).toUpperCase();
        }
        return letter;
    }
}
