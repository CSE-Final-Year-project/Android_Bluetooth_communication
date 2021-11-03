package com.hfad.bluetooth_chat_application;

import static androidx.core.app.NotificationCompat.*;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

// This is the class fragment that shows the view of our action page
public class ActionPageFragment extends Fragment {
    public static ArrayList<User> arrayOfUsers = new ArrayList<User>();
    private static final UUID MY_UUID_INSECURE=UUID.fromString("a1682af1-f7e0-49ec-b977-b93856cf5b79");
    private UUID deviceUUID;
    BluetoothDevice bluetoothDevice;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Button sendmessagebtn;
   // ConnectedThread mConnectedThread;
    private BluetoothDevice mmDevice;
    private Handler handler;
    LinearLayout childLayout;
    LinearLayout mylayout;
    private String mCurrentPhotoPath;
    View view;
    BluetoothAdapter bluetoothAdapter;
    String TAG = "ActionPageFragment ";
    EditText send_data;
    TextView outgoing_text_view ;
    String Busername;
    private Uri uriFilePath;
    StringBuilder messages;
    public static final String DOC = "application/msword";
    public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String IMAGE = "image/*";
    public static final String AUDIO = "audio/*";
    public static final String TEXT = "text/*";
    public static final String PDF = "application/pdf";
    public static final String VIDEO="video/*";
    public static final String XLS = "application/vnd.ms-excel";
    ImageView documentImageview;
    ImageView cameraImageview;
    ImageView galleryImageView;
    Dashboard_ListFragment dashboardListFragment;
    private long userId;
    Bitmap mImageBitmap = null;
   // ImageView imageView;
   private SoftInputAssist softInputAssist;
    String[] mimetypes = {DOC,DOCX,XLS,TEXT,PDF,VIDEO,AUDIO};
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                private ImageView NewImageView;

                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //openFile();
                        final InputStream imageStream;
                        Intent data = result.getData();
                        String filePath = mCurrentPhotoPath;
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        options.inSampleSize = 2;  //you can also calculate your inSampleSize
                        options.inJustDecodeBounds = false;
                        options.inTempStorage = new byte[16 * 1024];
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Uri imageUri = Uri.parse(filePath);
                        Bitmap selectedImage = null;
                        try {
                            imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                            selectedImage = BitmapFactory.decodeStream(imageStream, null, options);
                            int origWidth = selectedImage.getWidth();
                            int origHeight = selectedImage.getHeight();
                            final int destWidth = 600;
                           // imageView.setImageBitmap(selectedImage);
                            if (origWidth > destWidth) {
                                // picture is wider than we want it, we calculate its target height
                                int destHeight = origHeight / (origWidth / destWidth);
                                // we create an scaled bitmap so it reduces the image, not just trim it
                                Bitmap b2 = Bitmap.createScaledBitmap(selectedImage, destWidth, destHeight, false);
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                // compress to the format you want, JPEG, PNG...
                                // 70 is the 0-100 quality percentage
                                b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
                                // we save the file, at least until we have made use of it
//                                File f = new File(Environment.getExternalStorageDirectory()
//                                        + File.separator + "test.jpg");
//                                File f=new File(imageUri.toString());
//                                f.createNewFile();
                                //write the bytes in file
                                File myfile=createImageFile();
                                FileOutputStream fo = new FileOutputStream(myfile);
                                fo.write(outStream.toByteArray());
                                //deletting tthe overwritten file
                                File fdelete = new File(imageUri.getPath());
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        System.out.println("file Deleted :" + imageUri.getPath());
                                    } else {
                                        System.out.println("file not Deleted :" + imageUri.getPath());
                                    }
                                }
                                // remember close de FileOutput
                                fo.close();
                                Uri photoUri=FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID+".provider",myfile);
                                LinearLayout.LayoutParams outgoing_msg_params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                outgoing_msg_params.width = 200;
                                outgoing_msg_params.height=
                                        outgoing_msg_params.leftMargin = 180;
                                outgoing_msg_params.topMargin = 5;
                                outgoing_msg_params.rightMargin = 10;
                                NewImageView = new ImageView(getActivity());
                                NewImageView.setLayoutParams(new TableLayout.LayoutParams(
                                        outgoing_msg_params));
                                NewImageView.setPadding(5, 5, 5, 5);
                                NewImageView.setForegroundGravity(Gravity.LEFT | Gravity.CENTER);
                                NewImageView.setImageBitmap(selectedImage);
                                mylayout.addView(NewImageView);
                                NewImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(Intent.ACTION_VIEW);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(photoUri,IMAGE);
                                        startActivity(intent);
                                    }
                                });

                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//
