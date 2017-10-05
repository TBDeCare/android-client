package com.garuda45.tbdecarelab;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.File;

/**
 * Created by mure on 10/5/2017.
 */

public class UploadReceiver extends UploadServiceBroadcastReceiver {

    private static final String TAG = UploadReceiver.class.getSimpleName();

    File file;
    String uploadId;
    String patientID;

    public UploadReceiver(File file, String patientID, String uploadId) {
        this.file = file;
        this.uploadId = uploadId;
        this.patientID = patientID;
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        // your implementation
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        Log.e(TAG, serverResponse.getBodyAsString());
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        Log.e(TAG, serverResponse.getBodyAsString());
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        // your implementation
    }
}