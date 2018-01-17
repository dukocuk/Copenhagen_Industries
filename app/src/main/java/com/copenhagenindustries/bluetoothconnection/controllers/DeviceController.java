package com.copenhagenindustries.bluetoothconnection.controllers;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.copenhagenindustries.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;
import com.copenhagenindustries.bluetoothconnection.misc.SharedPreferencesStrings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DeviceController implements Serializable {

    private static volatile DeviceController sSoleInstance;     //Singleton cass.
    private List<Device> devices;                                       //List of devices.
    private Device deviceCurrentlyDisplayed;                            //The device currently being showed on the screen.


    private DeviceController() {
        //Prevent from the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        devices = new ArrayList<>();        //Instantiate deviceList.
    }


    public static DeviceController getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time                 //If singleton is null, proceed.

            synchronized (DeviceController.class) {   //Check for the second time.  and avoid raceconditions. yay
                //if there is no instance available... create new one
                if (sSoleInstance == null) sSoleInstance = new DeviceController();
            }
        }

        return sSoleInstance;
    }



    /**
     * Add a device to the list.
     * @param device device to be added.
     * @throws DeviceControllerNotInstantiatedException DeviceController not instantiated.
     */
    public void addDevice(Device device) throws DeviceControllerNotInstantiatedException {
        if (sSoleInstance == null) {
            throw new DeviceControllerNotInstantiatedException();
        }
        devices.add(device);

    }

    /**
     * Remove a device from the list.
     * @param device device device to be added.
     * @throws DeviceControllerNotInstantiatedException DeviceController not instantiated.
     */
    public void removeDevice(Device device) throws DeviceControllerNotInstantiatedException {
        if (sSoleInstance == null) {
            throw new DeviceControllerNotInstantiatedException();
        }
        devices.remove(device);
    }


    /**
     * getter for devicelist
     * @return List<Device> devices
     */
    public List<Device> getDevices() {
        return devices;
    }

    /**
     * Sets the device Currently displayed on screen.
     * @param name Device name
     */
    public void setDeviceCurrentlyDisplayed(String name) {
        for (Device d : devices) {
            if (d.getName().equals(name)) {
                deviceCurrentlyDisplayed = d;
            }
        }
    }

    /**
     * Getter
     * @return Device.
     */
    public Device getDeviceCurrentlyDisplayed() {
        return deviceCurrentlyDisplayed;
    }

    /**
     * If macAddress already exists in devicelist return true.
     * @param MacAddress    MacAddress of the device
     * @return      True if in devicelist
     */
    public boolean inDeviceList(String MacAddress) {
        for(Device d : devices) {
            if(MacAddress.equals(d.getMacAddress())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Save the deviceList
     * @param activity Activity
     */
    public void saveData(Activity activity){
        StringBuilder stringBuilder = new StringBuilder();
        for(Device d : devices) {
            stringBuilder.append(String.format("%s,",d.getName()));
            stringBuilder.append(String.format("%s,",d.getMacAddress()));
            stringBuilder.append(String.format("%s;",d.getSerialNumber()));
        }
        if(!devices.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        Log.d("savaData",stringBuilder.toString());
        PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(SharedPreferencesStrings.DEVICE_LIST).apply();
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(SharedPreferencesStrings.DEVICE_LIST,stringBuilder.toString()).apply();

    }

    /**
     * Load an devicelist saved to memory.
     * @param activity  Activity
     * @throws DeviceControllerNotInstantiatedException DeviceController Not Instantiated.
     */
    public void loadData(Activity activity) throws DeviceControllerNotInstantiatedException {
        if(!PreferenceManager.getDefaultSharedPreferences(activity).contains("deviceList")) {
            return;
        }
        String deviceList = PreferenceManager.getDefaultSharedPreferences(activity).getString(SharedPreferencesStrings.DEVICE_LIST, "");
        if(deviceList.equals("")) {
            return;
        }

        Log.d("deviceList",deviceList);
        String[] deviceArray = deviceList.split(";");
        if(deviceArray.length!=0) {
            for(String d : deviceArray) {
                String[] info = d.split(",");
                Device toBeAdded = new Device(info[0], info[1]);
                toBeAdded.setSerialNumber(info[2]);
                if (!inDeviceList(toBeAdded.getMacAddress())) {
                    addDevice(toBeAdded);
                }
            }

        }
    }



}
