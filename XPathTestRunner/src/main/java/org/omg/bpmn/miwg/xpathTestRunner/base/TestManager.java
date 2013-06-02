package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.Test;

public class TestManager {

	private List<Test> registeredTests = new ArrayList<Test>();

	private String application = null;

	public String getApplication() {
		return application;
	}

	public TestManager() {
	}

	public void registerTest(Test test) {
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

	public void executeTests(File file, TestOutput out) throws Throwable {
		out.println("Running Tests for " + file + ":");
		int numOK = 0;
		int numIssue = 0;
		for (Test test : registeredTests) {
			if (test.isApplicable(file)) {
				out.println("> TEST " + test.getName());
				try {
					test.init(out);
					test.execute(file);
				} catch (Exception e) {
					out.println("Exception during test execution of "
							+ test.getName());
					e.printStackTrace();
				}
				out.println();
				out.println("  TEST " + test.getName() + " results:");
				out.println("  * OK    : " + test.ResultsOK());
				out.println("  * ISSUES: " + test.ResultsIssue());
				out.println();

				numOK += test.ResultsOK();
				numIssue += test.ResultsIssue();
			}
		}

		out.println(">> TEST RESULTS TOTAL:");
		out.println("  * OK    : " + numOK);
		out.println("  * ISSUES: " + numIssue);
	}

}
