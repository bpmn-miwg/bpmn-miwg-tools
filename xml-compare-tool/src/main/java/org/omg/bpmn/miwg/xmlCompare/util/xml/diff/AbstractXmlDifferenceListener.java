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

package org.omg.bpmn.miwg.xmlCompare.util.xml.diff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.omg.bpmn.miwg.xmlCompare.util.xml.XPathUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Difference listener for XMLUnit's {@link Diff} class.
 * Decides for each difference found, whether it actually is a valid difference concerning SVG representation of diagrams
 * 
 * @author philipp.maschke
 *
 */
public abstract class AbstractXmlDifferenceListener implements DifferenceListener {

	//TODO rename to DIFFDocumentType
	public enum XmlDiffDocumentType {
		CONTROL, TEST
	}

	public static final String REGEXP_SIGNAVIO_ID = "sid-........-....-....-....-............";
	private static final String		ATTR_NULL						= "null";
	
	private static final Logger LOGGER = Logger.getLogger(AbstractXmlDifferenceListener.class);

	protected XmlUnitHelper helper;
	
	private Set<Node> ignoredNodes = new HashSet<Node>();
	private Set<Node> ignoredNodesParents = new HashSet<Node>();
	private Set<String> idsAndIdRefs = new HashSet<String>();
	private Set<String> caseInsensitiveAttributes = new HashSet<String>();
	private Set<String> ignoredNamespacesInAttributes = new HashSet<String>();
	private Set<String> languageSpecificAttributes = new HashSet<String>();
	private Map<Node, Set<String>> ignoredAttributesMap = new HashMap<Node, Set<String>>();
	private Map<Node, Set<String>> optionalAttributesMap = new HashMap<Node, Set<String>>();
	private Map<String, Pattern>	defaultAttributeValues			= new HashMap<String, Pattern>();
	private int numIgnoredDiffs;
	private int numAcceptedDiffs;


	/**
	 * Initializes the difference listener according to the configuration.
	 * 
	 * All XPath expressions are evaluated and the returned nodes stored for
	 * later comparison. This is necessary, because the nodes accessible through
	 * the XmlUnit API are not comparable to the actual document nodes. That comparison, however,
	 * is necessary to resolve differences.
	 * 
	 * @param controlDoc
	 * @param testDoc
	 * @param configuration
	 */
	public void initialize(Document controlDoc, Document testDoc, XmlDiffConfiguration configuration) {
		LOGGER.trace("DifferenceListener Initialization started");
		
		ignoredNodes.clear();
		ignoredNodesParents.clear();
		idsAndIdRefs.clear();
		caseInsensitiveAttributes.clear();
		ignoredNamespacesInAttributes.clear();
		languageSpecificAttributes.clear();
		ignoredAttributesMap.clear();
		optionalAttributesMap.clear();
		defaultAttributeValues.clear();
		
		numIgnoredDiffs = numAcceptedDiffs = 0;
		helper = new XmlUnitHelper(controlDoc, testDoc, configuration.getElementsPrefixMatcher());
		
		List<Node> tmpNodeList;
		// parse ignored nodes
		for (String ignoredNodeXpath : configuration.getIgnoredNodes()) {
			tmpNodeList = helper.getAllMatchingNodesFromBothDocuments(ignoredNodeXpath);
			for (Node ignoredNode : tmpNodeList) {
				ignoredNodes.add(ignoredNode);
				ignoredNodesParents.add(ignoredNode.getParentNode());
			}
		}

		// parse ignored attributes
		for (String ignoredAttributeXpath : configuration.getIgnoredAttributes()) {
			// split attribute name and XPath for node
			Pair<String, String> nodeAndAttribute = XPathUtil.splitXPathIntoNodeAndAttribute(ignoredAttributeXpath);			
			tmpNodeList = helper.getAllMatchingNodesFromBothDocuments(nodeAndAttribute.getKey());
			for (Node ignoredAttribute : tmpNodeList) {
				getIgnoredAttributesForNode(ignoredAttribute).add(nodeAndAttribute.getValue());
			}
		}

		// parse optional attributes
		for (String optionalAttributeXpath : configuration.getOptionalAttributes()) {
			// split attribute name and XPath for node
			Pair<String, String> nodeAndAttribute = XPathUtil.splitXPathIntoNodeAndAttribute(optionalAttributeXpath);
			tmpNodeList = helper.getAllMatchingNodesFromBothDocuments(nodeAndAttribute.getKey());
			for (Node optionalAttribute : tmpNodeList) {
				getOptionalAttributesForNode(optionalAttribute).add(nodeAndAttribute.getValue());
			}
		}
		
		defaultAttributeValues.putAll(configuration.getDefaultAttributeValues());

		// parse case insensitive attributes
		for (String caseAttributeName : configuration.getCaseInsensitiveAttributeNames()) {
			this.caseInsensitiveAttributes.add(caseAttributeName);
		}
		
		// store names of id and id reference attributes
		for (String attrName : configuration.getIdsAndIdRefNames()) {
			this.idsAndIdRefs.add(attrName);
		}
		
		// ignored namespaces in attributes
		for (String ns: configuration.getIgnoredNamespacesInAttributes()){
			this.ignoredNamespacesInAttributes.add(ns);
		}
		
		//language specific attributes
		for (String attrName: configuration.getLanguageSpecificAttributes()){
			this.languageSpecificAttributes.add(attrName);
		}
		LOGGER.trace("DifferenceListener Initialization finished");
	}

	
	@Override
	public void skippedComparison(Node node, Node node1) {
		//do nothing
	}


