package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.omg.bpmn.miwg.xpathTestRunner.base.testEntries.*;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_2_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_3_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_4_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_4_1_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_2_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.DemoTechnicalSupportTest;
import org.omg.bpmn.miwg.xpathTestRunner.tests.ValidatorTest;

public class TestManager {

	private List<Test> registeredTests = new ArrayList<Test>();

	private String application = null;

	public String getApplication() {
		return application;
	}

	public TestManager() {
		registerTest(new ValidatorTest());
		registerTest(new B_1_0_Test());
		registerTest(new B_2_0_Test());
		registerTest(new A_1_0_Test());
		registerTest(new A_2_0_Test());
		registerTest(new A_3_0_Test());
		registerTest(new A_4_0_Test());
		registerTest(new A_4_1_Test());
		registerTest(new DemoTechnicalSupportTest());
	}

	private void registerTest(Test test) {
		registeredTests.add(test);
	}

	public void limitApplication(String application) {
		this.application = application;
	}

	public boolean isAnyTestApplicable(File file) {
		for (Test test : registeredTests) {
			if (test.isApplicable(file))
				return true;
		}
		return false;
	}

	public void printApplicableTests(File file, TestOutput out) {
		out.println(new InfoEntry("Applicable Tests for " + file + ":"));
		out.push(new EmptyEntry());
		for (Test test : registeredTests) {
			if (test.isApplicable(file)) {
				out.println(new ListEntry(test.getName()));
			}
		}
		out.pop();
	}

	public void executeTests(TestInstance instance, String outputFolder)
			throws IOException {

		TestOutput out = new TestOutput(instance, outputFolder);

		out.println(new TestFileEntry(instance.getFile().getPath()));

		for (Test test : registeredTests) {
			if (test.isApplicable(instance.getFile())) {
				TestEntry entry = new TestEntry(test.getName());
				out.push(entry);
				try {
					test.init(out);
					test.execute(instance);
				} catch (Throwable e) {
					out.println(new ExceptionEntry(
							"Exception during test execution of "
									+ test.getName(), e));
					e.printStackTrace();
				}

				out.println();
				
				// Make sure the stack is not tainted by defective tests.
				out.popToTestEntry();

				out.push(new ResultsEntry(String.format("TEST %s results:",
						test.getName())));
				out.println(new ListKeyValueEntry("OK", Integer.toString(test
						.resultsOK())));
				out.println(new ListKeyValueEntry("FINDINGS", Integer
						.toString(test.resultsFinding())));
				out.pop();
				out.forcePop();
				out.println();
			}
		}

		out.push(new TotalResultsEntry());
		out.println(new ListKeyValueEntry("OK", Integer.toString(instance
				.getOKs())));
		out.println(new ListKeyValueEntry("FINDINGS ", Integer
				.toString(instance.getFindings())));
		out.println();
		out.pop();

	
		out.close();

	}

}
