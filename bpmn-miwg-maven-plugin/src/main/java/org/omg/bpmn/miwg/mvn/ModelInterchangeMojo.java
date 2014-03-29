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
package org.omg.bpmn.miwg.mvn;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.custommonkey.xmlunit.Difference;
import org.omg.bpmn.miwg.TestRunner;
import org.omg.bpmn.miwg.Variant;
import org.omg.bpmn.miwg.bpmn2_0.comparison.Bpmn20ConformanceChecker;
import org.omg.bpmn.miwg.bpmn2_0.comparison.Bpmn20DiffConfiguration;
import org.omg.bpmn.miwg.configuration.BpmnCompareConfiguration;
import org.omg.bpmn.miwg.testresult.IndexWriter;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.testresult.OutputType;
import org.omg.bpmn.miwg.testresult.Test;
import org.omg.bpmn.miwg.testresult.TestResults;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestManager;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Goal which scans project for BPMN files and tests them for interoperability.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ModelInterchangeMojo extends AbstractMojo {
	protected static final String SUITE_B = "/B - Validate that tool covers conformance class set/";

	protected static final String SUITE_A = "/A - Fixed Digrams with Variations of Attributes/";

	private static final String XML_COMPARE_TOOL_ID = "xml-compare";

	private static final String XPATH_TOOL_ID = "xpath";

	/**
	 * Project instance, needed for attaching the buildinfo file. Used to add
	 * new source directory to the build.
	 * 
	 */
	@Parameter(defaultValue = "${project}", required = true)
	protected MavenProject project;

	/**
	 * Application under test.
	 */
	@Parameter(defaultValue = "${project.bpmn.application}", property = "application", required = false)
	protected String application;

	/**
	 * Location to output interoperability results.
	 */
	@Parameter(defaultValue = "${project.reporting.outputDirectory}", property = "outputDir", required = true)
	protected File outputDirectory;

	/**
	 * The list of resource definitions to be included in the project jar. List
	 * of Resource objects for the current build, containing directory,
	 * includes, and excludes.
	 * 
	 */
	@Parameter(defaultValue = "${project.resources}", readonly = true, required = true)
	protected List<Resource> resources;

	protected FilenameFilter bpmnFileFilter = new BpmnFileFilter();

	protected FileFilter dirFilter = new DirFilter();

	protected TestManager manager = new TestManager();

	private static Map<String, org.omg.bpmn.miwg.testresult.FileResult> testsRun = new HashMap<String, org.omg.bpmn.miwg.testresult.FileResult>();

	public void execute() throws MojoExecutionException {
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		for (String app : getApplications()) {
			getLog().info("Running test suite for " + app);
			if (resources != null && !resources.isEmpty()) {
				for (Resource r : resources) {
					File dir = new File(r.getDirectory());
					List<File> bpmnFiles = new ArrayList<File>();

					getLog().debug(
							"Scanning for BPMN files in " + r.getDirectory());
					scanForBpmn(dir, bpmnFiles, app);
					getLog().info(
							"Found " + bpmnFiles.size() + " files to test.");

					for (File b : bpmnFiles) {
						String testName = inferTestName(b);
						InputStream refStream = findReference(testName, b);
						if (refStream != null) {
							runXPathTests(app, testName, dir.getAbsolutePath(),
									b);
							runXmlCompareTests(app, testName, b, refStream);
						} else {
							getLog().warn(
									"No Reference file found for: "
											+ b.getAbsolutePath());
						}
					}
				}
			}
		}

		File idx = new File(outputDirectory, "overview.html");
		getLog().info(
				"Writing " + testsRun.size() + " results to "
						+ idx.getAbsolutePath());
		IndexWriter.write2("ignored", idx, testsRun.values());
	}

	protected List<String> getApplications() {
		if (application == null) {
			JsonReader jsonReader = Json.createReader(getClass()
					.getResourceAsStream("/tools-tested-by-miwg.json"));
			JsonObject jsonObject = jsonReader.readObject();
			JsonArray toolArray = jsonObject.getJsonArray("tools");

			List<String> list = new ArrayList<String>();
			for (JsonValue tool : toolArray) {
				JsonObject obj = (JsonObject) tool;
				if ("true".equalsIgnoreCase(obj.get("testResultsSubmitted")
						.toString())
						|| "\"true\"".equalsIgnoreCase(obj.get(
								"testResultsSubmitted").toString())
						|| "\"partial\"".equalsIgnoreCase(obj.get(
								"testResultsSubmitted").toString())) {
					list.add(obj.getString("tool") + " "
							+ obj.getString("version"));
				}
			}

			return list;
		} else {
			return Collections.singletonList(application);
		}
	}

	/**
	 * 
	 * @param b
	 *            BPMN file.
	 * @return The BPMN-MIWG test this file is a submission for based on naming
	 *         convention.
	 */
	protected String inferTestName(File b) {
		return b.getName().replace("-export", "").replace("-roundtrip", "");
	}

	protected InputStream findReference(String testName, File b) {
		getLog().info("Checking for reference file to match: " + b);
		StringBuilder ref = new StringBuilder();
		String refDir = inferSuite(b) + "Reference/";
		ref.append(refDir).append(testName);
		InputStream refStream = getClass().getResourceAsStream(ref.toString());
		getLog().info("... found? " + (refStream != null));
		return refStream;
	}

	protected String inferSuite(File b) {
		if (b.getName().startsWith("A")) {
			return SUITE_A;
		} else if (b.getName().startsWith("B")) {
			return SUITE_B;
		} else {
			getLog().warn("File is not part of the BPMN MIWG test suite: " + b);
			return "";
		}
	}

	protected void runXmlCompareTests(final String app, final String testName,
			final File b, final InputStream refStream) {
		getLog().info(
				String.format("Running xml-compare test %2$s for %1$s on %3$s",
						app, testName, b));

		InputStream testStream = null;
		try {
			Variant variant = null;
			if (b.getName().contains(Variant.roundtrip.toString())) {
				variant = Variant.roundtrip;
			} else if (b.getName().contains(Variant.export.toString())) {
				variant = Variant.export;
			} else {
				getLog().error(
						"Looks like this BPMN does not have the expected naming convention: "
								+ b);
			}

			BpmnCompareConfiguration conf = BpmnCompareConfiguration
					.loadConfiguration(null);
			Bpmn20DiffConfiguration.setConf(conf);
			Bpmn20ConformanceChecker checker = new Bpmn20ConformanceChecker();

			TestResults results = new TestResults();
			testStream = new FileInputStream(b);
			if (testStream == null) {
				getLog().error("Cannot find test file: " + b);
			}
			Test test = results.addTool(app).addTest(testName);

			List<Difference> diffs = checker.getSignificantDifferences(
					refStream, testStream);
			for (Difference diff : diffs) {
				Output output = new Output(OutputType.finding,
						TestRunner.describeDifference(diff));
				output.setDescription(String.format(
						"Difference found in %1$s (id:%2$s)",
						diff.getDescription(), diff.getId()));
				test.addOutput(output);
			}

			reportXmlCompareResult(results.toString(), b.getName(), app);

			// get the xpath results that are already added and augment them
			FileResult result = (FileResult) testsRun.get(app + "-" + testName);
			if (result == null) {
				getLog().error(
						"Cannot find result for '" + app + "-" + testName
								+ "' in " + testsRun);

				getLog().error("XXX " + test.getName());
			} else {
				result.setXmlCompareDiffCount(diffs.size());
			}

		} catch (Exception e) {
			reportXmlCompareResult(e.getMessage(), b.getName(), app);
		}

	}

	private void reportXmlCompareResult(String result, String sourceFile,
			String app) {
		final File f = new File(new File(outputDirectory, XML_COMPARE_TOOL_ID),
				app + "-" + sourceFile.substring(0, sourceFile.length() - 5)
						+ ".html");
		getLog().debug("writing xml-compare report to: " + f.getAbsolutePath());
		try {
			FileUtils.writeStringToFile(f, result);
		} catch (IOException e) {
			getLog().error("Problem writing results to: " + f);
		}

	}

	protected void scanForBpmn(final File dir, final List<File> bpmnFiles) {
		// first search for bpmn files in this dir...
		bpmnFiles.addAll(Arrays.asList(dir.listFiles(bpmnFileFilter)));

		// ... then recurse child dirs
		File[] dirs = dir.listFiles(dirFilter);
		for (File d2 : dirs) {
			scanForBpmn(d2, bpmnFiles);
		}
	}

	protected void scanForBpmn(final File dir, final List<File> bpmnFiles,
			final String vendorId) {
		scanForBpmn(dir, bpmnFiles);
		if (vendorId != null && vendorId.length() > 0) {
			List<File> nonMatchingFiles = new ArrayList<File>();
			for (File bpmnFile : bpmnFiles) {
				if (!bpmnFile.getAbsolutePath().contains(vendorId)) {
					nonMatchingFiles.add(bpmnFile);
				}
			}
			if (nonMatchingFiles.size() > 0) {
				getLog().warn(
						String.format(
								"The following BPMN files are not in a directory named '%1$s' so will be ignored: %2$s",
								vendorId, nonMatchingFiles));
			}
			bpmnFiles.removeAll(nonMatchingFiles);
		}
	}

	/**
	 * Run the XPath assertions tests.
	 * 
	 * @param app
	 *            The name of the tool under test. Much match the folder name in
	 *            the test suite submitted for each test group under 'A...',
	 *            'B...' etc
	 * @param testName
	 *            Name of specific test to run, e.g. A.1.0.
	 * @param root
	 *            Base folder of test suite.
	 * @param bpmnRefFile
	 *            The BPMN reference file for this test.
	 */
	protected void runXPathTests(final String app, final String testName,
			final String root, final File bpmnRefFile) {
		getLog().info(
				String.format("Running xpath test %2$s for %1$s on %3$s", app,
						testName, bpmnRefFile));
		TestInstance instance = new TestInstance(root + File.separator
				+ inferSuite(bpmnRefFile), app, bpmnRefFile.getName());

		TestOutput out;
		try {
			// TODO add constructor that takes file rather than changing to
			// string
			String outputPath = outputDirectory.getAbsolutePath()
					+ File.separator + XPATH_TOOL_ID;
			out = new TestOutput(instance, outputPath);
			manager.executeTests(instance, outputPath);
			out.close();

			getLog().debug("Adding test to overview: " + app + "-" + testName);
			testsRun.put(app + "-" + testName, new FileResult(out, instance));
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
		}
	}

	protected class FileResult implements
			org.omg.bpmn.miwg.testresult.FileResult {

		private TestOutput xpathOutput;
		private TestInstance xpathInstance;
		private int diffs;

		public FileResult(TestOutput out, TestInstance instance) {
			this.xpathOutput = out;
			this.xpathInstance = instance;
		}

		public void setXmlCompareDiffCount(int size) {
			this.diffs = size;
		}

		public String buildHtml() {
			StringBuilder builder = new StringBuilder();

			builder.append("\t<div class=\"test\" data-diffs=\"" + diffs
					+ "\" data-findings=\"" + xpathInstance.getFindings()
					+ "\" data-ok=\"" + xpathInstance.getOKs() + "\">");
			builder.append("<a href=\"" + xpathOutput.getName() + ".txt\">"
					+ xpathOutput.getName() + "</a>");
			builder.append("</div>\n");

			return builder.toString();
		}

	}

	protected class BpmnFileFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			if (name.toLowerCase().endsWith(".bpmn")) {
				getLog().info(
						"Found BPMN file: " + dir.getAbsolutePath()
								+ File.separator + name);
				return true;
			}
			getLog().debug("Ignoring: " + name);
			return false;
		}
	};

	protected class DirFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	}
}
