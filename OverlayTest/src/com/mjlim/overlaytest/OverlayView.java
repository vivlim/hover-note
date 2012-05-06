package com.mjlim.overlaytest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnKeyListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


public class OverlayView extends LinearLayout implements OnKeyListener, OnClickListener, OnTouchListener{
	private TextView tv;
	private EditText ed;
	private boolean focused = true;
	private WindowManager.LayoutParams winparams;
	
	private WindowManager wm;
	
	private Context context;
	
	public OverlayView(Context context, WindowManager wm){
		super(context);
		
		this.context = context;
		
		this.wm = wm;
		
		winparams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.TYPE_PHONE |
						WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
						WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		
		winparams.gravity = Gravity.RIGHT | Gravity.TOP;
		winparams.setTitle("HELLO!");
		winparams.height = 60;
		this.setBackgroundColor(Color.BLUE);
		
		tv = new TextView(context);
		tv.setText("Text");
		
		ed = new EditText(context);
		ed.setText("default");
		
		this.addView(tv);
		this.addView(ed);
		
		this.setOnTouchListener(this);
		this.setOnClickListener(this);
		this.setOnKeyListener(this);
		ed.setOnKeyListener(this);
		wm.addView(this, winparams);
		
	}
	
	public void focus(){		
		this.focused = true;
		this.setBackgroundColor(Color.BLUE); // visual cue
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // this too, gets unset when FLAG_NOT_TOUCH_MODAL is turned off.
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag off
    	wm.updateViewLayout(this, winparams);
    	this.postInvalidate();//redraw
    	tv.invalidate();

	}
	
	public void unfocus(){	
		this.focused = false;
		this.setBackgroundColor(Color.BLACK); // visual cue
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag off
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag on
    	wm.updateViewLayout(this, winparams);
    	this.invalidate();//redraw
    	tv.invalidate();
	}
	public WindowManager.LayoutParams getWindowParams(){
		return winparams;
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) 
    {
//		tv.setText("key");

//		this.unfocus();
		if(keyCode == KeyEvent.KEYCODE_BACK){
	        context.stopService(new Intent(context, OverlayTest.class));
		}
        return false;
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.focus();
	}

	public boolean onTouch(View v, MotionEvent me) {
		// TODO Auto-generated method stub
		if((me.getActionMasked() & MotionEvent.ACTION_OUTSIDE) == MotionEvent.ACTION_OUTSIDE){
			this.unfocus();
		}
		else
			this.focus();
		return true;
	}
}
