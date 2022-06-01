package org.omg.bpmn.miwg.api;

public enum Variant {
	Import, Export, Roundtrip, Reference, Undefined;
}
/* "Reference" is used in jUnit tests for identifying regressions in our analysis tools */