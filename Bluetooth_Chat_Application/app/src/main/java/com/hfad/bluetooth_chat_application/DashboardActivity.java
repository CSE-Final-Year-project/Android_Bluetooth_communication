package com.hfad.bluetooth_chat_application;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.Tag;
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
import androidx.core.app.NotificationCompat;

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
    private UUID deviceUUID;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    private BluetoothDevice mmDevice;
    LinearLayout childLayout;
    LinearLayout mylayout;
    TextView Incoming_text_view;
    User user;
    Toolbar mytoolbar;
    ArrayList<User> arrayOfUsers;
    private static final UUID MY_UUID_INSECURE=UUID.fromString("a1682af1-f7e0-49ec-b977-b93856cf5b79");
    String TAG="DashboardActivity";
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final String EXTRA_USER_ID = "Id";
    public static final String notifcationId="b93856cf5b79";
    Set<BluetoothDevice> pairedDevices=MainActivity.pairedDevices;
    SoftInputAssist softInputAssist;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_CAMERA_PERMISSION_CODE);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.dashbord1_activity);
          mytoolbar = (Toolbar) findViewById(R.id.actiontoolbar);
         setSupportActionBar(mytoolbar);
       ActionBar actionBar = getSupportActionBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
       actionBar.setDisplayHomeAsUpEnabled(true);
        createNotificationChannel();

         frag = (ActionPageFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_frag);
        // frag.setWorkout(1);
        int UserId = (int) getIntent().getExtras().get(EXTRA_USER_ID);
        frag.setUser(UserId);
        //softInputAssist = new SoftInputAssist(this);
        arrayOfUsers = dashboardListFragment.arrayOfUsers;
         user = arrayOfUsers.get((int) UserId);
        bluetoothDevice = dashboardListFragment.PairedList.get(user.getName());
        Log.d("devicename:", user.getName());
        Log.d("device: ", "" + bluetoothDevice);
        Log.d(TAG,""+MY_UUID_INSECURE);
        Start_Server();
        DashboardActivity.connectThread = new DashboardActivity.ConnectThread(bluetoothDevice, MY_UUID_INSECURE);
       for (BluetoothDevice mybluetoothDevice:dashboardListFragment.PairedList.values()) {
            Log.d("Device ", "" + mybluetoothDevice);
            //if (mybluetoothDevice!=bluetoothDevice)
           //DashboardActivity.connectThread = new DashboardActivity.ConnectThread(mybluetoothDevice, MY_UUID_INSECURE);
    }
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
            case android.R.id.home:
                DashboardActivity.this.finish();
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
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name=getString(R.string.chanel_name);
            String description=getString(R.string.chanel_description);
            int importance=NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel chanel=new NotificationChannel(notifcationId,name,importance);
            chanel.setDescription(description);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            chanel.enableLights(true);
            chanel.enableVibration(true);
            chanel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            notificationManager.createNotificationChannel(chanel);

        }
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
                             Log.d(TAG,"Incoming message is :"+incomingMessage);
                             String receiverId=incomingMessage.substring(0,17);
                             Log.d("Receiver:",receiverId);
                             Log.d(TAG,"new coming message: "+incomingMessage.substring(17));
                               Log.d(TAG,"are they equal?"+(receiverId==bluetoothDevice.getAddress()));
                              mylayout = (LinearLayout) findViewById(R.id.my_message_pane_layout);
                             if(receiverId.equalsIgnoreCase(bluetoothDevice.getAddress())){
                                 Log.d(TAG,"sent: "+receiverId+" , received: "+bluetoothDevice.getAddress());
                                 LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                                         LinearLayout.LayoutParams.WRAP_CONTENT,
                                         LinearLayout.LayoutParams.MATCH_PARENT);
                                 childLayout = new LinearLayout(DashboardActivity.this);
                                 childLayout.setLayoutParams(linearParams);
                                 childLayout.setOrientation(LinearLayout.VERTICAL);
                                 linearParams.setMargins(10, 10, 10, 10);

                                 LinearLayout.LayoutParams comingmessageParams = new LinearLayout.LayoutParams(
                                         LinearLayout.LayoutParams.WRAP_CONTENT,
                                         LinearLayout.LayoutParams.WRAP_CONTENT);
                                 comingmessageParams.width = 200;
                                 comingmessageParams.leftMargin = 5;
                                 comingmessageParams.rightMargin = 100;
                                 comingmessageParams.topMargin = 5;
                                 LinearLayout.LayoutParams yourmessageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                         LinearLayout.LayoutParams.WRAP_CONTENT);
                                 comingmessageParams.width = 200;
                                 comingmessageParams.leftMargin = 70;

                                 comingmessageParams.rightMargin = 10;
                                 Incoming_text_view = new TextView(DashboardActivity.this);
                                 Incoming_text_view.setLayoutParams(new TableLayout.LayoutParams(
                                         comingmessageParams));
                                 Incoming_text_view.setBackground(DashboardActivity.this.getDrawable(R.drawable.text_messages_shape));
                                 Incoming_text_view.setTextSize(16);
                                 Incoming_text_view.setPadding(5, 3, 0, 50);
                                 Incoming_text_view.setTypeface(null, Typeface.ITALIC);
                                 Incoming_text_view.setGravity(Gravity.LEFT | Gravity.CENTER);

                                 Incoming_text_view.setText(incomingMessage.substring(17));
                                 Toast.makeText(getApplicationContext() ,""+user.getName()+" has sent you a message!", Toast.LENGTH_SHORT).show();
                                 childLayout.addView(Incoming_text_view, 0);
                                 mylayout.addView(childLayout);

                     }
                             Log.d(TAG, "Incoming message: " + incomingMessage.substring(17)+" From "+bluetoothDevice.toString());
                             int notfication_number=0;
                             Log.d("current id",bluetoothDevice.getName());
                             NotificationCompat.Builder builder=new NotificationCompat.Builder(DashboardActivity.this,notifcationId)
                                     .setSmallIcon(R.drawable.messageicon)
                                     .setContentTitle(user.getName()+": message")
                                     .setPriority(NotificationCompat.PRIORITY_HIGH)
                                     .setStyle(new NotificationCompat.BigTextStyle().bigText(incomingMessage.substring(17)))
                                     .setContentText("Incoming message");
                             builder.setNumber(++notfication_number);
                             // Adding notification
                             NotificationManager manager=(NotificationManager)DashboardActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                             manager.notify(0,builder.build());
                         }
                     });
//                    while(DashboardActivity.incomingMessagesObjCopy.size()>0){




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
                 if(mmOutStream!=null)
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
