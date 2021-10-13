package com.hfad.bluetooth_chat_application;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
//This is the activity which initiates the fragment to get viewed which is sent by dashboardmain as fragment id
public class DashboardActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "Id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashbord1_activity);

        ActionPageFragment frag = (ActionPageFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_frag);
        // frag.setWorkout(1);
        int UserId = (int) getIntent().getExtras().get(EXTRA_USER_ID);
        frag.setUser(UserId);
    }
}
