package com.jojo.flippy.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bright on 6/20/14.
 */
@DatabaseTable(tableName = "userTable")
public class User {
    @DatabaseField(columnName = "id", uniqueIndex = true)
    public String id;

    @DatabaseField(columnName = "user_auth", uniqueIndex = true)
    public String user_auth;

    @DatabaseField(columnName = "user_email")
    public String user_email;

    @DatabaseField(columnName = "user_number")
    public String user_number;

    @DatabaseField(columnName = "first_name")
    public String first_name;

    @DatabaseField(columnName = "last_name")
    public String last_name;

    @DatabaseField(columnName = "gender")
    public String gender;

    @DatabaseField(columnName = "avatar_thumb")
    public String avatar_thumb;

    @DatabaseField(columnName = "avatar")
    public String avatar;

    @DatabaseField(columnName = "date_of_birth")
    public String date_of_birth;


    public User(String id, String user_auth, String user_email, String first_name, String last_name) {
        this.id = id;
        this.user_auth = user_auth;
        this.user_email = user_email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    User() {
    }

    @Override
    public String toString() {
        return this.user_email;
    }
}
