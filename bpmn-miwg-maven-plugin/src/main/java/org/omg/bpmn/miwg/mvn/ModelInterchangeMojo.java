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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.omg.bpm.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.Variant;
import org.omg.bpmn.miwg.XmlCompareAnalysisTool;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.StreamAnalysisTool;
import org.omg.bpmn.miwg.input.BpmnFileFilter;
import org.omg.bpmn.miwg.input.DirFilter;
import org.omg.bpmn.miwg.testresult.IndexWriter;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.testresult.TestResults;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool2;
import org.omg.bpmn.miwg.xsd.XSDAnalysisTool;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Goal which scans project for BPMN files and tests them for interoperability.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ModelInterchangeMojo extends AbstractMojo {
	protected static final String SUITE_B_PATH = "/";

	protected static final String SUITE_A_PATH = "/";

	private static final String XML_COMPARE_TOOL_ID = "xml-compare";

	private static final String XPATH_TOOL_ID = "xpath";

	private static final String XSD_TOOL_ID = "xsd";

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

	private static Map<String, org.omg.bpmn.miwg.testresult.FileResult> testsRun = new HashMap<String, org.omg.bpmn.miwg.testresult.FileResult>();

	public void execute() throws MojoExecutionException {
		getLog().info("Running BPMN-MIWG test suite...");

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

					for (File b : bpmnFiles) {
						String testName = inferTestName(b);
						InputStream refStream = findReference(testName, b);
						if (refStream != null) {

							AnalysisJob job = new AnalysisJob();
							job.FullApplicationName = app;
							job.MIWGTestCase = testName;
							job.Variant = inferVariant2(b);

							AnalysisResult analysisResult = null;

							XSDAnalysisTool xsdTool = new XSDAnalysisTool();
							try {
								analysisResult = xsdTool.analyzeStream(job, null,
										new FileInputStream(b), null);
							} catch (Exception e) {
								getLog().error(e);
							}
							writeTestResult(analysisResult, xsdTool.getName(),
									job, dir.getAbsolutePath(), b);

							XPathAnalysisTool2 xpathTool = new XPathAnalysisTool2();
							try {
								analysisResult = xpathTool.analyzeDOM(job,
										null, DOMFactory.getDocument(b), null);
							} catch (Exception e) {
								getLog().error(e);
							}
							writeTestResult(analysisResult,
									xpathTool.getName(), job,
									dir.getAbsolutePath(), b);

							refStream = findReference(testName, b);
							XmlCompareAnalysisTool compareTool = new XmlCompareAnalysisTool();
							try {
								analysisResult = compareTool
										.analyzeDOM(job, DOMFactory.getDocument(refStream),
												DOMFactory.getDocument(b), null);
							} catch (Exception e) {
								getLog().error(e);
							}
							writeTestResult(analysisResult,
									compareTool.getName(), job,
									dir.getAbsolutePath(), b);

						}
					}
				}
			}
		}
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
	 *         convention. For example the export results file for the A.1.0
	 *         test will be called A.1.0-export.bpmn.
	 */
	protected String inferTestName(File b) {
		return b.getName()
				.substring(0, b.getName().toLowerCase().indexOf(".bpmn"))
				.replace("-export", "").replace("-roundtrip", "");
	}

	/**
	 * 
	 * @param testName
	 *            Test name in the form Suite.Test.Variation, for example A.1.0.
	 * @param b
	 *            The vendor BPMN file to compare to the reference.
	 * @return Stream of the reference file.
	 */
	protected InputStream findReference(String testName, File b) {
		getLog().info("Checking for reference file to match: " + b);
		StringBuilder ref = new StringBuilder();
		String refDir = inferSuitePath(b) + "Reference/";
		ref.append(refDir).append(testName).append(".bpmn");
		getLog().info("... seeking resource: " + ref.toString());
		InputStream refStream = getClass().getResourceAsStream(ref.toString());
		getLog().info("... found? " + (refStream != null));
		return refStream;
	}

	protected String inferSuitePath(File b) {
		if (b.getName().startsWith("A")) {
			return SUITE_A_PATH;
		} else if (b.getName().startsWith("B")) {
			return SUITE_B_PATH;
		} else {
			getLog().warn("File is not part of the BPMN MIWG test suite: " + b);
			return "";
		}
	}

	protected void writeTestResult(AnalysisResult analysisResult,
			String analysisToolName, AnalysisJob job, String toolPath,
			File bpmnFile) {

		final TestResults results = new TestResults();

		String app = job.FullApplicationName;
		String testName = job.MIWGTestCase;
		String variant = job.Variant.toString().toLowerCase();

		final File f = new File(new File(new File(outputDirectory,
				analysisToolName), app), app + "-" + testName + "-" + variant
				+ ".html");

		try {
			String testFile = toolPath + File.separator + app + File.separator
					+ bpmnFile.getName();

			results.addTool(app).addTest(testName, variant);

			getLog().debug("writing test report to: " + f.getAbsolutePath());
			results.writeResultFile(f);

			String resultKey = app + "-" + testName + "-" + variant;
			FileResult result = (FileResult) testsRun.get(resultKey);
			if (result == null) {
				getLog().debug(
						String.format(
								"Cannot find result for '%1$s %2$s' in %3$s, adding...",
								app, testName, testsRun));

				result = new FileResult(resultKey, analysisResult);
				testsRun.put(resultKey, result);
			} else {
				result.setAnalysisResult(analysisResult);
			}
		} catch (Exception e) {
			getLog().error(e.getMessage());
			try {
				results.writeResultFile(f);
			} catch (IOException e1) {
				getLog().error(e1);
			}
		}

	}

	protected MIWGVariant inferVariant2(File b) {
		MIWGVariant variant = null;
		if (b.getName().contains(Variant.roundtrip.toString())) {
			variant = MIWGVariant.Roundtrip;
		} else if (b.getName().contains(Variant.export.toString())) {
			variant = MIWGVariant.Export;
		} else {
			getLog().error(
					"Looks like this BPMN does not have the expected naming convention: "
							+ b);
		}
		return variant;
	}

	protected Variant inferVariant(final File b) {
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
		return variant;
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
			bpmnFiles.removeAll(nonMatchingFiles);
		}
	}

	protected class FileResult implements
			org.omg.bpmn.miwg.testresult.FileResult {

		protected String name;
		protected AnalysisResult result;

		public FileResult(String name, AnalysisResult result) {
			this.name = name;
			this.result = result;
		}

		public void setAnalysisResult(AnalysisResult result) {
			Collection<? extends Output> tmp = new ArrayList<Output>();
			Collections.addAll(tmp, this.result.output.toArray());
			Collections.addAll(tmp, result.output.toArray());
			this.result.output = tmp;
			this.result.numFindings += result.numFindings;
			this.result.numOK += result.numOK;
		}

		public String buildHtml() {
			StringBuilder builder = new StringBuilder();

			builder.append("\t<div class=\"test\" data-diffs=\""
					+ result.output.size() + "\" data-findings=\""
					+ result.numFindings + "\" data-name=\"" + name
					+ "\" data-ok=\"" + result.numOK + "\">");
			builder.append("<a href=\"" + name + ".html\">" + name + "</a>");
			builder.append("</div>\n");

			return builder.toString();
		}

	}

}
