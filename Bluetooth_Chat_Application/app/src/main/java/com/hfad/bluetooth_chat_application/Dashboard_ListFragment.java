package com.hfad.bluetooth_chat_application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.Set;
/*The list fragment class to handle the list of users */
public class Dashboard_ListFragment extends ListFragment{
    static interface Listener{
        void itemClicked(long id);
    }
    Listener listener;
    public UsersAdapter  Useradapter;
    MainActivity Main;
   public static Map<String, BluetoothDevice> PairedList = new HashMap<String,BluetoothDevice>();
 public static    BluetoothAdapter mybluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button Scan;
    String devicename;
    Dashboard_Main dashboardMain;

    User user;
    ArrayList<String> DeviceArray = new ArrayList<>();
    public ListView devicelist;
    Set<BluetoothDevice> pairedDevices;
    public static ArrayList<User> arrayOfUsers=new ArrayList<User>();


    private Bundle savedInstanceState;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(arrayOfUsers.size()>0)
            arrayOfUsers.clear();
        mybluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
         devicename = mybluetoothAdapter.getName();
        if(arrayOfUsers.size()>0)
            arrayOfUsers.clear();
        pairedDevices = Main.pairedDevices;

        //there are paired devices. get the name and addresses of each paired device

        for (BluetoothDevice device : pairedDevices) {
            Log.d("device: ",""+device.getUuids());
            Log.d("address: ",device.getAddress());
            String devicename = device.getName();
            String deviceMacAddress = device.getAddress();
            PairedList.put(devicename, device);
            DeviceArray.add(devicename);
            user=new User(devicename,deviceMacAddress);
            if(!arrayOfUsers.contains(user))
                arrayOfUsers.add(user);
            // Log.d("Paired Devices: ", newUser.username );

        }
        Useradapter=new UsersAdapter(getActivity(),R.layout.userslist,arrayOfUsers);
        for(int i=0;i<arrayOfUsers.size();i++){


        }

        setListAdapter(Useradapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    //This is called when the fragment get attached to the main activity which is dashboard_main actvity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      this.listener = (Listener)context;
    }
//This handles the click on a single view in the list view
    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id) {
        Toast.makeText(getActivity(), arrayOfUsers.get(position).getName(), Toast.LENGTH_SHORT).show();

        if (listener != null) {
            listener.itemClicked(id);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }
}