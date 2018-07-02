package com.ciberchat.ciberchat;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ciberchat.ciberchat.database.MyDbHelper;
import com.ciberchat.ciberchat.database.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by inmobitec on 6/14/18.
 */

public class MessageAdapter extends BaseAdapter{

    Context messageContext;
    List<Message> messageList;
    private JSONArray table;
    //private Db db;
    private MyDbHelper db;

    public MessageAdapter(Context context, List<Message>messages){
        messageList=messages;
        messageContext=context;

        db= new MyDbHelper(context);
        table = db.readUsers();

        Log.v("TABLE JSONARRAY:", String.valueOf(table));
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MessageViewHolder holder;
        String phone_cibertecid;



        if (convertView == null){
            LayoutInflater messageInflater = (LayoutInflater) messageContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = messageInflater.inflate(R.layout.message, null);

            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(135));
            /*
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)convertView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            */
            //params.addRule(RelativeLayout.LEFT_OF, R.id.id_to_be_left_of);

            holder = new MessageViewHolder();
            holder.messageBackground = (LinearLayout) convertView.findViewById(R.id.message_background);
            //holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.senderView = (TextView) convertView.findViewById(R.id.message_sender);
            holder.bodyView = (TextView) convertView.findViewById(R.id.message_body);
            holder.idView = (TextView) convertView.findViewById(R.id.message_id);

            convertView.setTag(holder);
        } else {
            holder = (MessageViewHolder) convertView.getTag();
        }

        Message message = (Message) getItem(position);

        Log.v("SENDER CIBERTECID:", message.getFrom_userciber());
        //obtaining phone's cibertec id and comparing it to the cibertecid of the sender
        try {
            Log.v("TRYING:", "ESTOY AQUI");
            phone_cibertecid = table.getJSONObject(0).getString("cibertecid");
            Log.v("PHONE CIBERTECID:", phone_cibertecid);


            //Following code is to add a TEXT VIEW that shows the TIME remaining for the message to be deleted
            /*
            TextView timer_tv = new TextView(messageContext);
            timer_tv.setTag("timer_"+String.valueOf(message.getId()));
            timer_tv.setGravity(Gravity.LEFT);
            timer_tv.setLayoutParams(new LinearLayout.LayoutParams(15, 15));
            RelativeLayout.LayoutParams myparams = (RelativeLayout.LayoutParams)timer_tv.getLayoutParams();
            myparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            myparams.addRule(RelativeLayout.CENTER_VERTICAL);
            timer_tv.setLayoutParams(myparams);

            holder.messageBackground.addView(timer_tv);
            */

            if (message.getFrom_userciber().equals(phone_cibertecid)){

                //message is user's own message
                //style the name of the user not to show up
                holder.senderView.setText("");

                if (Build.VERSION.SDK_INT < 23) {
                    //holder.bodyView.setTextAppearance(messageContext, R.style.boldText);
                } else {
                    //holder.bodyView.setTextAppearance(R.style.boldText);
                }
                //making cibertec_id disappear
                holder.senderView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

                //style to locate to the right of screen
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.messageBackground.getLayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }else{
                    //SETTING align_parent_right property of linearLayout to False
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                }

                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                holder.messageBackground.setLayoutParams(params);

                //style the background to show green
                //holder.messageBackground.setBackgroundResource(R.color.ownUserMessageBackgroundColor);
                holder.messageBackground.setBackgroundResource(R.drawable.layout_msg_own);

                //style the text of the message to show in black color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.bodyView.setTextAppearance(R.style.ownUserText);
                }else{
                    holder.bodyView.setTextAppearance(messageContext, R.style.ownUserText);
                }
                //set message to show up to the right of bubble
                holder.bodyView.setGravity(Gravity.RIGHT);


            }else{
                //message is from another user

                //style the cibertecid to show up in a random color
                holder.senderView.setText(message.getFrom_username());
                if (Build.VERSION.SDK_INT < 23) {
                    holder.senderView.setTextAppearance(messageContext, R.style.firstThirdUsername);
                } else {
                    holder.senderView.setTextAppearance(R.style.firstThirdUsername);
                }
                //making the cibertecid to show up
                holder.senderView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                LinearLayout.LayoutParams params_cibertecid = (LinearLayout.LayoutParams)holder.senderView.getLayoutParams();
                params_cibertecid.setMargins(15, 10,10,10);
                holder.senderView.setLayoutParams(params_cibertecid);
                //style to locate to the left of screen

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.messageBackground.getLayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                }else{
                    //SETTING align_parent_right property of linearLayout to False
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                }

                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                holder.messageBackground.setLayoutParams(params);

                //style the background of the message to show in a different color
                //holder.messageBackground.setBackgroundResource(R.color.thirdUserMessageBackgroundColor);
                holder.messageBackground.setBackgroundResource(R.drawable.layout_msg_third);

                //style the text of the message to show in black color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.bodyView.setTextAppearance(R.style.thirdUserText);
                }else{
                    holder.bodyView.setTextAppearance(messageContext, R.style.thirdUserText);
                }
                //set message to show up to the LEFT of bubble
                holder.bodyView.setGravity(Gravity.LEFT);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        holder.bodyView.setText(message.getContent());
        holder.idView.setText(String.valueOf(message.getId()));
        Log.v("MESSAGE ID: ", String.valueOf(holder.idView.getText()));
        Log.v("MESSAGE CONTENT: ", String.valueOf(holder.bodyView.getText()));
        return convertView;

    }

    public void add(Message message){
        messageList.add(message);
        Log.v("INCOMING MESSAGE:", message.getFrom_userciber());
        notifyDataSetChanged();
    }

    public void remove(Message message){
        messageList.remove(message);
        Log.v("DELETING MESSAGE", message.getContent());
        notifyDataSetChanged();
    }

    private static class MessageViewHolder{
        public LinearLayout messageBackground;
        public ImageView thumbnailImageView;
        public TextView senderView;
        public TextView bodyView;
        public TextView idView;
    }




}
