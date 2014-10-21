package org.omg.bpmn.miwg.xsd.checks;

import org.omg.bpmn.miwg.common.CheckOutput;
import org.omg.bpmn.miwg.xpath.base.testEntries.InfoEntry;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ValidationErrorHandler extends DefaultHandler {
	private String errMessage = "";
	public int numWarning = 0;
	public int numError = 0;
	public int numFatalError = 0;

	private CheckOutput out;

	public void setTestOutput(CheckOutput out) {
		this.out = out;
	}

	public void warning(SAXParseException e) {
		numWarning++;
		out.println(new InfoEntry("Warning Line " + e.getLineNumber() + ": " + e.getMessage()
				+ "\n"));
	}

	public void error(SAXParseException e) {
		numError++;
		errMessage = new String("Error Line " + e.getLineNumber() + ": "
				+ e.getMessage() + "\n");
		out.println(new InfoEntry(errMessage));
	}

	public void fatalError(SAXParseException e) {
		numFatalError++;
		errMessage = new String("Error Line " + e.getLineNumber() + ": "
				+ e.getMessage() + "\n");
		out.println(new InfoEntry(errMessage));
	}

	public boolean valid() {
		return (numError == 0) && (numFatalError == 0);
	}

}
