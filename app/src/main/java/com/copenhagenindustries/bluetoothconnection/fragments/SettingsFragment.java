package com.copenhagenindustries.bluetoothconnection.fragments;



import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceActivity;
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
import com.copenhagenindustries.bluetoothconnection.activities.CustomApplication;
import com.copenhagenindustries.bluetoothconnection.activities.CustomPinActivity;
import com.copenhagenindustries.bluetoothconnection.activities.MainActivity;
import com.github.omadahealth.lollipin.lib.managers.AppLock;


import java.util.Locale;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }


        Preference pref = getPreferenceManager().findPreference(getString(R.string.pref_key_pincode));

        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                Intent intent = new Intent(getActivity(), CustomPinActivity.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.CHANGE_PIN);
                startActivity(intent);


                return true;
            }


        });

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

            Log.d("TEST", p.getKey().toString());



            if (p.getKey().equalsIgnoreCase(getString(R.string.pref_key_language))) {

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