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
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.w3c.dom.Document;

public class SingleHTMLOutputTest {

	private static final String RPT_FOLDER = "target";

	@Before
	public void setUp() {
		new File(RPT_FOLDER).mkdirs();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testOutput() {
		XPathAnalysisTool xPathTestTool = new XPathAnalysisTool();
		InputStream inputStream = null;

		try {
			inputStream = getClass().getResourceAsStream(
					"/A.1.0-roundtrip.bpmn");
			assertNotNull("Cannot find resource to test", inputStream);

			Document document = DOMFactory.getDocument(inputStream);

			AnalysisJob job = new AnalysisJob();
			job.FullApplicationName = "Yaoqiang BPMN Editor 3.0.1 Error";
			job.MIWGTestCase = "A.1.0";
			job.Variant = MIWGVariant.Roundtrip;

			AnalysisResult result = xPathTestTool.analyzeDOM(job, null,
					document, null);

			HTMLAnalysisOutputWriter.writeOutput(new File(RPT_FOLDER), job,
					result);

			System.out.println("Created output file: "
					+ HTMLAnalysisOutputWriter.getOutputFile(new File(
							RPT_FOLDER), job));

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				;
			}
		}

	}
}
