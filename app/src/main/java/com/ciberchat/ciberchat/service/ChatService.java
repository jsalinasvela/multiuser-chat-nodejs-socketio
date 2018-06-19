package com.ciberchat.ciberchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

/**
 * Created by inmobitec on 6/14/18.
 */

public class ChatService extends Service{

    private JSONObject table;
    private Socket socketio;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        //Toast.makeText(this,"PASO ONBIND",Toast.LENGTH_LONG).show();
        return null;
    }



}
