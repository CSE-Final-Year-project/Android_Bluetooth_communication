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
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.os.FileUtils;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

// This is the class fragment that shows the view of our action page
public class ActionPageFragment extends Fragment implements Serializable {
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
    SeekBar audioSeek;
    MediaPlayer mMediaPlayer;
    boolean isReleased;
    TextView txtCurrentTime;
    Handler seekHandler;
    Dashboard_ListFragment dashboardListFragment;
    private long userId;
    Bitmap mImageBitmap = null;
   // ImageView imageView;
   private SoftInputAssist softInputAssist;
   private MessageClass messageClass;
   private StringMessage stringMessage;
   private ImageMessage imageMessage;

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
                            int destHeight=origHeight;
                           // imageView.setImageBitmap(selectedImage);
                            if (origWidth > destWidth) {
                                // picture is wider than we want it, we calculate its target height
                                destHeight = origHeight / (origWidth / destWidth);
                            }
                                // we create an scaled bitmap so it reduces the image, not just trim it
                                Bitmap b2 = Bitmap.createScaledBitmap(selectedImage, destWidth, destHeight, false);
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                // compress to the format you want, JPEG, PNG...
                                // 70 is the 0-100 quality percentage
                                b2.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                                // we save the file, at least until we have made use of it
//                                File f = new File(Environment.getExternalStorageDirectory()
//                                        + File.separator + "test.jpg");
//                                File f=new File(imageUri.toString());
//                                f.createNewFile();
                                //write the bytes in file
                                File myfile=createImageFile();
                                FileOutputStream fo = new FileOutputStream(myfile);
                                fo.write(outStream.toByteArray());
                                byte[] myImagebytes=outStream.toByteArray();
                                String messageType="Image";
                                BitmapDataObject bitmapDataObject=new BitmapDataObject();
                               // bitmapDataObject.imageByteArray=imagebytes;

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
                               // outgoing_msg_params.width = 200;
                                //outgoing_msg_params.height=
                                        //outgoing_msg_params.leftMargin = 180;
                               //outgoing_msg_params.topMargin = 5;
                                //outgoing_msg_params.rightMargin = 10;
                                NewImageView = new ImageView(getActivity());
                                NewImageView.setLayoutParams(new TableLayout.LayoutParams(
                                        outgoing_msg_params));
                                NewImageView.setPadding(5, 5, 5, 5);
                                NewImageView.setForegroundGravity(Gravity.LEFT | Gravity.CENTER);
                                NewImageView.setImageBitmap(selectedImage);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            linearParams.height=300;
                            linearParams.topMargin=5;
                            linearParams.width=300;
                            linearParams.leftMargin = 180;
                            childLayout = new LinearLayout(getActivity());
                            NewImageView.setLayoutParams(new TableLayout.LayoutParams(
                                    outgoing_msg_params));
                            String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                            childLayout.setLayoutParams(linearParams);
                            childLayout.setOrientation(LinearLayout.VERTICAL);
                            childLayout.setBackground(getActivity().getDrawable(R.drawable.your_message_shape));
                            childLayout.setPadding(5,5,5,50);

                            TextView timeTextview = new TextView(getContext());
                            timeTextview.setText(timeStamp);
                            childLayout.addView(timeTextview);
                            childLayout.addView(NewImageView);
                            mylayout.addView(childLayout);
                            messageClass=new ImageMessage(messageType,bluetoothAdapter.getName(),bluetoothDevice.getName(),myfile.getName(),myfile, myImagebytes);
                            DashboardActivity.mConnectedThread.write(messageClass);

                            NewImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(Intent.ACTION_VIEW);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(photoUri,IMAGE);
                                        startActivity(intent);
                                    }
                                });


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
//                                outgoing_msg_params.width = 500;
//                                outgoing_msg_params.height=500;

                                outgoing_msg_params.topMargin = 1;
                               // outgoing_msg_params.rightMargin = 10;
                                NewImageView.setLayoutParams(new TableLayout.LayoutParams(
                                        outgoing_msg_params));
                                NewImageView.setPadding(5, 5, 5, 5);
                                NewImageView.setForegroundGravity(Gravity.LEFT | Gravity.CENTER);
                                NewImageView.setImageBitmap(selectedImage);
                            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            linearParams.height=300;
                            linearParams.topMargin=5;
                            linearParams.width=300;
                            linearParams.leftMargin = 180;
                            childLayout = new LinearLayout(getActivity());
                            NewImageView.setLayoutParams(new TableLayout.LayoutParams(
                                    outgoing_msg_params));
                            String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                            childLayout.setLayoutParams(linearParams);
                            childLayout.setOrientation(LinearLayout.VERTICAL);
                            childLayout.setBackground(getActivity().getDrawable(R.drawable.your_message_shape));
                           childLayout.setPadding(5,5,5,50);

                            TextView timeTextview = new TextView(getContext());
                            LinearLayout.LayoutParams time_params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            time_params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            time_params.leftMargin = 200;
                            time_params.topMargin = 0;
                            time_params.bottomMargin = 5;
                            time_params.rightMargin = 10;
