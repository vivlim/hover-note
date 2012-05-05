package com.mjlim.overlaytest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class OverlayTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start service
        startService(new Intent(this, OverlayTest.class));

        setContentView(R.layout.main);
    }
}
