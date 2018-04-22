package com.garuda45.tbdecarelab.broadcastreceiver;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.garuda45.tbdecarelab.CameraActivity;
import com.garuda45.tbdecarelab.R;
import com.garuda45.tbdecarelab.Uploader;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mure on 10/6/2017.
 */

public class MyReceiver extends UploadServiceBroadcastReceiver {
    private static final String TAG = MyReceiver.class.getSimpleName();

    public static Uploader uploader;

    public MyReceiver() {
        super();
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        // your implementation
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        String message;

        try {
            message = serverResponse.getBodyAsString();
        }
        catch(Exception e) {
            message = exception.getMessage();
        }

        Log.e(TAG, message);

        // The id of the channel.
        String CHANNEL_ID = this.uploader.filenameNoPath;

        String compiled_error_message = this.uploader.filenameNoPath  + "\n\n" + message;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.uploader.context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Upload Error: " + this.uploader.patientID)
                        .setContentText(message + "\n" + this.uploader.filenameNoPath)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(compiled_error_message))
                        .setChannelId(CHANNEL_ID);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(CHANNEL_ID, CHANNEL_ID.hashCode(), mBuilder.build());

        File filename = new File(this.uploader.patientDirectory, this.uploader.filenameNoPath.replace(".jpg", "_errlog.txt"));
        try {
            // Write to SD Card
            FileOutputStream outStream = new FileOutputStream(filename);
            outStream.write(compiled_error_message.getBytes("UTF-8"));
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
        }
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        Log.d(TAG, serverResponse.getBodyAsString());
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        // your implementation
    }
}