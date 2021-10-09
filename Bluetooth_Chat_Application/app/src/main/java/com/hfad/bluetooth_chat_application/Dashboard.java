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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RunnableFuture;
import java.util.zip.Inflater;


public class Dashboard extends AppCompatActivity {

    private static final int REQUEST_DISCOVERY = 1;
    private static final int REQUEST_ENABLE =1 ;
    Map<String, String> PairedList = new HashMap<String, String>();
    BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button Scan;
    boolean ispaired=false;
   ArrayList< BluetoothDevice> NewDevices = new ArrayList<>();
    public UsersAdapter  Useradapter;
    NewUserAdapter newUserAdapter;
    User user;
   public static ArrayList<User>  arrayOfUsers=new ArrayList<User>();

    ArrayList<String> DeviceArray = new ArrayList<>();
   public ListView devicelist;
    String devicename;

    ArrayList<NewUser> newUserArrayList=new ArrayList<NewUser>();

    ArrayList<BluetoothDevice> pairedDevices;
    Map<String,BluetoothDevice> Device_to_pair=new HashMap<String,BluetoothDevice>();
    ListView DeviceArrayListview=null;
    ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data=result.getData();
                        turnonBlutooth();
                    }
                }
            }

    );

    public void setUseradapter(UsersAdapter useradapter) {
        Useradapter = useradapter;
    }

    public ListView getDevicelist() {
        return devicelist;
    }

    public ArrayList<User> getArrayOfUsers() {
        return arrayOfUsers;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if(arrayOfUsers.size()>0)
        arrayOfUsers.clear();
        mybluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        devicename=mybluetoothAdapter.getName();
        if(NewDevices!=null){
            NewDevices.clear();
        }
        int MY_PERMISSIOMS_REQUEST_ACCESS_COARSE_LOCATION=1;
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN},MY_PERMISSIOMS_REQUEST_ACCESS_COARSE_LOCATION);
        devicelist = (ListView) findViewById(R.id.paireddevlist);
         Scan = (Button) findViewById(R.id.scan);
        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   showList();
                }

        });

//        devicelist.setAdapter(adapter);
         pairedDevices = (ArrayList<BluetoothDevice>) getIntent().getExtras().get("PairedDevices");

        //there are paired devices. get the name and addresses of each paired device
        for (BluetoothDevice device : pairedDevices) {
            String devicename = device.getName();
            String deviceMacAddress = device.getAddress();
            PairedList.put(devicename, deviceMacAddress);
            DeviceArray.add(devicename);
            user=new User(devicename,deviceMacAddress);
            if(!arrayOfUsers.contains(user))
            arrayOfUsers.add(user);
           // Log.d("Paired Devices: ", newUser.username );

        }

        Useradapter=new UsersAdapter(this,arrayOfUsers);
         for(int i=0;i<arrayOfUsers.size();i++)
        Log.d("device: "+i, " "+Useradapter.getItem(i));
        devicelist.setAdapter(Useradapter);
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
                    Toast.makeText(getApplicationContext(), "TURNED_OFF BLUETOOTH!!", Toast.LENGTH_SHORT).show();

                }

            }
        });
        //onSwitchClicked(mySwitch);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Function that Displays the list of the available  devices
    public void showList(){
        //setContentView(R.layout.dialog_demo);
      Intent popIntent=new Intent(Dashboard.this,PopupActivity.class);
        activityResultLauncher.launch(popIntent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
//Function that lets your bbluetooth get turned on
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
    //Function that makes your device to be discovered

    //Function that make pair

}
