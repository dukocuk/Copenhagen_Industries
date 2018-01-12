package com.copenhagenindustries.bluetoothconnection.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;


import com.copenhagenindustries.bluetoothconnection.R;


public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefshow);

    }

}