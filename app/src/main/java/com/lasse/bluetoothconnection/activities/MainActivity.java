package com.lasse.bluetoothconnection.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.omadahealth.lollipin.lib.PinActivity;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.lasse.bluetoothconnection.R;
import com.lasse.bluetoothconnection.fragments.HelpFragment;
import com.lasse.bluetoothconnection.fragments.KnownDevicesListFragment;

import io.fabric.sdk.android.Fabric;

import static android.content.ContentValues.TAG;



public class MainActivity extends PinActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static final int REQUEST_CODE_ENABLE = 11;


    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nedbrudsrapportering sker kun når appen testes udenfor emulatoren
        boolean EMULATOR = Build.PRODUCT.contains("sdk") || Build.MODEL.contains("Emulator");
        if (!EMULATOR) {
            Fabric.with(this, new Crashlytics());
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        KnownDevicesListFragment fragment = new KnownDevicesListFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).commit();


        //Login
        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.ci_logo_login_50);
        lockManager.getAppLock().setFingerprintAuthEnabled(false);

        //lockManager.disableAppLock();
        //lockManager.getAppLock().disableAndRemoveConfiguration();

        Intent intent = new Intent(MainActivity.this, CustomPinActivity.class);

        //If there is a passcode, go to normal unlockscreen, if there isn't force them to make one
        if(lockManager.getAppLock().isPasscodeSet()) {
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
        } else{
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivityForResult(intent, REQUEST_CODE_ENABLE);
            lockManager.getAppLock().setLogoId(R.drawable.ci_logo_login_50);
            lockManager.getAppLock().setFingerprintAuthEnabled(false);
            Log.d(TAG, "Pincode enabled");
        }







    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_devices) {
            fragment = new KnownDevicesListFragment();
        }
        else if(id == R.id.nav_help) {
            fragment = new HelpFragment();
        }
        else if(id == R.id.nav_settings) {
            Toast.makeText(this,"You clicked on settings",Toast.LENGTH_LONG).show();
        }
        if(fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_main_fragment, fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
