package org.omg.bpmn.miwg.api;

import java.io.IOException;
import java.io.InputStream;

import org.omg.bpmn.miwg.api.input.AnalysisInput;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;

public class AnalysisJob {

	private String fullApplicationName; // e.g., "camunda Modeler 2.4.0",
										// "Reference")
	private String miwgTestCase; // e.g., "A.1.0"
	private Variant variant;
	private AnalysisInput actualInput, referenceInput;
	private String reportFolder = null;

	private boolean enableXpath = true;
	private boolean enableXmlCompare = true;
	private boolean enableSchema = true;

	public AnalysisJob(String FullApplicationName, String testCase,
			Variant variant, AnalysisInput actualInput,
			AnalysisInput referenceInput) {

		this.fullApplicationName = FullApplicationName;
		this.miwgTestCase = testCase;
		this.variant = variant;
		this.actualInput = actualInput;
		this.referenceInput = referenceInput;
		this.reportFolder = Consts.REPORT_FOLDER;
	}

	public AnalysisJob(String FullApplicationName, String testCase,
			Variant variant, AnalysisInput actualInput)
			throws ReferenceNotFoundException {
		this(FullApplicationName, testCase, variant, actualInput,
				null);
	}

	public void init() throws ReferenceNotFoundException {
		referenceInput = inferReferenceInput(miwgTestCase);
	}

	private static AnalysisInput inferReferenceInput(String miwgTestCase)
			throws ReferenceNotFoundException {
		String resourceName = "/Reference/" + miwgTestCase + ".bpmn";
		AnalysisInput referenceInput = new ResourceAnalysisInput(
				AnalysisJob.class, resourceName);
		try {
			InputStream is = referenceInput.getInputStream();
			if (is == null)
				throw new ReferenceNotFoundException(resourceName);
		} catch (IOException e) {
			throw new RuntimeException("Cannot find reference resource "
					+ resourceName, e);
		}
		return referenceInput;
	}

	public void disableXpath() {
		enableXpath = false;
	}

	public void disableXmlCompare() {
		enableXmlCompare = false;
	}

	public void disableSchema() {
		enableSchema = false;
	}
	
	public void disableReportWriting() {
		reportFolder= null;
	}

	public void setXpathOnly() {
		enableXpath = true;
		enableXmlCompare = false;
		enableSchema = false;
	}

	public void setXmlCompareOnly() {
		enableXpath = false;
		enableXmlCompare = true;
		enableSchema = false;
	}

	public void setSchemaOnly() {
		enableXpath = false;
		enableXmlCompare = false;
		enableSchema = true;
	}

	public String getName() {
		if (variant == Variant.Reference || variant == null)
			return fullApplicationName + "-" + miwgTestCase;
		else
			return fullApplicationName + "-" + miwgTestCase + "-"
					+ variant.toString().toLowerCase();
	}

	public String getFullApplicationName() {
		return fullApplicationName;
	}

	public String getMIWGTestCase() {
		return miwgTestCase;
	}

	public Variant getVariant() {
		return variant;
	}

	public AnalysisInput getActualInput() {
		return actualInput;
	}

	public boolean hasReference() {
		return referenceInput != null;
	}

	public AnalysisInput getReferenceInput() {
		return referenceInput;
	}

	public boolean isEnableXpath() {
		return enableXpath;
	}

	public boolean isEnableXmlCompare() {
		return enableXmlCompare;
	}

	public boolean isEnableSchema() {
		return enableSchema;
	}

	public String getReportFolder() {
		return reportFolder;
	}

	public void overrideReportFolder(String outputFolder) {
		this.reportFolder = outputFolder;
	}

}