//                        try {
//                             mImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(filePath));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                        //Bitmap photo = (Bitmap) data.getExtras().get("data");
                        //Uri uri=data.getData();
//                        imageView.setImageBitmap(mImageBitmap);
                        //String src=uri.getPath();
                        // File source=new File(src);
                        //String filename=uri.getLastPathSegment();
                        Log.d(TAG, "File choosen is " + filePath);
                        // String myfile=filePath.substring(6,filePath.length());

                        // Log.d(TAG,"The actual file name is "+filePath.getName());

                        //Bitmap bmp= BitmapFactory.decodeFile(filePath, options);

                    }
                }
            }

    );
    ActivityResultLauncher<Intent> image_ActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                       // openGallery();

                        Intent data = result.getData();
                        final Uri imageUri = data.getData();

                        final InputStream imageStream;
                        ImageView NewImageView;
                        try {
                            imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                NewImageView = new ImageView(getActivity());
                                LinearLayout.LayoutParams outgoing_msg_params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                outgoing_msg_params.width = 200;
                                outgoing_msg_params.height=
                                outgoing_msg_params.leftMargin = 180;
                                outgoing_msg_params.topMargin = 5;
                                outgoing_msg_params.rightMargin = 10;
                                NewImageView.setLayoutParams(new TableLayout.LayoutParams(
                                        outgoing_msg_params));
                                NewImageView.setPadding(5, 5, 5, 5);
                                NewImageView.setForegroundGravity(Gravity.LEFT | Gravity.CENTER);
                                NewImageView.setImageBitmap(selectedImage);
                                mylayout.addView(NewImageView);
                            NewImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                   // Uri photoUri=FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID+".provider",new File(imageUri.getPath()));
                                    Intent intent=new Intent(Intent.ACTION_VIEW);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.setDataAndType(imageUri,IMAGE);
                                    startActivity(intent);
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

//                        Uri uri=data.getData();
//                        String src=uri.getPath();
//                        src = src.replaceAll(" ", "%20");
//                        File source=new File(src);
//                        try {
//                            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                            imageView.setImageBitmap(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }


                       // String filename=uri.getLastPathSegment();
//                        Log.d(TAG,"File choosen is "+src);
//                        Log.d(TAG,"The actual file name is "+source.getName());

                    }
                    }
            }

    );
    ActivityResultLauncher<Intent> document_ActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // openFile();

                        Intent data = result.getData();
                        Uri uri=data.getData();
                        String src=uri.getPath();
                        src = src.replaceAll(" ", "%20");
                        File source=new File(src);
                         String filename=uri.getLastPathSegment();
                        Log.d(TAG,"File choosen is "+src);
                        Log.d(TAG,"The actual file name is "+source.getName());
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                    }
                }
            }

    );

    private void copyFile(File source,File destination) throws IOException {
        FileChannel in=new FileInputStream(source).getChannel();
        FileChannel out=new FileOutputStream(destination).getChannel();
        try {

            in.transferTo(0,in.size(),out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(in!=null){
                in.close();
            }
            if(out!=null){
                out.close();
            }
        }
    }
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
            if (uriFilePath == null && savedInstanceState.getString("uri_file_path") != null) {
                uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));
        }
           // softInputAssist=new SoftInputAssist(getActivity());
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


    }


    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("usertId", userId);
        if (uriFilePath != null)
            savedInstanceState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {
        super.onStart();
        view = getView();
        arrayOfUsers = dashboardListFragment.arrayOfUsers;
        Log.d(TAG,"this is my address: "+getBluetoothMacAddress());
        if (view != null) {
              Button SendFilebtn=(Button)view.findViewById(R.id.attachFilebutton);
            //imageView=(ImageView) view.findViewById(R.id.cameraimage);

            SendFilebtn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                 LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 View popupView=inflater.inflate(R.layout.pick_file,null);
                      if(popupView!=null) {
                          documentImageview=(ImageView)popupView.findViewById(R.id.document) ;
                          cameraImageview=(ImageView)popupView.findViewById(R.id.camera);
                          galleryImageView=(ImageView)popupView.findViewById(R.id.imagefile);
                          //create the popup womdow
                          int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                          int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                          boolean focusable = true;//lets tap outside makes window to dismiss
                          final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                          //show the popup window
                          popupWindow.showAtLocation(view, Gravity.BOTTOM, 4, 90);
                          popupWindow.setElevation(40);
                          //dismiss the window when touched
                          documentImageview.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  openFile();
                                  popupWindow.dismiss();
                              }
                          });
                          cameraImageview.setOnClickListener(new View.OnClickListener() {
                              @RequiresApi(api = Build.VERSION_CODES.M)
                              @Override
                              public void onClick(View v) {
                                  try {
                                      captureImage();
                                      popupWindow.dismiss();
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }
                              }
                          });
                          galleryImageView.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  openGallery();
                                  popupWindow.dismiss();
                              }
                          });
                      }
                  }
              });

            TextView Username = (TextView) view.findViewById(R.id.user_name);
            User user = arrayOfUsers.get((int) userId);
            Username.setText(user.getName());
            send_data=(EditText)view.findViewById(R.id.type_message);
            sendmessagebtn=(Button)view.findViewById(R.id.sendmessgae);
            Busername=user.getName();
            bluetoothDevice = dashboardListFragment.PairedList.get(user.getName());
            Log.d("devicename:", user.getName());
            Log.d("device: ", "" + bluetoothDevice);
            Log.d(TAG,""+MY_UUID_INSECURE);
            mylayout = (LinearLayout) view.findViewById(R.id.my_message_pane_layout);
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

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        softInputAssist.onDestroy();
//    }

    public void setUser(long id) {
        this.userId = id;
    }
    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        try {
            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);

            Object btManagerService = mServiceField.get(bluetoothAdapter);

            if (btManagerService != null) {
                bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
            }
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {

        }
        return bluetoothMacAddress;
    }

    public void openFile(){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      // intent.addCategory(Intent.CATEGORY_OPENABLE);
       //intent.putExtra("CONTENT_TYPE","*/*");
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
        intent=Intent.createChooser(intent,"Choose File to send");
        document_ActivityResultLauncher.launch(intent);
    }
    public void openGallery(){
        String[] mimetypes = {IMAGE};
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.putExtra("CONTENT_TYPE","*/*");
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
        intent=Intent.createChooser(intent,"Choose From Gallery to send");
      image_ActivityResultLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
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
    private void LunchCamera() throws IOException {
        Intent cameraIntent = new Intent();
        File myfile=createImageFile();
        Log.d(TAG,"my file  has been created"+myfile.getPath());

        // uriFilePath = Uri.fromFile(new File(mainDirectory, "IMG_" + calendar.getTimeInMillis()));
        uriFilePath = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", myfile);

        cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,  uriFilePath);
        activityResultLauncher.launch(cameraIntent);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void captureImage() throws IOException {

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},100);
                LunchCamera();
            }
            else
                LunchCamera();

