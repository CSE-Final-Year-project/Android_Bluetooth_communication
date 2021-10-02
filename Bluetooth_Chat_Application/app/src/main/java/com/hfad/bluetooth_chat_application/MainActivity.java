package com.hfad.bluetooth_chat_application;

import static android.bluetooth.BluetoothProfile.GATT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    final BluetoothAdapter mybluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

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

    onSwitchClicked(bluetoothstatus);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mybluetoothAdapter.disable();

    }

    public void onSwitchClicked(View view) {
       boolean Ischecked =  ((Switch)view).isChecked();

        HandleTurnOnBluetooth(Ischecked);
    }
    private void HandleTurnOnBluetooth(boolean isChecked) {
        Button gobtn=(Button) findViewById(R.id.gobtn);

//        if(mybluetoothAdapter.isEnabled()){
//          bluetoothstatus.setChecked(true);
//        }
//        else{
//            bluetoothstatus.setChecked(false);
//        }
        if(mybluetoothAdapter==null){
            Toast.makeText(getApplicationContext(), "BLUETOOTH NOT SUPPORTED TO DIS DEVICE", Toast.LENGTH_SHORT).show();
        }

                if(isChecked){
                    if(!mybluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivity(enableBtIntent);
                    }
                        if(!mybluetoothAdapter.isDiscovering()){
                            Toast.makeText(getApplicationContext(), "Making your device discoverable", Toast.LENGTH_SHORT).show();
                            Intent enablediscoveIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            startActivity(enablediscoveIntent);
                            BluetoothManager manager=(BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                            List<BluetoothDevice> connected=manager.getConnectedDevices(GATT);
                            Log.d("Connected Devices: ",connected.size()+" ");


                        }
                    gobtn.setEnabled(true);
                }
                else{
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