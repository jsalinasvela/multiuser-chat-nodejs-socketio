package com.ciberchat.ciberchat.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ciberchat.ciberchat.database.model.Message;
import com.ciberchat.ciberchat.database.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by inmobitec on 6/15/18.
 */

public class MyDbHelper {

    private DbConnection dbc;
    private final SQLiteDatabase db_write,db_read;

    Context contexto;
    //public static final String TABLE_NAME = "ciber_chat.db";

    //instantiating connection and Writable and readable connection variables
    public MyDbHelper(Context context){
        contexto = context;
        dbc= new DbConnection(context);
        db_write = dbc.getWritableDatabase();
        db_read = dbc.getReadableDatabase();
    }

    public JSONArray init(){
        JSONArray data = new JSONArray();
        JSONArray data_users = readUsers();
        ArrayList<Message> data_messages = readMessages();
        data.put(data_users);
        data.put(data_messages);
        Log.v("INICIANDO", "length: "+data.length());
        return data;
    }

    public JSONArray createUser(User user){
        ContentValues values = new ContentValues();
        values.put(dbc.COLUMN_CIBERTECID, user.getCibertecid());
        values.put(dbc.COLUMN_FIRSTNAME, user.getFirstname());
        values.put(dbc.COLUMN_LASTNAME, user.getLastname());
        db_write.insert(dbc.TABLE_USER, null, values);
        return readUsers();
    }

