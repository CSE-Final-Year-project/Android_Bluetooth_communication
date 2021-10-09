package com.hfad.bluetooth_chat_application;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class PopupActivity extends Activity {
    BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean ispaired=false;
    ArrayList< BluetoothDevice> NewDevices = new ArrayList<>();
    NewUserAdapter newUserAdapter;
    ArrayList<NewUser> newUserArrayList=new ArrayList<NewUser>();
    ListView newdeviceListview;
     Set<BluetoothDevice> paired_devices=mybluetoothAdapter.getBondedDevices();
    Map<String,BluetoothDevice> Device_to_pair=new HashMap<String,BluetoothDevice>();

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Resitering ", "Bluetooth event listern register");
            Toast.makeText(getApplicationContext(), "Bluetooth event listern register", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Discovering has found a device. get the bluetoothDevice object and its info its info from the intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String devicename = device.getName();
                String deviceMacAddress = device.getAddress();
                Device_to_pair.put(devicename, device);
                NewUser newUser = new NewUser(device);
                if ((!newUserArrayList.contains(newUser))&& !paired_devices.contains(device)) {
                    newUserArrayList.add(newUser);
                    newUserAdapter = new NewUserAdapter(PopupActivity.this, newUserArrayList);
                    newdeviceListview.setAdapter(newUserAdapter);
                    newUserAdapter.notifyDataSetChanged();

                    // ((BaseAdapter)DeviceArrayListview.getAdapter()).notifyDataSetChanged();
                    Log.d("discovable Devices: ", devicename + "  with address " + deviceMacAddress);

                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        discoveron();
        if(mybluetoothAdapter.isDiscovering()){
            mybluetoothAdapter.cancelDiscovery();
        }
        mybluetoothAdapter.startDiscovery();
       setContentView(R.layout.dialog_demo);
        Button cancle=(Button) findViewById(R.id.scancancel);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
       DisplayMetrics dm=new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(dm);
       int width=dm.widthPixels;
       int height=dm.heightPixels;
       getWindow().setLayout((int)(width*.9),(int)(height*.8));
       WindowManager.LayoutParams params=getWindow().getAttributes();
       params.gravity= Gravity.CENTER;
       params.x=0;
       params.y=-25;
       getWindow().setAttributes(params);
        newdeviceListview=(ListView)findViewById(R.id.newdevices);
        Log.d("Listview ",""+newdeviceListview);
        // newdeviceListview.setAdapter(newUserAdapter);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_demo, null);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (mybluetoothAdapter.isDiscovering())
            mybluetoothAdapter.cancelDiscovery();
    }
    public void discoveron(){
        if (!mybluetoothAdapter.isDiscovering()) {
            Toast.makeText(getApplicationContext(), "Making your device discoverable", Toast.LENGTH_SHORT).show();
            Intent enablediscoveIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            enablediscoveIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            startActivity(enablediscoveIntent);

        }
    }


    public Map<String, BluetoothDevice> getDevice_to_pair() {
        return Device_to_pair;
    }

}