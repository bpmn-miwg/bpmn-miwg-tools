package xpathTestRunner.xpathAutoChecker.Level1Descriptive;

import org.w3c.dom.Node;

import xpathTestRunner.testBase.AbstractXpathTest;
import xpathTestRunner.xpathAutoChecker.base.XpathAutoChecker;


public class L1SignalEventChecker implements XpathAutoChecker {

	@Override
	public void check(Node n, AbstractXpathTest test) throws Throwable {
		test.navigateElement("bpmn:signalEventDefinition | bpmn:signalEventDefinitionRef");
	}

	@Override
	public boolean isApplicable(Node n, String param) {
		return param != null && (param.equals("signalEvent"));
	}

}
