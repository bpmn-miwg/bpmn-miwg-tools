package org.omg.bpmn.miwg.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

import org.omg.bpmn.miwg.HtmlOutput.Pojos.AnalysisOutputFragment;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.tools.AnalysisTool;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class HTMLAnalysisOutputWriter {

	public static File getOverviewFile(File rootFolder) {
		return new File(rootFolder, "overview.html");
	}

	public static void writeAnalysisResults(File rootFolder, AnalysisJob job,
			AnalysisTool tool, AnalysisResult result) throws Exception {
		PrintWriter htmlOutputWriter = null;
		InputStream templateStream = null;
		Scanner scanner = null;
		try {
			// Build Simple XML POJO
			AnalysisOutputFragment root = new AnalysisOutputFragment();
			root.output = result.output;

			// Build a string containing the HTML fragment representing the
			// analysis results
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Serializer serializer = new Persister();
			serializer.write(root, outputStream);
			String outputString = outputStream.toString();

			templateStream = HTMLAnalysisOutputWriter.class
					.getResourceAsStream("/AnalysisResult.SingleJob.Template.html");

			scanner = new Scanner(templateStream, "UTF-8");
			String template = scanner.useDelimiter("\\A").next();

			String completeString = template.replace("{ANALYSISRESULTS}",
					outputString);

			htmlOutputWriter = new PrintWriter(result.getHTMLResultsFile(rootFolder, job));
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

	public static void writeOverview(File folder, Collection<AnalysisRun> runs)
			throws Exception {
		PrintWriter htmlOutputWriter = null;
		InputStream templateStream = null;
		Scanner scanner = null;
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
