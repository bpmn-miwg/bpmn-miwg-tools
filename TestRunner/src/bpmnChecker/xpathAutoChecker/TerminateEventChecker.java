package bpmnChecker.xpathAutoChecker;

import org.w3c.dom.Node;

import bpmnChecker.tests.AbstractXpathTest;

public class TerminateEventChecker implements XpathAutoChecker {

	@Override
	public void check(Node n, AbstractXpathTest test) throws Throwable {
		test.navigateElement("Terminate Event Definition",
				"bpmn:terminateEventDefinition | bpmn:eventDefinitionRef");
	}

	@Override
	public boolean isApplicable(Node n, String param) {
		return param != null
				&& (param.equals("terminateEvent"));
	}

}
