package xpathTestRunner.xpathAutoChecker.base;

import org.w3c.dom.Node;

import xpathTestRunner.testBase.AbstractXpathTest;


public interface XpathAutoChecker {

	boolean isApplicable(Node n, String param) throws Throwable;
	
	void check(Node n, AbstractXpathTest test) throws Throwable;
	
}
