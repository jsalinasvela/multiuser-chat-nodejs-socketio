package com.ciberchat.ciberchat.database;

import android.provider.BaseColumns;

/**
 * Created by inmobitec on 6/14/18.
 */

public class Contract {

    private Contract() {
    }

    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "ciberchat";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER = "user"; //json

    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Entry.TABLE_NAME + " (" +
            Entry.COLUMN_ID + " INTEGER PRIMARY KEY," +
            Entry.COLUMN_USER + " TEXT " +
            ")";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

}
