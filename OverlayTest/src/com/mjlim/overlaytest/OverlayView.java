package com.mjlim.overlaytest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.ClipboardManager;
import android.view.Display;
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
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;


public class OverlayView extends LinearLayout implements OnKeyListener, OnTouchListener, OnClickListener{
	
	private EditText ed;
	
	private LinearLayout layoutButtons;
	private ImageView resizeHandle;
	private LinearLayout moveHandle;
	private ImageView menuButton;
	
	private boolean focused = true;
	private boolean resizing = false;
	private boolean moving = false;
	
	private int initialPtrX = 0; // used for dragging
	private int initialPtrY = 0;
	private int initialX = 0;
	private int initialY = 0;
	private int initialW = 0;
	private int initialH = 0;
	
	private WindowManager.LayoutParams winparams;
	
	private WindowManager wm;
	private ClipboardManager clipboard;
	
	private Context context;
	
	private Drawable drActiveRect;
	private Drawable drInactiveRect;
	
	final private int MIN_WIDTH = 350;
	final private int MIN_HEIGHT = 128;
	
	public OverlayView(Context context, WindowManager wm, int y){
		this(context, wm, y, android.R.style.Animation_Dialog);
	}
	
	public OverlayView(Context context, WindowManager wm, int y, int transition ){
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
		winparams.setTitle("HoverNote");
		winparams.height = 150;
		if(isTablet(context)){
			winparams.width = 400;
			winparams.x = y;
		}

		winparams.y=y;
		
		winparams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		
		drActiveRect = this.getResources().getDrawable(R.drawable.activerectangle);
		drInactiveRect = this.getResources().getDrawable(R.drawable.inactiverectangle);
		this.setBackgroundDrawable(drInactiveRect);
		this.invalidate();
	
		// Retrieve UI elements
		ed = (EditText)findViewById(R.id.ed); // text field
	
		//Buttons
		layoutButtons = (LinearLayout)findViewById(R.id.layoutButtons);
		resizeHandle = (ImageView)findViewById(R.id.resizeHandle);
		moveHandle = (LinearLayout)findViewById(R.id.moveHandle);
		menuButton = (ImageView)findViewById(R.id.menuButton);
		
		// Assign listeners
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		ed.setOnTouchListener(this);
		ed.setOnKeyListener(this);
		resizeHandle.setOnTouchListener(this);
		moveHandle.setOnTouchListener(this);
//		menuButton.setOnTouchListener(this);
		menuButton.setOnClickListener(this);
		menuButton.setOnKeyListener(this);
		
		winparams.windowAnimations = transition;
		
		
		wm.addView(this, winparams);
		
		clipboard = (ClipboardManager)context.getSystemService(Activity.CLIPBOARD_SERVICE);
		
		//Bind to service
//		context.bindService(new Intent(context, OverlayTest.class), hnService, 0);
		
		this.focus();
		
	}
	
	public void focus(){		
		this.focused = true;
		this.setBackgroundDrawable(drActiveRect); // visual cue
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // this too, gets unset when FLAG_NOT_TOUCH_MODAL is turned off.
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag off
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		
		((OverlayTest) context).raiseOrUpdate(this, winparams);
		
    	this.postInvalidate();//redraw
    	

	}
	
	public void unfocus(){	
		this.focused = false;
		this.setBackgroundDrawable(drInactiveRect); // visual cue
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag off
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
    	wm.updateViewLayout(this, winparams);
    	this.invalidate();//redraw
    	
	}
	
	public void close(){
		((OverlayTest)context).closeNote(this);
		

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
			showMenu();
		}
        return false;
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
				return true;
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
	
	public void onClick(View v) {
		if((v == menuButton)){
			int pos[] = {0,0};
			v.getLocationOnScreen(pos);
			showMenu(pos[0],pos[1]);
		}
	}
	
	public void showMenu(){
		int pos[] = {0,0};
		menuButton.getLocationOnScreen(pos);
		showMenu(pos[0],pos[1]);
	}
	
	public void showMenu(int x, int y){
		
		unfocus();
		winparams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // required to block taps that close the context menu from activating apps below this one
    	wm.updateViewLayout(this, winparams);
		new contextmenu(context, this, wm, x, y+8);
		
	}
	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public void toggleButtons(){
		if(layoutButtons.getVisibility()==GONE){
			layoutButtons.setVisibility(VISIBLE);
		}else
			layoutButtons.setVisibility(GONE);
	}
	
	public void copy(){
		clipboard.setText(ed.getText());
		Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
	}
	
	public String getText(){
		return ed.getText().toString();
	}
	
	public void setText(String s){
		ed.setText(s);
	}
	public void createNotif(){
		((OverlayTest)context).createNotifForNote(this);
	}
	public void moveTo(int x, int y){
		if(isTablet(context)){
			winparams.x = x;
		}
		winparams.y = y;
		wm.updateViewLayout(this, winparams);
	}
	public void resizeTo(int width, int height){
		winparams.width =  width;
		winparams.height =  height;
		wm.updateViewLayout(this, winparams);
	}
	
	public void setWindowAnimation(int r){
		winparams.windowAnimations = r;
		wm.updateViewLayout(this, winparams);
	}

}
