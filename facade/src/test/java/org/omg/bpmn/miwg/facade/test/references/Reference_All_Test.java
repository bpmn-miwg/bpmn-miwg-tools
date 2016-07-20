package org.omg.bpmn.miwg.facade.test.references;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisOutput;
import org.omg.bpmn.miwg.api.AnalysisRun;
import org.omg.bpmn.miwg.api.Variant;
import org.omg.bpmn.miwg.api.input.AnalysisInput;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.facade.AnalysisFacade;
import org.omg.bpmn.miwg.schema.SchemaAnalysisTool;
import org.omg.bpmn.miwg.xpath.XpathAnalysisTool;


/**
 * Checks that the references yield no findings.
 *
 */
@RunWith(Parameterized.class)
public class Reference_All_Test {

	@Parameters
	public static List<Object[]> data() throws IOException {
		List<Object[]> list = new LinkedList<Object[]>();

		list.add(new String[] { "A.1.0" });
		list.add(new String[] { "A.2.0" });
		list.add(new String[] { "A.3.0" });
		list.add(new String[] { "A.4.0" });
		list.add(new String[] { "A.4.1" });
		list.add(new String[] { "B.1.0" });
		list.add(new String[] { "B.2.0" });
		list.add(new String[] { "C.1.0" });
		list.add(new String[] { "C.1.1" });
		list.add(new String[] { "C.2.0" });

		return list;
	}

	protected String param;

	public Reference_All_Test(String parameter) {
		this.param = parameter;
	}

	@Before
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("=============================");
		System.out.println("Running test:");
		System.out.println(param);
	}

	@Test
	public void testXpath() throws Exception {
		AnalysisInput input = new ResourceAnalysisInput(getClass(), "/Reference/" + param + ".bpmn");

		AnalysisJob job = new AnalysisJob("Reference", param, Variant.Reference,
				input);
		job.setXpathOnly();

		AnalysisRun run = AnalysisFacade.executeAnalysisJob(job);

		AnalysisOutput result = run.getResult(XpathAnalysisTool.class);

		assertEquals(0, result.numFindings());

		System.out.println();
		System.out.println(result);
	}

	@Test
	public void testSchema() throws Exception {
		AnalysisInput input = new ResourceAnalysisInput(getClass(), "/Reference/" + param + ".bpmn");

		AnalysisJob job = new AnalysisJob("Reference", param, Variant.Reference,
				input);
		job.setSchemaOnly();

		AnalysisRun run = AnalysisFacade.executeAnalysisJob(job);

		AnalysisOutput result = run.getResult(SchemaAnalysisTool.class);

		assertEquals(0, result.numFindings());

		System.out.println();
		System.out.println(result);

	}

}
