package org.omg.bpmn.miwg.mvn.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
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

	@Before
	public void setUp() {
		new File(RPT_FOLDER).mkdirs();
	}

	@After
	public void tearDown() {

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

			inputStream = getClass().getResourceAsStream(
					"/HTMLOutputTest/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", inputStream);
			dom = DOMFactory.getDocument(inputStream);
			inputStream.close();

			inputStream2 = getClass().getResourceAsStream(
					"/HTMLOutputTest/A.1.0-roundtrip-minor-change.bpmn");
			assertNotNull("Cannot find resource to test", inputStream2);
			referenceDom = DOMFactory.getDocument(inputStream2);
			inputStream2.close();

			// We need to re-open stream for a second run
			inputStream = getClass().getResourceAsStream(
					"/HTMLOutputTest/A.1.0-roundtrip.bpmn");
			result = xsdAnalysisTool
					.analyzeStream(job, null, inputStream, null);
			HTMLAnalysisOutputWriter.writeOutput(new File(RPT_FOLDER), job,
					xsdAnalysisTool, result);
			System.out.println("Created output file: "
					+ HTMLAnalysisOutputWriter.getOutputFile(new File(
							RPT_FOLDER), job, xsdAnalysisTool));

			result = xpathAnalysisTool.analyzeDOM(job, null, dom, null);
			HTMLAnalysisOutputWriter.writeOutput(new File(RPT_FOLDER), job,
					xpathAnalysisTool, result);
			System.out.println("Created output file: "
					+ HTMLAnalysisOutputWriter.getOutputFile(new File(
							RPT_FOLDER), job, xpathAnalysisTool));

			result = xmlCompareAnalysisTool.analyzeDOM(job, referenceDom, dom, null);
			HTMLAnalysisOutputWriter.writeOutput(new File(RPT_FOLDER), job,
					xmlCompareAnalysisTool, result);
			System.out.println("Created output file: "
					+ HTMLAnalysisOutputWriter.getOutputFile(new File(
							RPT_FOLDER), job, xmlCompareAnalysisTool));

			/*
			 * } catch (Exception e) { throw new
			 * RuntimeException(e.getMessage(), e);
			 */
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