	// TODO better performance
	@Override
	public int differenceFound(Difference difference) {
		try{
			if (isInIgnoredNode(difference) || isCausedByIgnoredChildNode(difference)
					|| isCausedByIgnorableMissingId(difference)
					|| isCausedByIgnorableDifferingId(difference)
					|| isCausedByIgnoredAttribute(difference)
					|| isCausedByOptionalAttribute(difference)
					|| isCausedByIgnorableAttributeValue(difference)
					|| isCausedByNamespaceProblems(difference)
					|| isCausedByLanguageSettings(difference)
					|| isCausedByCapitalizationOfAttributeValue(difference)
					|| canDifferenceBeIgnored(difference)){
				numIgnoredDiffs++;
				return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
			}
			numAcceptedDiffs++;
			return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
		}catch (Exception e){
			LOGGER.error("Exception while examining difference: " + difference.toString(), e);
			return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
		}
	}


	public int getNumberOfIgnoredDifferences(){
		return numIgnoredDiffs;
	}
	public int getNumberOfAcceptedDifferences(){
		return numAcceptedDiffs;
	}
	
	/**
	 * This is the place where inheriting listeners can add their own checks, if other options did not work 
	 * (implementing abstract methods, adjusting the configuration, overwriting protected 'isCausedBy...' methods)
	 * @param difference
	 * @return whether the given difference can be ignored
	 */
	abstract protected boolean canDifferenceBeIgnored(Difference difference);
	
	/**
	 * Sometimes attributes can be ignored if they have specific values. Inheriting listeners can check for these attributes here.
	 * <p/>
	 * Examples:
	 * <ul>
	 * <li>SVG: transform=translate(0)
	 * <li>SVG: transform=rotate(0)
	 * </ul>
	 * @param attrValue
	 * @param attrName
	 * @return whether the given property can be ignored (i.e. it has no impact on the semantic of the document)
	 */
	protected boolean canAttributeBeIgnored(String attrName, String attrValue) {
		Pattern defaultValRegex = defaultAttributeValues.get(attrName);
		if (defaultValRegex != null) {
			return defaultValRegex.matcher(attrValue).matches();
		}
		
		return false;
	}
	
	/**
	 * Check whether the differences between the ids from control and test document can be ignored. 
	 * @param difference
	 * @param controlId
	 * @param testId
	 * @return
	 */
	abstract protected boolean canDifferingIdBeIgnored(Difference difference, String controlId, String testId);

