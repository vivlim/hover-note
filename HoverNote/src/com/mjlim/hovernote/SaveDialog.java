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

public class SaveDialog extends LinearLayout implements OnClickListener{
	
	HoverNoteView note;
//	EditText saveToPath;
	ImageView saveButton, openButton;
//	Button browseButton;
	Button saveOver;
	
	String openFilename=null;
	
	Context context;
	
	String defaultSaveLocation;
	
	OnDialogClosedListener onCloseListener;

	public SaveDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.savedialog, this);
		
		if(!this.isInEditMode()){

//			saveToPath = (EditText)findViewById(R.id.SDsaveToPath);
			saveButton = (ImageView)findViewById(R.id.SDsaveButton);
			openButton = (ImageView)findViewById(R.id.SDopenButton);
			saveOver = (Button)findViewById(R.id.saveOver);
//			browseButton = (Button)findViewById(R.id.SDbrowseButton);
			
			this.setOnClickListener(this);
			
			saveOver.setVisibility(GONE);
			
			this.setFocusableInTouchMode(true); // necessary, or we can't handle the back key

			
			SharedPreferences settings = context.getApplicationContext().getSharedPreferences(HoverNoteService.PREFS_NAME, 0);
			defaultSaveLocation = settings.getString("defaultSaveLocation", "/sdcard/");
		}
	}

	public void onClick(View v) {
		String saveToPath;
		if(note.getFilename() == null){
			saveToPath = defaultSaveLocation;
		}else{
			try{
				File f = new File(note.getFilename());
				if(f.isFile()){
					// this is a file, so get its parent (a directory)
					saveToPath = f.getParent();
				}else{
					// this isn't a file, it's a directory.
					saveToPath = f.getPath();
				}
			}
			catch(Exception e){
				// something failed so just default to default save location
				saveToPath = defaultSaveLocation;
			}
		}
		if(v==saveButton){
			SaveFileWindow sfw = new SaveFileWindow(context, this, saveToPath, note, note.getWm(), note.getWindowParams().x+3, note.getWindowParams().y+3);
		}else if(v== openButton){
			OpenFileWindow ofw = new OpenFileWindow(context, this, saveToPath, note, note.getWm(), note.getWindowParams().x+3, note.getWindowParams().y+3);
		}else if(v == saveOver){
			saveFile(openFilename);
		}
	}
	
	
	public void setOnTouchListener(OnTouchListener v){
		super.setOnTouchListener(v);
		saveButton.setOnTouchListener(v);
		openButton.setOnTouchListener(v);
		saveOver.setOnTouchListener(v);
	}
	
	public void setOnKeyListener(OnKeyListener v){
		super.setOnKeyListener(v);
		saveButton.setOnKeyListener(v);
		openButton.setOnKeyListener(v);
		saveOver.setOnKeyListener(v);
	}
	
	public void setOnClickListener(OnClickListener v){
		super.setOnClickListener(v);
		saveButton.setOnClickListener(v);
		openButton.setOnClickListener(v);
		saveOver.setOnClickListener(v);
	}
	
	public void setFilename(String path){
//		saveToPath.setText(f);
		openFilename = path;
		String fileName = new File(path).getName().toString();
		saveOver.setText("Save over " + fileName);
		saveOver.setVisibility(VISIBLE);
		
	}
	public void setNote(HoverNoteView n){
		this.note = n;
	}
	
	public void setOnDialogClosedListener(OnDialogClosedListener o){
		this.onCloseListener = o;
	}
	
	public void openFile(String path){
		onCloseListener.onDialogClosed(); // close dialog
    	Intent i = new Intent(context.getApplicationContext(), HoverNoteService.class);
    	i.setAction(HoverNoteService.INTENT_OPEN_NOTE_FILE);
    	i.setDataAndType(Uri.fromFile(new File(path)), "text/plain"); // data and type too
    	context.getApplicationContext().startService(i); // pass it on
    	
	}
	
	public void saveFile(String path){
		note.saveFile(path);
		onCloseListener.onDialogClosed(); // close dialog
	}

}
