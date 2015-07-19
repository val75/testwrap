package com.testinmotion.twrap;

import java.io.*;

public class TestCase {
	
	// Instance variables
	private String name;
	private String testcaseDep;
	private boolean isActive;
	private String execCommandLine;
	private String execScript;
	private String execArguments;
	enum Result { Pass, Fail, Skip, Error };
	Result testResult;
	File resultsFile;
	File globalResultsFile;
	private InstanceTools tcInstanceTools;

	// Instance methods
	
	// Method: setInstanceTools
	// Initializes the instance tools methods
	public void setInstanceTools (InstanceTools fromMainInstanceTools) {
		tcInstanceTools = fromMainInstanceTools;
	}
	
	// Method: setName
	// Sets the name of the test case object
	public void setName (String tcName) {
		name = tcName;
	}
	
	// Method: getName
	// Returns the name of the test case object
	public String getName () {
		return this.name;
	}
	
	// Method: setIsActive
	// Sets the isActive field of the test case object
	public void setIsActive (boolean tcActive) {
		isActive = tcActive;
	}
	
	// Method: getIsActive
	// Returns boolean value of the isActive field of the test case object
	public boolean getIsActive () {
		return this.isActive;
	}
	
	// Method: setDep
	// Sets the test case dependency
	public void setDep (String tcName) {
		testcaseDep = tcName;
	}
	
	// Method: getDep
	// Returns the string containing the test case name that is dependency for this test case
	public String getDep () {
		return this.testcaseDep;
	}
	
