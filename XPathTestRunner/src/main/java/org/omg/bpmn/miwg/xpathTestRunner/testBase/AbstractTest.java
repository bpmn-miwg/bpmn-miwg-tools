package org.omg.bpmn.miwg.xpathTestRunner.testBase;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;

public abstract class AbstractTest implements Test {

	private int numIssues = 0;
	private int numOK = 0;
	protected TestOutput out;

	@Override
	public boolean isApplicable(File file) {
		String fn = file.getName();
		return fn.equals(getName() + "-roundtrip.bpmn")
				|| fn.equals(getName() + ".bpmn");
	}

	protected void ok(String assertion) {
		numOK++;
		printOK(assertion);
	}

	protected void info(String assertion) {
		printInfo(assertion);
	}

	protected void issue(String assertion, String message) {
		numIssues++;
		printIssue(message, assertion);
	}

	protected void printOK(String assertion) {
		out.println("  > Assertion " + assertion + ": OK");
	}

	protected void printIssue(String message, String assertion) {
		out.println("  > Assertion " + assertion + ": ISSUE: " + message);
	}

	protected void printInfo(String assertion) {
		out.println("  > Assertion " + assertion);
	}

	@Override
	public int ResultsOK() {
		return numOK;
	}

	@Override
	public int ResultsIssue() {
		return numIssues;
	}

	@Override
	public void init(TestOutput out) {
		numIssues = 0;
		numOK = 0;
		this.out = out;
	}

	protected void addIssues(int number) {
		numIssues += number;
	}

}
