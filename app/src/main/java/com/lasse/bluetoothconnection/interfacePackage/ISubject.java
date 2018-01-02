package com.lasse.bluetoothconnection.interfacePackage;

/**
 * Created by lasse on 30-11-2017.
 */

public interface ISubject {
    void addToObserverList(IObserver observer);
    void removeFromObserverList(IObserver observer);
    void notifyList();
}
