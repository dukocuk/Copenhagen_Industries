package com.copenhagenindustries.bluetoothconnection.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.copenhagenindustries.bluetoothconnection.R;

/**
 * Created by Ejer on 09-01-2018.
 */

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings,container,false);
        return root;
    }
}
