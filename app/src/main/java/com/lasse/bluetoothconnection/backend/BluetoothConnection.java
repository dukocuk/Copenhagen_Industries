package com.lasse.bluetoothconnection.backend;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

import com.lasse.bluetoothconnection.exceptions.BTNotEnabledException;
import com.lasse.bluetoothconnection.exceptions.NoBTAdapterException;
import com.lasse.bluetoothconnection.interfacePackage.IBluetooth;

import java.io.IOException;


public class BluetoothConnection implements IBluetooth {

    private ConnecterThread connecterThread;


    //private String commands[] = {"GST","GSS","GTS","SAS","SFM","SRF","SGN","KCA"};


    //Handler
    private final Handler handlerToNotify;

    // TODO: Check for null values in cmd
    // TODO: How do we know its a GST string. How is the message formatted. Arduino puzzle yay.
    // Message format: <GST;IArm:1,IFM:0,IRoF:10>
    public BluetoothConnection(Handler handler) {
        this.handlerToNotify = handler;

    }


    /**
     * Starts a new connection to the device with the provided macaddress
     * @param macAddress : the mac address of the device.
     * @throws BTNotEnabledException BluetoothNotEnabled
     * @throws NoBTAdapterException  Device has no Bluetooth adapter.
     */
    @Override
    public void startConnection(String macAddress) throws BTNotEnabledException, NoBTAdapterException {
        //if the device has bluetooth
        System.out.println("Reached startConnection Method");
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(myBluetoothAdapter == null)
        {
            throw new NoBTAdapterException();

        }

        //If it isn't enabled
        else if(!myBluetoothAdapter.isEnabled())
        {
            throw new BTNotEnabledException();
        }

        connecterThread = new ConnecterThread(macAddress,handlerToNotify);
        if(connecterThread.isAlive()) {
            //Starts the connection
            new Thread(connecterThread).start();
        }
        System.out.println("Reached end of startConnection Method");

    }

    /**
     * Stops the bt connection
     */
    @Override
    public void stopConnection() {
        connecterThread.shutdown();

    }


    /**
     * This functions return an arraylist with the propany, oxygen and battery lvl.
     *
     *
     */
    @Override
    public void getStatus() throws IOException {
        if(connecterThread.isAlive()) {
            String command =  "<GST:>";
            connecterThread.write(command);
        }


    }

    /**
     *
     */
    @Override
    public void getShootingStatus() throws IOException {
        String command = "<GSS:>";
        connecterThread.write(command);
    }

    /**
     * Returns all the information related to the gun: <br>
     * - Serialnumber           <br>
     * - gunType                <br>
     * - GunName                <br>
     * - Armed state            <br>
     * - FireMode               <br>
     * - Rate of fire           <br>
     * - Oxygen lvl             <br>
     * - Propane lvl            <br>
     * - battery lvl            <br>
     *
     */
    @Override
    public void getTotalStatus() throws IOException {

        String command = "<GTS:>";
        connecterThread.write(command);
    }

    @Override
    public void setArmedState(boolean state) throws IOException {
        String command = "<SAS:";
        if(state) {
            command += 1;
        }
        else {
            command +=0;
        }
        command += ">";
        connecterThread.write(command);

    }

    /**
     * Sets the fire mode       <br>
     * - 0 Semi                <br>
     * - 1 Burst               <br>
     * - 2 full automatic      <br>
     *
     * @param state FireModeState, 0,1 or 2.
     */
    @Override
    public void setFireMode(int state) throws IOException {
        String command = "<SFM:" + state + ">";
        connecterThread.write(command);
    }

    /**
     * Set the rate of fire for the weapon. <br>
     * How many times in a second that the weapon should shoot.
     *
     * @param ms The delay between each shot in milliseconds.
     */
    @Override
    public void setRateOfFire(int ms) throws IOException {
        String command = "<SRF:" + ms + ">";
        connecterThread.write(command);
    }

    /**
     * Set the name of the gun.
     *
     * @param gunName  Name of the gun
     */
    @Override
    public void setGunName(String gunName) throws IOException {
        String command = "<SGN:" + gunName + ">";
        connecterThread.write(command);
    }

    /**
     * Sends a message to the gun to keep avoid a timeout <br>
     * (Timeout: Disarms the weapon)
     */
    @Override
    public void keepConnectionAlive() throws IOException {
        String command = "<KCA:>";
        connecterThread.write(command);
    }

    @Override
    public boolean isAlive() {
        return connecterThread.isAlive();
    }


}