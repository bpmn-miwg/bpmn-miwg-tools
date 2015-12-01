package org.omg.bpmn.miwg.api.output.overview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.output.html.Test;
import org.omg.bpmn.miwg.api.output.html.TestResults;
import org.omg.bpmn.miwg.api.output.html.Tool;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;
import org.omg.bpmn.miwg.util.StringUtil;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class OverviewWriter {

	public static File getOverviewFileHtml(File rootFolder) {
		return new File(rootFolder, "overview.html");
	}

	public static File writeAnalysisResults(AnalysisJob job,
			AnalysisTool aTool, AnalysisOutput result) throws Exception {
		PrintWriter htmlOutputWriter = null;
		InputStream templateStream = null;
		Scanner scanner = null;
		File resultsFile = null;
		try {
			// Build Simple XML POJO
			// AnalysisOutputFragment root = new AnalysisOutputFragment();
			// root.output = result.output;

			TestResults testResults_root = new TestResults();
			Tool tool = testResults_root.addTool(job.getFullApplicationName());
			Test test = tool.addTest(job.getMIWGTestCase(), job.getVariant()
					.toString());
			test.addAll(result.getHtmlOutput());

			// Build a string containing the HTML fragment representing the
			// analysis results
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Serializer serializer = new Persister();
			serializer.write(testResults_root, outputStream);
			String outputString = outputStream.toString();

			templateStream = OverviewWriter.class
					.getResourceAsStream("/AnalysisResult.SingleJob.Template.html");

			scanner = new Scanner(templateStream, "UTF-8");
			String template = scanner.useDelimiter("\\A").next();

			String completeString = template.replace("{ANALYSISRESULTS}",
					outputString);

			resultsFile = new File(result.getResultsFileName(job, ".html"));

			resultsFile.getParentFile().mkdirs();

			htmlOutputWriter = new PrintWriter(resultsFile);
			htmlOutputWriter.println(completeString);
		} finally {
			try {
				htmlOutputWriter.close();
			} catch (Exception e) {
				;
			}
			try {
				templateStream.close();
			} catch (IOException e) {
				;
			}
			scanner.close();
		}
		return resultsFile;
	}

	public static void writeOverviewHtml(String outputFolder,
			Collection<AnalysisRun> runs) throws Exception {
		PrintWriter htmlOutputWriter = null;
		InputStream templateStream = null;
		Scanner scanner = null;
		File folder = new File(outputFolder);
		try {

			templateStream = OverviewWriter.class
					.getResourceAsStream("/AnalysisResult.Overview.Template.html");

			scanner = new Scanner(templateStream, "UTF-8");
			String template = scanner.useDelimiter("\\A").next();

			StringBuilder sb = new StringBuilder();

			for (AnalysisRun run : runs) {
				sb.append(run.buildOverviewFragmentHtml(folder));
			}

			String overviewString = sb.toString();

			String completeString = template.replace("{OVERVIEW}",
					overviewString);

			htmlOutputWriter = new PrintWriter(getOverviewFileHtml(folder));

			htmlOutputWriter.println(completeString);
		} finally {
			try {
				htmlOutputWriter.close();
			} catch (Exception e) {
				;
			}
			try {
				templateStream.close();
			} catch (IOException e) {
				;
			}
			scanner.close();
		}

	}

	public static File getOverviewFileTxt(File rootFolder) {
		return new File(rootFolder, "overview.txt");
	}

	public static void writeOverviewTxt(String outputFolder,
			Collection<AnalysisRun> runs,
			List<Class<? extends AnalysisTool>> sortedTools) throws Exception {
		PrintWriter writer = null;
		File folder = new File(outputFolder);

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("%-60s %-8s ", "Application", "Testcase"));
		
		
		for (Class<? extends AnalysisTool> toolClass : sortedTools) {
			sb.append(String.format("  %11s", toolClass.newInstance().getName()));
		}

        sb.append(System.getProperty("line.separator"));
		sb.append(StringUtil.generateCharacters(109, '='));
        sb.append(System.getProperty("line.separator"));

		for (AnalysisRun run : runs) {
			sb.append(run.buildOverviewFragmentTxt(folder, sortedTools));
		}

		System.err.println(sb.toString());

		try {
			writer = new PrintWriter(getOverviewFileTxt(folder));
			writer.write(sb.toString());
		} finally {
			writer.close();
		}
	}
}
