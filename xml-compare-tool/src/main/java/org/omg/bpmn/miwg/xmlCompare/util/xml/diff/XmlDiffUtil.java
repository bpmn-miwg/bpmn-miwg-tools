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

package org.omg.bpmn.miwg.xmlCompare.util.xml.diff;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.ElementQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.omg.bpmn.miwg.xmlCompare.util.xml.SignavioNamespaceContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;



/**
 * This class offers SVG / XML comparison functionality.
 * 
 * @author Robert Gurol, Philipp Maschke
 * 
 */
public class XmlDiffUtil {

	private static final Logger LOGGER = Logger.getLogger(XmlDiffUtil.class);
	
	// TODO FindBugs make private / public and static?
	// comparator, to order Difference list
	class MyComparatorClass implements Comparator<Difference> {

		@Override
		public int compare(Difference d1, Difference d2) {
			return d1.getId() - d2.getId();
		}

	}
	
	XmlDiffConfiguration configuration;
	ElementQualifier elementQualifier;
	AbstractXmlDifferenceListener diffListener;
	
	public XmlDiffUtil(XmlDiffConfiguration configuration, ElementQualifier elementQualifier,  AbstractXmlDifferenceListener listener) {
		super();
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setCompareUnmatched(false);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setXpathNamespaceContext(new SignavioNamespaceContext());
		this.configuration = configuration;
		this.elementQualifier = elementQualifier;
		this.diffListener = listener;
	}


	/**
	 * For 2 SVG files given as {@link Document}, this method returns null if
	 * they are "equal", a list of differences if they are not.
	 * 
	 * @param controlXml
	 *            the SVG that acts as the base for comparison
	 * @param testXml
	 *            the SVG that need to be tested
	 * @return null if they are "equal", a list of differences if they are not
	 * @throws SAXException
	 *             parsing exception
	 */
	public List<Difference> areDocumentsEqualReporting(Document controlXml, Document testXml)
			throws SAXException, IOException {
		Diff diff = new Diff(controlXml, testXml);

		diff.overrideElementQualifier(elementQualifier);
		
		diffListener.initialize(controlXml, testXml, configuration);
		diff.overrideDifferenceListener(diffListener);

		return evaluateDifferences(diff, diffListener);
	}


	public String getDifferencesString(List<Difference> differenceList) {
		Collections.sort(differenceList, new MyComparatorClass());
		StringBuffer sBuff = new StringBuffer();
		sBuff.append("#Differences: " + differenceList.size() + "\n");

		for (Difference difference : differenceList) {
			sBuff.append(difference.getDescription() + " (id:" + difference.getId() + ")\n");
			sBuff
					.append("control: " + difference.getControlNodeDetail().getXpathLocation()
							+ ":\t");
			sBuff.append(difference.getControlNodeDetail().getValue() + "\n");
			sBuff.append("test:    " + difference.getTestNodeDetail().getXpathLocation() + ":\t");
			sBuff.append(difference.getTestNodeDetail().getValue() + "\n\n");
		}

		return sBuff.toString();
	}


	@SuppressWarnings("unchecked")
	private List<Difference> evaluateDifferences(Diff diff, AbstractXmlDifferenceListener listener) {
		LOGGER.trace("Comparison of XML started");
		DetailedDiff d = new DetailedDiff(diff);
		List<Difference> diffList;
		try{
			diffList = d.getAllDifferences();
		}catch(RuntimeException e){//catch all exceptions to at least have some stacktrace and logging
			LOGGER.error("Exception while retrieving SVG differences", e);
			throw e;
		}
		LOGGER.debug("Comparison of XML finished; differences ignored: " + 
				((AbstractXmlDifferenceListener)listener).getNumberOfIgnoredDifferences() + "; accepted: " + 
				((AbstractXmlDifferenceListener)listener).getNumberOfAcceptedDifferences());
		
		if (diffList != null)
			return diffList;
		else
			return null;
	}
}
