package com.hfad.bluetooth_chat_application;

import java.io.File;

public class DocumentFileMessage extends MessageClass {
    String fileName;
    String fileExtention;
    File myfile;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }

    public void setMyfile(File myfile) {
        this.myfile = myfile;
    }

    public void setDocbytes(byte[] docbytes) {
        this.docbytes = docbytes;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExtention() {
        return fileExtention;
    }

    public File getMyfile() {
        return myfile;
    }

    public byte[] getDocbytes() {
        return docbytes;
    }

    public DocumentFileMessage(String messageType, String fileName, String fileExtention, File myfile, byte[] docbytes) {
        super(messageType);
        this.fileName = fileName;
        this.fileExtention = fileExtention;
        this.myfile = myfile;
        this.docbytes = docbytes;
    }

    public DocumentFileMessage(String messageType, String sender, String receiver, String fileName, String fileExtention, File myfile, byte[] docbytes) {
        super(messageType, sender, receiver);
        this.fileName = fileName;
        this.fileExtention = fileExtention;
        this.myfile = myfile;
        this.docbytes = docbytes;
    }

    byte[] docbytes;
    public DocumentFileMessage(String messageType) {
        super(messageType);
    }

    public DocumentFileMessage(String messageType, String sender, String receiver) {
        super(messageType, sender, receiver);
    }
}
