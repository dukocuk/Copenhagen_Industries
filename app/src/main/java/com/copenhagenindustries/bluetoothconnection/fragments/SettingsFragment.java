package com.copenhagenindustries.bluetoothconnection.fragments;


import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.copenhagenindustries.bluetoothconnection.R;
import com.copenhagenindustries.bluetoothconnection.activities.CustomPinActivity;
import com.copenhagenindustries.bluetoothconnection.misc.RequestCodes;
import com.copenhagenindustries.bluetoothconnection.misc.SharedPreferencesStrings;
import com.github.omadahealth.lollipin.lib.managers.AppLock;

import java.util.Locale;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getActivity().setTitle(R.string.action_settings);


        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }

        Preference pref_pin = getPreferenceManager().findPreference(getString(R.string.pref_key_pincode));
        Preference pref_dev = getPreferenceManager().findPreference(getString(R.string.pref_key_dev));

        if(pref_pin!=null) {
            pref_pin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getPreferenceManager().getSharedPreferences().edit().putBoolean(SharedPreferencesStrings.CHANGING_PIN, true).apply();


                    Intent changePin = new Intent(getActivity(), CustomPinActivity.class);
                    changePin.putExtra(AppLock.EXTRA_TYPE, AppLock.CONFIRM_PIN);
                    startActivityForResult(changePin, RequestCodes.STATE_CHANGE_PIN);

                    Intent enablePin = new Intent(getActivity(), CustomPinActivity.class);
                    enablePin.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                    TaskStackBuilder.create(getActivity()).addNextIntent(changePin).addNextIntent(enablePin).startActivities();

                    return true;
                }
            });
        }

        if(pref_dev!=null) {
            pref_dev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {


                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAbout();
                    return true;
                }
            });
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MA_Settings","asdf");
        Log.d("MA_Settings","requestCode:" + requestCode + " ResultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
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
            listPref.setSummary(listPref.getEntry());

            if (listPref.getKey().equalsIgnoreCase(getString(R.string.pref_key_language))) {

                listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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
                                listPref.setSummary(listPref.getEntry());
                                break;
                            case "Dansk":
                                language = "da";
                                locale = new Locale(language);
                                Locale.setDefault(locale);
                                config.locale = locale;
                                getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
                                getActivity().recreate();
                                listPref.setSummary(listPref.getEntry());
                                break;
                        }


                        //preference.setSummary(newValue.toString());

                        Log.d("PREF_TEST", preference.toString());
                        Log.d("PREF_TEST", newValue.toString());
                        Log.d("PREF_TEST", Locale.getDefault().getLanguage());
                        return true;
                    }
                });
            }
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
    }


    private void showAbout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        LayoutInflater factory = LayoutInflater.from(getActivity());

        final View view = factory.inflate(R.layout.about, null);

        dialog.setView(view);

        dialog.setCancelable(true);
        dialog.setTitle(R.string.pref_about_title2);
        dialog.setMessage(R.string.pref_about_devs);
        dialog.setPositiveButton(R.string.pref_about_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Delete".
            }
        })
                /*.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                    }
                })*/;

        final AlertDialog alert = dialog.create();
        alert.show();
    }


}