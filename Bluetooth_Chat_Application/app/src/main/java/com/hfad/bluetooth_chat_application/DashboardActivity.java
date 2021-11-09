package com.hfad.bluetooth_chat_application;

import static android.os.FileUtils.copy;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.ParcelUuid;
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
import android.widget.ImageView;
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
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//This is the activity which initiates the fragment to get viewed which is sent by dashboardmain as fragment id
public class DashboardActivity extends AppCompatActivity implements Serializable {
    Dashboard_ListFragment dashboardListFragment;
    BluetoothDevice my_device;
    ActionPageFragment frag;
    public static ConnectedThread mConnectedThread;
    public static ConnectThread connectThread;
//    public static ConnectThread connectImageThread;

    private UUID deviceUUID;
    private String mCurrentPhotoPath;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    private BluetoothDevice mmDevice;
    LinearLayout childLayout;
    LinearLayout mylayout;
    TextView Incoming_text_view;
    User user;
    private ImageView NewImageView;
    Toolbar mytoolbar;
    ArrayList<User> arrayOfUsers;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("a1682af1-f7e0-49ec-b977-b93856cf5b79");
    String TAG = "DashboardActivity";
    private static UUID image_UUID;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final String EXTRA_USER_ID = "Id";
    public static final String notifcationId = "b93856cf5b79";
    Set<BluetoothDevice> pairedDevices = MainActivity.pairedDevices;
    SoftInputAssist softInputAssist;

    private String getDeviceUUID() {
        String myUuid = "";
        String TotalUuid = "";
        try {
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(bluetoothAdapter, null);
            if (uuids != null) {
                for (ParcelUuid uuid : uuids) {
                    TotalUuid += uuid;
                    Log.d(TAG, "UUId: " + uuid);
                }
                myUuid = ((uuids[0]).getUuid()).toString();
                Log.d(TAG, "UUID length: " + myUuid.length());
                Log.d(TAG, "TotalUUid is: " + TotalUuid + " with the length: " + TotalUuid.length());

            } else
                Log.d(TAG, "No UUID found! ");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return myUuid;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.dashbord1_activity);
        mytoolbar = (Toolbar) findViewById(R.id.actiontoolbar);
        setSupportActionBar(mytoolbar);
        ActionBar actionBar = getSupportActionBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        actionBar.setDisplayHomeAsUpEnabled(true);
        createNotificationChannel();
        NewImageView = new ImageView(this);
        image_UUID = UUID.fromString(getDeviceUUID());
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
        Log.d(TAG, "" + MY_UUID_INSECURE);
        Start_Server();

        DashboardActivity.connectThread = new DashboardActivity.ConnectThread(bluetoothDevice, MY_UUID_INSECURE);
        for (BluetoothDevice mybluetoothDevice : dashboardListFragment.PairedList.values()) {
            Log.d("Device ", "" + mybluetoothDevice);
            //if (mybluetoothDevice!=bluetoothDevice)
            //DashboardActivity.connectThread = new DashboardActivity.ConnectThread(mybluetoothDevice, MY_UUID_INSECURE);
        }
        DashboardActivity.connectThread.start();
//        DashboardActivity.connectImageThread = new DashboardActivity.ConnectThread(bluetoothDevice, MY_UUID_INSECURE);
//        DashboardActivity.connectImageThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DashboardActivity.this.finish();
            case R.id.action_settings: {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.chanel_name);
            String description = getString(R.string.chanel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel chanel = new NotificationChannel(notifcationId, name, importance);
            chanel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            chanel.enableLights(true);
            chanel.enableVibration(true);
            chanel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(chanel);

        }
    }

    class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream = null;
        private final OutputStream mmOutStream = null;
        private final ObjectInputStream objectInputStream;
        private final ObjectOutputStream objectOutputStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            ObjectInputStream tmpobjectInputStream = null;
            ObjectOutputStream tmpobjectOutputStream = null;

            try {
                //tmpIn = mmSocket.getInputStream();
                // tmpOut = mmSocket.getOutputStream();
                tmpobjectOutputStream = new ObjectOutputStream(mmSocket.getOutputStream());
                tmpobjectInputStream = new ObjectInputStream(mmSocket.getInputStream());

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error:", e.getMessage());
            }

