package com.ciberchat.ciberchat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by inmobitec on 6/14/18.
 */

public class Db {
    private final DbHelper dbh;
    private final SQLiteDatabase db_write,db_read;

    Context contexto;

    public Db(Context context) {
        contexto = context;
        dbh = new DbHelper(context);
        db_write = dbh.getWritableDatabase();
        db_read = dbh.getReadableDatabase();
    }

    public JSONObject init(){

        JSONObject data = read();

        //Log.v("INICIANDO", "length: "+data.length());

        try {

            //Checking if database exists

            if (data.length()<=0){

                ContentValues values = new ContentValues();
                values.put(Contract.Entry.COLUMN_USER, "");

                //inserting records
                data = create(values);


                //Log.v("INICIANDO", "CREADO: "+data.toString(1));
            }else{
                //Log.v("INICIANDO", "OBTENIDO: "+data.toString(1));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    public JSONObject create(ContentValues values) throws JSONException {
        db_write.insert(Contract.Entry.TABLE_NAME, null, values);
        return read();
    }

    public JSONObject read(){
        String[] projection = {
                Contract.Entry.COLUMN_ID,
                Contract.Entry.COLUMN_USER,
        };

        String selection = Contract.Entry.COLUMN_ID + " = ?";
        String[] selectionArgs = { "1" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = Contract.Entry.COLUMN_ID + " DESC";

        Cursor c = db_read.query(
                Contract.Entry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return getData(c);
    }

    public JSONObject update(ContentValues values){
        //Log.v("LOGIN dbupdate",uloveContract.uloveEntry.COLUMN_ID);
        db_read.update(Contract.Entry.TABLE_NAME, values, Contract.Entry.COLUMN_ID+"=1", null);

        JSONObject data = read();
        return data;
    }

    public JSONObject getData(Cursor cursor){
        //ArrayList list = new ArrayList();
        JSONObject values = new JSONObject();

        //Log.v("GETDATA","PASO");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //Log.v("GETDATA",cursor.toString());
            values = setCursor(cursor);
        }

        cursor.close();

        return values;
    }

    public JSONObject setCursor(Cursor cursor){
        JSONObject c = new JSONObject();
        //Log.v("GETDATA","PASO - - -");

        try {
            c.put(Contract.Entry.COLUMN_ID, cursor.getString(0));
            c.put(Contract.Entry.COLUMN_USER, cursor.getString(1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }


}
