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
