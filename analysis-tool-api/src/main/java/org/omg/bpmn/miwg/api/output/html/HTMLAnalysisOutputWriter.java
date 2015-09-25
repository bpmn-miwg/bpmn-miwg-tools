package org.omg.bpmn.miwg.api.output.html;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class HTMLAnalysisOutputWriter {

	public static File getOverviewFile(File rootFolder) {
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

			templateStream = HTMLAnalysisOutputWriter.class
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

	public static void writeOverview(String outputFolder,
			Collection<AnalysisRun> runs) throws Exception {
		PrintWriter htmlOutputWriter = null;
		InputStream templateStream = null;
		Scanner scanner = null;
		File folder = new File(outputFolder);
		try {

			templateStream = HTMLAnalysisOutputWriter.class
					.getResourceAsStream("/AnalysisResult.Overview.Template.html");

			scanner = new Scanner(templateStream, "UTF-8");
			String template = scanner.useDelimiter("\\A").next();

			StringBuilder sb = new StringBuilder();

			for (AnalysisRun run : runs) {
				sb.append(run.buildOverviewHTML(folder));
			}

			String overviewString = sb.toString();

			String completeString = template.replace("{OVERVIEW}",
					overviewString);

			htmlOutputWriter = new PrintWriter(getOverviewFile(folder));

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

}
