package com.hfad.bluetooth_chat_application;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

public class Dashboard extends AppCompatActivity {

    private static final int REQUEST_DISCOVERY = 1;
    private static final int REQUEST_ENABLE =1 ;
    Map<String, String> PairedList = new HashMap<String, String>();
    BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    
   ArrayList< BluetoothDevice> NewDevices = new ArrayList<>();
    ArrayList<String> newdevicesname = new ArrayList<>();
    ArrayList<String> DeviceArray = new ArrayList<>();
    ListView DeviceArrayListview=null;
    ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data=result.getData();
                        turnonBlutooth();
                        discoveron();
                        showList();
                    }
                }
            }

    );
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           mybluetoothAdapter.startDiscovery();
            Log.d("Resitering ", "Bluetooth event listern register");
            Toast.makeText(getApplicationContext(), "Bluetooth event listern register", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Discovering has found a device. get the bluetoothDevice object and its info its info from the intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String devicename = device.getName();
                String deviceMacAddress = device.getAddress();
                NewDevices.add(device);
                 if(!newdevicesname.contains(devicename)){
                     newdevicesname.add(devicename);


                 }

               // ((BaseAdapter)DeviceArrayListview.getAdapter()).notifyDataSetChanged();
                Log.d("discovable Devices: ", devicename + "  with address " + deviceMacAddress);

            }
        }
    };
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mybluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(NewDevices!=null){
            NewDevices.clear();
        }
        int MY_PERMISSIOMS_REQUEST_ACCESS_COARSE_LOCATION=1;
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN},MY_PERMISSIOMS_REQUEST_ACCESS_COARSE_LOCATION);
        ListView devicelist = (ListView) findViewById(R.id.paireddevlist);

        ToggleButton Scan = (ToggleButton) findViewById(R.id.scan);

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean Checked=((ToggleButton) v).isChecked();
                if(Checked) {
                    discoveron();
                    if(mybluetoothAdapter.isDiscovering()){
                        mybluetoothAdapter.cancelDiscovery();
                    }
                    mybluetoothAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter);
                   showList();

                }

                    else {
                        if(NewDevices!=null)
                        unregisterReceiver(receiver);
                        if (mybluetoothAdapter.isDiscovering())
                            mybluetoothAdapter.cancelDiscovery();
                    }
                    // mybluetoothAdapter.cancelDiscovery();


                }

        });
        User user;
        ArrayList<User>  arrayOfUsers=new ArrayList<User>();

//        devicelist.setAdapter(adapter);
        ArrayList<BluetoothDevice> pairedDevices = (ArrayList<BluetoothDevice>) getIntent().getExtras().get("PairedDevices");

        //there are paired devices. get the name and addresses of each paired device
        for (BluetoothDevice device : pairedDevices) {
            String devicename = device.getName();
            String deviceMacAddress = device.getAddress();
            PairedList.put(devicename, deviceMacAddress);
            DeviceArray.add(devicename);
            user=new User(devicename,deviceMacAddress);
            arrayOfUsers.add(user);
           // Log.d("Paired Devices: ", newUser.username );

        }

        UsersAdapter adapter=new UsersAdapter(this,arrayOfUsers);
         for(int i=0;i<arrayOfUsers.size();i++)
        Log.d("device: "+i, " "+adapter.getItem(i));
        devicelist.setAdapter(adapter);
        ArrayAdapter<String> DeviceAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                DeviceArray);
        Switch mySwitch = (Switch) findViewById(R.id.bluetooth_enabling);
        //devicelist.setAdapter(DeviceAdapter);
        mySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean Ischecked = mySwitch.isChecked();
                if (Ischecked) {
                    turnonBlutooth();

                } else {
                    // oldswitch.setEnabled(false);
                    mybluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(), "TURNING_OFF BLUETOOTH!!", Toast.LENGTH_SHORT).show();

                }

            }
        });
        //onSwitchClicked(mySwitch);

    }


    public void showList(){


//    newdevicesname.add("New device....");
//       newdevicesname.add("second device....");
        //final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(Dashboard.this, android.R.layout.select_dialog_singlechoice);

        //if (NewDevices != null)
            for (BluetoothDevice device : NewDevices) {
                Log.d("new device:  ", device.getName());
                if(!newdevicesname.contains(device.getName()))
                newdevicesname.add(device.getName());
                //arrayAdapter.add(device.getName());
            }

        String[]  devices= new String[newdevicesname.size()];
        devices=newdevicesname.toArray(devices);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ArrayAdapter<String> DeviceAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                newdevicesname);

        builder.setTitle("Attepting to connect to other devices around....");


        int checkedItem = 0;


        builder.setSingleChoiceItems(DeviceAdapter, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String chosendevice=newdevicesname.get(which);
                Log.d("chosen: ", chosendevice);
                AlertDialog.Builder confirmprompt=new AlertDialog.Builder(Dashboard.this);
                confirmprompt.setTitle("you are about to connect "+chosendevice);
                confirmprompt.setNegativeButton("cancel",null);
                confirmprompt.setPositiveButton("connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "connecting "+chosendevice, Toast.LENGTH_SHORT).show();
                    }
                });
                confirmprompt.create().show();
            }
        });
//        builder.setAdapter(DeviceAdapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("chosen: ", finalDevices[which]);
//            }
//        });

        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        //newdevicesname.add("My device");
        ListView listView=dialog.getListView();
        ArrayAdapter adapter=(ArrayAdapter) listView.getAdapter();
        Handler Handlechange=new Handler();
        Handlechange.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        },5000);
       //1adapter.notifyDataSetChanged();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void turnonBlutooth() {
//    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//    registerReceiver(receiver, filter);
        mybluetoothAdapter.startDiscovery();
        if (mybluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "BLUETOOTH NOT SUPPORTED TO DIS DEVICE", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mybluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityResultLauncher.launch(enableBtIntent);

            //Log.d(" Is it on ?: "," "+mybluetoothAdapter.isEnabled());
        }


    }
    public void discoveron(){
        if (!mybluetoothAdapter.isDiscovering()) {
            Toast.makeText(getApplicationContext(), "Making your device discoverable", Toast.LENGTH_SHORT).show();
            Intent enablediscoveIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            enablediscoveIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            activityResultLauncher.launch(enablediscoveIntent);

        }
    }



}
