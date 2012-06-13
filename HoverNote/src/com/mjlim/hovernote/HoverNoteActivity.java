package com.mjlim.hovernote;

import com.mjlim.hovernote.R;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class HoverNoteActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start service
        Intent i = new Intent(this, HoverNoteService.class);
        i.setAction(HoverNoteService.INTENT_NEW_NOTE);
//        i.putExtra(OverlayTest.REMAKE_TEXT_KEY, "bluh");
        startService(i);
  
        setContentView(R.layout.main);
        finish();
    }
}