//            File mainDirectory = new File(Environment.getExternalStorageDirectory(), "MyFolder/tmp");
//            if (!mainDirectory.exists())
//                mainDirectory.mkdirs();
//                Calendar calendar = Calendar.getInstance();


        }
    }

//    public String getPath(Uri uri){
//
//    }


//    private void connected(BluetoothSocket mmSocket) {
//        Log.d(TAG, "connected: Starting.");
//
//        // Start the thread to manage the connection and perform transmissions
//        mConnectedThread = new ConnectedThread(mmSocket);
//        mConnectedThread.start();
//    }

//    private class ConnectedThread extends Thread {
//        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            Log.d(TAG, "ConnectedThread: Starting.");
//
//            mmSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//
//            try {
//                tmpIn = mmSocket.getInputStream();
//                tmpOut = mmSocket.getOutputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//
//            int bytes; // bytes returned from read()
//
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                // Read from the InputStream
//                try {
//                    bytes = mmInStream.read(buffer);
//                    final String incomingMessage = new String(buffer, 0, bytes);
//                    Log.d(TAG, "InputStream: " + incomingMessage);
//                   getActivity().runOnUiThread(new Runnable() {
//
//                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                        @Override
//                        public void run() {
//                            Log.d(TAG,"BUSER: "+Busername+"\n bluetooth name :"+bluetoothDevice.getName());
//                            if(Busername.equals(bluetoothDevice.getName())){
//                            Log.d(TAG,"Incoming message: "+incomingMessage);

