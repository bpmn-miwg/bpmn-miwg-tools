package org.omg.bpmn.miwg.mvn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.omg.bpmn.miwg.util.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.util.TestUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelInterchangeMojoTest {

	private static final String W4_MODELER_ID = "W4 BPMN+ Composer V.9.0";
	private static final String CAMUNDA_MODELER_ID = "camunda Modeler 2.4.0";
	private static final String CAMUNDA_JS_ID = "camunda-bpmn.js c906a7c941b82dbb832ed9be62989c171c724199";
	private static final String ECLIPSE_MODELER_ID = "Eclipse BPMN2 Modeler 0.2.6";
	private static final String IBM_MODELER_ID = "IBM Process Designer 8.0.1";
	private static final String MID_MODELER_ID = "MID Innovator 11.5.2.30413";
	private static final String SIGNAVIO_MODELER_ID = "Signavio Process Editor 7.8.1";
	private static final String YAOQIANG_2_MODELER_ID = "Yaoqiang BPMN Editor 2.2.6";
	private static final String YAOQIANG_3_MODELER_ID = "Yaoqiang BPMN Editor 3.0.1";
	private static ModelInterchangeMojo mojo;
	private static XPathExpression testOverviewExpr;
	private static File overview;
	private static DocumentBuilder docBuilder;

	@Before
	public void setUp() throws Exception {
		TestUtil.prepareHTMLReportFolder(TestUtil.REPORT_FOLDER);
		
		mojo = new ModelInterchangeMojo();
		mojo.outputDirectory = new File(TestUtil.REPORT_FOLDER);
		mojo.resources = new ArrayList<Resource>();

		overview = HTMLAnalysisOutputWriter
				.getOverviewFile(mojo.outputDirectory);

		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		testOverviewExpr = xPath.compile("//div[@class=\"test\"]");
	}

	@Test
	public void testScanResourcesForAllBpmnFiles() {
		List<File> bpmnFiles = new ArrayList<File>();
		File dir = new File("src/test/resources");
		mojo.scanForBpmn(dir, bpmnFiles);
		// 15 = the total number of BPMN files in src/test/resources
		assertEquals(21, bpmnFiles.size());
	}

	@Test
	public void testScanResourcesForSpecificVendorBpmnFiles() {
		List<File> bpmnFiles = new ArrayList<File>();
		File dir = new File("src" + File.separator + "test" + File.separator
				+ "resources");
		mojo.scanForBpmn(dir, bpmnFiles, W4_MODELER_ID);
		assertEquals(9, bpmnFiles.size());
	}

	@Test
	public void testMojo() {
		try {
			Resource res = new Resource();
			res.setDirectory("src/test/resources");
			mojo.resources.add(res);
			mojo.execute();

			assertTrue(overview.exists());
			Document document = docBuilder.parse(overview);
			NodeList nodes = (NodeList) testOverviewExpr.evaluate(document,
					XPathConstants.NODESET);

			assertEquals(9 /* count of .bpmn in W4 dir */, nodes.getLength());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getClass() + ":" + e.getMessage());
		}
	}

	@Test
	public void testMojoHandlingSchemaInvalidBpmn() {
		try {
			Resource res = new Resource();
			res.setDirectory("src/test/invalid-resources");
			mojo.resources.add(res);
			mojo.execute();

			assertTrue(overview.exists());
			Document document = docBuilder.parse(overview);
			NodeList nodes = (NodeList) testOverviewExpr.evaluate(document,
					XPathConstants.NODESET);

			Node invalidNode = nodes.item(0);

			// There should be 2 findings: an invalid element and an invalid
			// attribute
			assertEquals("2",
					invalidNode.getAttributes().getNamedItem("xsd-finding")
							.getNodeValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getClass() + ":" + e.getMessage());
		}
	}

	@Test
	public void testApplicationsParsing() {
		List<String> applications = mojo.getApplications();
		assertEquals(17, applications.size());
		assertTrue(applications.contains(CAMUNDA_MODELER_ID));
		assertTrue(applications.contains(CAMUNDA_JS_ID));
		assertTrue(applications.contains(ECLIPSE_MODELER_ID));
		assertTrue(applications.contains(IBM_MODELER_ID));
		assertTrue(applications.contains(MID_MODELER_ID));
		assertTrue(applications.contains(SIGNAVIO_MODELER_ID));
		assertTrue(applications.contains(YAOQIANG_2_MODELER_ID));
		assertTrue(applications.contains(YAOQIANG_3_MODELER_ID));
		assertTrue(applications.contains(W4_MODELER_ID));
	}
}
