package com.lasse.bluetoothconnection.controllers;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lasse.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DeviceController implements Serializable {

    private static volatile DeviceController sSoleInstance;     //Singleton cass.
    List<Device> devices;                                       //List of devices.
    Device deviceCurrentlyDisplayed;                            //The device currently being showed on the screen.


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
     * @throws DeviceControllerNotInstantiatedException
     */
    public void addDevice(Device device) throws DeviceControllerNotInstantiatedException {
        if (sSoleInstance == null) {
            throw new DeviceControllerNotInstantiatedException();
        }
        devices.add(device);
    }

    /**
     * Remove a device from the list.
     * @param device
     * @throws DeviceControllerNotInstantiatedException
     */
    public void removeDevice(Device device) throws DeviceControllerNotInstantiatedException {
        if (sSoleInstance == null) {
            throw new DeviceControllerNotInstantiatedException();
        }
        devices.remove(device);
    }

    /**
     * Returns an arraylist of devicenames.
     * @return ArrayList<String> nameList
     */
    public ArrayList<String> getDeviceNameList() {
        ArrayList<String> nameList = new ArrayList<>();
        for (Device d : devices) {
            nameList.add(d.getName());
        }

        return nameList;
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
     * @param name
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
     * @param MacAddress
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
     * @param activity
     */
    public void saveData(Activity activity){
        StringBuilder stringBuilder = new StringBuilder();
        for(Device d : devices) {
            stringBuilder.append(d.getName() + ",");
            stringBuilder.append(d.getMacAddress() + ",");
            stringBuilder.append(d.getSerialNumber()+";");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        Log.d("savaData",stringBuilder.toString());
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("deviceList",stringBuilder.toString()).apply();

    }

    /**
     * Load an devicelist saved to memory.
     * @param activity
     * @throws DeviceControllerNotInstantiatedException
     */
    public void loadData(Activity activity) throws DeviceControllerNotInstantiatedException {
        if(!PreferenceManager.getDefaultSharedPreferences(activity).contains("deviceList")) {
            return;
        }
        String deviceList = PreferenceManager.getDefaultSharedPreferences(activity).getString("deviceList", "");

        String[] deviceArray = deviceList.split(";");
        for(String d : deviceArray) {
            String[] info = d.split(",");
            Device toBeAdded = new Device(info[0],info[1]);
            toBeAdded.setSerialNumber(info[2]);
            if(!inDeviceList(toBeAdded.getMacAddress())) {
                addDevice(toBeAdded);
            }

        }
    }



}
