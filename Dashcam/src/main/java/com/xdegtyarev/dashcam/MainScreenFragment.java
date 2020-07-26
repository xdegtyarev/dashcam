package com.xdegtyarev.dashcam;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainScreenFragment extends Fragment {
    public OnMenuButtonClickInterface callback;
    public boolean isPreviewingRecording;

    CameraPreview preview;

    public interface OnMenuButtonClickInterface {
        public void OnMenuButtonClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (OnMenuButtonClickInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("FRAGMENT","FRAGMENT PAUSED");
        Toast.makeText(getActivity().getBaseContext(), "MainScreenFragment paused", Toast.LENGTH_SHORT).show();
        if(RecordService.isRecording){
            if(isPreviewingRecording){
                Log.d("FRAGMENT","Closing previewer surface");
                RecordService.SetRecordProperties(null,true);
            }
        }else{
            Log.d("FRAGMENT","Recording not started yet, nothing to resume");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FRAGMENT","FRAGMENT RESUMED");
        Toast.makeText(getActivity().getBaseContext(), "MainScreenFragment resumed", Toast.LENGTH_SHORT).show();
        if(RecordService.isRecording){
            if(isPreviewingRecording){
                Log.d("FRAGMENT","Opening previewer surface");
                preview.shouldStartRecordAfterCreation = true;
                //RecordService.SetRecordProperties(preview.getHolder(), false);
            }
        }else{
            Log.d("FRAGMENT","Recording not started yet, nothing to resume");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("FRAGMENT","MY VIEW IS CREATED");
        view.findViewById(R.id.record_button).setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(RecordService.isRecording){
                    Log.d("FRAGMENT","INTENTING TO STOP RECORDING SERVICE");
                    isPreviewingRecording = false;
                    getActivity().stopService(new Intent(getActivity(), RecordService.class));
                }else{
                    Log.d("FRAGMENT","INTENTING TO START RECORDING SERVICE");
                    isPreviewingRecording = true;
                    RecordService.SetRecordProperties(preview.getHolder(),false);
                    Intent intent = new Intent(getActivity(),RecordService.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startService(intent);
                }
            }
        }
        );
        view.findViewById(R.id.toggle_menu_button).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                callback.OnMenuButtonClick();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FRAGMENT","ON CREATE VIEW");
        View rootView = inflater.inflate(R.layout.recorder_fragment, container, false);
        preview = new CameraPreview(getActivity());
        FrameLayout cameraPreviewPlaceholder = (FrameLayout)rootView.findViewById(R.id.cameraPreviewPlaceholder);
        cameraPreviewPlaceholder.addView(preview);
        return rootView;
    }
}
