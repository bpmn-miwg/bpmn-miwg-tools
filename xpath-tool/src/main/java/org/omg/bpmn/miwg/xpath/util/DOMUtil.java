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

package org.omg.bpmn.miwg.xpath.util;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DOMUtil {

	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db;

	static {
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("Cannot initialize DocumentBuilder");
		}
	}

	public static void printDOM(Document document)
			throws TransformerFactoryConfigurationError, TransformerException {
		DOMSource domSource = new DOMSource(document);
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		System.out.println(sw.toString());
	}

	public static List<Difference> checkSubtreeForDifferences(Node testNode,
			Node refNode) throws ParserConfigurationException,
			TransformerException {

		Document newTestDoc = db.newDocument();
		Document newRefDoc = db.newDocument();

		newTestDoc.appendChild(newTestDoc.adoptNode(testNode.cloneNode(true)));
		newRefDoc.appendChild(newRefDoc.adoptNode(refNode.cloneNode(true)));

		newTestDoc.normalizeDocument();
		newRefDoc.normalizeDocument();

		XMLUnit.setIgnoreWhitespace(true);
		
		Diff diff = new Diff(newRefDoc, newTestDoc);
		DetailedDiff detailedDiff = new DetailedDiff(diff);
		detailedDiff.overrideDifferenceListener(new DomSubtreeCompareDifferenceListener());
		detailedDiff.overrideElementQualifier(new ElementNameAndAttributeQualifier());

		List<Difference> differences = new LinkedList<Difference>();

		for (Object o : detailedDiff.getAllDifferences()) {
			Difference difference = (Difference) o;
			// System.err.println(difference.getId() + ": " + difference);
			differences.add(difference);
		}

		return differences;
	}

}
