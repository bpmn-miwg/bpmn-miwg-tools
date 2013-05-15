package xpathTestRunner.testBase;

import xpathTestRunner.base.TestOutput;


public interface Test {

	void init(TestOutput out);
	
	boolean isApplicable(String fileName);
	
	void execute(String fileName) throws Throwable;

	String getName();
	
	int ResultsOK();
	
	int ResultsIssue();

	
}
