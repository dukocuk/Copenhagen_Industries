package com.example.lasse.arduinobluetooth;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import com.example.lasse.arduinobluetooth.backend.BluetoothConnection;
import com.example.lasse.arduinobluetooth.exceptions.BTNotEnabledException;
import com.example.lasse.arduinobluetooth.exceptions.NoBTAdapterException;
import com.example.lasse.arduinobluetooth.interfacePackage.IBluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;



public class ledControl extends AppCompatActivity implements View.OnClickListener{

    private TextView t1,t2;
    private Button newConn, btnOff, btnDis;
    private String MACAddress = null;                   //Address is given to ledControl by DeviceList

    Handler bluetoothHandler;

    IBluetooth bluetoothConnection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_led_control);

        //receive the MACAddress of the bluetooth device
        Intent intent = getIntent();
        if(intent.getStringExtra("MACAddress") != null) {
            MACAddress = intent.getStringExtra("MACAddress");
            Log.i("MACADDRESS: ",MACAddress);
        }
        else {
            finish();
        }


        bluetoothHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {

                if(msg.what == 0) {
                    HashMap<String,String> hashMap = (HashMap<String,String>) msg.obj;
                    t1.setText("Armedstate: " + hashMap.get("IArm"));


                }
            }
        };
        bluetoothConnection = new BluetoothConnection(bluetoothHandler);

/*
if(btAdapter==null) {
            } else {
      if (btAdapter.isEnabled()) {
      } else {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
      }
    }
 */

        //call the widgtes
        newConn = (Button)findViewById(R.id.OnButton);
        newConn.setText("NewConn");
        newConn.setOnClickListener(this);
        btnOff = (Button)findViewById(R.id.OffButton);
        btnOff.setOnClickListener(this);
        btnDis = (Button)findViewById(R.id.DisButton);
        btnDis.setOnClickListener(this);
        t1 = (TextView)findViewById(R.id.tchange);
        newConnection();
    }



    @Override
    public void onClick(View v) {
        if(v == newConn) {
            try {
                bluetoothConnection.getShootingStatus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(v==btnOff) {
            try {
                bluetoothConnection.getStatus();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Couldn't send command",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else if( v== btnDis) {
            bluetoothConnection.stopConnection();
        }
    }



    public void msg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
    public void newConnection() {
        try {
            bluetoothConnection.startConnection(MACAddress);
        } catch (BTNotEnabledException e) {
            e.printStackTrace();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } catch (NoBTAdapterException e) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }


}
