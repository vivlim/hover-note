package com.mjlim.hovernote;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.View.OnClickListener;

public class FilePicker extends LinearLayout implements OnItemClickListener, OnClickListener, OnEditorActionListener {

	// Used http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/ as a reference!
	
	ListView fileList;
	TextView pathView;
	MLImageButton upLevel,clearFilter;
	EditText filter;
	
	Context c;
	
	File currentFile;
	
	OnFileSelectedListener onSelectedListener;
	
	private FilePickerFileArrayAdapter adapter;
	
	public FilePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.filepicker, this);
		fileList = (ListView)findViewById(R.id.fileList);
		pathView = (TextView)findViewById(R.id.pathView);
		filter = (EditText)findViewById(R.id.filter);
		upLevel = (MLImageButton)findViewById(R.id.upLevel);
		clearFilter = (MLImageButton)findViewById(R.id.clearFilter);
		
//		String test[] = {"one", "two", "three"};
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1,test);
//		
		
		openDirectory(new File("/sdcard/"));
		
		upLevel.setOnClickListener(this);
		clearFilter.setOnClickListener(this);
		fileList.setOnItemClickListener(this);
		filter.setOnEditorActionListener(this);
		
		

	}
	
	public void openDirectory(File f){
		
		currentFile = f;
		
		final String filterString = filter.getText().toString(); 
		final String filterRegex = "(?i).*(" + filterString + ").*";
		
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(c.getApplicationContext());
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(c.getApplicationContext());
		
		File[]dc = f.listFiles(); // directory contents
		this.pathView.setText(f.getPath());
		List<FilePickerOption>dirs = new ArrayList<FilePickerOption>();
		List<FilePickerOption>files = new ArrayList<FilePickerOption>();
		
		try{
			for(File file: dc){ // iterate through file in directory contents
				if(file.getName().matches(filterRegex)){
					if(file.isDirectory()){// Is this a directory?
						dirs.add(new FilePickerOption(file.getName(),"Folder", FilePickerOption.FileType.FOLDER, file.getAbsolutePath()));
					}
					else if(file.getName().endsWith(".txt")){
						// It's not a directory, so it's a file!
						Date d = new Date(file.lastModified());
						files.add(new FilePickerOption(file.getName(), "Modified: " + dateFormat.format(d) + " " + timeFormat.format(d), FilePickerOption.FileType.FILE, file.getAbsolutePath()));
						
					}
				}
			}
		}catch(Exception e){
			Log.v("com.mjlim.hovernote", "Problem populating file picker; " + e.getStackTrace().toString());
		}
		Collections.sort(dirs);
		Collections.sort(files);
		dirs.addAll(files);
		
//		dirs.add(0, new FilePickerOption("..", "Parent", f.getParent()));
		
		adapter = new FilePickerFileArrayAdapter(c,R.layout.filepickeritem,dirs);
		fileList.setAdapter(adapter);
		fileList.invalidate();
	}

	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		// TODO Auto-generated method stub
		FilePickerOption o = adapter.getItem(position);
		if(o.getType() == FilePickerOption.FileType.FOLDER){
			filter.setText("");
			openDirectory(new File(o.getPath()));
		}else if (o.getType() == FilePickerOption.FileType.FILE){
			// open the file
			onSelectedListener.onFileSelected(o); // issue callback

		}
	}

	public void onClick(View v) {
		if(v == upLevel){
			if(!currentFile.getAbsolutePath().equals("/")){ // only navigate up if we aren't at root
				openDirectory(currentFile.getParentFile());
			}
		}else if(v==clearFilter){
			filter.setText("");
			openDirectory(currentFile);
		}
		
	}

	public boolean onEditorAction(TextView tv, int action, KeyEvent event) {
		// TODO Auto-generated method stub
		openDirectory(currentFile);
		return false;
	}

	public void setOnKeyListener(OnKeyListener okl){
		super.setOnKeyListener(okl);
		filter.setOnKeyListener(okl);
		fileList.setOnKeyListener(okl);
	}

	public void setFileSelectedListener(OnFileSelectedListener o) {
		onSelectedListener = o;	
	}

}
