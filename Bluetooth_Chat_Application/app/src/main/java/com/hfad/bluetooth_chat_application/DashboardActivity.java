package com.hfad.bluetooth_chat_application;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//This is the activity which initiates the fragment to get viewed which is sent by dashboardmain as fragment id
public class DashboardActivity extends AppCompatActivity {
    Dashboard_ListFragment dashboardListFragment;
    BluetoothDevice my_device;
    ActionPageFragment frag;
   public static ConnectedThread mConnectedThread;
   public static ConnectThread connectThread;
   public static Map<BluetoothDevice,String> incomingMessagesObj=new HashMap<>();
    public static Map<BluetoothDevice,String> incomingMessagesObjCopy=new HashMap<>();
    private UUID deviceUUID;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    private BluetoothDevice mmDevice;
    ArrayList<User> arrayOfUsers;
    private static final UUID MY_UUID_INSECURE=UUID.fromString("a1682af1-f7e0-49ec-b977-b93856cf5b79");
    String TAG="DashboardActivity";
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final String EXTRA_USER_ID = "Id";
    Set<BluetoothDevice> pairedDevices=MainActivity.pairedDevices;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_CAMERA_PERMISSION_CODE);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.dashbord1_activity);
         Toolbar mytoolbar = (Toolbar) findViewById(R.id.actiontoolbar);
         setSupportActionBar(mytoolbar);
       ActionBar actionBar = getSupportActionBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
       actionBar.setDisplayHomeAsUpEnabled(true);

         frag = (ActionPageFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_frag);
        // frag.setWorkout(1);
        int UserId = (int) getIntent().getExtras().get(EXTRA_USER_ID);
        frag.setUser(UserId);
        arrayOfUsers = dashboardListFragment.arrayOfUsers;
        User user = arrayOfUsers.get((int) UserId);
        bluetoothDevice = dashboardListFragment.PairedList.get(user.getName());
        Log.d("devicename:", user.getName());
        Log.d("device: ", "" + bluetoothDevice);
        Log.d(TAG,""+MY_UUID_INSECURE);
        Start_Server();
        DashboardActivity.connectThread = new DashboardActivity.ConnectThread(bluetoothDevice, MY_UUID_INSECURE);
        DashboardActivity.connectThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_page_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:  {
                // navigate to settings screen
                return true;
            }
            case R.id.action_call: {
                // save profile changes
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
   public class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + MY_UUID_INSECURE);
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE);
            }

            connected(mmSocket);
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    private void connected(BluetoothSocket mmSocket) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new DashboardActivity.ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

     class ConnectedThread extends Thread {
         private final BluetoothSocket mmSocket;
         private final InputStream mmInStream;
         private final OutputStream mmOutStream;

         public ConnectedThread(BluetoothSocket socket) {
             Log.d(TAG, "ConnectedThread: Starting.");

             mmSocket = socket;
             InputStream tmpIn = null;
             OutputStream tmpOut = null;


             try {
                 tmpIn = mmSocket.getInputStream();
                 tmpOut = mmSocket.getOutputStream();
             } catch (IOException e) {
                 e.printStackTrace();
             }

             mmInStream = tmpIn;
             mmOutStream = tmpOut;
         }

         public void run() {
             byte[] buffer = new byte[1024];  // buffer store for the stream

             int bytes; // bytes returned from read()

             // Keep listening to the InputStream until an exception occurs
             while (true) {
                 // Read from the InputStream
                 try {
                     bytes = mmInStream.read(buffer);
                     final String incomingMessage = new String(buffer, 0, bytes);
                     Log.d(TAG, "InputStream: " + incomingMessage);
                     runOnUiThread(new Runnable() {

                         @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                         @Override
                         public void run() {
                             // Log.d(TAG,"BUSER: "+Busername+"\n bluetooth name :"+bluetoothDevice.getName());
                             // if(Busername.equals(bluetoothDevice.getName())){
                             Log.d(TAG, "Incoming message: " + incomingMessage+" From "+bluetoothDevice);
                             incomingMessagesObj.put(bluetoothDevice,incomingMessage);
                             incomingMessagesObjCopy.put(bluetoothDevice,incomingMessage);

                         }
                     });


                 } catch (IOException e) {
                     //Toast.makeText(getActivity(), "write: Error reading Input Stream" + e.getMessage(), Toast.LENGTH_SHORT).show();
                     Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                     break;
                 }
             }
         }
         public void cancel() {
             try {
                 mmSocket.close();
             } catch (IOException e) {
             }
         }
         public void write(byte[] bytes) {
             String text = new String(bytes, Charset.defaultCharset());
             Log.d(TAG, "write: Writing to outputstream: " + text);
             try {
                 mmOutStream.write(bytes);
                 mmOutStream.flush();
             } catch (IOException e) {
                 Toast.makeText(getApplicationContext(), "write: Error writing to output stream", Toast.LENGTH_SHORT).show();
                 Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
             }
         }
     }

    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {

                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(getString(R.string.app_name), MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            //talk about this is in the 3rd
            if (socket != null) {
                connected(socket);
            }

            Log.i(TAG, "END mAcceptThread ");
        }


        /* Call this from the main activity to shutdown the connection */

    }
         public void Start_Server() {

             AcceptThread accept = new AcceptThread();
             accept.start();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mConnectedThread.isAlive())
            mConnectedThread.cancel();
    }

}
