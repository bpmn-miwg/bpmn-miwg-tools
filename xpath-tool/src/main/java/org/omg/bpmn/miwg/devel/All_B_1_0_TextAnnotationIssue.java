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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.devel.common.AbstractTestCase;
import org.omg.bpmn.miwg.devel.common.InstanceParameter;
import org.omg.bpmn.miwg.devel.common.ScanUtil;
import org.omg.bpmn.miwg.devel.common.StandardScanParameters;
import org.omg.bpmn.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.omg.bpmn.miwg.xpath.common.AbstractXpathCheck;
import org.w3c.dom.Document;

@RunWith(Parameterized.class)
public class All_B_1_0_TextAnnotationIssue extends AbstractTestCase {

	public All_B_1_0_TextAnnotationIssue(InstanceParameter parameter) {
		super(parameter);
	}

	@Parameters
	public static List<Object[]> data() throws IOException {
		return ScanUtil.data(new StandardScanParameters(null,
				"B.1.0"));
	}

	@Test
	@Override
	@Ignore
	public void testSchema() throws Exception {
		
	}
	
	@Test
	@Override
	public void testXpath() throws Exception {
		XPathAnalysisTool tool = new XPathAnalysisTool();

		AnalysisJob job = new AnalysisJob(param.application.toString(),
				param.testResult.name, null, null, null);

		InputStream bpmnXmlStream = null;
		Document bpmnXmlDOM = null;
		try {
			bpmnXmlStream = new FileInputStream(param.testResult.file);
			bpmnXmlDOM = DOMFactory.getDocument(bpmnXmlStream);
		} catch (Exception e) {
			System.err.println("Could not create DOM: " + e.getMessage());
			fail("Could not create DOM");
			return;
		}

		AnalysisResult result = null;
		try {
			result = tool.analyzeDOM(job, null, bpmnXmlDOM,
					new B_1_0_TextAnnotationIssue_Check(), param.outputRoot);

			assertEquals(0, result.numFindings);
		} catch (Exception e) {
			fail("Exception during execution: " + e.getMessage());
			System.err.println("Exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (bpmnXmlStream != null)
				bpmnXmlStream.close();

			System.out.println();
			System.out.println(result);
		}
	}

	private class B_1_0_TextAnnotationIssue_Check extends AbstractXpathCheck {

		@Override
		public String getName() {
			return "B.1.0";
		}

		@Override
		public void doExecute() throws Throwable {
			selectElementX("//bpmn:collaboration");
			{
				selectProcessX("//bpmn:process[@id=//bpmn:participant[@name='Pool']/@processRef]");
				navigateElement("bpmn:callActivity",
						"Call Activity Collapsed");
				checkTextAssociation("Text Annotation");
				pop();
			}
			pop();
		}

	}

}
