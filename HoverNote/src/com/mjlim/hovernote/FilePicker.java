package com.mjlim.hovernote;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FilePicker extends LinearLayout {

	// Used http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/ as a reference!
	
	ListView fileList;
	TextView pathView;
	Context c;
	
	private FilePickerFileArrayAdapter adapter;
	
	public FilePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.filepicker, this);
		fileList = (ListView)findViewById(R.id.fileList);
		pathView = (TextView)findViewById(R.id.pathView);
		
//		String test[] = {"one", "two", "three"};
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1,test);
//		
		
		openDirectory(new File("/sdcard/"));
		fileList.setAdapter(adapter);

	}
	
	public void openDirectory(File f){
		File[]dc = f.listFiles(); // directory contents
		this.pathView.setText(f.getPath());
		List<FilePickerOption>dirs = new ArrayList<FilePickerOption>();
		List<FilePickerOption>files = new ArrayList<FilePickerOption>();
		
		try{
			for(File file: dc){ // iterate through file in directory contents
				if(file.isDirectory()){// Is this a directory?
					dirs.add(new FilePickerOption(file.getName(),"Folder",file.getAbsolutePath()));
				}
				else{
					// It's not a directory, so it's a file!
					files.add(new FilePickerOption(file.getName(), "File Size: " + file.length(), file.getAbsolutePath()));
				}
			}
		}catch(Exception e){
			Log.v("com.mjlim.hovernote", "Problem populating file picker; " + e.getStackTrace().toString());
		}
		Collections.sort(dirs);
		Collections.sort(files);
		dirs.addAll(files);
		
		dirs.add(0, new FilePickerOption("..", "Parent", f.getParent()));
		
		adapter = new FilePickerFileArrayAdapter(c,R.layout.filepickeritem,dirs);
	}
	
	
	

}
