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

package org.omg.bpmn.miwg.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.xsd.XsdAnalysisTool;

public class XSDTest {

	private static final String RPT_FOLDER = "target";

	@Test
	public void testSchemaInvalidBpmn() {
		XsdAnalysisTool schemaValidator = new XsdAnalysisTool();
		InputStream actualBpmnXml = null;
		
		AnalysisJob job = new AnalysisJob("Custom", "A.1.0", MIWGVariant.Roundtrip, null, null);
		
		try {
			actualBpmnXml = getClass().getResourceAsStream(
					"/Schema Invalid/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", actualBpmnXml);

			AnalysisResult result = schemaValidator.analyzeStream(job, null,
					actualBpmnXml, new File(RPT_FOLDER));
			// Any number of validation errors are reported as a single finding
			assertEquals(2, result.numFindings);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				actualBpmnXml.close();
			} catch (Exception e) {
				;
			}
		}
	}

	@Test
	public void testSchemaValidBpmn() {
		XsdAnalysisTool schemaValidator = new XsdAnalysisTool();
		InputStream actualBpmnXml = null;
		AnalysisJob job = new AnalysisJob("Custom", "A.1.0", MIWGVariant.Roundtrip, null, null);
		try {
			actualBpmnXml = getClass().getResourceAsStream(
					"/Schema Valid/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", actualBpmnXml);

			AnalysisResult result = schemaValidator.analyzeStream(job, null,
					actualBpmnXml, null);
			// Any number of validation errors are reported as a single finding
			assertEquals(0, result.numFindings);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				actualBpmnXml.close();
			} catch (Exception e) {
				;
			}
		}
	}
	
	@Test
	public void testOutputPOJOs() {
		XsdAnalysisTool schemaValidator = new XsdAnalysisTool();
		InputStream actualBpmnXml = null;
		AnalysisJob job = new AnalysisJob("Custom", "A.1.0", MIWGVariant.Roundtrip, null, null);

		try {
			actualBpmnXml = getClass().getResourceAsStream(
					"/Schema Valid/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", actualBpmnXml);

			AnalysisResult result = schemaValidator.analyzeStream(job, null,
					actualBpmnXml, null);

			// Ensure there are output POJOs
			assertFalse(result.output.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				actualBpmnXml.close();
			} catch (Exception e) {
				;
			}
		}
	}

}