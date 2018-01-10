package com.copenhagenindustries.bluetoothconnection.activities;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.copenhagenindustries.bluetoothconnection.R;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import static android.content.ContentValues.TAG;


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
        Intent intent2 = new Intent(CustomPinActivity.this, MainActivity.class);
        startActivity(intent2);
    }

    @Override
    public int getContentView(){
        return R.layout.activity_pin_code;
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();//override this method to get a longer pin code
    }

}
