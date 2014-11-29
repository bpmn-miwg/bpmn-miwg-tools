package org.omg.bpmn.miwg.api;

import java.io.File;

import org.omg.bpmn.miwg.util.HTMLAnalysisOutputWriter;

public class AnalysisRun {

	private AnalysisResult xsdResult, compareResult, xpathResult;

	private AnalysisJob job;

	private final String OVERVIEW_TEMPLATE = "<div class=\"analysisRun\" xsd-ok=\"%d\" xsd-finding=\"%d\" compare-ok=\"%d\" compare-finding=\"%d\" xpath-ok=\"%d\" xpath-finding=\"%d\"><a href=\"%s\">%s</a></div>\n";

	public AnalysisRun(AnalysisResult xsdResult, AnalysisResult compareResult,
			AnalysisResult xpathResult, AnalysisJob job) {
		this.xsdResult = xsdResult;
		this.compareResult = compareResult;
		this.xpathResult = xpathResult;
		this.job = job;
	}

	public String buildOverviewHTML(File folder) {
		String html = 	String.format(OVERVIEW_TEMPLATE, xsdResult.numOK, xsdResult.numOK,
				compareResult.numOK, compareResult.numFindings,
				xpathResult.numOK, xpathResult.numFindings,
				HTMLAnalysisOutputWriter.getAnalysisResultsFile(folder,
						job), HTMLAnalysisOutputWriter
						.getAnalysisResultsFile(folder, job));

		return html;
	}
}
