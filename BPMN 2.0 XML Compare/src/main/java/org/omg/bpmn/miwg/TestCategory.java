package org.omg.bpmn.miwg;

public enum TestCategory {
	A("A - Fixed Digrams with Variations of Attributes"), 
	B("B - Validate that tool covers conformance class set");

	private String folderName;

	TestCategory(String folderName) {
		this.folderName = folderName;
	}

	public String toString() {
		return this.folderName;
	}
}
