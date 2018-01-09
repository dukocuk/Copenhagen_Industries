package com.lasse.bluetoothconnection.backend;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.util.Log;

import com.lasse.bluetoothconnection.exceptions.BTNotEnabledException;
import com.lasse.bluetoothconnection.exceptions.NoBTAdapterException;
import com.lasse.bluetoothconnection.interfacePackage.IBluetooth;

import java.io.IOException;

/**
 * Definition of commands
 */
public class BluetoothConnection implements IBluetooth {

    private ConnecterThread connecterThread;

    //Handler
    private final Handler handlerToNotify;


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
            throw new NoBTAdapterException();           //If the device doesn't have a bluetoothadapter.

        }

        //If it isn't enabled
        else if(!myBluetoothAdapter.isEnabled())
        {
            throw new BTNotEnabledException();          //If the device's bluetooth isn't enabled.
        }

        connecterThread = new ConnecterThread(macAddress,handlerToNotify);      //Instantiate the connecterthread
        if(connecterThread.isAlive()) {                                         //If the socket isn't null and a connection has been established.
            //Starts the connection
            new Thread(connecterThread).start();                                //Begin listening for messages.
        }

    }

    /**
     * Stops the bt connection
     */
    @Override
    public void stopConnection() {
        connecterThread.shutdown();

    }


    /**
     * Get status.
     * Check interface for more details
     */
    @Override
    public void getStatus() throws IOException {
        if(connecterThread.isAlive()) {
            String command =  "<GST:>";
            connecterThread.write(command);
        }


    }

    /**
     * Get shooting status.
     * Check interface for more details.
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
     * - oxygen lvl             <br>
     * - Propane lvl            <br>
     * - battery lvl            <br>
     *
     */
    @Override
    public void getTotalStatus() throws IOException {

        String command = "<GTS:>";
        connecterThread.write(command);
    }

    /**
     * Arms and disarms the weapon.
     * @param state boolean. true if armed false if disamred.
     * @throws IOException
     */
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

    /**
     * If thread isn't null and is alive return true.
     * @return
     */
    @Override
    public boolean isAlive() {
        if(connecterThread != null) {
            return connecterThread.isAlive();
        }
        Log.d("ConnectorThread","ConnectorThread: null");
        return false;
    }


}