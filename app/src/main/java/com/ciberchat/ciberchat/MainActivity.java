package com.ciberchat.ciberchat;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ciberchat.ciberchat.database.Contract;
import com.ciberchat.ciberchat.database.Db;
import com.ciberchat.ciberchat.database.MyDbHelper;
import com.ciberchat.ciberchat.database.model.Message;
import com.ciberchat.ciberchat.database.model.User;
import com.ciberchat.ciberchat.helper.Sockets;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    MessageAdapter messageAdapter;
    Button sendButton;
    EditText  messageInput;
    ListView messagesView;
    private Sockets socketIO;

    private JSONArray table;
    //private Db db;
    private MyDbHelper db;

    User self_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing database
        db = new MyDbHelper(getApplicationContext());
        table = db.init();

        messageInput = (EditText) findViewById(R.id.message_input);
        sendButton = (Button) findViewById(R.id.send_button);

        //self_user = new User(1,"jesuschali", "I201612010");
        self_user = new User(1,"cesarsv", "I201612011");
        db.createUser(self_user);

        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        messagesView = (ListView)findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);


        /*
        JSONObject user = new JSONObject();
        try {
            user.put("id", 1);
            user.put("user_name", "jesus");
            user.put("cibertec_id", "I201612010");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.v("ESTADO login facebook - response", "" + user.toString());

        ContentValues values = new ContentValues();
        values.put(Contract.Entry.COLUMN_USER, user.toString());
        db.update(values);
        */

        events_clicks();
/*
        try {
            self_user= new JSONObject(table.getString(Contract.Entry.COLUMN_USER));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        socketIO = new Sockets(getApplicationContext());
        socketIO.socketio.connect();
        socketListen();

    }

    private void postMessage(){

        String text = messageInput.getText().toString();

        if (text.equals("")){
            return;
        }

        //Log.v("text:", text);


        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate= df.format(c);
            //boolean own_msg = true;

            final Message message = new Message(self_user.getUsername(), self_user.getCibertecid(), text, formattedDate);
            JSONObject messageJSON = new JSONObject();
            messageJSON.put("from_username", message.getFrom_username());
            messageJSON.put("from_userciber", message.getFrom_userciber());
            messageJSON.put("content", message.getContent());
            messageJSON.put("received_at", message.getReceived_at());
            //messageJSON.put("own_message", message.isOwn_message());
            //emit message to server
            socketIO.socketio.emit("sentMessage", messageJSON);

            //clear input
            messageInput.setText("");

            //Log.v("MessageJSON: ", messageJSON.toString());
            //show message in interface and wait for confirmation
            //messageAdapter.add(message);

            //Save message to local database and get as a result the id that has been given to that meesage and add that to the interface
            ArrayList<Message> last_messages = db.createMessage(message);
            //final Message last_message = db.createMessage(message);
            //Log.v("ALL MESSAGES: ", String.valueOf(last_messages));
            //Log.v("Last MESSAGE: ", String.valueOf(last_messages.get(last_messages.size()-1).getContent()));

            //give a style to message so that it can be shown to the left of the screen


            final Message last_message = last_messages.get(last_messages.size()-1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(last_message);
                    messagesView.setSelection(messageAdapter.getCount() - 1);
                }

            });



        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void events_clicks(){

        //Assign event to SEND button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postMessage();
            }
        });
    }

    private void socketListen(){

        socketIO.socketio.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final JSONObject data = (JSONObject) args[0];
                Log.v("SOCKET received: ", data.toString());
                /*
                data is in this format
                {"from_username":"cesarsv93", "from_userciber":"I201612015",
                "content":"Hello world","received_at":"18-Jun-2018"}
                 */


                try {
                    Log.v("FROM: ", data.getString("from_username"));
                    Log.v("FROM_CIBER: ", data.getString("from_userciber"));
                    Log.v("MESSAGE: ", data.getString("content"));

                    //JSONObject user = new JSONObject(table.getString(Contract.Entry.COLUMN_USER));
                    //Log.v("USER: ", user.getString("user_name"));

                    if (self_user.getCibertecid().equals(data.getString("from_userciber"))){
                        //If message is my own, then show a first check mark the the server received it
                        Log.v("SENT: ","OK");

                        //TODO: SAVE STATE OF SENT MESSAGE IN DATABASE

                        //

                    }else {
                        //If message is is from other person, then show another message in UI
                        final Message incomingMessage = new Message(data.getString("from_username"), data.getString("from_userciber"), data.getString("content"), data.getString("received_at"));

                        //Save message to database
                        //final Message incoming_message = db.createMessage(incomingMessage);
                        ArrayList<Message> last_messages = db.createMessage(incomingMessage);

                        //Log.v("ALL MESSAGES inc :", String.valueOf(last_messages));
                        //Log.v("Last MESSAGE inc: ", String.valueOf(last_messages.get(last_messages.size()-1).getContent()));

                        final Message incoming_message = last_messages.get(last_messages.size()-1);

                        //give a style to message so that it can be shown to the right of screen;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.add(incoming_message);
                                messagesView.setSelection(messageAdapter.getCount() - 1);
                            }

                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
