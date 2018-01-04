package com.lasse.bluetoothconnection.fragments;


import android.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
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

import com.lasse.bluetoothconnection.controllers.Device;
import com.lasse.bluetoothconnection.controllers.DeviceController;
import com.lasse.bluetoothconnection.R;
import com.lasse.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;

import java.util.Set;
import java.util.ArrayList;

public class addDeviceFragment extends Fragment implements View.OnClickListener{

    //Widgets
    private Button btnPaired;
    private ListView pairedBTDevicesList;

    //Bluetooth
    protected BluetoothAdapter myBluetoothAdapter = null;             //The devices bluetoothadapter.

    private ArrayList<String> devicesName;
    private ArrayList<String> devicesMacAddress;
    private DeviceController deviceController;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_device,container,false);

        //Calling widgets
        btnPaired = (Button) root.findViewById(com.lasse.bluetoothconnection.R.id.BPair);
        pairedBTDevicesList = (ListView) root.findViewById(R.id.listView);
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
        return root;
    }

    //Find paired devices (if any) and add them to the listview.
    private void pairedDevicesList()
    {
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        devicesName = new ArrayList();
        devicesMacAddress = new ArrayList();

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
        }
    }

}
