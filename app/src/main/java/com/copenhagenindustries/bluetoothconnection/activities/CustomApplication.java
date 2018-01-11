package com.copenhagenindustries.bluetoothconnection.activities;

import android.content.Intent;
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
    public void onStart() {
        super.onStart();

        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.ci_logo_login_50);
        lockManager.getAppLock().setFingerprintAuthEnabled(true);

        //lockManager.disableAppLock();
        //lockManager.getAppLock().disableAndRemoveConfiguration();

        Intent intent = new Intent(CustomApplication.this, CustomPinActivity.class);
        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);

        //If there is a passcode, go to normal unlockscreen, if there isn't force them to make one
        if (lockManager.getAppLock().isPasscodeSet()) {
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.CONFIRM_PIN);
        } else {
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivityForResult(intent, 11);
            lockManager.getAppLock().setLogoId(R.drawable.ci_logo_login_50);
            lockManager.getAppLock().setFingerprintAuthEnabled(true);
            Log.d(TAG, "Pincode enabled");
        }
    }

}