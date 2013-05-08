package bpmnChecker.xpathAutoChecker;

import org.w3c.dom.Node;

import bpmnChecker.tests.AbstractXpathTest;

public class MessageEventChecker implements XpathAutoChecker {

	@Override
	public void check(Node n, AbstractXpathTest test) throws Throwable {
		test.navigateElement("Message Event Definition",
				"bpmn:messageEventDefinition | bpmn:eventDefinitionRef");
	}

	@Override
	public boolean isApplicable(Node n, String param) {
		return param != null
				&& (param.equals("messageStartEvent") || param
						.equals("messageEndEvent"));
	}

}
