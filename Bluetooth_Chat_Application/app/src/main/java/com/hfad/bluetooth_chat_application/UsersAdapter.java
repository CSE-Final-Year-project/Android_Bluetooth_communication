package com.hfad.bluetooth_chat_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UsersAdapter extends BaseAdapter {
private Context context;
private ArrayList<User> users;

public UsersAdapter(Context context,ArrayList<User> users) {
    this.context=context;
    this.users=users;
}
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
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

            converterView =(View) inflater.inflate(R.layout.userslist,null);
        }
            TextView username = (TextView) converterView.findViewById(R.id.user);
            username.setText(users.get(position).getName());
            return converterView;

    }


    }
