package com.testinmotion.twrap;

public class InstanceTools {
	
	// Instance variables
	private boolean debugFlag = false;
	
	// Instance methods
	
	// Method: setDebugFlag
	// Sets the value of boolean debugFlag
	public void setDebugFlag (boolean flag) {
		this.debugFlag = flag;
	}
	
	// Method: getDebugFlag
	// Returns the value of boolean debugFlag
	public boolean getDebugFlag () {
		return this.debugFlag;
	}
	
	// Method: LogMessage
	// Logs a given message with the indicated message level
	// Levels: INFO, WARN, ERROR, DIAG, SCENARIO, TCSTART, RESULT
	public void LogMessage (char logLevel, String logMsg) {
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
			if (this.getDebugFlag()) {
				msgString = "==> TWRAP-DIAG:      ";
				break;
			} else {
				return;
			}
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
}
