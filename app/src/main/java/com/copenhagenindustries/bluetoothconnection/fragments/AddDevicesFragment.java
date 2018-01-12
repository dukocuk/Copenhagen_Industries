package com.copenhagenindustries.bluetoothconnection.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.TextView;
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

public class AddDevicesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;
    //Widgets
    private ListView pairedDevicesListView;
    //Bluetooth
    protected BluetoothAdapter myBluetoothAdapter = null;             //The devices bluetoothadapter.

    private DeviceController deviceController;

    private ArrayList<Device> deviceList = new ArrayList<>();

    private DeviceAdapter deviceListAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_device,container,false);
        deviceController = DeviceController.getInstance();
        swipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.add_device_swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(   getResources().getColor(android.R.color.holo_blue_bright),
                                            getResources().getColor(android.R.color.holo_green_light),
                                            getResources().getColor(android.R.color.holo_orange_light),
                                            getResources().getColor(android.R.color.holo_red_light));



        pairedDevicesListView = (ListView) root.findViewById(R.id.add_device_listview);
        pairedDevicesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int t) {
                /*
                Koden i onScroll() er fundet på http://nlopez.io/swiperefreshlayout-with-listview-done-right/
                 */
                int topRowVerticalPosition;
                if(pairedDevicesListView ==null || pairedDevicesListView.getChildCount() == 0) {
                    topRowVerticalPosition = 0;
                }
                else {
                    topRowVerticalPosition = pairedDevicesListView.getChildAt(0).getTop();
                }
                swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        enableBluetooth();
        initializeList();
        getPairedDevices();
        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Device device = (Device) pairedDevicesListView.getItemAtPosition(position);
                try {
                    deviceController.addDevice(device);
                } catch (DeviceControllerNotInstantiatedException e) {
                    e.printStackTrace();
                }
                KnownDevicesListFragment fragment = new KnownDevicesListFragment();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).addToBackStack(null).commit();
            }
        }); //Method called when the device from the list is clicked








        Animation animation = AnimationUtils.makeInChildBottomAnimation(getActivity());
        root.startAnimation(animation);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","requestCode: " + requestCode + " resultCode: " + resultCode);

        // check if the request code is same as what is passed  here it is 1
        if(requestCode==1) {
            initializeList();
            getPairedDevices();
            for (Device d : deviceList) {
                Log.d("device", d.getName());
            }
            Log.d("DeviceList", deviceList.toString());
        }
        else{
            Toast.makeText(getActivity(),"Unable to fetch list",Toast.LENGTH_LONG);
        }

    }

    private void enableBluetooth() {
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
    }

    private void initializeList() {
        if(deviceListAdapter == null) {
            deviceListAdapter = new DeviceAdapter(getActivity(), deviceList);
            pairedDevicesListView.setAdapter(deviceListAdapter);
        }
    }
    //Find paired devices (if any) and add them to the listview.
    private void getPairedDevices()
    {
        final Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size()>0)
        {
            deviceList.clear();
            deviceListAdapter.clear();
            for(BluetoothDevice bt : pairedDevices)
            {
                deviceList.add(new Device(bt.getName(),bt.getAddress()));
            }
            deviceListAdapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(getActivity(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }






    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
                enableBluetooth();
                getPairedDevices();
                deviceListAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

    class DeviceAdapter extends ArrayAdapter<Device> {

        public DeviceAdapter(Context context, ArrayList<Device> devices) {
            super(context, 0, devices);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Device device = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.add_device_list_item, parent, false);
            }
            // Lookup view for data population
            TextView deviceName = (TextView) convertView.findViewById(R.id.add_device_device_name);
            TextView deviceMacAddress = (TextView) convertView.findViewById(R.id.add_device_mac_address);

            deviceName.setText(device.getName());
            deviceMacAddress.setText(device.getMacAddress());

            // Return the completed view to render on screen
            return convertView;
        }
        public void updateDeviceList(ArrayList<Device> list) {
            deviceList.clear();
            deviceList.addAll(list);
            this.notifyDataSetChanged();

        }
    }


}
