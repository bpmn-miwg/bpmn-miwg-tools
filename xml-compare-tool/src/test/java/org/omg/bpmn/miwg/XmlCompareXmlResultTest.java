/*
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
package org.omg.bpmn.miwg;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omg.bpmn.miwg.testresult.FileResult;
import org.omg.bpmn.miwg.testresult.IndexWriter;
import org.xml.sax.SAXException;

/**
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@RunWith(value = Parameterized.class)
public class XmlCompareXmlResultTest {

    private static final String S = File.separator;
    private static final String RESOURCE_BASE_DIR = "." + S + "target" + S
            + "test-suite" + S;

    private static final String RPT_DIR = "target" + S + "site";
    private static final String REFERENCE_FOLDER_NAME = "Reference";

    private static File baseDir;
    private static List<FileResult> results = new ArrayList<FileResult>();

    private String tool;

    /**
     * Several tests are run for each reference model. Valid values are
     * "import", "-export", "-roundtrip".
     */
    private Variant variant;
    
    public XmlCompareXmlResultTest(String tool, TestCategory testCategory,
            Variant variant) {
        this.tool = tool;
        this.variant = variant;
    }

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        setUp();
        ArrayList<Object[]> tools = new ArrayList<Object[]>();
        for (String dir : baseDir.list()) {
            if (!dir.equals("Reference") && !dir.startsWith(".")) {
                    for (Variant v : Variant.values()) {
                        tools.add(new Object[] { dir, null, v });
                    }
            }
        }
        return tools;
    }

    public static void setUp() throws Exception {
        baseDir = new File(RESOURCE_BASE_DIR);
        System.out.println("Looking for MIWG resources in: "
                + baseDir.getAbsolutePath());

        // Check resources availability
        assertTrue("Dir does not exist at " + baseDir.getAbsolutePath()
                + ", please ensure you have checked out the MIWG resources.",
                baseDir.exists());

        new File(RPT_DIR).mkdirs();
    }

    @AfterClass
    public static void tearDown() {
        File idx = new File(RPT_DIR, "overview.html");
        System.out.println("writing index to " + idx);
        IndexWriter.write2(XmlCompareXmlResultTest.class.getSimpleName(), idx,
                results);
    }

    /**
     * 
     * @param tool
     *            The name of the tool folder
     * @param testCategory
     *            The name test category folder e.g.
     *            "A - Fixed Digrams with Variations of Attributes"
     * @param result
     *            The test result xml structure
     * @throws IOException
     */
    private void reportTestResult(String result) throws IOException {
        final File f = new File(RPT_DIR, getFileName());
        System.out.println("writing report to: " + f.getAbsolutePath());
        FileUtils.writeStringToFile(f, result);
        results.add(new FileResult() {

            public String buildHtml() {
                StringBuilder builder = new StringBuilder();

                builder.append("\t<div class=\"test\">");
                builder.append("<a href=\"");
                builder.append(f.getName());
                builder.append("\">");
                builder.append(f.getName());
                builder.append("</a>");
                builder.append("</div>\n");

                return builder.toString();
            }

        });
    }

    private String getName() {
        return getShortName();
    }

    private String getShortName() {
        return tool + "-" + variant;
    }

    private String getFileName() {
        return getName() + ".html";
    }

    private File getReferenceFolder() {
        return new File(baseDir, REFERENCE_FOLDER_NAME);
    }

    private File getToolFolder() {
        return new File(baseDir, tool);
    }

    @Test
    public void testXmlCompare() throws ParserConfigurationException,
            SAXException, IOException {

        try {
            String result = XmlCompareAnalysisTool.runXmlCompareTest(getReferenceFolder()
                    .getAbsolutePath(), getToolFolder().getAbsolutePath(),
                    variant, null);
            reportTestResult(result);
        } catch (Exception e) {
            reportTestResult(e.getMessage());
        }
    }

}
