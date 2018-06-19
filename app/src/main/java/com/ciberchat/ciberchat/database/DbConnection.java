package com.ciberchat.ciberchat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by inmobitec on 6/15/18.
 */

public class DbConnection extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ciber_chat.db";
    //Table Names
    public static final String TABLE_USER = "user";
    public static final String TABLE_MESSAGE = "message";

    //common column names
    public static final String COLUMN_ID= "id";
    //USER Table column names
    public static final String COLUMN_USERNAME= "username";
    public static final String COLUMN_CIBERTECID= "cibertec_id";
    //MESSAGE Table column names
    public static final String COLUMN_FROMUSER= "from_username";
    public static final String COLUMN_FROMCIBER="from_userciber";
    public static final String COLUMN_TEXT="text";
    public static final String COLUMN_RECEIVED_AT="received_at";

    public static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_USERNAME
            + " TEXT," + COLUMN_CIBERTECID + " TEXT" + ")";

    private static final String CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGE
            + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_FROMUSER+ " TEXT,"
            + COLUMN_FROMCIBER+ " TEXT,"
            + COLUMN_TEXT+ " TEXT,"
            + COLUMN_RECEIVED_AT + " DATETIME" + ")";

    public DbConnection(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //String TABLA = "create table usuario(user_id integer primary key, username text, cibertec_id text);";
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_MESSAGE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        // create new tables
        onCreate(db);
    }
}
