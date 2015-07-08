package org.omg.bpmn.miwg.mvn.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.Consts;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.util.DOMFactory;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.omg.bpmn.miwg.xsd.XsdAnalysisTool;
import org.w3c.dom.Document;

@RunWith(Parameterized.class)
public class Reference_All_Test {

	private final static String REF = "Reference";

	@Parameters
	public static List<Object[]> data() throws IOException {
		List<Object[]> list = new LinkedList<Object[]>();

		list.add(new String[] { "A.1.0.bpmn" });
		list.add(new String[] { "A.2.0.bpmn" });
		list.add(new String[] { "A.3.0.bpmn" });
		list.add(new String[] { "A.4.0.bpmn" });
		list.add(new String[] { "A.4.1.bpmn" });
		list.add(new String[] { "B.1.0.bpmn" });
		list.add(new String[] { "B.2.0.bpmn" });
		list.add(new String[] { "C.1.0.bpmn" });
		list.add(new String[] { "C.1.1.bpmn" });
		list.add(new String[] { "C.2.0.bpmn" });

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
		XPathAnalysisTool tool = new XPathAnalysisTool();

		AnalysisJob job = new AnalysisJob(REF, param, MIWGVariant.Reference,
				null, null);

		Document bpmnXmlDOM = DOMFactory.getDocument("/" + Consts.REFERENCE_DIR
				+ "/" + param);
		AnalysisResult result = tool.analyzeDOM(job, bpmnXmlDOM, bpmnXmlDOM, null);

		assertEquals(0, result.numFindings);

		System.out.println();
		System.out.println(result);
	}

	@Test
	public void testSchema() throws Exception {
		XsdAnalysisTool tool = new XsdAnalysisTool();

		AnalysisJob job = new AnalysisJob(REF, param, MIWGVariant.Reference,
				null, null);

		InputStream bpmnXmlStream = this.getClass().getResourceAsStream(
				"/" + Consts.REFERENCE_DIR + "/" + param);
		try {
			AnalysisResult result = tool.analyzeStream(job, null,
					bpmnXmlStream, null);

			assertEquals(0, result.numFindings);

			System.out.println();
			System.out.println(result);
		} finally {
			bpmnXmlStream.close();
		}
	}
	

}
