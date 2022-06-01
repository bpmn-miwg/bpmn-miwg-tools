package org.omg.bpmn.miwg.schema;

import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.output.dom.finding.ValidationFindingEntry;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ValidationErrorHandler extends DefaultHandler {
	private String errMessage = "";
	public int numWarning = 0;
	public int numError = 0;
	public int numFatalError = 0;

	private AnalysisOutput out;

	public void setTestOutput(AnalysisOutput out) {
		this.out = out;
	}

	public void warning(SAXParseException e) {
		numWarning++;
		out.finding(new ValidationFindingEntry("Warning Line "
				+ e.getLineNumber() + ": " + e.getMessage() + "\n"));
	}

	public void error(SAXParseException e) {
		numError++;
		errMessage = new String("Error Line " + e.getLineNumber() + ": "
				+ e.getMessage() + "\n");
		out.finding(new ValidationFindingEntry(errMessage));
	}

	public void fatalError(SAXParseException e) {
		numFatalError++;
		errMessage = new String("Error Line " + e.getLineNumber() + ": "
				+ e.getMessage() + "\n");
		out.finding(new ValidationFindingEntry(errMessage));
	}

	public boolean valid() {
		return (numError == 0) && (numFatalError == 0);
	}

}
