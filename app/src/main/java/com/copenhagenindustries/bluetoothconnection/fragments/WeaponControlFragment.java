package com.copenhagenindustries.bluetoothconnection.fragments;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.copenhagenindustries.bluetoothconnection.R;
import com.copenhagenindustries.bluetoothconnection.controllers.DeviceController;
import com.copenhagenindustries.bluetoothconnection.exceptions.BTNotEnabledException;
import com.copenhagenindustries.bluetoothconnection.exceptions.NoBTAdapterException;
import com.copenhagenindustries.bluetoothconnection.interfacePackage.IObserver;
import com.copenhagenindustries.bluetoothconnection.misc.HandlerStates;

import java.io.IOException;


public class WeaponControlFragment extends Fragment implements IObserver {


    //Textviews:

    private TextView oxygen;
    private TextView propane;

    //Shooting mode information
    private ImageView mode;
    private int[] modeImages = {R.drawable.bulletsingle,R.drawable.bulletburst,R.drawable.bulletauto};
    private int modeNr = 0;

    private ImageView battery;
    private int[] batteryImages = {R.drawable.battery0,R.drawable.battery1,R.drawable.battery2,R.drawable.battery3,R.drawable.battery4};

    private EditText rateOfFire;
    private EditText name;

    private Button aButton;

    private Button editBtn;

    private boolean editMode = false;


    private DeviceController deviceController;

    private static Handler handler;
    private HandlerStates handlerStates = new HandlerStates();

    private AsyncTask task;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(com.copenhagenindustries.bluetoothconnection.R.layout.fragment_weapon_control,container,false);
        name = (EditText) root.findViewById(R.id.weapon_control_name);
        battery = (ImageView) root.findViewById(R.id.weapon_control_battery_image);
        oxygen = (TextView) root.findViewById(R.id.weapon_control_oxygen);
        propane = (TextView) root.findViewById(R.id.weapon_control_propane);
        mode = (ImageView) root.findViewById(R.id.weapon_control_mode_imageView);
        rateOfFire = (EditText) root.findViewById(R.id.weapon_control_RoF);
        aButton = (Button) root.findViewById(R.id.weapon_control_switch);
        editBtn = (Button) root.findViewById(R.id.weapon_control_doneEditing_button);

        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deviceController.getDeviceCurrentlyDisplayed().connectionAlive()) {
                    try {

                        deviceController.getDeviceCurrentlyDisplayed().setArmedState(!deviceController.getDeviceCurrentlyDisplayed().isArmedState());
                        if(deviceController.getDeviceCurrentlyDisplayed().isArmedState()){
                            aButton.setBackgroundColor(getResources().getColor(R.color.colorButtonRed));
                            aButton.setBackground(getResources().getDrawable(R.drawable.danger));
                            aButton.setText("Armed");
                        }
                        else {
                            aButton.setBackgroundColor(getResources().getColor(R.color.colorButtonGreen));
                            aButton.setBackground(getResources().getDrawable(R.drawable.safe));
                            aButton.setText("Safe");
                        }
                        deviceController.getDeviceCurrentlyDisplayed().getTotalStatus();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    aButton.setBackgroundColor(getResources().getColor(R.color.colorButtonGrey));
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
                mode.setImageResource(modeImages[modeNr]);
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
        battery.setImageResource(R.drawable.battery4);
        oxygen.setText(na);
        propane.setText(na);
        mode.setImageResource(R.drawable.bulletauto);
    }

    private void updateDisplay() {


        if(!(deviceController.getDeviceCurrentlyDisplayed().getName()==null)) {
            if(!editMode) {
                this.name.setText(deviceController.getDeviceCurrentlyDisplayed().getName());
            }
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
            oxygen.setText(deviceController.getDeviceCurrentlyDisplayed().getOxygen() + "%");
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getPropane()==null)) {
            propane.setText(deviceController.getDeviceCurrentlyDisplayed().getPropane() + "%");
        }
        if(!(deviceController.getDeviceCurrentlyDisplayed().getFireMode()==-1)) {
            mode.setImageResource(modeImages[deviceController.getDeviceCurrentlyDisplayed().getFireMode()]);

        }
        if(deviceController.getDeviceCurrentlyDisplayed().getFireMode()!=-1) {
            if(!editMode){
                rateOfFire.setText("" + deviceController.getDeviceCurrentlyDisplayed().getRateOfFire());
            }
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
                aButton.setClickable(false);
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
