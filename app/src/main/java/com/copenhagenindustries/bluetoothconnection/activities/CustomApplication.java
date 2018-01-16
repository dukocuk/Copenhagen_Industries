package com.copenhagenindustries.bluetoothconnection.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.copenhagenindustries.bluetoothconnection.R;
import com.github.omadahealth.lollipin.lib.PinActivity;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import static android.content.ContentValues.TAG;

/**
 * Created by Ejer on 10-01-2018.
 */

public class CustomApplication extends PinActivity{
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MA_","CustomApp_Created");

        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setFingerprintAuthEnabled(true);
        lockManager.getAppLock().setLogoId(1);

        //lockManager.disableAppLock();
        //lockManager.getAppLock().disableAndRemoveConfiguration();
        lockManager.getAppLock().setTimeout(500);
        Intent intent = new Intent(CustomApplication.this, CustomPinActivity.class);
        //If there is a passcode, go to normal unlockscreen, if there isn't force them to make one
        if (lockManager.getAppLock().isPasscodeSet()) {
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.CONFIRM_PIN);
        } else {
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivityForResult(intent, 11);
            lockManager.getAppLock().setLogoId(1);
            lockManager.getAppLock().setFingerprintAuthEnabled(true);
            Log.d(TAG, "Pincode enabled");
        }



    }

}
