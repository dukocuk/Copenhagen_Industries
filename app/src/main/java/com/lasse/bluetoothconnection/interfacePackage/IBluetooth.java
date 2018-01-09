package com.lasse.bluetoothconnection.interfacePackage;

import com.lasse.bluetoothconnection.exceptions.BTNotEnabledException;
import com.lasse.bluetoothconnection.exceptions.NoBTAdapterException;

import java.io.IOException;

/**
 * Created by lasse on 02-11-2017.
 */

public interface IBluetooth {

    //Connection related functions
    //*********************************************************************************************

    /**
     * Starts a new connection to the device with the provided macaddress
     * @param macAddress
     *
     */
    void startConnection(String macAddress) throws BTNotEnabledException,NoBTAdapterException, BTNotEnabledException, NoBTAdapterException;

    /**
     * Stops the bt connection
     */
    void stopConnection();

    //Weapon related functions
    //*********************************************************************************************
    /**
     * This functions return an arraylist with the propany, oxygen and battery lvl.
     * @return ArrayList<String>
     */
    void getStatus() throws IOException;

    /**
     *
     * @return
     *      armed status    <br>
     *      Fire mode       <br>
     *      Rate of fire    <br>
     */
    void getShootingStatus() throws IOException;

    //Should probably return a table.
    /**
     * Returns all the information related to the gun: <br>
     *     - Serialnumber           <br>
     *     - gunType                <br>
     *     - GunName                <br>
     *     - Armed state            <br>
     *     - FireMode               <br>
     *     - Rate of fire           <br>
     *     - oxygen lvl             <br>
     *     - Propane lvl            <br>
     *     - battery lvl            <br>
     * @return ArrayList<String>
     */
    void getTotalStatus() throws IOException;

    void setArmedState(boolean state) throws IOException;

    /**
     * Sets the fire mode       <br>
     *  - 0 Semi                <br>
     *  - 1 Burst               <br>
     *  - 2 full automatic      <br>
     * @param state
     */
    void setFireMode(int state) throws IOException;

    /**
     * Set the rate of fire for the weapon. <br>
     * How many times in a second that the weapon should shoot.
     * @param ms
     */
    void setRateOfFire(int ms) throws IOException;

    /**
     * Set the name of the gun.
     * @param gunName
     */
    void setGunName(String gunName) throws IOException;


    /**
     * Sends a message to the gun to keep avoid a timeout <br>
     * (Timeout: Disarms the weapon)
     */
    void keepConnectionAlive() throws IOException;

    /**
     *
     * @return true if connection is alive, or, false if not.
     */
    boolean isAlive();

}