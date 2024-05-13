package com.hfad.bluetooth_chat_application;

import static android.bluetooth.BluetoothProfile.GATT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE = 1;
    public final BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    int REQUEST_DISCOVERY = 1;
    public static Set<BluetoothDevice> pairedDevices = null;
    Map<String, String> NewDevices = new HashMap<String, String>();
    Map<String, String> PairedList = new HashMap<String, String>();
    Button gobtn;
    Switch bluetoothstatus;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        turnonBlutooth();
                    }
                }
            }

    );

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
if(mybluetoothAdapter.isEnabled())
    mybluetoothAdapter.disable();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        turnonBlutooth();
        Toolbar mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);
        TextView Initialsetup = (TextView) findViewById(R.id.initialsetup);
        SpannableString content = new SpannableString(Initialsetup.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        Initialsetup.setText(content);
        bluetoothstatus = (Switch) findViewById(R.id.bluetooth_enable);
        //Object Inflater=this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //if(bluetoothstatus.isChecked())
        int MY_PERMISSIOMS_REQUEST_ACCESS_BLUETOOTH = 1;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIOMS_REQUEST_ACCESS_BLUETOOTH);
        onSwitchClicked(bluetoothstatus);
        if (bluetoothstatus.isChecked())
            gobtn.setEnabled(true);
    }

    private void SetDevices(Set<BluetoothDevice> devices) {
        pairedDevices = devices;
    }

    protected Set<BluetoothDevice> GetDevices() {
        return pairedDevices;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mybluetoothAdapter.disable();
        Log.d("Bluetooth is ", "" + mybluetoothAdapter.isEnabled());


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
        if(mybluetoothAdapter.isEnabled()) {
            bluetoothstatus.setChecked(true);
            Get_Forward();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onSwitchClicked(View view) {

        boolean Ischecked = ((Switch) view).isChecked();

        gobtn = (Button) findViewById(R.id.gobtn);

        if (Ischecked) {
            turnonBlutooth();

            Get_Forward();

        } else {
            gobtn.setEnabled(false);
            mybluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "TURNING_OFF BLUETOOTH!!", Toast.LENGTH_SHORT).show();
        }
    }
    void Get_Forward(){
        TextView devicename = (TextView) findViewById(R.id.devname);
        // if(mybluetoothAdapter.isEnabled())
        devicename.setText(mybluetoothAdapter.getName());
        Log.d("New device name", "" + NewDevices.get(0));
//         for(int i=0;i<NewDevices.size();i++)
//            Log.d("New device name",""+NewDevices.get(i));
        final Handler handler = new Handler(Looper.getMainLooper());
        if(!gobtn.isEnabled()){
            gobtn.setText("Waiting..");
        }
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                Log.d("My bluetooth name  : ", mybluetoothAdapter.getName() + "  with address " + mybluetoothAdapter.getAddress());
                Set<BluetoothDevice> pairedDevices = mybluetoothAdapter.getBondedDevices();

                BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                List<BluetoothDevice> connected = manager.getConnectedDevices(GATT);

                //Log.d("Discovering ?", " " + mybluetoothAdapter.isDiscovering());

                if (pairedDevices != null) {
                    SetDevices(pairedDevices);
                    ArrayList<BluetoothDevice> DeviceArray = new ArrayList<>();

                    //there are paired devices. get the name and addresses of each paired device
                    for (BluetoothDevice device : pairedDevices) {
                        String devicename = device.getName();
                        String deviceMacAddress = device.getAddress();
                        PairedList.put(devicename, deviceMacAddress);
                        DeviceArray.add(device);
                        //Log.d("Paired Devices: ", devicename + "  with address " + deviceMacAddress);

                    }

                    gobtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Devices are ", "" + pairedDevices);
                            Class<Dashboard_Main> test = Dashboard_Main.class;
                            Intent DashboardIntent = new Intent(MainActivity.this, Dashboard_Main.class);
                            DashboardIntent.putExtra("PairedDevices", DeviceArray);
                            startActivity(DashboardIntent);

                        }
                    });
                    if (mybluetoothAdapter.isEnabled()){
                        gobtn.setEnabled(true);
                        gobtn.setText("Go...");
                    }

                    else
                        Toast.makeText(getApplicationContext(), "Turn on bluetooth please!", Toast.LENGTH_SHORT).show();
                }

            }
        }, 5000);
    }





//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_main,menu);
//
//        return true;
//    }
}