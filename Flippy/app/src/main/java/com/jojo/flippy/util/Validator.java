package com.jojo.flippy.util;

public class Validator {

    //Validator
    private static final String NAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+\n" +
            "(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    public static boolean isValidNameString(String nameString){
       return nameString.matches(NAME_PATTERN);
    }

    public static boolean isValidEmail(String email){
        return email.matches(EMAIL_PATTERN);
    }


    public static boolean isValidPassword(String password){
        return password.matches(PASSWORD_PATTERN);
    }

    public static boolean isValidEmailOrPhoneNumber(String emailOrPhoneNumber){
        return isValidEmail(emailOrPhoneNumber);
    }
}
