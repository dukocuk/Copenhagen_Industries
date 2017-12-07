package com.lasse.bluetoothconnection.controllers;

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


}
