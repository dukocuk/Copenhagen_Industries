package com.durankose.copenhagen_industries.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.durankose.copenhagen_industries.R;

import java.util.HashMap;


public class WeaponControlFragment extends Fragment {
    /** Formaalet med TAG er at kunne fejlfinde hvis noget skulle gaa galt.
     *  Vi kan så proppe den TAG i en LOG.d og eks. få
     *  "D/DevicesFragment: Du er inde i Devices"
     */
    private final String TAG = this.getClass().getSimpleName();
    private Handler bluetoothHandler;

    public WeaponControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Du er inde i Weapon Control.");

        View view = inflater.inflate(R.layout.fragment_weapon_control, container, false);


        bluetoothHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {

                if(msg.what == 0) {
                    HashMap<String,String> hashMap = (HashMap<String,String>) msg.obj;
                    //t1.setText("Armedstate: " + hashMap.get("IArm"));


                }
            }
        };


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.weapon_control_fragment));
    }


}
