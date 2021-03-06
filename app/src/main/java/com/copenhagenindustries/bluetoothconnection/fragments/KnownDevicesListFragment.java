package com.copenhagenindustries.bluetoothconnection.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import com.copenhagenindustries.bluetoothconnection.controllers.Device;
import com.copenhagenindustries.bluetoothconnection.controllers.DeviceController;
import com.copenhagenindustries.bluetoothconnection.R;
import com.copenhagenindustries.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class KnownDevicesListFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton addNewDevice;
    private ListView listView;
    private ConstraintLayout noItemView;

    private DeviceController deviceController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_known_devices, container, false);
        getActivity().setTitle(R.string.maindrawer_devices);


        addNewDevice = root.findViewById(R.id.known_devices_floatingActionButton);
        listView = root.findViewById(R.id.known_devices_listView);
        noItemView = root.findViewById(R.id.no_item_view);

        deviceController = DeviceController.getInstance();
        try {
            deviceController.loadData(getActivity());
        } catch (DeviceControllerNotInstantiatedException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.known_devices_unable_to_load_data, Toast.LENGTH_SHORT).show();
        }
        addNewDevice.setOnClickListener(this);

        // devicelist
        ArrayList<Device> deviceList = (ArrayList<Device>) deviceController.getDevices();

        final DeviceAdapter deviceAdapter = new DeviceAdapter(getActivity(), deviceList);
        listView.setAdapter(deviceAdapter);

        if (!deviceList.isEmpty()) noItemView.setVisibility(View.INVISIBLE);
        return root;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v == addNewDevice) {
            AddDevicesFragment fragment = new AddDevicesFragment();
            getFragmentManager().popBackStack();

            Log.d("BackStackEntryCount", "" + getFragmentManager().getBackStackEntryCount());
            getFragmentManager().beginTransaction().replace(R.id.content_main_fragment, fragment).addToBackStack(null).commit();
        }
    }

    class DeviceAdapter extends ArrayAdapter<Device> {
        private HashMap<String, Integer> gunTypeLogos = new HashMap<>();

        public DeviceAdapter(Context context, ArrayList<Device> devices) {
            super(context, 0, devices);

            gunTypeLogos.put("AK47", R.drawable.ic_rifle);
            gunTypeLogos.put("Gun", R.drawable.ic_gun);
            gunTypeLogos.put("Sub", R.drawable.ic_sub);
            gunTypeLogos.put("Sniper", R.drawable.ic_sniper);
            gunTypeLogos.put("Musket", R.drawable.ic_musket);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Device device = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_elem, parent, false);
            }
            // Lookup view for data population
            ImageView deviceLogo = convertView.findViewById(R.id.device_logo);
            TextView deviceName = convertView.findViewById(R.id.device_name);
            Button deviceArmStatus = convertView.findViewById(R.id.device_arm_status);

            // Populate template view using the data object
            deviceLogo.setImageResource(findLogoForGunType(device.getGunType()));
            deviceName.setText(device.getName());
            String armStatusText;
            if (device.connectionAlive()) {
                if (device.isArmedState()) {
                    armStatusText = getString(R.string.known_devices_status_armed);
                    deviceArmStatus.setBackground(getResources().getDrawable(R.drawable.danger));
                } else {
                    armStatusText = getString(R.string.known_devices_status_safe);
                    deviceArmStatus.setBackground(getResources().getDrawable(R.drawable.safe));
                }
            } else {
                armStatusText = getString(R.string.known_devices_status_disconnected);
                deviceArmStatus.setBackground(getResources().getDrawable(R.drawable.disconnected));
            }

            deviceArmStatus.setText(armStatusText);

            // Manage clicks to go to the control fragment
            View.OnClickListener goToControlFragment = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = device.getName();
                    Log.d("onClick: ", name);

                    deviceController.setDeviceCurrentlyDisplayed(name);

                    WeaponControlFragment fragment = new WeaponControlFragment();
                    getFragmentManager().beginTransaction().replace(R.id.content_main_fragment, fragment).addToBackStack(null).commit();
                }
            };
            deviceLogo.setOnClickListener(goToControlFragment);
            deviceName.setOnClickListener(goToControlFragment);

            // Manage arm/disarm clicks
            View.OnClickListener toggleArm = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String buttonText = getResources().getString(R.string.known_devices_status_disconnected);
                    Button b = (Button) v;
                    b.setBackground(getResources().getDrawable(R.drawable.disconnected));

                    try {
                        if (device.connectionAlive()) {
                            boolean state = device.isArmedState();
                            device.setArmedState(!state);
                            if (device.isArmedState()) {
                                b.setBackground(getResources().getDrawable(R.drawable.danger));
                                buttonText = getResources().getString(R.string.known_devices_status_armed);
                            } else {
                                b.setBackground(getResources().getDrawable(R.drawable.safe));
                                buttonText = getResources().getString(R.string.known_devices_status_safe);
                            }
                        }
                    } catch (IOException e) {
                        buttonText = getResources().getString(R.string.known_devices_status_disconnected);
                        b.setBackground(getResources().getDrawable(R.drawable.disconnected));

                    }
                    b.setText(buttonText);
                }
            };
            deviceArmStatus.setOnClickListener(toggleArm);


            // Return the completed view to render on screen
            return convertView;
        }


        // Finds the appropriate logo for a given guntype
        private int findLogoForGunType(String guntype) {
            int gunlogo = R.drawable.ic_help;
            if (gunTypeLogos.get(guntype) != null) {
                gunlogo = gunTypeLogos.get(guntype);
            }
            return gunlogo;
        }
    }


}
