package com.testinmotion.twrap;

import java.io.*;
import java.util.*;
import org.xml.sax.InputSource;
import javax.xml.xpath.*;

public class Twrap {

	// Main
	public static void main(String[] arguments) throws Exception {
		
		// Initialize local flags
		boolean scenFlag = false;
		boolean topoFlag = false;
		boolean diagFlag = false;
		boolean buildFlag = false;
		boolean reportFlag = false;
		boolean resultsFlag = false;
		boolean mailFlag = false;
		boolean workDirFlag = false;
		boolean runScriptOnly = false;
		boolean scriptArgsFlag = false;
		
		// Initialize local variables
		String scenFile = null;
		ArrayList<String> scenFileList = new ArrayList<String>();
		String topoFile = null;
		String buildNumber = null;
		String resultsFile = null;
		String mailToList = null;
		String workDirName = null;
		String workDirNameUser = null;
		String comment = null;
		String runScript = null;
		String scriptArgs = null;
		
		File homeDir = new File(System.getProperty("user.home"));	
		String timeStamp = Tools.GetTimeStamp();
		
		// Initialize the InstanceTools class
		InstanceTools instanceTools = new InstanceTools();
		
		// Verify arguments
		if (arguments.length < 1) {
			Tools.LogMessage('e', "Calling twrap without input arguments");
			Tools.Usage();
		} else {
			int argIndex = 0;
			while (argIndex < arguments.length) {
				String currentArg = arguments[argIndex];
				instanceTools.LogMessage('d', "====" + currentArg + "====");
				
				if (currentArg.equals("-scen") == true) {
					scenFile = arguments[++argIndex];
					scenFlag = true;
					scenFileList.add(scenFile);
					argIndex++;
				} else if (currentArg.equals("-topo") == true) {
					topoFile = arguments[++argIndex];
					topoFlag = true;
					argIndex++;
				} else if (currentArg.equals("-build")) {
					buildNumber = arguments[++argIndex];
					buildFlag = true;
					argIndex++;
				} else if (currentArg.equals("-diag")) {
					diagFlag = true;
					Tools.LogMessage('i', "Diag flag set");
					instanceTools.setDebugFlag(diagFlag);
					argIndex++;
				} else if (currentArg.equals("-report") == true) {
					reportFlag = true;
					argIndex++;
				} else if (currentArg.equals("-results") == true) {
					resultsFile = arguments[++argIndex];
					resultsFlag = true;
					argIndex++;
				} else if (currentArg.equals("-workDir")) {
					workDirNameUser = arguments[++argIndex];
					workDirFlag = true;
					argIndex++;
				} else if (currentArg.equals("-comment")) {
					comment = arguments[++argIndex];
					argIndex++;
				} else if (currentArg.equals("-script")) {
					runScript = arguments[++argIndex];
					runScriptOnly = true;
					argIndex++;
				} else if (currentArg.equals("-args")) {
					scriptArgs = arguments[++argIndex];
					scriptArgsFlag = true;
					argIndex++;
				} else if (currentArg.equals("-mail") == true) {
					mailToList = arguments[++argIndex];
					mailFlag = true;
					argIndex++;
				} else {
					instanceTools.LogMessage('e', "Unknown flag or argument: " + currentArg);
					Tools.Usage();
				}
			}
		}
					
		// Print time stamp
		instanceTools.LogMessage('d', "Timestamp: " + timeStamp);
		
		// Verify if the topology file is specified.
		// If it is, verify that the file exists.
		if (topoFlag == false) {
			instanceTools.LogMessage('e', "No topology file specified");
			Tools.Usage();
		} else {
			instanceTools.LogMessage('d', "Topology file specified: " + topoFile);
			if (Tools.CheckFile(topoFile)) {
				instanceTools.LogMessage('d', "Topology file found: " + topoFile);
			} else {
				instanceTools.LogMessage('e', "Cannot find topology file: " + topoFile);
					System.exit(1);
			}
		}
			
		// Verify if user wants to set the working directory
		if (workDirFlag == true) {
			instanceTools.LogMessage('i', "Set work directory to " + workDirNameUser + "/" + timeStamp);
			
			// Check if directory name exists
			if (Tools.CheckFile(workDirNameUser)) {
				instanceTools.LogMessage('d', "Found: " + workDirNameUser);
				
				// Check if directory
				if (Tools.CheckIfDirectory(workDirNameUser)) {
					instanceTools.LogMessage('d', "Verified that " + workDirNameUser + " is directory");
				} else {
					instanceTools.LogMessage('e', "File " + workDirNameUser + " is not a directory");
					instanceTools.LogMessage('e', "Parameter for -workdir has to be directory");
					Tools.Usage();
				}
			}
			workDirName = workDirNameUser + "/" + timeStamp;
		} else {
			// Otherwise just pick the default directory
			workDirName = homeDir + "/twrap/" + timeStamp;
			instanceTools.LogMessage('i', "Set work directory to default: " + workDirName);
		}
		
		// Create working directory (default or user specified)
		instanceTools.LogMessage('i', "Working directory doesn't exist, creating " + workDirName);
		if (!Tools.CreateDirectory(workDirName)) {
			instanceTools.LogMessage('e', "Could not create working directory" + workDirName);
			System.exit(1);
		}
		
		File workDir = new File(workDirName);
		
		// Create log file
		LogStream ls;
		boolean foundErr = false;
		try {
			ls = Tools.CreateLogFile(workDir, "/logfile");
		}
		catch (IOException e) {
			instanceTools.LogMessage('e', "Error while creating log file: " + e);
			foundErr = true;
			return;
		}
		finally {
			if (foundErr == true) {
				instanceTools.LogMessage('e', "TWRAP exits with error");
				System.exit(1);
			}
		}
		instanceTools.LogMessage('i', "Logfile created: " + workDir + "/logfile");
		
		// Check if resultsFlag is set
		File globResultsFile;
		foundErr = false;
		
		if (resultsFlag == true) {
			instanceTools.LogMessage('i', "Global results file flag set");
		} else {
			instanceTools.LogMessage('d', "Global results file will be created in the working directory");
			resultsFile = workDir + "/results";
		}
		try {
			globResultsFile = Tools.CreateGlobalResultsFile(resultsFile);
		}
		catch (IOException e) {
			instanceTools.LogMessage('e', "Error while creating results file: " + e);
			foundErr = true;
			return;
		}
		finally {
			if (foundErr == true) {
				instanceTools.LogMessage('e', "TWRAP exits with error");
				System.exit(1);
			}
		}
		instanceTools.LogMessage('d', "Global results file created: " + resultsFile);
		
		// Check if buildFlag is set, if yes proceed to check the given build number
		if (buildFlag == true) {
			if (buildNumber.equals("latest")) {
				buildNumber = Tools.GetLatestBuild();
			} else {
				if (!Tools.CheckBuildNumberValid(buildNumber)) {
					instanceTools.LogMessage('e', "Invalid build number: " + buildNumber);
					Tools.Usage();
				}
			}
			instanceTools.LogMessage('i', "Set build number to " + buildNumber);
		} else {
			instanceTools.LogMessage('w', "No build number specified, proceeding without one.");
		}
		
		//	Reallocate the internal array for scenFileList, since at this point it's not going to change
		scenFileList.trimToSize();		
		// Convert the ArrayList to an array of type String
		String[] scenFileArray = (String[]) scenFileList.toArray(new String[0]);
		
		// Test logging for stderr channel
		if (diagFlag) {
			System.err.println("==> TWRAP-DIAG:      Testing correct order for stderr logging");
		}
			
		// Verify if the scenario file is specified.
		// If it is, go through the list and verify that the file exists.
		// If it doesn't exist, skip to next file in the list.
		if (runScriptOnly == true) {
			instanceTools.LogMessage('i', "Run test script only");
			
			// New TestCase object
			TestCase tc = new TestCase();
			
			// Initialize the TestCase object name with the name of the script
			tc.setName(runScript);
			
			// Initialize the test case global results file field, so we can report results
			tc.setGlobalResultsFile(globResultsFile);
			
			// Initialize the TestCase object execCommand field
			tc.setTestCaseCommandLine(runScript);
			
			// If scriptArgsFlag set, initialize test case arguments to be used when calling script
			if (scriptArgsFlag) {
				tc.setExecArgs(scriptArgs);
			}
			
			// Verify that the TestCase execCommand exists and is executable
			if (!tc.checkExecScript()) {
				// TestCase execCommand doesn't exist, or is not executable
				tc.setTestCaseResult(TestCase.Result.Fail);
				
				// Report results anyway
				try {
					tc.reportGlobalResults();
				}
				catch (IOException e) {
					e.printStackTrace();
					instanceTools.LogMessage('e', "TWRAP exits with error");
					System.exit(1);
				}
			} else {
				// Execute the test case
				tc.runTest();
				
				// Report test case results
				try {
					tc.reportGlobalResults();
				}
				catch (IOException e) {
					e.printStackTrace();
					instanceTools.LogMessage('e', "TWRAP exits with error");
					System.exit(1);
				}
			}
			
		} else if (scenFlag == false) {
			instanceTools.LogMessage('e',"No scenario file specified");
			Tools.Usage();
		} else {
			
			// ##########################
			// ### Scenario list loop ###
			// ##########################
			for ( int scenCounter = 0; scenCounter < scenFileArray.length; scenCounter++) {
				
				// New ScenarioFile object
				ScenarioFile sf = new ScenarioFile();
				
				 // Get scenario file name from list and initialize the ScenarioFile object name
				sf.setAbsoluteName(scenFileArray[scenCounter]);
				instanceTools.LogMessage('d', "Scenario file specified: " + sf.getAbsoluteName());
				
				// Check if scenario file exists
				if (Tools.CheckFile(sf.getAbsoluteName())) {
					// Initialize the ScenarioFile fileExists field
					sf.setFileExists(true);
					instanceTools.LogMessage('d', "Scenario file found: " + sf.getAbsoluteName());
					
					// Save the name of the scenario file
					sf.setFileName(Tools.GetFileName(sf.getAbsoluteName()));
					instanceTools.LogMessage('s', "Now running scenario file " + sf.getFileName());
					
					// Create scenario-dependent directory
					File newScenFileDir = new File(workDir + "/" + sf.getFileName());
					if (!newScenFileDir.exists()) {
						instanceTools.LogMessage('i', "Creating scenario specific directory " + newScenFileDir);
						newScenFileDir.mkdirs();
						
						// Initialize ScenarioFile scenarioDir field
						sf.setScenarioDir(newScenFileDir);
					}
					
					// Copy scenario file in workDir/scenarioFile
					Tools.CopyFile(sf.getAbsoluteName(), sf.getScenarioDir().toString() + "/");
					
					// Parse scenario file
					XPath xpath = XPathFactory.newInstance().newXPath();
					InputSource source = new InputSource(sf.getAbsoluteName());
					
					// Initialize list of test cases
					ArrayList<?> testcaseList = XmlTools.GetTestCaseNames(xpath, source);
					// Reallocate the internal array for testcaseList
					testcaseList.trimToSize();
					// Convert the ArrayList to an array of type String
					String[] testcaseArray = (String[]) testcaseList.toArray(new String[0]);
					
					// Initialize the ScenarioFile status field
					sf.setScenarioStatus("executing");
					
					// Create results file for this scenario
					File rf;
					foundErr = false;
					try {
						rf = sf.createResultsFile();
					}
					catch (IOException e) {
						e.printStackTrace();
						foundErr = true;
						return;
					}
					finally {
						if (foundErr == true) {
							instanceTools.LogMessage('e', "TWRAP exits with error");
							System.exit(1);
						}
					}
					
					
					// Update global results file with the name of this scenario
					try {
						sf.initGlobalResultsFile(globResultsFile);
					}
					catch (IOException e) {
						e.printStackTrace();
						instanceTools.LogMessage('e', "TWRAP exits with error");
						System.exit(1);
					}
					
					// Initialize test case loop error flag to track test case errors
					boolean tcLoopErrorFlag = false;
					// Initialize IF_FAIL target test case index
					int tcIfFailIndex = -1;
					
					// ######################
					// ### Test Case Loop ###
					// ######################
					for ( int testcaseCounter = 0; testcaseCounter < testcaseArray.length; testcaseCounter++) {
						
						// Debug
						instanceTools.LogMessage('d', "Test case loop index: " + testcaseCounter);
						
						// New TestCase object
						TestCase tc = new TestCase();
						
						// Initialize instance tools for the new TestCase object
						tc.setInstanceTools(instanceTools);
						
						// Get test case name from list and initialize the TestCase object name
						tc.setName(testcaseArray[testcaseCounter]);
						
						// Initialize the test case results file field, so we can report results
						// for both scenario results and global results
						tc.setResultsFile(rf);
						tc.setGlobalResultsFile(globResultsFile);
						
						// Figure out if we're already in an error situation, check tcLoopErrorFlag
						if (tcLoopErrorFlag) {
							// We are in an error situation, now we need to figure out if we're the IF_FAIL test case
							if (testcaseCounter != tcIfFailIndex) {
								// We are NOT the IF_FAIL test case, skipping
								instanceTools.LogMessage('d', "Loop error flag set, skipping this test case");
								tc.setTestCaseResult(TestCase.Result.Skip);
								
								// Report results
								try {
									tc.reportResults();
								}
								catch (IOException e) {
									e.printStackTrace();
									instanceTools.LogMessage('e', "TWRAP exits with error");
									System.exit(1);
								}
								
								// Keep going to the next test case
								continue;
							}
							// Otherwise, we ARE the IF_FAIL test case, so we keep going to execute this one
						}
						
						// Figure out if this test case is active or not
						tc.setIsActive(XmlTools.TestCaseIsActive(xpath, source, tc.getName()));
						if (tc.getIsActive()) {
							instanceTools.LogMessage('t', "Now running test case " + tc.getName());
						} else {
							instanceTools.LogMessage('i', "Test case " + tc.getName() + " is inactive, skipping...");
							tc.setTestCaseResult(TestCase.Result.Skip);
							
							// Report results
							try {
								tc.reportResults();
							}
							catch (IOException e) {
								e.printStackTrace();
								instanceTools.LogMessage('e', "TWRAP exits with error");
								System.exit(1);
							}
							
							// Keep going
							continue;
						}
						
						// Check test case dependencies
						tc.setDep(XmlTools.GetTestCaseDependencies(xpath, source, tc.getName()));
						if (tc.checkDep()) {
							instanceTools.LogMessage('d', "Dependencies check for test case " + tc.getName() + " completed");
						} else {
							instanceTools.LogMessage('w', "Dependencies check for test case " + tc.getName() + " failed");
							instanceTools.LogMessage('i', "Test case " + tc.getName() + " will be skipped...");
							tc.setTestCaseResult(TestCase.Result.Skip);
							
							// Report results
							try {
								tc.reportResults();
							}
							catch (IOException e) {
								e.printStackTrace();
								instanceTools.LogMessage('e', "TWRAP exits with error");
								System.exit(1);
							}
							
							// Keep going
							continue;
						}		
						
						// Initialize the TestCase object execCommandLine field
						tc.setTestCaseCommandLine(XmlTools.GetTestCaseExecCmd(xpath, source, tc.getName()));
						
						// Initialize the TestCase object execScript field
						tc.setTestCaseScript(tc.getTestCaseCommandLine().split("\\s+")[0]);
						
						// Verify that the TestCase execScript exists and is executable
						if (!tc.checkExecScript()) {
							// TestCase execScript doesn't exist or is not executable, skip to next test case
							tc.setTestCaseResult(TestCase.Result.Fail);
							
							// Report results anyway
							try {
								tc.reportResults();
							}
							catch (IOException e) {
								e.printStackTrace();
								instanceTools.LogMessage('e', "TWRAP exits with error");
								System.exit(1);
							}
							
							// Verify If_Fail condition
							if (tc.getTestCaseResult().equals(TestCase.Result.Fail.toString())) {
								String tcIfFail = XmlTools.GetTestCaseIfFailCmd(xpath, source, tc.getName());
								if (tcIfFail.equals("")) {
									instanceTools.LogMessage('i', "Test case failed, but no If_Fail action specified, continue");
								} else {
									int newCounter;
									for (newCounter = 0; newCounter < testcaseArray.length; newCounter++) {
										if (testcaseArray[newCounter].equals(tcIfFail)) {
											tcIfFailIndex = newCounter;
											tcLoopErrorFlag = true;
											break;
										}
									}
									if (newCounter == testcaseArray.length) {
										instanceTools.LogMessage('w', "Couldn't find test case specified as If_Fail action: " + tcIfFail);
										instanceTools.LogMessage('w', "Continuing with the next test case in scenario file");
									}
								}
							}
							
							// Keep going
							continue;
						}
						
						// Initialize the TestCase object execCommand field
						tc.setTestCaseCommandLine(tc.getTestCaseCommandLine() + " -tc " + tc.getName() + " -topo " + topoFile + " -scendir " + newScenFileDir);
						
						// Execute the test case
						tc.runTest();
						
						// Report test case results
						try {
							tc.reportResults();
						}
						catch (IOException e) {
							e.printStackTrace();
							instanceTools.LogMessage('e', "TWRAP exits with error");
							System.exit(1);
						}
						
						// Verify If_Fail condition
						if (tc.getTestCaseResult().equals(TestCase.Result.Fail.toString())) {
							String tcIfFail = XmlTools.GetTestCaseIfFailCmd(xpath, source, tc.getName());
							if (tcIfFail.equals("")) {
								instanceTools.LogMessage('i', "Test case failed, but no If_Fail action specified, continue");
							} else {
								int newCounter;
								for (newCounter = 0; newCounter < testcaseArray.length; newCounter++) {
									if (testcaseArray[newCounter].equals(tcIfFail)) {
										tcIfFailIndex = newCounter;
										tcLoopErrorFlag = true;
										break;
									}
								}
								if (newCounter == testcaseArray.length) {
									instanceTools.LogMessage('w', "Couldn't find test case specified as If_Fail action: " + tcIfFail);
									instanceTools.LogMessage('w', "Continuing with the next test case in scenario file");
								}
							}
						}
					}
					// ##########################
					// ### End Test Case Loop ###
					// ##########################
					
					// Update ScenarioFile status field
					sf.setScenarioStatus("executed");
					
				} else {
					// Scenario file not found, initialize the ScenarioFile fileExists field
					sf.setFileExists(false);
					instanceTools.LogMessage('w', "Cannot find scenario file: " + sf.getAbsoluteName());
					
					// Skip to next file in the list, initialize the ScenarioFile status field
					sf.setScenarioStatus("skipped");
					instanceTools.LogMessage('w', "Skipping the execution of scenario file " + sf.getAbsoluteName());
				}
			}
			// ##############################
			// ### End scenario list loop ###
			// ##############################
		}
		
		// Print end point banner in global results file
		try {
			Tools.EndGlobalResultsFile(globResultsFile);
		}
		catch (IOException e) {
			e.printStackTrace();
			instanceTools.LogMessage('e', "TWRAP exits with error");
			System.exit(1);
		}
		
		// Check reportFlag and print results if true
		if (reportFlag) {
			// Just read the content of the global results file and print it on screen
			try {
				FileTools.ListFile(globResultsFile);
			}
			catch (IOException e) {
				e.printStackTrace();
				instanceTools.LogMessage('e', "TWRAP exits with error");
				System.exit(1);
			}
		}
		
		// Check mailFlag and send email with results if true
		if (mailFlag) {
			instanceTools.LogMessage('i', "Email flag set, sending email with results");
			Tools.SendResultsEmail(mailToList, comment);
		}
		
		// Print again the working directory
		instanceTools.LogMessage('i', "Working directory for this run is: " + workDir);
		
		// Print again the path to log file
		instanceTools.LogMessage('i', "Logfile at: " + workDir + "/logfile\n");
		
		// Flush and close logfile stream
		instanceTools.LogMessage('d', "Closing logfile stream\n");
		Tools.CloseLogFile(ls);
		
	}
	// Close main bracket

}
// Close class bracket
