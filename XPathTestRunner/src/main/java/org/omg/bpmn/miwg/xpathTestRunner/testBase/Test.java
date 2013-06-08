package org.omg.bpmn.miwg.xpathTestRunner.testBase;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;

public interface Test {

	void init(TestOutput out);

	boolean isApplicable(File file);

	void execute(TestInstance instance) throws Throwable;

	String getName();

	int resultsOK();

	int resultsFinding();

}
