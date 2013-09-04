package org.omg.bpmn.miwg.testresult;

import java.io.File;

/**
 * Contains the statistics from the run of the xpath tool.
 * 
 * @author mk
 *
 */
public class XPathTestRunResults {
	
	private File file;
	
	private int numOK = 0;
	
	private int numFindings = 0;

	public XPathTestRunResults(File file, int numOK, int numFindings) {
		this.file = file;
		this.numOK = numOK;
		this.numFindings = numFindings;
	}
	
	public int getNumOK() {
		return numOK;
	}

	public int getNumFindings() {
		return numFindings;
	}

	public File getFile() {
		return file;
	}



}
