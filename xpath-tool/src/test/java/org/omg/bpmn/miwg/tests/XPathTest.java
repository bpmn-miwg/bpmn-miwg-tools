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
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.w3c.dom.Document;

public class XPathTest {

	private static final String RPT_FOLDER = "target";


	@Test
	public void testYaoqiang3_with_A_1_0_roundtrip_Error() {
		XPathAnalysisTool xPathTestTool = new XPathAnalysisTool();
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream(
					"/Yaoqiang BPMN Editor 3.0.1 Error/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", inputStream);
			
			
			Document document = DOMFactory.getDocument(inputStream);
			
			AnalysisJob job = new AnalysisJob();
			job.FullApplicationName = "Yaoqiang BPMN Editor 3.0.1 Error";
			job.MIWGTestCase = "A.1.0";
			job.Variant = MIWGVariant.Roundtrip;
			
					
					
			AnalysisResult result = xPathTestTool
					.analyzeDOM(job,  null, document, new File(RPT_FOLDER));
			assertEquals(1, result.numFindings);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				;
			}
		}
	}
	
	@Test
	public void testYaoqiang3_with_A_1_0_roundtrip_Correct() {
		XPathAnalysisTool xPathTestTool = new XPathAnalysisTool();
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream(
					"/Yaoqiang BPMN Editor 3.0.1 Correct/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", inputStream);
			
			
			Document document = DOMFactory.getDocument(inputStream);
			
			AnalysisJob job = new AnalysisJob();
			job.FullApplicationName = "Yaoqiang BPMN Editor 3.0.1 Error";
			job.MIWGTestCase = "A.1.0";
			job.Variant = MIWGVariant.Roundtrip;
			
					
					
			AnalysisResult result = xPathTestTool
					.analyzeDOM(job,  null, document, new File(RPT_FOLDER));
			assertEquals(0, result.numFindings);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				;
			}
		}
	}
	
	
	@Test
	public void testYaoqiang3_with_A_1_0_roundtrip_Correct_NoLog() {
		XPathAnalysisTool xPathTestTool = new XPathAnalysisTool();
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream(
					"/Yaoqiang BPMN Editor 3.0.1 Correct/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", inputStream);
			
			
			Document document = DOMFactory.getDocument(inputStream);
			
			AnalysisJob job = new AnalysisJob();
			job.FullApplicationName = "Yaoqiang BPMN Editor 3.0.1 Error";
			job.MIWGTestCase = "A.1.0";
			job.Variant = MIWGVariant.Roundtrip;
			
					
					
			AnalysisResult result = xPathTestTool
					.analyzeDOM(job,  null, document, null);
			assertEquals(0, result.numFindings);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				;
			}
		}
	}
	
	
	@Test
	public void testOutputPOJOs() {
		XPathAnalysisTool xPathTestTool = new XPathAnalysisTool();
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream(
					"/Yaoqiang BPMN Editor 3.0.1 Correct/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", inputStream);
			
			
			Document document = DOMFactory.getDocument(inputStream);
			
			AnalysisJob job = new AnalysisJob();
			job.FullApplicationName = "Yaoqiang BPMN Editor 3.0.1 Error";
			job.MIWGTestCase = "A.1.0";
			job.Variant = MIWGVariant.Roundtrip;
			
					
					
			AnalysisResult result = xPathTestTool
					.analyzeDOM(job,  null, document, null);


			assertFalse(result.output.isEmpty());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				;
			}
		}
	}
	

}