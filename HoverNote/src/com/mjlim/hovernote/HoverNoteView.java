/*
 * Copyright 2012 Mike Lim
 * 
 * This file is part of hovernote.
 *
 *  hovernote is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  hovernote is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with hovernote.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mjlim.hovernote;

import com.mjlim.hovernote.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;


public class HoverNoteView extends LinearLayout implements OnKeyListener, OnTouchListener, OnClickListener{
	
	private EditText ed;
	
	private LinearLayout layoutButtons;
	private ImageView resizeHandle;
	private LinearLayout moveHandle;
	private ImageView menuButton;
	
	private boolean focused = true;
	private boolean resizing = false;
	private boolean moving = false;
	
	// these are all used to calculate new positions and height when dragging the window.
	private int initialPtrX = 0; // the initial pointer x position, when beginning the drag action.
	private int initialPtrY = 0;
	private int initialX = 0; // initial x position of the window, at the time the drag action begins.
	private int initialY = 0;
	private int initialW = 0;
	private int initialH = 0;
	
	private WindowManager.LayoutParams winparams;
	
	private WindowManager wm;
	private ClipboardManager clipboard;
	
	private Context context;
	
	private Drawable drActiveRect;
	private Drawable drInactiveRect;
	
	// arbitrarily selected minimum sizes
	final private int MIN_WIDTH = 225;
	final private int MIN_HEIGHT = 80;
	final private int INITIAL_HEIGHT = 150;
	final private int INITIAL_WIDTH_TABLET = 400;
	
	public HoverNoteView(Context context, WindowManager wm, int y){
		this(context, wm, y, android.R.style.Animation_Dialog);
	}
	
	public HoverNoteView(Context context, WindowManager wm, int y, int transition ){
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
		winparams.height = INITIAL_HEIGHT;
		if(isTablet(context)){
			// if we are on a tablet, the width isn't locked to the width of the screen. therefore, define width
			winparams.width = INITIAL_WIDTH_TABLET;
			winparams.x = y;
		}

		winparams.y=y;
		
		// so that it can be pushed up by the IME
		winparams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		
		// retrieve blue border and gray border, to be used to indicate window focus state.
		drActiveRect = this.getResources().getDrawable(R.drawable.activerectangle);
		drInactiveRect = this.getResources().getDrawable(R.drawable.inactiverectangle);
		this.setBackgroundDrawable(drInactiveRect); // start out with gray inactive border. doesn't really matter I guess, but whatever.
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
		menuButton.setOnClickListener(this);
		menuButton.setOnKeyListener(this);
		
		winparams.windowAnimations = transition;
		
		wm.addView(this, winparams);
		
		clipboard = (ClipboardManager)context.getSystemService(Activity.CLIPBOARD_SERVICE); // retrieve clipboardmanager, to be used with copy and paste.
		
		this.focus(); 
		
	}
	
	public void focus(){		
		this.focused = true;
		this.setBackgroundDrawable(drActiveRect); // visual cue
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; // this too, gets unset when FLAG_NOT_TOUCH_MODAL is turned off.
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // set this flag off
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		
		((HoverNoteService) context).raiseOrUpdate(this, winparams);
		
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
		((HoverNoteService)context).closeNote(this); // tell the service to close me.
	}
	public WindowManager.LayoutParams getWindowParams(){
		return winparams; // yeah
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) 
    {
		switch(keyCode){
			case KeyEvent.KEYCODE_BACK:
				// back button unfocuses the note
				this.unfocus();
				return true;
			case KeyEvent.KEYCODE_MENU:
				// menu button shows the context menu. okay.
				showMenu();
				return true;
		}
        return false;
    }


	public boolean onTouch(View v, MotionEvent me) {
		// this handles touch events directed at any of the following:
		// * the hovernote window
		// * outside the hovernote window
		// * any views in the hovernote window
		// as such, it is huge and bloated. it works, but it's gross. will rework it in some way if I have time.
		if((me.getActionMasked() & MotionEvent.ACTION_OUTSIDE) == MotionEvent.ACTION_OUTSIDE){
			this.unfocus();
		}
		else
		{
			if(focused == false){
				this.focus(); // Focus the overlay, because we touched it.
				return true;
			}
			
			if(resizing == true){
				// are we in the middle of resizing?
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
				// are we in the middle of moving?
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
				return ed.onTouchEvent(me);
			}else if((resizing || moving) == false){ // only start resizing or moving if not already doing those.
				if(v == resizeHandle && me.getAction() == MotionEvent.ACTION_DOWN){
					// we're touching the resize handle. start resizing
					initialPtrX = (int)me.getRawX();
					initialPtrY = (int)me.getRawY();
					initialW = this.getWidth();
					initialH = this.getHeight();
					resizing = true;
				}else if(v == moveHandle && me.getAction() == MotionEvent.ACTION_DOWN){
					// touching the move handle, start that.
					initialPtrX = (int)me.getRawX();
					initialPtrY = (int)me.getRawY();
					initialX = winparams.x;
					initialY = winparams.y;		

					moving = true;
				}
			}
		}	
		return true;
	}
	
	public void onClick(View v) {
		if((v == menuButton)){
//			pop up the context menu at the location of the button.
			int pos[] = {0,0};
			v.getLocationOnScreen(pos);
			showMenu(pos[0],pos[1]);
		}
	}
	
	public void showMenu(){
//		show the context menu. default to position of menuButton
		int pos[] = {0,0};
		menuButton.getLocationOnScreen(pos);
		showMenu(pos[0],pos[1]);
	}
	
	public void showMenu(int x, int y){
//		
		unfocus();
		winparams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // required to block taps that close the context menu from activating apps below this one
    	wm.updateViewLayout(this, winparams);
		new contextmenu(context, this, wm, x, y+8);
		
	}
	public static boolean isTablet(Context context) {
		// returns whether the app thinks we are on a tablet or not.
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public void toggleButtons(){
		// show/hide button bar. this is a holdover from back when I wanted this to be a feature. I'll just leave it in.
		if(layoutButtons.getVisibility()==GONE){
			layoutButtons.setVisibility(VISIBLE);
		}else
			layoutButtons.setVisibility(GONE);
	}
	
	public void copy(){
		int start = ed.getSelectionStart();
		int end = ed.getSelectionEnd();
		if(start == end){
			clipboard.setText(ed.getText()); // just copy everything, nothing is selected.
		}else{
			clipboard.setText(ed.getText().subSequence(Math.min(start,end), Math.max(start,end))); // copy what is selected
		}
		
		Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
	}
	public void paste(){
		CharSequence in = clipboard.getText();
		int start = ed.getSelectionStart();
		int end = ed.getSelectionEnd();
		ed.setText(ed.getText().replace(Math.min(start, end), Math.max(start,end), in, 0, in.length()));
		Toast.makeText(context, "Pasted", Toast.LENGTH_SHORT).show();
	}
	
	public String getText(){
		return ed.getText().toString();
	}
	
	public void setText(String s){
		ed.setText(s);
	}
	public void createNotif(){
//		creates a notification that can be expanded by HoverNoteService into a copy of this note.
		((HoverNoteService)context).createNotifForNote(this);
	}
	public void moveTo(int x, int y){
		// reposition note window
		if(isTablet(context)){
			winparams.x = x;
		}
		winparams.y = y;
		wm.updateViewLayout(this, winparams);
	}
	public void resizeTo(int width, int height){
		// resize note window
		winparams.width =  width;
		winparams.height =  height;
		wm.updateViewLayout(this, winparams);
	}
	
	public void setWindowAnimation(int r){
		// changes the animation this window will use when opening or closing.
		// note that we can only use system animations
		winparams.windowAnimations = r;
		wm.updateViewLayout(this, winparams);
	}

}