//                            }
//
//                        }
//                    });
//
//
//                } catch (IOException e) {
//                    //Toast.makeText(getActivity(), "write: Error reading Input Stream" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
//                    break;
//                }
//            }
//        }

   // }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SendMessage() {
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            childLayout = new LinearLayout(getActivity());
            childLayout.setLayoutParams(linearParams);
            childLayout.setOrientation(LinearLayout.VERTICAL);
            String macAddress=getBluetoothMacAddress();
            Log.d(TAG,"This is my mac: "+macAddress);
            if (send_data.getText().length() > 0) {

                byte[] bytes = (macAddress+send_data.getText().toString()).getBytes(Charset.defaultCharset());
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
                send_data.getText().clear();
                DashboardActivity dashboardActivity=new DashboardActivity();
                if(dashboardActivity.mConnectedThread!=null)
                    dashboardActivity.mConnectedThread.write(bytes);
            } else {
                send_data.setError("Message should not be empty!");
            }

        }


//    public void Start_Server() {
//
//        AcceptThread accept = new AcceptThread();
//        accept.start();
//
//    }
//
//    private class AcceptThread extends Thread {
//
//        // The local server socket
//        private final BluetoothServerSocket mmServerSocket;
//
//        public AcceptThread() {
//            BluetoothServerSocket tmp = null;
//
//            // Create a new listening server socket
//            try {
//
//                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(getString(R.string.app_name), MY_UUID_INSECURE);
//
//                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
//            } catch (IOException e) {
//                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
//            }
//
//            mmServerSocket = tmp;
//        }
//
//        public void run() {
//            Log.d(TAG, "run: AcceptThread Running.");
//
//            BluetoothSocket socket = null;
//
//            try {
//                // This is a blocking call and will only return on a
//                // successful connection or an exception
//                Log.d(TAG, "run: RFCOM server socket start.....");
//
//                socket = mmServerSocket.accept();
//
//                Log.d(TAG, "run: RFCOM server socket accepted connection.");
//
//            } catch (IOException e) {
//                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
//            }
//
//            //talk about this is in the 3rd
//            if (socket != null) {
//                connected(socket);
//            }
//
//            Log.i(TAG, "END mAcceptThread ");
//        }
//
//        public void cancel() {
//            Log.d(TAG, "cancel: Canceling AcceptThread.");
//            try {
//                mmServerSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
//            }
//        }
//
//    }


    @Override
    public void onStop() {
        super.onStop();

    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                getActivity().finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
