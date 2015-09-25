/**
 * The MIT License (MIT)
 * Copyright (c) 2013 Signavio, OMG BPMN Model Interchange Working Group
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

package org.omg.bpmn.miwg.xmlCompare;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Difference;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.output.dom.NoReferenceEntry;
import org.omg.bpmn.miwg.api.output.dom.XmlDiffEntry;
import org.omg.bpmn.miwg.api.tools.DOMAnalysisTool;
import org.omg.bpmn.miwg.xmlCompare.bpmn2_0.comparison.Bpmn20ConformanceChecker;
import org.omg.bpmn.miwg.xmlCompare.bpmn2_0.comparison.Bpmn20DiffConfiguration;
import org.omg.bpmn.miwg.xmlCompare.configuration.BpmnCompareConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class XmlCompareAnalysisTool implements DOMAnalysisTool {

	public static final String NAME = "xml-compare";

	private static Bpmn20ConformanceChecker checker;

	protected static Document getDocument(InputStream inputStream)
			throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(inputStream);
		return document;
	}

	private static void initChecker(String confName) throws JsonParseException,
			JsonMappingException, IOException, ParserConfigurationException {
		BpmnCompareConfiguration conf = BpmnCompareConfiguration
				.loadConfiguration(confName);
		Bpmn20DiffConfiguration.setConf(conf);
		checker = new Bpmn20ConformanceChecker();
	}

	private static Bpmn20ConformanceChecker getChecker()
			throws JsonParseException, JsonMappingException, IOException,
			ParserConfigurationException {
		if (checker == null) {
			initChecker(null/* rely on default configuration */);
		}
		return checker;
	}

	@Override
	public AnalysisOutput analyzeDOM(AnalysisJob job,
			Document referenceDocument, Document actualDocument)
			throws Exception {

		AnalysisOutput output = new AnalysisOutput(job, this);
		try {

			if (referenceDocument != null) {
				List<Difference> diffs = getChecker()
						.getSignificantDifferences(actualDocument,
								referenceDocument);

				for (Difference diff : diffs) {
					XmlDiffEntry entry = new XmlDiffEntry(diff);
					output.println(entry);
				}

			} else {
				NoReferenceEntry entry = new NoReferenceEntry();
				output.println(entry);
			}
			return output;
		} finally {
			output.close();
		}
	}

	public String getName() {
		return NAME;
	}
}
