package com.durankose.copenhagen_industries.backend;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by lasse on 02-10-2017.
 */

class ConnecterThread implements Runnable {

    //thread
    private volatile boolean shutdown = false;

    //Final variables
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final int handlerState = 0; //used to identify handler message

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice bluetoothDevice = null;
    private BluetoothSocket bluetoothSocket = null;


    private final InputStream inputStream;
    private final OutputStream outputStream;

    Handler handlerToNotify;


    public ConnecterThread(String MACAddress, Handler handlerToNotify) {
        this.handlerToNotify = handlerToNotify;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(MACAddress);

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();
            tmpIn = bluetoothSocket.getInputStream();
            tmpOut = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    /**
     * Continously listens for a message from the bluetoothconnection.
     */
    @Override
    public void run() {
        byte[] buffer = new byte[96];
        int bytes;

        //Listen for input
        while(!shutdown) {
            try {
                bytes = inputStream.read(buffer);
                String readMSG = new String(buffer, 0, bytes);
                handlerToNotify.obtainMessage(handlerState, bytes, -1, readMSG).sendToTarget();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Entering this piece of code means that the thread shall shutdown.
        try {
            inputStream.close();
            outputStream.close();
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /**
     * Sends a string over the bluetooth connection.
     * @param input
     * @throws IOException
     */
    public void write(String input) throws IOException {
        byte[] msgBuffer = input.getBytes();
        outputStream.write(msgBuffer);
    }

    public boolean isSocketAlive(){
        if(bluetoothSocket!=null) {
            return true;
        }
        if(bluetoothSocket.isConnected()) {
            return true;
        }
        return false;
    }

    public void shutdown() {
        shutdown = true;

    }


}
