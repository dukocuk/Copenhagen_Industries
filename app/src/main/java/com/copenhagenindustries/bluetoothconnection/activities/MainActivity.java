package com.copenhagenindustries.bluetoothconnection.activities;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.copenhagenindustries.bluetoothconnection.R;
import com.copenhagenindustries.bluetoothconnection.controllers.DeviceController;
import com.copenhagenindustries.bluetoothconnection.fragments.HelpFragment;
import com.copenhagenindustries.bluetoothconnection.fragments.KnownDevicesListFragment;
import com.copenhagenindustries.bluetoothconnection.fragments.SettingsFragment;
import com.crashlytics.android.Crashlytics;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MA_","Created");
        Log.d("PREF_TEST", Locale.getDefault().getLanguage());

        // Nedbrudsrapportering sker kun når appen testes udenfor emulatoren
        boolean EMULATOR = Build.PRODUCT.contains("sdk") || Build.MODEL.contains("Emulator");
        if (!EMULATOR) {
            Fabric.with(this, new Crashlytics());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {
            Log.d("MA_SavedInstance","null");
            Log.d("MA_BackStackCount","" + getFragmentManager().getBackStackEntryCount());
            KnownDevicesListFragment fragment = new KnownDevicesListFragment();
            getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().add(R.id.content_main_fragment, fragment).commit();

        }

    }

    @Override
    public void onBackPressed() {
        Log.d("MA_BackStackCount","" + getFragmentManager().getBackStackEntryCount());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Log.d("MA_OnBackPressed","Else");
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("Login",false).apply();
        DeviceController.getInstance().saveData(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_devices) {
            fragment = new KnownDevicesListFragment();
        }
        else if (itemId == R.id.nav_settings) {
            fragment = new SettingsFragment();

        }
        else if (itemId == R.id.nav_help) {
            fragment = new HelpFragment();
        }

        if (fragment != null) {
            getFragmentManager().popBackStack();
            if (fragment instanceof KnownDevicesListFragment) {
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().replace(R.id.content_main_fragment, fragment).commit();
            } else {
                getFragmentManager().popBackStack();
                Log.d("MA_BackStackCount", "" + getFragmentManager().getBackStackEntryCount());
                getFragmentManager().beginTransaction().replace(R.id.content_main_fragment, fragment).addToBackStack(null).commit();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
