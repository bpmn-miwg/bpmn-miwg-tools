package bpmnChecker.xpathAutoChecker.Level1Descriptive;

import org.w3c.dom.Node;

import bpmnChecker.testBase.AbstractXpathTest;
import bpmnChecker.xpathAutoChecker.base.XpathAutoChecker;

public class L1MessageEventChecker implements XpathAutoChecker {

	@Override
	public void check(Node n, AbstractXpathTest test) throws Throwable {
		test.navigateElement("bpmn:messageEventDefinition | bpmn:messageEventDefinitionRef");
	}

	@Override
	public boolean isApplicable(Node n, String param) {
		return param != null && (param.equals("messageEvent"));
	}

}
