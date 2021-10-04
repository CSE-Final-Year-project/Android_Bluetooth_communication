package com.hfad.bluetooth_chat_application;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

public class Dashboard extends AppCompatActivity {
    private static final int REQUEST_DISCOVERY = 1;
    Map<String,String> PairedList=new HashMap<String,String>();
    public  final BluetoothAdapter mybluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    Map<String,String> NewDevices=new HashMap<String,String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        turnonBlutooth();
        setContentView(R.layout.activity_dashboard);
        ListView devicelist=(ListView) findViewById(R.id.paireddevlist);
        ToggleButton Scan=(ToggleButton) findViewById(R.id.scan);
//        LayoutInflater inflater=getLayoutInflater();
//        View myview=inflater.inflate(R.layout.activity_dashboard,null);
        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver,filter);
                mybluetoothAdapter.startDiscovery();
            }
        });
        ArrayList<BluetoothDevice> pairedDevices=(ArrayList<BluetoothDevice>) getIntent().getExtras().get("PairedDevices");
        ArrayList<String> DeviceArray = new ArrayList<>();
        //there are paired devices. get the name and addresses of each paired device
        for (BluetoothDevice device : pairedDevices) {
            String devicename = device.getName();
            String deviceMacAddress = device.getAddress();
            PairedList.put(devicename,deviceMacAddress);
            DeviceArray.add(devicename);
            Log.d("Paired Devices: ", devicename + "  with address " + deviceMacAddress);

        }
        ArrayAdapter<String> DeviceAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                DeviceArray);
        Switch mySwitch=(Switch) findViewById(R.id.bluetooth_enabling);
        devicelist.setAdapter(DeviceAdapter);
        onSwitchClicked(mySwitch);
    }
    private final BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Resitering ","Bluetooth event listern register");
            Toast.makeText(getApplicationContext(), "Bluetooth event listern register", Toast.LENGTH_SHORT).show();
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //Discovering has found a device. get the bluetoothDevice object and its info its info from the intent
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String devicename=device.getName();
                String deviceMacAddress=device.getAddress();
                NewDevices.put(devicename,deviceMacAddress);
                Log.d("discovable Devices: ",devicename+"  with address "+deviceMacAddress);

            }
        }
    };
    public  void turnonBlutooth(){
//    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//    registerReceiver(receiver, filter);
        mybluetoothAdapter.startDiscovery();
        if (mybluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "BLUETOOTH NOT SUPPORTED TO DIS DEVICE", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mybluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_DISCOVERY);

            //Log.d(" Is it on ?: "," "+mybluetoothAdapter.isEnabled());
        }
        if (!mybluetoothAdapter.isDiscovering()) {
            Toast.makeText(getApplicationContext(), "Making your device discoverable", Toast.LENGTH_SHORT).show();
            Intent enablediscoveIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(enablediscoveIntent,REQUEST_DISCOVERY);

        }

    }

    public void onSwitchClicked(View view) {
       // Switch oldswitch=(Switch) findViewById(R.id.bluetooth_enable);
        boolean Ischecked = ((Switch) view).isChecked();
        if (Ischecked) {
            turnonBlutooth();

        }
        else
        {
           // oldswitch.setEnabled(false);
            mybluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "TURNING_OFF BLUETOOTH!!", Toast.LENGTH_SHORT).show();

        }

    }
}