package org.omg.bpmn.miwg.mvn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.mvn.AnalysisFacade;
import org.omg.bpmn.miwg.util.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.util.TestUtil;
import org.omg.bpmn.miwg.xsd.XsdAnalysisTool;

public class AnalysisFacadeTest {

	private static final String BPMN_RESOURCE = "/HTMLOutputTest/A.1.0-roundtrip.bpmn";

	private static final String BPMN_RESOURCE_REFERENCE = "/HTMLOutputTest/A.1.0-roundtrip-minor-change.bpmn";

	@Before
	public void cleanGeneratedHTMLFiles() {
		TestUtil.prepareHTMLReportFolder(TestUtil.REPORT_FOLDER);
	}

	@Test
	public void testMultipleResources() throws Exception {
		Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
		jobs.add(new AnalysisJob("HTML Output Test 1", "A.1.0",
				MIWGVariant.Roundtrip, new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE), new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE_REFERENCE)));

		jobs.add(new AnalysisJob("HTML Output Test 2", "A.1.0",
				MIWGVariant.Roundtrip, new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE_REFERENCE), new ResourceAnalysisInput(
						getClass(), BPMN_RESOURCE_REFERENCE)));

		AnalysisFacade faccade = new AnalysisFacade(new File(
				TestUtil.REPORT_FOLDER));

		Collection<AnalysisRun> runs = faccade.executeAnalysisJobs(jobs);
		assertEquals(2, runs.size());

		File overviewFile = HTMLAnalysisOutputWriter.getOverviewFile(new File(
				TestUtil.REPORT_FOLDER));
		assertTrue(overviewFile.exists());
		assertTrue(overviewFile.length() > 0);
	}

	@Test
	public void testSingleResource() throws Exception {
		try {
			// Collect information about the analysis
			AnalysisJob job = new AnalysisJob(
					"Yaoqiang BPMN Editor 3.0.1 Error", "A.1.0",
					MIWGVariant.Roundtrip, new ResourceAnalysisInput(
							getClass(), BPMN_RESOURCE),
					new ResourceAnalysisInput(getClass(),
							BPMN_RESOURCE_REFERENCE));

			AnalysisFacade faccade = new AnalysisFacade(new File(
					TestUtil.REPORT_FOLDER));
			AnalysisRun run = faccade.executeAnalysisJob(job);

			File xsdFile = run.getResult(XsdAnalysisTool.NAME)
					.getHTMLResultsFile(new File(TestUtil.REPORT_FOLDER), job);
			File xpathFile = run.getResult(XsdAnalysisTool.NAME)
					.getHTMLResultsFile(new File(TestUtil.REPORT_FOLDER), job);
			File xmlCompareFile = run.getResult(XsdAnalysisTool.NAME)
					.getHTMLResultsFile(new File(TestUtil.REPORT_FOLDER), job);

			assertTrue(xsdFile.exists());
			assertTrue(xpathFile.exists());
			assertTrue(xmlCompareFile.exists());

			assertTrue(xsdFile.length() > 0);
			assertTrue(xpathFile.length() > 0);
			assertTrue(xmlCompareFile.length() > 0);
		} finally {
		}

	}

    @Test
    public void testMultipleFilesFromOneApplication() throws Exception {
          Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
          jobs.add(new AnalysisJob(
                        "src/test/resources/W4 BPMN+ Composer V.9.0/A.1.0-roundtrip.bpmn"));
          jobs.add(new AnalysisJob(
                        "src/test/resources/W4 BPMN+ Composer V.9.0/A.2.0-roundtrip.bpmn"));

          AnalysisFacade facade = new AnalysisFacade(new File(
                        TestUtil.REPORT_FOLDER));
          facade.executeAnalysisJobs(jobs);
    }

	
	@Test
	public void testMultipleFilesFromTwoApplications() throws Exception {
		Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
		jobs.add(new AnalysisJob(
				"src/test/resources/W4 BPMN+ Composer V.9.0/A.1.0-roundtrip.bpmn"));
		jobs.add(new AnalysisJob(
				"src/test/resources/W4 BPMN+ Composer V.9.0/A.2.0-roundtrip.bpmn"));
		jobs.add(new AnalysisJob("src/test/resources/bpmn.io 0.5.0/B.2.0-roundtrip.bpmn"));
		
		AnalysisFacade facade = new AnalysisFacade(new File(
				TestUtil.REPORT_FOLDER));
		facade.executeAnalysisJobs(jobs);
	}

}
