package com.testinmotion.twrap;

import java.io.*;

public class FileTools {
	
	// Method: WriteLineToFile
	// Writes one line to an existing file, then closes the writing stream
	public static void WriteLineToFile (File fileName, String msgToWrite) throws Exception {
		try {
			FileWriter fw = new FileWriter(fileName, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(msgToWrite);
			fw.close();
		}
		catch (IOException e) {
			throw new IOException(e);
		}
	}
	
	// Method: ListFile
	// Reads a file and prints its content on the screen line by line
	public static void ListFile (File fileName) throws Exception {
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line;
			
			while ((line = br.readLine()) != null)
				System.out.println(line);
		}
		catch (IOException e) {
			Tools.LogMessage('e', "Error while reading file " + fileName.getName());
			throw new IOException(e);
		}
	}
}