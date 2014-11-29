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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.omg.bpmn.miwg.HtmlOutput.Pojos.Detail;
import org.omg.bpmn.miwg.HtmlOutput.Pojos.DetailedOutput;
import org.omg.bpmn.miwg.HtmlOutput.Pojos.Output;
import org.omg.bpmn.miwg.HtmlOutput.Pojos.OutputType;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.tools.DOMAnalysisTool;
import org.omg.bpmn.miwg.xmlCompare.bpmn2_0.comparison.Bpmn20ConformanceChecker;
import org.omg.bpmn.miwg.xmlCompare.bpmn2_0.comparison.Bpmn20DiffConfiguration;
import org.omg.bpmn.miwg.xmlCompare.configuration.BpmnCompareConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class XmlCompareAnalysisTool implements DOMAnalysisTool {

	public static final String NAME = "compare";
	
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

	public static DetailedOutput describeDifference(Difference difference) {
		DetailedOutput dOut = new DetailedOutput();

		// Reference xpath/value
		dOut.addDetail(printDifferenceDetail(difference.getControlNodeDetail(),
				"reference"));

		// Vendor xpath/value
		dOut.addDetail(printDifferenceDetail(difference.getTestNodeDetail(),
				"vendor"));

		return dOut;
	}

	private static Detail printDifferenceDetail(NodeDetail detail, String type) {
		Detail d = new Detail();
		d.setMessage(detail.getXpathLocation() + " :\t" + detail.getValue());
		d.setType(type);
		d.setXpath(detail.getXpathLocation());
		return d;
	}

	@Override
	public AnalysisResult analyzeDOM(AnalysisJob job,
			Document referenceDocument, Document actualDocument, File logDir)
			throws Exception {
		List<Difference> diffs = getChecker().getSignificantDifferences(
				actualDocument, referenceDocument);

		return new AnalysisResult(0, diffs.size(), adapt(diffs), this);
	}

	private Collection<? extends Output> adapt(List<Difference> diffs) {
		List<Output> outputs = new ArrayList<Output>();
		for (Difference diff : diffs) {
			Output output = new Output(OutputType.finding,
					describeDifference(diff));
			output.setDescription(String.format(
					"Difference found in %1$s (id:%2$s)",
					diff.getDescription(), diff.getId()));
			outputs.add(output);
		}

		return outputs;
	}

	public String getName() {
		return NAME;
	}
}
