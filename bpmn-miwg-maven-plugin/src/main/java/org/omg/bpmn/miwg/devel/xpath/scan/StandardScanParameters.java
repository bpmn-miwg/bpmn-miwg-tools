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

package org.omg.bpmn.miwg.devel.xpath.scan;

import java.io.File;
import java.io.IOException;

public class StandardScanParameters implements ScanParameters {

	protected String applicationName;
	protected String testResultName;
	private String inputRoot;
	private String outputRoot;

	public StandardScanParameters(String application, String testResult) {
		this(application, testResult, "../../bpmn-miwg-test-suite",
				"../../XPathOutput");
	}

	public StandardScanParameters(String application, String testResult,
			String inputRoot, String outputRoot) {
		this.applicationName = application;
		this.testResultName = testResult;
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
	}

	public File getInputRoot() throws IOException {
		String s = new File(inputRoot).getCanonicalPath();
		return new File(s);
	}

	public File getOutputRoot() throws IOException {
		String s = new File(outputRoot).getCanonicalPath();
		return new File(s);
	}

	public boolean acceptApplication(Application application) {
		if (applicationName == null)
			return true;
		else
			return application.name.toLowerCase().equals(
					applicationName.toLowerCase());
	}

	public boolean acceptTestResult(TestResult testResult) {
		if (testResultName == null)
			return true;
		else
			return testResult.name.toLowerCase().startsWith(
					testResultName.toLowerCase());
	}

}
