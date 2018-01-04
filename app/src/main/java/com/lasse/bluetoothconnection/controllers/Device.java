package com.lasse.bluetoothconnection.controllers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lasse.bluetoothconnection.backend.BluetoothConnection;
import com.lasse.bluetoothconnection.exceptions.BTNotEnabledException;
import com.lasse.bluetoothconnection.exceptions.NoBTAdapterException;
import com.lasse.bluetoothconnection.interfacePackage.IBluetooth;
import com.lasse.bluetoothconnection.interfacePackage.IObserver;
import com.lasse.bluetoothconnection.interfacePackage.ISubject;
import com.lasse.bluetoothconnection.misc.HandlerStates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
        fireMode = -1;
        rateOfFire = -1;
        listToNotify = new ArrayList<>();

        deviceHandler = new InformationHandler();
    }



    private void resetDynamicInformation() {
        armedState = false;
        fireMode = 0;
        rateOfFire = 0;
        oxygen = "N/A";
        propane = "N/A";
        battery = "N/A";
    }



    public void startConnection() throws BTNotEnabledException, NoBTAdapterException {
        connection = new BluetoothConnection(deviceHandler);
        connection.startConnection(macAddress);
    }

    public boolean connectionAlive() {
        return connection.isAlive();
    }
    public void getTotalStatus() {
        try {
            connection.getTotalStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getName() {
        return name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public boolean isArmedState() {
        return armedState;
    }
    public void setArmedState(boolean armedState) throws IOException {
        this.armedState = armedState;
        connection.setArmedState(armedState);
    }

    public int getFireMode() {
        return fireMode;
    }
    public void setFireMode(int fireMode) throws IOException {
        this.fireMode = fireMode;
        connection.setFireMode(fireMode);
    }

    public int getRateOfFire() {
        return rateOfFire;
    }
    public void setRateOfFire(int rateOfFire) throws IOException {
        this.rateOfFire = rateOfFire;
        connection.setRateOfFire(rateOfFire);
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
        return gunType;
    }





    @Override
    public void addToObserverList(IObserver observer) {
        listToNotify.add(observer);
    }

    @Override
    public void removeFromObserverList(IObserver observer) {
        if(listToNotify.contains(observer)) {
            listToNotify.remove(observer);
        }
    }

    @Override
    public void notifyList() {
        for(IObserver o : listToNotify) {
            o.notifyObs();
        }
    }


    private void updateInformation(String info) {
        Log.d("info",info);
        String[] informationReceived = info.split(";");
        String[] infopart = informationReceived[1].split(",");
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
                    Log.d("Oxygen", part[1]);
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
        notifyList();
    }
    class InformationHandler extends Handler {
        private StringBuilder recDataString = new StringBuilder();
        private String messageReceived;

        @Override
        public void handleMessage(final Message msg) {
            if(msg.what == handlerStates.getHandlerStateInformationReceived()) {
                super.handleMessage(msg);

                if (msg == null) {
                    return;
                }

                messageReceived = (String) msg.obj;
                recDataString.append(messageReceived);
                int endOfLineIndex = recDataString.indexOf(">");
                if (endOfLineIndex > 0) {
                    String dataInPrint = recDataString.substring(1, endOfLineIndex);
                    Log.i("dataInPrint", dataInPrint);
                    if(dataInPrint == "") {
                        return;
                    }
                    updateInformation(dataInPrint);
                    recDataString = new StringBuilder();
                }
            }
            else if(msg.what==handlerStates.getHandlerStateDisconnected()) {
                resetDynamicInformation();
                for(IObserver o : listToNotify) {
                    o.notifyObsConnectionLost();
                }
            }
        }
    }





}
