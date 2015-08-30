/**
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
 *
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.omg.bpmn.miwg.xpath.pluggableAssertions;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.omg.bpmn.miwg.xpath.common.NameSpaceContexts;
import org.w3c.dom.Node;

public class AssertionUtil {

	private static XPath xpathTest;

	static {
		XPathFactory xpathfactory = XPathFactory.newInstance();
		xpathTest = xpathfactory.newXPath();
		xpathTest.setNamespaceContext(new NameSpaceContexts());
	}

	public static Node findNode(Node baseNode, String expr)
			throws XPathExpressionException {
		if (baseNode == null)
			throw new IllegalArgumentException("The node parameter is null");
		
		Object o = xpathTest.evaluate(expr, baseNode, XPathConstants.NODE);
		if (o == null)
			return null;
		else {
			Node n = (Node) o;
			return n;
		}
	}

	public static String getAttributeValue(Node element, String attributeName) {
		if (element == null)
			throw new IllegalArgumentException("The node parameter is null");

		if (element.getNodeType() != Node.ELEMENT_NODE)
			throw new IllegalArgumentException("The node parameter is not an element");

		Node attr = element.getAttributes().getNamedItem(attributeName);
		
		if (attr == null)
			return null;
		else 
			return attr.getTextContent();
	}
	
	public static String getTextContent(Node element) {
		if (element == null)
			throw new IllegalArgumentException("The node parameter is null");

		return element.getTextContent();
	}

}
