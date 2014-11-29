package org.omg.bpmn.miwg.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import org.omg.bpmn.miwg.api.tools.AnalysisTool;

public class AnalysisRun {

	private AnalysisJob job;

	private Map<AnalysisTool, AnalysisResult> results = new Hashtable<AnalysisTool, AnalysisResult>();

	public AnalysisRun(AnalysisJob job) {
		this.job = job;
	}

	public void addResult(AnalysisTool tool, AnalysisResult result) {
		results.put(tool, result);
	}

	public String buildOverviewHTML(File folder)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();

		sb.append("<div class=\"analysisRun\"");

		for (AnalysisTool tool : results.keySet()) {
			AnalysisResult result = results.get(tool);
			assert result != null;

			sb.append(" " + tool.getName() + "-ok=\"" + result.numOK + "\" ");
			sb.append(" " + tool.getName() + "-finding=\"" + result.numFindings
					+ "\" ");
		}

		sb.append(">");
		
		sb.append("<ul>");

		for (AnalysisTool tool : results.keySet()) {
			AnalysisResult result = results.get(tool);
			assert result != null;

			sb.append("<li>\n");
			sb.append("<a href=\"" + result.getHTMLResultsLink(job) + "\">");
			sb.append(result.getResultsFileName(job));
			sb.append("</a>");
			sb.append("</li>\n");
		}

		sb.append("</ul>");
		
		
		sb.append("</div>\n");

		String html = sb.toString();

		return html;
	}

	public AnalysisResult getResult(String analysisToolName) {
		for (AnalysisTool tool : results.keySet()) {
			if (tool.getName().equals(analysisToolName)) {
				AnalysisResult result = results.get(tool);
				assert result != null;

				return result;
			}
		}

		return null;
	}

}
