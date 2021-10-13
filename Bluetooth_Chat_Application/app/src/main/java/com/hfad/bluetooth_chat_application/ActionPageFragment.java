package com.hfad.bluetooth_chat_application;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
// This is the class fragment that shows the view of our action page
public class ActionPageFragment extends Fragment {
    public static ArrayList<User> arrayOfUsers=new ArrayList<User>();

    Dashboard_ListFragment dashboardListFragment;
    private long userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        arrayOfUsers= dashboardListFragment.arrayOfUsers;
        return inflater.inflate(R.layout.action_page_layout, container, false);
    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            userId = savedInstanceState.getLong("userId");
        }
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong("usertId", userId);
    }
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        arrayOfUsers= dashboardListFragment.arrayOfUsers;
        if (view != null) {
            TextView Username = (TextView) view.findViewById(R.id.user_name);
            User user = arrayOfUsers.get((int) userId);
            Username.setText(user.getName());

        }
    }
    public void setUser(long id) {
        this.userId= id;
    }
}