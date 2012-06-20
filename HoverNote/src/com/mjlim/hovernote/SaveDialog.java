package com.mjlim.hovernote;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.view.View.OnClickListener;

public class SaveDialog extends LinearLayout implements OnClickListener{
	
	HoverNoteView note;
	EditText saveToPath;
	ImageView saveButton;

	public SaveDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.savedialog, this);
		
		saveToPath = (EditText)findViewById(R.id.SDsaveToPath);
		saveButton = (ImageView)findViewById(R.id.SDsaveButton);
//		browseButton = (Button)findViewById(R.id.SDbrowseButton);
		
		saveButton.setOnClickListener(this);
//		browseButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		if(v==saveButton){
			note.saveFile(saveToPath.getText().toString());
			note.leaveDialogs();
		}
	}
	
	public void setOnTouchListener(OnTouchListener v){
		super.setOnTouchListener(v);
		saveToPath.setOnTouchListener(v);
		saveButton.setOnTouchListener(v);
	}
	
	public void setOnKeyListener(OnKeyListener v){
		super.setOnKeyListener(v);
		saveToPath.setOnKeyListener(v);
		saveButton.setOnKeyListener(v);
	}
	
	public void setFilename(String f){
		saveToPath.setText(f);
	}
	public void setNote(HoverNoteView n){
		this.note = n;
	}

}
