package com.lasse.bluetoothconnection.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lasse.bluetoothconnection.R;

/**
 * Created by Ejer on 09-01-2018.
 */

public class SettingsFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weapon_control,container,false);
        return root;
    }
}
