package com.mjlim.hovernote;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mjlim.hovernote.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class HoverNoteService extends Service {
	
	EditText tView;
//	OverlayView oView;
	LinkedList<HoverNoteView> oViews; 
	
	HoverNoteView top; // topmost element
	
	private NotificationManager nm;
	
	private WindowManager wm;
	
	private Notification notification;
	private final int NOTIFICATION_ID=3333;
	
	public static final String INTENT_NEW_NOTE = "com.mjlim.hovernote.NEW_NOTE";
	public static final String INTENT_REMAKE_NOTE = "com.mjlim.hovernote.REMAKE_NOTE";
	public static final String REMAKE_TEXT_KEY = "com.mjlim.hovernote.text";
	public static final String REMAKE_X_KEY = "com.mjlim.hovernote.x";
	public static final String REMAKE_Y_KEY = "com.mjlim.hovernote.y";
	public static final String REMAKE_WIDTH_KEY = "com.mjlim.hovernote.width";
	public static final String REMAKE_HEIGHT_KEY = "com.mjlim.hovernote.height";
	
	
	
	private int notifCount = 0;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
		
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		oViews = new LinkedList<HoverNoteView>();
		

		int icon = R.drawable.notificon_24;
		CharSequence notifText = "hovernote";
		notification = new Notification(icon, notifText,System.currentTimeMillis());
		
		updateNotification();
			
	}
	
	public int onStartCommand(Intent i, int flags, int startId){
		super.onStartCommand(i, flags, startId);
		if(i.getAction().equals(INTENT_NEW_NOTE)){
			newNote();
		}else if(i.getAction().equals(INTENT_REMAKE_NOTE)){
			HoverNoteView note = newNote(i.getStringExtra(REMAKE_TEXT_KEY), android.R.style.Animation_Translucent);
			int x = i.getIntExtra(REMAKE_X_KEY, 0);
			int y = i.getIntExtra(REMAKE_Y_KEY, 0);
			int width = i.getIntExtra(REMAKE_WIDTH_KEY, 0);
			int height = i.getIntExtra(REMAKE_HEIGHT_KEY, 0);
			note.moveTo(x, y);
			note.resizeTo(width, height);
		}
		else{
			newNote();
		}
		return START_STICKY;
	}
	
	public void updateNotification(){
		updateNotification("hovernote", "Select to open a note");
	}
	public void updateNotification(CharSequence title, CharSequence text){
		Intent notificationIntent = new Intent(this, HoverNoteActivity.class);
		notificationIntent.setAction(INTENT_NEW_NOTE);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), title, text, contentIntent);
	}
	public void newNote(){
		newNote("", android.R.style.Animation_Dialog);
	}
	public HoverNoteView newNote(String s, int animation){
		
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
		HoverNoteView oView = new HoverNoteView(this, wm, ((oViews.size()+1)*30) % (screenHeight - 200), animation);
		oView.setText(s);
		oViews.add(oView);
		return oView;
		
	}
	
	public void closeNote(HoverNoteView v){
		oViews.remove(v);
		wm.removeView(v);
		
		if(oViews.size() == 0){
			// this is the last note; clear the persistent notification and create a temporary one
			stopForeground(true);
			nm.notify(NOTIFICATION_ID, notification);
			this.stopSelf();
		}
		
		
	}
	
	public void raiseOrUpdate(HoverNoteView v, WindowManager.LayoutParams winparams){
		if(top == v){
			// v is the top note, no need to remove and add.
			wm.updateViewLayout(v, winparams);
		}else{
			v.setWindowAnimation(android.R.style.Animation_Toast); // necessary, otherwise windows restored from being minimized will slide around a lot (distractingly)
			wm.removeView(v);
			wm.addView(v, winparams);
			top = v; // note that v is the new top.
		}
	}
	
	
/*	@Override
	public void onDestroy() {
		int offset=0;
		for(int i = 0; i < oViews.size(); i++){
			
			OverlayView v = oViews.get(i);
			createNotifForNote(v,offset);
			closeNote(v);
			offset++;
		}
//		super.onDestroy(); // not required

		
	}
	*/
	
	public void createNotifForNote(HoverNoteView v){
		createNotifForNote(v,0);
	}
	public void createNotifForNote(HoverNoteView v, int offset){
		int icon = R.drawable.notificon_24;
		CharSequence notifText = "hovernote stored note";
		Notification n = new Notification(icon, notifText,System.currentTimeMillis() - offset);
		n.flags = Notification.FLAG_AUTO_CANCEL; // make notif remove itself when clicked
		
		String title = "hovernote stored note";
		String text = v.getText();
		
		
		Intent nIntent = new Intent(this.getApplicationContext(), HoverNoteService.class);
		nIntent.putExtra(REMAKE_TEXT_KEY, text);
		WindowManager.LayoutParams wp = v.getWindowParams(); 
		nIntent.putExtra(REMAKE_X_KEY, wp.x);
		nIntent.putExtra(REMAKE_Y_KEY, wp.y);
		nIntent.putExtra(REMAKE_HEIGHT_KEY, wp.height);
		nIntent.putExtra(REMAKE_WIDTH_KEY, wp.width);
		nIntent.setAction(INTENT_REMAKE_NOTE);
		nIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		nIntent.setData((Uri.parse("foobar://"+SystemClock.elapsedRealtime())));
		
		PendingIntent cIntent = PendingIntent.getService(this, 0, nIntent, PendingIntent.FLAG_UPDATE_CURRENT); // PendingIntent.FLAG_ONE_SHOT

		n.setLatestEventInfo(getApplicationContext(), title, text, cIntent);
		nm.notify(notifCount, n);
		notifCount++;
	}
	
	
	
	
}