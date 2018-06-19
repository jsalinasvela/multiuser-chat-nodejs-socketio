package com.ciberchat.ciberchat.helper;

import android.content.Context;

import com.ciberchat.ciberchat.R;
import com.ciberchat.ciberchat.database.Contract;
import com.ciberchat.ciberchat.database.Db;
import com.ciberchat.ciberchat.database.MyDbHelper;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by inmobitec on 6/14/18.
 */

public class Sockets {

    public Socket socketio;
    private JSONArray table;
    private MyDbHelper db;

    public Sockets(Context context){

        db = new MyDbHelper(context);
        table = db.readUsers();

        try {
            /*
            if(!table.getString(Contract.Entry.COLUMN_USER).isEmpty()){
                IO.Options opt = new IO.Options();
                //opt.query = "model=a&movil=b";
                opt.secure = true;

                //socketio = IO.socket("http://192.168.1.70:1103" , opt);
                socketio = IO.socket(context.getString(R.string.SOCKET) , opt);
                //conecto a Socketio
                //ConnectSocket();
                //checkeo si es que hay conexion a SocketIO
                SocketListen();

            }
            */

            Log.v("TABLE: ", table.toString());

            //if(!table.getString(Contract.Entry.COLUMN_USER).isEmpty()) {
              if(!table.getJSONObject(0).getString("id").isEmpty()){
                //to do this, user has to have registered in the app
                Log.v("OTRA VEZ", table.getJSONObject(0).getString("id"));
                Log.v("AQUI", "estoy aqui");

                IO.Options opt = new IO.Options();
                //opt.query = "model=a&movil=b";
                opt.secure = true;

                //socketio = IO.socket("http://192.168.1.70:1103" , opt);
                socketio = IO.socket(context.getString(R.string.SOCKET), opt);
                //conecto a Socketio
                //ConnectSocket();
                //checkeo si es que hay conexion a SocketIO
                SocketListen();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            //Debug.send(context, Helper.getDeviceName()+">Serving>onCreate>SocketIO", e.toString());
        }
    }

    private void SocketListen(){
        socketio.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("Socket Listen", "conecto");

                /*
                try {
                    JSONObject user = new JSONObject(table.getString(Contract.Entry.COLUMN_USER));
                    Log.v("Socket Listen", "paso");
                    socketio.emit("room", "s"+user.getString("social_id"));
                } catch (JSONException e) {
                    //Debug.send(getApplicationContext(),Helper.getDeviceName()+">Serving>SocketListen>connect", e.toString());
                    Log.v("Socket Listen", "error conection"+e.toString());
                }
                */
            }
        });
    }

}
