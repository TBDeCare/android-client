package com.garuda45.tbdecarelab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by mure on 2/3/2017.
 */
public class MainActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnBeginCapture;
    private Button btnProgress;
    private Button btnSetting;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Permissions.verifyPermissions(MainActivity.this);

        SharedPreferences prefs = getSharedPreferences(Config.MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("server", null);
        if (restoredText != null) {
            String server = prefs.getString("server", "http://tbdc.garuda45.org/patient/upload");
            Config.SERVER = server;
        }
        else {
            SharedPreferences.Editor editor = getSharedPreferences(Config.MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("server", "http://tbdc.garuda45.org/patient/upload");
            String server = prefs.getString("server", "http://tbdc.garuda45.org/patient/upload");
            Config.SERVER = server;
            editor.commit();
        }
        
        setContentView(R.layout.activity_main);

        btnBeginCapture = (Button) findViewById(R.id.btnCapturePicture);
        btnBeginCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptPatiendIDDialog();
            }
        });

        btnProgress = (Button) findViewById(R.id.btnProgress);
        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWipActivity();
            }
        });

        btnSetting = (Button) findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSettingActivity();
            }
        });

        btnExit = (Button) findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchExit();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Permissions.REQUEST_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int x : grantResults) {
                        if (x == PackageManager.PERMISSION_DENIED) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setTitle("Exiting Application");
                            alertDialogBuilder
                                    .setMessage("You cannot use any feature without granting the app permissions. Restart app to grant permissions.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    moveTaskToBack(true);
                                                    android.os.Process.killProcess(android.os.Process.myPid());
                                                    System.exit(1);
                                                }
                                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }
            }
        }
    }

    private void promptPatiendIDDialog() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_input_patient_id, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText editTextPatientID = (EditText) promptsView.findViewById(R.id.editTextPatientID);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String patientID = editTextPatientID.getText().toString().trim();
                                if (!patientID.equals("")) {
                                    if (isAllowed(patientID)) {
                                        launchCameraActivity(patientID);
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Patient ID must only contain letters, numbers, - or _!", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Patient ID must not be empty!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public boolean isAllowed(String str) {
        String regex = "^[a-zA-Z0-9_-]*$";
        boolean result = str.matches(regex);

        return result;
    }

    public boolean isAlphanumeric(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void launchCameraActivity(String patientID){
       Intent i = new Intent(MainActivity.this, CameraActivity.class);
        i.putExtra("patientID", patientID);
        i.putExtra("counter", 1);
        startActivity(i);
    }

    private void launchWipActivity(){
        Intent i = new Intent(MainActivity.this, WorkInProgressActivity.class);
        startActivity(i);
    }

    private void launchSettingActivity(){
        Intent i = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(i);
    }

    private void launchExit() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
        /*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        */
    }
}
