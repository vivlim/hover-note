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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HoverNoteActivity extends Activity {
    /** Called when the activity is first created. */
	// this activity doesn't do anything but start the service and then go away.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        String action = intent.getAction();

        if(Intent.ACTION_SEND.equals(action)){ // sharing something to hovernote? do this.
        	// alter the intent action to work with the service
        	Intent i = new Intent(this, HoverNoteService.class);
        	i.setAction("com.mjlim.hovernote.SEND_TO_NOTE");
        	i.putExtras(intent); // rip all of the extras from the external intent
        	i.setDataAndType(intent.getData(), intent.getType());
        	startService(i); // pass it on
        	finish(); // go away
        }
        else{ // in any other case we just start a new note
	        // start service
	        Intent i = new Intent(this, HoverNoteService.class);
	        i.setAction(HoverNoteService.INTENT_NEW_NOTE);
	        startService(i);
	  
//	        setContentView(R.layout.main); // .. not really necessary, this is just the default hello world stuff LOL!
	        finish();
        }
    }
}
