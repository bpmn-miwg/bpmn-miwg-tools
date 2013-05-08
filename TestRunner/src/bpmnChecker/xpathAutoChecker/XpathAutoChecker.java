package bpmnChecker.xpathAutoChecker;

import org.w3c.dom.Node;

import bpmnChecker.tests.AbstractXpathTest;

public interface XpathAutoChecker {

	boolean isApplicable(Node n, String param) throws Throwable;
	
	void check(Node n, AbstractXpathTest test) throws Throwable;
	
}
