package com.lasse.bluetoothconnection.misc;


public final class HandlerStates {
    private final int handlerStateInformationReceived = 0;   //Handler state for when information is received.
    private final int handlerStateDisconnected = 1;          //Handler state for when the connection is no more
    private final int handlerStateToast = 2;                 //Handler state for when background thread needs to Toast something.
    public int getHandlerStateInformationReceived() {
        return handlerStateInformationReceived;
    }

    public int getHandlerStateDisconnected() {
        return handlerStateDisconnected;
    }

    public int getHandlerStateToast() {
        return handlerStateToast;
    }
}
