package org.omg.bpmn.miwg.facade.test.xmlCompare;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.api.output.html.Output;
import org.omg.bpmn.miwg.facade.AnalysisFacade;
import org.omg.bpmn.miwg.xmlCompare.XmlCompareAnalysisTool;

public class XMLCompareTest {

	private static final String TOOL_ID = "Yaoqiang BPMN Editor 2.2.18";
	private static final String INPUT = "/xmlCompare/Yaoqiang BPMN Editor 2.2.18/A.1.0-roundtrip.bpmn";

	@Test
	public void testDomInputs() {

		try {
			AnalysisJob job = new AnalysisJob(TOOL_ID, "A.1.0",
					Variant.Reference, new ResourceAnalysisInput(getClass(),
							INPUT));
			job.setXmlCompareOnly();

			AnalysisOutput result = AnalysisFacade.executeAnalysisJob(job).getResult(
					XmlCompareAnalysisTool.class);

			Collection<? extends Output> significantDifferences = result
					.getHtmlOutput();
			System.out.println("Found " + significantDifferences.size()
					+ " diffs.");
			for (Output output : significantDifferences) {
				System.out.println("..." + output.getDescription());
			}

			assertNotSame(0, significantDifferences.size());
			assertNotSame(0, result.numFindings());

			File resultsFile = new File(result.getResultsFileName(job, ".html"));
			assertTrue(resultsFile.exists());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getClass() + ":" + e.getMessage());
		}
	}

}
