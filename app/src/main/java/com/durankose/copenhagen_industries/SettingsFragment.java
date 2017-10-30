package com.durankose.copenhagen_industries;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsFragment extends Fragment {

    /** Formaalet med TAG er at kunne fejlfinde hvis noget skulle gaa galt.
     *  Vi kan så proppe den TAG i en LOG.d og eks. få
     *  "D/DevicesFragment: Du er inde i Devices"
     */
    private final String TAG = this.getClass().getSimpleName();
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Du er inde i Settings");
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.settings_fragment));
    }

}
