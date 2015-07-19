package com.testinmotion.twrap;

import org.xml.sax.InputSource;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import java.util.*;

public class XmlTools {

	// Method: GetTestCaseNames
	// Parse XML scenario file and build a list of test cases
	public static ArrayList<String> GetTestCaseNames (XPath xpath, InputSource srcFile) throws Exception {
		ArrayList<String> tcList = new ArrayList<String>();
		String expression = "//TestCase/Name/text()";
		
		NodeList elements = (NodeList)xpath.evaluate(expression, srcFile, XPathConstants.NODESET);
		
		Tools.LogMessage('i', "Number of test cases in scenario file: " + elements.getLength());
		
		for( int i=0; i<elements.getLength(); i++ ) {
			Node tc =  elements.item(i);
			String v = tc.getNodeValue();
			tcList.add(v);
		}
		
		return tcList;
	}
	
	public static boolean TestCaseIsActive (XPath xpath, InputSource srcFile, String tcName) throws Exception {
		String expression = "//TestCase[Name=\"" + tcName + "\"]/Active";
		
		String isActive = xpath.evaluate(expression,srcFile);
		
		if (isActive.equals("yes")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String GetTestCaseExecCmd (XPath xpath, InputSource srcFile, String tcName) throws Exception {
		String expression = "//TestCase[Name=\"" + tcName + "\"]/Command";
		
		String execCmd = xpath.evaluate(expression, srcFile);
		
		return execCmd;
	}
	
	public static String GetTestCaseDependencies (XPath xpath, InputSource srcFile, String tcName) throws Exception {
		String expression = "//TestCase[Name=\"" + tcName + "\"]/Dependencies";
		
		String tcDep = xpath.evaluate(expression, srcFile);
		
		return tcDep;
	}
	
	public static String GetTestCaseIfFailCmd (XPath xpath, InputSource srcFile, String tcName) throws Exception {
		String expression = "//TestCase[Name=\"" + tcName + "\"]/If_Fail";
		
		String tcIfFail = xpath.evaluate(expression, srcFile);
		
		return tcIfFail;
	}
}
