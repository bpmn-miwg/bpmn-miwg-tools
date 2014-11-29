package org.omg.bpmn.miwg.api;

import org.omg.bpmn.miwg.api.input.AnalysisInput;

public class AnalysisJob {

	private String fullApplicationName; // e.g., “camunda Modeler 2.4.0”,
										// “Reference”)
	private String miwgTestCase; // e.g., “A.1.0”
	private MIWGVariant variant;
	private AnalysisInput actualInput, referenceInput;

	
	public AnalysisJob(String FullApplicationName, String testCase, MIWGVariant variant, AnalysisInput actualInput, AnalysisInput referenceInput) {
		this.fullApplicationName = FullApplicationName;
		this.miwgTestCase = testCase;
		this.variant = variant;
		this.actualInput = actualInput;
		this.referenceInput = referenceInput;		
	}
	
	public String getName() {
		return fullApplicationName + "-" + miwgTestCase + "-" + variant;
	}

	public String getFullApplicationName() {
		return fullApplicationName;
	}

	public String getMIWGTestCase() {
		return miwgTestCase;
	}

	public MIWGVariant getVariant() {
		return variant;
	}

	public AnalysisInput getActualInput() {
		return actualInput;
	}

	public AnalysisInput getReferenceInput() {
		return referenceInput;
	}
	
}
