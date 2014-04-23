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
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.omg.bpmn.miwg.TestRunner;
import org.omg.bpmn.miwg.Variant;
import org.omg.bpmn.miwg.api.TestTool;
import org.omg.bpmn.miwg.input.BpmnFileFilter;
import org.omg.bpmn.miwg.input.DirFilter;
import org.omg.bpmn.miwg.testresult.IndexWriter;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.testresult.OutputType;
import org.omg.bpmn.miwg.testresult.Test;
import org.omg.bpmn.miwg.testresult.TestResults;
import org.omg.bpmn.miwg.xpathTestRunner.XPathTestTool;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestInstance;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestManager;
import org.omg.bpmn.miwg.xpathTestRunner.base.TestOutput;

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
                            runTestTool(new XPathTestTool(testName,
                                    outputDirectory.getAbsolutePath()), app,
                                    testName, dir.getAbsolutePath(), b,
                                    refStream);
                            // TODO optimise to not read file twice
                            refStream = findReference(testName, b);
                            runTestTool(new TestRunner(), app, testName,
                                    dir.getAbsolutePath(), b, refStream);
                        }
                    }
                }
            }
        }

        File idx = new File(outputDirectory, "overview.html");
        getLog().info("writing test overview to " + idx.getAbsolutePath());
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
        return b.getName()
                .substring(0, b.getName().toLowerCase().indexOf(".bpmn"))
                .replace("-export", "").replace("-roundtrip", "");
    }

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

    protected void runTestTool(TestTool testTool, final String app,
            final String testName, final String toolPath, final File b,
            final InputStream refStream) {
        getLog().info(
                String.format("Running xml-compare test %2$s for %1$s on %3$s",
                        app, testName, b));

        InputStream testStream = null;
        final TestResults results = new TestResults();
        final Variant variant = inferVariant(b);
        final File f = new File(new File(new File(outputDirectory,
                testTool.getName()), app), app + "-" + testName + "-"
                + variant.name() + ".html");
        try {
            String testFile = toolPath + File.separator + app + File.separator
                    + b.getName();
            getLog().debug("xml-compare testing: " + testFile);
            testStream = new FileInputStream(testFile);
            Test test = results.addTool(app).addTest(testName, variant.name());
            Collection<? extends Output> diffs = testTool
                    .getSignificantDifferences(refStream, testStream);
            test.addAll(diffs);
            getLog().debug(
"writing test report to: " + f.getAbsolutePath());
            results.writeResultFile(f);

            String resultKey = app + "-" + testName + "-" + variant;
            FileResult result = (FileResult) testsRun.get(resultKey);
            if (result == null) {
                getLog().info(
                        "Cannot find result for '" + app + "-" + testName
                                + "' in " + testsRun + ", add new");

                result = new FileResult(resultKey);
                testsRun.put(resultKey, result);
            }
            switch (testTool.getName()) {
            case "xml-compare":
                result.diffs = diffs.size();
                break;
            case "xpath":
                result.oks = match(OutputType.ok, diffs);
                result.findings = match(OutputType.finding, diffs);
                break;
            default:
                getLog().error(
                        "Don't know how to report results of "
                                + testTool.getName());
            }

        } catch (Exception e) {
            getLog().error(e.getMessage());
            try {
                results.writeResultFile(f);
            } catch (IOException e1) {
                getLog().error(e1);
            }
        } finally {
            try {
                testStream.close();
            } catch (Exception e) {
                ;
            }
            try {
                refStream.close();
            } catch (Exception e) {
                ;
            }
        }

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

    private int match(OutputType outputType, Collection<? extends Output> diffs) {
        int count = 0;
        for (Output output : diffs) {
            if (output.getOutputType() == outputType) {
                count++;
            }
        }
        return count;
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

    /**
     * @deprecated No longer needed as have added interface to invoke both xpath
     *             and xml-compare the same way.
     */
    protected void runXPathTests(final String app, final String testName,
            final String root, final File b) {
        getLog().info(
                String.format("Running xpath test %2$s for %1$s on %3$s", app,
                        testName, b));
        TestInstance instance = new TestInstance(root + File.separator
                + inferSuitePath(b), app, b.getName());
        // TODO add constructor that takes file rather than changing to string
        TestOutput out;
        try {
            String outputPath = outputDirectory.getAbsolutePath()
                    + File.separator + XPATH_TOOL_ID;
            out = new TestOutput(instance, outputPath);
            manager.executeTests(instance, outputPath);
            out.close();
            testsRun.put(out.getName(), new FileResult(out, instance));

        } catch (IOException e) {
            getLog().error(e.getMessage(), e);
        }
    }

    protected class FileResult implements
            org.omg.bpmn.miwg.testresult.FileResult {

        protected String name;
        protected int oks;
        protected int findings;
        protected int diffs;

        public FileResult(TestOutput out, TestInstance instance) {
            this.name = out.getName();
            this.oks = instance.getOKs();
            this.findings = instance.getFindings();
        }

        public FileResult(String name) {
            this.name = name;
        }

        public void setXmlCompareDiffCount(int size) {
            this.diffs = size;
        }

        public String buildHtml() {
            StringBuilder builder = new StringBuilder();

            builder.append("\t<div class=\"test\" data-diffs=\"" + diffs
                    + "\" data-findings=\"" + findings + "\" data-ok=\"" + oks
                    + "\">");
            builder.append("<a href=\"" + name + ".html\">" + name + "</a>");
            builder.append("</div>\n");

            return builder.toString();
        }

    }

}
