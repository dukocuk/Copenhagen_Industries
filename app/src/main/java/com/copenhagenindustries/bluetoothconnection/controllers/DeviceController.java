package com.copenhagenindustries.bluetoothconnection.controllers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.copenhagenindustries.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;
import com.copenhagenindustries.bluetoothconnection.misc.SharedPreferencesStrings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    public void saveData(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Set<String> stringSet = prefs.getStringSet(SharedPreferencesStrings.DEVICE_LIST,new HashSet<String>());
        prefs.edit().remove(SharedPreferencesStrings.DEVICE_LIST).apply();
        stringSet.clear();
        for(int i = 0;i<devices.size();i++) {
            String dto = devices.get(i).getName() + ";" + devices.get(i).getMacAddress() + ";" + devices.get(i).getSerialNumber();
            stringSet.add(dto);
            Log.d("dto",dto);

        }
        Log.d("saveData Stringset",stringSet.toString());
        prefs.edit().putStringSet(SharedPreferencesStrings.DEVICE_LIST, stringSet).apply();

    }
    /**
     * Load an devicelist saved to memory.
     * @param activity  Activity
     * @throws DeviceControllerNotInstantiatedException DeviceController Not Instantiated.
     */
    public void loadData(Activity activity) throws DeviceControllerNotInstantiatedException{
        if(!PreferenceManager.getDefaultSharedPreferences(activity).contains("deviceList")) {
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Set<String> stringSet = prefs.getStringSet(SharedPreferencesStrings.DEVICE_LIST,new HashSet<String>());
        Log.d("Loaddata:stringset",stringSet.toString());
        if(!stringSet.isEmpty()) {
            for (String dto : stringSet) {
                if(dto==null) {
                    Log.d("Loaddata: dto","null");
                    break;
                }
                if(dto.equals("")){
                    Log.d("Loaddata: dto","String empty");
                    break;
                }
                Log.d("LoadData: dto",dto);

                String[] stringparts = dto.split(";");
                System.out.println(dto);
                Log.d("Stringparts",stringparts.toString());
                Log.d("part 0",stringparts[0]);
                Log.d("part 1",stringparts[1]);
                Log.d("part 2",stringparts[2]);

                Device device = new Device(stringparts[0],stringparts[1]);
                device.setSerialNumber(stringparts[2]);
                Log.d("deviceDTO",device.toString());
                if(!inDeviceList(device.getMacAddress())) {
                    addDevice(device);
                }
                Log.d("doneAdding","doneAdding");

            }
        }

        Log.d("Loaddata: list",devices.toString());

    }




}
