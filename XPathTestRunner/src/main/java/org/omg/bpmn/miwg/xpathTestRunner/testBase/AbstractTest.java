package org.omg.bpmn.miwg.xpathTestRunner.testBase;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;
import org.omg.bpmn.miwg.xpathTestRunner.base.testEntries.*;

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

	protected void ok(AbstractTestEntry entry) {
		numOK++;
		out.println(entry);
	}
	
	protected void ok(String assertion, String message) {
		ok(new OKAssertionEntry(assertion, message, null));
	}

	protected void finding(AbstractTestEntry entry) {
		numIssues++;
		out.println(entry);
	}
	
	protected void finding(String assertion, String message) {
		finding(new FindingAssertionEntry(assertion, message, (TestContext)null));
	}

	protected void info(String message) {
		info(new InfoEntry(message));
	}
	
	protected void info(AbstractTestEntry entry) {
		out.println(entry);
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
