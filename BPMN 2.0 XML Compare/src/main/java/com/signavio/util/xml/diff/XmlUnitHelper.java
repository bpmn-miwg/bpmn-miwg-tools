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

package com.signavio.util.xml.diff;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.Difference;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.signavio.util.xml.XPathUtil;
import com.signavio.util.xml.diff.AbstractXmlDifferenceListener.XmlDiffDocumentType;


/**
 * Helper class for DOM and XPath functions
 * @author philipp.maschke
 *
 */
public class XmlUnitHelper {

	private static final Logger LOGGER = Logger.getLogger(XmlUnitHelper.class);	
	
	private XPath xPath;
	private Document controlDoc;
	private Document testDoc;
	private ElementsPrefixMatcher prefixMatcher;
	
	public XmlUnitHelper(Document controlDoc, Document testDoc, ElementsPrefixMatcher prefixMatcher){
		xPath = XPathUtil.getXPathSignavio();
		this.controlDoc = controlDoc;
		this.testDoc = testDoc;
		this.prefixMatcher = prefixMatcher;
	}

	
	public String addNamespacePrefixToAttribute(String attrName, Node node) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs == null)
			return attrName;
		
		String ns = getNamespaceOfAttribute(attrName, attrs);
		if (ns != null){
			String prefix = xPath.getNamespaceContext().getPrefix(ns);
			attrName = (prefix.equals(""))? attrName: prefix + ":" + attrName;
		}

		return attrName;
	}


	public String getNamespaceOfAttribute(String attrName, NamedNodeMap attrs) {
		String ns = null;
		for (int i=0; i < attrs.getLength(); i++){
			String name = attrs.item(i).getLocalName();
			if (name != null && name.equals(attrName)){
				ns = attrs.item(i).getNamespaceURI();
				if (ns != null && !ns.trim().equals(""))
					break;
				else
					ns = null;
			}	
		}
		return ns;
	}
	
	
	/**
	 * Retrieves the DOM node which is referenced in the Difference object by generating an XPath query and executing it on the
	 * document of the given type (control or test).
	 * 
	 * @param difference
	 * @param type
	 * @param removeAttributeFromPath set to true if you do not want to get the attribute node when an attribute is affected 
	 * (the parent node will be returned instead) 
	 * @return
	 */
	public Node getDocumentNode(Difference difference, XmlDiffDocumentType type,
			boolean removeAttributeFromPath) {
		Node node = null;
		String validXpath; 
		Document doc;
		if (type == XmlDiffDocumentType.CONTROL){
			validXpath = XPathUtil.getXPathString(difference.getControlNodeDetail(), xPath.getNamespaceContext());
			doc = controlDoc;
		}else{
			validXpath = XPathUtil.getXPathString(difference.getTestNodeDetail(), xPath.getNamespaceContext());
			doc = testDoc;
		}
		
		if (validXpath != null){
			try {
				if (removeAttributeFromPath && validXpath.contains("/@"))
					validXpath = XPathUtil.splitXPathIntoNodeAndAttribute(validXpath).getLeft();

				node = (Node) xPath.evaluate(validXpath, doc, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				throw new RuntimeException("Could not parse xPath location of difference node: " + validXpath, e);
			}
			if (node == null){
				LOGGER.warn("Couldnt find node '" + validXpath + "' in " + type.toString());
			}
		}
		return node;
	}
	
	
	/**
	 * Evaluates the given XPath expression on both documents (control and test) and returns all nodes that match that expression
	 * @param xpath
	 * @return
	 */
	public List<Node> getAllMatchingNodesFromBothDocuments(String xpath) {
		String nodeXpath = prefixMatcher.prependNamespacePrefixesToNodes(xpath);
		List<Node> nodes = new ArrayList<Node>();
		NodeList ignoredNodesList;
		// control SVG
		try {
			ignoredNodesList = (NodeList) xPath.evaluate(nodeXpath, controlDoc,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Could not evaluate xPath in control SVG document: "
					+ nodeXpath, e);
		}
		for (int i = 0; i < ignoredNodesList.getLength(); i++) {
			nodes.add(ignoredNodesList.item(i));
		}

		// test SVG
		try {
			ignoredNodesList = (NodeList) xPath.evaluate(nodeXpath, testDoc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Could not evaluate xPath in test SVG document: "
					+ nodeXpath, e);
		}
		for (int i = 0; i < ignoredNodesList.getLength(); i++) {
			nodes.add(ignoredNodesList.item(i));
		}

		return nodes;
	}
	
	/**
	 * Builds the absolute XPath string for the given node using default namespace context to add namespace prefixes
	 * @param node
	 * @return
	 */
	public String getXPathString(Node node) {
		return XPathUtil.getXPathString(node, xPath.getNamespaceContext());
	}
}
