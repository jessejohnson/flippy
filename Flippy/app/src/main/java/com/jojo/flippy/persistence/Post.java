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

    @DatabaseField(columnName = "notice_subtitle")
    public String notice_subtitle;

    @DatabaseField(columnName = "notice_title")
    public String notice_title;

    @DatabaseField(columnName = "notice_body")
    public String notice_body;

    public Post(String notice_id, String notice_title, String notice_subtitle, String notice_body) {
        this.notice_id = notice_id;
        this.notice_subtitle = notice_subtitle;
        this.notice_title = notice_title;
        this.notice_body = notice_body;
    }

    Post() {
    }

    @Override
    public String toString() {
        return this.notice_title + " " + this.notice_id;
    }
}
