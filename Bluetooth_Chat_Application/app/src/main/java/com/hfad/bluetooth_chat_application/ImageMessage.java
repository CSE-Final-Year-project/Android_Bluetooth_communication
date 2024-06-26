package com.hfad.bluetooth_chat_application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageMessage extends MessageClass{
    String imageName;
    File myfile;
    byte[] imagebytes;
   // byte[] outStream;
    BitmapDataObject bitmap;
    Bitmap myBitmap;

    public ImageMessage(String messageType, String sender, String receiver,String imageName, File myfile,byte[] imagebytes) {
        super(messageType, sender, receiver);
      this.imageName=imageName;
      this.myfile=myfile;
      this.imagebytes=imagebytes;
    }
public Bitmap convertToBitmap(){
       myBitmap= BitmapFactory.decodeByteArray(imagebytes,0,imagebytes.length);
       return myBitmap;
}
    public static byte[] encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(compressFormat,quality,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public BitmapDataObject getBitmap() {
        return bitmap;
    }

    public ImageMessage(String messageType, String sender, String receiver, String imageName, File myfile, BitmapDataObject bitmap) {
        super(messageType, sender, receiver);
        this.imageName = imageName;
        this.myfile = myfile;
        this.bitmap = bitmap;
    }

    public ImageMessage(String messageType, String sender, String receiver, String imageName, File myfile) {
        super(messageType, sender, receiver);
        this.imageName = imageName;
        this.myfile = myfile;
    }

    public ImageMessage(String messageType, String sender, String receiver, String imageName, byte[] imagebyte) {
        super(messageType, sender, receiver);
        this.imageName = imageName;
        this.imagebytes=imagebytes;
    }

    public void setImagebytes(byte[] imagebyte) {
        this.imagebytes=imagebytes;
    }

    public byte[] getImagebytes() {
        return imagebytes;
    }

    public void setMyfile(File myfile) {
        this.myfile = myfile;
    }

    public File getMyfile() {
        return myfile;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


//    public ImageMessage(String messageType, String imageName, byte[]  outStream) {
//        super(messageType);
//        this.imageName = imageName;
//        this.outStream = outStream;
//    }

    public String getImageName() {
        return imageName;
    }

//    public byte[]  getOutStream() {
//        return outStream;
//    }

//    public void setOutStream(byte[]  outStream) {
//        this.outStream = outStream;
//    }
//
//    public ImageMessage(String messageType, String sender, String receiver, String imageName, byte[]  outStream) {
//        super(messageType, sender, receiver);
//        this.imageName = imageName;
//        this.outStream = outStream;
//    }
}
