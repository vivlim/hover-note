package com.mjlim.overlaytest;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class contextmenu extends LinearLayout implements OnKeyListener, OnTouchListener {

	private WindowManager wm;
	private WindowManager.LayoutParams winparams;
	private ClipboardManager clipboard;

	OverlayView ov;
	Button bCopy, bClose;
	
	public contextmenu(Context context, OverlayView ov, WindowManager wm, int x, int y) {
		super(context);
		// TODO Auto-generated constructor stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.contextmenu, this);
		
		Drawable drActiveRect = this.getResources().getDrawable(R.drawable.activerectangle);
		
		this.setBackgroundDrawable(drActiveRect);
		
		this.ov=ov;
		this.wm = wm;
		
		winparams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_PHONE |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				WindowManager.LayoutParams.FLAG_DIM_BEHIND |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		
		winparams.dimAmount = 0.0f; // yes, this is indeed a dim with no amount. hackish way to stop presses intended to get rid of the menu from going through to apps underneath.
		
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		winparams.gravity = Gravity.LEFT | Gravity.TOP;
		winparams.setTitle("Context Menu");
		winparams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		winparams.width = 250;

		winparams.x=x;
		winparams.y=y;
		
		winparams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
	
		// Retrieve UI elements
		//Buttons
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);

		bCopy = (Button)findViewById(R.id.bCopy);
		bClose = (Button)findViewById(R.id.bClose);
//		((EditText)findViewById(R.id.editText1)).setOnKeyListener(this);
		// Assign listeners
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		
//		bCopy.setOnKeyListener(this);
//		bClose.setOnKeyListener(this);
//		
		bCopy.setOnTouchListener(this);
		bClose.setOnTouchListener(this);
		
		this.invalidate();
		wm.addView(this, winparams);

		this.requestFocus();
		
		clipboard = (ClipboardManager)context.getSystemService(Activity.CLIPBOARD_SERVICE);
		
	}

	public boolean onTouch(View v, MotionEvent me) {
		// TODO Auto-generated method stub
		if((me.getActionMasked() & (MotionEvent.ACTION_OUTSIDE | MotionEvent.ACTION_DOWN)) == (MotionEvent.ACTION_OUTSIDE | MotionEvent.ACTION_DOWN)){
			this.dismiss();
			return true;
		}
		
		if((v==bClose) && (me.getAction() == MotionEvent.ACTION_UP)){
			this.dismiss();
			ov.close();
			
			return true;
		}
		if((v==bCopy) && (me.getAction() == MotionEvent.ACTION_UP)){
			this.dismiss();
			ov.copy();
			
			return true;
		}

		return false;
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		this.dismiss();
		if(event.getAction() == KeyEvent.ACTION_UP){
			switch(keyCode){
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_MENU:
				this.dismiss();
				return true;

			}
		}
		return false;
		
	}
	

	
	public void dismiss(){
		wm.removeView(this);
		ov.focus();
		
	}

}
