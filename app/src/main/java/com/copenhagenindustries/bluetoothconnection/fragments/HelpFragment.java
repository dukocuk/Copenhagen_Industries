package com.copenhagenindustries.bluetoothconnection.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.copenhagenindustries.bluetoothconnection.R;

import java.util.Locale;


public class HelpFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help, container, false);
        getActivity().setTitle(R.string.maindrawer_help);

        Log.d("getLanguage", Locale.getDefault().getLanguage());

        WebView webview = root.findViewById(R.id.webview);

        Log.d("PREF_TEST", Locale.getDefault().getLanguage());
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("da")) {
            webview.loadUrl("file:///android_asset/help_web/index_da.html");
        }
        else if (Locale.getDefault().getLanguage().equalsIgnoreCase("en")){
            webview.loadUrl("file:///android_asset/help_web/index_en.html");
        }
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);


        return root;
    }
}
