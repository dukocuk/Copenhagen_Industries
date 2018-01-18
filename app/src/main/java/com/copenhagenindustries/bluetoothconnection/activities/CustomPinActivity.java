package com.copenhagenindustries.bluetoothconnection.activities;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.copenhagenindustries.bluetoothconnection.R;
import com.copenhagenindustries.bluetoothconnection.misc.RequestCodes;
import com.copenhagenindustries.bluetoothconnection.misc.SharedPreferencesStrings;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;


public class CustomPinActivity extends AppLockActivity {

    @Override
    public void showForgotDialog() {
        Resources res = getResources();
        // Create the builder with required paramaters - Context, Title, Positive Text
        CustomDialog.Builder builder = new CustomDialog.Builder(this,
                getString(R.string.custom_pin_activity_forgot_dialog_title),
                getString(R.string.custom_pin_activity_forgot_dialog_positive_text));
        builder.content(getString(R.string.custom_pin_activity_forgot_dialog_content));
        builder.negativeText(getString(R.string.custom_pin_activity_forgot_dialog_negative_text));

        //Set theme
        builder.darkTheme(false);
        builder.typeface(Typeface.SANS_SERIF);
        builder.positiveColor(res.getColor(R.color.light_blue_500)); // int res, or int colorRes parameter versions available as well.
        builder.negativeColor(res.getColor(R.color.light_blue_500));
        builder.rightToLeft(false); // Enables right to left positioning for languages that may require so.
        builder.titleAlignment(BaseDialog.Alignment.CENTER);
        builder.buttonAlignment(BaseDialog.Alignment.CENTER);
        //builder.setButtonStacking(false);

        //Set text sizes
        builder.titleTextSize(22);
        builder.contentTextSize( 22);
        //builder.positiveButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_positive_button_size));
        //builder.negativeButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_negative_button_size));

        //Build the dialog.
        CustomDialog customDialog = builder.build();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                Intent intent = new Intent(CustomPinActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelClick() {
            }
        });

        // Show the dialog.
        customDialog.show();
    }

    @Override
    public void onPinFailure(int attempts) {
        if(attempts > 2 && PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SharedPreferencesStrings.CHANGING_PIN,true)) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove(SharedPreferencesStrings.CHANGING_PIN).apply();
            Intent cancel = new Intent(CustomPinActivity.this,MainActivity.class);
            startActivityForResult(cancel,RequestCodes.STATE_CHANGE_PIN_FAILURE);


            Toast.makeText(this, R.string.change_pin_wrong_password, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onPinSuccess(int attempts) {
        boolean changing_pin = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SharedPreferencesStrings.CHANGING_PIN,false);
        boolean login = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SharedPreferencesStrings.ON_PIN_SUCCESS_NR,false);
        Log.d("onPinSuccess_pin","" + changing_pin);
        Log.d("onPinSuccess_login","" + login);



        if(!login && !changing_pin) {
            Log.d("onPinSuccess","Moving to MainActivity");
            Intent intent2 = new Intent(CustomPinActivity.this, MainActivity.class);
            startActivity(intent2);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SharedPreferencesStrings.ON_PIN_SUCCESS_NR, true).apply();
        }
        else if(changing_pin) {
            Log.d("onPinSuccess","Pin confirmed. Now possible to change it");
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SharedPreferencesStrings.CHANGING_PIN,false).apply();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SharedPreferencesStrings.ON_PIN_SUCCESS_NR,true).apply();
        }

    }


    @Override
    public int getContentView(){
        return R.layout.activity_pin_code;
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();//override this method to get a longer pin code
    }

    @Override
    public void onBackPressed() {
        Log.d("CPA_BackStackCount", "" + getFragmentManager().getBackStackEntryCount());
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Login success full. Reset value
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SharedPreferencesStrings.ON_PIN_SUCCESS_NR, false).apply();

    }
}