//                            timeTextview.setLayoutParams(new TableLayout.LayoutParams(
                             //       time_params));
                            timeTextview.setText(timeStamp);


                            int origWidth = selectedImage.getWidth();
                            int origHeight = selectedImage.getHeight();
                            int destHeight=origHeight;
                            final int destWidth = 600;
                            if (origWidth > destWidth) {
                                // picture is wider than we want it, we calculate its target height
                                destHeight = origHeight / (origWidth / destWidth);
                            }
                            Bitmap b2 = Bitmap.createScaledBitmap(selectedImage,destWidth,destHeight, false);
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            // compress to the format you want, JPEG, PNG...
                            // 70 is the 0-100 quality percentage
                            b2.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                            byte[] imagebytes=outStream.toByteArray();
                               // byte[] imageToSend=encodeToBase64(selectedImage, Bitmap.CompressFormat.JPEG,100);
                            byte[]imageToSend=imagebytes;
                                String messageType="Image";
                                String selectedPath = imageUri.getPath();
                                File f = new File(selectedPath);
                                BitmapDataObject bitmapDataObject=new BitmapDataObject();
                                bitmapDataObject.imageByteArray=imageToSend;
                                TextView filenameTXTV=new TextView(getContext());
                                filenameTXTV.setText(f.getName());
                          // childLayout.addView(filenameTXTV);
                            childLayout.addView(timeTextview);
                            childLayout.addView(NewImageView);

                            mylayout.addView(childLayout);
                                messageClass=new ImageMessage(messageType,bluetoothAdapter.getName(),bluetoothDevice.getName(),f.getName(),f,imageToSend);
                                DashboardActivity.mConnectedThread.write(messageClass);



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

