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

import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.Consts;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.tools.DOMAnalysisTool;
import org.omg.bpmn.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.w3c.dom.Document;

/**
 * This test case applies all tools to the reference files.
 * 
 * @author matthias
 *
 */

public class Reference_C_3_0_XPath_Test {

	private static final String REFERENCE_RESOURCE = "/" + Consts.REFERENCE_DIR
			+ "/C.3.0.bpmn";

	private static Document referenceDOM;

	@BeforeClass
	public static void setUpOnce() throws Exception {
		referenceDOM = DOMFactory.getDocument(REFERENCE_RESOURCE);
	}



	@Test
	public void testReference_XPath() throws Exception {
		DOMAnalysisTool xpathTool = new XPathAnalysisTool();

		AnalysisJob job = new AnalysisJob(Consts.REFERENCE_DIR, "C.3.0",
				MIWGVariant.Reference, null, null);

		assertEquals(
				0,
				xpathTool.analyzeDOM(job, referenceDOM, referenceDOM, null).numFindings);
	}
	
	
}