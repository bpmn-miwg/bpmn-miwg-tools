package bpmnChecker.xpathAutoChecker.base;

import org.w3c.dom.Node;

import bpmnChecker.testBase.AbstractXpathTest;

public interface XpathAutoChecker {

	boolean isApplicable(Node n, String param) throws Throwable;
	
	void check(Node n, AbstractXpathTest test) throws Throwable;
	
}
