package com.mjlim.hovernote;


import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class SaveDialog extends LinearLayout implements OnClickListener, OnFileSelectedListener{
	
	HoverNoteView note;
	EditText saveToPath;
	ImageView saveButton, openButton;
	Button browseButton;
	
	Context context;
	
	String defaultSaveLocation;
	
	OnDialogClosedListener onCloseListener;

	public SaveDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.savedialog, this);
		
		if(!this.isInEditMode()){

			saveToPath = (EditText)findViewById(R.id.SDsaveToPath);
			saveButton = (ImageView)findViewById(R.id.SDsaveButton);
			openButton = (ImageView)findViewById(R.id.SDopenButton);
			browseButton = (Button)findViewById(R.id.SDbrowseButton);
			
			saveButton.setOnClickListener(this);
			openButton.setOnClickListener(this);
			browseButton.setOnClickListener(this);
			
			SharedPreferences settings = context.getApplicationContext().getSharedPreferences(HoverNoteService.PREFS_NAME, 0);
			defaultSaveLocation = settings.getString("defaultSaveLocation", "/sdcard/");
			saveToPath.setText(defaultSaveLocation);
		}
	}

	public void onClick(View v) {
		if(v==saveButton){
			note.saveFile(saveToPath.getText().toString());
			onCloseListener.onDialogClosed();
		}else if(v== openButton){
			try{
				openFile(saveToPath.getText().toString());
				onCloseListener.onDialogClosed();
				saveToPath.setText(defaultSaveLocation); // reset
			}catch (Exception e){
				Toast.makeText(context, "Sorry, there was an error opening the file.", Toast.LENGTH_SHORT).show();
			}
		}else if (v==browseButton){
			FilePickerWindow fpw = new FilePickerWindow(context, note, note.getWm(), note.getWindowParams().x+3, note.getWindowParams().y+3);
			fpw.setFileSelectedListener(this);
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

	public void onFileSelected(FilePickerOption o) {
		this.saveToPath.setText(o.getPath());
		
	}
	
	public void setOnDialogClosedListener(OnDialogClosedListener o){
		this.onCloseListener = o;
	}
	
	private void openFile(String path){
    	Intent i = new Intent(context.getApplicationContext(), HoverNoteService.class);
    	i.setAction(HoverNoteService.INTENT_OPEN_NOTE_FILE);
    	i.setDataAndType(Uri.fromFile(new File(path)), "text/plain"); // data and type too
    	context.getApplicationContext().startService(i); // pass it on
	}

}
