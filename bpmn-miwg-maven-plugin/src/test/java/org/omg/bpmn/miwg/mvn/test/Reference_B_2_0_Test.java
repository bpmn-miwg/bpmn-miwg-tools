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

package org.omg.bpmn.miwg.mvn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.bpm.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.XmlCompareAnalysisTool;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.DOMAnalysisTool;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.StreamAnalysisTool;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool2;
import org.omg.bpmn.miwg.xsd.XSDAnalysisTool;
import org.w3c.dom.Document;

/**
 * This test case applies all tools to the reference files.
 * 
 * @author matthias
 *
 */

public class Reference_B_2_0_Test {

	private static final String TESTRESULTS_FOLDER = "../../bpmn-miwg-test-suite";
	private static final String REFERENCE_FILE = TESTRESULTS_FOLDER
			+ "/Reference/B.2.0.bpmn";

	private InputStream referenceStream;
	private Document referenceDOM;

	@Before
	public void setUp() throws Exception {
		assertTrue("Test results directory cannot be found.", new File(
				TESTRESULTS_FOLDER).exists());
		assertTrue("Reference file appears not to exist", new File(
				REFERENCE_FILE).exists());
		referenceDOM = DOMFactory.getDocument(REFERENCE_FILE);
		referenceStream = new FileInputStream(REFERENCE_FILE); // Execute a second time in order to have an open stream
	}

	@After
	public void dearDown() throws Exception {
		if (referenceStream == null)
			referenceStream.close();
		referenceDOM = null;
	}

	@Test
	public void testReference_XSD() throws Exception {
		StreamAnalysisTool xsdTool = new XSDAnalysisTool();

		AnalysisJob job = new AnalysisJob();
		job.FullApplicationName = "Reference";
		job.MIWGTestCase = "B.2.0";
		job.Variant = MIWGVariant.Reference;

		assertEquals(0, xsdTool.analyzeStream(job, null, referenceStream, null).numFindings);
	}

	@Test
	public void testReference_XPath() throws Exception {
		DOMAnalysisTool xpathTool = new XPathAnalysisTool2();

		AnalysisJob job = new AnalysisJob();
		job.FullApplicationName = "Reference";
		job.MIWGTestCase = "B.2.0";
		job.Variant = MIWGVariant.Reference;

		assertEquals(0, xpathTool.analyzeDOM(job, null, referenceDOM, null).numFindings);
	}

	@Test
	public void testReference_XMLCompare() throws Exception {
		DOMAnalysisTool compareTool = new XmlCompareAnalysisTool();

		AnalysisJob job = new AnalysisJob();
		job.FullApplicationName = "Reference";
		job.MIWGTestCase = "B.2.0";
		job.Variant = MIWGVariant.Reference;

		assertEquals(0, compareTool.analyzeDOM(job, referenceDOM, referenceDOM, null).numFindings);
	}

}