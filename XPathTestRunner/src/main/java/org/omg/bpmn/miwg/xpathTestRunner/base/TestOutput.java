package org.omg.bpmn.miwg.xpathTestRunner.base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import org.omg.bpmn.miwg.xpathTestRunner.base.testEntries.*;

import com.thoughtworks.xstream.XStream;

public class TestOutput {
	private File textFile;
	private PrintWriter textFileWriter;
	private File xmlFile;
	private PrintWriter xmlFileWriter;

	private Stack<AbstractTestEntry> stack = new Stack<AbstractTestEntry>();
	private String name;

	public TestOutput(String name, String outputFolder) throws IOException {
		init(name, outputFolder);
	}

	public TestOutput(TestInstance info, String outputFolder)
			throws IOException {
		init(info.getApplication() + "-" + info.getTest(), outputFolder);
	}

	public String getName() {
		return name;
	}

	private void init(String name, String outputFolder) throws IOException {

		this.name = name;

		textFile = new File(outputFolder, name + ".txt");
		if (!textFile.getParentFile().exists()) {
			textFile.getParentFile().mkdirs();
		}
		textFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
				textFile)));

		xmlFile = new File(outputFolder, name + ".xml");
		if (!xmlFile.getParentFile().exists()) {
			xmlFile.getParentFile().mkdirs();
		}
		xmlFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
				xmlFile)));

		TestRunEntry entry = new TestRunEntry(name);
		push(entry);
	}

	public File getFile() {
		return textFile;
	}

	private String generateSpaces(int n) {
		String s = "";
		for (int i = 0; i < n; i++) {
			s += " ";
		}
		return s;
	}

	public void println(AbstractTestEntry entry) {
		String line = generateSpaces(stack.size() * 2) + entry.toLine();

		if (!(entry instanceof EmptyEntry))
			stack.peek().addChild(entry);
		System.out.println(line);
		textFileWriter.println(line);
	}

	public void println() {
		println(new EmptyEntry());
	}

	public void push(AbstractTestEntry entry) {
		if (!stack.empty()) {
			stack.peek().addChild(entry);
		}

		String line = generateSpaces(stack.size() * 2) + entry.toLine();

		System.out.println(line);
		textFileWriter.println(line);

		stack.push(entry);
	}

	public void pop() {
		if (!stack.empty() && (!(stack.peek() instanceof TestEntry))) {
			stack.pop();
		}
	}

	public void pop(NodePopEntry entry) {
		if (!stack.empty() && (!(stack.peek() instanceof TestEntry))) {
			println(entry);
			stack.pop();
		}
	}

	public void forcePop() {
		stack.pop();
	}

	public void popToTestEntry() {
		while ((!stack.empty()) && (!(stack.peek() instanceof TestEntry))) {
			stack.pop();
		}
	}

	public void close() {
		if (textFileWriter != null)
			textFileWriter.close();

		XStream xstream = new XStream();
		xstream.processAnnotations(ExceptionEntry.class);
		xstream.processAnnotations(FindingAssertionEntry.class);
		xstream.processAnnotations(FindingNavigationEntry.class);
		xstream.processAnnotations(KeyValueEntry.class);
		xstream.processAnnotations(ListKeyValueEntry.class);
		xstream.processAnnotations(InfoEntry.class);
		xstream.processAnnotations(ListEntry.class);
		xstream.processAnnotations(OKAssertionEntry.class);
		xstream.processAnnotations(OKNavigationEntry.class);
		xstream.processAnnotations(NodePushEntry.class);
		xstream.processAnnotations(EmptyEntry.class);
		xstream.processAnnotations(NodePopEntry.class);
		xstream.processAnnotations(TestEntry.class);
		xstream.processAnnotations(ResultsEntry.class);
		xstream.processAnnotations(TotalResultsEntry.class);
		xstream.processAnnotations(TestRunEntry.class);
		xstream.processAnnotations(TestFileEntry.class);
		String xml = xstream.toXML(stack.firstElement());

		xmlFileWriter.print(xml);

		if (xmlFileWriter != null)
			xmlFileWriter.close();
	}
}
