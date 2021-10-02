package com.hfad.bluetooth_chat_application;

import static android.bluetooth.BluetoothProfile.GATT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    final BluetoothAdapter mybluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    boolean IsbluetoothOn=false;
    boolean IsTurningOn=false;
    Set<BluetoothDevice> pairedDevices=null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mytoolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
        TextView Initialsetup = (TextView)findViewById(R.id.initialsetup);
        SpannableString content = new SpannableString(Initialsetup.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        Initialsetup.setText(content);
        Switch bluetoothstatus=(Switch) findViewById(R.id.bluetooth_enable);
        Object Inflater=this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //if(bluetoothstatus.isChecked())
        onSwitchClicked(bluetoothstatus);
        View main=this.findViewById(R.id.main);
        AlertDialog.Builder build=new AlertDialog.Builder(this);
        build.setTitle("Chose the device you want");
        ListView devicelist=new ListView(this);

       pairedDevices=GetDevices();
        final Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               ArrayList<String> DeviceArray = new ArrayList<>();
                Dialog dialog = new Dialog(MainActivity.this);
                if (pairedDevices != null) {
                    for (BluetoothDevice device : pairedDevices) {
                        String devicename = device.getName();
                       DeviceArray.add(devicename);
                        String deviceMacAddress = device.getAddress();
                        Log.d("Paired Devicesmain: ", devicename + "  with address " + deviceMacAddress);

                    }
                    ArrayAdapter<String> DeviceAdapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            DeviceArray);;
                    devicelist.setAdapter(DeviceAdapter);
                    build.setView(devicelist);
                    dialog=build.create();
                    dialog.show();

//                    LayoutInflater inflater = (LayoutInflater)Inflater ;
//                    PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.popup_paired_devices, null, false), 100, 100, true);
//                    pw.showAtLocation(main, Gravity.CENTER, 0, 0);
                }
            }
        },20000);

    }
    private void SetDevices(Set<BluetoothDevice> devices){
        pairedDevices=devices;
    }
    private Set<BluetoothDevice> GetDevices(){
        return pairedDevices;
    }
//    private static String[] append(String[] arr,String element){
//        List<String> DeviceArray=new ArrayList<>(Arrays.asList(arr));
//        DeviceArray.add(element);
//        return DeviceArray.toArray(new String[0]);
//    }
    private final BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Resitering ","Bluetooth event listern register");
            Toast.makeText(getApplicationContext(), "Bluetooth event listern register", Toast.LENGTH_SHORT).show();
            String action=intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                //Discovering has found a device. get the bluetoothDevice object and its info its info from the intent
                int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_ON:
                        IsTurningOn=true;
                        Toast.makeText(getApplicationContext(), "Bluetooth is turned on...", Toast.LENGTH_SHORT).show();

                        break;

                }
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.ACTION_FOUND);
                String devicename=device.getName();
                String deviceMacAddress=device.getAddress();
                Log.d("discovable Devices: ",devicename+"  with address "+deviceMacAddress);

            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

    }
private void turnonBlutooth(){
    if (mybluetoothAdapter == null) {

        Toast.makeText(getApplicationContext(), "BLUETOOTH NOT SUPPORTED TO DIS DEVICE", Toast.LENGTH_SHORT).show();
    }

    if (!mybluetoothAdapter.isEnabled()) {

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBtIntent);

        //Log.d(" Is it on ?: "," "+mybluetoothAdapter.isEnabled());
    }
    if (!mybluetoothAdapter.isDiscovering()) {
        Toast.makeText(getApplicationContext(), "Making your device discoverable", Toast.LENGTH_SHORT).show();
        Intent enablediscoveIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(enablediscoveIntent);

    }

}
    public void onSwitchClicked(View view) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        boolean Ischecked = ((Switch) view).isChecked();

        Button gobtn = (Button) findViewById(R.id.gobtn);

        if (Ischecked) {
            turnonBlutooth();
            final Handler handler=new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("My bluetooth name  : ", mybluetoothAdapter.getName() + "  with address " + mybluetoothAdapter.getAddress());
                    Set<BluetoothDevice>  pairedDevices = mybluetoothAdapter.getBondedDevices();
                    BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                    List<BluetoothDevice> connected = manager.getConnectedDevices(GATT);
                    Log.d("Connected Devices: ", connected.size() + " ");

                    Log.d("Discovering ?", " " + mybluetoothAdapter.isDiscovering());


                    if (pairedDevices!=null) {
                        SetDevices(pairedDevices);
                        //there are paired devices. get the name and addresses of each paired device
                        for (BluetoothDevice device : pairedDevices) {
                            String devicename = device.getName();
                            String deviceMacAddress = device.getAddress();
                            Log.d("Paired Devices: ", devicename + "  with address " + deviceMacAddress);

                        }


                    }
                    gobtn.setEnabled(true);
                }
            },10000);

        } else {
            gobtn.setEnabled(false);
            mybluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "TURNING_OFF BLUETOOTH!!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }
}