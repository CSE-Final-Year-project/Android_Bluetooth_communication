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
import android.renderscript.Sampler;
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

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public  final BluetoothAdapter mybluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    boolean IsbluetoothOn=false;
    boolean IsTurningOn=false;
    int REQUEST_DISCOVERY=1;
    Set<BluetoothDevice> pairedDevices=null;
    Map<String,String> NewDevices=new HashMap<String,String>();
    Map<String,String> PairedList=new HashMap<String,String>();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
        //mybluetoothAdapter.disable();
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_DISCOVERY){
            turnonBlutooth();
        }
    }

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

        boolean Ischecked = ((Switch) view).isChecked();

        Button gobtn = (Button) findViewById(R.id.gobtn);

        if (Ischecked) {
            turnonBlutooth();
            Log.d("New device name",""+NewDevices.get(0));
//         for(int i=0;i<NewDevices.size();i++)
//            Log.d("New device name",""+NewDevices.get(i));
            final Handler handler=new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("My bluetooth name  : ", mybluetoothAdapter.getName() + "  with address " + mybluetoothAdapter.getAddress());
                    Set<BluetoothDevice>  pairedDevices = mybluetoothAdapter.getBondedDevices();
                    BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                    List<BluetoothDevice> connected = manager.getConnectedDevices(GATT);

                    Log.d("Discovering ?", " " + mybluetoothAdapter.isDiscovering());


                    if (pairedDevices!=null) {
                        SetDevices(pairedDevices);
                        ArrayList<BluetoothDevice> DeviceArray = new ArrayList<>();

                        //there are paired devices. get the name and addresses of each paired device
                        for (BluetoothDevice device : pairedDevices) {
                            String devicename = device.getName();
                            String deviceMacAddress = device.getAddress();
                            PairedList.put(devicename,deviceMacAddress);
                            DeviceArray.add(device);
                            Log.d("Paired Devices: ", devicename + "  with address " + deviceMacAddress);

                        }

                        gobtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("Devices are ",""+pairedDevices);
                                Intent DashboardIntent=new Intent(MainActivity.this,Dashboard.class);
                                DashboardIntent.putExtra("PairedDevices",DeviceArray);
                                startActivity(DashboardIntent);

                            }
                        });

                    }
                    gobtn.setEnabled(true);
                }
            },15000);

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