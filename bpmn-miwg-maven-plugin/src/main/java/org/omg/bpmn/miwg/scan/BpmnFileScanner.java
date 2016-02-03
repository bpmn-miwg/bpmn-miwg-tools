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
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.ReferenceNotFoundException;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.FileAnalysisInput;

public class BpmnFileScanner {

	public static List<Object[]> data(ScanParameters conf) throws IOException,
			ReferenceNotFoundException {
		List<Object[]> parameters = new LinkedList<Object[]>();
		List<AnalysisJob> jobs = buildListOfAnalysisJobs(conf);

		for (AnalysisJob job : jobs) {
			parameters.add(new Object[] { job });
		}

		return parameters;
	}

	public static List<AnalysisJob> buildListOfAnalysisJobs(ScanParameters conf)
			throws IOException, ReferenceNotFoundException {

		List<AnalysisJob> jobs = new LinkedList<AnalysisJob>();

		List<Application> applications = listApplications(conf.getInputRoots());
		for (Application application : applications) {
			if (conf.acceptApplication(application)) {
				List<TestResult> testResults = listTestResults(application.folder);
				for (TestResult testResult : testResults) {
					if (conf.acceptTestResult(testResult)) {
						AnalysisJob job = new AnalysisJob(application.fullName,
								inferTestName(testResult.file),
								inferMiwgVariant(testResult.file),
								new FileAnalysisInput(testResult.file));
						jobs.add(job);
					}
				}
			}
		}

		return jobs;
	}

	private static Application convertApplicationFolder(File applicationFolder) {
		Application application = new Application(applicationFolder);
		return application;
	}

	public static List<Application> listApplications(
			Collection<File> inputFolders) {

		class ApplicationDirFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						&& !pathname.getName().equals(".git");
			}
		}

		List<Application> applications = new LinkedList<Application>();
		for (File inputFolder : inputFolders) {
			for (File applicationFolder : inputFolder
					.listFiles(new ApplicationDirFilter())) {
				applications.add(convertApplicationFolder(applicationFolder));
			}
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

	public static Variant inferMiwgVariant(File file) {
		String fileName = file.getName().toLowerCase();
		return inferMiwgVariant(fileName);
	}

	public static Variant inferMiwgVariant(String variantName) {
		Variant variant = null;
		if (variantName.contains(Variant.Roundtrip.toString().toLowerCase())) {
			variant = Variant.Roundtrip;
		} else if (variantName.contains(Variant.Export.toString().toLowerCase())) {
			variant = Variant.Export;
		} else {
			variant = Variant.Reference;
		}
		return variant;
	}

	public static String inferTestName(File bpmnFile) {
		String fileName = bpmnFile.getName();
		if (fileName.contains("-")) {
			return fileName.substring(0, bpmnFile.getName().indexOf('-'));
		} else {
			return fileName.substring(0, bpmnFile.getName().indexOf(".bpmn"));
		}
	}

}
