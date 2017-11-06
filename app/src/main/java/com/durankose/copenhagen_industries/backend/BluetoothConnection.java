package com.durankose.copenhagen_industries.backend;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.util.Log;


import com.durankose.copenhagen_industries.exceptions.BTNotEnabledException;
import com.durankose.copenhagen_industries.exceptions.NoBTAdapterException;
import com.durankose.copenhagen_industries.interfacePackage.IBluetooth;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by lasse on 02-11-2017.
 */

public class BluetoothConnection implements IBluetooth {

    private ConnecterThread connecterThread;

    //Bluetooth
    private BluetoothAdapter myBluetoothAdapter;             //The devices bluetoothadapter.


    private String commands[] = {"GST","GSS","GTS","SAS","SFM","SRF","SGN","KCA"};


    //Handler
    public final Handler handlerToNotify;
    private Handler bluetoothHandler;
    private StringBuilder recDataString;

    private final int gstState = 0;                 //HandlerState. Received information for function getStatus
    private final int gssState = 1;                 //HandlerState. Received information for function getShootingStatus
    private final int gtsState = 2;                 //HandlerState. Received information for functions getTotalStatus


    // TODO: Check for null values in cmd
    // TODO: How do we know its a GST string. How is the message formatted. Arduino puzzle yay.
    // Message format: <GST;IArm:1,IFM:0,IRoF:10>
    public BluetoothConnection(Handler handler) {
        this.handlerToNotify = handler;
        recDataString = new StringBuilder();
        bluetoothHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {

                if(msg.what == 0) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf(">");
                    if(endOfLineIndex>0) {
                        String dataInPrint = recDataString.substring(1, endOfLineIndex);
                        Log.i("dataInPrint",dataInPrint);
                        String[] parts = dataInPrint.split(";");
                        HashMap<String,String> hashMap = new HashMap<String,String>();
                        int handlerState = 0;

                        switch (parts[0]) {
                            case "GST" : {
                                String[] informationParts = parts[1].split(",");
                                for(String infoParts : informationParts) {
                                    Log.i("infoParts",infoParts);
                                    String[] info = infoParts.split(":");

                                    hashMap.put(info[0],info[1]);
                                    Log.i("info[0]",info[0]);
                                    Log.i("GST",info[1]);

                                }
                                handlerToNotify.obtainMessage(gstState,hashMap);
                                break;
                            }
                            case "GSS" : {
                                String[] informationParts = parts[1].split(",");
                                for(String infoParts : informationParts) {
                                    String[] info = infoParts.split(":");
                                    hashMap.put(info[0],info[1]);

                                }
                                handlerToNotify.obtainMessage(gssState,hashMap);
                                break;
                            }
                            case "GTS" : {
                                String[] informationParts = parts[1].split(",");
                                for(String infoParts : informationParts) {
                                    String[] info = infoParts.split(":");
                                    hashMap.put(info[0],info[1]);

                                }
                                handlerToNotify.obtainMessage(gtsState,hashMap);
                                break;
                            }

                        }

                        String command;
                        for (String cmd : parts) {
                            Log.i("cmd",cmd);


                        }
                        handlerToNotify.obtainMessage(handlerState,hashMap).sendToTarget();

                        recDataString.delete(0,recDataString.length());

                    }

                }
            }
        };
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
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(myBluetoothAdapter == null)
        {
            throw new NoBTAdapterException();
        }

        //If it isn't enabled
        else if(!myBluetoothAdapter.isEnabled())
        {
            throw new BTNotEnabledException();
        }

        connecterThread = new ConnecterThread(macAddress,bluetoothHandler);

        //Starts the connection
        new Thread(connecterThread).start();

//        connecterThread = new ConnecterThread(macAddress,bluetoothHandler);
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
        String command =  "<GST:>";
        connecterThread.write(command);
    }

    /**
     *
     */
    @Override
    public void getShootingStatus() throws IOException {
        String command = "GSS:";
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
        String command = "GTS:";
        connecterThread.write(command);
    }

    @Override
    public void setArmedState(boolean state) throws IOException {
        String command = "SAS:";
        if(state) {
            command += 1;
        }
        else {
            command +=0;
        }
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
        String command = "SFM:" + state;
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
        String command = "SRF:" + ms;
        connecterThread.write(command);
    }

    /**
     * Set the name of the gun.
     *
     * @param gunName  Name of the gun
     */
    @Override
    public void setGunName(String gunName) throws IOException {
        String command = "SGN;" + gunName;
        connecterThread.write(command);
    }

    /**
     * Sends a message to the gun to keep avoid a timeout <br>
     * (Timeout: Disarms the weapon)
     */
    @Override
    public void keepConnectionAlive() throws IOException {
        String command = "KCA:";
        connecterThread.write(command);
    }




}