            //mmInStream = tmpIn;
            //mmOutStream = tmpOut;
            objectInputStream = tmpobjectInputStream;
            objectOutputStream = tmpobjectOutputStream;
        }
        private File copyFile(File source,File destination) throws IOException {
            InputStream inputStream=new FileInputStream(source);
            try {
                OutputStream outputStream=new FileOutputStream(destination);
                try{
                    byte[] buf=new byte[1024];
                    int len;
                    while((len=inputStream.read(buf))>0){
                        outputStream.write(buf,0,len);
                        Log.d(TAG,"Streams are ggetting writen: "+len);
                    }

                }
                finally {
                    outputStream.close();
                }
            }
            finally {
                inputStream.close();
            }
            return destination;


        }
        public File createImageFile(String imageFileName) throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String imageFileName = "IMAGE_" + timeStamp + "_";
            String folder_main = "BluetoothChatFolder";
            File storageDir = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Images");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            File image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = "file:" + image.getAbsolutePath();
            return image;
        }
        @RequiresApi(api = Build.VERSION_CODES.Q)
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    Uri photoUri=null;
                    String incomingMessage = null;
                    StringMessage stringMessage=null;
                    ImageMessage imageMessage=null;
                    File myfile=null;
                    Bitmap imageBitmap=null;
                    File madefile=null;
                    Bitmap b2=null;
                    BitmapDataObject bitmapDataObject=new BitmapDataObject();
                    byte[]  imagebytes=null;
                    File createdFile=null;
                    MessageClass messageClass = (MessageClass) objectInputStream.readObject();
                    if(messageClass.getMessageType().equalsIgnoreCase("StringMessage")) {
                        stringMessage = (StringMessage) messageClass;
                        incomingMessage = (stringMessage.getMessage());
                        Log.d(TAG, "InputStream: " + incomingMessage);

                    }
                    if(messageClass.getMessageType().equalsIgnoreCase("Image")){
                        imageMessage=(ImageMessage) messageClass;
                        bitmapDataObject=(BitmapDataObject)imageMessage.getBitmap();
                        imageBitmap=imageMessage.convertToBitmap();
                        Log.d("Bitmap:",imageBitmap.toString());
                        Log.d(TAG,"Image received "+imageMessage.getMyfile().getPath());
                        myfile=imageMessage.getMyfile();
//                         madefile=createImageFile(imageMessage.getImageName());
//                         //imageBitmap=imageMessage.getImagebitmap();
//                        FileOutputStream fo = new FileOutputStream(madefile);
//                        copy(new FileInputStream(myfile), fo);
//                        fo.write(buffer);
//                        fo.close();
                        int origWidth = imageBitmap.getWidth();
                        int origHeight = imageBitmap.getHeight();
                        final int destWidth = 600;
                        int destHeight=origHeight;
                        // imageView.setImageBitmap(selectedImage);
                        if (origWidth > destWidth) {
                            // picture is wider than we want it, we calculate its target height
                            destHeight = origHeight / (origWidth / destWidth);
                        }
                            // we create an scaled bitmap so it reduces the image, not just trim it
                             b2 = Bitmap.createScaledBitmap(imageBitmap, destWidth, destHeight, false);
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            // compress to the format you want, JPEG, PNG...
                            // 70 is the 0-100 quality percentage
                            b2.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            File received = createImageFile(imageMessage.getImageName());
                            FileOutputStream fo = new FileOutputStream(received);
                            fo.write(outStream.toByteArray());
                            //createdFile=copyFile(myfile,madefile);
                            createdFile=received;
                            Log.d(TAG,"created file: "+createdFile.getPath());
                            photoUri= FileProvider.getUriForFile(DashboardActivity.this,BuildConfig.APPLICATION_ID+".provider",createdFile);

                            Log.d("path: ",photoUri.getPath());


                    }


                    // incomingMessage = new String(buffer, 0, bytes);


                    String finalIncomingMessage = incomingMessage;
                    StringMessage finalStringMessage = stringMessage;
                    ImageMessage finalImageMessage = imageMessage;
                    Uri finalPhotoUri = photoUri;
                    File finalMyfile =createdFile;
                    Bitmap finalImageBitmap = imageBitmap;
                    byte[] finalImagebytes = imagebytes;
                    Bitmap finalImageBitmap1 = imageBitmap;
                    Bitmap finalB = b2;
                    runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            Log.d(TAG, "Bluetooth uuid id" + bluetoothDevice.getUuids()[0].toString());
                            Log.d(TAG, "Incoming message is :" + finalIncomingMessage);
                            String sender = bluetoothDevice.getName();
                            String receiver = bluetoothAdapter.getName();
