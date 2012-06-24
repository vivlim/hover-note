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

import java.io.File;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SaveFileWindow extends FloatingModalWindow implements OnKeyListener, OnClickListener, OnFileSelectedListener, OnEditorActionListener{

	ImageView bPaste, bCopy, bClose, bMini, bShare, bSave, bSettings;
	FilePicker filePicker;
	SaveDialog sd;
	EditText fileName;
	Button saveButton;
	
	public SaveFileWindow(Context context, SaveDialog sd, String defaultLocation, HoverNoteView ov, WindowManager wm, int x, int y) {
		super(context, ov, wm, x, y);
		this.sd = sd;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.savefilewindow, this);

		filePicker = (FilePicker)findViewById(R.id.filePicker);
		fileName = (EditText)findViewById(R.id.filename);
		saveButton = (Button)findViewById(R.id.save);
		
		// Assign listeners
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		saveButton.setOnClickListener(this);
		
		filePicker.setOnKeyListener(this);
		fileName.setOnKeyListener(this);
		fileName.setOnEditorActionListener(this);
		filePicker.setFileSelectedListener(this);
		
		filePicker.openDirectory(new File(defaultLocation));
		
		winparams.y = 0;
		winparams.height=LayoutParams.MATCH_PARENT;
		
		this.invalidate();
		wm.addView(this, winparams); // make it visible
		this.requestFocus();	
		
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_UP){
			switch(keyCode){
			case KeyEvent.KEYCODE_BACK:
				this.dismiss();
				return true;
			}
		}
		return false;
	}

	public void onFileSelected(FilePickerOption o) {
		if(o.getType() == FilePickerOption.FileType.FILE){
			fileName.setText(o.getName());
		}
		
	}

	public void onClick(View v) {
		if(v==saveButton){
			doSave();
		}
	}

	public boolean onEditorAction(TextView v, int action, KeyEvent arg2) {
		if(v == fileName){
			doSave();
			return true;
		}
		return false;
	}
	
	public void doSave(){
		try{
			if(fileName.getText().charAt(0) == '/')
			{ // this is a path, so assume we know what we're doing, and just save to it
				sd.saveFile(fileName.getText().toString());
			}else{ // not a path, so we must use the file picker's current directory path
				String fn = fileName.getText().toString();
				if(!((fn.endsWith(".txt")) || (fn.endsWith(".hnautosave")))){fn = fn + ".txt";} // if the filename doesn't end with .txt or .hnautosave, append .txt
				String saveToPath = filePicker.getCurrentDirectoryPath() + "/" + fn;
				sd.saveFile(saveToPath);
				this.dismiss();
			}
		}catch(Exception e){
		
		}
	}

}
