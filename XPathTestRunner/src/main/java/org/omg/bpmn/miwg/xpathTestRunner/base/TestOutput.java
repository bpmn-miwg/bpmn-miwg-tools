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

	public TestOutput(String name, String outputFolder) throws IOException {
		init(name, outputFolder);
	}

	public TestOutput(TestInstance info, String outputFolder)
			throws IOException {
		init(info.getApplication() + "-" + info.getTest(), outputFolder);
	}

	private void init(String name, String outputFolder) throws IOException {
		KeyValueEntry entry = new KeyValueEntry("TEST RUN", name);
		stack.push(entry);

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
		String line = generateSpaces((stack.size() - 1) * 2) + entry.toLine();

		if (!(entry instanceof EmptyEntry))
			stack.peek().addChild(entry);
		System.out.println(line);
		textFileWriter.println(line);
	}

	public void println() {
		println(new EmptyEntry());
	}

	public void push(AbstractTestEntry entry) {
		String line = generateSpaces(stack.size() * 2) + entry.toLine();

		System.out.println(line);
		textFileWriter.println(line);

		stack.peek().addChild(entry);
		stack.push(entry);
	}

	public void pop() {
		stack.pop();
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
		xstream.processAnnotations(PushEntry.class);
		xstream.processAnnotations(EmptyEntry.class);
		xstream.processAnnotations(PopEntry.class);
		xstream.processAnnotations(TestEntry.class);
		xstream.processAnnotations(ResultsEntry.class);
		xstream.processAnnotations(TotalResultsEntry.class);
		String xml = xstream.toXML(stack.firstElement());

		xmlFileWriter.print(xml);

		if (xmlFileWriter != null)
			xmlFileWriter.close();
	}
}
