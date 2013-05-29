package org.omg.bpmn.miwg.xpathTestRunner.testBase;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;


public interface Test {

	void init(TestOutput out);
	
	boolean isApplicable(File file);
	
	void execute(File file) throws Throwable;

	String getName();
	
	int ResultsOK();
	
	int ResultsIssue();

	
}
