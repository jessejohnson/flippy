package com.jojo.flippy.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bright on 6/20/14.
 */
@DatabaseTable(tableName = "postTable")
public class Post {
    @DatabaseField(id = true)
    public String notice_id;

    @DatabaseField(columnName = "notice_title")
    public String notice_title;

    @DatabaseField(columnName = "notice_body")
    public String notice_body;

    @DatabaseField(columnName = "notice_image")
    public String notice_image;

    @DatabaseField(columnName = "channel_id")
    public String channel_id;

    @DatabaseField(columnName = "author_id")
    public String author_id;

    @DatabaseField(columnName = "author_email")
    public String author_email;

    @DatabaseField(columnName = "author_first_name")
    public String author_first_name;

    @DatabaseField(columnName = "author_last_name")
    public String author_last_name;

    @DatabaseField(columnName = "start_date")
    public String start_date;

    @DatabaseField(columnName = "local_id")
    public long local_id;


    public Post(String notice_id, String notice_title, String notice_body, String notice_image, String start_date, String author_email, String author_id, String author_first_name, String author_last_name, String channel_id, long local_id) {
        this.notice_id = notice_id;
        this.notice_title = notice_title;
        this.notice_body = notice_body;
        this.notice_image = notice_image;
        this.start_date = start_date;
        this.author_email = author_email;
        this.author_id = author_id;
        this.author_first_name = author_first_name;
        this.author_last_name = author_last_name;
        this.channel_id = channel_id;
        this.local_id = local_id;
    }

    Post() {
    }

    @Override
    public String toString() {
        return this.notice_title + " " + this.notice_id + this.notice_image;
    }
}
