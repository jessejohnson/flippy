package com.jojo.flippy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bright on 6/4/14.
 */
public class Validator {

    //Validator regexes
    private static final String NAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+\n" +
            "(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private static final String PHONE_NUMBER_PATTERN = "\\d{10}"; //0240216169

    public static boolean validateNameString(String nameString){
       return nameString.matches(NAME_PATTERN);
    }

    public static boolean validateEmail(String email){
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean validatePhoneNumber(String phoneNumber){
        return phoneNumber.matches(PHONE_NUMBER_PATTERN);
    }

    public static boolean validatePassword(String password){
        return password.matches(PASSWORD_PATTERN);
    }

    public static boolean validateEmailOrPhoneNumber(String emailOrPhoneNumber){
        return validateEmail(emailOrPhoneNumber) || validatePhoneNumber(emailOrPhoneNumber);
    }
}
