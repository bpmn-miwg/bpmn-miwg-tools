package org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.Level2Analytic;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.base.XpathAutoChecker;
import org.w3c.dom.Node;



public class L2MessageFlowChecker implements XpathAutoChecker {

	@Override
	public void check(Node n, AbstractXpathTest test) throws Throwable {
		test.navigateElementByParam("//bpmn:message[@id='%s']", "./@messageRef");
	}

	@Override
	public boolean isApplicable(Node n, String param) {
		return param != null && (param.equals("messageFlowL2"));
	}

}
