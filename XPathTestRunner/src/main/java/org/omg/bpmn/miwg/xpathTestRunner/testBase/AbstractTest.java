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

	protected void finding(String assertion, String message) {
		numIssues++;
		printFinding(message, assertion);
	}

	protected void printOK(String assertion) {
		out.println("  > Assertion " + assertion + ": OK");
	}

	protected void printFinding(String message, String assertion) {
		out.println("  > Assertion " + assertion + ": FINDING: " + message);
	}

	protected void printInfo(String assertion) {
		out.println("  > Assertion " + assertion);
	}

	@Override
	public int resultsOK() {
		return numOK;
	}

	@Override
	public int resultsFinding() {
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
