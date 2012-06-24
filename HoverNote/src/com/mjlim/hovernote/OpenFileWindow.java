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
import android.widget.ImageView;
import android.widget.LinearLayout;

public class OpenFileWindow extends FloatingModalWindow implements OnKeyListener, OnFileSelectedListener{

	ImageView bPaste, bCopy, bClose, bMini, bShare, bSave, bSettings;
	FilePicker filePicker;
	SaveDialog sd;
	
	public OpenFileWindow(Context context, SaveDialog sd, String defaultLocation, HoverNoteView ov, WindowManager wm, int x, int y) {
		super(context, ov, wm, x, y);
		this.sd = sd;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.openfilewindow, this);

		filePicker = (FilePicker)findViewById(R.id.filePicker);
		
		// Assign listeners
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		
		filePicker.setOnKeyListener(this);
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
			sd.openFile(o.getPath());
			this.dismiss();
		}
		
	}
	

}
