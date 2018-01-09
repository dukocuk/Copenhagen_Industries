package com.lasse.bluetoothconnection.activities;

import android.app.Application;

import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;

/**
 * Created by Ejer on 07-01-2018.
 */

public class CustomPinActivity extends AppLockActivity {

    @Override
    public void showForgotDialog() {
    /*
    write forget popup here
    */
    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {

    }

    @Override
    public int getPinLength() {
        return super.getPinLength();//override this method to get a longer pin code
    }

}
