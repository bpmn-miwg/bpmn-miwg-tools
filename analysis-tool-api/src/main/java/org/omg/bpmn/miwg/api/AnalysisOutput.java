package org.omg.bpmn.miwg.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.omg.bpmn.miwg.api.output.dom.AbstractCheckEntry;
import org.omg.bpmn.miwg.api.output.dom.Analysis;
import org.omg.bpmn.miwg.api.output.dom.EmptyEntry;
import org.omg.bpmn.miwg.api.output.dom.FindingAssertionEntry;
import org.omg.bpmn.miwg.api.output.dom.InfoEntry;
import org.omg.bpmn.miwg.api.output.dom.NodePopEntry;
import org.omg.bpmn.miwg.api.output.dom.OKAssertionEntry;
import org.omg.bpmn.miwg.api.output.dom.TestEntry;
import org.omg.bpmn.miwg.api.output.html.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.api.output.html.Output;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Receives the progress and (partial) results from the test tools.
 *
 */

public class AnalysisOutput {
	private File textFile;
	private PrintWriter textFileWriter;
	private File xmlFile;
	private PrintWriter xmlFileWriter;

	private Stack<AbstractCheckEntry> stack = new Stack<AbstractCheckEntry>();
	private String name;
	private boolean logToFile;
	protected List<Output> htmlOutputs = new ArrayList<Output>();

	private int numFindings = 0;
	private int numOK = 0;

	private AnalysisTool analysisTool;

	private AnalysisJob job;

	public AnalysisOutput(AnalysisJob job, AnalysisTool tool)
			throws IOException {
		this.analysisTool = tool;
		this.name = job.getName();
		this.job = job;

		String outputFolder = job.getReportFolder();

		logToFile = outputFolder != null;

		if (logToFile) {
			textFile = new File(getResultsFileName(job, ".txt"));
			if (!textFile.getParentFile().exists()) {
				textFile.getParentFile().mkdirs();
			}
			textFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					textFile)));

			xmlFile = new File(getResultsFileName(job, ".xml"));
			if (!xmlFile.getParentFile().exists()) {
				xmlFile.getParentFile().mkdirs();
			}
			xmlFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					xmlFile)));
		}

		Analysis entry = new Analysis(name);
		push(entry);
	}

	public void ok(AbstractCheckEntry entry) {
		numOK++;
		println(entry);
	}

	public void ok(String assertion, String message) {
		ok(new OKAssertionEntry(assertion, message, null));
	}

	public void finding(AbstractCheckEntry entry) {
		numFindings++;
		println(entry);
	}

	public void finding(String assertion, String message) {
		finding(new FindingAssertionEntry(assertion, message,
				(AnalysisContext) null));
	}

	public void info(String message) {
		info(new InfoEntry(message));
	}

	public void info(AbstractCheckEntry entry) {
		println(entry);
	}

	public void pluggableAssertionOk(String assertion, String message) {
		ok(new OKAssertionEntry("Pluggable: " + assertion, message, null));
	}

	public void pluggableAssertionFinding(String assertion, String message,
			String parameter) {
		ok(new FindingAssertionEntry("Pluggable: " + assertion, message,
				(String) null));
	}

	public int resultsOK() {
		return numOK;
	}

	public int resultsFinding() {
		return numFindings;
	}

	public String getName() {
		return name;
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

		if (!(entry instanceof EmptyEntry)) {
			stack.peek().addChild(entry);
			// System.err.println(stack.peek() + ".ADD(" + entry + ")");
		}

		System.out.println(line);

		Output htmlOutput = entry.getHtmlOutput();
		htmlOutputs.add(htmlOutput);

		if (logToFile)
			textFileWriter.println(line);

	}

	public void println() {
		println(new EmptyEntry());
	}

	public void push(AbstractCheckEntry entry) {
		if (!stack.empty()) {
			stack.peek().addChild(entry);
			// System.err.println(stack.peek() + ".ADD(" + entry + ")");
		}

		String line = generateSpaces(stack.size() * 2) + entry.toLine();

		Output miwgOutput = new Output(entry.getOutputType(), line);
		htmlOutputs.add(miwgOutput);

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

	public void close() throws Exception {

		if (logToFile) {
			if (textFileWriter != null)
				textFileWriter.close();

			Serializer serializer = new Persister();

			try {
				serializer.write(stack.firstElement(), xmlFileWriter);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (xmlFileWriter != null)
				xmlFileWriter.close();

			HTMLAnalysisOutputWriter.writeAnalysisResults(job,
					analysisTool, this);
		}
	}

	public Collection<? extends Output> getHtmlOutput() {
		return htmlOutputs;
	}

	public void addIssues(int i) {
		numFindings += i;
	}

	public AnalysisTool getAnalysisTool() {
		return analysisTool;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OK      : ");
		sb.append(resultsOK());
		sb.append("\n");
		sb.append("Findings: ");
		sb.append(resultsFinding());
		sb.append("\n");
		return sb.toString();
	}

	public String getResultsFileName(AnalysisJob job, String suffix)
			throws UnsupportedEncodingException {
		String fileName = job.getReportFolder() + "/site/" + analysisTool.getName()
				+ "/" + job.getFullApplicationName() + "/" + job.getName()
				+ suffix;
		return fileName;
	}

	public String getHTMLResultsLink(AnalysisJob job)
			throws UnsupportedEncodingException {
		String link = getResultsFileName(job, ".html");
		return link;
	}

	public int numFindings() {
		return numFindings;
	}

	public int numOKs() {
		return numOK;
	}

}
