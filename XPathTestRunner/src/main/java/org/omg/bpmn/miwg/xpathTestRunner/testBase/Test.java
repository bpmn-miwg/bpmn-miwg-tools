package org.omg.bpmn.miwg.xpathTestRunner.testBase;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;

public interface Test {

	void init(TestOutput out);

	boolean isApplicable(File file);

	void execute(TestInstance instance) throws Throwable;

	String getName();

	int resultsOK();

	int resultsFinding();

    List<? extends Output> execute(InputStream actualBpmnXml) throws Throwable;

}