//

                    }
                    }
            }

    );
    ActivityResultLauncher<Intent> document_ActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // openFile();

                        txtCurrentTime=new TextView(getActivity());
                        Intent data = result.getData();
                        Uri uri=data.getData();
                        String src=uri.getPath();
                        src = src.replaceAll(" ", "%20");
                        File source=new File(src);
                         String filename=uri.getLastPathSegment();

                        Log.d(TAG,"File choosen is "+src);
                        Log.d(TAG,"The actual file name is "+filename);
                        try {
//                            byte[] mybytes=new byte[(int)(new File(uri.getPath()).length())];
//                            BufferedInputStream buf=new BufferedInputStream(new FileInputStream(new File(uri.getPath())));
//                            buf.read(mybytes,0,mybytes.length);
//                            buf.close();
                            InputStream iStream=getActivity().getContentResolver().openInputStream(uri);

                            byte[] fileBytes= getBytes(iStream);
                           // Log.d("bytes:",""+fileBytes);
                            String mimeType=getActivity().getContentResolver().getType(uri);
                            Log.d("Type: ",mimeType);
                            if(mimeType.contains("video/")){
                                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                linearParams.height=300;
                                linearParams.topMargin=5;
                                linearParams.width=300;
                                linearParams.leftMargin = 180;
                                RelativeLayout.LayoutParams videoparams = new RelativeLayout.LayoutParams(
                                        300,200);
                               // videoparams .height=200;
                                videoparams.topMargin=1;
                               // videoparams.width=600;
//                                LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(
//                                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                                        LinearLayout.LayoutParams.MATCH_PARENT);
//                                btnparams .height=50;
//                                btnparams.topMargin=1;
//                                btnparams.width=100;


                             //   Log.d("Found?","Video type");
                                childLayout=new LinearLayout(getActivity());
                                childLayout.setLayoutParams(linearParams);
                                childLayout.setOrientation(LinearLayout.VERTICAL);
                                childLayout.setPadding(5,5,5,50);
                                childLayout.setBackground(getActivity().getDrawable(R.drawable.your_message_shape));
                                VideoView Videoview=new VideoView(getActivity());
                                Videoview.setVideoURI(uri);
                               Videoview.setLayoutParams( new TableLayout.LayoutParams(videoparams));
                                String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                                TextView timeTextview = new TextView(getContext());
                                timeTextview.setText(timeStamp);
                                childLayout.addView(Videoview);
                                childLayout.addView(timeTextview);
                                MediaController mc=new MediaController(getActivity());
                                mc.setAnchorView(Videoview);
                                Videoview.setMediaController(mc);
//                                Button btn=new Button(getActivity());
//                                btn.setLayoutParams(btnparams);
                               Videoview.requestFocus();
                               Videoview.seekTo(01);
                                //btn.setText("play");
//                                btn.setBackground(getActivity().getDrawable(R.drawable.play));
//                                childLayout.addView(btn);
                                mylayout.addView(childLayout);

                                 Videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                     @Override
                                     public void onCompletion(MediaPlayer mp) {
                                         //btn.setBackground(getActivity().getDrawable(R.drawable.play));
//                                         mp.stop();
                                         mp.seekTo(0);

                                     }
                                 });


                            }
                            else if(mimeType.contains("audio/")){


                                // MediaController mMediaController;
                                mMediaPlayer = new MediaPlayer();
                                Button audiobttn=new Button(getActivity());
                                     audiobttn.setText("Play");
                                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                linearParams.height=300;
                                linearParams.topMargin=5;
                                linearParams.width=300;
                                linearParams.leftMargin = 180;
                                LinearLayout Audioview=new LinearLayout(getActivity());
                                audiobttn.setLayoutParams(new TableLayout.LayoutParams(100,70));
                                Audioview.setBackground(getActivity().getDrawable(R.drawable.audio));
                                Audioview.setLayoutParams(new TableLayout.LayoutParams(150,80));
                                Handler mHandler = new Handler();
                                // mMediaPlayer=;
                                String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                                TextView timeTextview = new TextView(getContext());
                                timeTextview.setText(timeStamp);
                                childLayout=new LinearLayout(getActivity());
                                //mMediaController.setAnchorView(Audioview);
                                childLayout.setOrientation(LinearLayout.VERTICAL);
                                childLayout.setLayoutParams(linearParams);
                                childLayout.setBackground(getActivity().getDrawable(R.drawable.your_message_shape));
                                childLayout.addView(Audioview);
                                audioSeek=new SeekBar(getActivity());
                                childLayout.addView(audioSeek);
                                childLayout.addView(txtCurrentTime);
                                childLayout.addView(audiobttn);
                                childLayout.addView(timeTextview);
                                childLayout.setPadding(5,5,5,60);
                                mylayout.addView(childLayout);
                                 isReleased = false;
                               // mMediaController=new MediaController(getActivity());
                                try{

                                   // mp.setDataSource(source.getPath());//Write your location here

                                    mMediaPlayer.reset();
                                    mMediaPlayer.setDataSource(getContext(),uri);//The location of my audio
                                    mMediaPlayer.prepare();
                                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    audioSeek.setMax(mMediaPlayer.getDuration());
                                    //mMediaPlayer.start();
                                    mMediaPlayer.seekTo(01);
                                   // final MediaPlayer[] finalMMediaPlayer = {mMediaPlayer[0][0]};

                                    audiobttn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mMediaPlayer != null && !isReleased) {

                                                if (!mMediaPlayer.isPlaying()) {
                                                    mMediaPlayer.start();
                                                    updateSeekBar();
                                                    audiobttn.setText("Pause");

                                                } else if (mMediaPlayer.isPlaying()) {
                                                    mMediaPlayer.pause();
                                                    audiobttn.setText("Resume");
                                                }

                                            }
                                        }
                                    });
                                    MediaPlayer finalMMediaPlayer1 = mMediaPlayer;
                                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            finalMMediaPlayer1.seekTo(0);
                                            updateSeekBar();
                                            //mMediaPlayer.stop();
                                          //  mMediaPlayer[0][0].stop();
                                          //  mMediaPlayer[0][0].release();
//                                            mMediaPlayer[0][0].reset();

//                                            mMediaPlayer=null;
//                                            isReleased[0][0] =true;
                                            audiobttn.setText("Play");


                                        }
                                    });

                                 audioSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                     @Override
                                     public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                         if(mMediaPlayer!=null && fromUser) {
                                             milliSecondsToTimer((long) progress);
                                             updateSeekBar();
                                             mMediaPlayer.seekTo(progress);
                                         }
                                     }

                                     @Override
                                     public void onStartTrackingTouch(SeekBar seekBar) {

                                     }

                                     @Override
                                     public void onStopTrackingTouch(SeekBar seekBar) {

                                     }
                                 });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                finally {

                                }



                                //mylayout.addView(audiobttn);

                            }


                            Toast.makeText(getContext(), "This is the type: "+mimeType, Toast.LENGTH_SHORT).show();
                            messageClass=new DocumentFileMessage("document", bluetoothAdapter.getName(), bluetoothDevice.getName(),source.getName(), mimeType, source, fileBytes);
                            DashboardActivity.mConnectedThread.write(messageClass);
                          iStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
//                            mp.stop();
//                            mp.release();
                        }
