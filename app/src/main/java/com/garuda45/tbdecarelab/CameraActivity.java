package com.garuda45.tbdecarelab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.garuda45.tbdecarelab.broadcastreceiver.MyReceiver;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = CameraActivity.class.getSimpleName();

    String filenameNoPath;
    String patientID;
    public static int counter;
    String timeStamp;

    TextView labelPatientID;
    TextView labelCounter;
    ImageView image;
    Activity context;
    Preview preview;
    Camera camera;
    ImageView fotoButton;
    LinearLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        context = this;

        // Receiving the data from previous activity
        Intent i = getIntent();
        patientID = i.getStringExtra("patientID");
        counter = i.getIntExtra("counter", 1);

        if (counter == 1) {
            timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        }
        else {
            timeStamp = i.getStringExtra("timeStamp");
        }

        labelPatientID = (TextView) findViewById(R.id.labelPatientID);
        setLabelPatientID();

        labelCounter = (TextView) findViewById(R.id.labelCounter);
        setLabelCounter();

        fotoButton = (ImageView) findViewById(R.id.imageView_foto);
        image = (ImageView) findViewById(R.id.imageView_photo);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);

        preview = new Preview(this,
                (SurfaceView) findViewById(R.id.KutCameraFragment));
        FrameLayout frame = (FrameLayout) findViewById(R.id.preview);
        frame.addView(preview);
        preview.setKeepScreenOn(true);
        fotoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    takeFocusedPicture();
                } catch (Exception e) {

                }
                fotoButton.setClickable(false);
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void exitCameraActivity() {
        try {
            camera.stopPreview();
            camera.setPreviewCallback(null);

            camera.release();
        }
        catch(Exception e){}
        finally {
            camera = null;
        }
        this.finish();
    }

    private void setLabelPatientID() {
        labelPatientID.setText("Patient ID: " + patientID);
    }

    private void setLabelCounter() {
        labelCounter.setText("(" + counter + " of 100)");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO Auto-generated method stub
        if (camera == null) {
            camera = Camera.open();
            camera.startPreview();
            camera.setErrorCallback(new ErrorCallback() {
                public void onError(int error, Camera mcamera) {

                    camera.release();
                    camera = Camera.open();
                    Log.d("Camera died", "error camera");

                }
            });
        }
        if (camera != null) {
            if (Build.VERSION.SDK_INT >= 14)
                setCameraDisplayOrientation(context,
                        CameraInfo.CAMERA_FACING_BACK, camera);
            preview.setCamera(camera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId,
                                             android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        camera.setDisplayOrientation(result);
    }


    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

            try {
                camera.takePicture(mShutterCallback, null, jpegCallback);
            } catch (Exception e) {

            }

        }
    };

    Camera.ShutterCallback mShutterCallback = new ShutterCallback() {

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };

    public void takeFocusedPicture() {
        camera.autoFocus(mAutoFocusCallback);

    }

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // Log.d(TAG, "onPictureTaken - raw");
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        @SuppressWarnings("deprecation")
        public void onPictureTaken(byte[] data, Camera camera) {

            FileOutputStream outStream = null;
            Calendar c = Calendar.getInstance();

            //TBDC Image directory
            File imgDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
            if (!imgDirectory.exists()) {
                if (!imgDirectory.mkdirs()) {
                    Log.d(TAG, "Oops! Failed to create " + Config.IMAGE_DIRECTORY_NAME + " directory");
                    Toast.makeText(CameraActivity.this, "Oops! Failed to create " + Config.IMAGE_DIRECTORY_NAME + " directory", Toast.LENGTH_LONG).show();
                    exitCameraActivity();
                }
            }

            //Patient directory
            File patientDirectory = new File(imgDirectory.getAbsolutePath(), patientID);
            if (!patientDirectory.exists()) {
                if (!patientDirectory.mkdirs()) {
                    Log.d(TAG, "Oops! Failed to create " + patientDirectory + " directory");
                    Toast.makeText(CameraActivity.this, "Oops! Failed to create " + patientDirectory + " directory", Toast.LENGTH_LONG).show();
                    exitCameraActivity();
                }
            }

            filenameNoPath = patientID + "_" + counter + "_" + timeStamp + ".jpg";
            File filename = new File(patientDirectory, filenameNoPath);
            try {
                // Write to SD Card
                outStream = new FileOutputStream(filename);
                outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
            }

            try {
                MyReceiver.uploader = new Uploader(patientDirectory, context, filename, filenameNoPath, patientID);
                MyReceiver.uploader.uploadMultipart();
            }
            catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Bitmap realImage;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;

            options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

            options.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future


            realImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(filename.getAbsolutePath());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                Log.d("EXIF value",
                        exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("1")) {
                    realImage = rotate(realImage, 90);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("8")) {
                    realImage = rotate(realImage, 90);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("3")) {
                    realImage = rotate(realImage, 90);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("0")) {
                    realImage = rotate(realImage, 90);
                }
            } catch (Exception e) {

            }

            image.setImageBitmap(realImage);


            fotoButton.setClickable(true);
            counter++;

            if (counter > 100) {
                Toast.makeText(CameraActivity.this, "Patient \"" + patientID + "\"'s sample has been captured completely (100/100). Wait for upload to finish, or start capturing another patient's sample!", Toast.LENGTH_LONG).show();
                exitCameraActivity();
            }
            else {
                setLabelCounter();
                camera.startPreview();
                progressLayout.setVisibility(View.GONE);
            }
        }
    };

    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);

                    camera.release();
                }
                catch(Exception e){}
                finally {
                    camera = null;
                }
                this.finish();
                return true;
            default:
                try {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);

                    camera.release();
                }
                catch(Exception e){}
                finally {
                    camera = null;
                }
                return super.onOptionsItemSelected(item);
        }
    }
}