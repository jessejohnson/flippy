package com.jojo.flippy.persistence;

/**
 * Created by bright on 6/20/14.
 */

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "flippy.db";
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the SimpleData table
    private Dao<User, Integer> userDao = null;
    private Dao<Post, Integer> postDao = null;
    private Dao<Channels, Integer> channelDao = null;
    private RuntimeExceptionDao<User, Integer> simpleRuntimeUserDao = null;
    private RuntimeExceptionDao<Post, Integer> simpleRuntimePostDao = null;
    private RuntimeExceptionDao<Channels, Integer> simpleRuntimeChannelDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Post.class);
            TableUtils.createTable(connectionSource, Channels.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Post.class, true);
            TableUtils.dropTable(connectionSource, Channels.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    public Dao<Post, Integer> getPostDao() throws SQLException {
        if (postDao == null) {
            postDao = getDao(Post.class);
        }
        return postDao;
    }

    public Dao<Channels, Integer> getChannelDao() throws SQLException {
        if (channelDao == null) {
            channelDao = getDao(Channels.class);
        }
        return channelDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<User, Integer> getSimpleDataUserDao() {
        if (simpleRuntimeUserDao == null) {
            simpleRuntimeUserDao = getRuntimeExceptionDao(User.class);
        }
        return simpleRuntimeUserDao;
    }

    public RuntimeExceptionDao<Post, Integer> getSimplePostDataDao() {
        if (simpleRuntimePostDao == null) {
            simpleRuntimePostDao = getRuntimeExceptionDao(Post.class);
        }
        return simpleRuntimePostDao;
    }

    public RuntimeExceptionDao<Channels, Integer> getSimpleChannelDataDao() {
        if (simpleRuntimeChannelDao == null) {
            simpleRuntimeChannelDao = getRuntimeExceptionDao(Channels.class);
        }
        return simpleRuntimeChannelDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        userDao = null;
        postDao = null;
        channelDao = null;
        simpleRuntimeUserDao = null;
        simpleRuntimePostDao = null;
        simpleRuntimeChannelDao = null;
    }
}
