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

package org.omg.bpmn.miwg.xsd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.tools.StreamAnalysisTool;
import org.omg.bpmn.miwg.common.CheckOutput;
import org.omg.bpmn.miwg.xsd.checks.SchemaCheck;

public class XsdAnalysisTool implements StreamAnalysisTool {

	public final static String NAME="xsd";
	
	public String getName() {
		return NAME;
	}

	@Override
	public AnalysisResult analyzeStream(
			AnalysisJob job, InputStream referenceBpmnXml, InputStream actualBpmnXml,
			File reportFolder) throws Exception {

		SchemaCheck check = new SchemaCheck();
		CheckOutput checkOutput = new CheckOutput(NAME + job.getName() + "-" + job.getMIWGTestCase(), reportFolder);
		check.init(checkOutput);

		AnalysisResult result;
		
		try {
			 result = check.execute(actualBpmnXml, this);
		} catch (Throwable e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			checkOutput.close();
		}
		
		return result;
		
	}

}
