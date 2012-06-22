package com.mjlim.hovernote;

public class FilePickerOption implements Comparable<FilePickerOption>{
	private String name, data, path;
	
	public FilePickerOption(String name, String data, String path){
		this.name = name;
		this.data = data;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	public String getPath() {
		return path;
	}

	public int compareTo(FilePickerOption o){
		if(this.name != null){
			return this.name.compareTo(o.getName());
		}
		else{
			throw new IllegalArgumentException();
		}
	}
}
