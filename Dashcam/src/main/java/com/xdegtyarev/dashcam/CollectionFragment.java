package com.xdegtyarev.dashcam;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by degtyarev on 3/6/14.
 */
public class CollectionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FRAGMENT", "ON CREATE COLLECTION VIEW");
        View rootView = inflater.inflate(R.layout.collection_fragment, container, false);
        return rootView;
    }
}
