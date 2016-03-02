package org.omg.bpmn.miwg.cli;

import java.io.File;
import java.util.Arrays;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.ReferenceNotFoundException;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.FileAnalysisInput;
import org.omg.bpmn.miwg.facade.AnalysisFacade;
import org.omg.bpmn.miwg.facade.scan.BpmnFileScanner;

public class CLI2 {

	private static void printUsage(String[] args) {
		System.out
				.println("Please specify the file name using the schema application-testcase-variant.bpmn");
		System.out
				.println("Given: " + Arrays.toString(args));
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			printUsage(args);
			return;
		}
		String inputFileName = args[0];
		File inputFile = new File(inputFileName);

		String[] parts = inputFile.getName().split("-");
		if (parts.length != 3) {
			printUsage(args);
			return;
		}

		String applicationName = parts[0];
		String testCaseName = parts[1];
		String variantName = parts[2];


		if (!inputFile.isFile()) {
			System.out.println(String.format("There is no file %s",
					inputFileName));
			return;
		}

		String testCase = testCaseName.toUpperCase();
		Variant variant = BpmnFileScanner.inferMiwgVariant(variantName);

		System.out.println(String.format("Input      : %s", inputFileName));
		System.out.println(String.format("Application: %s", applicationName));
		System.out.println(String.format("Test Case  : %s", testCaseName));
		System.out
				.println(String.format("Variant    : %s", variant.toString()));

		try {
			AnalysisJob job = new AnalysisJob(applicationName, testCase,
					variant, new FileAnalysisInput(inputFile));
			job.disableXmlCompare();
			job.disableReportWriting();
			AnalysisFacade.executeAnalysisJob(job);

		} catch (ReferenceNotFoundException e) {
			System.out.println(String.format(
					"The reference file could not be found for test case %s",
					testCaseName));
		} catch (Exception e) {
			System.out
					.println(String.format("Error occured: %s", e.toString()));
			e.printStackTrace();
		}

		return;
	}
}
