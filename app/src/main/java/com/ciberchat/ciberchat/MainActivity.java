package com.ciberchat.ciberchat;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ciberchat.ciberchat.database.Contract;
import com.ciberchat.ciberchat.database.Db;
import com.ciberchat.ciberchat.database.DbConnection;
import com.ciberchat.ciberchat.database.MyDbHelper;
import com.ciberchat.ciberchat.database.model.Message;
import com.ciberchat.ciberchat.database.model.User;
import com.ciberchat.ciberchat.helper.Sockets;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Controls for main activity
    MessageAdapter messageAdapter, search_msgAdapter;
    Button sendButton, searchButton, exit_searchButton;
    EditText  messageInput, searchInput;
    ListView messagesView, found_messagesView;
    ArrayList<Message> last_messages;

    TextView username_Tv;

    String logged_cibertecid, logged_firstname, logged_lastname;

    //Controls for login activity
//    EditText cibertecid_input;
//    Button ciberchat_login;


    private Sockets socketIO;

    private JSONArray table;
    //private Db db;
    private MyDbHelper db;
    private DbConnection dbc;
    Boolean login = Boolean.FALSE;

    JSONArray users_array;
    JSONObject own_user;
    User self_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //initializing database
        db = new MyDbHelper(getApplicationContext());
        table = db.init();

        Log.v("TABLE:",String.valueOf(table));
        try {
            //check users table and see if there is a user there

            //Log.v("element 1:", String.valueOf(table.getJSONArray(0)));
            users_array = table.getJSONArray(0);
            own_user = users_array.getJSONObject(0);
            //Log.v("user 0", String.valueOf(users_array.getJSONObject(0)));
            if (own_user.getString(dbc.COLUMN_ID)!=""){
                Log.v("OWN_USER:" , String.valueOf(own_user));
                login = Boolean.TRUE;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

/*
        try{
            if (table.get(0))

        }catch(JSONException e){
            e.printStackTrace();
        }
*/

        if (login){
            setContentView(R.layout.activity_main);

            messageInput = (EditText) findViewById(R.id.message_input);
            sendButton = (Button) findViewById(R.id.send_button);

            searchInput = (EditText) findViewById(R.id.search_input);
            searchButton = (Button)findViewById(R.id.search_button);
            exit_searchButton = (Button)findViewById(R.id.search_exit);
            username_Tv = (TextView)findViewById(R.id.username);


            Bundle b = getIntent().getExtras();
            if(b!=null){
                logged_cibertecid = b.getString("cibertec_id");
                logged_firstname= b.getString("first_name");
                logged_lastname = b.getString("last_name");

                username_Tv.setText(logged_firstname + " " + logged_lastname);
            }else{
                try {
                    username_Tv.setText(own_user.getString(dbc.COLUMN_FIRSTNAME) + " "+ own_user.getString(dbc.COLUMN_LASTNAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                Log.v("OWN_USER:" , String.valueOf(own_user));
                self_user = new User(1,own_user.getString(dbc.COLUMN_CIBERTECID), own_user.getString(dbc.COLUMN_FIRSTNAME), own_user.getString(dbc.COLUMN_LASTNAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //self_user = new User(1,"cesarsv", "I201612011");
            //self_user = new User(1,"chulloMamani", "I201611015");
            //self_user = new User(1,"nelsonaPotter", "I201611020");


            //IF THERE IS A USERED LOGGED IN, THEN log him in...if there is not...then show the login Activity. This login activity will create or replace the only
            //user in the database

            //db.createUser(self_user);

            //CHAT WILL NOT LOAD MESSAGES BECAUSE ONE OF THE FUNCTIONALITIES IS THAT MESSAGES WILL DISAPPEAR AFTER 20 SECONDS
            //in any case, only the last UNSEEN message should be loaded --NEED TO ADD A COLUMN TO MESSAGE DATABASE

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

        }else{
            Log.v("NOT LOGGED IN:", "NOT LOGGED IN");

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

    }

    private void postMessage(){

        String text = messageInput.getText().toString();

        Log.v("OWN_USER:" , String.valueOf(own_user));

        if (text.equals("")){
            return;
        }

        //Log.v("text:", text);


        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate= df.format(c);
            //boolean own_msg = true;
            String user_name= self_user.getFirstname()+ " " + self_user.getLastname(); //Ex. Jose Jesus Salinas Vela

            final Message message = new Message(user_name, self_user.getCibertecid(), text, formattedDate);
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
            last_messages = db.createMessage(message);
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

            hideMessageTimer(last_message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void searchMessage(){
        String search_word = String.valueOf(searchInput.getText());
        Log.v("SEARCH: ", search_word);

        if (search_word!=""){
            final ArrayList<Message> found_messages = db.readMessageByText(search_word);

            //Log.v("FOUND MESSAGES: ", String.valueOf(found_messages));

            search_msgAdapter = new MessageAdapter(this, found_messages);
            found_messagesView = (ListView)findViewById(R.id.found_messages_view);
            found_messagesView.setAdapter(search_msgAdapter);

            //change List view to the one that shows found messages
            messagesView.setVisibility(View.GONE);
            found_messagesView.setVisibility(View.VISIBLE);

            //change search btn to exit button to go back to list view that has conversation
            searchButton.setVisibility(View.GONE);
            exit_searchButton.setVisibility(View.VISIBLE);

            exit_searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("EXIT BUTTON", "true");
                    found_messagesView.setVisibility(View.GONE);
                    messagesView.setVisibility(View.VISIBLE);
                    exit_searchButton.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    searchInput.setText("");
                }
            });

            for (int i=0; i<found_messages.size()-1; i++){
                //Code to see in console output of returned messages
                Log.v("msg id "+ found_messages.get(i).getId(), found_messages.get(i).getContent());
                ////////
                final int index = i;
                /*
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        search_msgAdapter.add(found_messages.get(index));
                    }
                });
                */
            }

        }else {
            Log.v("INPUT EMPTY", "Input Empty");
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMessage();
            }
        });


        //Following code is to make search dynamic after each key stroke
        /*
        searchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction()==keyEvent.ACTION_DOWN)){
                    Log.v("hoy", String.valueOf(searchInput.getText()));
                }

                return false;
            }
        });
        */

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
                        //If message is from other person, then show another message in UI
                        final Message incomingMessage = new Message(data.getString("from_username"), data.getString("from_userciber"), data.getString("content"), data.getString("received_at"));

                        //Save message to database
                        //final Message incoming_message = db.createMessage(incomingMessage);
                        last_messages = db.createMessage(incomingMessage);

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

                        hideMessageTimer(incoming_message);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void hideMessageTimer(final Message msg){

        //time_left = 10;

        /*
        final Handler handler = new Handler(Looper.getMainLooper());
*/


        //code to just make the message disappear

        /*
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.v("Hiding message", "Message id: "+String.valueOf(msg.getId()));
                        hideMessage(msg);
                    }
                },10000);
        */



        //code to show a timer and show it in UI so user know how much time is left for the msg to be deleted
        //only using different threads is missing on this code;
        /*
        final Runnable mShowTimerRunnable =  new Runnable() {
            @Override
            public void run() {
                Log.v("Time left:", String.valueOf(time_left));
                time_left--;
                if (time_left<=0){
                    Log.v("DONE:", "REMOVING IT");
                    handler.removeCallbacks(this);

                }else{
                    handler.postDelayed(this, 1000);
                }
            }
        };

        mShowTimerRunnable.run();
        */

        //Handler handler = new Handler(Looper.getMainLooper());

        //code to show a timer and show it in UI so user know how much time is left for the msg to be deleted
        Handler handler = new Handler(Looper.getMainLooper());
        callRunnable(handler, msg);
    }

    public void hideMessage(Message message){
        messageAdapter.remove(message);
    }


    public void callRunnable (final Handler handler, final Message msg){

        final Runnable runn= new Runnable() {
            int timer = 10;
            @Override
            public void run() {

                if (timer<=0){
                    //timer=10;
                    Log.v("Deleting msg:", String.valueOf(msg.getContent()));
                    hideMessage(msg);
                    handler.removeCallbacks(this);
                }else{
                    Log.v("DELETING IN: ", String.valueOf(timer));
                    //TextView timer_tv = (TextView)findViewById(R.id.)
                    timer--;
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(runn, 1000);
    }
}
