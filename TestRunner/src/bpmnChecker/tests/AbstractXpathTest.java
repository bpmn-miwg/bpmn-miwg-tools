package bpmnChecker.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bpmnChecker.TestOutput;
import bpmnChecker.xpathAutoChecker.XpathAutoChecker;
import bpmnChecker.xpathAutoChecker.Level1Descriptive.L1TerminateEventChecker;
import bpmnChecker.xpathAutoChecker.Level1Descriptive.L1TimerEventChecker;
import bpmnChecker.xpathAutoChecker.Level2Analytic.L2MessageFlowChecker;

public abstract class AbstractXpathTest extends AbstractTest {

	@SuppressWarnings("unused")
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

	protected void loadFile(String fileName) throws Throwable {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		builder = factory.newDocumentBuilder();
		doc = builder.parse(fileName);
		XPathFactory xpathfactory = XPathFactory.newInstance();
		xpath = xpathfactory.newXPath();
		xpath.setNamespaceContext(new NameSpaceContexts());
		nodeStack = new Stack<Node>();
		push(doc.getDocumentElement());
		autoChecker = new ArrayList<XpathAutoChecker>();
		registerAutoChecker();
		normalizeNames();
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
			// out.println(">>>" + s);
			s = s.replaceAll("&#10;", " ");
			s = s.replaceAll("  ", " ");
			attr.setTextContent(s);
		}
	}

	protected void registerAutoChecker() {
		autoChecker.add(new L2MessageFlowChecker());
		autoChecker.add(new L1TimerEventChecker());
		autoChecker.add(new L1TerminateEventChecker());
		autoChecker.add(new L2MessageFlowChecker());
	}

	protected Node pop() {
		out.println(generateSpaces((depth() - 1) * 2) + "> Pop");
		return nodeStack.pop();
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

	private void currentNode(Node n, String param) throws Throwable {
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
		StackTraceElement e = stacktrace[4];// maybe this number needs to be
											// corrected
		String methodName = e.getMethodName();
		return methodName;
	}

	private String getAttribute(String name) {
		Node namedItem = head().getAttributes().getNamedItem(name);
		if (namedItem == null)
			return null;
		return namedItem.getNodeValue();
	}

	private Node findNode(String expr) throws Throwable {
		// return findNode(currentRoot(), expr);
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

	public Node navigateElement(String expr) throws Throwable {
		return navigateElement(expr, null);
	}

	public Node navigateElement(String expr, String param) throws Throwable {
		if (head() == null) {
			issue(expr, "Parent failed");
			return null;
		}
		Node n = findNode(expr);
		if (n == null) {
			issue(expr, "No node found");
			return null;
		}
		ok(expr);
		currentNode(n, param);
		return n;
	}

	public void navigateElementByParam(String xpathNav, String xpathVal)
			throws Throwable {
		navigateElementByParam(xpathNav, xpathVal, null);
	}

	public void navigateElementByParam(String xpathNav, String xpathVal,
			String param) throws Throwable {
		String message = "Navigation: " + xpathNav + "; Value: " + xpathVal;
		if (head() == null) {
			issue(message, "Parent failed");
			return;
		}

		Node n = findNode(xpathVal);
		if (n == null) {
			issue(message, "Value node not found");
			return;
		}

		if (!(n instanceof Attr)) {
			issue(message, "Value node was found yet is not an attribute");
			return;
		}

		Attr valAttr = (Attr) n;
		String value = valAttr.getNodeValue();

		if (value == null) {
			issue(message, "Value node was found yet has no value");
			return;
		}

		String xpath = String.format(xpathNav, value);
		message += "; Generated: " + xpath;

		Node n2 = findNode(xpath);
		if (n2 == null) {
			issue(message, "Node not found");
			return;
		}

		ok(xpath);
		currentNode(n2, param);
	}

	public void selectElement(String expr) throws Throwable {
		selectElement(expr, null);
	}

	public void selectElement(String expr, String param) throws Throwable {
		if (head() == null) {
			issue(expr, "Parent failed");
			push(null);
			return;
		}
		Node n = findNode(expr);
		if (n == null) {
			issue(expr, "No node found");
			push(null);
			return;
		}
		ok(expr);
		currentNode(n, param);
		push(n);
	}

	public void selectProcess(String xpath) throws Throwable {
		if (head() == null) {
			issue(xpath, "Parent failed");
			push(null);
			return;
		}
		Object o = findNode(xpath);
		if (o == null) {
			issue(xpath, "Process node not found");
			push(null);
			return;
		}
		if (!(o instanceof Node)) {
			issue(xpath, "Wrong type (" + Node.class.getName() + ")");
			push(null);
			return;
		}
		Node n = (Node) o;

		ok(xpath);
		currentNode(n, null);
		push(n);
	}

	public void selectProcessOfCallActivity() throws Throwable {
		if (head() == null) {
			issue("selectProcessofCallActivity", "Parent failed");
			push(null);
			return;
		}
		String processID = getAttribute("calledElement");

		if (processID == null) {
			issue("selectProcessofCallActivity", "Cannot retreive process id");
			push(null);
			return;
		}

		String xpath = "//bpmn:process[@id='" + processID + "']";
		Node n = findNode(xpath);
		if (n == null) {
			issue(xpath, "Cannot find process with id " + processID);
			push(null);
			return;
		}

		ok(xpath);
		currentNode(n, null);
		push(n);
	}

	@Override
	protected void printOK(String assertion) {
		out.println(generateSpaces(depth() * 2) + "> Assertion OK   : "
				+ callingMethod() + ": " + assertion);
	}

	@Override
	protected void printIssue(String assertion, String message) {
		out.println(generateSpaces(depth() * 2) + "> Assertion ISSUE: "
				+ callingMethod() + ": " + assertion + ": " + message);
	}

}
