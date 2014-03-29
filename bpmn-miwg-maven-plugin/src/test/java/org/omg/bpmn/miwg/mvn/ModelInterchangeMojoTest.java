/**
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
 *
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package org.omg.bpmn.miwg.mvn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

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

	@BeforeClass
	public static void setUpClass() {
		mojo = new ModelInterchangeMojo();
	}

	@Test
	public void testScanResourcesForAllBpmnFiles() {
		List<File> bpmnFiles = new ArrayList<File>();
		File dir = new File("src" + File.separator + "test" + File.separator
				+ "resources");
		mojo.scanForBpmn(dir, bpmnFiles);
		assertEquals(14, bpmnFiles.size());
	}

	@Test
	public void testScanResourcesForSpecificVendorBpmnFiles() {
		List<File> bpmnFiles = new ArrayList<File>();
		File dir = new File("src" + File.separator + "test" + File.separator
				+ "resources");
		mojo.scanForBpmn(dir, bpmnFiles, W4_MODELER_ID);
		for (File file : bpmnFiles) {
			System.out.println("  " + file.getAbsolutePath());
		}
		assertEquals(9, bpmnFiles.size());
	}

	@Test
	public void testInferTestNames() {
		assertEquals("A.1.0.bpmn",
				mojo.inferTestName(new File("A.1.0-export.bpmn")));
		assertEquals("B.1.0.bpmn",
				mojo.inferTestName(new File("B.1.0-export.bpmn")));
		assertEquals("A.1.0.bpmn", mojo.inferTestName(new File(W4_MODELER_ID,
				"A.1.0-export.bpmn")));
		assertEquals(
				"A.1.0.bpmn",
				mojo.inferTestName(new File(ModelInterchangeMojo.SUITE_A
						+ File.separator + W4_MODELER_ID, "A.1.0-export.bpmn")));
	}

	@Test
	public void testFindReference() {
		File b = new File("A.1.0-export.bpmn");
		assertNotNull(mojo.findReference("A.1.0.bpmn", b));
		b = new File("src" + File.separator + "test" + File.separator
				+ "resources" + File.separator + "A.1.0-export.bpmn");
		assertNotNull(mojo.findReference("A.1.0.bpmn", b));
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

	@Test
	public void testXPathSuite() {
		String test = "A.1.0-export";
		mojo.outputDirectory = new File("target");
		File testFile = new File("src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "Reference"
				+ File.separator + test + ".bpmn");
		String testFileRoot = "src/test/resources";
		mojo.runXPathTests(YAOQIANG_2_MODELER_ID, test, testFileRoot, testFile);

		test = "A.1.0-roundtrip";
		testFile = new File("src" + File.separator + "test" + File.separator
				+ "resources" + File.separator + "Reference" + File.separator
				+ test + ".bpmn");
		;
		mojo.runXPathTests(YAOQIANG_2_MODELER_ID, test, testFileRoot, testFile);
	}
}
