package com.jojo.flippy.persistence;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bright on 7/23/14.
 */
@DatabaseTable(tableName = "channelTable")
public class Channels {

    @DatabaseField(id = true)
    public String channel_id;


    public Channels() {
    }

    public Channels(String channel_id) {
        this.channel_id = channel_id;
    }

    @Override
    public String toString() {
        return this.channel_id;
    }
}
