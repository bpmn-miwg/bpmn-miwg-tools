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

package org.omg.bpmn.miwg.test.references;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.Consts;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.mvn.AnalysisFacade;
import org.omg.bpmn.miwg.xpath.XpathAnalysisTool;

/**
 * This test case applies all tools to the reference files.
 * 
 * @author matthias
 *
 */

public class Reference_C_1_1_XPath_Test {

	private static final String REFERENCE_RESOURCE = "/Reference/C.1.1.bpmn";
	private static final String TEST_RESOURCE = "/Reference/C.1.1-Removed-Extension-Elements.bpmn";

	@Test
	public void testIdentical() throws Exception {
		AnalysisJob job = new AnalysisJob(Consts.REFERENCE_DIR, "C.1.1",
				Variant.Reference, new ResourceAnalysisInput(getClass(),
						REFERENCE_RESOURCE));
		job.setXpathOnly();

		AnalysisOutput result = AnalysisFacade.executeAnalysisJob(job)
				.getResult(XpathAnalysisTool.class);

		assertEquals(0, result.numFindings());
	}

	/***
	 * This test will yield differences between the reference and test bpmn
	 * files. The differences are explained in
	 * /src/test/resources/Reference/C.1.1-Differences.txt
	 */
	@Test
	public void testMissingExtensionElements() throws Exception {
		AnalysisJob job = new AnalysisJob(Consts.REFERENCE_DIR, "C.1.1",
				Variant.Reference, new ResourceAnalysisInput(getClass(),
						TEST_RESOURCE));
		job.setXpathOnly();

		AnalysisOutput result = AnalysisFacade.executeAnalysisJob(job)
				.getResult(XpathAnalysisTool.class);

		assertEquals(5, result.numFindings());

	}

}