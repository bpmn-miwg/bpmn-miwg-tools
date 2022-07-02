/**
 * The MIT License (MIT)
 * Copyright (c) 2013 Signavio, OMG BPMN Model Interchange Working Group
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

package org.omg.bpmn.miwg.xmlCompare.util.xml;

import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.custommonkey.xmlunit.NodeDetail;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XPathUtil {

	private static final XPathFactory factory = XPathFactory.newInstance();

	/**
	 * Creates a new XPath object, without namespace context or other configuration.
	 * 
	 * @return a new XPath object, using the default XPathFactory.
	 */
	public static XPath getXPath() {
		return factory.newXPath();
	}

	/**
	 * Returns an XPath instance as returned by {@link #getXPath()}, additionally with correct prefix handling for all
	 * standard namespaces used in Signavio projects (as defined {@link XmlNamespace}). These prefixes can be used in
	 * xpath queries. <br>
	 * <br>
	 * 
	 * Usage example: <br>
	 * <blockquote> XPath xpath = XPathUtil.getXPathForXsl();<br>
	 * <br>
	 * String q = "//fo:inline/descendant::fo:block";<br>
	 * XPathExpression expr = xpath.compile(q);<br>
	 * <br>
	 * NodeList attributeBlocks = (NodeList)expr.evaluate(<br>
	 * doc.getFirstChild(), // Is necessary! There are faulty results if querying the document. <br>
	 * XPathConstants.NODESET);<br>
	 * <br>
	 * for(int i = 0; i < attributeBlocks.getLength(); i++){<br>
	 * Node node = attributeBlocks.item(i);<br>
	 * // process node...<br>
	 * }<br>
	 * </blockquote>
	 * 
	 * @return
	 */
	public static XPath getXPathSignavio() {
		XPath xpath = getXPath();
		xpath.setNamespaceContext(new SignavioNamespaceContext());
		xpath.setXPathFunctionResolver(new SignavioXPathFunctionResolver());
		return xpath;
	}

	/**
	 * Returns result of xpath query on document's first child node. See {@link #getQueryResult(Node, String)}.
	 * 
	 * @throws XPathExpressionException
	 */
	public static NodeList getQueryResult(Document document, String query) throws XPathExpressionException {
		return getQueryResult(document.getFirstChild(), query);
	}

	/**
	 * Returns result of xpath query on the node as NodeList. Uses xpath as returned by {@link #getXPathForXsl}.
	 * 
	 * @param node
	 * @param query
	 * @throws XPathExpressionException
	 */
	public static NodeList getQueryResult(Node node, String query) throws XPathExpressionException {
		XPath xpath = getXPathSignavio();
		XPathExpression expr = xpath.compile(query);
		return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
	}

	/**
	 * Splits an XPath string into the part referencing a node and another part referencing an attribute of that node.
	 * <p/>
	 * Examples:<br/>
	 * "/svg/@width" -> "/svg" & "/@width"<br/>
	 * "/svg/def/markers/marker[@id="bla"]/@width" -> "/svg/def/markers/marker[@id="bla"]" & "/@width"
	 * 
	 * @param xpath an XPath string referencing an attribute (see examples)
	 * @return the xpath without a final reference to an attribute and the attribute reference
	 * @throws IllegalArgumentException if the given XPAth does not reference an attribute!
	 */
	public static Pair<String, String> splitXPathIntoNodeAndAttribute(String xpath) {
		String[] parts = xpath.split("/@");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Problem parsing XPath attribute name for XPath: " + xpath);
		}

		
		return Pair.of(parts[0], parts[1]);
	}

	/**
	 * Takes an XPath string referencing an attribute of an element and returns only the last part (referencing that
	 * attribute).
	 * <p/>
	 * Examples:<br/>
	 * "/svg/@width" -> "/@width"<br/>
	 * "/svg/def/markers/marker[@id="bla"]/@width" -> "/@width"
	 * 
	 * @param xpath an XPath string referencing an attribute (see examples)
	 * @return the attribute reference
	 * @throws IllegalArgumentException if the given XPAth does not reference an attribute!
	 */
	public static String getAttributeNameFromXPath(String xpath) {
		return splitXPathIntoNodeAndAttribute(xpath).getRight();
	}

	/**
	 * Builds an absolute XPath string from the contained node.
	 * 
	 * @param nodeDetail
	 * @param context namespace context used to determine correct namespace prefixes, see
	 *            {@link SignavioNamespaceContext}
	 * @return an XPath string or null (if no node could be found)
	 */
	public static String getXPathString(NodeDetail nodeDetail, NamespaceContext context) {
		if (nodeDetail != null && nodeDetail.getNode() != null)
			return getXPathString(nodeDetail.getNode(), context);
		else
			return null;
	}

	/**
	 * Builds an absolute XPath string from the given node.
	 * 
	 * @param node
	 * @param context namespace context used to determine correct namespace prefixes, see
	 *            {@link SignavioNamespaceContext}
	 * @return an XPath string or null (if no node was given)
	 */
	public static String getXPathString(Node node, NamespaceContext context) {
		if (node == null) {
			return null;
		}

		LinkedList<String> xpathSteps = new LinkedList<String>();
		String string = getNodeString(node, context);
		xpathSteps.add(string);
		Node current;
		if (node instanceof Attr)
			current = ((Attr) node).getOwnerElement();
		else
			current = node.getParentNode();

		while (current != node.getOwnerDocument()) {
			xpathSteps.add(getNodeString(current, context));
			current = current.getParentNode();
		}

		StringBuffer buff = new StringBuffer();
		Iterator<String> it = xpathSteps.descendingIterator();
		while (it.hasNext()) {
			buff.append("/" + it.next());
		}
		return buff.toString();
	}

	/**
	 * Builds an XPath string referencing the given node relative to it's parent.
	 * 
	 * @param node
	 * @param context namespace context used to determine correct namespace prefixes, see
	 *            {@link SignavioNamespaceContext}
	 * @return a relative XPath string or null (if no node given)
	 */
	private static String getNodeString(Node node, NamespaceContext context) {
		if (node == null) {
			return null;
		}

		// get qualified name
		String nodeName = node.getLocalName();
		if (nodeName == null)
			nodeName = node.getNodeName();

		if (node.getNamespaceURI() != null) {
			String prefix = context.getPrefix(node.getNamespaceURI());
			nodeName = prefix + ":" + node.getLocalName();
		}

		if (node instanceof Attr) {
			return "@" + nodeName;
		} else if (node instanceof Text) {
			nodeName = "text()";
		}

		// determine position
		Node current = node;
		while (current.getPreviousSibling() != null) {
			current = current.getPreviousSibling();
		}
		int position = 1;

		while (current != node) {
			if (current.getNodeName().equals(node.getNodeName()))
				position++;
			current = current.getNextSibling();
		}

		return nodeName + "[" + position + "]";
	}
}
