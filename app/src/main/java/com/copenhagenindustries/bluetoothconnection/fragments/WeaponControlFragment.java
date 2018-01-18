package com.copenhagenindustries.bluetoothconnection.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.copenhagenindustries.bluetoothconnection.R;
import com.copenhagenindustries.bluetoothconnection.controllers.DeviceController;
import com.copenhagenindustries.bluetoothconnection.exceptions.BTNotEnabledException;
import com.copenhagenindustries.bluetoothconnection.exceptions.DeviceControllerNotInstantiatedException;
import com.copenhagenindustries.bluetoothconnection.exceptions.NoBTAdapterException;
import com.copenhagenindustries.bluetoothconnection.interfacePackage.IObserver;
import com.copenhagenindustries.bluetoothconnection.misc.HandlerStates;
import com.copenhagenindustries.bluetoothconnection.misc.RequestCodes;

import java.io.IOException;
import java.util.HashMap;


public class WeaponControlFragment extends Fragment implements IObserver {

    private static final String STATE_TASK_RUNNING = "taskRunning";
    private static final String STATE_CONNECT_ONCE = "connectOnce";
    private static final String STATE_CANCEL_BUTTON_SHOWING = "cancelButtonShowing";
    private boolean connectOnce = false;
    private boolean cancelButtonShowing = false;


    //Textviews:

    private TextView oxygen;
    private TextView propane;
    private TextView rateOfFire;
    private TextView name;

    //Shooting mode information
    private ImageView mode;
    private int[] modeImages = {R.drawable.bullet_single,R.drawable.bullet_burst,R.drawable.bullet_auto};
    private int modeNr = 0;

    private ImageView battery;
    private int[] batteryImages = {R.drawable.battery0,R.drawable.battery1,R.drawable.battery2,R.drawable.battery3,R.drawable.battery4};

    private ImageView gunImage;

    private Button armingButton;


    private DeviceController deviceController;

    private static Handler handler;
    private HandlerStates handlerStates = new HandlerStates();

    private AsyncTask task;

    HashMap<String, Integer> gunTypeLogos = new HashMap<>();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(com.copenhagenindustries.bluetoothconnection.R.layout.fragment_weapon_control,container,false);
        setHasOptionsMenu(true);
        RelativeLayout layout = root.findViewById(R.id.weapon_control);
        getActivity().setTitle("Weapon Control");
        deviceController = DeviceController.getInstance();


        gunTypeLogos.put("AK47", R.drawable.ic_rifle_big);
        gunTypeLogos.put("Gun", R.drawable.ic_gun_big);
        gunTypeLogos.put("Sub", R.drawable.ic_sub_big);
        gunTypeLogos.put("Sniper", R.drawable.ic_sniper_big);
        gunTypeLogos.put("Musket", R.drawable.ic_musket_big);

        name = root.findViewById(R.id.weapon_control_name);
        battery = root.findViewById(R.id.weapon_control_battery_image);
        oxygen = root.findViewById(R.id.weapon_control_oxygen);
        propane = root.findViewById(R.id.weapon_control_propane);
        mode = root.findViewById(R.id.weapon_control_mode_imageView);
        rateOfFire = root.findViewById(R.id.weapon_control_RoF);
        armingButton = root.findViewById(R.id.weapon_control_switch);
        gunImage = root.findViewById(R.id.weapon_control_image_header);
        if(gunTypeLogos.containsKey(deviceController.getDeviceCurrentlyDisplayed().getGunType())) {
            gunImage.setImageResource(gunTypeLogos.get(deviceController.getDeviceCurrentlyDisplayed().getGunType()));
        }
        else {
            gunImage.setImageResource(R.drawable.ic_help);
        }

        armingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                    try {

                        deviceController.getDeviceCurrentlyDisplayed().setArmedState(!deviceController.getDeviceCurrentlyDisplayed().isArmedState());
                        if(deviceController.getDeviceCurrentlyDisplayed().isArmedState()){
                            armingButton.setBackground(getResources().getDrawable(R.drawable.danger));
                            armingButton.setText(R.string.weapon_control_button_armed);
                        }
                        else {
                            armingButton.setBackground(getResources().getDrawable(R.drawable.safe));
                            armingButton.setText(R.string.weapon_control_button_safe);
                        }
                        deviceController.getDeviceCurrentlyDisplayed().getTotalStatus();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    //armingButton.setBackgroundColor(getResources().getColor(R.color.colorButtonGrey));
                    armingButton.setBackground(getResources().getDrawable(R.drawable.disconnected));
                    armingButton.setText(R.string.weapon_control_button_disconnected);
                }
            }
        });


        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeNr++;
                modeNr = modeNr%3;
                mode.setImageResource(modeImages[modeNr]);
                try {
                    if(deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                        deviceController.getDeviceCurrentlyDisplayed().setFireMode(modeNr);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        resetDisplay();



        if(savedInstanceState!=null) {
            connectOnce = savedInstanceState.getBoolean(STATE_CONNECT_ONCE,false);
            cancelButtonShowing = savedInstanceState.getBoolean(STATE_CANCEL_BUTTON_SHOWING,false);
            if(!connectOnce) {
                if (savedInstanceState.getBoolean(STATE_TASK_RUNNING,false) && !deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                    task = new ProgressTask(getActivity()).execute();
                }
            }
        }
        else {
            if (!deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {

                task = new ProgressTask(getActivity()).execute();
            }
        }

        updateDisplay();
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(msg.what == handlerStates.getHandlerStateToast()) {
                    Toast.makeText(getActivity().getApplicationContext(),"Connection Not Established",Toast.LENGTH_LONG).show();
                    connectOnce = true;
                    if(isTaskRunning()) {
                        task.cancel(true);
                    }
                }
            }
        };

        deviceController.getDeviceCurrentlyDisplayed().addToObserverList(this);


        return root;
    }



    private void resetDisplay() {
        String na = "N/A";
        name.setText(na);
        battery.setImageResource(R.drawable.battery4);
        oxygen.setText(na);
        propane.setText(na);
        mode.setImageResource(R.drawable.bulletauto);
    }

    private void updateDisplay() {
        if(gunTypeLogos.containsKey(deviceController.getDeviceCurrentlyDisplayed().getGunType())) {
            gunImage.setImageResource(gunTypeLogos.get(deviceController.getDeviceCurrentlyDisplayed().getGunType()));
        }
        else {
            gunImage.setImageResource(R.drawable.ic_help);
        }
        if(!deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
            armingButton.setBackground(getResources().getDrawable(R.drawable.disconnected));
            armingButton.setText(R.string.weapon_control_button_disconnected);
        }
        else if(deviceController.getDeviceCurrentlyDisplayed().isArmedState()){
            armingButton.setBackground(getResources().getDrawable(R.drawable.danger));
            armingButton.setText(R.string.weapon_control_button_armed);
        }
        else {
            armingButton.setBackground(getResources().getDrawable(R.drawable.safe));
            armingButton.setText(R.string.weapon_control_button_safe);
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getName()==null)) {
            this.name.setText(deviceController.getDeviceCurrentlyDisplayed().getName());
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getBattery()==null)) {
            Log.d("battery", "" + deviceController.getDeviceCurrentlyDisplayed().getBattery());
            try {
                battery.setImageResource(batteryImages[Math.abs(Integer.parseInt(deviceController.getDeviceCurrentlyDisplayed().getBattery())) / 25]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!(deviceController.getDeviceCurrentlyDisplayed().getOxygen()==null)) {
            oxygen.setText(String.format("%s%%",deviceController.getDeviceCurrentlyDisplayed().getOxygen()));
            // v v  oxygen.setText(deviceController.getDeviceCurrentlyDisplayed().getOxygen() + "%");
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getPropane()==null)) {
            propane.setText(String.format("%s%%",deviceController.getDeviceCurrentlyDisplayed().getPropane()));
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getFireMode()==-1)) {
            mode.setImageResource(modeImages[deviceController.getDeviceCurrentlyDisplayed().getFireMode()]);

        }
        if(deviceController.getDeviceCurrentlyDisplayed().getRateOfFire()!=-1) {
            rateOfFire.setText(String.format("%s",deviceController.getDeviceCurrentlyDisplayed().getRateOfFire()));
        }
    }

    @Override
    public void notifyObs() {
        updateDisplay();
    }

    @Override
    public void notifyObsConnectionLost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.weapon_control_connection_lost);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new ProgressTask((getActivity())).execute();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                armingButton.setClickable(false);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {


        private Context context;
        private ProgressDialog dialog;
        private final Handler dialogHandler = new Handler();
        private boolean canceled = false;



        public ProgressTask(Context context) {
            this.context = context;
            dialog = new ProgressDialog(this.context);


            dialog.setCancelable(false);
        }


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.weapon_control_connecting));
            this.dialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.weapon_control_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i == DialogInterface.BUTTON_NEGATIVE) {
                        canceled = true;
                    }
                }
            });
            this.dialog.show();
            if(!cancelButtonShowing) {
                this.dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
                this.dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setClickable(false);
                dialogHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(dialog.isShowing()) {
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setClickable(true);
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                            cancelButtonShowing = true;
                        }
                    }
                },1500);
            }
            else {
                if(dialog.isShowing()) {
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setClickable(true);
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                }
            }


        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                deviceController.getDeviceCurrentlyDisplayed().getTotalStatus();
                connectOnce=true;
            } else {
                if (handler != null) {
                    if(!canceled) {
                        handler.obtainMessage(handlerStates.getHandlerStateToast()).sendToTarget();
                    }
                    connectOnce = true;
                }
            }

        }
        @Override
        protected Boolean doInBackground(String... params) {
                if (deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                    return null;
                }
                try {
                    deviceController.getDeviceCurrentlyDisplayed().startConnection();
                } catch (BTNotEnabledException e) {
                    e.printStackTrace();
                    //Ask to the user turn the bluetooth on
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBTIntent, 1);
                    return null;
                } catch (NoBTAdapterException e) {
                    e.printStackTrace();
                    getActivity().finish();
                }
            return null;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","requestCode: " + requestCode + " resultCode: " + resultCode);

        // check if the request code is same as what is passed  here it is 1
        if(requestCode == RequestCodes.STATE_ENABLE_BLUETOOTH) {
            if(resultCode!=0) {
                if(isTaskRunning()) {
                    task.cancel(true);
                }
                task = new ProgressTask(getActivity()).execute();
            }
            else if(resultCode == 0) {
                Toast.makeText(getActivity(), R.string.toast_enable_bluetooth,Toast.LENGTH_LONG).show();
            }
        }

    }
    private boolean isTaskRunning() {
        return (task != null) && (task.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If the task is running, save it in our state
        if (isTaskRunning()) {
            outState.putBoolean(STATE_TASK_RUNNING, true);
            outState.putBoolean(STATE_CANCEL_BUTTON_SHOWING,cancelButtonShowing);
        }
        outState.putBoolean(STATE_CONNECT_ONCE,connectOnce);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler = null;
        if(deviceController!=null) {
            deviceController.saveData(getActivity());
            deviceController.getDeviceCurrentlyDisplayed().removeFromObserverList(this);
        }
        if(isTaskRunning()) {
            task.cancel(true);
        }
        task = null;




    }


    // make the fragments menu from the ressource file
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.weapon_control, menu);

    }

    // menu item was pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("trykkede på noget", "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.weapon_control_menu_edit_name:
                getUserInputDialog(getString(R.string.change_the_weapon_name),0);

                break;
            case R.id.weapon_control_menu_edit_RoF:
                getUserInputDialog(getString(R.string.chance_the_rate_of_fire),1);
                break;
            case R.id.weapon_control_delete_weapon: {
                try {
                    deviceController.getDeviceCurrentlyDisplayed().stopConnection();
                    deviceController.removeDevice(deviceController.getDeviceCurrentlyDisplayed());
                    deviceController.saveData(getActivity());
                } catch (DeviceControllerNotInstantiatedException e) {
                    e.printStackTrace();
                }
                KnownDevicesListFragment fragment = new KnownDevicesListFragment();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().replace(R.id.content_main_fragment,fragment).commit();
                break;
            }
            default:
                break;
        }
        return false;
    }


    private void getUserInputDialog(String title, final int type){
        Log.d("min log", "changeNameDialog: ");

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        final EditText input = new EditText(getActivity());
        if(type==1) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(8);
            input.setFilters(filterArray);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else if(type==0) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(25);
            input.setFilters(filterArray);

        }
        builder.setView(input);
        builder.setPositiveButton(R.string.weapon_control_user_input_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 String textInput = input.getText().toString();
                 Log.d("GetUserInput",textInput);
                 switch (type) {
                     case 0: {
                         try {

                             deviceController.getDeviceCurrentlyDisplayed().setName(textInput);
                             updateDisplay();
                             deviceController.saveData(getActivity());

                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         break;
                     }
                     case 1: {
                         try {
                             deviceController.getDeviceCurrentlyDisplayed().setRateOfFire(Integer.parseInt(textInput));
                             updateDisplay();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         break;
                     }

                 }
            }
        });
        builder.setNegativeButton(R.string.weapon_control_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }




}
