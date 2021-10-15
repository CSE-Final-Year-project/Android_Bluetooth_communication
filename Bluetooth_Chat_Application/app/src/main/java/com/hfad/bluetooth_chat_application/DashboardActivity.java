package com.hfad.bluetooth_chat_application;

import android.bluetooth.BluetoothDevice;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Set;

//This is the activity which initiates the fragment to get viewed which is sent by dashboardmain as fragment id
public class DashboardActivity extends AppCompatActivity {
    Dashboard_ListFragment dashboardListFragment;
    BluetoothDevice my_device;
    public static final String EXTRA_USER_ID = "Id";
    Set<BluetoothDevice> pairedDevices=MainActivity.pairedDevices;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashbord1_activity);
         Toolbar mytoolbar = (Toolbar) findViewById(R.id.actiontoolbar);
         setSupportActionBar(mytoolbar);
       ActionBar actionBar = getSupportActionBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
       actionBar.setDisplayHomeAsUpEnabled(true);
      // EditText messageedit=(EditText)findViewById(R.id.type_message);
       //TextView Username_texview=(TextView) findViewById(R.id.user_name);
      // String username=Username_texview.getText().toString();
     //  Log.d("devices: ",dashboardListFragment.PairedList.toString());
      // my_device=dashboardListFragment.PairedList.get(username);
       //Log.d("username:",username);

        ActionPageFragment frag = (ActionPageFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_frag);
        // frag.setWorkout(1);
        int UserId = (int) getIntent().getExtras().get(EXTRA_USER_ID);
        frag.setUser(UserId);
    }
     @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_page_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:  {
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
}
