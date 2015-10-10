/**
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
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

package org.omg.bpmn.miwg.scan;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class StandardScanParameters implements ScanParameters {

	protected String applicationName;
	protected String testResultName;
	private String outputRoot;
	private Collection<String> inputRoots;
	private final static Set<String> SUPPORTED_TESTCASES = new HashSet<String>(
			Arrays.asList(new String[] { "A.1.0", "A.2.0", "A.3.0", "A.4.0",
					"A.4.1", "B.1.0", "B.2.0", "C.1.0", "C.1.1", "C.2.0",
					"C.3.0" }));

	public StandardScanParameters(String application, String testResult) {
		this(application, testResult, Collections
				.singletonList("../../bpmn-miwg-test-suite"),
				"../../XPathOutput");
	}

	public StandardScanParameters(String application, String testResult,
			Collection<String> inputRoots, String outputRoot) {
		this.applicationName = application;
		this.testResultName = testResult;
		this.inputRoots = inputRoots;
		this.outputRoot = outputRoot;
	}

	public StandardScanParameters(String application, String testResult,
			String inputRoot, String outputRoot) {
		this(application, testResult, Collections.singletonList(inputRoot),
				outputRoot);
	}

	public Collection<File> getInputRoots() throws IOException {
		Collection<File> inputFolders = new LinkedList<File>();
		for (String folderName : inputRoots) {
			File f = new File(folderName).getCanonicalFile();
			inputFolders.add(f);
		}
		return inputFolders;
	}

	public File getOutputRoot() throws IOException {
		String s = new File(outputRoot).getCanonicalPath();
		return new File(s);
	}

	public boolean acceptApplication(Application application) {
		if (applicationName == null)
			return true;
		else
			return application.fullName.toLowerCase().equals(
					applicationName.toLowerCase());
	}

	public boolean acceptTestResult(TestResult testResult) {
		if (testResultName == null) {
			String testName = BpmnFileScanner.inferTestName(testResult.file)
					.toUpperCase();
			return SUPPORTED_TESTCASES.contains(testName);
		}
		else
			return testResult.name.toLowerCase().startsWith(
					testResultName.toLowerCase());
	}

}
