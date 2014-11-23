package org.omg.bpmn.miwg.mvn.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.util.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.xmlCompare.XmlCompareAnalysisTool;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.omg.bpmn.miwg.xsd.XSDAnalysisTool;
import org.w3c.dom.Document;

public class HTMLOutputTest {

	private static final String RPT_FOLDER = "target";

	private static final String BPMN_RESOURCE = "/HTMLOutputTest/A.1.0-roundtrip.bpmn";

	private static final String BPMN_RESOURCE_REFERENCE = "/HTMLOutputTest/A.1.0-roundtrip-minor-change.bpmn";

	@Before
	public void setUp() {
		new File(RPT_FOLDER).mkdirs();
	}


	@Test
	public void testOutput() throws Exception {
		XPathAnalysisTool xpathAnalysisTool = new XPathAnalysisTool();
		XSDAnalysisTool xsdAnalysisTool = new XSDAnalysisTool();
		XmlCompareAnalysisTool xmlCompareAnalysisTool = new XmlCompareAnalysisTool();
		InputStream inputStream = null;
		InputStream inputStream2 = null;

		try {
			// Collect information about the analysis
			AnalysisJob job = new AnalysisJob();
			job.FullApplicationName = "Yaoqiang BPMN Editor 3.0.1 Error";
			job.MIWGTestCase = "A.1.0";
			job.Variant = MIWGVariant.Roundtrip;

			AnalysisResult result;
			Document dom;
			Document referenceDom;

			inputStream = getClass().getResourceAsStream(BPMN_RESOURCE);
			assertNotNull("Cannot find resource to test", inputStream);
			dom = DOMFactory.getDocument(inputStream);
			inputStream.close();

			inputStream2 = getClass().getResourceAsStream(
					BPMN_RESOURCE_REFERENCE);
			assertNotNull("Cannot find resource to test", inputStream2);
			referenceDom = DOMFactory.getDocument(inputStream2);
			inputStream2.close();

			// We need to re-open stream for a second run
			inputStream = getClass().getResourceAsStream(BPMN_RESOURCE);
			result = xsdAnalysisTool
					.analyzeStream(job, null, inputStream, null);
			HTMLAnalysisOutputWriter.writeOutput(new File(RPT_FOLDER), job,
					xsdAnalysisTool, result);
			File xsdFile = HTMLAnalysisOutputWriter.getOutputFile(new File(
					RPT_FOLDER), job, xsdAnalysisTool);
			System.out.println("Created output file: " + xsdFile);

			result = xpathAnalysisTool.analyzeDOM(job, null, dom, null);
			HTMLAnalysisOutputWriter.writeOutput(new File(RPT_FOLDER), job,
					xpathAnalysisTool, result);
			File xpathFile = HTMLAnalysisOutputWriter.getOutputFile(new File(
					RPT_FOLDER), job, xpathAnalysisTool);
			System.out.println("Created output file: " + xpathFile);

			result = xmlCompareAnalysisTool.analyzeDOM(job, referenceDom, dom,
					null);
			HTMLAnalysisOutputWriter.writeOutput(new File(RPT_FOLDER), job,
					xmlCompareAnalysisTool, result);
			File xmlCompareFile = HTMLAnalysisOutputWriter.getOutputFile(
					new File(RPT_FOLDER), job, xmlCompareAnalysisTool);
			System.out.println("Created output file: " + xmlCompareFile);

			assertTrue(xsdFile.exists());
			assertTrue(xpathFile.exists());
			assertTrue(xmlCompareFile.exists());

			assertTrue(xsdFile.length() > 0);
			assertTrue(xpathFile.length() > 0);
			assertTrue(xmlCompareFile.length() > 0);

		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				;
			}
			try {
				inputStream2.close();
			} catch (IOException e) {
				;
			}
		}

	}
}
