package com.copenhagenindustries.bluetoothconnection.fragments;



import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.preference.ListPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.copenhagenindustries.bluetoothconnection.R;

import java.util.Locale;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    private void initSummary(android.preference.Preference p) {
        //init summary for PreferenceCategories
        if (p instanceof PreferenceCategory) {
            PreferenceCategory pCat = (PreferenceCategory) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initSummary(pCat.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    //update summary according to preference
    private void updatePrefSummary(android.preference.Preference p) {

        if (p instanceof android.preference.ListPreference) {
            final android.preference.ListPreference listPref = (android.preference.ListPreference) p;
            p.setSummary(listPref.getEntry());
            p.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {


                    String language;
                    Locale locale;
                    Configuration config = new Configuration();


                    switch (newValue.toString()) {
                        case "English":
                            language = "en";
                            locale = new Locale(language);
                            Locale.setDefault(locale);
                            config.locale = locale;
                            getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
                            getActivity().recreate();
                            break;
                        case "Danish":
                            language = "da";
                            locale = new Locale(language);
                            Locale.setDefault(locale);
                            config.locale = locale;
                            getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
                            getActivity().recreate();
                            break;
                    }


                    //preference.setSummary(newValue.toString());


                    Log.d("PREF_TEST", preference.toString());
                    Log.d("PREF_TEST", newValue.toString());
                    Log.d("PREF_TEST", Locale.getDefault().getLanguage());
                    return false;
                }
            });
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
    }


}