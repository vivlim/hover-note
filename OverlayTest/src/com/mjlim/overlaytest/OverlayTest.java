package com.mjlim.overlaytest;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;


public class OverlayTest extends Service {
	
	EditText tView;
	OverlayView oView;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
		
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		Toast.makeText(getBaseContext(), "onCreate", Toast.LENGTH_LONG).show();
		tView = new EditText(this);
		tView.setText("HELLO");
		
		oView = new OverlayView(this, wm);
//		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//				WindowManager.LayoutParams.TYPE_PHONE,
//				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
//		params.gravity = Gravity.RIGHT | Gravity.TOP;
//		params.setTitle("HELLO!");
//		params.height = 60;
		
		
//        tView.setOnKeyListener(new OnKeyListener(){
//            public boolean onKey(View v, int keyCode, KeyEvent event) 
//            {
////            	wm.removeView(tView);
//        		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//        				WindowManager.LayoutParams.TYPE_PHONE,
//        				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        		params.gravity = Gravity.RIGHT | Gravity.TOP;
//        		params.setTitle("HELLO!");
//        		params.height = 60;
//            	wm.updateViewLayout(tView, params);
//            	Toast.makeText(getBaseContext(), "a keypress!", Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
        
//		wm.addView(tView,params);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(tView != null){
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(tView);
			
		}
	}
	
	
}