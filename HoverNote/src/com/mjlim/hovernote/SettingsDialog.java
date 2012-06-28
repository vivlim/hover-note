package com.mjlim.hovernote;


import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class SettingsDialog extends LinearLayout implements OnSeekBarChangeListener, OnClickListener, OnCheckedChangeListener, OnItemSelectedListener {
	
	Context context;
	
	SeekBar seekTransparencyBar, seekFontSize;
	Button bDefaultTransparency;
	CheckBox chkNotifClose, chkAutosave;
	Spinner spinnerFontChoices;
	TextView fontSample;
	
	HoverNoteView note = null;

	public SettingsDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.settingsdialog, this);
		
		if(!this.isInEditMode()){
	
			seekTransparencyBar = (SeekBar)findViewById(R.id.seekTransparencyBar);
			bDefaultTransparency = (Button)findViewById(R.id.bDefaultTransparency);
			chkNotifClose = (CheckBox)findViewById(R.id.chkNotifClose);
			chkAutosave = (CheckBox)findViewById(R.id.chkAutosave);
			
			seekFontSize = (SeekBar)findViewById(R.id.seekFontSize);
			spinnerFontChoices = (Spinner)findViewById(R.id.spinnerFontChoices);
			fontSample = (TextView)findViewById(R.id.fontSample);
			
			seekTransparencyBar.setOnSeekBarChangeListener(this);
			bDefaultTransparency.setOnClickListener(this);
			chkNotifClose.setOnCheckedChangeListener(this);
			chkAutosave.setOnCheckedChangeListener(this);
			seekFontSize.setOnSeekBarChangeListener(this);
			spinnerFontChoices.setOnItemSelectedListener(this);
			
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
		if(seekBar == seekTransparencyBar){
			note.setAlpha(progressToAlpha(progress));
		}else if(seekBar == seekFontSize){
			note.setFontSize(progress/3 + 5);
			note.getService().setFontSize(progress/3+5);
			fontSample.setTextSize(progress/3 + 5);
		}
		
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
		
		seekFontSize.setProgress((int)(note.getService().getFontSize() * 3)-15);
		fontSample.setTextSize(note.getService().getFontSize());
		fontSample.setTextAppearance(context, note.getService().getFontFace());
		
		switch(note.getService().getFontFace()){
		case R.style.defaultfont:
			spinnerFontChoices.setSelection(0);
			break;
		case R.style.monospacefont:
			spinnerFontChoices.setSelection(1);
			break;
		case R.style.seriffont:
			spinnerFontChoices.setSelection(2);
		}
		
		
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


	public void onItemSelected(AdapterView<?> av, View v, int arg2,
			long arg3) {
		if(av == spinnerFontChoices){
			switch(arg2){
			case 0:
				// normal
				note.setFontFace(R.style.defaultfont);
				note.getService().setFontFace(R.style.defaultfont);
				fontSample.setTextAppearance(context,R.style.defaultfont);
				break;
			case 1:
				// mono
				note.setFontFace(R.style.monospacefont);
				note.getService().setFontFace(R.style.monospacefont);
				fontSample.setTextAppearance(context,R.style.monospacefont);
				break;
			case 2:
				// serif
				note.setFontFace(R.style.seriffont);
				note.getService().setFontFace(R.style.seriffont);
				fontSample.setTextAppearance(context,R.style.seriffont);
				break;
				
			}
		}
		
	}


	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
