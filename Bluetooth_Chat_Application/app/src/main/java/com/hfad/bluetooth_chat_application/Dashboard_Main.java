package com.hfad.bluetooth_chat_application;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/* This class is an activity class which holds the fragments which implements the Interface Listener
* and handles onItemClickListener whenever the user clicks one one item in the
* listfragment view to open the correspondent action page fragment view */
public class Dashboard_Main extends AppCompatActivity implements Dashboard_ListFragment.Listener {

    public static Dashboard_Main dashboard_main;
    private static final int REQUEST_DISCOVERY = 1;
    private static final int REQUEST_ENABLE =1 ;
    Map<String, String> PairedList = new HashMap<String, String>();
    BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button Scan;
    Intent myintent ;
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction().equalsIgnoreCase("message")){
                myintent.putExtra(DashboardActivity.EXTRA_USER_ID, (int)5);
                startActivity(myintent);
            }
        }

    };
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


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar mytoolbar = (Toolbar) findViewById(R.id.dashtool);
        setSupportActionBar(mytoolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        int MY_PERMISSIOMS_REQUEST_ACCESS_COARSE_LOCATION=1;
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN},MY_PERMISSIOMS_REQUEST_ACCESS_COARSE_LOCATION);
       myintent= new Intent(this, DashboardActivity.class);
        IntentFilter intentFilter=new IntentFilter();
intentFilter.addAction("message");
registerReceiver(receiver,intentFilter);
         Scan = (Button) findViewById(R.id.scan);
        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   showList();
                }

        });


//        devicelist.setAdapter(adapter);

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
      Intent popIntent=new Intent(Dashboard_Main.this,PopupActivity.class);
        activityResultLauncher.launch(popIntent);

    }

    @Override
    protected void onDestroy() {
//        if(DashboardActivity.mConnectedThread.isAlive())
//            DashboardActivity.mConnectedThread.cancel();
        super.onDestroy();

    }
//Function that lets your bbluetooth get turned on
    public void turnonBlutooth() {
        mybluetoothAdapter.startDiscovery();
        if (mybluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "BLUETOOTH NOT SUPPORTED TO DIS DEVICE", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mybluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityResultLauncher.launch(enableBtIntent);

        }

    }
    //This is the abstract methode which is in Dashboard_Listfragement to handle onitemclick
    @Override
    public void itemClicked(long id) {
        View fragmentContainer = findViewById(R.id.fragment_container);
        if (fragmentContainer != null) {
            ActionPageFragment actionPageFragment=new ActionPageFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            actionPageFragment.setUser(id);
            ft.replace(R.id.fragment_container, actionPageFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
        else{
        Log.d("clicked","id "+id);
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(DashboardActivity.EXTRA_USER_ID, (int)id);
        startActivity(intent);
    }
    }
}
