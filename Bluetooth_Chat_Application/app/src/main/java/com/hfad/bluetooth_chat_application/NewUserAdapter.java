package com.hfad.bluetooth_chat_application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class NewUserAdapter extends BaseAdapter {

    BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
PopupActivity popupActivity;
    private Context context;
    Dashboard_Main dashboardMain;
    private ArrayList<NewUser> NewUsers;
    NewUser newUser;
    customButtonListener customListener;
    public interface customButtonListener{
        public void onButtonClickListener(int position,String value);
    }
    public void setCustomButtonListener(customButtonListener listener){
        this.customListener=listener;
    }
    public NewUserAdapter(Context context,ArrayList<NewUser> users) {
        this.context=context;
        this.NewUsers=users;
    }
    @Override
    public int getCount() {
        return NewUsers.size();
    }

    @Override
    public String getItem(int position) {
        return NewUsers.get(position).bluetoothDevice.getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {
        if (converterView == null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            converterView =(View) inflater.inflate(R.layout.newdevice,null);
        }
        TextView username = (TextView) converterView.findViewById(R.id.newuser);
        username.setText(NewUsers.get(position).getBluetoothDevice().getName());
        NewUsers.get(position).button=(Button) converterView.findViewById(R.id.connectbtn);
        converterView.setTag(NewUsers.get(position));
            final String temp=getItem(position);
            Log.d("name of the devices:  ",temp);

            NewUsers.get(position).devicename=temp;

            Log.d("clicked ",temp);
            NewUsers.get(position).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(customListener!=null){
                        customListener.onButtonClickListener(position,temp);
                    }
                    Log.d("clicked ",temp);
                    PairDevices(NewUsers.get(position).bluetoothDevice);

                    ArrayList<BluetoothDevice> DeviceArray = new ArrayList<>();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Set<BluetoothDevice> pairedDevices=mybluetoothAdapter.getBondedDevices();
                            //there are paired devices. get the name and addresses of each paired device
                            for (BluetoothDevice device : pairedDevices) {
                                DeviceArray.add(device);
                                //Log.d("Paired Devices: ", devicename + "  with address " + deviceMacAddress);

                            }
                            if(DeviceArray.contains(NewUsers.get(position).bluetoothDevice)) {
                                Intent DashboardIntent = new Intent(context, Dashboard_Main.class);
                                DashboardIntent.putExtra("PairedDevices", DeviceArray);
                                DashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.d("Devices are ", "" + pairedDevices);
                                context.startActivity(DashboardIntent);
                            }


                        }
                    },10000);


                }
            });

        return converterView;

    }
    public Boolean PairDevices(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            //Log.i("Log", "This is: "+bool.booleanValue());
            //Log.i("Log", "devicesss: "+bdDevice.getName());



        } catch (Exception e) {
            Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        //Log.d("Is paired? ",""+bool.booleanValue());

        return bool.booleanValue();
    };



}
