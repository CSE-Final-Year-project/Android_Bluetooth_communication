package com.hfad.bluetooth_chat_application;

public class StringMessage extends MessageClass {
    String message;
    public StringMessage(String messageType) {
        super(messageType);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public StringMessage(String messageType, String message) {
        super(messageType);
        this.message = message;
    }

    public StringMessage(String messageType, String sender, String receiver, String message) {
        super(messageType, sender, receiver);
        this.message = message;
    }
}