	/**
	 * Check whether a missing id (in the test document) can be ignored.
	 * @param difference
	 * @param controlId the id of the control document
	 * @return
	 */
	abstract protected boolean canMissingIdBeIgnored(Difference difference, String controlId);
	
	
	/**
	 * Ignores all attribute value differences for attributes marked as being language (locale) specific
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByLanguageSettings(Difference difference) {
		if (difference.getId() == DifferenceConstants.ATTR_VALUE_ID && 
				languageSpecificAttributes.contains(difference.getControlNodeDetail().getNode().getLocalName())){
			return true;
		}
		return false;
	}


	/**
	 * Checks whether a difference in attribute values is caused by variations in the capitalization of textual representation of the same value
	 * Example:
	 * 	"true" vs. "TRUE vs. "True"
	 * 
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByCapitalizationOfAttributeValue(Difference difference) {
		if (difference.getId() == DifferenceConstants.ATTR_VALUE_ID) {
			String testValue = difference.getTestNodeDetail().getValue();
			String controlValue = difference.getControlNodeDetail().getValue();
			
			//check case-insensitive attributes
			String testName = difference.getTestNodeDetail().getNode().getLocalName();
			if (caseInsensitiveAttributes.contains(testName) && testValue.equalsIgnoreCase(controlValue)){
				return true;
			}
		}
		return false;
	}


	/**
	 * Server renderer removes all attributes with no values, client renderer doesn't always do this -> 
	 * ignore missing attributes if they don't have a value.
	 * Also ignores missing transform attributes, when editor generates a "transform='translate(0)'"
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByIgnorableAttributeValue(Difference difference) {
		if (difference.getId() == DifferenceConstants.ATTR_NAME_NOT_FOUND_ID) {
			//name is the same for both
			NodeDetail detail = difference.getControlNodeDetail();
			XmlDiffDocumentType type;
			if (detail.getValue() == null || detail.getValue().equals(ATTR_NULL)) {
				type = XmlDiffDocumentType.TEST;
				detail = difference.getTestNodeDetail();
			} else {
				type = XmlDiffDocumentType.CONTROL;
				detail = difference.getControlNodeDetail();
			}
			
			String attrName = detail.getValue();
			if (attrName != null) {
				attrName = helper.addNamespacePrefixToAttribute(attrName, detail.getNode());
				Node attrNode = detail.getNode().getAttributes().getNamedItem(attrName);
				String attrValue = (attrNode == null)? null : attrNode.getNodeValue();
				if (canAttributeBeIgnored(attrName, attrValue)) {
					Node node = helper.getDocumentNode(difference, type, false);
					getIgnoredAttributesForNode(node).add(attrName);
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Editor generates different ids for some attributes.
	 * If this difference is caused by such an attribute, the method checks whether the fixed part 
	 * corresponds to the common id structure ({@link #REGEXP_ID}) and compares the variable part for equality 
	 */
	protected boolean isCausedByIgnorableDifferingId(Difference difference) {
		if (difference.getId() == DifferenceConstants.ATTR_VALUE_ID &&
				idsAndIdRefs.contains(difference.getControlNodeDetail().getNode().getLocalName())) {
			
			String controlId = difference.getControlNodeDetail().getValue();
			String testId = difference.getTestNodeDetail().getValue();
			return canDifferingIdBeIgnored(difference, controlId, testId);
		}
		return false;
	}

	
	/**
	 * Some elements don't need an 'id' attribute. The client renderer still generates one for them, the server doesn't.
	 * Ignores differences where such an id is missing
	 * 
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByIgnorableMissingId(Difference difference) {
		if (difference.getId() == DifferenceConstants.ATTR_NAME_NOT_FOUND_ID
				&& difference.getControlNodeDetail().getValue().equals("id")) {
			String id = difference.getControlNodeDetail().getNode().getAttributes().getNamedItem(
					"id").getNodeValue();
			return canMissingIdBeIgnored(difference, id);
		} else if (difference.getId() == DifferenceConstants.ELEMENT_NUM_ATTRIBUTES_ID) {
			int controlNum = Integer.valueOf(difference.getControlNodeDetail().getValue());
			int testNum = Integer.valueOf(difference.getTestNodeDetail().getValue());
			if (testNum == (controlNum - 1)) {// check if only 1 attribute less
												// in test node
				Node idNode = difference.getControlNodeDetail().getNode().getAttributes()
						.getNamedItem("id");
				if (idNode == null) {
					return false;
				}
				String id = idNode.getNodeValue();
				return canMissingIdBeIgnored(difference, id);
			}
		}
		return false;
	}




	/**
	 * Sometimes differences are generated, where the namespaces are the same, but the prefixes are different.
	 * Ignores such differences
	 * 
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByNamespaceProblems(Difference difference) {
		if (difference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID) {
			if (difference.getControlNodeDetail().getNode().getNamespaceURI().equals(
					difference.getTestNodeDetail().getNode().getNamespaceURI())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Ignores all differences caused by an ignored attribute (see {@link DiffConfiguration#getIgnoredAttributes()}
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByIgnoredAttribute(Difference difference) {
		if (difference.getId() == DifferenceConstants.ATTR_NAME_NOT_FOUND_ID){
			//ignore attributes belonging to a certain namespace
			String attrName = difference.getControlNodeDetail().getValue();
			Node node;
			if (attrName == null || attrName.equals(ATTR_NULL)){
				attrName = difference.getTestNodeDetail().getValue();
				node = helper.getDocumentNode(difference, XmlDiffDocumentType.TEST, false);
			}else{
				node = helper.getDocumentNode(difference, XmlDiffDocumentType.CONTROL, false);
			}
			String namespace = helper.getNamespaceOfAttribute(attrName, node.getAttributes());
			if (namespace != null && ignoredNamespacesInAttributes.contains(namespace)){
				//add attribute name to ignored attributes so that a difference in attribute numbers can be caught later on
				getIgnoredAttributesForNode(node).add(attrName);
				return true;
			}
				
		}
		return isCausedByAttribute(difference, false);
	}


	/**
	 * Ignores all differences, where an optional attribute is missing (see {@link DiffConfiguration#getOptionalAttributes()}
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByOptionalAttribute(Difference difference) {
		return isCausedByAttribute(difference, true);
	}


	protected boolean isCausedByAttribute(Difference difference, boolean isOptional) {
		if (difference.getId() != DifferenceConstants.ATTR_NAME_NOT_FOUND_ID
				&& difference.getId() != DifferenceConstants.ELEMENT_NUM_ATTRIBUTES_ID
				&& difference.getId() != DifferenceConstants.HAS_CHILD_NODES_ID
				&& difference.getId() != DifferenceConstants.ATTR_VALUE_ID) {
			return false;
		}

		// get the nodes that are different
		// don't use 'difference.getControlNodeDetail().getNode()' because it
		// returns a node that is not even equal to ones returned by the xPath
		// parser
		Set<String> attributeSet;
		Node node;
		for (XmlDiffDocumentType type : XmlDiffDocumentType.values()) {// test whether one of
															// the document
															// nodes contains an
															// ignored attribute
			// optional attributes must have same value, if they are set
			if (!isOptional && difference.getId() == DifferenceConstants.ATTR_VALUE_ID) {
				// xpath originally points to the actual attribute node ->
				// remove attribute reference from xpath
				node = helper.getDocumentNode(difference, type, true);
				if (node != null){
//					String attributeName = difference.getControlNodeDetail().getNode().getNodeName();
					String attributeName = XPathUtil.getAttributeNameFromXPath(difference.getControlNodeDetail().getXpathLocation());
					if (getIgnoredAttributesForNode(node).contains(attributeName)) {
						return true;
					}
				}
			}
			node = helper.getDocumentNode(difference, type, false);
			if (node == null) {
				continue;
			}

			if (isOptional) {
				attributeSet = getOptionalAttributesForNode(node);
			} else {
				attributeSet = getIgnoredAttributesForNode(node);
			}

			switch (difference.getId()) {
			case DifferenceConstants.ATTR_NAME_NOT_FOUND_ID:
				String attrName = difference.getControlNodeDetail().getValue();
				if (attrName == null || attrName.equals(ATTR_NULL)){
					attrName = difference.getTestNodeDetail().getValue();
					attrName = helper.addNamespacePrefixToAttribute(attrName, difference.getTestNodeDetail().getNode());
				}else{
					attrName = helper.addNamespacePrefixToAttribute(attrName, difference.getControlNodeDetail().getNode());
				}
				if (attributeSet.contains(attrName)) {
					return true;
				}
				break;
			case DifferenceConstants.ELEMENT_NUM_ATTRIBUTES_ID:
			case DifferenceConstants.HAS_CHILD_NODES_ID:
				if (attributeSet.size() > 0) {
					return true;
				}
				if (containsAttributesOfIgnoredNamespaces(node)) {
					return true;
				}
				if (containsIgnorableAttributeValues(node)) {
					return true;
				}
				break;
			}
		}
		return false;
	}


	/**
	 * Ignores all differences caused by an ignored child node (see {@link DiffConfiguration#getIgnoredNodes()}
	 * @param difference
	 * @return
	 */
	protected boolean isCausedByIgnoredChildNode(Difference difference) {
		switch (difference.getId()) {
		case DifferenceConstants.CHILD_NODELIST_LENGTH_ID:
		case DifferenceConstants.HAS_CHILD_NODES_ID:
			for (XmlDiffDocumentType type : XmlDiffDocumentType.values()) {
				Node node = helper.getDocumentNode(difference, type, false);
				if (node == null) {
					continue;
				}
				if (ignoredNodesParents.contains(node)) {
					return true;
				}
				if (findCorrespondingNode(ignoredNodesParents, node) != null) {
					return true;
				}
			}
			break;
		case DifferenceConstants.CHILD_NODELIST_SEQUENCE_ID:
			for (XmlDiffDocumentType type : XmlDiffDocumentType.values()) {
				Node node = helper.getDocumentNode(difference, type, false);
				if (node == null) {
					continue;
				}
				if (ignoredNodesParents.contains(node.getParentNode())) {
					return true;
				}
				if (findCorrespondingNode(ignoredNodesParents, node.getParentNode()) != null) {
					return true;
				}
			}
			break;
		case DifferenceConstants.CHILD_NODE_NOT_FOUND_ID:
			Node node;
			if (difference.getControlNodeDetail().getNode() != null){
				node = helper.getDocumentNode(difference, XmlDiffDocumentType.CONTROL, false);
			}else{
				node = helper.getDocumentNode(difference, XmlDiffDocumentType.TEST, false);
			}
			
			if (ignoredNodes.contains(node)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Ignores all differences within an ignored node (see {@link DiffConfiguration#getIgnoredNodes()}
	 * @param difference
	 * @return
	 */
	protected boolean isInIgnoredNode(Difference difference) {
		Node controlNode = helper.getDocumentNode(difference, XmlDiffDocumentType.CONTROL, false);

		if (controlNode == null) {
			return false;
		} else {
			Node current = controlNode;
			while (current != null) {
				if (ignoredNodes.contains(current)) {
					return true;
				}
				// else, try to find a node thats equal to current
				if (findCorrespondingNode(ignoredNodes, current) != null) {
					return true;
				}
				// if no equal node found continue with parent
				if (current instanceof Attr) {
					current = ((Attr) current).getOwnerElement();
				} else {
					current = current.getParentNode();
				}
			}
		}
		return false;

		// add same test for testNode if blacklisted nodes are not ignored
	}


	// **************************************************************************************
	// Helper methods
	// **************************************************************************************

	private boolean containsAttributesOfIgnoredNamespaces(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs == null) {
			return false;
		}
		
		String ns = null;
		for (int i=0; i < attrs.getLength(); i++){
			ns = attrs.item(i).getNamespaceURI();
			if (ns != null && ignoredNamespacesInAttributes.contains(ns)) {
				return true;
			}
		}
		return false;
	}


	private boolean containsIgnorableAttributeValues(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs == null) {
			return false;
		}
		
		for (int i=0; i < attrs.getLength(); i++){
			Attr attr = (Attr)attrs.item(i);
			if (isEmptyOrIgnorableAttributeValue(attr.getValue(), attr.getName())) {
				return true;
			}
		}
		return false;
	}
	private boolean isEmptyOrIgnorableAttributeValue(String attrValue, String attrName){
		 return attrValue == null || attrValue.trim().equals("") ||
		 	canAttributeBeIgnored(attrName, attrValue);
	}
	
	protected Set<String> getIgnoredAttributesForNode(Node node) {
		return getAttributeSetForNode(node, ignoredAttributesMap);
	}
	protected Set<String> getOptionalAttributesForNode(Node node) {
		return getAttributeSetForNode(node, optionalAttributesMap);
	}
	private Set<String> getAttributeSetForNode(Node node, Map<Node, Set<String>> nodeToAttributesMap) {
		Set<String> attributeNames = nodeToAttributesMap.get(node);

		if (attributeNames == null) {
			Node corresponding = findCorrespondingNode(nodeToAttributesMap, node);
			if (corresponding != null) {
				attributeNames = nodeToAttributesMap.get(corresponding);
			}

			if (attributeNames == null) {
				attributeNames = new HashSet<String>();
				nodeToAttributesMap.put(node, attributeNames);
			}
		}

		return attributeNames;
	}


	private Node findCorrespondingNode(Map<Node, Set<String>> map, Node node) {
		// try to find existing node that corresponds to this one
		// using Node.isEqual(Node), don't use Node.isSimilar(Node)!
		for (Node existing : map.keySet()) {
			if (node.isEqualNode(existing)) {
				// exchange the new node for the old one to prevent additional
				// searches for the same node
				Set<String> attributeNames = map.remove(existing);
				map.put(node, attributeNames);
				return node;
			}
		}
		return null;
	}
	
	
	private Node findCorrespondingNode(Set<Node> set, Node node) {
		for (Node existing : set) {
			if (node.isEqualNode(existing)) {
				// exchange the new node for the old one to prevent
				// additional searches for the same node
				set.remove(existing);
				set.add(node);
				return node;
			}
		}
		return null;
	}
}
