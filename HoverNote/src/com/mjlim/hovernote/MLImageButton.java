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
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class MLImageButton extends ImageView {

	TransitionDrawable t;
	public MLImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonglow));
		t = (TransitionDrawable)this.getBackground();
		// TODO Auto-generated constructor stub
	}
	public MLImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonglow));
		t = (TransitionDrawable)this.getBackground();
		// TODO Auto-generated constructor stub
	}
	public MLImageButton(Context context) {
		super(context);
		this.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.buttonglow));
		t = (TransitionDrawable)this.getBackground();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		
		if(me.getAction() == MotionEvent.ACTION_DOWN){
			t.startTransition(10);
		}else if(me.getAction() == MotionEvent.ACTION_UP){
			t.reverseTransition(100);
		}
		
		return super.onTouchEvent(me);
		
	}

}
