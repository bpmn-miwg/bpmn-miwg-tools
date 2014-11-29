package org.omg.bpmn.miwg.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.omg.bpmn.miwg.HtmlOutput.Pojos.Output;
import org.omg.bpmn.miwg.common.testEntries.*;

import com.thoughtworks.xstream.XStream;

public class CheckOutput {
	private File textFile;
	private PrintWriter textFileWriter;
	private File xmlFile;
	private PrintWriter xmlFileWriter;

	private Stack<AbstractCheckEntry> stack = new Stack<AbstractCheckEntry>();
	private String name;
	private boolean logToFile;
	protected List<Output> miwgOutputs = new ArrayList<Output>();

	public CheckOutput(String name, File outputFolder) throws IOException {
		init(name, outputFolder);
	}

	public String getName() {
		return name;
	}

	private void init(String name, File outputFolder) throws IOException {

		this.name = name;

		logToFile = outputFolder != null;

		if (logToFile) {
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

		Analysis entry = new Analysis(name);
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

	public void println(AbstractCheckEntry entry) {
		String line = generateSpaces(stack.size() * 2) + entry.toLine();

		if (!(entry instanceof EmptyEntry))
			stack.peek().addChild(entry);

		System.out.println(line);

		Output miwgOutput = new Output(entry.getOutputType(), line);
		miwgOutputs.add(miwgOutput);

		if (logToFile)
			textFileWriter.println(line);
	}

	public void println() {
		println(new EmptyEntry());
	}

	public void push(AbstractCheckEntry entry) {
		if (!stack.empty()) {
			stack.peek().addChild(entry);
		}

		String line = generateSpaces(stack.size() * 2) + entry.toLine();

		Output miwgOutput = new Output(entry.getOutputType(), line);
		miwgOutputs.add(miwgOutput);
		
		System.out.println(line);

		if (logToFile)
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

		if (logToFile) {
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
			xstream.processAnnotations(Analysis.class);
			xstream.processAnnotations(TestFileEntry.class);
			String xml = xstream.toXML(stack.firstElement());

			xmlFileWriter.print(xml);

			if (xmlFileWriter != null)
				xmlFileWriter.close();

		}
	}

	public Collection<? extends Output> getMiwgOutput() {
		return miwgOutputs;
	}

}
