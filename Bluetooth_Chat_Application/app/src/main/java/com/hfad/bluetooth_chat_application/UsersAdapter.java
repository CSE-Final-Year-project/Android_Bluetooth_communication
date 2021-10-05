package com.hfad.bluetooth_chat_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> {

    public UsersAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {
        if (converterView == null)
            converterView = LayoutInflater.from(getContext()).inflate(R.layout.userslist, parent, false);

            TextView username = (TextView) converterView.findViewById(R.id.user);
            username.setText(User.username);
            return converterView;

    }
}
