package com.ciberchat.ciberchat.database.model;

/**
 * Created by inmobitec on 6/15/18.
 */

public class Message {
    int id;
    String from_username;
    String from_userciber;
    String content;
    String received_at;
    //boolean own_message;

    public Message(){

    }

    public Message(int id, String from_username, String from_userciber, String content, String received_at){
        this.id = id;
        this.from_username= from_username;
        this.from_userciber= from_userciber;
        this.content = content;
        this.received_at = received_at;
    }

    public Message(String from_username, String from_userciber, String content, String received_at){
        this.from_username= from_username;
        this.from_userciber= from_userciber;
        this.content = content;
        this.received_at = received_at;
    }

    /*
    public Message(String from_username, String from_userciber, String content, String received_at, boolean own_message){
        this.from_username= from_username;
        this.from_userciber= from_userciber;
        this.content = content;
        this.received_at = received_at;
        this.own_message = own_message;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom_username() {
        return from_username;
    }

    public void setFrom_username(String from_username) {
        this.from_username = from_username;
    }

    public String getFrom_userciber() {
        return from_userciber;
    }

    public void setFrom_userciber(String from_userciber) {
        this.from_userciber = from_userciber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceived_at() {
        return received_at;
    }

    public void setReceived_at(String received_at) {
        this.received_at = received_at;
    }

    /*
    public boolean isOwn_message() {
        return own_message;
    }

    public void setOwn_message(boolean own_message) {
        this.own_message = own_message;
    } */
}
