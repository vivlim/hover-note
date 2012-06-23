package com.mjlim.hovernote;

public class FilePickerOption implements Comparable<FilePickerOption>{
	
	public enum FileType {
		FOLDER, FILE
	}
	
	private String name, data, path;
	private FileType type;
	
	public FilePickerOption(String name, String data, FileType type, String path){
		this.name = name;
		this.data = data;
		this.path = path;
		this.type = type;
	}

	public FileType getType() {
		return type;
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
