package org.omg.bpmn.miwg.mvn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.omg.bpmn.miwg.api.output.overview.OverviewWriter;
import org.omg.bpmn.miwg.schema.SchemaAnalysisTool;
import org.omg.bpmn.miwg.util.TestUtil;
import org.omg.bpmn.miwg.xmlCompare.XmlCompareAnalysisTool;
import org.omg.bpmn.miwg.xpath.XpathAnalysisTool;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ModelInterchangeMojoTest {

    private static final String SRC_TEST_RESOURCES = "src/test/resources";
    private static ModelInterchangeMojo mojo;
	private static File overview;
	private static DocumentBuilder docBuilder;
	private XPath xPath;

	@Before
	public void setUp() throws Exception {
		TestUtil.prepareHTMLReportFolder(TestUtil.REPORT_BASE_FOLDER_NAME);

		mojo = new ModelInterchangeMojo();
		mojo.outputDirectory = new File(TestUtil.REPORT_BASE_FOLDER_NAME);
		mojo.resources = new ArrayList<Resource>();

		overview = OverviewWriter
				.getOverviewFileHtml(TestUtil.REPORT_BASE_FOLDER);

		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	private XPathExpression createXpathExpression(String expression)
			throws XPathExpressionException {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		xPath = xPathFactory.newXPath();
		XPathExpression xpath = xPath.compile(expression);
		return xpath;
	}

	@Test
	public void testMojo() {
		try {
			Resource res = new Resource();
            res.setDirectory(SRC_TEST_RESOURCES);
			mojo.resources.add(res);
			mojo.execute();

			System.out
					.println("Checking expected output exists with base folder: "
							+ TestUtil.REPORT_BASE_FOLDER_NAME);

			// assert structure and content of overview file
			assertTrue(overview.exists());
			Document document = docBuilder.parse(overview);
			XPathExpression xpath = createXpathExpression("//div[@class=\"test\"]");
			NodeList nodes = (NodeList) xpath.evaluate(document,
					XPathConstants.NODESET);

			// first param is number of bpmn files in the src/test/resources
			// folders of vendors listed in tools-tested-by-miwg.json
            // At time of writing this includes W4 & camunda but excludes
            // bpmn.io
            assertEquals(8, nodes.getLength());

			// report files for each tool
			assertHtmlReportsExist(new File(TestUtil.REPORT_BASE_FOLDER_NAME,
					XmlCompareAnalysisTool.NAME));
			assertHtmlReportsExist(new File(TestUtil.REPORT_BASE_FOLDER_NAME,
					SchemaAnalysisTool.NAME));
			assertHtmlReportsExist(new File(TestUtil.REPORT_BASE_FOLDER_NAME,
					XpathAnalysisTool.NAME));

			// assert structure of individual results file
			File xpathResult = new File(TestUtil.REPORT_BASE_FOLDER_NAME
					+ File.separator + XpathAnalysisTool.NAME + File.separator
					+ TestConsts.W4_MODELER_ID + File.separator
					+ TestConsts.W4_MODELER_ID + "-A.1.0-roundtrip.html");
			System.out.println("Checking file: " + xpathResult);
			assertTrue(xpathResult.exists());
			document = docBuilder.parse(xpathResult);
			nodes = (NodeList) xPath.compile(
					"//body/div[@class=\"testresults\"]").evaluate(document,
					XPathConstants.NODESET);
			assertTrue("Did not find result element", nodes.getLength() == 1);
			nodes = (NodeList) xPath.compile(
					"//body/div[@class=\"testresults\"]/div[@class=\"tool\"]")
					.evaluate(document, XPathConstants.NODESET);
			assertTrue("Did not find tool element", nodes.getLength() == 1);
			nodes = (NodeList) xPath
					.compile(
							"//body/div[@class=\"testresults\"]/div[@class=\"tool\"]/div[@class=\"test\"]")
					.evaluate(document, XPathConstants.NODESET);
			assertTrue("Did not find test element", nodes.getLength() >= 1);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getClass() + ":" + e.getMessage());
		}
	}

	private void assertHtmlReportsExist(File toolFldr) {
		File w4Fldr = new File(toolFldr, TestConsts.W4_MODELER_ID);
		assertTrue("Tool folder " + toolFldr.getAbsolutePath() + " not found",
				toolFldr.exists());
		/*
		 * The automation tools only work reliably with roundtrips.
		 */
		assertTrue("Tool report for W4 A.1.0 roundtrip not found",
				new File(w4Fldr, TestConsts.W4_MODELER_ID
						+ "-A.1.0-roundtrip.html").exists());
		assertTrue("Tool report for W4 A.2.0 roundtrip not found",
				new File(w4Fldr, TestConsts.W4_MODELER_ID
						+ "-A.2.0-roundtrip.html").exists());
		assertTrue("Tool report for W4 A.3.0 roundtrip not found",
				new File(w4Fldr, TestConsts.W4_MODELER_ID
						+ "-A.3.0-roundtrip.html").exists());
		assertTrue("Tool report for W4 A.4.0 roundtrip not found",
				new File(w4Fldr, TestConsts.W4_MODELER_ID
						+ "-A.4.0-roundtrip.html").exists());
		assertTrue("Tool report for W4 A.4.1 roundtrip not found",
				new File(w4Fldr, TestConsts.W4_MODELER_ID
						+ "-A.4.1-roundtrip.html").exists());
	}

	@Test
	public void testMojoHandlingSchemaInvalidBpmn()
			throws XPathExpressionException, SAXException, IOException,
			MojoExecutionException {
		Resource res = new Resource();
		res.setDirectory("src/test/invalid-resources");
		mojo.resources.add(res);
		mojo.application = "Yaoqiang BPMN Editor 2.2.6";
		mojo.execute();

		assertTrue(overview.exists());
		Document document = docBuilder.parse(overview);

		XPathExpression xpath = createXpathExpression("//div[@class=\"test\" and @testcase=\"A.1.0\"  and contains(@tool, \"Yaoqiang\")]");
		NodeList nodes = (NodeList) xpath.evaluate(document,
				XPathConstants.NODESET);

		Node invalidNode = nodes.item(0);

		// There should be 2 findings: an invalid element and an invalid
		// attribute
		assertEquals("2",
				invalidNode.getAttributes().getNamedItem("data-xsd-finding")
						.getNodeValue());
	}

}
