package com.mjlim.hovernote;

import java.util.List;

import com.mjlim.hovernote.FilePickerOption.FileType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class FilePickerFileArrayAdapter extends ArrayAdapter {
	private Context context;
	private int id;
	private List<FilePickerOption>items;
	
	public FilePickerFileArrayAdapter(Context context, int textViewResourceId, List<FilePickerOption> objects){
		super(context,textViewResourceId, objects);
		this.context = context;
		this.id = textViewResourceId;
		this.items = objects;
	}
	
	public FilePickerOption getItem(int i)
	{
		return items.get(i);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v == null){
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(id, null);
			
		}
		final FilePickerOption o = items.get(position);
		if(o!= null){
			TextView title = (TextView)v.findViewById(R.id.fpiTitle);
			TextView detail = (TextView)v.findViewById(R.id.fpiDetail);
			
			ImageView icon = (ImageView)v.findViewById(R.id.fpiIcon);
			
			if(title != null){
				title.setText(o.getName());
			}
			if(detail != null){
				detail.setText(o.getData());
			}
			
			if(icon != null){
				if(o.getType() == FilePickerOption.FileType.FILE){
					icon.setImageResource(R.drawable.fileicon);
				}else if(o.getType() == FilePickerOption.FileType.FOLDER){
					icon.setImageResource(R.drawable.foldericon);
				}
			}
		}
		
		return v;
	}
}


