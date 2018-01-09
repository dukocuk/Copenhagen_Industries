package com.lasse.bluetoothconnection.activities;



import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;



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
