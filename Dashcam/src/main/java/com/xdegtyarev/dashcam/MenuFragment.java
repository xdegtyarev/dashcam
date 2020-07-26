package com.xdegtyarev.dashcam;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by degtyarev on 3/5/14.
 */
public class MenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FRAGMENT", "ON CREATE VIEW");
        View rootView = inflater.inflate(R.layout.menu_fragment, container, false);
        return rootView;
    }
}
