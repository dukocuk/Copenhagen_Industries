package com.copenhagenindustries.bluetoothconnection.backend;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.copenhagenindustries.bluetoothconnection.misc.HandlerStates;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Connecterthread is responsible for establishing the connection between app and bluetoothdevice, and sending/receiving messages via the connection.
 */
class ConnecterThread implements Runnable {

    //thread
    private volatile boolean shutdown = false;                         //Used to close the thread.

    //Final variables
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");        //Wiki pedersen is your best friend here


    private BluetoothAdapter bluetoothAdapter = null;           //Phone's bluetooth-adapter.
    private BluetoothDevice bluetoothDevice = null;             //Bluetoothdevice to connect to. Created based on mac address
    private BluetoothSocket bluetoothSocket = null;             //Communication.


    private final InputStream inputStream;                      //Receive bytes from this stream
    private final OutputStream outputStream;                    //Send bytes on this stream.

    private Handler handlerToNotify;                            //Handler from Device. Send the received characters to be collected in the specific device.
    private HandlerStates handlerStates = new HandlerStates();  //No hardcoded values. yay.


    public ConnecterThread(String MACAddress, Handler handlerToNotify) {
        this.handlerToNotify = handlerToNotify;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();            //Get the device's default bluetoothadapter
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(MACAddress);     //Create the bluetoothdevice in the system.

        InputStream tmpIn = null;           //Instantiating
        OutputStream tmpOut = null;         //Instantiating

        try {
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);        //Create the socket.
            bluetoothSocket.connect();                                                                  //Self explanatory.
            tmpIn = bluetoothSocket.getInputStream();                                                   //We now have a input stream
            tmpOut = bluetoothSocket.getOutputStream();                                                 //And a output stream.
        } catch (IOException e) {
            e.printStackTrace();
            shutdown = true;                                                    //No reason to keep the thread alive if we cant communicate with the device.
        }
        inputStream = tmpIn;                                                    //The input stream and output stream will either be null or alive when reaching the end.
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
        while(!shutdown) {                          //While thread aren't supposed to shutdown, listen for input.
            if(!bluetoothSocket.isConnected()) {        //Doesn't help jackshit. Haven't experienced it at least. As far as i remember
                handlerToNotify.obtainMessage(handlerStates.getHandlerStateDisconnected()).sendToTarget();
                shutdown = true;
                Log.d(".isConnected()","ConnecterThread isn't connected");
                return;

            }


                        //Listen for input. If any input has been received send it up the stream via the handler.
            try {
                bytes = inputStream.read(buffer);
                String readMSG = new String(buffer, 0, bytes);
                Log.d("ConnecterThread", readMSG);
                handlerToNotify.obtainMessage(handlerStates.getHandlerStateInformationReceived(), bytes, -1, readMSG).sendToTarget();
            }
            catch (IOException e) {
                Log.d("isConnected()", "" + bluetoothSocket.isConnected());                            //Stream closed.
                shutdown = true;                                                                                 //Close the thread
                handlerToNotify.obtainMessage(handlerStates.getHandlerStateDisconnected()).sendToTarget();       //Notify the app-device that the connection is closed.
                e.printStackTrace();
            }
        }

        //Entering this piece of code means that the thread shall terminate.
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
     * @param input input
     * @throws IOException IOException
     */
    public void write(String input) throws IOException {
        byte[] msgBuffer = input.getBytes();
        if(outputStream!=null) {
            outputStream.write(msgBuffer);
        }
    }

    /**
     * Terminate the thread.
     */
    protected void shutdown() {
        shutdown = true;
    }

    /**
     * Returns the status of the thread.
     * @return boolean true of thread is alive and false if not.
     */
    public boolean isAlive(){
        if(bluetoothSocket!=null && bluetoothSocket.isConnected() && !shutdown) {
            Log.d("isAlive","true");
            return true;
        }
            Log.d("isAlive","false");
            return false;
        }

    }