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

package org.omg.bpmn.miwg.xpath;

import java.util.LinkedList;
import java.util.List;

import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.tools.DOMAnalysisTool;
import org.omg.bpmn.miwg.xpath.checks.Registry;
import org.omg.bpmn.miwg.xpath.util.AbstractXpathCheck;
import org.omg.bpmn.miwg.xpath.util.DOMCheck;
import org.w3c.dom.Document;

public class XpathAnalysisTool implements DOMAnalysisTool {
	public final static String NAME = "xpath";

	private List<AbstractXpathCheck> registeredChecks = new LinkedList<AbstractXpathCheck>();

	public XpathAnalysisTool() {
		registeredChecks = Registry.getChecks();
	}

	public String getName() {
		return NAME;
	}

	private DOMCheck getCheck(AnalysisJob job) {
		for (AbstractXpathCheck check : registeredChecks) {
			if (check.isApplicable(job.getMIWGTestCase())) {
				return check;
			}
		}
		return null;
	}

	@Override
	public AnalysisOutput analyzeDOM(AnalysisJob job,
			Document referenceDocument, Document actualDocument)
			throws Exception {
		AnalysisOutput output = new AnalysisOutput(job, this);
		DOMCheck check = getCheck(job);
		if (check == null)
			throw new Exception(String.format(
					"No applicable test found for %s", job.getName()));

		analyzeDOM(job, referenceDocument, actualDocument, check, output);

		return output;
	}

	private void analyzeDOM(AnalysisJob job, Document referenceDocument,
			Document actualDocument, DOMCheck check, AnalysisOutput output)
			throws Exception {

		try {
			check.execute(actualDocument, referenceDocument, job, output);
		} catch (Throwable e) {
			throw new Exception(e);
		} finally {
			output.close();
		}

	}

}
