package org.omg.bpmn.miwg.xpath.common;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;
import org.omg.bpmn.miwg.common.AbstractCheck;
import org.omg.bpmn.miwg.common.DOMCheck;
import org.omg.bpmn.miwg.common.CheckOutput;
import org.omg.bpmn.miwg.common.testEntries.FindingAssertionEntry;
import org.omg.bpmn.miwg.common.testEntries.NodePopEntry;
import org.omg.bpmn.miwg.common.testEntries.NodePushEntry;
import org.omg.bpmn.miwg.common.testEntries.OKAssertionEntry;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class AbstractXpathCheck extends AbstractCheck implements
		DOMCheck {

	private XPath xpath;
	private Node currentNode;
	private Stack<Node> nodeStack;

	public boolean isApplicable(String testResultName) {
		String checkName = getName();
		return testResultName.startsWith(checkName);
	}

	@Override
	public void init(CheckOutput out) {
		xpath = null;
		nodeStack = null;
		super.init(out);
	}

	private void normalizeNames() throws XPathExpressionException {
		Object o = xpath.evaluate("//bpmn:*[@name]", head(),
				XPathConstants.NODESET);
		NodeList nl = (NodeList) o;
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			Attr attr = (Attr) n.getAttributes().getNamedItem("name");
			String s = attr.getValue();
			s = s.trim();
			// out.println(">>>  " + s);
			s = s.replaceAll("&#10;", " ");
			s = s.replaceAll("\n", " ");
			s = s.replaceAll("\r", " ");

			while (s.contains("  "))
				s = s.replaceAll("  ", " ");
			
			s = s.trim();
			// out.println(">>>>>" + s);
			attr.setTextContent(s);
		}

		Object o2 = xpath.evaluate("//child::text()", head(),
				XPathConstants.NODESET);
		NodeList nl2 = (NodeList) o2;
		for (int i = 0; i < nl2.getLength(); i++) {
			Node n = nl2.item(i);
			Text text = (Text) n;
			text.setTextContent(text.getTextContent().trim());
		}
	}

	protected void loadResource(InputStream is) throws Throwable {
		super.loadResource(is);
		XPathFactory xpathfactory = XPathFactory.newInstance();
		xpath = xpathfactory.newXPath();
		xpath.setNamespaceContext(new NameSpaceContexts());
		nodeStack = new Stack<Node>();
		push(doc.getDocumentElement());
		normalizeNames();
	}

	protected Node pop() {
		NodePopEntry e = new NodePopEntry(callingMethod(),
				getNodeIDNoNull(nodeStack.peek()),
				CheckContext.createTestContext(this));
		out.pop(e);
		Node n = nodeStack.pop();
		currentNode = n;
		return n;
	}

	protected String getNodeIDNoNull(Node n) {
		if (n == null)
			return "";

		String s = getAttribute(n, "id");
		if (s == null)
			return "";
		else
			return s;
	}

	protected void push(Node n) {
		NodePushEntry e = new NodePushEntry(callingMethod(),
				getNodeIDNoNull(n), CheckContext.createTestContext(this));
		out.push(e);
		nodeStack.push(n);
	}

	protected Node head() {
		if (nodeStack.isEmpty())
			return null;
		return nodeStack.lastElement();
	}

	protected void ok(String message) {
		ok(new OKAssertionEntry(callingMethod(), message,
				CheckContext.createTestContext(this)));
	}

	@Override
	protected void finding(String parameter, String message) {
		finding(new FindingAssertionEntry(callingMethod(), message, parameter));
	}

	private void setCurrentNode(Node n, String param) throws Throwable {
		currentNode = n;
	}

	private String callingMethod() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

		String lastName = null;
		for (StackTraceElement e : stacktrace) {
			String methodName = e.getMethodName();

			// System.err.println(" >> " + methodName);

			if (methodName.startsWith("navigate")
					|| methodName.startsWith("select")
					|| methodName.startsWith("check")) {
				lastName = methodName;
				// break;
			}

		}

		if (lastName == null)
			return stacktrace[1].getMethodName();
		else
			return lastName;
	}

	private String getAttribute(String name) {
		return getAttribute(head(), name);
	}

	private String getAttribute(Node n, String name) {
		return getAttribute(n, name, true);
	}

	private String getAttribute(Node n, String name, boolean throwFinding) {
		if (n == null && throwFinding) {
			finding("", "Node is null");
			return null;
		}
		Node namedItem = n.getAttributes().getNamedItem(name);
		if (namedItem == null) {
			if (throwFinding) {
				finding(name, "Cannot find attribute");
			}
			return null;
		}
		return namedItem.getNodeValue();
	}

	private Node findNode(String expr) throws Throwable {
		return findNode(head(), expr);
	}

	private Node findNode(Node base, String expr)
			throws XPathExpressionException {
		Object o = xpath.evaluate(expr, base, XPathConstants.NODE);
		if (o == null)
			return null;
		else {
			Node n = (Node) o;
			return n;
		}
	}

	protected String getCurrentNodeID() {
		return getAttribute(currentNode, "id");
	}

	protected String getStackID() {
		return getAttribute(head(), "id");
	}

	protected String getPath(Node node) {
		Node c = node;
		String path = "";

		while (c != null) {

			String nodeName = c.getNodeName();
			Node attr = null;

			if (c.hasAttributes()) {
				attr = c.getAttributes().getNamedItem("id");
			}
			if (attr != null) {
				nodeName = String.format("%s[@id='%s']", nodeName, attr);
			}

			path = nodeName + "/" + path;
			c = c.getParentNode();
		}

		return path;
	}

	private List<Node> findNodes(Node base, String expr)
			throws XPathExpressionException {
		Object o = xpath.evaluate(expr, base, XPathConstants.NODESET);
		if (o == null)
			return null;
		else {
			List<Node> l = new LinkedList<Node>();
			NodeList nl = (NodeList) o;

			for (int i = 0; i < nl.getLength(); i++)
				l.add(nl.item(i));

			return l;
		}
	}

	/***
	 * This works only if the gateway is on the stack
	 */
	public void navigateGatewaySequenceFlowStack(String sequenceFlowName)
			throws Throwable {
		navigateReferenceX("bpmn:outgoing", "//bpmn:sequenceFlow[@id='%s']",
				".",
				String.format("self::node()[@name='%s']", sequenceFlowName));
	}

	public void navigateGatewaySequenceFlow(String sequenceFlowName)
			throws Throwable {
		navigateReferenceX(currentNode, "bpmn:outgoing",
				"//bpmn:sequenceFlow[@id='%s']", ".",
				String.format("self::node()[@name='%s']", sequenceFlowName));
	}

	public void navigateGatewaySequenceFlowCurrentNode(String sequenceFlowName)
			throws Throwable {
		navigateReferenceX(currentNode, "bpmn:outgoing",
				"//bpmn:sequenceFlow[@id='%s']", ".",
				String.format("self::node()[@name='%s']", sequenceFlowName));
	}

	public void navigateReferenceX(String referenceXpath, String targetXpath,
			String targetXpathParameter, String targetCheckXpath)
			throws Throwable {
		navigateReferenceX(head(), referenceXpath, targetXpath,
				targetXpathParameter, targetCheckXpath);
	}

	public void navigateReferenceX(Node baseNode, String referenceXpath,
			String targetXpath, String targetXpathParameter,
			String targetCheckXpath) throws Throwable {

		if (baseNode == null) {
			finding("", "Parent failed");
			return;
		}

		List<Node> nodes = findNodes(baseNode, referenceXpath);
		if (nodes.size() == 0) {
			finding(referenceXpath, "No reference nodes found");
			return;
		}

		Node foundNode = null;
		for (Node n : nodes) {
			Node evaluatedTargetXpathParameterNode = findNode(n,
					targetXpathParameter);
			if (evaluatedTargetXpathParameterNode == null) {
				finding(targetXpathParameter,
						"Target parameter cannot be evaluated");
				return;
			}
			String evaluatedTargetXpathParameter = evaluatedTargetXpathParameterNode
					.getTextContent();

			String evaluatedXpath = String.format(targetXpath,
					evaluatedTargetXpathParameter);
			Node fn = findNode(n, evaluatedXpath);

			if (fn == null) {
				finding(evaluatedXpath, "Target node not found");
				return;
			}

			Node checkedNode = findNode(fn, targetCheckXpath);

			if (checkedNode != null) {
				foundNode = fn;
			}
		}

		if (foundNode != null) {
			String message = String
					.format("Reference: %s; Target: %s; Target parameter %s; Target Check: %s",
							referenceXpath, targetXpath, targetXpathParameter,
							targetCheckXpath);
			ok(message);
			setCurrentNode(foundNode, message);
		} else {
			finding(targetCheckXpath, "Target check failed");
			return;
		}

	}

	public Node navigateElementX(String expr) throws Throwable {
		return navigateElementX(expr, null);
	}

	public Node navigateElement(String type, String name) throws Throwable {
		if (name != null) {
			String xpath = String.format("%s[@name='%s']", type, name);
			Node n = navigateElementX(xpath);
			return n;
		} else {
			String xpath = String.format("%s[not(@name)]", type);
			Node n = navigateElementX(xpath);
			return n;
		}
	}

	public Node navigateFollowingElement(String type, String name)
			throws Throwable {
		return navigateFollowingElement(currentNode, type, name, null);
	}

	public Node navigateFollowingElement(String type, String name,
			String sequenceFlowName) throws Throwable {
		return navigateFollowingElement(currentNode, type, name,
				sequenceFlowName);
	}

	public Node navigateSequenceFlow(String type, String name) throws Throwable {
		return navigateSequenceFlow(currentNode, type, name);
	}

	public Node navigateLane(String name) throws Throwable {
		String xPath = String
				.format("bpmn:laneSet/bpmn:lane[@name='%s']", name);
		return navigateElementX(xPath);
	}

	protected Node navigateSequenceFlow(Node node, String type, String name)
			throws Throwable {
		String nameCondition;
		if (name == null) {
			nameCondition = "";
		} else {
			nameCondition = String.format("@name='%s' and ", name);
		}

		String targetId = getAttribute(node, "targetRef");
		String xpathTarget = String.format("%s[%s@id='%s']", type,
				nameCondition, targetId);
		xpathTarget = String.format("%s[@id='%s']", type, targetId);
		Node n = findNode(xpathTarget);
		if (n != null) {
			ok(xpathTarget);
			setCurrentNode(n, null);
			return n;
		}

		finding(String.format("%s[@name='%s']", type, name),
				"No outgoing reference found");
		return null;
	}

	protected Node navigateFollowingElement(Node node, String type,
			String name, String sequenceFlowName) throws Throwable {
		String xpathOutgoing = "bpmn:outgoing";

		if (node == null) {
			finding(null, "The base node is null");
			return null;
		}

		if (head() == null) {
			finding(null, "Parent failed");
			return null;
		}

		for (Node outgoingNode : findNodes(node, xpathOutgoing)) {
			String sequenceFlowId = outgoingNode.getTextContent();

			String xpathSequenceFlow;

			if (sequenceFlowName == null) {
				xpathSequenceFlow = String.format(
						"bpmn:sequenceFlow[@id='%s']", sequenceFlowId);
			} else {
				xpathSequenceFlow = String.format(
						"bpmn:sequenceFlow[@id='%s' and @name='%s']",
						sequenceFlowId, sequenceFlowName);
			}
			Node nSequenceFlow = findNode(xpathSequenceFlow);

			if (nSequenceFlow != null) {

				String nameCondition;
				
				
				if (name == null) {
					nameCondition = "not(@name) and ";
				} else if (name.equals("")) {
					nameCondition = "";
				} else {
					nameCondition = String.format("@name='%s' and ", name);
				}

				String targetId = getAttribute(nSequenceFlow, "targetRef");
				String xpathTarget = String.format("%s[%s@id='%s']", type,
						nameCondition, targetId);

				Node n = findNode(xpathTarget);
				if (n != null) {
					ok(xpathTarget);
					setCurrentNode(n, null);
					return n;
				}

			}
		}

		finding(String.format("%s[@name='%s']", type, name),
				"No outgoing reference found");
		return null;
	}

	public Node navigateElementX(String expr, String param) throws Throwable {
		if (head() == null) {
			finding(expr, "Parent failed");
			return null;
		}
		Node n = findNode(expr);
		if (n == null) {
			finding(expr, "No node found");
			return null;
		}
		ok(expr);
		setCurrentNode(n, param);
		return n;
	}

	public void navigateElementXByParam(String xpathNav, String xpathVal)
			throws Throwable {
		navigateElementXByParam(xpathNav, xpathVal, null);
	}

	public void selectCollaboration() throws Throwable {
		selectElementX("bpmn:collaboration");
	}

	public void selectPool(String name) throws Throwable {
		String xpath = String.format("bpmn:participant[@name='%s']", name);
		selectElementX(xpath);
	}

	public void selectReferencedProcess() throws Throwable {
		String ref = getAttribute("processRef");
		if (ref == null) {
			return;
		}

		String xpath = String.format("//bpmn:process[@id='%s']", ref);

		selectElementX(xpath);
	}

	public void navigateElementXByParam(String xpathNav, String xpathVal,
			String param) throws Throwable {
		String message = "Navigation: " + xpathNav + "; Value: " + xpathVal;
		if (head() == null) {
			finding(message, "Parent failed");
			return;
		}

		Node n = findNode(xpathVal);
		if (n == null) {
			finding(message, "Value node not found");
			return;
		}

		String value;
		if (n instanceof Element)
			value = n.getTextContent();
		else
			value = n.getNodeValue();

		if (value == null) {
			finding(message, "Value node was found yet has no value");
			return;
		}

		String xpath = String.format(xpathNav, value);
		message += "; Generated: " + xpath;

		Node n2 = findNode(xpath);
		if (n2 == null) {
			finding(message, "Node not found");
			return;
		}

		ok(xpath);
		setCurrentNode(n2, message);
	}

	public void navigateBoundaryEvent(String name) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}
		if (head() == null) {
			finding(null, "Parent failed");
			return;
		}

		String currentElementID = getCurrentNodeID();

		String nameCondition;
		if (name == null) {
			nameCondition = "";
		} else {
			nameCondition = String.format("@name='%s' and ", name);
		}

		String xpathBoundaryElement = String.format(
				"bpmn:boundaryEvent[%s@attachedToRef='%s']", nameCondition,
				currentElementID);
		Node n = findNode(xpathBoundaryElement);
		if (n == null) {
			finding(xpathBoundaryElement, "Cannot find boundary element");
			return;
		}
		setCurrentNode(n, null);
		ok(String.format("Boundary element '%s' found", name));
	}

	public void navigateElement(Node node) throws Throwable {
		setCurrentNode(node, null);
		String path = getPath(node);
		ok(path);
	}

	public void selectFollowingElement(String type, String name)
			throws Throwable {
		if (head() == null) {
			finding(String.format("Type: %s, name: %s", type, name),
					"Parent failed");
			push(null);
			return;
		}
		Node n = navigateFollowingElement(type, name);
		push(n);
	}

	public void selectElement(String type, String name) throws Throwable {
		String xpath = String.format("%s[@name='%s']", type, name);
		selectElementX(xpath);
	}

	public void selectElementX(String expr) throws Throwable {
		selectElementX(expr, null);
	}

	public void selectElementX(String expr, String param) throws Throwable {
		if (head() == null) {
			finding(expr, "Parent failed");
			push(null);
			return;
		}
		Node n = findNode(expr);
		if (n == null) {
			finding(expr, "No node found");
			push(null);
			return;
		}
		ok(expr);
		setCurrentNode(n, param);
		push(n);
	}

	public Node selectCallActivityProcess() throws Throwable {
		if (head() == null) {
			finding(null, "Parent failed");
			push(null);
			return null;
		}

		String calledId = getAttribute(currentNode, "calledElement");
		String xpath = String.format("//bpmn:process[@id='%s']", calledId);

		Node n = findNode(xpath);

		if (n == null) {
			finding(xpath, "Process node not found");
			push(null);
			return null;
		}

		ok(xpath);
		setCurrentNode(n, null);
		push(n);
		return null;
	}

	public void selectProcessX(String xpath) throws Throwable {
		if (head() == null) {
			finding(xpath, "Parent failed");
			push(null);
			return;
		}
		Object o = findNode(xpath);
		if (o == null) {
			finding(xpath, "Process node not found");
			push(null);
			return;
		}
		if (!(o instanceof Node)) {
			finding(xpath, "Wrong type (" + Node.class.getName() + ")");
			push(null);
			return;
		}
		Node n = (Node) o;

		ok(xpath);
		setCurrentNode(n, null);
		push(n);
	}

	public void selectProcessByParticipant(String participant) throws Throwable {
		String xpath = String
				.format("//bpmn:process[@id=//bpmn:participant[@name='%s']/@processRef]",
						participant);
		selectProcessX(xpath);
	}

	public void selectProcessOfCallActivity() throws Throwable {
		if (head() == null) {
			finding("selectProcessofCallActivity", "Parent failed");
			push(null);
			return;
		}
		String processID = getAttribute("calledElement");

		if (processID == null) {
			finding("selectProcessofCallActivity", "Cannot retreive process id");
			push(null);
			return;
		}

		String xpath = "//bpmn:process[@id='" + processID + "']";
		Node n = findNode(xpath);
		if (n == null) {
			finding(xpath, "Cannot find process with id " + processID);
			push(null);
			return;
		}

		ok(xpath);
		setCurrentNode(n, null);
		push(n);
	}

	public void checkDefaultSequenceFlow() throws Throwable {
		if (currentNode == null) {
			finding("", "No current node");
			return;
		}

		if (!currentNode.getLocalName().equals("sequenceFlow")) {
			finding(currentNode.getLocalName(),
					"Current node is not a sequenceFlow");
			return;
		}

		String currentId = getAttribute(currentNode, "id");

		String sourceRef = getAttribute(currentNode, "sourceRef");

		String xpath = String.format("//bpmn:*[@id='%s']", sourceRef);
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find source gateway");
			return;
		}

		String value = getAttribute(n, "default");

		if (!currentId.equals(value)) {
			finding(null, "Not a default sequence flow");
			return;
		}

		ok("Default Sequence Flow");
	}

	public void checkAttributeExists(String attribute) throws Throwable {
		if (currentNode == null) {
			finding("", "No current node");
			return;
		}

		String s = getAttribute(attribute);

		if (s == null) {
			// Finding is thrown by getAttribute
			return;
		}

		ok("Attribute " + attribute + "exists");
	}

	protected void checkAttributeValue(String attribute, boolean value,
			boolean defaultValue) throws Throwable {
		checkAttributeValue(attribute, Boolean.toString(value),
				Boolean.toString(defaultValue));
	}

	protected void checkAttributeValue(Node n, String attribute, String value)
			throws Throwable {
		if (n == null) {
			finding("", "Null node");
			return;
		}

		Node attr = n.getAttributes().getNamedItem(attribute);

		if (attr == null) {
			finding("",
					String.format("Attribute %s is not existing.", attribute));
			return;

		}

		String s = attr.getTextContent();

		if (!s.equals(value)) {
			finding(attribute, String.format(
					"Attribute %s does not have the expected value '%s'",
					attribute, value));
			return;
		}

		ok("Attribute " + attribute + "=" + value);
	}

	protected void checkAttributeValue(String attribute, String value,
			String defaultValue) throws Throwable {
		if (currentNode == null) {
			finding("", "No current node");
			return;
		}

		Node attr = currentNode.getAttributes().getNamedItem(attribute);

		if (attr == null) {
			if (value.equals(defaultValue)) {
				ok(String
						.format("Attribute %s is not existing but the expected value (%s) equals the default value (%s)",
								attribute, value, defaultValue));
				return;
			} else {
				finding("",
						String.format(
								"Attribute %s is not existing and the expected value (%s) is not equal to the default value (%s)",
								attribute, value, defaultValue));
				return;
			}
		}

		String s = attr.getTextContent();

		if (!s.equals(value)) {
			finding(attribute, String.format(
					"Attribute %s does not have the expected value '%s'",
					attribute, value));
			return;
		}

		ok("Attribute " + attribute + "=" + value);
	}

	protected void checkCancelActivity(boolean value) throws Throwable {
		checkAttributeValue("cancelActivity", value, true);
	}

	protected void checkParallelMultiple(boolean value) throws Throwable {
		checkAttributeValue("parallelMultiple", value, false);
	}

	private String artifactTypeToString(ArtifactType artifactType) {
		switch (artifactType) {
		case DataStoreReference:
			return "dataStoreReference";
		case DataObject:
			return "dataObject";
		default:
			assert false;
			return null;
		}
	}

	public void checkDataAssociation(ArtifactType artifactType,
			String artifactName, Direction associationDirection)
			throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		Node elementNode = currentNode;

		push(elementNode);

		String data;
		String setDataRef;
		String dataAssociationRef;
		String artifactRef;

		switch (associationDirection) {
		case Input:
			data = "bpmn:ioSpecification/bpmn:dataInput";
			setDataRef = "bpmn:ioSpecification/bpmn:inputSet/bpmn:dataInputRefs[text()='%s']";
			dataAssociationRef = "bpmn:dataInputAssociation/bpmn:targetRef[text()='%s']";
			artifactRef = "../bpmn:sourceRef";
			break;
		case Output:
			data = "bpmn:ioSpecification/bpmn:dataOutput";
			setDataRef = "bpmn:ioSpecification/bpmn:outputSet/bpmn:dataOutputRefs[text()='%s']";
			dataAssociationRef = "bpmn:dataOutputAssociation/bpmn:sourceRef[text()='%s']";
			artifactRef = "../bpmn:targetRef";
			break;
		default:
			data = null;
			setDataRef = null;
			dataAssociationRef = null;
			artifactRef = null;
			assert false;
		}

		for (Node n : findNodes(elementNode, data)) {
			String id = getAttribute(n, "id");

			{
				String setDataRef2 = String.format(setDataRef, id);
				Node dataRefNode = findNode(elementNode, setDataRef2);

				if (dataRefNode == null) {
					finding(setDataRef2, "Cannot find node");
					pop();
					return;
				}
			}

			{
				String dataAssociationRef2 = String.format(dataAssociationRef,
						id);
				Node dataAssociationRefNode = findNode(elementNode,
						dataAssociationRef2);

				if (dataAssociationRefNode == null) {
					finding(dataAssociationRef2, "Cannot find node");
					pop();
					return;
				} else {
					Node artifactRefNode = findNode(dataAssociationRefNode,
							artifactRef);

					if (artifactRefNode == null) {
						finding(artifactRef, "Cannot find node");
						pop();
						return;
					}

					String artifactID = artifactRefNode.getTextContent();
					String artifact;
					switch (artifactType) {
					case DataStoreReference:
						artifact = "../bpmn:dataStoreReference[@id='%s']";
						break;
					case DataObject:
						artifact = "../bpmn:dataObjectReference[@id='%s']";
						break;
					default:
						artifact = null;
						assert false;
					}

					String artifact2 = String.format(artifact, artifactID);
					Node artifactNode = findNode(elementNode, artifact2);

					if (artifactNode == null) {
						finding(artifact2, "Cannot find artifact node");
						pop();
						return;
					}

					if (getAttribute(artifactNode, "name").equals(artifactName)) {
						ok(String.format("Association reference %s '%s' found",
								artifactTypeToString(artifactType),
								artifactName));
						pop();
						return;
					}
				}

			}

		}

		finding(null, String.format(
				"Cannot find the associated artifact %s '%s'",
				artifactTypeToString(artifactType), artifactName));
		pop();
	}

	public void checkTerminateEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:terminateEventDefinition | bpmn:eventDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find terminate event definition");
			return;
		} else {
			ok("Terminate event definition");
			return;
		}
	}

	public void checkSignalEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:signalEventDefinition | bpmn:signalEventDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find signal event definition");
			return;
		} else {
			ok("Signal event definition");
			return;
		}
	}

	public void checkMessageEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:messageEventDefinition | bpmn:messageEventDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find message event definition");
			return;
		} else {
			ok("Message event definition");
			return;
		}
	}

	public void checkTimerEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:timerEventDefinition | bpmn:eventDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find timer event definition");
			return;
		} else {
			ok("Timer event definition");
			return;
		}
	}

	public void checkXORMarkersForProcess(boolean hasMarker) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = ".//bpmn:exclusiveGateway";
		List<Node> gatewayNodes = findNodes(currentNode, xpath);

		for (Node gatewayNode : gatewayNodes) {
			String id = getAttribute(gatewayNode, "id");
			String xpath2 = String.format(
					"//bpmndi:BPMNShape[@bpmnElement='%s']", id);
			Node n = findNode(currentNode, xpath2);

			if (n == null) {
				finding(xpath,
						String.format(
								"There is no BPMNShape element for the BPMN element with the id '%s'.",
								id));
				return;
			} else {
				String value = getAttribute(n, "isMarkerVisible", false);

				if (value == null) {
					if (hasMarker) {
						finding(null,
								String.format(
										"There is no isMarkerVisible attribute in the BPMNShape element for the BPMN element with the id '%s' although %s is expected.",
										id, Boolean.toString(true)));
						return;
					}
				} else {
					if (!value.equals(Boolean.toString(hasMarker))) {
						finding(null,
								String.format(
										"The XOR marker for the BPMN element with the id '%s' is %s although %s is expected.",
										id, value, Boolean.toString(hasMarker)));
						return;
					}
				}
			}

			ok(String.format("All XOR markers have the expected value '%s'.",
					Boolean.toString(hasMarker)));
		}
	}

	public void checkEscalationEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:escalationEventDefinition | bpmn:escalationDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find escalation event definition");
			return;
		} else {
			ok("Escalation event definition");
			return;
		}
	}

	public void checkLinkEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:linkEventDefinition | bpmn:linkDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find link event definition");
			return;
		} else {
			ok("Link event definition");
			return;
		}
	}

	public void checkErrorEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:errorEventDefinition | bpmn:errorDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find error event definition");
			return;
		} else {
			ok("Error event definition");
			return;
		}
	}

	public void checkName(String nameExpected) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String nameActual = getAttribute(currentNode, "name", false);

		if (nameActual == null) {
			finding("", "No name attribute");
			return;
		} else if (nameExpected.equals(nameActual)) {
			ok(String.format("Element name attribute: %s", nameExpected));
			return;
		} else {
			finding("", String.format("Wrong name (expected: %s, actual: %s",
					nameExpected, nameActual));
			return;
		}
	}

	public void checkGlobalTask(boolean globalTaskShouldExist) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String calledElement = getAttribute(currentNode, "calledElement", false);

		if (globalTaskShouldExist) {
			if (calledElement == null) {
				finding(null, "Attribute 'calledElement' does not exist");
				return;
			}

			String xpath = String.format("//bpmn:globalUserTask[@id='%s']",
					calledElement);

			Node n = findNode(currentNode, xpath);
			if (n == null) {
				finding(xpath, "Cannot find global Task");
				return;
			} else {
				ok("Global Task is referenced");
				return;
			}
		} else {
			if (calledElement != null) {
				finding(null,
						"Attribute 'calledElement' exists although the test expects that this element does not exist");
				return;
			} else {
				ok("Attibute 'calledElement' does not exist (as expected)");
				return;
			}
		}

	}

	public void checkTextAssociation(String text) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}
		if (head() == null) {
			finding(null, "Parent failed");
			return;
		}

		String currentID = getCurrentNodeID();
		if (currentID == null) {
			return;
		}

		// /bpmn:text

		String xpath = String
				.format("bpmn:textAnnotation[@id=../bpmn:association[@sourceRef='%s']/@targetRef]/bpmn:text",
						currentID);

		List<Node> nl = findNodes(head(), xpath);
		for (Node candidate : nl) {
			String value = candidate.getTextContent();
			if (value.equals(text)) {
				ok(String.format("Text annotation found: %s", text));
				return;
			}
		}

		finding(xpath, String.format("Text annotation '%s' not found", text));
	}

	public void checkConditionalEvent() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:conditionalEventDefinition | bpmn:conditionalDefinitionRef";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find conditional event definition");
			return;
		} else {

			String xpath2 = "bpmn:condition";
			Node n2 = findNode(n, xpath2);

			if (n2 == null) {
				finding(xpath2, "Cannot find condition");
				return;
			}

			ok("Conditional event definition");
			return;
		}
	}

	public void checkMessageFlow(String name, Direction direction)
			throws Throwable {
		checkMessageFlow(name, direction, null, null);
	}

	public void checkMessageFlow(String name, Direction direction,
			String partnerType, String partnerName) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		if (head() == null) {
			finding(null, "Parent failed");
			return;
		}

		String dir;
		switch (direction) {
		case Input:
			dir = "targetRef";
			break;
		case Output:
			dir = "sourceRef";
			break;
		default:
			assert false;
			return;
		}

		String xpathName;
		if (name == null || name.equals("")) {
			xpathName = "(not(@name) or @name='')"; // "string-length(@attr)=0";
		} else {
			xpathName = String.format("@name='%s'", name);
		}

		String xpath = String.format("//bpmn:messageFlow[%s and @%s='%s']",
				xpathName, dir, getCurrentNodeID());

		Node n = findNode(xpath);
		if (n == null) {
			finding(xpath, "Could not find messageFlow");
			return;
		}

		if (partnerType != null && partnerName != null) {

			String partnerDirectionAttribute;
			switch (direction) {
			case Input:
				partnerDirectionAttribute = "sourceRef";
				break;
			case Output:
				partnerDirectionAttribute = "targetRef";
				break;
			default:
				assert false;
				return;
			}

			// TODO: Find target node

			String targetID = getAttribute(n, partnerDirectionAttribute);

			String xpathTarget = String.format("//%s[@name='%s' and @id='%s']",
					partnerType, partnerName, targetID);

			Node targetNode = findNode(xpathTarget);
			if (targetNode == null) {
				finding(xpathTarget, "Could not find messageFlow partner");
				return;
			}

			ok(String.format("message flow '%s' (%s) -> %s", name, direction,
					xpathTarget));
		} else {
			ok(String.format("message flow '%s' (%s)", name, direction));
		}
	}

	protected void checkMultiInstance(boolean sequential) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath = "bpmn:multiInstanceLoopCharacteristics";
		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Loop characteristics not set");
			return;
		}

		boolean defaultValue = false;
		String attribute = "isSequential";
		Node attr = n.getAttributes().getNamedItem(attribute);

		if (attr == null) {
			if (sequential == defaultValue) {
				ok(String
						.format("Attribute %s is not existing but the expected value (%s) equals the default value (%s)",
								attribute, sequential, defaultValue));
				return;
			} else {
				finding("",
						String.format(
								"Attribute %s is not existing and the expected value (%s) is not equal to the default value (%s)",
								attribute, sequential, defaultValue));
				return;
			}
		} else {
			String v = attr.getTextContent();
			if (!v.equals(Boolean.toString(sequential))) {
				finding(null, String.format("%s=%s, should be %s", attribute,
						v, sequential));
				return;
			}

			ok(String.format(
					"Multi instance loop characteristics (sequential=%s)",
					sequential));
		}
	}

	public void checkMultiInstanceSequential() throws Throwable {
		checkMultiInstance(true);
	}

	public void checkMultiInstanceParallel() throws Throwable {
		checkMultiInstance(false);
	}

	public void checkEventGatewayExclusive(boolean exclusive) throws Throwable {
		checkAttributeValue("eventGatewayType", exclusive ? "Exclusive"
				: "Inclusive", "Exclusive");
	}

	public void checkMessageDefinition() throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String messageID = getAttribute(currentNode, "messageRef");
		if (messageID == null) {
			// Finding is raised by getAttribute
			return;
		}

		String xpath = String.format("//bpmn:message[@id='%s']", messageID);
		Node n = findNode(xpath);

		if (n == null) {
			finding(xpath, "No message definition");
			return;
		}

		ok("Message definition");

	}

	public void checkOwner(OwnerType ownerType, String potentialOwner)
			throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String xpath;
		if (ownerType == OwnerType.PotentialOwner)
			xpath = "bpmn:potentialOwner/bpmn:resourceRef";
		else
			xpath = "bpmn:performer/bpmn:resourceRef";

		Node n = findNode(currentNode, xpath);

		if (n == null) {
			finding(xpath, "Cannot find resource reference");
			return;
		}

		String ref = n.getTextContent();

		String xpath2 = String.format(
				"/bpmn:definitions/bpmn:resource[@id='%s']", ref);
		Node n2 = findNode(currentNode, xpath2);
		if (n2 == null) {
			finding(xpath, "Cannot find resource");
			return;
		}

		checkAttributeValue(n2, "name", potentialOwner);
	}

	public void checkOperation(String operation) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}

		String ref = getAttribute(currentNode, "operationRef");
		String xpath = String.format(
				"/bpmn:definitions/bpmn:interface/bpmn:operation[@id='%s']",
				ref);

		Node n = findNode(currentNode, xpath);
		if (n == null) {
			finding(xpath, "Cannot find operation");
			return;
		}

		checkAttributeValue(n, "name", operation);
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	@Override
	public AnalysisResult execute(Document actualDocument, AnalysisTool tool) throws Throwable {
		this.doc = actualDocument;
		XPathFactory xpathfactory = XPathFactory.newInstance();
		xpath = xpathfactory.newXPath();
		xpath.setNamespaceContext(new NameSpaceContexts());
		nodeStack = new Stack<Node>();
		push(doc.getDocumentElement());
		normalizeNames();
		doExecute();
		return new AnalysisResult(resultsOK(), resultsFinding(),
				out.getMiwgOutput(), tool);
	}

	protected abstract void doExecute() throws Throwable;

}
