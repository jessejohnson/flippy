package com.jojo.flippy.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "userTable")
public class User {
    @DatabaseField(id = true)
    public String user_id;

    @DatabaseField(columnName = "user_auth", uniqueIndex = true)
    public String user_auth;

    @DatabaseField(columnName = "user_email", uniqueIndex = true)
    public String user_email;

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
    @DatabaseField(columnName = "community_id")
    public String community_id;

    @DatabaseField(columnName = "community_name")
    public String community_name;


    public User(String user_id, String user_auth, String user_email, String first_name, String last_name) {
        this.user_id = user_id;
        this.user_auth = user_auth;
        this.user_email = user_email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public User(String user_id, String user_auth, String user_email, String first_name, String last_name, String avatar, String avatar_thumb, String gender, String date_of_birth) {
        this.user_id = user_id;
        this.user_auth = user_auth;
        this.user_email = user_email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.avatar = avatar;
        this.avatar_thumb = avatar_thumb;
    }

    public User(String user_id, String user_auth, String user_email, String first_name, String last_name, String avatar, String avatar_thumb, String gender, String date_of_birth, String community_id) {
        this.user_id = user_id;
        this.user_auth = user_auth;
        this.user_email = user_email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.avatar = avatar;
        this.avatar_thumb = avatar_thumb;
        this.community_id = community_id;
    }

    User() {
    }

    @Override
    public String toString() {
        return this.user_email + " " + this.user_auth;
    }
}
