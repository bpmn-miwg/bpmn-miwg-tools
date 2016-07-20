package org.omg.bpmn.miwg.facade.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.ReferenceNotFoundException;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.facade.scan.BpmnFileScanner;
import org.omg.bpmn.miwg.facade.scan.StandardScanParameters;

public class BpmnFileScannerTest {

	@Test
	public void testScanResourcesForAllBpmnFiles() throws IOException,
			ReferenceNotFoundException {
		Collection<AnalysisJob> jobs = BpmnFileScanner
				.buildListOfAnalysisJobs(new StandardScanParameters(null, null,
						"src/test/resources", null));
		assertNotSame(0, jobs.size());
	}

	@Test
	public void testScanResourcesForSpecificApplication() throws IOException,
			ReferenceNotFoundException {
		Collection<AnalysisJob> jobs;

		jobs = BpmnFileScanner
				.buildListOfAnalysisJobs(new StandardScanParameters(
						FacadeTestConsts.W4_MODELER_ID, null, "src/test/resources/TestResults",
						null));
		assertEquals(5, jobs.size());

		jobs = BpmnFileScanner
				.buildListOfAnalysisJobs(new StandardScanParameters(
						FacadeTestConsts.BPMN_IO_ID, null, "src/test/resources/TestResults", null));
		assertEquals(1, jobs.size());
	}

	@Test
	public void testInferMiwgVariant() {
		File bpmnFile = new File("src" + File.separator + "test"
				+ File.separator + "resources" + File.separator
				+ FacadeTestConsts.W4_MODELER_ID + File.separator
				+ "A.1.0-export.bpmn");
		Variant variant = BpmnFileScanner.inferMiwgVariant(bpmnFile);
		assertEquals(Variant.Export, variant);
	}

	@Test
	public void testInferTestName() {
		File bpmnFile = new File("src" + File.separator + "test"
				+ File.separator + "resources" + File.separator
				+ FacadeTestConsts.W4_MODELER_ID + File.separator
				+ "A.1.0-export.bpmn");
		String test = BpmnFileScanner.inferTestName(bpmnFile);
		assertEquals("A.1.0", test);
	}

	@Test
	public void testCurrent() throws IOException {
		File f = new File(".");
		System.err.println(f.getCanonicalPath());
	}

	private Collection<String> getAllApplicationsFromJobList(
			Collection<AnalysisJob> jobs) {
		Collection<String> applications = new HashSet<String>();

		for (AnalysisJob job : jobs) {
			applications.add(job.getFullApplicationName());
		}

		return applications;
	}

	@Test
	public void testApplicationsParsing() throws IOException,
			ReferenceNotFoundException {
		Collection<AnalysisJob> jobs;
		jobs = BpmnFileScanner
				.buildListOfAnalysisJobs(new StandardScanParameters(
						null, null, "src/test/resources/TestResults",
						null));
		assertNotSame(0, jobs.size());

		Collection<String> applications = getAllApplicationsFromJobList(jobs);

		// The following tools are in src/test/resources
		assertTrue(applications.contains(FacadeTestConsts.CAMUNDA_MODELER_ID));
		assertTrue(applications.contains(FacadeTestConsts.W4_MODELER_ID));
		
		// The following tools are not in src/test/resources
		assertFalse(applications.contains(FacadeTestConsts.CAMUNDA_JS_ID));
		assertFalse(applications.contains(FacadeTestConsts.ECLIPSE_MODELER_ID));
		assertFalse(applications.contains(FacadeTestConsts.IBM_MODELER_ID));
		assertFalse(applications.contains(FacadeTestConsts.MID_MODELER_ID));
		assertFalse(applications.contains(FacadeTestConsts.SIGNAVIO_MODELER_ID));
		assertFalse(applications.contains(FacadeTestConsts.YAOQIANG_2_MODELER_ID));
		assertFalse(applications.contains(FacadeTestConsts.YAOQIANG_3_MODELER_ID));
	}
}
