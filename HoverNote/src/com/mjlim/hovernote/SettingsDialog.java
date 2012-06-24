package com.mjlim.hovernote;


import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ViewFlipper;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class SettingsDialog extends LinearLayout implements OnSeekBarChangeListener, OnClickListener, OnCheckedChangeListener {
	
	SeekBar seekTransparencyBar;
	Button bDefaultTransparency;
	CheckBox chkNotifClose, chkAutosave;
	
	HoverNoteView note = null;

	public SettingsDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.settingsdialog, this);
		
		if(!this.isInEditMode()){
	
			seekTransparencyBar = (SeekBar)findViewById(R.id.seekTransparencyBar);
			bDefaultTransparency = (Button)findViewById(R.id.bDefaultTransparency);
			chkNotifClose = (CheckBox)findViewById(R.id.chkNotifClose);
			chkAutosave = (CheckBox)findViewById(R.id.chkAutosave);
			
			seekTransparencyBar.setOnSeekBarChangeListener(this);
			bDefaultTransparency.setOnClickListener(this);
			chkNotifClose.setOnCheckedChangeListener(this);
			chkAutosave.setOnCheckedChangeListener(this);
			
			this.setFocusableInTouchMode(true); // necessary, or we can't handle the back key
		}

	}

	
	public void setOnTouchListener(OnTouchListener v){
		super.setOnTouchListener(v);
		seekTransparencyBar.setOnTouchListener(v);
	}
	
	public void setOnKeyListener(OnKeyListener v){
		super.setOnKeyListener(v);
		seekTransparencyBar.setOnKeyListener(v);
		bDefaultTransparency.setOnKeyListener(v);
//		saveToPath.setOnKeyListener(v);
//		saveButton.setOnKeyListener(v);
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
		note.setAlpha(progressToAlpha(progress));
		
	}

	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setNote(HoverNoteView n){
		this.note = n;
		
		// set the seekbar to the appropriate position
		seekTransparencyBar.setProgress(alphaToProgress(n.getWindowParams().alpha));
		
		chkNotifClose.setChecked(note.getService().getNotifOnClose());
		chkAutosave.setChecked(note.getService().getAutosave());
	}


	public void onClick(View v) {
		if(v == bDefaultTransparency){
			note.getService().setDefaultAlpha(progressToAlpha(seekTransparencyBar.getProgress()));
		}
	}
	
	private float progressToAlpha(int progress){
		return (float)progress/100*.9f+.1f;
	}
	private int alphaToProgress(float alpha){
		// todo: make this more accurate
		return (int)(note.getWindowParams().alpha  * 100);
	}

	public void onCheckedChanged(CompoundButton v, boolean value) {
		if(v==chkNotifClose){
			note.getService().setNotifOnClose(value);
		}else if(v== chkAutosave){
			note.getService().setAutosave(value);
		}
		
	}
}
