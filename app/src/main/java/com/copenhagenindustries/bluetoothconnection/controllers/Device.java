package com.copenhagenindustries.bluetoothconnection.controllers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.copenhagenindustries.bluetoothconnection.backend.BluetoothConnection;
import com.copenhagenindustries.bluetoothconnection.exceptions.BTNotEnabledException;
import com.copenhagenindustries.bluetoothconnection.exceptions.NoBTAdapterException;
import com.copenhagenindustries.bluetoothconnection.interfacePackage.IBluetooth;
import com.copenhagenindustries.bluetoothconnection.interfacePackage.IObserver;
import com.copenhagenindustries.bluetoothconnection.interfacePackage.ISubject;
import com.copenhagenindustries.bluetoothconnection.misc.HandlerStates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Device implements ISubject{

    //Bluetoothconnection related information
    private String macAddress;
    private IBluetooth connection;

    //Receive information and pass information on.
    private List<IObserver> listToNotify;
    private Handler deviceHandler;
    private HandlerStates handlerStates = new HandlerStates();


    //Gun information
    private String name;
    private String serialNumber;
    private String gunType;
    private boolean armedState;
    private int fireMode;
    private int rateOfFire;
    private String oxygen;
    private String propane;
    private String battery;


    public Device(final String name, String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
        this.gunType = "AK47";
        fireMode = -1;
        rateOfFire = -1;
        listToNotify = new ArrayList<>();

        deviceHandler = new InformationHandler();
    }


    /**
     *     Resets the device information.
     */
    private void resetDynamicInformation() {
        armedState = false;
        fireMode = 0;
        rateOfFire = 0;
        oxygen = "N/A";
        propane = "N/A";
        battery = "N/A";
    }


    /**
     * Start the bluetoothconnection
     * @throws BTNotEnabledException
     * @throws NoBTAdapterException
     */
    public void startConnection() throws BTNotEnabledException, NoBTAdapterException {
        connection = new BluetoothConnection(deviceHandler);
        connection.startConnection(macAddress);
    }

    /**
     * If the connection isn't null, returns the connections status.
     * @return
     */
    public boolean connectionAlive() {
        if(connection == null) {
            return false;
        }
        return connection.isAlive();
    }

    /**
     * Get total status of weapon.
     */
    public void getTotalStatus() {
        try {
            connection.getTotalStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) throws IOException {
        this.name = name;
        if(connectionAlive()) {
            connection.setGunName(name);
        }
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isArmedState() {
        return armedState;
    }
    public void setArmedState(boolean armedState) throws IOException {
        this.armedState = armedState;
        if(connectionAlive()) {
            connection.setArmedState(armedState);
        }
    }

    public int getFireMode() {
        return fireMode;
    }
    public void setFireMode(int fireMode) throws IOException {
        this.fireMode = fireMode;
        if(connectionAlive()) {
            connection.setFireMode(fireMode);
        }
    }

    public int getRateOfFire() {
        return rateOfFire;
    }

    public void setRateOfFire(int rateOfFire) throws IOException {
        this.rateOfFire = rateOfFire;
        if(connectionAlive()) {

        }
    }

    public String getOxygen() {
        return oxygen;
    }

    public String getPropane() {
        return propane;
    }

    public String getBattery() {
        return battery;
    }

    public String getGunType() {
        String[] temp = {"AK47","Gun","Sub","Sniper","Musket"};
        Random random = new Random();
        int i = random.nextInt(5);
        //int i = (int) Math.random() * 5;
        Log.d("gunType int","" + i);

        return temp[i];
    }

    public String getMacAddress() { return macAddress;}


    /**
     * Add observer to list to be notified.
     * @param observer
     */
    @Override
    public void addToObserverList(IObserver observer) {
        listToNotify.add(observer);
    }

    /**
     * Remove an observer from the observer-list.
     * @param observer
     */
    @Override
    public void removeFromObserverList(IObserver observer) {
        if(listToNotify.contains(observer)) {
            listToNotify.remove(observer);
        }
    }

    /**
     * Notify every observer
     */
    @Override
    public void notifyList() {
        for(IObserver o : listToNotify) {
            o.notifyObs();
        }
    }

    /**
     * Splits the received command into different pieces and updates device information according to received information.
     * @param info Command received.
     */
    private void updateInformation(String info) {
        Log.d("info+mac",macAddress + " " + info);
        String[] informationReceived = info.split(";");
        String[] infopart;
        try {
            infopart = informationReceived[1].split(",");
        }
        catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        for(String parts : infopart) {
            String part[] = parts.split(":");

            Log.d("infoTag", part[1]);
            switch (part[0]) {
                case "IN": {
                    this.name = part[1];
                    break;
                }
                case "ISN": {
                    this.serialNumber = part[1];
                    break;
                }
                case "IGT": {
                    this.gunType = part[1];
                    break;
                }
                case "IArm": {
                    if (part[1].equals("1")) {
                        this.armedState = true;
                    } else if (part[1].equals("0")) {
                        this.armedState = false;
                    }
                    break;
                }
                case "IFM": {
                    this.fireMode = Integer.parseInt(part[1]);
                    break;
                }
                case "IRoF": {
                    this.rateOfFire = Integer.parseInt(part[1]);
                    break;
                }
                case "IO": {
                    this.oxygen = part[1];
                    Log.d("oxygen", part[1]);
                    break;
                }
                case "IP": {
                    this.propane = part[1];
                    break;
                }
                case "IB": {
                    this.battery = part[1];
                    break;
                }
            }


        }
        notifyList();               //Notify observers that device information has changed.
    }

    /**
     * Custom handler.
     */
    class InformationHandler extends Handler {
        private StringBuilder recDataString = new StringBuilder();
        private String messageReceived;

        @Override
        public void handleMessage(final Message msg) {
            if(msg.what == handlerStates.getHandlerStateInformationReceived()) { //New characters from inputstream.
                super.handleMessage(msg);

                messageReceived = (String) msg.obj;  //Get the new characters
                recDataString.append(messageReceived);
                int endOfLineIndex = recDataString.indexOf(">");        //Index of end char.
                if (endOfLineIndex > 0) {                               //If we have the end char, handle the message.
                    String dataInPrint = recDataString.substring(1, endOfLineIndex);    //Remove 1st char and get the rest of the msg.
                    Log.i("dataInPrint", dataInPrint);
                    if(dataInPrint.equals("")) {            //If we received an empty msg.
                        return;
                    }
                    updateInformation(dataInPrint);             //Act according to the msg.
                    //recDataString.delete(recDataString.indexOf("<"),recDataString.indexOf(">")); //Clear the stringbuilder.
                    recDataString = new StringBuilder();      //This works if line above doesn't. Test atm.
                }
            }
            else if(msg.what==handlerStates.getHandlerStateDisconnected()) {        //Notify observers that the connection has been lost.
                resetDynamicInformation();
                for(IObserver o : listToNotify) {
                    o.notifyObsConnectionLost();
                }
            }

        }
    }





}
