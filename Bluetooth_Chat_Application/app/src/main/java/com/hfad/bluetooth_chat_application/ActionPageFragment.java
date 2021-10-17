package com.hfad.bluetooth_chat_application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

// This is the class fragment that shows the view of our action page
public class ActionPageFragment extends Fragment {
    public static ArrayList<User> arrayOfUsers = new ArrayList<User>();
    private static final UUID MY_UUID_INSECURE=UUID.fromString("a1682af1-f7e0-49ec-b977-b93856cf5b79");
    private UUID deviceUUID;
    Button sendmessagebtn;
    ConnectedThread mConnectedThread;
    private BluetoothDevice mmDevice;
    private Handler handler;
    LinearLayout childLayout;
    LinearLayout mylayout;
    BluetoothAdapter bluetoothAdapter;
    String TAG = "ActionPageFragment ";
    EditText send_data;
    TextView outgoing_text_view ;
    TextView Incoming_text_view;
    StringBuilder messages;
    Dashboard_ListFragment dashboardListFragment;
    private long userId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        arrayOfUsers = dashboardListFragment.arrayOfUsers;
        return inflater.inflate(R.layout.action_page_layout, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG,bluetoothAdapter.getName());
        if (savedInstanceState != null) {
            userId = savedInstanceState.getLong("userId");
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("usertId", userId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        BluetoothDevice bluetoothDevice;
        arrayOfUsers = dashboardListFragment.arrayOfUsers;
        if (view != null) {

            TextView Username = (TextView) view.findViewById(R.id.user_name);
            User user = arrayOfUsers.get((int) userId);
            Username.setText(user.getName());
            send_data=(EditText)view.findViewById(R.id.type_message);
            sendmessagebtn=(Button)view.findViewById(R.id.sendmessgae);
            bluetoothDevice = dashboardListFragment.PairedList.get(user.getName());
            Log.d("devicename:", user.getName());
            Log.d("device: ", "" + bluetoothDevice);
            Log.d(TAG,""+MY_UUID_INSECURE);
            mylayout = (LinearLayout) view.findViewById(R.id.my_message_pane_layout);
            Start_Server();
            ConnectThread connect = new ConnectThread(bluetoothDevice, MY_UUID_INSECURE);
            connect.start();
            LinearLayout.LayoutParams all_layout_params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            all_layout_params.width = 700;
            sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessage();
                }
            });
            }


        }


    public void setUser(long id) {
        this.userId = id;
    }

    private class ConnectThread extends Thread {
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
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    private class ConnectedThread extends Thread {
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
                   getActivity().runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            Log.d(TAG,"Incoming message: "+incomingMessage);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            childLayout = new LinearLayout(getActivity());
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
                            Incoming_text_view = new TextView(getActivity());

                            Incoming_text_view.setLayoutParams(new TableLayout.LayoutParams(
                                    comingmessageParams));
                            Incoming_text_view.setBackground(getActivity().getDrawable(R.drawable.text_messages_shape));
                            Incoming_text_view.setTextSize(16);
                            Incoming_text_view.setPadding(5, 3, 0, 50);
                            Incoming_text_view.setTypeface(null, Typeface.ITALIC);
                            Incoming_text_view.setGravity(Gravity.LEFT | Gravity.CENTER);
                            Incoming_text_view.setText(incomingMessage);
                            childLayout.addView(Incoming_text_view, 0);
                            mylayout.addView(childLayout);

                        }
                    });


                } catch (IOException e) {
                    //Toast.makeText(getActivity(), "write: Error reading Input Stream" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }


        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
               // Toast.makeText(getActivity(), "write: Error writing to output stream", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SendMessage() {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        childLayout = new LinearLayout(getActivity());
        childLayout.setLayoutParams(linearParams);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        byte[] bytes = send_data.getText().toString().getBytes(Charset.defaultCharset());
        outgoing_text_view = new TextView(getActivity());
        LinearLayout.LayoutParams outgoing_msg_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        outgoing_msg_params.width = 200;
        outgoing_msg_params.leftMargin = 180;
        outgoing_msg_params.topMargin = 5;
        outgoing_msg_params.rightMargin = 10;
        outgoing_text_view.setLayoutParams(new TableLayout.LayoutParams(
                outgoing_msg_params));
        outgoing_text_view.setTextSize(17);
        outgoing_text_view.setPadding(5, 5, 5, 50);
        outgoing_text_view.setTypeface(Typeface.DEFAULT_BOLD);
        outgoing_text_view.setGravity(Gravity.LEFT | Gravity.CENTER);
        outgoing_text_view.setBackground(getActivity().getDrawable(R.drawable.your_message_shape));
        outgoing_text_view.setText(send_data.getText().toString());
        childLayout.addView(outgoing_text_view, 0);
        //childLayout.removeView(outgoing_text_view);
        mylayout.addView(childLayout);
       //mylayout.removeView(childLayout);
        mConnectedThread.write(bytes);


    }

    public void Start_Server() {

        AcceptThread accept = new AcceptThread();
        accept.start();

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

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }

    }
}
