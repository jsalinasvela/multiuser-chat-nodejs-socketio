package com.ciberchat.ciberchat.database.model;

/**
 * Created by inmobitec on 6/15/18.
 */

public class User {
    int id;
    String firstname, lastname;
    String cibertecid;

    //Constructors
    public User(){
    }

    public User(int id, String cibertecid, String firstname, String lastname){
        this.id = id;
        this.cibertecid = cibertecid;
        this.firstname=firstname;
        this.lastname=lastname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCibertecid(){
        return cibertecid;
    }

    public void setCibertecid(String cibertecid) {
        this.cibertecid = cibertecid;
    }

    public String getFirstname(){ return firstname;}

    public void setFirstname(String firstname){ this.firstname= firstname; }

    public String getLastname() {return lastname;}

    public void setLastname(String lastname) {this.lastname = lastname;}
}
