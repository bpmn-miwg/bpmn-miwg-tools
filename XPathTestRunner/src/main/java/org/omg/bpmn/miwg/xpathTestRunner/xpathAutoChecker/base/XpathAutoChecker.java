package org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.base;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;
import org.w3c.dom.Node;

public interface XpathAutoChecker {

	boolean isApplicable(Node n, String param) throws Throwable;

	void check(Node n, AbstractXpathTest test) throws Throwable;

}
