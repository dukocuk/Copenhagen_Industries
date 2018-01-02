package com.lasse.bluetoothconnection.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.lasse.bluetoothconnection.controllers.DeviceController;
import com.lasse.bluetoothconnection.R;

import java.util.ArrayList;


public class KnownDevicesListFragment extends Fragment implements View.OnClickListener{

    private Button addNewDevice;
    private ListView listView;

    private ArrayAdapter arrayAdapter;

    private DeviceController deviceController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_known_devices,container,false);

        addNewDevice = (Button) root.findViewById(R.id.known_devices_add_new_device);
        listView = (ListView) root.findViewById(R.id.knowndevices_listview);
        deviceController = DeviceController.getInstance();
        addNewDevice.setOnClickListener(this);
        ArrayList<String> list = deviceController.getDeviceNameList();

        arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                String name = (String) arrayAdapter.getItem(position);

                deviceController.setDeviceCurrentlyDisplayed(name);

                WeaponControlFragment fragment = new WeaponControlFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).commit();

            }
        });
        return root;
    }



    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v==addNewDevice) {
            addDeviceFragment fragment = new addDeviceFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).commit();
        }
    }
}
