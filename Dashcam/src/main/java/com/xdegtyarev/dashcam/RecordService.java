package com.xdegtyarev.dashcam;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordService extends Service implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener, SurfaceHolder.Callback {
    public static RecordService instance;
    public static boolean isRecording;
    public static boolean isBackgroudRecording;
    private Camera cam;
    public static SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private WindowManager windowManager;
    private MediaRecorder recorder;
    private int notificationID;

    public static void SetRecordProperties(SurfaceHolder holder, boolean background){
        isBackgroudRecording = background;
        if(instance!=null){
            if(isRecording){
                if(isBackgroudRecording){
                    Log.d("SERVICE","Trying to start record in background");
                    instance.RecordInBackground();
                }else{
                    Log.d("SERVICE","Trying to start record in foreground");
                    instance.RecordInForeground(holder);
                }
            }
        }else{
            surfaceHolder = holder;
        }
    }

    public void RecordInForeground(SurfaceHolder holder) {
        if(isRecording){
            StopRecorder();
            windowManager.removeView(surfaceView);
            fakeSurfaceCreated = false;
        }else{
            StartRecordNotification();
        }
        isBackgroudRecording = false;
        surfaceHolder = holder;
        Log.d("SERVICE","Starting record in foreground, surface is creating?:" + surfaceHolder.isCreating() + " and is " + surfaceHolder.toString());
        LaunchRecorder();
    }

    public void RecordInBackground(){
        if(isRecording){
            StopRecorder();
        }else{
            StartRecordNotification();
        }
        isBackgroudRecording = true;
        if (fakeSurfaceCreated) {
            surfaceHolder = surfaceView.getHolder();
            Log.d("SERVICE","Starting record in background, surface is creating?:" + surfaceHolder.isCreating() + " and is " + surfaceHolder.toString());
            LaunchRecorder();
        } else {
            Log.d("SERVICE","Starting record in background, with creating surface");
            CreateSurface();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVICE","Instance Created");
        instance = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("SERVICE","On Start Command");
        Log.d("SERVICE","SURF HOLDER ON START COMMAND IS" + surfaceHolder.toString());
        if(!isRecording){
            SetRecordProperties(surfaceHolder,false);
            RecordInForeground(surfaceHolder);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("SERVICE","SERVICE IS SHUTTING DOWN");
        ForceStopRec();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void StartRecordNotification() {
        notificationID = R.drawable.ic_recording_stat;
        Notification notification = new Notification.Builder(getApplicationContext()).setPriority(0x00000001)
                .setContentTitle("recording")
                .setContentText("record started")
                .setSmallIcon(R.drawable.ic_recording_stat)
                .setLights(0xFFFF0000, 10, 10)
                .setProgress(0, 0, true)
                .build();
        startForeground(notificationID, notification);
    }

    boolean fakeSurfaceCreated;
    private void CreateSurface() {

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                100, 100,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        fakeSurfaceCreated = true;
    }

    private void LaunchRecorder(){
        if(isBackgroudRecording){
            Log.d("SERVICE","Recorder starting in background and surf " + surfaceHolder.toString());
        }else{
            Log.d("SERVICE","Recorder starting in foreground and surf " + surfaceHolder.toString());
        }
        Toast.makeText(getBaseContext(), "Recorder Started", Toast.LENGTH_SHORT).show();

        isRecording = true;

        if(cam == null){
            cam = Camera.open();
            setCameraDisplayOrientation();
            cam.unlock();
            Log.d("SERVICE", "CAM UNLOCKED AND CREATED" + cam.toString());
        }

        if(recorder == null)
        {
            recorder = new MediaRecorder();
        }

        recorder.reset();
        recorder.setCamera(cam);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        recorder.setOutputFile(getOutputFile().toString());
        recorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            recorder.prepare();
        } catch (Exception e) {
            Log.d("SERVICE", "Preparations failed" + e.getMessage());
            recorder.release();
        }
        recorder.start();
    }

    private File getOutputFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "DashCamera");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("SERVICE", "failed to create directory");
            }
        }

        if (mediaStorageDir.canWrite()) {
            Log.d("SERVICE", "Can write");
        } else {
            Log.d("SERVICE", "Cant write");
        }
        File file = null;
        try {
            file = File.createTempFile("CAM_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()), ".mp4", mediaStorageDir);
            Log.d("SERVICE", "Saving to path:" + file.toString());
        } catch (IOException e) {
            Log.e("SERVICE", "Error creating temp file");
        }
        return file;
    }

    private void StopRecorder(){
        Toast.makeText(getBaseContext(), "Recorder Stopped", Toast.LENGTH_SHORT).show();
        recorder.stop();
        recorder.reset();
        recorder.release();
        cam.release();
        recorder = null;
        cam = null;
        isRecording = false;
    }

//StopRec
    public void ForceStopRec() {
        StopRecordNotification();
        Toast.makeText(getBaseContext(), "Recording Force Stopped", Toast.LENGTH_SHORT).show();
        StopRecorder();
        if(isBackgroudRecording){
            windowManager.removeView(surfaceView);
        }
    }

    void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        Log.i("SERVICE","Display orientation in Degrees " + degrees);
        int result;
        result = (info.orientation - degrees + 360) % 360;
        Log.i("SERVICE","Display orientation Result " + result);
        cam.setDisplayOrientation(result);
    }

    private void StopRecordNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationID);
    }
//Mediarec callbacks
    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i2) {
        Log.e("SERVICE", "error" + i + " " + i2);
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
        Log.i("SERVICE", "info" + i + " " + i2);
    }

//Surface callbacks
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Only in background
        LaunchRecorder();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }


}
