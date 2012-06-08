package com.mjlim.overlaytest;

import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
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
//	OverlayView oView;
	LinkedList<OverlayView> oViews; 
	
	OverlayView top; // topmost element
	
	private NotificationManager nm;
	
	private WindowManager wm;
	
	private Notification notification;
	private final int NOTIFICATION_ID=3333;
	
	public static final String INTENT_NEW_NOTE = "com.mjlim.OverlayTest.NEW_NOTE";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
		
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		oViews = new LinkedList<OverlayView>();
		
		Toast.makeText(getBaseContext(), "onCreate", Toast.LENGTH_LONG).show();
		tView = new EditText(this);
		tView.setText("HELLO");
		
		int icon = R.drawable.ic_launcher;
		CharSequence notifText = "HoverNote";
		notification = new Notification(icon, notifText,System.currentTimeMillis());
		
		updateNotification();
			
	}
	
	public int onStartCommand(Intent i, int flags, int startId){
		
		if(i.getAction() == INTENT_NEW_NOTE){
			newNote();
		}
		return START_STICKY;
	}
	
	public void updateNotification(){
		updateNotification("HoverNote", "Select to open a note");
	}
	public void updateNotification(CharSequence title, CharSequence text){
		Intent notificationIntent = new Intent(this, OverlayTestActivity.class);
		notificationIntent.setAction(INTENT_NEW_NOTE);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), title, text, contentIntent);
	}
	public void newNote(){
		
		if(oViews.size() == 0){
			// this is the first note; make a persistent notification and clear any temporary ones
			nm.cancel(NOTIFICATION_ID);
			startForeground(NOTIFICATION_ID, notification);
		}else{
			for(int i=0; i< oViews.size(); i++){
				oViews.get(i).unfocus(); // Unfocus all of the notes if opening a new one.
			}
		}
		Point size = new Point();
		int screenHeight = wm.getDefaultDisplay().getHeight();
		OverlayView oView = new OverlayView(this, wm, ((oViews.size()+1)*30) % (screenHeight - 200));
		oViews.add(oView);
		
	}
	
	public void closeNote(OverlayView v){
		oViews.remove(v);
		wm.removeView(v);
		
		if(oViews.size() == 0){
			// this is the last note; clear the persistent notification and create a temporary one
			stopForeground(true);
			nm.notify(NOTIFICATION_ID, notification);
		}
		
		
	}
	
	public void raiseOrUpdate(OverlayView v, WindowManager.LayoutParams winparams){
		if(top == v){
			// v is the top note, no need to remove and add.
			wm.updateViewLayout(v, winparams);
		}else{
			wm.removeView(v);
			wm.addView(v, winparams);
			top = v; // note that v is the new top.
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(tView != null){
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(tView);
			
		}
	}
	
	
	
	
}