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
		
		this.ov=ov;
		this.wm = wm;
		
		winparams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.TYPE_PHONE);
		
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; 
		winparams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		winparams.dimAmount = 0.1f;
		winparams.gravity = Gravity.LEFT | Gravity.TOP;
		winparams.setTitle("Context Menu");
		winparams.height = 150;
		winparams.width = 150;

		winparams.x=x;
		winparams.y=y;
		
		winparams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		
		Drawable drActiveRect = this.getResources().getDrawable(R.drawable.activerectangle);
		
		this.setBackgroundDrawable(drActiveRect);
		this.invalidate();
	
		// Retrieve UI elements
		//Buttons
		bCopy = (Button)findViewById(R.id.bCopy);
		bClose = (Button)findViewById(R.id.bClose);
		
		// Assign listeners
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		
		bCopy.setOnTouchListener(this);
		bClose.setOnTouchListener(this);
		
		wm.addView(this, winparams);
		
		clipboard = (ClipboardManager)context.getSystemService(Activity.CLIPBOARD_SERVICE);
		
	}

	public boolean onTouch(View v, MotionEvent me) {
		// TODO Auto-generated method stub
		if((me.getActionMasked() & MotionEvent.ACTION_OUTSIDE) == MotionEvent.ACTION_OUTSIDE){
			this.dismiss();
			return true;
		}
		
		if(v==bClose){
			wm.removeView(this);
			ov.close();
			return true;
		}
		
		return false;
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.dismiss();
		}
		return false;
		
	}
	
	public void dismiss(){
		wm.removeView(this);
	}

}
