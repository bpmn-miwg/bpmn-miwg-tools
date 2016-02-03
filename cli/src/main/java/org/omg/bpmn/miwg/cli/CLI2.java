package org.omg.bpmn.miwg.cli;

import java.io.File;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.ReferenceNotFoundException;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.FileAnalysisInput;
import org.omg.bpmn.miwg.mvn.AnalysisFacade;
import org.omg.bpmn.miwg.scan.BpmnFileScanner;

public class CLI2 {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Parameters: InputFile.bpmn ApplicationName");
			return;
		}

		String inputFileName = args[0];
		String applicationName = args[1];

		File inputFile = new File(inputFileName);

		if (!inputFile.isFile()) {
			System.out.println(String.format("There is no file %s", inputFileName));
			return;
		}

		if (applicationName.isEmpty()) {
			System.out.println("No application name specified");
			return;
		}

		String testCaseName = BpmnFileScanner.inferTestName(inputFile);
		Variant variant = BpmnFileScanner.inferMiwgVariant(inputFile);

		System.out.println(String.format("Input    : %s", inputFileName));
		System.out.println(String.format("Test Case: %s", testCaseName));
		System.out.println(String.format("Variant  : %s", variant.toString()));

		try {
			AnalysisJob job = new AnalysisJob(applicationName, testCaseName,
					variant, new FileAnalysisInput(inputFile));
			job.disableXmlCompare();
			AnalysisFacade.executeAnalysisJob(job);

		} catch (ReferenceNotFoundException e) {
			System.out.println(String.format(
					"The reference file could not be found for test case %s",
					testCaseName));
		} catch (Exception e) {
			System.out
					.println(String.format("Error occured: %s", e.toString()));
		}

		return;
	}

}
