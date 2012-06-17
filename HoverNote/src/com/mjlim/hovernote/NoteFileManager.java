package com.mjlim.hovernote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import android.os.Environment;
import android.util.Log;

public class NoteFileManager {
	public static String getFile(String filename){

		String returnValue;
		try{
			Log.v("com.mjlim.hovernote", "Trying to open file " + filename);
			File f = new File(filename);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			try{

				
				String line = null;
				
				StringBuilder contents = new StringBuilder();
				
				while((line = br.readLine()) != null){
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
				returnValue = contents.toString();
			} catch (FileNotFoundException e){
				returnValue= "File was not found!";
			}
			finally{
				br.close();
			}
		}catch (IOException e){
			returnValue = "There was a problem reading the file.";
		}
		return returnValue;

	}
	
	public static void writeFile(String filename, String contents) throws IOException {
		Log.v("com.mjlim.hovernote", "Trying to open file " + filename);
		
		FileWriter fw = new FileWriter(filename,false);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.write(contents);
		bw.close();

	}
}
