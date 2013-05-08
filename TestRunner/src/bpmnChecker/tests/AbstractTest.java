package bpmnChecker.tests;

import bpmnChecker.TestOutput;

public abstract class AbstractTest implements Test {

	private int numIssues = 0;
	private int numOK = 0;
	protected TestOutput out;

	protected void ok(String assertion) {
		numOK++;
		printOK(assertion);
	}

	protected void issue(String assertion, String message) {
		numIssues++;
		printIssue(assertion, message);
	}

	protected void printOK(String assertion) {
		out.println("  > Assertion " + assertion + ": OK");
	}

	protected void printIssue(String assertion, String message) {
		out.println("  > Assertion " + assertion + ": ISSUE: " + message);
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

}
