package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_2_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_3_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_4_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.A_4_1_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_1_0_Test;
import org.omg.bpmn.miwg.xpathTestRunner.tests.B_2_0_Test;
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
		out.println("Applicable Tests for " + file + ":");
		for (Test test : registeredTests) {
			if (test.isApplicable(file)) {
				out.println(" - " + test.getName());
			}
		}
	}

	public void executeTests(TestInstance instance, String outputFolder) throws IOException {

		TestOutput out = new TestOutput(instance, outputFolder);

		out.println(String.format("Running tests for %s:", instance.getFile()
				.getPath()));

		for (Test test : registeredTests) {
			if (test.isApplicable(instance.getFile())) {
				out.println("> TEST " + test.getName());
				try {
					test.init(out);
					test.execute(instance);
				} catch (Throwable e) {
					out.println("Exception during test execution of "
							+ test.getName());
					e.printStackTrace();
				}
				out.println();
				out.println("  TEST " + test.getName() + " results:");
				out.println("  * OK      : " + test.resultsOK());
				out.println("  * FINDINGS: " + test.resultsFinding());
				out.println();
			}
		}

		out.println(">> TEST RESULTS TOTAL:");
		out.println("  * OK      : " + instance.getOKs());
		out.println("  * FINDINGS: " + instance.getFindings());
		out.println();
		out.println();
		
		out.close();

	}

}
