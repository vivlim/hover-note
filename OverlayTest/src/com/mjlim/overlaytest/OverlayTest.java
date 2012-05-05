package com.mjlim.overlaytest;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class OverlayTest extends Service {
	
	EditText tView;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
		
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Toast.makeText(getBaseContext(), "onCreate", Toast.LENGTH_LONG).show();
		tView = new EditText(this);
		tView.setHeight(40);
		tView.setText("HELLO");
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		params.gravity = Gravity.RIGHT | Gravity.TOP;
		params.setTitle("HELLO!");
		params.height = 60;
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.addView(tView,params);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(tView != null){
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(tView);
			
		}
	}
	
	
}