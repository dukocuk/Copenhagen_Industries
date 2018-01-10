package com.copenhagenindustries.bluetoothconnection.fragments;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.copenhagenindustries.bluetoothconnection.controllers.Device;
import com.copenhagenindustries.bluetoothconnection.controllers.DeviceController;
import com.copenhagenindustries.bluetoothconnection.R;
import com.copenhagenindustries.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;

import java.util.Set;
import java.util.ArrayList;

public class AddDevicesFragment extends Fragment implements View.OnClickListener{

    //Widgets
    private FloatingActionButton btnPaired;
    private ListView pairedBTDevicesList;

    //Bluetooth
    protected BluetoothAdapter myBluetoothAdapter = null;             //The devices bluetoothadapter.

    private ArrayList<String> devicesName = new ArrayList<>();
    private ArrayList<String> devicesMacAddress = new ArrayList<>();
    private DeviceController deviceController;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_device,container,false);

        //Calling widgets
        btnPaired = (FloatingActionButton) root.findViewById(R.id.add_device_floatingActionButton);
        pairedBTDevicesList = (ListView) root.findViewById(R.id.add_device_listview);
        btnPaired.setOnClickListener(this);

        deviceController = DeviceController.getInstance();


        //if the device has bluetooth
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(myBluetoothAdapter == null)
        {
           Toast.makeText(getActivity(),"NoBTAdapter",Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        //If it isn't enabled
        else if(!myBluetoothAdapter.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent,1);
        }
        pairedDevicesList();

        final BroadcastReceiver bReciever = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Create a new device item
                    devicesName.add(device.getName());
                    devicesMacAddress.add(device.getAddress());

                }
            }
        };


        return root;
    }

    //Find paired devices (if any) and add them to the listview.
    private void pairedDevicesList()
    {
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();


        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                devicesName.add(bt.getName()); //Get the device's name
                devicesMacAddress.add(bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(getActivity(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, devicesName);
        pairedBTDevicesList.setAdapter(adapter);
        pairedBTDevicesList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }



    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> parent, View view, int position, long id)
        {
            String name = devicesName.get(position);
            String macAddress = devicesMacAddress.get(position);
            if(deviceController.inDeviceList(macAddress)) {
                Toast.makeText(getActivity(), "Device already added", Toast.LENGTH_SHORT).show();

                return;
            }
            
            try {
                deviceController.addDevice(new Device(name,macAddress));
                deviceController.saveData(getActivity());
            } catch (DeviceControllerNotInstantiatedException deviceControllerNotInstantiated) {
                deviceControllerNotInstantiated.printStackTrace();
                            
                
            }
            KnownDevicesListFragment fragment = new KnownDevicesListFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).commit();

        }
    };


    @Override
    public void onClick(View v)
    {
        if(v == btnPaired) {
            pairedDevicesList(); //method that will be called
            scanForDevices();
        }
    }

    public void scanForDevices() {

    }

}
