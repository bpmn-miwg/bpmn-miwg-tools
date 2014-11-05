package org.omg.bpmn.miwg.api;


public class AnalysisJob {

	public String FullApplicationName; // e.g., “camunda Modeler 2.4.0”, “Reference”)
	public String MIWGTestCase; // e.g., “A.1.0”
	public MIWGVariant Variant;

	public String getName() {
		return FullApplicationName + "-" + MIWGTestCase + "-" + Variant;
	}
	
}
