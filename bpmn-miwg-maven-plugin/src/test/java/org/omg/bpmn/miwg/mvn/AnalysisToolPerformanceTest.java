package org.omg.bpmn.miwg.mvn;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.omg.bpmn.miwg.XmlCompareAnalysisTool;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.xpath.XPathAnalysisTool;
import org.w3c.dom.Document;

public class AnalysisToolPerformanceTest {

    private static final String TEST = "A.1.0";
    private static final String REFERENCE_RESOURCE = "/Reference/" + TEST
            + ".bpmn";
    private static final String VENDOR_RESOURCE = "/bpmn.io 0.5.0/" + TEST
            + "-roundtrip.bpmn";
    private static DocumentBuilder docBuilder;

    @BeforeClass
    public static void setUpClass() throws Exception {
        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    @Test
    public void testParseDom() {
        Document vendorDom = getVendorDom();
        Document refDom = getReferenceDom();
        vendorDom = getVendorDom();
        refDom = getReferenceDom();
        vendorDom = getVendorDom();
        refDom = getReferenceDom();
    }

    private Document getVendorDom() {
        return getDom(VENDOR_RESOURCE);
    }

    private Document getReferenceDom() {
        return getDom(REFERENCE_RESOURCE);
    }

    private Document getDom(String bpmnResource) {
        InputStream is = null;
        Document domVendor = null;
        try {
            is = getBpmnStream(bpmnResource);
            long start = System.currentTimeMillis();
            domVendor = docBuilder.parse(is);
            System.out.println("Parse DOM took (ms)"
                    + (System.currentTimeMillis() - start));
            assertNotNull("Unable to parse vendor BPMN", domVendor);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass() + ":" + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                ;
            }
        }
        return domVendor;
    }

    private InputStream getBpmnStream(String bpmnResource) {
        InputStream is;
        is = getClass().getResourceAsStream(bpmnResource);
        assertNotNull("No vendor BPMN file to test with", is);
        return is;
    }

    @Test
    public void testXmlCompareAnalysisDom() {
        Document vendorDom = getVendorDom();
        Document refDom = getReferenceDom();

        Collection<? extends Output> significantDifferences;
        try {
            long start = System.currentTimeMillis();
            significantDifferences = new XmlCompareAnalysisTool().runAnalysis(
                    refDom, vendorDom).output;
            System.out.println("XML Compare took (ms) "
                    + (System.currentTimeMillis() - start));
            System.out.println("Found " + significantDifferences.size()
                    + " diffs.");
            for (Output output : significantDifferences) {
                System.out.println("..." + output.getDescription());
                // for (DetailedOutput detail : output.getDetail()) {
                // System.out.println("    " + detail.getDescription());
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    @Test
    @Ignore
    public void testXPathAnalysisDom() {
        Collection<? extends Output> significantDifferences;
        InputStream vendorStream = null ;
        try {
            vendorStream = getBpmnStream(VENDOR_RESOURCE);
            long start = System.currentTimeMillis();
            
            // TODO This fails with an NPE at the moment due to reliance on File
            // (implicit rather than explicit API)
            significantDifferences = new XPathAnalysisTool().runAnalysis(
                    new File(TEST + ".bpmn"),
                    null, vendorStream, new File("target")).output;
            System.out.println("XPath took (ms) "
                    + (System.currentTimeMillis() - start));
            System.out.println("Found " + significantDifferences.size()
                    + " diffs.");
            for (Output output : significantDifferences) {
                System.out.println("..." + output.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass().getName() + ":" + e.getMessage());
        }finally { 
            try { 
                vendorStream.close(); 
            } catch (Exception e) { 
                ;
            }
        }

    }
}
