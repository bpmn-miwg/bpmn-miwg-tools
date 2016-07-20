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

package org.omg.bpmn.miwg.devel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.facade.AnalysisFacade;
import org.omg.bpmn.miwg.schema.SchemaAnalysisTool;
import org.omg.bpmn.miwg.xpath.XpathAnalysisTool;

@RunWith(Parameterized.class)
public abstract class AbstractTest {

	protected AnalysisJob job;

	private static List<AnalysisRun> runs = new LinkedList<AnalysisRun>();
	
	
	public AbstractTest(AnalysisJob parameter) {
		this.job = parameter;
	}

	@Before
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("=============================");
		System.out.println("Running test:");
		System.out.println(job);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testSchema() throws Exception {
		job.setSchemaOnly();

		AnalysisRun run = AnalysisFacade.executeAnalysisJob(job);

		AnalysisOutput result = run.getResult(SchemaAnalysisTool.class);

		if (result == null)
			fail("Null result returned");

		assertEquals(0, result.numFindings());
		runs.add(run);
	}

	@Test
	public void testXpath() throws Exception {
		job.setXpathOnly();

		AnalysisRun run = AnalysisFacade.executeAnalysisJob(job);

		AnalysisOutput result = run.getResult(XpathAnalysisTool.class);

		if (result == null)
			fail("Null result returned");

		assertEquals(0, result.numFindings());
		runs.add(run);
	}
	
	@AfterClass
	public static void afterClass() {
		
	}

}
