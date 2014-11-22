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

package org.omg.bpmn.miwg.test.common;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.omg.bpmn.miwg.xsd.XSDAnalysisTool;
import org.w3c.dom.Document;

@RunWith(Parameterized.class)
public abstract class AbstractTestCase {

	protected InstanceParameter param;

	public AbstractTestCase(InstanceParameter parameter) {
		this.param = parameter;
	}

	@Before
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("=============================");
		System.out.println("Running test:");
		System.out.println(param);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testXpath() throws Exception {
		XPathAnalysisTool tool = new XPathAnalysisTool();

		AnalysisJob job = new AnalysisJob();
		job.FullApplicationName = param.application.toString();
		job.MIWGTestCase = param.testResult.name;
		
		InputStream bpmnXmlStream = new FileInputStream(param.testResult.file);
		Document bpmnXmlDOM = DOMFactory.getDocument(bpmnXmlStream);
		try {
			AnalysisResult result = tool.analyzeDOM(job, null,
					bpmnXmlDOM, param.outputRoot);

			assertEquals(0, result.numFindings);

			System.out.println();
			System.out.println(result);
		} finally {
			bpmnXmlStream.close();
		}
	}

	@Test
	public void testSchema() throws Exception {
		XSDAnalysisTool tool = new XSDAnalysisTool();

		AnalysisJob job = new AnalysisJob();
		job.FullApplicationName = param.application.toString();
		job.MIWGTestCase = param.testResult.name;

		InputStream bpmnXmlStream = new FileInputStream(param.testResult.file);
		try {
			AnalysisResult result = tool.analyzeStream(job, null, bpmnXmlStream,
					param.outputRoot);

			assertEquals(0, result.numFindings);

			System.out.println();
			System.out.println(result);
		} finally {
			bpmnXmlStream.close();
		}
	}

}
