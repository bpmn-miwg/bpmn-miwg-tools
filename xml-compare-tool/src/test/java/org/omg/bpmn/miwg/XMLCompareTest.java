package org.omg.bpmn.miwg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.omg.bpmn.miwg.HtmlOutput.Pojos.Output;
import org.omg.bpmn.miwg.api.AnalysisJob;
import org.omg.bpmn.miwg.api.AnalysisResult;
import org.omg.bpmn.miwg.api.Consts;
import org.omg.bpmn.miwg.api.MIWGVariant;
import org.omg.bpmn.miwg.api.input.ResourceAnalysisInput;
import org.omg.bpmn.miwg.util.HTMLAnalysisOutputWriter;
import org.omg.bpmn.miwg.util.TestUtil;
import org.omg.bpmn.miwg.xmlCompare.XmlCompareAnalysisTool;
import org.w3c.dom.Document;

public class XMLCompareTest {

    private static final String TOOL_ID = "Yaoqiang BPMN Editor 2.2.18";
    private static final String TEST_ID = "A.1.0";
    private static final String VARIANT = "roundtrip";

    @Test
    public void testDomInputs() {
        InputStream bpmnStream = null;
        InputStream compareStream = null;
        try {
            bpmnStream = getClass().getResourceAsStream(
                    "/" + Consts.REFERENCE_DIR + "/" + TEST_ID + ".bpmn");
            assertNotNull(bpmnStream);
            compareStream = getClass().getResourceAsStream(
                    "/" + TOOL_ID + "/" + TEST_ID + "-" + VARIANT + ".bpmn");
            assertNotNull(compareStream);
            AnalysisJob job = new AnalysisJob(
                    "Yaoqiang BPMN Editor 3.0.1 Error", "A.1.0",
                    MIWGVariant.Roundtrip, new ResourceAnalysisInput(
                            getClass(), "/" + Consts.REFERENCE_DIR + "/"
                                    + TEST_ID + ".bpmn"),
                    new ResourceAnalysisInput(getClass(), "/" + TOOL_ID + "/"
                            + TEST_ID + "-" + VARIANT + ".bpmn"));

            File dir = new File(new File("target", "xml-compare"), TOOL_ID);
            XmlCompareAnalysisTool tool = new XmlCompareAnalysisTool();
            AnalysisResult result = tool
                    .analyzeDOM(job, readDom(bpmnStream),
                    readDom(compareStream), dir);
            Collection<? extends Output> significantDifferences = result.output;
            System.out.println("Found " + significantDifferences.size()
                    + " diffs.");
            for (Output output : significantDifferences) {
                System.out.println("..." + output.getDescription());
            }

            assertEquals(45, significantDifferences.size());

            File resultsFile = HTMLAnalysisOutputWriter.writeAnalysisResults(
                    TestUtil.REPORT_BASE_FOLDER, job, tool, result);
            assertTrue(resultsFile.exists());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        } finally {
            try {
                bpmnStream.close();
            } catch (Exception e) {
                ;
            }
            try {
                compareStream.close();
            } catch (Exception e) {
                ;
            }
        }
    }

    private Document readDom(InputStream bpmnStream) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.parse(bpmnStream);
    }

}