//                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(intent);



                    }
                }
            }

    );
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(mMediaPlayer!=null&& !isReleased && !isDetached())
            updateSeekBar();
        }
    };
    private void updateSeekBar() {
        seekHandler=new Handler();
        audioSeek.setProgress(mMediaPlayer.getCurrentPosition());
        txtCurrentTime.setText(milliSecondsToTimer(mMediaPlayer.getCurrentPosition()));
        seekHandler.postDelayed(runnable, 50);
    }
    private String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
    private byte[] getBytes(InputStream inputStream) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        int bufferSize=1024;
        byte[] buffer=new byte[bufferSize];
        int len=0;
        while((len=inputStream.read(buffer))!=-1){
            byteArrayOutputStream.write(buffer,0,len);
        }
        return byteArrayOutputStream.toByteArray();
    }

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
  public void onDestroy() {
       super.onDestroy();
       if(mMediaPlayer!=null) {
           if(seekHandler!=null && runnable!=null)
           seekHandler.removeCallbacksAndMessages(runnable);
           mMediaPlayer.release();
           isReleased = true;

       }
//        softInputAssist.onDestroy();
   }
    private String getDeviceUUID(){
        String myUuid="";
        String TotalUuid="";
        try{
            Method getUuidsMethod=BluetoothAdapter.class.getDeclaredMethod("getUuids",null);
            ParcelUuid[] uuids=(ParcelUuid[]) getUuidsMethod.invoke(bluetoothAdapter,null);
            if(uuids!=null){
                for(ParcelUuid uuid:uuids){
                   TotalUuid+=uuid;
                    Log.d(TAG,"UUId: "+uuid);
                }
                myUuid=((uuids[0]).getUuid()).toString();
                Log.d(TAG,"UUID length: "+myUuid.length());
                Log.d(TAG,"TotalUUid is: "+TotalUuid+" with the length: "+TotalUuid.length());

            }
            else
                Log.d(TAG,"No UUID found! ");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return myUuid;

    }

    public void setUser(long id) {
        this.userId = id;
    }
    private String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        Log.d(TAG,"my Uuid is :"+getDeviceUUID());
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
        String[] mimetypes = {DOC,DOCX,XLS,TEXT,PDF,VIDEO,AUDIO};
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
        intent.putExtra("CONTENT_TYPE","*/*");
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimetypes);
        intent=Intent.createChooser(intent,"Choose From Gallery to send");
      image_ActivityResultLauncher.launch(intent);
    }

    public File createImageFile() throws IOException {
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
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
                LunchCamera();
            } else
                LunchCamera();
        }
    }

private String GetIdentifiedId(){
        String usedId=bluetoothAdapter.getName()+Busername;
        return usedId;

}

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
            String messageType="StringMessage";
            if (send_data.getText().length() > 0) {
                Log.d(TAG,"my address:"+bluetoothDevice.getAddress());


                byte[] bytes = (GetIdentifiedId()+send_data.getText().toString()).getBytes(Charset.defaultCharset());
//               StmessageBytes=send_data.getText().toString().getBytes(Charset.defaultCharset());
                String message=send_data.getText().toString();
                stringMessage=new StringMessage(messageType,bluetoothAdapter.getName(),bluetoothDevice.getName(),message);

                outgoing_text_view = new TextView(getActivity());
                LinearLayout.LayoutParams outgoing_msg_params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                outgoing_msg_params.width =WindowManager.LayoutParams.WRAP_CONTENT;
                outgoing_msg_params.leftMargin = 180;
                outgoing_msg_params.topMargin = 2;
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
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                TextView timeTextview=new TextView(getActivity());
                LinearLayout.LayoutParams time_params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                time_params.width =WindowManager.LayoutParams.WRAP_CONTENT;
                time_params.leftMargin = 200;
                time_params.topMargin = 0;
                time_params.bottomMargin=5;
                time_params.rightMargin = 10;
                timeTextview.setLayoutParams(new TableLayout.LayoutParams(
                        time_params));
                timeTextview.setText(timeStamp);
                childLayout.addView(timeTextview);
                mylayout.addView(childLayout);
                send_data.getText().clear();
                Log.d("my data: ",stringMessage.getMessage());
                byte[] mybytes= toByteArray(stringMessage);
                DashboardActivity dashboardActivity=new DashboardActivity();
                if(dashboardActivity.mConnectedThread!=null)
                   // dashboardActivity.mConnectedThread.write(bytes);
                    dashboardActivity.mConnectedThread.write(stringMessage);
            } else {
                send_data.setError("Message should not be empty!");
            }

        }
    public static byte[] toByteArray(Object obj)
    {
        byte[] bytes = null;
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();

            return bos.toByteArray();
        }
        catch(Exception e)
        {
            Log.e("Bluetooth", "Cast exception at sending end ...");
        }

        return bytes;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        image.compress(compressFormat,quality,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }



}
