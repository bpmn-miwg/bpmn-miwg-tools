package org.omg.bpmn.miwg.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.omg.bpmn.miwg.api.tools.AnalysisTool;

public class AnalysisRun {

	private AnalysisJob job;

	private Map<AnalysisTool, AnalysisOutput> results = new Hashtable<AnalysisTool, AnalysisOutput>();

	public AnalysisRun(AnalysisJob job) {
		this.job = job;
	}

	public void addResult(AnalysisTool tool, AnalysisOutput result) {
		results.put(tool, result);
	}

	public String buildOverviewFragmentHtml(File folder)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();

		sb.append("<div class=\"test\" tool=\"" + job.getFullApplicationName()
				+ "\" testcase=\"" + job.getMIWGTestCase() + "\" ");

		for (AnalysisTool analysisTool : results.keySet()) {
			AnalysisOutput result = results.get(analysisTool);
			assert result != null;

			sb.append("data-" + analysisTool.getName() + "-ok=\""
					+ result.numOKs() + "\" ");
			sb.append("data-" + analysisTool.getName() + "-finding=\""
					+ result.numFindings() + "\" ");
		}

		sb.append(">\n");

		sb.append("<ul>\n");

		for (AnalysisTool tool : results.keySet()) {
			AnalysisOutput result = results.get(tool);
			assert result != null;

			sb.append("<li>");
			sb.append("<a href=\"" + result.getHTMLResultsLink(job) + "\">");
			sb.append(result.getHTMLResultsLink(job));
			sb.append("</a>");
			sb.append("</li>\n");
		}

		sb.append("</ul>\n");

		sb.append("</div>\n");

		String html = sb.toString();

		return html;
	}

	public AnalysisOutput getResult(Class<? extends AnalysisTool> analysisTool) {
		for (AnalysisTool tool : results.keySet()) {
			if (tool.getClass().equals(analysisTool)) {
				AnalysisOutput result = results.get(tool);
				assert result != null;

				return result;
			}
		}

		return null;
	}

	public AnalysisJob getJob() {
		return job;
	}

	public String buildOverviewFragmentTxt(File folder,
			List<Class<? extends AnalysisTool>> sortedTools) {

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("%-60s %-8s ", job.getFullApplicationName(),
				job.getMIWGTestCase()));

		for (Class<? extends AnalysisTool> toolClass : sortedTools) {
			AnalysisOutput result = getResult(toolClass);
			if (result != null) {
				sb.append(String.format("  %11d", result.numFindings()));
			} else {
				sb.append(String.format("  %11s", "-"));
			}
		}

        sb.append(System.getProperty("line.separator"));

		return sb.toString();
	}

}
