package com.lasse.bluetoothconnection.controllers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lasse.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lasse on 21-11-2017.
 */

public class DeviceController implements Serializable {

    private static volatile DeviceController sSoleInstance;
    List<Device> devices;
    Device deviceCurrentlyDisplayed;


    private DeviceController() {
        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        devices = new ArrayList<>();
    }


    public static DeviceController getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time

            synchronized (DeviceController.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) sSoleInstance = new DeviceController();
            }
        }

        return sSoleInstance;
    }

    //If we ever need to serialize the device list. Which we probably need to do, when remembering the devices.
    //Make singleton from serialize and deserialize operation.
    protected DeviceController readResolve() {
        return getInstance();
    }


    public void addDevice(Device device) throws DeviceControllerNotInstantiatedException {
        if (sSoleInstance == null) {
            throw new DeviceControllerNotInstantiatedException();
        }
        devices.add(device);
    }

    public void removeDevice(Device device) throws DeviceControllerNotInstantiatedException {
        if (sSoleInstance == null) {
            throw new DeviceControllerNotInstantiatedException();
        }
        for (Device d : devices) {
            if (d.equals(device)) {
                devices.remove(device);
            }
        }
    }


    public ArrayList<String> getDeviceNameList() {
        ArrayList<String> nameList = new ArrayList<>();
        for (Device d : devices) {
            nameList.add(d.getName());
        }

        return nameList;
    }

    public void setDeviceCurrentlyDisplayed(String name) {
        for (Device d : devices) {
            if (d.getName().equals(name)) {
                deviceCurrentlyDisplayed = d;
            }
        }
    }

    public Device getDeviceCurrentlyDisplayed() {
        return deviceCurrentlyDisplayed;
    }

    public boolean inDeviceList(String MacAddress) {
        for(Device d : devices) {
            if(MacAddress.equals(d.getMacAddress())) {
                return true;
            }
        }
        return false;
    }

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
