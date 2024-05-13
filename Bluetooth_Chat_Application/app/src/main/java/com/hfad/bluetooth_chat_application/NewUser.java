package com.hfad.bluetooth_chat_application;

import android.bluetooth.BluetoothDevice;
import android.widget.Button;

public class NewUser {
    public Button button;
    BluetoothDevice bluetoothDevice;
    String devicename;

    public NewUser(Button button, BluetoothDevice bluetoothDevice) {
        this.button = button;
        this.bluetoothDevice = bluetoothDevice;
    }

    public NewUser(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public Button getButton() {
        return button;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getDevicename() {
        return devicename;
    }
}
