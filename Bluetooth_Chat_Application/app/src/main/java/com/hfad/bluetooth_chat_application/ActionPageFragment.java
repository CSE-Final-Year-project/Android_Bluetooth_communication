package com.hfad.bluetooth_chat_application;

import android.bluetooth.BluetoothDevice;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
// This is the class fragment that shows the view of our action page
public class ActionPageFragment extends Fragment {
    public static ArrayList<User> arrayOfUsers = new ArrayList<User>();

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
            bluetoothDevice=dashboardListFragment.PairedList.get(user.getName());
            Log.d("devicename:",user.getName());
            Log.d("device: ",""+bluetoothDevice);
            LinearLayout.LayoutParams all_layout_params =new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            all_layout_params.width=700;

            LinearLayout mylayout = (LinearLayout)view.findViewById(R.id.my_message_pane_layout);
            for(int j=0;j<30;j++) {

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayout.setLayoutParams(linearParams);
                childLayout.setOrientation(LinearLayout.VERTICAL);
                linearParams.setMargins(10,10,10,10);
                LinearLayout.LayoutParams comingmessageParams =new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                comingmessageParams.width=200;
                comingmessageParams.leftMargin=5;
                comingmessageParams.rightMargin=100;
                comingmessageParams.topMargin=5;
                LinearLayout.LayoutParams yourmessageParams =new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                comingmessageParams.width=200;
                comingmessageParams.leftMargin=70;

                comingmessageParams.rightMargin=10;
                TextView outgoing_text_view = new TextView(getActivity());
                TextView Incoming_text_view= new TextView(getActivity());

                Incoming_text_view.setLayoutParams(new TableLayout.LayoutParams(
                        comingmessageParams));

                LinearLayout.LayoutParams outgoing_msg_params=new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                outgoing_msg_params.width=200;
                outgoing_msg_params.leftMargin=120;
                outgoing_msg_params.topMargin=5;
                outgoing_msg_params.rightMargin=10;
                outgoing_text_view.setLayoutParams(new TableLayout.LayoutParams(
                        outgoing_msg_params));

                outgoing_text_view.setTextSize(17);
                outgoing_text_view.setPadding(5, 5, 5, 50);
                outgoing_text_view.setTypeface(Typeface.DEFAULT_BOLD);
                outgoing_text_view.setGravity(Gravity.LEFT | Gravity.CENTER);
                outgoing_text_view.setBackground(getActivity().getDrawable(R.drawable.your_message_shape));
                Incoming_text_view.setBackground(getActivity().getDrawable(R.drawable.text_messages_shape));
                Incoming_text_view.setTextSize(16);
                Incoming_text_view.setPadding(5, 3, 0, 50);
                Incoming_text_view.setTypeface(null, Typeface.ITALIC);
                Incoming_text_view.setGravity(Gravity.LEFT | Gravity.CENTER);

                Incoming_text_view.setText("Incoming message");
                outgoing_text_view.setText("your message");

                childLayout.addView(Incoming_text_view, 0);
                childLayout.addView(outgoing_text_view, 0);

                mylayout.addView(childLayout);
            }


        }
    }

    public void setUser(long id) {
        this.userId = id;
    }

}