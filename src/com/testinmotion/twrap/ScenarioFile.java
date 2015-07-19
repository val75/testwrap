package com.testinmotion.twrap;

import java.util.*;
import java.io.*;

public class ScenarioFile {

	// Instance variables
	private String fileName;
	private String absoluteName;
	private String runDir;
	private boolean fileExists;
	private ArrayList<String> tcList;
	private String status;
	File scenarioFile;
	File scenarioDir;
	File resultsFile;
	
	// Instance methods
	
	// Method: setFileName
	// Sets the file name of the ScenarioFile object
	public void setFileName (String scenFileName) {
		fileName = scenFileName;
	}
	
	// Method: getFileName
	// Returns the file name of the ScenarioFile object
	public String getFileName () {
		return this.fileName;
	}
	
	// Method: setAbsoluteName
	// Sets the absolute (including directory path) name of the ScenarioFile object
	public void setAbsoluteName (String absName) {
		absoluteName = absName;
	}
	
	// Method: getAbsoluteName
	// Returns the absolute (including directory path) name of the ScenarioFile object
	public String getAbsoluteName () {
		return absoluteName;
	}
	
	// Method: setRunDir
	// Sets the running directory for the ScenarioFile object
	public void setRunDir (String scenDir) {
		runDir = scenDir;
	}
	
	// Method: getRunDir
	// Returns the running directory field for the ScenarioFile object
	public String getRunDir () {
		return this.runDir;
	}
	
	// Method: setFileExists
	// Sets the fileExists field of the ScenarioFile object
	public void setFileExists (boolean foundFile) {
		fileExists = foundFile;
	}
	
	// Method: getFileExists
	// Returns the value of the fileExists for the ScenarioFile object
	public boolean getFileExists () {
		return this.fileExists;
	}
	
	// Method: addTestCaseToList
	// Adds a test case name to the test case list of ScenarioFile object
	public void addTestCaseToList (String tcName) {
		tcList.add(tcName);
	}
	
	// Method: getTestCaseFromList
	// Returns test case at index i from test case list of ScenarioFile object
	public String getTestCaseFromList (int i) {
		return tcList.get(i);
	}
	
	// Method: getTestCaseList
	// Returns the whole test case list for ScenarioFile object
	public String[] getTestCaseList () {
		return (String[]) tcList.toArray();
	}
	
	// Method: setScenarioStatus
	// Sets the status of the scenario file (can be executing, executed or skipped)
	public void setScenarioStatus (String stat) {
		status = stat;
	}
	
	// Method: getScenarioStatus
	// Returns the status of the scenario file (can be executing, executed or skipped)
	public String getScenarioStatus () {
		return this.status;
	}
	
	// Method: setScenarioDir
	// Sets the scenario directory for the ScenarioFile object
	public void setScenarioDir (File scenDir) {
		scenarioDir = scenDir;
	}
	
	// Method: getScenarioDir
	// Returns the scenario directory for the ScenarioFile object
	public File getScenarioDir () {
		return this.scenarioDir;
	}
	
	// Method: createResultsFile
	// Creates results file for test cases in this scenario file
	public File createResultsFile () throws Exception {
		Tools.LogMessage('i', "Creating results file in " + this.getScenarioDir().toString() + "/");
		resultsFile = new File(this.getScenarioDir().toString() + "/results");
		try {
			resultsFile.createNewFile();
		}
		catch (IOException e) {
			//Tools.LogMessage('e', "Error while creating results file: " + e);
			throw new IOException("Error while creating results file", e);
		}
		
		try {
			FileWriter fw = new FileWriter(resultsFile);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("Results for scenario file " + this.getFileName());
			fw.close();
		}
		catch (IOException e) {
			//Tools.LogMessage('e', "Error while trying to write to results file: " + e);
			throw new IOException("Error while trying to write to results file", e);
		}
		
		return resultsFile;
	}
	
	// Method: initGlobalResultsFile
	// Writes a line identifying current scenario in the global results file 
	public void initGlobalResultsFile (File globalResultsFile) throws Exception {
		try {
			FileTools.WriteLineToFile(globalResultsFile, "### Results for scenario file " + this.getFileName() + " ###\n");
		}
		catch (IOException e) {
			//Tools.LogMessage('e', "Error while trying to write to global results file: " + e);
			throw new IOException("Error while trying to write to global results file", e);
		}
	}
	
	public void reportResults () {
		System.out.println("Results for scenario file " + this.getFileName());
	}
}
