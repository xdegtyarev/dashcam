package com.xdegtyarev.dashcam;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements MainScreenFragment.OnMenuButtonClickInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainScreenFragment())
                    .commit();
            getFragmentManager().beginTransaction()
                    .add(R.id.menu_container, new MenuFragment())
                    .commit();
        }
    }
    boolean isOpened;
    @Override
    public void OnMenuButtonClick() {
        Log.d("ACTIVITY","Recieved menu button click");
        FrameLayout menuContainer = (FrameLayout)findViewById(R.id.menu_container);
        FrameLayout recorderContainer = (FrameLayout)findViewById(R.id.container);
        if(!isOpened){
            isOpened = true;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
            menuContainer.setLayoutParams(lp);
            recorderContainer.setTranslationX(300);
            menuContainer.setTranslationX(0);
        }else{
            isOpened = false;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            menuContainer.setLayoutParams(lp);
            recorderContainer.setTranslationX(0);
            menuContainer.setTranslationX(-300);
        }
    }
}