	// Method: checkDep
	// Verify the result of the test case dependency
	public boolean checkDep () {
		tcInstanceTools.LogMessage('i', "Checking dependencies for test case \"" + name + "\"");
		if (this.getDep().equalsIgnoreCase("none")) {
			tcInstanceTools.LogMessage('i', "No dependencies declared for test case \"" + name + "\"");
			return true;
		} else {
			if (this.getDepTestCaseResult(this.getDep()).equals(Result.Pass)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	// Method: getDepTestCaseResult
	// Returns the result of another test case
	public Result getDepTestCaseResult (String depTestCaseName) {
		try {
			FileReader fr = new FileReader (this.getResultsFile());
			BufferedReader br = new BufferedReader( fr );
			String line;
			String [] parsed;
			while ((line = br.readLine()) != null) {
				parsed = line.split("\\s+");
				tcInstanceTools.LogMessage('d', "parsed[0] = " + parsed[0] + " while parsed[1] = " + parsed[1]);
				if (parsed[0].equals(depTestCaseName)) {
					if (parsed[1].equals("Pass")) {
						return Result.Pass;
					} else if (parsed[1].equals("Fail")) {
						return Result.Fail;
					} else if (parsed[1].equals("Skip")) {
						return Result.Skip;
					}
				} else {
					continue;
				}
			}
			return Result.Error;
		}
		catch (IOException e) {
			tcInstanceTools.LogMessage('e', "Error while trying to read results file: " + e);
			return Result.Error;
		}
		
	}
	
	// Method: setTestCaseCommandLine
	// Sets the string containing the command line for the test case as defined in the topo file
	public void setTestCaseCommandLine (String tcExecCommandLine) {
		execCommandLine = tcExecCommandLine;
		tcInstanceTools.LogMessage('d', "The test case command line as defined in the topo file is: " + tcExecCommandLine);
	}
	
	// Method: getTestCaseCommandLine
	// Returns the string containing the command line for the test case as defined in the topo file
	public String getTestCaseCommandLine () {
		return this.execCommandLine;
	}
	
	// Method: setTestCaseScript
	// Sets the name of the executable script for the test case object
	public void setTestCaseScript (String tcExecScript) {
		//String line;
		//String [] parsed;
		//line = tcExecScript;
		//parsed = line.split("\\s+");
		//execScript = parsed[0];
		
		execScript = tcExecScript;
		tcInstanceTools.LogMessage('d', "Set the script for test case " + name + " to: " + execScript);
	}
	
	// Method: getTestCaseScript
	// Returns the name of the executable script for the test case object
	public String getTestCaseScript () {
		return this.execScript;
	}
	
	// Method: setExecArgs
	// Sets the arguments to be used when executing the test case script
	public void setExecArgs (String tcExecArgs) {
		execArguments = tcExecArgs;
		tcInstanceTools.LogMessage('d', "Set arguments for test case " + name + " to: " + execArguments);
	}
	
	// Method: getExecArgs
	// Returns the arguments to be used when executing the test case script
	public String getExecArgs () {
		return this.execArguments;
	}
	
	// Method: checkExecScript
	// Verifies that execScript exists and is executable
	public boolean checkExecScript () {
		if (Tools.CheckFile(execScript)) {
			tcInstanceTools.LogMessage('d', "Found test case script: " + execScript);
			if (Tools.CheckExecutable(execScript)) {
				tcInstanceTools.LogMessage('d', "Script " + execScript + " is executable");
				return true;
			} else {
				tcInstanceTools.LogMessage('e', "Script " + execScript + " is not executable");
				return false;
			}
		} else {
			tcInstanceTools.LogMessage('e', "Cannot find test case script: " + execScript);
			return false;
		}
	}
	
	// Method: runTest
	// Runs the script specified in the test case object
	public void runTest () {
		try {
			Process p = Runtime.getRuntime().exec(this.execCommandLine);
				
			InputStreamReader esr = new InputStreamReader(p.getErrorStream());
			BufferedReader ereader = new BufferedReader(esr);
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader ireader = new BufferedReader(isr);
			
			String line = null;
			String line1 = null;
			while ((line = ireader.readLine()) != null ) {
				//System.out.println("Output: " + line);
				System.out.println(line);
			}
			while ((line1 = ereader.readLine()) != null) {
				System.err.println("Error: " + line1);
			}
						
			int exitValue = p.waitFor();
			tcInstanceTools.LogMessage('d', "Test Case exit value: " + exitValue);
			if (exitValue == 0) {
				setTestCaseResult(Result.Pass);
			} else {
				setTestCaseResult(Result.Fail);
			}
		}
			
		catch (IOException ioe) {
			System.out.println("Error: " + ioe.getMessage());
		}
		
		catch (InterruptedException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	// Method: setTestCaseResult
	// Sets the test case result (possible values defined in the Result enum)
	public void setTestCaseResult (Result r) {
		this.testResult = r;
	}
	
	// Method: getTestCaseResult
	// Returns the test case result
	public String getTestCaseResult () {
		return this.testResult.toString();
	}
	
	// Method: setResultsFile
	// Sets the name (including path) of the results file
	public void setResultsFile (File resFile) {
		resultsFile = resFile;
	}
	
	// Method: getResultsFile
	// Returns the name of the results file
	public File getResultsFile () {
		return this.resultsFile;
	}
	
	// Method: setGlobalResultsFile
	// Sets the global results file
	public void setGlobalResultsFile (File globResultsFile) {
		globalResultsFile = globResultsFile;
	}
	
	// Method: getGlobalResultsFile
	// Returns the global results file
	public File getGlobalResultsFile () {
		return this.globalResultsFile;
	}
	
	// Method: checkIfFailCondition
	// Verifies If_Fail condition
	public void checkIfFailCondition () {
	}
	
	// Method: reportResults
	// In this case, writes the test case result to scenario results file and to global results file
	public void reportResults () throws Exception {
		tcInstanceTools.LogMessage('r', "Testcase \"" + this.getName() + "\" result is: " + this.getTestCaseResult());
		
		// Report results for current running scenario
		try {
			FileTools.WriteLineToFile(this.getResultsFile(), this.getName() + "    " + this.getTestCaseResult());
		}
		catch (IOException e) {
			throw new IOException("Error while trying to write local results file: ", e);
		}
		
		// Report global results
		try {
			FileTools.WriteLineToFile(this.getGlobalResultsFile(), this.getName() + "    " + this.getTestCaseResult());
		}
		catch (IOException e) {
			throw new IOException("Error while trying to write to global results file", e);
		}
	}
	
	// Method: reportGlobalResults
	// Writes the test case result only to the global results file
	public void reportGlobalResults () throws Exception {
		tcInstanceTools.LogMessage('r', "Testcase \"" + this.getName() + "\" result is: " + this.getTestCaseResult());
		
		try {
			FileTools.WriteLineToFile(this.getGlobalResultsFile(), this.getName() + "    " + this.getTestCaseResult());
		}
		catch (IOException e) {
			throw new IOException("Error while trying to write to global results file", e);
		}
	}
}
