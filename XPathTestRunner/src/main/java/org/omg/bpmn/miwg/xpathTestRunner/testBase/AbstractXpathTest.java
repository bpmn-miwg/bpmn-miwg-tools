package org.omg.bpmn.miwg.xpathTestRunner.testBase;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.Level1Descriptive.L1ExclusiveGatewayChecker;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.Level1Descriptive.L1MessageEventChecker;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.Level1Descriptive.L1SignalEventChecker;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.Level1Descriptive.L1TerminateEventChecker;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.Level1Descriptive.L1TimerEventChecker;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.Level2Analytic.L2MessageFlowChecker;
import org.omg.bpmn.miwg.xpathTestRunner.xpathAutoChecker.base.XpathAutoChecker;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractXpathTest extends AbstractTest {

	private Node currentNode;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document doc;
	private XPath xpath;
	private Stack<Node> nodeStack;
	private List<XpathAutoChecker> autoChecker;

	@Override
	public void init(TestOutput out) {
		factory = null;
		builder = null;
		doc = null;
		xpath = null;
		nodeStack = null;
		autoChecker = null;
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
			s = s.replaceAll("  ", " ");
			// out.println(">>>>>" + s);
			attr.setTextContent(s);
		}
	}

	protected void loadFile(File file) throws Throwable {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		builder = factory.newDocumentBuilder();
		doc = builder.parse(file);
		XPathFactory xpathfactory = XPathFactory.newInstance();
		xpath = xpathfactory.newXPath();
		xpath.setNamespaceContext(new NameSpaceContexts());
		nodeStack = new Stack<Node>();
		push(doc.getDocumentElement());
		autoChecker = new ArrayList<XpathAutoChecker>();
		registerAutoChecker();
		normalizeNames();
	}

	protected void registerAutoChecker() {
		autoChecker.add(new L1TimerEventChecker());
		autoChecker.add(new L1TerminateEventChecker());
		autoChecker.add(new L1SignalEventChecker());
		autoChecker.add(new L1MessageEventChecker());
		autoChecker.add(new L1ExclusiveGatewayChecker());
		autoChecker.add(new L2MessageFlowChecker());
	}

	protected Node pop() {
		out.println(generateSpaces((depth() - 1) * 2) + "> Pop");
		Node n = nodeStack.pop();
		currentNode = n;
		return n;
	}

	protected void push(Node n) {
		nodeStack.push(n);
	}

	protected Node head() {
		return nodeStack.lastElement();
	}

	private int depth() {
		return nodeStack.size();
	}

	private void setCurrentNode(Node n, String param) throws Throwable {
		currentNode = n;
		for (XpathAutoChecker c : autoChecker) {
			if (c.isApplicable(n, param)) {
				out.println(generateSpaces(depth() * 2) + "> Auto Checker: "
						+ c.getClass().getSimpleName());
				push(n);
				c.check(n, this);
				pop();
			}
		}
	}

	private String generateSpaces(int n) {
		String s = "";
		for (int i = 0; i < n; i++) {
			s += " ";
		}
		return s;
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
		Node namedItem = n.getAttributes().getNamedItem(name);
		if (namedItem == null) {
			finding(name, "Cannot find attribute");
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

	private String getCurrentNodeID() {
		return getAttribute(currentNode, "id");
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

	@Override
	protected void printOK(String message) {
		out.println(generateSpaces(depth() * 2) + "> Assertion OK     : "
				+ callingMethod() + ": " + message);
	}

	@Override
	protected void printFinding(String message, String parameter) {
		if (parameter == null)
			out.println(generateSpaces(depth() * 2) + "> Assertion FINDING: "
					+ callingMethod() + ": " + message);
		else
			out.println(generateSpaces(depth() * 2) + "> Assertion FINDING: "
					+ callingMethod() + ": " + parameter + ": " + message);
	}

	@Override
	protected void printInfo(String message) {
		out.println(generateSpaces(depth() * 2) + "> Assertion INFO : "
				+ callingMethod() + ": " + message);
	}

	public void navigateGatewaySequenceFlow(String sequenceFlowName)
			throws Throwable {
		navigateReferenceX("bpmn:outgoing", "//bpmn:sequenceFlow[@id='%s']",
				".",
				String.format("self::node()[@name='%s']", sequenceFlowName));
	}

	public void navigateReferenceX(String referenceXpath, String targetXpath,
			String targetXpathParameter, String targetCheckXpath)
			throws Throwable {

		if (head() == null) {
			finding("", "Parent failed");
			return;
		}

		List<Node> nodes = findNodes(head(), referenceXpath);
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

			if (findNode(fn, targetCheckXpath) != null)
				foundNode = fn;
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
		String xpath = String.format("%s[@name='%s']", type, name);
		Node n = navigateElementX(xpath);
		return n;
	}

	public Node navigateFollowingElement(String type, String name)
			throws Throwable {
		return navigateFollowingElement(currentNode, type, name);
	}

	protected Node navigateFollowingElement(Node node, String type, String name)
			throws Throwable {
		String xpathOutgoing = "bpmn:outgoing";

		if (node == null) {
			finding(null, "The base node is null");
			return null;
		}

		for (Node outgoingNode : findNodes(node, xpathOutgoing)) {
			String sequenceFlowId = outgoingNode.getTextContent();

			String xpathSequenceFlow = String.format(
					"bpmn:sequenceFlow[@id='%s']", sequenceFlowId);
			Node nSequenceFlow = findNode(xpathSequenceFlow);

			if (nSequenceFlow == null) {
				finding(xpathSequenceFlow, "Cannot find sequence flow");
				return null;
			}

			String nameCondition;
			if (name == null) {
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
		Node n = navigateFollowingElement(type, name);
		push(n);
	}

	protected void selectFollowingElement(Node current, String type, String name)
			throws Throwable {
		Node n = navigateFollowingElement(current, type, name);
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

	public void selectProcess(String xpath) throws Throwable {
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

	public void checkAttribute(String attribute) throws Throwable {
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

	public void checkAttribute(String attribute, String value) throws Throwable {
		if (currentNode == null) {
			finding("", "No current node");
			return;
		}

		String s = getAttribute(currentNode, attribute);

		if (s == null) {
			// Finding is thrown by getAttribute
			return;
		}

		if (!s.equals(value)) {
			finding(attribute, "Attribute does not have the expected value '"
					+ value + "'");
			return;
		}

		ok("Attribute " + attribute + "=" + value);
	}

	protected void checkCancelActivity(boolean value) throws Throwable {
		checkAttribute("cancelActivity", value);
	}

	protected void checkParallelMultiple(boolean value) throws Throwable {
		checkAttribute("parallelMultiple", value);
	}

	protected void checkAttribute(String attribute, boolean value)
			throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
			return;
		}
		String v = getAttribute(currentNode, attribute);
		if (v == null)
			return;
		if (v.equals(Boolean.toString(value))) {
			ok(String.format("@%s = '%s'", attribute, value));
			return;
		} else {
			finding(null, String.format("@%s <> '%s'", attribute, value));
			return;
		}
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

	public void checkTextAssociation(String text) throws Throwable {
		if (currentNode == null) {
			finding(null, "Current node is null");
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

		finding(xpath, "Text annotation not found");
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

		String xpath = String.format(
				"//bpmn:messageFlow[@name='%s' and @%s='%s']", name, dir,
				getCurrentNodeID());

		Node n = findNode(xpath);
		if (n == null) {
			finding(xpath, "Could not find flow");
			return;
		}

		ok(String.format("message flow '%s' (%s)", name, direction));
	}

	public void checkMultiInstance(boolean sequential) throws Throwable {
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

		String v = getAttribute(n, "isSequential");
		if (v == null) {
			// Finding is raised by getAttribute
			return;
		}

		if (!v.equals(Boolean.toString(sequential))) {
			finding(null, String.format("isSequential=%s, should be %s", v,
					sequential));
			return;
		}

		ok(String.format("Multi instance loop characteristics (sequential=%s)",
				sequential));
	}

	public void checkMultiInstanceSequential() throws Throwable {
		checkMultiInstance(true);
	}

	public void checkMultiInstanceParallel() throws Throwable {
		checkMultiInstance(false);
	}

	public void checkEventGatewayExclusive(boolean exclusive) throws Throwable {
		checkAttribute("eventGatewayType", exclusive ? "Exclusive"
				: "Inclusive");
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

}
