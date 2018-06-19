package com.ciberchat.ciberchat.database.model;

/**
 * Created by inmobitec on 6/15/18.
 */

public class User {
    int id;
    String username;
    String cibertecid;

    //Constructors
    public User(){
    }

    public User(int id, String username, String cibertecid){
        this.id = id;
        this.username=username;
        this.cibertecid = cibertecid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCibertecid(){
        return cibertecid;
    }

    public void setCibertecid(String cibertecid) {
        this.cibertecid = cibertecid;
    }
}
