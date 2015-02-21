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

package org.omg.bpmn.miwg.devel.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ScanUtil {

	public static List<Object[]> data(ScanParameters conf) throws IOException {

		InstanceParameter template = new InstanceParameter();
		template.inputRoot = conf.getInputRoot();
		template.outputRoot = conf.getOutputRoot();

		List<Application> applications = listApplications(template.inputRoot);

		List<Object[]> parameters = new LinkedList<Object[]>();

		for (Application application : applications) {

			if (conf.acceptApplication(application)) {

				List<TestResult> testResults = listTestResults(application.folder);

				for (TestResult testResult : testResults) {

					if (conf.acceptTestResult(testResult)) {

						InstanceParameter param = (InstanceParameter) template
								.clone();

						param.application = application;
						param.testResult = testResult;
						parameters.add(new Object[] { param });

					}

				}

			}

		}

		return parameters;
	}

	private static Application convertApplicationFolder(File applicationFolder) {
		Application application = new Application(applicationFolder);
		return application;
	}

	public static List<Application> listApplications(File folderInput) {

		class ApplicationDirFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						&& !pathname.getName().equals(".git");
			}
		}

		List<Application> applications = new LinkedList<Application>();
		for (File folder : folderInput.listFiles(new ApplicationDirFilter())) {
			applications.add(convertApplicationFolder(folder));
		}
		return applications;
	}

	public static List<TestResult> listTestResults(File folder) {
		class TestResultFilter implements FileFilter {
			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				String folderName = file.getParentFile().getName();
				return file.isFile()
						&& (fileName.endsWith("-roundtrip.bpmn") || (folderName
								.equals("Reference") && fileName
								.endsWith(".bpmn")));
			}
		}

		List<TestResult> testResults = new LinkedList<TestResult>();
		for (File testResultFile : folder.listFiles(new TestResultFilter())) {
			testResults.add(new TestResult(testResultFile));
		}
		return testResults;
	}
}
