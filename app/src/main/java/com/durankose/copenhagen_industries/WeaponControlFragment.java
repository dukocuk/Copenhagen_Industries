package com.durankose.copenhagen_industries;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeaponControlFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    public WeaponControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Du er inde i Weapon Control.");
        return inflater.inflate(R.layout.fragment_weapon_control, container, false);
    }

}
