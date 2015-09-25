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

package org.omg.bpmn.miwg.test.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.AnalysisInput;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.mvn.AnalysisFacade;
import org.omg.bpmn.miwg.xpath.XpathAnalysisTool;

public class XPathTest {

	private static final String YAOQIANG_CORRECT = "/xpath/Yaoqiang BPMN Editor 3.0.1 Correct/A.1.0-roundtrip.bpmn";
	private static final String YAOQIANG_ERROR = "/xpath/Yaoqiang BPMN Editor 3.0.1 Error/A.1.0-roundtrip.bpmn";

	@Test
	public void testYaoqiang3_with_A_1_0_roundtrip_Error() throws Exception {
		AnalysisInput input = new ResourceAnalysisInput(getClass(),
				YAOQIANG_ERROR);

		AnalysisJob job = new AnalysisJob("Yaoqiang BPMN Editor Error",
				"A.1.0", Variant.Roundtrip, input);
		job.setXpathOnly();

		AnalysisFacade faccade = new AnalysisFacade();
		AnalysisRun run = faccade.executeAnalysisJob(job);

		AnalysisOutput result = run.getResult(XpathAnalysisTool.class);
		assertEquals(1, result.numFindings());
	}

	@Test
	public void testYaoqiang3_with_A_1_0_roundtrip_Correct() throws Exception {
		AnalysisInput input = new ResourceAnalysisInput(getClass(),
				YAOQIANG_CORRECT);

		AnalysisJob job = new AnalysisJob("Yaoqiang BPMN Editor Error",
				"A.1.0", Variant.Roundtrip, input);
		job.setXpathOnly();

		AnalysisFacade faccade = new AnalysisFacade();
		AnalysisRun run = faccade.executeAnalysisJob(job);

		AnalysisOutput result = run.getResult(XpathAnalysisTool.class);
		assertEquals(0, result.numFindings());
	}

	@Test
	public void testOutputPOJOs() throws Exception {
		AnalysisInput input = new ResourceAnalysisInput(getClass(),
				YAOQIANG_CORRECT);

		AnalysisJob job = new AnalysisJob("Yaoqiang BPMN Editor Error",
				"A.1.0", Variant.Roundtrip, input);
		job.setXpathOnly();

		AnalysisFacade faccade = new AnalysisFacade();
		AnalysisRun run = faccade.executeAnalysisJob(job);

		AnalysisOutput result = run.getResult(XpathAnalysisTool.class);
		assertFalse(result.getHtmlOutput().isEmpty());
	}

}