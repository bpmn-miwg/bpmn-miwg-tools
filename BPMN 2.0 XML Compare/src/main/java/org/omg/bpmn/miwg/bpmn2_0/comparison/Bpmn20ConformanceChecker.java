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

package org.omg.bpmn.miwg.bpmn2_0.comparison;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.ElementQualifier;
import org.omg.bpmn.miwg.util.xml.diff.XmlDiffConfiguration;
import org.omg.bpmn.miwg.util.xml.diff.XmlDiffUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @author Sven
 * 
 */
public class Bpmn20ConformanceChecker {

	/********************************************
	 * CONFIGURATION
	 *******************************************/
	//@formatter:off
	private static final String[] GENERATED_ID_ELEMS = {
		"definitions", "collaboration", "bpmndi:BPMNPlane",	"bpmndi:BPMNDiagram", "globalTask",
		"ioSpecification", "dataState", "dataStore", "dataStoreReference", "transformation", "laneSet", "documentation"
	};
	//@formatter:on

	private XmlDiffUtil xmlDiff;
	private DocumentBuilder docBuilder;

	public Bpmn20ConformanceChecker() throws ParserConfigurationException {
		this(new Bpmn20DiffConfiguration());
	}

	public Bpmn20ConformanceChecker(XmlDiffConfiguration config) throws ParserConfigurationException {
		ElementQualifier qualifier = new BPMN20ElementQualifier(GENERATED_ID_ELEMS);
		xmlDiff = new XmlDiffUtil(config, qualifier, new Bpmn20DifferenceListener());

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		docBuilder = domFactory.newDocumentBuilder();
	}

	public List<Difference> getSignificantDifferences(File expectedBpmnXmlFile,
			File actualBpmnXmlFile) throws SAXException, IOException,
			ParserConfigurationException {
		Document expectedBpmnXmlDoc = docBuilder.parse(expectedBpmnXmlFile);
		Document actualBpmnXmlDoc = docBuilder.parse(actualBpmnXmlFile);
		return xmlDiff.areDocumentsEqualReporting(expectedBpmnXmlDoc,
				actualBpmnXmlDoc);
	}

	public String checkForSignificantDifferences(File expectedBpmnXmlFile, File actualBpmnXmlFile) throws SAXException,
			IOException, ParserConfigurationException {
		Document expectedBpmnXmlDoc = docBuilder.parse(expectedBpmnXmlFile);
		Document actualBpmnXmlDoc = docBuilder.parse(actualBpmnXmlFile);
		return checkForSignificantDifferences(expectedBpmnXmlDoc, actualBpmnXmlDoc);
	}

	public String checkForSignificantDifferences(File expectedBpmnXmlFile, String actualBpmnXml) throws SAXException,
			IOException, ParserConfigurationException {
		Document expectedBpmnXmlDoc = docBuilder.parse(expectedBpmnXmlFile);
		Document actualBpmnXmlDoc = docBuilder.parse(new InputSource(new StringReader(actualBpmnXml)));
		return checkForSignificantDifferences(expectedBpmnXmlDoc, actualBpmnXmlDoc);
	}

	public String checkForSignificantDifferences(String expectedBpmnXml, String actualBpmnXml) throws SAXException,
			IOException, ParserConfigurationException {
		Document expectedBpmnXmlDoc = docBuilder.parse(new InputSource(new StringReader(expectedBpmnXml)));
		Document actualBpmnXmlDoc = docBuilder.parse(new InputSource(new StringReader(actualBpmnXml)));
		return checkForSignificantDifferences(expectedBpmnXmlDoc, actualBpmnXmlDoc);
	}

	public String checkForSignificantDifferences(Document expectedBpmnXml, Document actualBpmnXml) throws SAXException,
			IOException, ParserConfigurationException {
		List<Difference> diffs = xmlDiff.areDocumentsEqualReporting(expectedBpmnXml, actualBpmnXml);

		if (diffs != null && diffs.size() > 0) {
			return xmlDiff.getDifferencesString(diffs);
		} else {
			return null;
		}
	}

}
