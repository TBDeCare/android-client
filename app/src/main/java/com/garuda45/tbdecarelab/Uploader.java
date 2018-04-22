package com.garuda45.tbdecarelab;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceSingleBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.File;
import java.util.UUID;

/**
 * Created by mure on 10/6/2017.
 */

public class Uploader {

    private static final String TAG = Uploader.class.getSimpleName();

    public File patientDirectory;
    public Activity context;
    public File file;
    public String filenameNoPath;
    public String patientID;

    private UploadNotificationConfig notificationConfig;

    public Uploader(File patientDirectory, Activity context, File file, String filenameNoPath, String patientID) {
        this.patientDirectory = patientDirectory;
        this.context = context;
        this.file = file;
        this.filenameNoPath = filenameNoPath;
        this.patientID = patientID;
    }

    public boolean uploadMultipart() {
        String path = file.getAbsolutePath();

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            notificationConfig = new UploadNotificationConfig();
            notificationConfig.setTitleForAllStatuses(filenameNoPath);
            notificationConfig.getCompleted().autoClear = true;
            notificationConfig.getError().autoClear = true;

            //Creating a multi part request
            new MultipartUploadRequest(this.context, uploadId, Config.getFileUploadUrl())
                    .addFileToUpload(path, "photo")
                    .setBasicAuth("admin", "tbdc-garuda45")
                    .addParameter("patient_id", patientID)
                    .setNotificationConfig(notificationConfig)
                    .setMaxRetries(3)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this.context, exc.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
