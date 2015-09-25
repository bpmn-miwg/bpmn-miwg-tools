package org.omg.bpmn.miwg.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.api.output.html.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.mvn.AnalysisFacade;
import org.omg.bpmn.miwg.schema.SchemaAnalysisTool;
import org.omg.bpmn.miwg.util.TestUtil;
import org.omg.bpmn.miwg.xmlCompare.XmlCompareAnalysisTool;
import org.omg.bpmn.miwg.xpath.XpathAnalysisTool;

public class AnalysisFacadeTest {

	private static final String BPMN_RESOURCE = "/HTMLOutputTest/A.1.0-roundtrip.bpmn";

	private static final String BPMN_RESOURCE_REFERENCE = "/HTMLOutputTest/A.1.0-roundtrip-minor-change.bpmn";

	@Before
	public void cleanGeneratedHTMLFiles() {
		TestUtil.prepareHTMLReportFolder(TestUtil.REPORT_BASE_FOLDER_NAME);
	}

	@Test
	public void testMultipleResources() throws Exception {
		Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
		jobs.add(new AnalysisJob("HTML Output Test 1", "A.1.0",
				Variant.Roundtrip, new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE), new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE_REFERENCE)));

		jobs.add(new AnalysisJob("HTML Output Test 2", "A.1.0",
				Variant.Roundtrip, new ResourceAnalysisInput(getClass(),
						BPMN_RESOURCE_REFERENCE), new ResourceAnalysisInput(
						getClass(), BPMN_RESOURCE_REFERENCE)));

		AnalysisFacade faccade = new AnalysisFacade();

		Collection<AnalysisRun> runs = faccade.executeAnalysisJobs(jobs, TestUtil.REPORT_BASE_FOLDER_NAME);
		assertEquals(2, runs.size());

		File overviewFile = HTMLAnalysisOutputWriter.getOverviewFile(new File(
				TestUtil.REPORT_BASE_FOLDER_NAME));
		assertTrue(overviewFile.exists());
		assertTrue(overviewFile.length() > 0);
	}

	@Test
	public void testSingleResource() throws Exception {
		// Collect information about the analysis
		AnalysisJob job = new AnalysisJob("Yaoqiang BPMN Editor 3.0.1 Error",
				"A.1.0", Variant.Roundtrip, new ResourceAnalysisInput(
						getClass(), BPMN_RESOURCE), new ResourceAnalysisInput(
						getClass(), BPMN_RESOURCE_REFERENCE));

		AnalysisFacade faccade = new AnalysisFacade();
		AnalysisRun run = faccade.executeAnalysisJob(job);

		File xsdFile = new File(run.getResult(SchemaAnalysisTool.class)
				.getHTMLResultsLink(job));
		File xpathFile = new File(run.getResult(XpathAnalysisTool.class)
				.getHTMLResultsLink(job));
		File xmlCompareFile = new File(run.getResult(
				XmlCompareAnalysisTool.class).getHTMLResultsLink(job));

		assertTrue(xsdFile.exists());
		assertTrue(xpathFile.exists());
		assertTrue(xmlCompareFile.exists());

		assertTrue(xsdFile.length() > 0);
		assertTrue(xpathFile.length() > 0);
		assertTrue(xmlCompareFile.length() > 0);

	}

	@Test
	public void testMultipleFilesFromOneApplication() throws Exception {
		Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
		jobs.add(new AnalysisJob("W4 BPMN+ Composer V.9.0", "A.1.0",
				Variant.Roundtrip, new ResourceAnalysisInput(getClass(),
						"/W4 BPMN+ Composer V.9.0/A.1.0-roundtrip.bpmn"), null));
		jobs.add(new AnalysisJob("W4 BPMN+ Composer V.9.0", "A.2.0",
				Variant.Roundtrip, new ResourceAnalysisInput(getClass(),
						"/W4 BPMN+ Composer V.9.0/A.2.0-roundtrip.bpmn"), null));

		AnalysisFacade facade = new AnalysisFacade();
		facade.executeAnalysisJobs(jobs, null);
	}

	@Test
	public void testMultipleFilesFromTwoApplications() throws Exception {
		Collection<AnalysisJob> jobs = new LinkedList<AnalysisJob>();
		jobs.add(new AnalysisJob("W4 BPMN+ Composer V.9.0", "A.1.0",
				Variant.Roundtrip, new ResourceAnalysisInput(getClass(),
						"/W4 BPMN+ Composer V.9.0/A.1.0-roundtrip.bpmn"), null));
		jobs.add(new AnalysisJob("W4 BPMN+ Composer V.9.0", "A.2.0",
				Variant.Roundtrip, new ResourceAnalysisInput(getClass(),
						"/W4 BPMN+ Composer V.9.0/A.2.0-roundtrip.bpmn"), null));
		jobs.add(new AnalysisJob("bpmn.io 0.5.0", "B.2.0", Variant.Roundtrip,
				new ResourceAnalysisInput(getClass(),
						"/bpmn.io 0.5.0/B.2.0-roundtrip.bpmn"), null));

		AnalysisFacade facade = new AnalysisFacade();
		facade.executeAnalysisJobs(jobs, null);
	}

}
