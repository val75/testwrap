package com.testinmotion.twrap;

import java.io.*;
import java.util.*;

public class Tools {

	// Method: Usage
	// Prints program usage and example
	public static void Usage () {
		System.out.println("\n\n##############################################################\n");
		System.out.println("Usage: twrap -scen <scenario_file> -topo <topology_file>\n\n");
		System.out.println("Optional arguments:\n");
		System.out.println("\t-build <build_number>");
		System.out.println("\t-results <path/file>");
		System.out.println("\t-workdir <work_dir>");
		System.out.println("\t-comment <comment_string");
		System.out.println("\t-mail <mailto_list>");
		System.out.println("\n##############################################################\n");
		System.exit(1);
	}
	
	// Method: LogMessage
	// Logs a given message with the indicated message level
	// Levels: INFO, WARN, ERROR, DIAG, SCENARIO, TCSTART, RESULT
	public static void LogMessage (char logLevel, String logMsg) {
		String msgString = null;
		
		switch(logLevel) {
		case 'i':
			msgString = "--> TWRAP-INFO:      ";
			break;
		case 'w':
			msgString = "--> TWRAP-WARN:      ";
			break;
		case 'e':
			msgString = "--> TWRAP-ERROR:     ";
			break;
		case 'd':
			msgString = "--> TWRAP-DIAG:      ";
			break;
		case 's':
			msgString = "--> TWRAP-SCENARIO:  ";
			break;
		case 't':
			msgString = "--> TWRAP-TCSTART:   ";
			break;
		case 'r':
			msgString = "--> TWRAP-RESULT:    ";
		}
		
		System.out.println(msgString + logMsg);
	}
	
	// Method: CheckFile
	// Verifies if a file exists
	public static boolean CheckFile (String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	// Method: CheckIfDirectory
	// Verify if a File is directory
	public static boolean CheckIfDirectory (String fileName) {
		File f = new File(fileName);
		if (f.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}
	
	// Method: CreateDirectory
	// Create a new directory based on given directory name
	public static boolean CreateDirectory (String directoryName) {
		File d = new File(directoryName);
		if (d.mkdirs()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean CheckExecutable (String fileName) {
		File f = new File(fileName);
		if (f.canExecute()) {
			return true;
		} else {
			return false;
		}
	}
	
	// Method: GetFileName
	// Returns the name of the file without the directories in the path
	public static String GetFileName (String fileName) {
		File f = new File(fileName);
		return f.getName();
	}
	
	// Method: StringToFile
	// Returns the File corresponding to the String
	public static File StringToFile (String fileName) {
		File f = new File(fileName);
		return f;
	}
	
	// Method: GetTimeStamp
	// Returns a formated time stamp string
	public static String GetTimeStamp () {
		Calendar c = Calendar.getInstance();
		String s = String.format("%tm%td%ty_%tH%tM%tS", c, c, c, c, c, c);
		return s;
	}
	
	// Method: CreateLogFile
	// Tees stdout and stderr to a log file
	public static LogStream CreateLogFile (File logDir, String logName) throws IOException {
		LogStream ls = new LogStream(System.out, logDir + logName, true);
		System.setOut(ls);
		System.setErr(ls);
		
		return ls;
	}
	
	// Method: CloseLogFile
	// Calls flush and closes LogStream logfile
	public static void CloseLogFile (LogStream lf) {
		lf.flush();
		lf.close();
	}
	
	// Method: CopyFile
	// Copies a file bit by bit
	public static void CopyFile (String srcFile, String destDir) {
		Tools.LogMessage('i', "Copy " + srcFile + " to " + destDir);
		File inputFile = new File(srcFile);
		String fileName = Tools.GetFileName(srcFile);
		File outputFile = new File(destDir + fileName);
		
		try {
			FileReader in = new FileReader(inputFile);
			FileWriter out = new FileWriter(outputFile);
			int c;
			
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			
			in.close();
			out.close();
		}
		catch (IOException e) {
			Tools.LogMessage('e', "Found error while copying file " + srcFile + ": " + e);
		}
	}
	
	// Method: CreateGlobalResultsFile
	// Creates a results file specified by user, containing all results for all scenario files
	public static File CreateGlobalResultsFile (String fileName) throws Exception {
		Tools.LogMessage('i', "Creating global results file: " + fileName);
		File globResultsFile = new File(fileName);
		try {
			globResultsFile.createNewFile();
		}
		catch (IOException e) {
			Tools.LogMessage('e', "Error while creating user results file " + fileName + ": " + e);
		}
		
		try {
			FileTools.WriteLineToFile(globResultsFile, "\n######################");
			FileTools.WriteLineToFile(globResultsFile, "### Global Results ###");
			FileTools.WriteLineToFile(globResultsFile, "######################\n");
		}
		catch (IOException e) {
			Tools.LogMessage('e', "Error while trying to write to global results file: " + e);
		}
		
		return globResultsFile;
	}
	
	// Method: EndGlobalResultsFile
	// Prints end banner in global results file
	public static void EndGlobalResultsFile (File fileName) throws Exception {
		try {
			FileTools.WriteLineToFile(fileName, "\n##########################");
			FileTools.WriteLineToFile(fileName, "### End Global Results ###");
			FileTools.WriteLineToFile(fileName, "##########################\n");
		}
		catch (IOException e) {
			Tools.LogMessage('e', "Error while trying to write to global results file " + fileName.getName());
			throw new IOException(e);
		}
	}
	
	// Method: ExecTestScript
	// Executes a test script/program
	public static boolean ExecTestScript (String execScript) throws Exception {
		try {
			Process p = Runtime.getRuntime().exec(execScript);
			
			InputStreamReader esr = new InputStreamReader(p.getErrorStream());
			BufferedReader ereader = new BufferedReader(esr);
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader ireader = new BufferedReader(isr);
			
			String line = null;
			String line1 = null;
			while ((line = ireader.readLine()) != null ) {
				System.out.println("Output: " + line);
			}
			while ((line1 = ereader.readLine()) != null) {
				System.err.println("Error: " + line1);
			}
						
			int exitValue = p.waitFor();
			Tools.LogMessage('d', "Test Case exit value: " + exitValue);
			if (exitValue == 0) {
				return true;
			} else {
				return false;
			}
		}
		
		catch (IOException ioe) {
			System.out.println("Error: " + ioe.getMessage());
			throw new Exception(ioe);
		}
		
		catch (InterruptedException e) {
			System.out.println("Error: " + e.getMessage());
			throw new Exception(e);
		}
	}
	
	// Method: GetLatestBuild
	// Returns the latest valid build number
	public static String GetLatestBuild () {
		String buildNumber = "1.0.0";
		return buildNumber;
	}
	
	// Method: CheckBuildNumberValid
	// Check if the given build number is valid
	public static boolean CheckBuildNumberValid (String buildNum) {
		return true;
	}
	
	// Method SendResultEmail
	// Send email to the specified list
	public static void SendResultsEmail (String emailList, String resultsComment) {
		Tools.LogMessage('i', "Sending result to email " + emailList);
	}
}
