package org.omg.bpmn.miwg.mvn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.mvn.AnalysisFaccade;
import org.omg.bpmn.miwg.util.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.xsd.XsdAnalysisTool;

public class AnalysisFaccadeTest {

	private static final String RPT_FOLDER = "target";

	private static final String BPMN_RESOURCE = "/HTMLOutputTest/A.1.0-roundtrip.bpmn";

	private static final String BPMN_RESOURCE_REFERENCE = "/HTMLOutputTest/A.1.0-roundtrip-minor-change.bpmn";

	@Before
	public void setUp() {
		File reportFolder = new File(RPT_FOLDER);
		reportFolder.mkdirs();
		
		File[] files = reportFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".html");
			}
			
		});
		
		for (File file:files) {
			file.delete();
		}
	}

	@Test
	public void testMultipleToolOutputs() throws Exception {
		Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
		jobs.add(new AnalysisJob("HTML Output Test", "A.1.0",
				MIWGVariant.Roundtrip, new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE), new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE_REFERENCE)));

		jobs.add(new AnalysisJob("HTML Output Test", "A.1.0",
				MIWGVariant.Roundtrip, new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE_REFERENCE), new ResourceAnalysisInput(
						getClass(), BPMN_RESOURCE_REFERENCE)));

		AnalysisFaccade faccade = new AnalysisFaccade(new File(RPT_FOLDER));

		Collection<AnalysisRun> runs = faccade.executeAnalysisJobs(jobs);
		assertEquals(2, runs.size());

		File overviewFile = HTMLAnalysisOutputWriter.getOverviewFile(new File(
				RPT_FOLDER));
		assertTrue(overviewFile.exists());
		assertTrue(overviewFile.length() > 0);
	}

	@Test
	public void testSingleToolOutput() throws Exception {
		try {
			// Collect information about the analysis
			AnalysisJob job = new AnalysisJob(
					"Yaoqiang BPMN Editor 3.0.1 Error", "A.1.0",
					MIWGVariant.Roundtrip, new ResourceAnalysisInput(
							getClass(), BPMN_RESOURCE),
					new ResourceAnalysisInput(getClass(),
							BPMN_RESOURCE_REFERENCE));

			AnalysisFaccade faccade = new AnalysisFaccade(new File(RPT_FOLDER));
			AnalysisRun run = faccade.executeAnalysisJob(job);

			File xsdFile = run.getResult(XsdAnalysisTool.NAME)
					.getHTMLResultsFile(new File(RPT_FOLDER), job);
			File xpathFile = run.getResult(XsdAnalysisTool.NAME)
					.getHTMLResultsFile(new File(RPT_FOLDER), job);
			File xmlCompareFile = run.getResult(XsdAnalysisTool.NAME)
					.getHTMLResultsFile(new File(RPT_FOLDER), job);

			assertTrue(xsdFile.exists());
			assertTrue(xpathFile.exists());
			assertTrue(xmlCompareFile.exists());

			assertTrue(xsdFile.length() > 0);
			assertTrue(xpathFile.length() > 0);
			assertTrue(xmlCompareFile.length() > 0);
		} finally {
		}

	}

}