//                             int IdLength = IdentifiedId.length();
                            Intent resultIntent = new Intent(DashboardActivity.this, Dashboard_Main.class);
                            resultIntent.setAction("message");
                            sendBroadcast(resultIntent);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                            String receiverId = messageClass.getReceiver();
                            String SenderId = messageClass.getSender();
                            Log.d("Receiver:", receiverId);
                            Log.d(TAG, "new coming message: " + finalIncomingMessage);
                            //Log.d(TAG, "are they equal?" + (receiverId == bluetoothDevice.getAddress()));
                            if (receiverId.equalsIgnoreCase(receiver) && SenderId.equalsIgnoreCase(sender)) {
                                mylayout = (LinearLayout) findViewById(R.id.my_message_pane_layout);
                                Log.d(TAG, "sent: " + receiverId + " , received: " + bluetoothDevice.getAddress());
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
                                comingmessageParams.topMargin = 2;
                                comingmessageParams.bottomMargin = 5;
                                LinearLayout.LayoutParams yourmessageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                comingmessageParams.width = 200;
                                comingmessageParams.leftMargin = 70;

                                comingmessageParams.rightMargin = 10;
                                if(finalStringMessage !=null) {
                                    Incoming_text_view = new TextView(DashboardActivity.this);
                                    Incoming_text_view.setLayoutParams(new TableLayout.LayoutParams(
                                            comingmessageParams));
                                    Incoming_text_view.setBackground(DashboardActivity.this.getDrawable(R.drawable.text_messages_shape));
                                    Incoming_text_view.setTextSize(16);
                                    Incoming_text_view.setPadding(5, 3, 0, 50);
                                    Incoming_text_view.setTypeface(null, Typeface.ITALIC);
                                    Incoming_text_view.setGravity(Gravity.LEFT | Gravity.CENTER);

                                    Incoming_text_view.setText(finalIncomingMessage);
                                    TextView timeTextview = new TextView(DashboardActivity.this);
                                    Toast.makeText(getApplicationContext(), "" + user.getName() + " has sent you a message!", Toast.LENGTH_SHORT).show();
                                    childLayout.addView(Incoming_text_view, 0);

                                    String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                                    LinearLayout.LayoutParams time_params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    time_params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                    time_params.leftMargin = 200;
                                    time_params.topMargin = 0;
                                    time_params.bottomMargin = 5;
                                    time_params.rightMargin = 10;
                                    timeTextview.setLayoutParams(new TableLayout.LayoutParams(
                                            time_params));
                                    timeTextview.setText(timeStamp);
                                    childLayout.addView(timeTextview);
                                    mylayout.addView(childLayout);
                                    String senderName = "";
                                    senderName = SenderId;
                                    Log.d(TAG, "Incoming message: " + finalIncomingMessage + " From " + bluetoothDevice.toString());
                                    int notfication_number = 1;
                                    Log.d("current id", bluetoothDevice.getName());
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(DashboardActivity.this, notifcationId)
                                            .setSmallIcon(R.drawable.messageicon)
                                            .setContentTitle(senderName + ": message")
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(finalIncomingMessage))
                                            .setContentIntent(pendingIntent)
                                            .setContentText("Incoming message");
                                    builder.setNumber(++notfication_number);
                                    // Adding notification
                                    NotificationManager manager = (NotificationManager) DashboardActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.notify(0, builder.build());
                                }
                                if(finalImageMessage !=null){
                                    NewImageView=new ImageView(DashboardActivity.this);
                                    InputStream imageStream = null;
                                    try {
                                        Uri myfileuri=Uri.parse(finalMyfile.getAbsolutePath());
                                        Log.d("image","path :"+myfileuri.getPath());
                                        imageStream = DashboardActivity.this.getContentResolver().openInputStream(myfileuri);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                    NewImageView.setPadding(5, 5, 5, 5);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        NewImageView.setForegroundGravity(Gravity.LEFT | Gravity.CENTER);
                                    }

                                    NewImageView.setImageBitmap(finalB);
                                    mylayout.addView(NewImageView);
                                }
                        }
                        //}
                    }
//                    while(DashboardActivity.incomingMessagesObjCopy.size()>0){


                    });

                }
                catch (Exception e) {
                    //Toast.makeText(getActivity(), "write: Error reading Input Stream" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    Log.d(TAG, e.getMessage());
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

        public void write(MessageClass messageClass) {
            try {
                if (objectOutputStream != null)
//                 mmOutStream.write(bytes);
//                 mmOutStream.flush();
                {
                    objectOutputStream.writeObject(messageClass);
                    objectOutputStream.flush();
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "write: Error writing to output stream", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }
//           public void write(byte[] bytes) {
//             //String text = new String(bytes, Charset.defaultCharset());
//            // Log.d(TAG, "write: Writing to outputstream: " + text);
//             try {
//                 if(objectOutputStream!=null)
////                 mmOutStream.write(bytes);
////                 mmOutStream.flush();
//                     objectOutputStream.writeObject(bytes);
//                     objectOutputStream.flush();
//             } catch (IOException e) {
//                 Toast.makeText(getApplicationContext(), "write: Error writing to output stream", Toast.LENGTH_SHORT).show();
//                 Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
//             }

//         public void writeImage(byte[] bytes){
//             try{
//                 mmOutStream.write(bytes,0,bytes.length);
//                 mmOutStream.flush();
//             } catch (IOException e) {
//                 Toast.makeText(getApplicationContext(), "write: Error writing to output stream", Toast.LENGTH_SHORT).show();
//                 Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
//             }
//         }
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
//        if(mConnectedThread.isAlive())
//            mConnectedThread.cancel();
    }
    public static Object toObject(byte[] bytes)
    {
        Object obj = null;
        ObjectInputStream ois = null;

        try
        {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject();
        }
        catch(Exception ex)
        {
            Log.e("Bluetooth", "Cast exception at receiving end ...");
        }

        return obj;
    }

}
