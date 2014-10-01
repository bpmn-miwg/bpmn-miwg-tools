package org.omg.bpmn.miwg.mvn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.bpmn.miwg.xpathTestRunner.XPathTestTool;
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

    @BeforeClass
    public static void setUpClass() throws Exception {
        mojo = new ModelInterchangeMojo();
        mojo.outputDirectory = new File("target");
        mojo.resources = new ArrayList<Resource>();

        overview = new File(mojo.outputDirectory, "overview.html");

        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        testOverviewExpr = xPath.compile("//div[@class=\"test\"]");
    }

    @Test
    public void testScanResourcesForAllBpmnFiles() {
        List<File> bpmnFiles = new ArrayList<File>();
        File dir = new File("src" + File.separator + "test" + File.separator
                + "resources");
        mojo.scanForBpmn(dir, bpmnFiles);
        assertEquals(11, bpmnFiles.size());
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
            res.setDirectory("src" + File.separator + "test" + File.separator
                    + "resources" + File.separator);
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
            res.setDirectory("src" + File.separator + "test" + File.separator
                    + "invalid-resources" + File.separator);
            mojo.resources.add(res);
            mojo.execute();

            assertTrue(overview.exists());
            Document document = docBuilder.parse(overview);
            NodeList nodes = (NodeList) testOverviewExpr.evaluate(document,
                    XPathConstants.NODESET);

            assertEquals(1 /* count of .bpmn in Yaoqiang dir */,
                    nodes.getLength());
            Node node = nodes.item(0);
            // There should be 1 finding: that the file is schema invalid
            assertEquals("1", node.getAttributes()
                    .getNamedItem("data-findings").getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        }
    }

    @Test 
    public void testSingleSchemaInvalidBpmnFile() throws Exception {
        final String testName = "A.1.0";
        XPathTestTool xpathTool = new XPathTestTool(testName, mojo.outputDirectory.getAbsolutePath()); 
        String baseDir = "src" + File.separator + "test" + File.separator
                + "invalid-resources";
        mojo.runTestTool(
                xpathTool,
                YAOQIANG_2_MODELER_ID,
                testName,
                baseDir,
                new File(new File(baseDir, YAOQIANG_2_MODELER_ID), testName
                        + "-roundtrip.bpmn"),
                (InputStream) null);

        assertTrue(overview.exists());
        Document document = docBuilder.parse(overview);
        NodeList nodes = (NodeList) testOverviewExpr.evaluate(document,
                XPathConstants.NODESET);

        assertEquals(1 /* count of .bpmn in Yaoqiang dir */, nodes.getLength());
        Node node = nodes.item(0);
        // There should be 1 finding: that the file is schema invalid
        assertEquals("1", node.getAttributes().getNamedItem("data-findings")
                .getNodeValue());
    }
    
    @Test
    public void testInferTestNames() {
        assertEquals("A.1.0",
                mojo.inferTestName(new File("A.1.0-export.bpmn")));
        assertEquals("B.1.0",
                mojo.inferTestName(new File("B.1.0-export.bpmn")));
        assertEquals("A.1.0", mojo.inferTestName(new File(W4_MODELER_ID,
                "A.1.0-export.bpmn")));
        assertEquals(
                "A.1.0",
                mojo.inferTestName(new File(ModelInterchangeMojo.SUITE_A_PATH
                        + File.separator + W4_MODELER_ID, "A.1.0-export.bpmn")));
    }

    @Test
    public void testFindReference() {
        File b = new File("A.1.0-export.bpmn");
        assertNotNull(mojo.findReference("A.1.0", b));
        b = new File("src" + File.separator + "test" + File.separator
                + "resources" + File.separator + "A.1.0-export.bpmn");
        assertNotNull(mojo.findReference("A.1.0", b));
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
