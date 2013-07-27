package org.omg.bpmn.miwg.experimental;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;
import org.omg.bpmn.miwg.xpathTestRunner.testBase.NameSpaceContexts;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class StackMachine {

	private Node currentNode;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document doc;
	private XPath xpath;
	private Stack<Node> nodeStack;
	@SuppressWarnings("unused")
	private TestOutput out;

	public void init(TestOutput out) {
		factory = null;
		builder = null;
		doc = null;
		xpath = null;
		nodeStack = null;
		this.out = out;
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
			s = s.replaceAll("&#10;", " ");
			s = s.replaceAll("  ", " ");
			s = s.trim();
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

	protected void loadFile(File file) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		builder = factory.newDocumentBuilder();
		doc = builder.parse(file);
		XPathFactory xpathfactory = XPathFactory.newInstance();
		xpath = xpathfactory.newXPath();
		xpath.setNamespaceContext(new NameSpaceContexts());
		nodeStack = new Stack<Node>();
		push(doc.getDocumentElement());
		normalizeNames();
	}

	public Node pop() {
		
		Node n = nodeStack.pop();
		currentNode = n;
		return n;
	}

	public void push(Node n) {
		nodeStack.push(n);
	}

	public Node head() {
		return nodeStack.lastElement();
	}
	
	public Node current() {
		return currentNode;
	}

	@SuppressWarnings("unused")
	private int depth() {
		return nodeStack.size();
	}

	
	@SuppressWarnings("unused")
	private void pushElement(Nav nav) {
		
	}

	public XPath getXPath() {
		return xpath;
	}
	

}
