package com.hfad.bluetooth_chat_application;

import java.io.Serializable;

public class MessageClass implements Serializable {
   protected String messageType;
   protected String sender;
   protected String receiver;
    public MessageClass(String messageType) {
        this.messageType = messageType;
    }

    public MessageClass(String messageType, String sender, String receiver) {
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
