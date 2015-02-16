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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.Consts;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.input.FileAnalysisInput;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.mvn.filter.BpmnFileFilter;
import org.omg.bpmn.miwg.mvn.filter.DirFilter;
import org.omg.bpmn.miwg.xmlCompare.Variant;

/**
 * Goal which scans project for BPMN files and tests them for interoperability.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ModelInterchangeMojo extends AbstractMojo {
	    /**
     * Project instance Used to add new source directory to the build.
     */
	@Parameter(defaultValue = "${project}", required = true)
	protected MavenProject project;

	/**
	 * BPMN Application under test.
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
	
	private Collection<AnalysisRun> analysisRuns;

	public void execute() throws MojoExecutionException {
		getLog().info("Running BPMN-MIWG test suite...");

		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		AnalysisFacade analysisFacade = new AnalysisFacade(outputDirectory);
		Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
		try {
			for (String app : getApplications()) {
				getLog().info("Running test suite for " + app);
				if (resources != null && !resources.isEmpty()) {
					for (Resource r : resources) {
						File dir = new File(r.getDirectory());
						List<File> bpmnFiles = new ArrayList<File>();

						getLog().debug(
								"Scanning for BPMN files in "
										+ r.getDirectory());
						scanForBpmn(dir, bpmnFiles, app);

						for (File b : bpmnFiles) {
							AnalysisJob job;
                            try {
                                job = new AnalysisJob(app, inferTestName(b),
                                        inferMiwgVariant(b),
                                        new FileAnalysisInput(b),
                                        new ResourceAnalysisInput(getClass(),
                                                inferReference(b)));
                                jobs.add(job);
                            } catch (Throwable t) {
                                getLog().error(
                                        String.format("ERROR: %1$s, %2$s", app,
                                                b.getCanonicalPath()));
                                getLog().error(
                                        String.format("     : %1$s, %2$s", t
                                                .getClass().getName(), t
                                                .getMessage()));
                            }
						}
					}
				}
			}

			analysisRuns = analysisFacade.executeAnalysisJobs(jobs);
		} catch (Exception e) {
			e.printStackTrace();
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

	public Collection<AnalysisRun> getAnalysisRuns() {
		return analysisRuns;
	}

    protected MIWGVariant inferMiwgVariant(File b) {
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

    protected String inferTestName(File bpmnFile) {
        return bpmnFile.getName().substring(0, bpmnFile.getName().indexOf('-'));
    }

    protected String inferReference(File bpmnFile) {
        return "/" + Consts.REFERENCE_DIR + "/" + inferTestName(bpmnFile)
                + Consts.BPMN_FILE_EXTENSION;
    }
}
