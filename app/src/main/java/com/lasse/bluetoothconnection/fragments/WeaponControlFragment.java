package com.lasse.bluetoothconnection.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lasse.bluetoothconnection.controllers.DeviceController;
import com.lasse.bluetoothconnection.R;
import com.lasse.bluetoothconnection.exceptions.BTNotEnabledException;
import com.lasse.bluetoothconnection.exceptions.NoBTAdapterException;
import com.lasse.bluetoothconnection.interfacePackage.IObserver;
import com.lasse.bluetoothconnection.misc.HandlerStates;

import java.io.IOException;


public class WeaponControlFragment extends Fragment implements IObserver {


    //Textviews:
    private TextView status;
    private TextView battery;
    private TextView oxygen;
    private TextView propane;

    //Shooting mode information
    private TextView mode;
    private String[] modeStrings = {"Semi", "Burst", "Full Automatic"};
    private int[] pngRecourses;
    private int modeNr = 0;

    private EditText rateOfFire;
    private EditText name;

    private Switch aSwitch;

    private Button editBtn;

    private boolean editMode = false;


    private DeviceController deviceController;

    private static Handler handler;
    private HandlerStates handlerStates = new HandlerStates();

    private AsyncTask task;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(com.lasse.bluetoothconnection.R.layout.fragment_weapon_control,container,false);
        name = (EditText) root.findViewById(R.id.weapon_control_name);
        status = (TextView) root.findViewById(R.id.weapon_control_status);
        battery = (TextView) root.findViewById(R.id.weapon_control_battery);
        oxygen = (TextView) root.findViewById(R.id.weapon_control_oxygen);
        propane = (TextView) root.findViewById(R.id.weapon_control_propane);
        mode = (TextView) root.findViewById(R.id.weapon_control_mode);
        rateOfFire = (EditText) root.findViewById(R.id.weapon_control_RoF);
        aSwitch = (Switch) root.findViewById(R.id.weapon_control_switch);
        editBtn = (Button) root.findViewById(R.id.weapon_control_doneEditing_button);


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if(!deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {         //If there's no connection disallow the check of the switch.
                        aSwitch.setChecked(false);
                    }
                    deviceController.getDeviceCurrentlyDisplayed().setArmedState(isChecked);
                    if(deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                        deviceController.getDeviceCurrentlyDisplayed().getTotalStatus();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editMode) {
                    editBtn.setText("Done Editing");
                    editMode = true;

                    name.setEnabled(true);
                    rateOfFire.setEnabled(true);

                }
                else {
                    editMode = false;
                    try {
                        deviceController.getDeviceCurrentlyDisplayed().setName(name.getText().toString());
                        deviceController.getDeviceCurrentlyDisplayed().setRateOfFire(Integer.parseInt(rateOfFire.getText().toString()));
                        name.clearFocus();
                        rateOfFire.clearFocus();
                        name.setEnabled(false);
                        rateOfFire.setEnabled(false);
                        editBtn.setText("Start Editing");
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Invalid Rate of Fire", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeNr++;
                modeNr = modeNr%3;
                mode.setText(modeStrings[modeNr]);
                try {
                    if(deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                        deviceController.getDeviceCurrentlyDisplayed().setFireMode(modeNr);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //TODO change image resource.
            }
        });

        deviceController = DeviceController.getInstance();


        resetDisplay();
        if(!deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
            task = new ProgressTask(getActivity()).execute();
        }

        updateDisplay();
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(msg.what == handlerStates.getHandlerStateToast()) {
                    Toast.makeText(getActivity().getApplicationContext(),"Connection Not Established",Toast.LENGTH_LONG).show();
                }
            }
        };
        deviceController.getDeviceCurrentlyDisplayed().addToObserverList(this);


        return root;
    }



    private void resetDisplay() {
        String na = "N/A";
        name.setText(na);
        status.setText("Safe");
        battery.setText(na);
        oxygen.setText(na);
        propane.setText(na);
        mode.setText(na);
    }

    private void updateDisplay() {

        if(!(deviceController.getDeviceCurrentlyDisplayed().getName()==null)) {
            this.name.setText(deviceController.getDeviceCurrentlyDisplayed().getName());
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getBattery()==null)) {
            battery.setText(deviceController.getDeviceCurrentlyDisplayed().getBattery());
            Log.d("Battery", "" + deviceController.getDeviceCurrentlyDisplayed().getBattery());
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getOxygen()==null)) {
            oxygen.setText(deviceController.getDeviceCurrentlyDisplayed().getOxygen());
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getPropane()==null)) {
            propane.setText(deviceController.getDeviceCurrentlyDisplayed().getPropane());
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getFireMode()==-1)) {
            String fireMode = "" + deviceController.getDeviceCurrentlyDisplayed().getFireMode();
            mode.setText(fireMode);
        }
        if(deviceController.getDeviceCurrentlyDisplayed().isArmedState()) {
            this.status.setText("Armed");
        }
        else {
            this.status.setText("Disarmed");
        }
    }

    @Override
    public void notifyObs() {
        updateDisplay();
    }

    @Override
    public void notifyObsConnectionLost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Forbindelse mistet.\nLav ny forbindelse?");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                new ProgressTask((getActivity())).execute();
            }
        });
        builder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                aSwitch.setClickable(false);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {


        private Context context;
        private ProgressDialog dialog;



        public ProgressTask(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Connecting...");
            this.dialog.show();

        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                deviceController.getDeviceCurrentlyDisplayed().startConnection();
                if(deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                    deviceController.getDeviceCurrentlyDisplayed().getTotalStatus();

                }
                else {
                    handler.obtainMessage(handlerStates.getHandlerStateToast()).sendToTarget();

                }
            } catch (BTNotEnabledException | NoBTAdapterException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    @Override
    public void onDestroy() {
        deviceController.getDeviceCurrentlyDisplayed().removeFromObserverList(this);
        handler = null;
        task = null;
        super.onDestroy();

    }
}
