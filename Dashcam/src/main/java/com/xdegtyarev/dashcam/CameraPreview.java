package com.xdegtyarev.dashcam;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    public SurfaceHolder holder;
    public Camera previewCam;

    public CameraPreview(Context context) {
        super(context);
        //previewCam = Camera.open();
        holder = getHolder();
        holder.addCallback(this);
    }

    public boolean shouldStartRecordAfterCreation;
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d("CAMPREVIEW","SURF CREATED!");
        if(shouldStartRecordAfterCreation){
            shouldStartRecordAfterCreation = false;
            RecordService.SetRecordProperties(holder, false);
        }
        //try{
        //    setCameraDisplayOrientation();
//          // cam.setPreviewDisplay(holder);
//          //  cam.startPreview();
//
//        }catch (Exception ex){
//            Log.d("CAMERAPREVIEW",ex.getMessage());
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.d("CAMPREVIEW","SURFACE CHANGED CALLBACK");
      //  cam = Camera.open();
        //setCameraDisplayOrientation();
//        cam.stopPreview();
      //  try {
    //        cam.reconnect();
  //      } catch (IOException e) {
//            Log.d("Err","Error reconnecting cam" + e.getMessage());
//        }
//        setCameraDisplayOrientation();
        //cam.startPreview();
    }

    void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = getDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        Log.i("CAMERAPREVIEW","Display orientation in Degrees " + degrees);
        int result;
        result = (info.orientation - degrees + 360) % 360;
        Log.i("CAMERAPREVIEW","Display orientation Result " + result);
//        cam.setDisplayOrientation(result);
//        cam.release();
//        cam =null;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("CAMPREVIEW","SURF DESTROYED!");
//        if(!RecordService.isPreviewingRecording){
//            //cam.stopPreview();
//        }
    }
}