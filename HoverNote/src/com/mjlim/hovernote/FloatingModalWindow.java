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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public abstract class FloatingModalWindow extends LinearLayout implements OnKeyListener, OnTouchListener {

	protected WindowManager wm;
	protected WindowManager.LayoutParams winparams;

	HoverNoteView noteView;
	
	public FloatingModalWindow(Context context, HoverNoteView ov, WindowManager wm, int x, int y) {
		super(context);
				
		Drawable drActiveRect = this.getResources().getDrawable(R.drawable.activerectangle);
		
		this.setBackgroundDrawable(drActiveRect);
		
		this.noteView=ov;
		this.wm = wm;
		
		ov.unfocusForPopup();
		
		winparams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_PHONE |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				WindowManager.LayoutParams.FLAG_DIM_BEHIND |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		
		winparams.dimAmount = 0.0f; // yes, this is indeed a dim with no amount. hackish way to stop presses intended to get rid of the window from going through to apps underneath.
		
		winparams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // set this flag on
		winparams.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		winparams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		winparams.gravity = Gravity.LEFT | Gravity.TOP;
		winparams.setTitle("hovernote popup window");
		winparams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		winparams.width = WindowManager.LayoutParams.WRAP_CONTENT;

		winparams.x=x;
		winparams.y=y;
		
		winparams.windowAnimations = android.R.style.Animation_Toast;
		
		winparams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
	
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);

		this.setFocusable(true);
		this.setFocusableInTouchMode(true);	
	}

	public boolean onTouch(View v, MotionEvent me) {
		if((me.getActionMasked() & (MotionEvent.ACTION_OUTSIDE | MotionEvent.ACTION_DOWN)) == (MotionEvent.ACTION_OUTSIDE | MotionEvent.ACTION_DOWN)){
			// if you have clicked outside of the window, dismiss this context menu.
			this.dismiss();
			return true;
		}
		
		return false;
	}
	
	public void dismiss(){
		// go away.
		wm.removeView(this);
		noteView.focus(); // focus parent
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// basic: close this window when hitting back
		if(event.getAction() == KeyEvent.ACTION_UP){
			switch(keyCode){
			case KeyEvent.KEYCODE_BACK:
				this.dismiss();
				return true;
			}
		}
		return false;
	}

}
