package com.lasse.bluetoothconnection.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class KnownDevicesListFragment extends Fragment implements View.OnClickListener{

    private FloatingActionButton addNewDevice;
    private ListView listView;

    private DeviceController deviceController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_known_devices,container,false);

        addNewDevice = (FloatingActionButton) root.findViewById(R.id.known_devices_floatingActionButton);
        listView = (ListView) root.findViewById(R.id.known_devices_listView);
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
//                Log.d("onItemClick(arrayadapter   )", "onItemClick() called with: parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
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
            AddDevicesFragment fragment = new AddDevicesFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).addToBackStack(null).commit();
        }
    }

    class DeviceAdapter extends ArrayAdapter<Device> {
        private HashMap<String, Integer> gunTypeLogos = new HashMap<>();

        public DeviceAdapter(Context context, ArrayList<Device> devices) {
            super(context, 0, devices);

            gunTypeLogos.put("AK47", R.drawable.ic_rifle);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Device device = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_elem, parent, false);
            }
            // Lookup view for data population
            ImageView deviceLogo = (ImageView) convertView.findViewById(R.id.device_logo);
            TextView deviceName = (TextView) convertView.findViewById(R.id.device_name);
            Button deviceArmStatus = (Button) convertView.findViewById(R.id.device_arm_status);

            // Populate template view using the data object
            deviceLogo.setImageResource(findLogoForGunType(device.getGunType()));
            deviceName.setText(device.getName());
            String armStatusText = "";
            if(device.connectionAlive()) {
                if (device.isArmedState()) {
                    armStatusText = "Armed";
                    deviceArmStatus.setBackgroundColor(getResources().getColor(R.color.colorButtonRed));
                } else {
                    armStatusText = "Safe";
                    deviceArmStatus.setBackgroundColor(getResources().getColor(R.color.colorButtonGreen));
                }
            }
            else {
                armStatusText = "DC";
                deviceArmStatus.setBackgroundColor(getResources().getColor(R.color.colorButtonGrey));
            }

            deviceArmStatus.setText(armStatusText);

            // Manage clicks to go to the control fragment
            View.OnClickListener goToControlFragment = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = (String) device.getName();
                    Log.d("onClick: ", name);

                    deviceController.setDeviceCurrentlyDisplayed(name);

                    WeaponControlFragment fragment = new WeaponControlFragment();
                    getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).addToBackStack(null).commit();
                }
            };
            deviceLogo.setOnClickListener(goToControlFragment);
            deviceName.setOnClickListener(goToControlFragment);

            // Manage arm/disarm clicks
            View.OnClickListener toggleArm = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String buttonText = "DC";
                    Button b = (Button) v;
                    b.setBackgroundColor(getResources().getColor(R.color.colorButtonGrey));

                    try {
                        if(device.connectionAlive()) {
                            boolean state = device.isArmedState();
                            device.setArmedState(!state);
                            if(device.isArmedState()) {
                                b.setBackgroundColor(getResources().getColor(R.color.colorButtonRed));
                                buttonText = "Armed";
                            }
                            else {
                                b.setBackgroundColor(getResources().getColor(R.color.colorButtonGreen));
                                buttonText = "Safe";
                            }
                        }
                    } catch (IOException e) {
                        buttonText = "DC";
                        b.setBackgroundColor(getResources().getColor(R.color.colorButtonGrey));

                    }
                    b.setText(buttonText);
                }
            };
            deviceArmStatus.setOnClickListener(toggleArm);


            // Return the completed view to render on screen
            return convertView;
        }


//        // Finds the appropriate logo for a given guntype
        private int findLogoForGunType(String guntype){
            int gunlogo = R.drawable.ic_help;
            if (gunTypeLogos.get(guntype)!=null) gunlogo = gunTypeLogos.get(guntype);
            return gunlogo;
        }
    }



}
