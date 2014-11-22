package org.omg.bpmn.miwg.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.testresult.AnalysisOutputFragment;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class HTMLAnalysisOutputWriter {

	public static File getOutputFile(File folder, AnalysisJob job) {
		return new File(folder, job.getName() + ".html");
	}

	public static void writeOutput(File folder, AnalysisJob job,
			AnalysisResult result) throws Exception {
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
					.getResourceAsStream("/AnalysisTool.HTMLOutput.Template.html");

			scanner = new Scanner(templateStream, "UTF-8");
			String template = scanner.useDelimiter("\\A").next();

			String completeString = template.replace("{ANALYSISRESULTS}",
					outputString);

			htmlOutputWriter = new PrintWriter(getOutputFile(folder, job));

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
