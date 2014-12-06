package org.omg.bpmn.miwg.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.omg.bpmn.miwg.HtmlOutput.Pojos.Output;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;

/***
 * 
 * Represents the findings of a check run.
 * 
 * Stores the number of successful assertions (numOK), findings (numFindings),
 * and analysis output.
 * 
 * @author mk
 *
 */
public class AnalysisResult {

	public int numOK;

	public int numFindings;

	public Collection<? extends Output> output;

	private AnalysisTool analysisTool;

	public AnalysisResult(int numOK, int numFindings,
			Collection<? extends Output> output, AnalysisTool tool) {
		this.numOK = numOK;
		this.numFindings = numFindings;
		this.output = output;
		this.analysisTool = tool;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OK      : ");
		sb.append(numOK);
		sb.append("\n");
		sb.append("Findings: ");
		sb.append(numFindings);
		sb.append("\n");
		return sb.toString();
	}

	public AnalysisTool getAnalysisTool() {
		return analysisTool;
	}

	public String getResultsFileName(AnalysisJob job)
			throws UnsupportedEncodingException {
		String fileName = analysisTool.getName() + "/" + job.getFullApplicationName() + "/" + job.getName()
				+ ".html";
		// fileName = URLEncoder.encode(fileName, "UTF-8");
		return fileName;
	}

	public String getHTMLResultsLink(AnalysisJob job)
			throws UnsupportedEncodingException {
		String link = getResultsFileName(job);
		return link;
	}

	public File getHTMLResultsFile(File rootFolder, AnalysisJob job)
			throws UnsupportedEncodingException {
		assert job != null;
		String fileName = getResultsFileName(job);
		File file = new File(rootFolder, fileName);
		return file;
	}
}
