package com.mjlim.overlaytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnKeyListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


public class OverlayView extends LinearLayout implements OnKeyListener, OnClickListener, OnTouchListener{
	
	private EditText ed;
	private Button bCopy;
	private Button bClose;
	private LinearLayout layoutButtons;
	private ImageView resizeHandle;
	private TextView moveHandle;
	
	private boolean focused = false;
	private boolean resizing = false;
	private boolean moving = false;
	
	private int initialPtrX = 0; // used for dragging
	private int initialPtrY = 0;
	private int initialX = 0;
	private int initialY = 0;
	private int initialW = 0;
	private int initialH = 0;
	
	private WindowManager.LayoutParams winparams;
	
	private ScaleGestureDetector ScaleGD;
	
	private WindowManager wm;
	private ClipboardManager clipboard;
	
	private Context context;
	
	private Drawable drActiveRect;
	private Drawable drInactiveRect;
	
	final private int MIN_WIDTH = 350;
	final private int MIN_HEIGHT = 128;
	
	private OverlayTest hnService;
	
	
	public OverlayView(Context context, WindowManager wm){
		super(context);
		
		this.context = context;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.overlay, this);
		
		this.wm = wm;
		
		winparams = new WindowManager.LayoutParams(
						WindowManager.LayoutParams.TYPE_PHONE |
						WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
						WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		
		winparams.gravity = Gravity.LEFT | Gravity.TOP;
		winparams.setTitle("HELLO!");
		//winparams.height = 60;
		winparams.height = 120;
//		winparams.width= 600;
		winparams.x=30;
		winparams.y=30;
		
		winparams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		
		drActiveRect = this.getResources().getDrawable(R.drawable.activerectangle);
		drInactiveRect = this.getResources().getDrawable(R.drawable.inactiverectangle);
		this.setBackgroundDrawable(drInactiveRect);
		this.invalidate();
		
//		tv = new TextView(context);
//		tv.setText("Text");
	
		ed = (EditText)findViewById(R.id.ed);
//		ed = new EditText(context);
//		ed.setText("default");
		
		
		//Buttons
		bCopy = (Button)findViewById(R.id.bCopy);
		bClose = (Button)findViewById(R.id.bClose);
		layoutButtons = (LinearLayout)findViewById(R.id.layoutButtons);
		resizeHandle = (ImageView)findViewById(R.id.resizeHandle);
		moveHandle = (TextView)findViewById(R.id.moveHandle);
		
		// Assign listeners
		this.setOnTouchListener(this);
		this.setOnClickListener(this);
		this.setOnKeyListener(this);
		ed.setOnTouchListener(this);
		ed.setOnKeyListener(this);
		bCopy.setOnTouchListener(this);
		bClose.setOnTouchListener(this);
		resizeHandle.setOnTouchListener(this);
		moveHandle.setOnTouchListener(this);
		
		wm.addView(this, winparams);
		
		clipboard = (ClipboardManager)context.getSystemService(Activity.CLIPBOARD_SERVICE);
		
		//Bind to service
//		context.bindService(new Intent(context, OverlayTest.class), hnService, 0);
		
		this.unfocus();
		
	}
	
	public void focus(){		
		this.focused = true;
		this.setBackgroundDrawable(drActiveRect); // visual cue
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // this too, gets unset when FLAG_NOT_TOUCH_MODAL is turned off.
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag off
    	wm.updateViewLayout(this, winparams);
    	this.postInvalidate();//redraw
    	

	}
	
	public void unfocus(){	
		this.focused = false;
		this.setBackgroundDrawable(drInactiveRect); // visual cue
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag off
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag on
    	wm.updateViewLayout(this, winparams);
    	this.invalidate();//redraw
    	
	}
	
	public void close(){
//		this.
//		wm.removeView(this);
//		wm.removeView(this);
		((OverlayTest)context).closeNote(this);
//		context.stopService(new Intent(context, OverlayTest.class));
		

	}
	public WindowManager.LayoutParams getWindowParams(){
		return winparams;
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) 
    {
//		tv.setText("key");

//		this.unfocus();
		if(event.getAction() == KeyEvent.ACTION_UP)
		if(keyCode == KeyEvent.KEYCODE_BACK){
//	        context.stopService(new Intent(context, OverlayTest.class));
	        this.unfocus(); 
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			if(layoutButtons.getVisibility()==GONE){
				layoutButtons.setVisibility(VISIBLE);
			}else
				layoutButtons.setVisibility(GONE);
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
//			return true;
		}
		else
		{
			if(focused == false){
				this.focus(); // Focus the overlay, because we touched it.
//				return true;
			}
			
			
			if((me.getAction() == MotionEvent.ACTION_POINTER_UP) && (me.getPointerCount() == 2) ){ // Tap with two fingers: show/hide buttons.
		        
				if(layoutButtons.getVisibility()==GONE){
					layoutButtons.setVisibility(VISIBLE);
				}else{
					layoutButtons.setVisibility(GONE);
				}
			}
			else // not doing multitouch gestures
			{
				if(resizing == true){
					if(me.getAction() == MotionEvent.ACTION_UP){
						// Motion has ended, so stop the drag.
						resizing = false;
						
					}else{
						winparams.height = Math.max(initialH + ((int)me.getRawY() - initialPtrY), MIN_HEIGHT);
						if(isTablet(context)){
							winparams.width =  Math.max(initialW + ((int)me.getRawX() - initialPtrX), MIN_WIDTH);
						}
						wm.updateViewLayout(this, winparams);
						this.invalidate();
					}
				}
				else if(moving == true){
					if(me.getAction() == MotionEvent.ACTION_UP){
						// Motion has ended, so stop the drag.
						moving = false;

					}else{
						winparams.y = initialY + ((int)me.getRawY() - initialPtrY);
						if(isTablet(context)){
							winparams.x = initialX + ((int)me.getRawX() - initialPtrX);
						}
						wm.updateViewLayout(this, winparams);
						this.invalidate();
					}
				}
				if(v == ed){ // pass through to the text field if we're touching it
					ed.onTouchEvent(me);
				}else if(v.getClass() == Button.class){
					v.onTouchEvent(me);
					if(v == bCopy){
						clipboard.setText(ed.getText());
						Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
					}else if((v == bClose) && (me.getAction() == MotionEvent.ACTION_UP)){
				        this.close();
					}
				}else if((resizing || moving) == false){ // only start resizing or moving if not already doing those.
					if(v == resizeHandle && me.getAction() == MotionEvent.ACTION_DOWN){
						initialPtrX = (int)me.getRawX();
						initialPtrY = (int)me.getRawY();
						initialW = this.getWidth();
//						if(initialW < 0){initialW = 400;}
						initialH = this.getHeight();
						resizing = true;
					}else if(v == moveHandle && me.getAction() == MotionEvent.ACTION_DOWN){
						initialPtrX = (int)me.getRawX();
						initialPtrY = (int)me.getRawY();
						initialX = winparams.x;
						initialY = winparams.y;						

						moving = true;
					}
				}
			}	
			
			
			
			
		}
		return true;
	}
	
	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}
