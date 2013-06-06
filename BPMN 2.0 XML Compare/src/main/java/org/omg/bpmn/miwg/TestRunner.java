/**
 * The MIT License (MIT)
 * Copyright (c) 2013 Signavio, OMG BPMN Model Interchange Working Group
 *
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.omg.bpmn.miwg;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.Difference;
import org.omg.bpmn.miwg.bpmn2_0.comparison.Bpmn20ConformanceChecker;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.testresult.OutputType;
import org.omg.bpmn.miwg.testresult.Test;
import org.omg.bpmn.miwg.testresult.TestResults;
import org.xml.sax.SAXException;

public class TestRunner {

	private static final String FILE_EXTENSION = "bpmn";

	/**
	 * First argument path to folder containing the reference bpmn xml files
	 * Second argument path to folder containing the bpmn files to compare with
	 * 
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * 
	 */
	public static void main(String[] args) throws SAXException, IOException,
			ParserConfigurationException {

		System.out.println("Runing BPMN 2.0 XML Compare Test...");
		String result = runXmlCompareTest(args[0], args[1],
				Variant.valueOf(args[2]));

		if (args.length > 3) {
			File outputFile = new File(args[3]);
			FileUtils.writeStringToFile(outputFile, result);
			System.out.println("Output printed to: \n"
					+ outputFile.getAbsolutePath());
		} else {
			System.out.println(result);
		}

		System.out.println("Finished BPMN 2.0 XML Compare Test");
	}

	/**
	 * Performs the BPMN 2.0 XML Compare test and prints out the resulting XML
	 * structure
	 * 
	 * @param referenceFolderPath
	 *            Path to the folder containing the reference files
	 * @param testFolderPath
	 *            Path to the folder containing the test file of a specific tool
	 * 
	 * @param variant
	 *            Test variant either export or roundtrip
	 * 
	 * @return Outputs the XML structure
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static String runXmlCompareTest(String refFolderPath,
			String testFolderPath, Variant variant)
			throws ParserConfigurationException, SAXException, IOException {
		File refFolder = new File(refFolderPath);
		File testFolder = new File(testFolderPath);

		if (!refFolder.isDirectory() || !testFolder.isDirectory()) {
			throw new IllegalArgumentException("Invalid path to folder");
		}

		Bpmn20ConformanceChecker checker = new Bpmn20ConformanceChecker();
		TestResults results = new TestResults();

		for (File bpmnFile : refFolder.listFiles()) {
			if (isBpmnFile(bpmnFile)) {
				File compareFile = getCompareFile(bpmnFile, testFolder, variant);
				if (compareFile.exists()) {

					// Building test output structure
					Test test = results.addTool(testFolder.getName()).addTest(
							bpmnFile.getName());

					List<Difference> diffs = checker.getSignificantDifferences(
							bpmnFile, compareFile);
					for (Difference diff : diffs) {
						Output output = new Output(OutputType.finding,
								describeDifference(diff));
						test.addOutput(output);
					}
				}
			}
		}

		return results.toString();
	}

	private static String describeDifference(Difference difference) {
		StringBuffer sBuff = new StringBuffer();
		sBuff.append(difference.getDescription() + " (id:" + difference.getId()
				+ ")\n");
		sBuff.append("control: "
				+ difference.getControlNodeDetail().getXpathLocation() + ":\t");
		sBuff.append(difference.getControlNodeDetail().getValue() + "\n");
		sBuff.append("test:    "
				+ difference.getTestNodeDetail().getXpathLocation() + ":\t");
		sBuff.append(difference.getTestNodeDetail().getValue() + "\n\n");

		return sBuff.toString();
	}

	private static boolean isBpmnFile(File bpmnFile) {
		int i = bpmnFile.getName().lastIndexOf(".");
		return bpmnFile.getName().substring(i + 1).equals(FILE_EXTENSION);
	}

	private static File getCompareFile(File refFile, File testFolder,
			Variant variant) {
		int i = refFile.getName().lastIndexOf(".");
		String fName = refFile.getName().substring(0, i) + "-"
				+ variant.toString() + "." + FILE_EXTENSION;

		return new File(testFolder, fName);
	}
}
