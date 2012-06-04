package com.mjlim.overlaytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnKeyListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


public class OverlayView extends LinearLayout implements OnKeyListener, OnClickListener, OnTouchListener{
	
	private EditText ed;
	private Button bCopy;
	
	private boolean focused = true;
	private WindowManager.LayoutParams winparams;
	
	private ScaleGestureDetector ScaleGD;
	
	private WindowManager wm;
	private ClipboardManager clipboard;
	
	private Context context;
	
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
		
		winparams.gravity = Gravity.TOP;
		winparams.setTitle("HELLO!");
		//winparams.height = 60;
		winparams.height = 120;
		winparams.horizontalMargin = 10;
		this.setBackgroundColor(Color.BLUE);
		
//		tv = new TextView(context);
//		tv.setText("Text");
	
		ed = (EditText)findViewById(R.id.ed);
//		ed = new EditText(context);
//		ed.setText("default");
		
		bCopy = (Button)findViewById(R.id.bCopy);
		
//		edparams.width = ViewGroup.LayoutParams.MATCH_PARENT; // not working. need to move layout code to xml
		
//		this.addView(tv);
//		this.addView(ed);
		
		this.setOnTouchListener(this);
		this.setOnClickListener(this);
		this.setOnKeyListener(this);
		ed.setOnTouchListener(this);
		ed.setOnKeyListener(this);
		bCopy.setOnTouchListener(this);
		
		wm.addView(this, winparams);
		
		clipboard = (ClipboardManager)context.getSystemService(Activity.CLIPBOARD_SERVICE);
		
	}
	
	public void focus(){		
		this.focused = true;
		this.setBackgroundColor(Color.BLUE); // visual cue
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // this too, gets unset when FLAG_NOT_TOUCH_MODAL is turned off.
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag off
    	wm.updateViewLayout(this, winparams);
    	this.postInvalidate();//redraw
    	

	}
	
	public void unfocus(){	
		this.focused = false;
		this.setBackgroundColor(Color.BLACK); // visual cue
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag off
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag on
    	wm.updateViewLayout(this, winparams);
    	this.invalidate();//redraw
    	
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
		{
			if(v == ed){ // pass through to the text field if we're touching it
				ed.onTouchEvent(me);
			}else if(v.getClass() == Button.class){
				v.onTouchEvent(me);
				if(v == bCopy){
					clipboard.setText(ed.getText());
				}
			}
				
			this.focus();
			
			if(me.getPointerCount() > 1){
		        
				final int location[] = { 0, 0 };
			    v.getLocationOnScreen(location);

				float y1 = me.getY(0);
				float y2 = me.getY(1);
				
				float absy1 = (int)(winparams.y + y1);
				float absy2 = (int)(winparams.y + y2);
				
				
				
				//if(absy1 > absy2){int temp = absy2; absy2 = absy1; absy1 = temp;}
				
				winparams.y = (int)((absy1+absy2)/2);
				
				//winparams.height = (int)(absy2-absy1); // resizing with two-finger gesture is jittery. do something else

//				tv.setText(Float.toString(y1));
				
				wm.updateViewLayout(this, winparams);
		    	
			}
			
			
			
		}
		return true;
	}
}
