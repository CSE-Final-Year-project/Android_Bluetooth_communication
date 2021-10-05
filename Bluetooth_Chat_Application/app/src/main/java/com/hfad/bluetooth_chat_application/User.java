package com.hfad.bluetooth_chat_application;

import android.widget.TextView;

public class User {
    public  String username;
   //public TextView Usertextview;
    public String macaddress;
    public User(String username,String macaddress){
        this.username=username;
        this.macaddress=macaddress;
    }

    public User() {

    }

    public String getName(){
        return username;
    }
    public void setName(String username){
        this.username=username;
    }

}
