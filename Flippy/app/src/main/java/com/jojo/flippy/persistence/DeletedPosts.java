package com.jojo.flippy.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bright on 7/23/14.
 */
@DatabaseTable(tableName = "deletedPostsTable")
public class DeletedPosts {

    @DatabaseField(id = true)
    public String post_id;


    public DeletedPosts() {
    }

    public DeletedPosts(String post_id) {
        this.post_id = post_id;
    }

    @Override
    public String toString() {
        return this.post_id;
    }
}