    public JSONArray readUsers(){
        JSONArray users = new JSONArray();

        String selectQuery= "SELECT * FROM " + dbc.TABLE_USER;
        Cursor c = db_read.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do{
                User user = new User();
                JSONObject userJSON = new JSONObject();
                user.setId(c.getInt((c.getColumnIndex(dbc.COLUMN_ID))));
                user.setCibertecid(c.getString(c.getColumnIndex(dbc.COLUMN_CIBERTECID)));
                user.setFirstname((c.getString(c.getColumnIndex(dbc.COLUMN_FIRSTNAME))));
                user.setLastname((c.getString(c.getColumnIndex(dbc.COLUMN_LASTNAME))));

                try {
                    userJSON.put("id", user.getId());
                    userJSON.put("cibertecid", user.getCibertecid());
                    userJSON.put("first_name", user.getFirstname());
                    userJSON.put("last_name", user.getLastname());
                    users.put(userJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }while(c.moveToNext());
        }
        return users;
    }

    public JSONArray updateUser(User user){
        ContentValues values = new ContentValues();
        values.put(dbc.COLUMN_FIRSTNAME, user.getFirstname());
        values.put(dbc.COLUMN_LASTNAME, user.getLastname());

        db_write.update(dbc.TABLE_USER, values, dbc.COLUMN_ID+"= ?", new String[] {String.valueOf(user.getId())});
        //return updated table records
        return readUsers();
    }

    public ArrayList<Message> createMessage(Message message){
        ContentValues values = new ContentValues();
        //values.put(dbc.COLUMN_ID, message.getId());
        values.put(dbc.COLUMN_FROMUSER, message.getFrom_username());
        values.put(dbc.COLUMN_FROMCIBER, message.getFrom_userciber());
        values.put(dbc.COLUMN_TEXT, message.getContent());
        values.put(dbc.COLUMN_RECEIVED_AT, message.getReceived_at());
        long message_id= db_write.insert(dbc.TABLE_MESSAGE, null, values);
        //Log.v("MESSAGE_ID: ", String.valueOf(message_id));
        return readMessages();
    }

    public Message readLastMessage(){
        //JSONObject messageJSON = new JSONObject();
        //ArrayList<Message> messageArray = new ArrayList<Message>();
        String selectQuery= "SELECT * FROM " + dbc.TABLE_MESSAGE + " ORDER BY "+dbc.COLUMN_RECEIVED_AT +" DESC LIMIT 1";
        Cursor c = db_read.rawQuery(selectQuery, null);
        Message message = new Message();

        if (c.moveToFirst()){
            do{
                message.setId(c.getInt((c.getColumnIndex(dbc.COLUMN_ID))));
                message.setFrom_username((c.getString(c.getColumnIndex(dbc.COLUMN_FROMUSER))));
                message.setFrom_userciber(c.getString(c.getColumnIndex(dbc.COLUMN_FROMCIBER)));
                message.setContent(c.getString(c.getColumnIndex(dbc.COLUMN_TEXT)));
                message.setReceived_at(c.getString(c.getColumnIndex(dbc.COLUMN_RECEIVED_AT)));
                //messageArray.add(message);
                /*
                try {
                    messageJSON.put("id", message.getId());
                    messageJSON.put("from_username", message.getFrom_username());
                    messageJSON.put("from_userciber", message.getFrom_userciber());
                    messageJSON.put("content", message.getContent());
                    messageJSON.put("received_at", message.getReceived_at());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
            }while(c.moveToNext());
        }
        return message;
    }

    public ArrayList<Message> readMessageByText(String search_word){

        ArrayList<Message> messageArray = new ArrayList<Message>();

        String selectQuery = "SELECT * FROM " + dbc.TABLE_MESSAGE + " WHERE " + dbc.COLUMN_TEXT + " LIKE ?";
        String modif_search_word = "%"+search_word+"%";
        String[] selectionArgs = {modif_search_word};

        Log.v("SELECT QUERY:", selectQuery);

        Cursor c = db_read.rawQuery(selectQuery, selectionArgs);

        if (c != null){
            c.moveToFirst();
            Log.v("C:", String.valueOf(c.getCount()));
            Log.v("c.getColumnIndex:", String.valueOf(c.getColumnIndex(dbc.COLUMN_ID)));
            Log.v("c.getInt:", String.valueOf(c.getColumnIndex(dbc.COLUMN_ID)));

            if (c.moveToFirst()){
                do {
                    Message message = new Message();
                    //JSONObject messageJSON = new JSONObject();
                    message.setId(c.getInt((c.getColumnIndex(dbc.COLUMN_ID))));
                    message.setFrom_username((c.getString(c.getColumnIndex(dbc.COLUMN_FROMUSER))));
                    message.setFrom_userciber(c.getString(c.getColumnIndex(dbc.COLUMN_FROMCIBER)));
                    message.setContent(c.getString(c.getColumnIndex(dbc.COLUMN_TEXT)));
                    message.setReceived_at(c.getString(c.getColumnIndex(dbc.COLUMN_RECEIVED_AT)));

                    messageArray.add(message);
                }while(c.moveToNext());
            }
        }
        return messageArray;
    }

    public ArrayList<Message> readMessages(){
        //JSONArray messages = new JSONArray();
        ArrayList<Message> messageArray = new ArrayList<Message>();

        String selectQuery= "SELECT * FROM " + dbc.TABLE_MESSAGE;
        Cursor c = db_read.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do{
                Message message = new Message();
                //JSONObject messageJSON = new JSONObject();
                message.setId(c.getInt((c.getColumnIndex(dbc.COLUMN_ID))));
                message.setFrom_username((c.getString(c.getColumnIndex(dbc.COLUMN_FROMUSER))));
                message.setFrom_userciber(c.getString(c.getColumnIndex(dbc.COLUMN_FROMCIBER)));
                message.setContent(c.getString(c.getColumnIndex(dbc.COLUMN_TEXT)));
                message.setReceived_at(c.getString(c.getColumnIndex(dbc.COLUMN_RECEIVED_AT)));

                messageArray.add(message);
                /*
                try {
                    messageJSON.put("id", message.getId());
                    messageJSON.put("from_username", message.getFrom_username());
                    messageJSON.put("from_userciber", message.getFrom_userciber());
                    messageJSON.put("content", message.getContent());
                    messageJSON.put("received_at", message.getReceived_at());
                    //adding to messageJSON
                    messages.put(messageJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                } */

            }while(c.moveToNext());
        }

        return messageArray;
    }

    public ArrayList<Message> udpateMessage(Message message){
        ContentValues values = new ContentValues();
        values.put(dbc.COLUMN_TEXT, message.getContent());

        db_write.update(dbc.TABLE_MESSAGE, values, dbc.COLUMN_ID+"= ?", new String[] {String.valueOf(message.getId())});
        //return updated table records
        return readMessages();
    }

    /*
    public JSONObject read(String table_name, String[] return_columns,){
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
    */
/*
    public JSONObject create(String tableName, ContentValues values) throws JSONException {
        db_write.insert(tableName, null, values);
        return read();
    }
*/
    /*
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
    */
}
