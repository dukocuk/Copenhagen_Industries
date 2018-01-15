package com.copenhagenindustries.bluetoothconnection.activities;



import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.copenhagenindustries.bluetoothconnection.R;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;
import com.github.omadahealth.lollipin.lib.managers.LockManager;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

import static android.content.ContentValues.TAG;


/**
 * Created by Ejer on 07-01-2018.
 */

public class CustomPinActivity extends AppLockActivity {

    @Override
    public void showForgotDialog() {
        Resources res = getResources();
        // Create the builder with required paramaters - Context, Title, Positive Text
        CustomDialog.Builder builder = new CustomDialog.Builder(this,
                "Glemt din PIN-kode?",
                "Ja");
        builder.content("Tryk herunder hvis du vil komme ind i appen uden din kode");
        builder.negativeText("Nej");

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
        builder.titleTextSize((int) 22);
        builder.contentTextSize((int) 22);
        //builder.positiveButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_positive_button_size));
        //builder.negativeButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_negative_button_size));

        //Build the dialog.
        CustomDialog customDialog = builder.build();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                Toast.makeText(getApplicationContext(), "Unlock", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CustomPinActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelClick() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog.
        customDialog.show();
    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {
        Log.d(TAG, "onPinSuccess: pls stop");
        if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Login",false)) {
            Intent intent2 = new Intent(CustomPinActivity.this, MainActivity.class);
            startActivity(intent2);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("Login", true).apply();
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
}
