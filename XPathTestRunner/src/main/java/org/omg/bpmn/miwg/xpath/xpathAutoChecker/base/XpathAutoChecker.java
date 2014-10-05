package org.omg.bpmn.miwg.xpath.xpathAutoChecker.base;

import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.w3c.dom.Node;

public interface XpathAutoChecker {

	boolean isApplicable(Node n, String param) throws Throwable;

	void check(Node n, AbstractXpathCheck test) throws Throwable;

}
