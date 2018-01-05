package com.lasse.bluetoothconnection.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lasse.bluetoothconnection.controllers.Device;
import com.lasse.bluetoothconnection.controllers.DeviceController;
import com.lasse.bluetoothconnection.R;
import com.lasse.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;

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
        try {
            deviceController.loadData(getActivity());
        } catch (DeviceControllerNotInstantiatedException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to load data", Toast.LENGTH_SHORT).show();
        }
        addNewDevice.setOnClickListener(this);

        // stringlist
//        ArrayList<String> list = deviceController.getDeviceNameList();
//        arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,list);
//
//        listView.setAdapter(arrayAdapter);
//        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//
//                String name = (String) arrayAdapter.getItem(position);
//
//                deviceController.setDeviceCurrentlyDisplayed(name);
//
//                WeaponControlFragment fragment = new WeaponControlFragment();
//                getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).commit();
//
//            }
//        });

        // devicelist
        ArrayList<Device> deviceList = (ArrayList<Device>) deviceController.getDevices();
        final DeviceAdapter deviceAdapter = new DeviceAdapter(getActivity(), deviceList);
        listView.setAdapter(deviceAdapter);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                String name = (String) deviceAdapter.getItem(position).getName();

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

    class DeviceAdapter extends ArrayAdapter<Device> {
        public DeviceAdapter(Context context, ArrayList<Device> devices) {
            super(context, 0, devices);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Device device = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_elem, parent, false);
            }
            // Lookup view for data population
            ImageView deviceLogo = (ImageView) convertView.findViewById(R.id.device_logo);
            TextView deviceName = (TextView) convertView.findViewById(R.id.device_name);
            Button deviceArmStatus = (Button) convertView.findViewById(R.id.device_arm_status);
            // Populate the data into the template view using the data object

            deviceName.setText(device.getName());


            // Return the completed view to render on screen
            return convertView;
        }
    }


}
